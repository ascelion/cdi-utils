package ascelion.cdi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

import ascelion.cdi.literal.NonbindingLiteral;
import ascelion.cdi.literal.QualifierLiteral;

import static java.util.Arrays.asList;

import lombok.RequiredArgsConstructor;

public final class AnnotatedTypeModifier<T> {
	static public <A extends Annotation> AnnotatedType<A> makeQualifier(AnnotatedType<A> type, String... bindings) {
		final AnnotatedTypeModifier<A> mod = AnnotatedTypeModifier.create(type);

		mod.type().add(QualifierLiteral.INSTANCE);

		final List<String> filter = asList(bindings);

		mod.type.getMethods()
				.stream()
				.filter(m -> !filter.contains(m.getJavaMember().getName()))
				.map(AnnotatedMethodImpl.class::cast)
				.forEach(m -> m.add(NonbindingLiteral.INSTANCE));

		return mod.get();
	}

	static public <X> AnnotatedTypeModifier<X> create(AnnotatedType<X> type) {
		return new AnnotatedTypeModifier<>(type);
	}

	public interface Modifier<T> {
		Modifier<T> add(Annotation a);

		Modifier<T> remove(Class<? extends Annotation> a);

		AnnotatedTypeModifier<T> and();
	}

	@RequiredArgsConstructor
	class ModifierImpl implements Modifier<T> {
		final AnnotatedImpl<?> annotated;

		@Override
		public Modifier<T> add(Annotation a) {
			this.annotated.add(a);

			return this;
		}

		@Override
		public Modifier<T> remove(Class<? extends Annotation> t) {
			this.annotated.remove(t);

			return this;
		}

		@Override
		public AnnotatedTypeModifier<T> and() {
			return AnnotatedTypeModifier.this;
		}
	}

	private final AnnotatedTypeImpl<T> type;

	private AnnotatedTypeModifier(AnnotatedType<T> type) {
		this.type = new AnnotatedTypeImpl<>(type);
	}

	public Modifier<T> type() {
		return new ModifierImpl(this.type);
	}

	public Set<AnnotatedConstructor<T>> getConstructors() {
		return this.type.getConstructors();
	}

	public Set<AnnotatedMethod<? super T>> getMethods() {
		return this.type.getMethods();
	}

	public Set<AnnotatedCallable<? super T>> getCallables() {
		return this.type.getCallables();
	}

	public Set<AnnotatedField<? super T>> getFields() {
		return this.type.getFields();
	}

	public Modifier<T> executable(Executable executable) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getExecutable(executable));
	}

	public Modifier<T> executableParam(Executable executable, int index) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getExecutable(executable).getParameters().get(index));
	}

	public Modifier<T> constructor(Constructor<T> constructor) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getConstructor(constructor));
	}

	public Modifier<T> constructor(Class<?>... types) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getConstructor(types));
	}

	public Modifier<T> constructorParam(int index, Class<?>... types) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getConstructor(types).getParameters().get(index));
	}

	public Modifier<T> method(Method method) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getMethod(method));
	}

	public Modifier<T> method(String name, Class<?>... types) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getMethod(name, types));
	}

	public Modifier<T> methodParam(String name, int index, Class<?>... types) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getMethod(name, types).getParameters().get(index));
	}

	public Modifier<T> field(AnnotatedField<? super T> field) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getField(field.getJavaMember()));
	}

	public Modifier<T> field(Field field) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getField(field));
	}

	public Modifier<T> field(String name) {
		return new ModifierImpl((AnnotatedImpl<?>) this.type.getField(name));
	}

	public AnnotatedType<T> get() {
		return this.type;
	}
}
