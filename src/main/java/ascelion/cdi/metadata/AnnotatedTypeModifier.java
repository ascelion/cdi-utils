package ascelion.cdi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import ascelion.cdi.literal.NonbindingLiteral;
import ascelion.cdi.literal.QualifierLiteral;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import lombok.RequiredArgsConstructor;

public final class AnnotatedTypeModifier<X> {
	static public <A extends Annotation> AnnotatedType<A> makeQualifier(AnnotatedType<A> type, String... bindings) {
		final AnnotatedTypeModifier<A> mod = AnnotatedTypeModifier.create(type);

		mod.type().add(QualifierLiteral.INSTANCE);

		final List<String> filter = asList(bindings);

		mod.type.methods.stream()
				.filter(m -> !filter.contains(m.getJavaMember().getName()))
				.forEach(m -> m.annotations.add(NonbindingLiteral.INSTANCE));

		return mod.get();
	}

	static public <X> AnnotatedTypeModifier<X> create(AnnotatedType<X> type) {
		return new AnnotatedTypeModifier<>(type);
	}

	public interface Modifier<X> {
		AnnotatedTypeModifier<X> and();
	}

	abstract class ModifierImpl implements Modifier<X> {
		@Override
		public final AnnotatedTypeModifier<X> and() {
			return AnnotatedTypeModifier.this;
		}
	}

	public interface Annotations<X, A extends Annotated> extends Modifier<X> {
		A get();

		Annotations<X, A> add(Annotation a);

		Annotations<X, A> remove(Class<? extends Annotation> a);

		Annotations<X, A> clear();
	}

	@RequiredArgsConstructor
	class AnnotationsImpl<A extends Annotated> extends ModifierImpl implements Annotations<X, A> {
		final AnnotatedImpl<A> annotated;

		@Override
		public A get() {
			return (A) this.annotated;
		}

		@Override
		public Annotations<X, A> add(Annotation a) {
			this.annotated.annotations.add(a);

			return this;
		}

		@Override
		public Annotations<X, A> remove(Class<? extends Annotation> t) {
			this.annotated.annotations.removeIf(a -> a.annotationType() == t);

			return this;
		}

		@Override
		public Annotations<X, A> clear() {
			this.annotated.annotations.clear();

			return this;
		}

		@Override
		public String toString() {
			return this.annotated.toString();
		}

	}

	private final AnnotatedTypeImpl<X> type;

	private AnnotatedTypeModifier(AnnotatedType<X> type) {
		this.type = new AnnotatedTypeImpl<>(type);
	}

	public Annotations<X, AnnotatedType<X>> type() {
		return new AnnotationsImpl<>(this.type);
	}

	public Set<AnnotatedConstructor<X>> getConstructors() {
		return unmodifiableSet(this.type.constructors);
	}

	public Set<AnnotatedMethod<X>> getMethods() {
		return unmodifiableSet(this.type.methods);
	}

	public Set<AnnotatedCallable<X>> getCallables() {
		return unmodifiableSet(this.type.callables);
	}

	public Set<AnnotatedField<X>> getFields() {
		return unmodifiableSet(this.type.fields);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Annotations<X, AnnotatedCallable<X>> executable(Executable executable) {
		final AnnotatedCallableImpl<?, X> x = this.type.executable(executable);

		return new AnnotationsImpl(x);
	}

	public Annotations<X, AnnotatedParameter<X>> executableParam(Executable executable, int index) {
		return new AnnotationsImpl<>(this.type.executable(executable).parameters.get(index));
	}

	public Annotations<X, AnnotatedConstructor<X>> constructor(Constructor<X> constructor) {
		return new AnnotationsImpl<>(this.type.constructor(constructor));
	}

	public Annotations<X, AnnotatedConstructor<X>> constructor(Class<?>... types) {
		return new AnnotationsImpl<>(this.type.constructor(types));
	}

	public Annotations<X, AnnotatedParameter<X>> constructorParam(int index, Class<?>... types) {
		return new AnnotationsImpl<>(this.type.constructor(types).parameters.get(index));
	}

	public Annotations<X, AnnotatedMethod<X>> method(Method method) {
		return new AnnotationsImpl<>(this.type.method(method));
	}

	public Annotations<X, AnnotatedMethod<X>> method(String name, Class<?>... types) {
		return new AnnotationsImpl<>(this.type.method(name, types));
	}

	public Annotations<X, AnnotatedParameter<X>> methodParam(String name, int index, Class<?>... types) {
		return new AnnotationsImpl<>(this.type.method(name, types).parameters.get(index));
	}

	public Annotations<X, AnnotatedField<X>> field(AnnotatedField<? super X> field) {
		return new AnnotationsImpl<>(this.type.field(field.getJavaMember()));
	}

	public Annotations<X, AnnotatedField<X>> field(Field field) {
		return new AnnotationsImpl<>(this.type.field(field));
	}

	public Annotations<X, AnnotatedField<X>> field(String name) {
		return new AnnotationsImpl<>(this.type.field(name));
	}

	public AnnotatedType<X> get() {
		return this.type;
	}
}
