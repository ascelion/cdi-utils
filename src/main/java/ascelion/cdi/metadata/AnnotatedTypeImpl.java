
package ascelion.cdi.metadata;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

@SuppressWarnings("unchecked")
final class AnnotatedTypeImpl<X> extends AnnotatedImpl<AnnotatedType<X>> implements AnnotatedType<X> {

	final Set<AnnotatedFieldImpl<X>> fields = new LinkedHashSet<>();
	final Set<AnnotatedConstructorImpl<X>> constructors = new LinkedHashSet<>();
	final Set<AnnotatedMethodImpl<X>> methods = new LinkedHashSet<>();
	final Set<AnnotatedCallableImpl<?, X>> callables = new HashSet<>();

	AnnotatedTypeImpl(AnnotatedType<X> delegate) {
		super(delegate);

		delegate.getFields().stream()
				.map(AnnotatedFieldImpl::new)
				.map(f -> (AnnotatedFieldImpl<X>) f)
				.forEach(f -> this.fields.add(f));
		delegate.getConstructors().stream()
				.map(AnnotatedConstructorImpl::new)
				.forEach(this.constructors::add);
		delegate.getMethods().stream()
				.map(AnnotatedMethodImpl::new)
				.map(m -> (AnnotatedMethodImpl<X>) m)
				.forEach(this.methods::add);

		this.callables.addAll(this.constructors);
		this.callables.addAll(this.methods);
	}

	@Override
	public Class<X> getJavaClass() {
		return this.delegate.getJavaClass();
	}

	@Override
	public Set<AnnotatedField<? super X>> getFields() {
		return unmodifiableSet(this.fields);
	}

	@Override
	public Set<AnnotatedConstructor<X>> getConstructors() {
		return unmodifiableSet(this.constructors);
	}

	@Override
	public Set<AnnotatedMethod<? super X>> getMethods() {
		return unmodifiableSet(this.methods);
	}

	AnnotatedConstructorImpl<X> constructor(Constructor<X> ct) {
		return this.constructors.stream()
				.filter(m -> ct.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", ct.getParameterTypes()));
	}

	AnnotatedConstructorImpl<X> constructor(Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.constructors.stream()
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", types));
	}

	AnnotatedMethodImpl<X> method(Method method) {
		return this.methods.stream()
				.filter(m -> method.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(method.getName(), method.getParameterTypes()));
	}

	AnnotatedMethodImpl<X> method(String name, Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.methods.stream()
				.filter(m -> name.equals(m.getJavaMember().getName()))
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(name, types));
	}

	AnnotatedCallableImpl<?, X> executable(Executable executable) {
		final AnnotatedCallableImpl<?, X> x1 = this.constructors.stream()
				.filter(c -> executable.equals(c.getJavaMember()))
				.findFirst()
				.orElse(null);

		if (x1 != null) {
			return x1;
		}

		return this.methods.stream()
				.filter(m -> executable.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(executable.getName(), executable.getParameterTypes()));

	}

	AnnotatedFieldImpl<X> field(Field field) {
		return this.fields.stream()
				.filter(f -> field.equals(f.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchFieldException(field.getName()));
	}

	AnnotatedFieldImpl<X> field(String name) {
		return this.fields.stream()
				.filter(f -> name.equals(f.getJavaMember().getName()))
				.findFirst()
				.orElseThrow(() -> noSuchFieldException(name));
	}

	private IllegalArgumentException noSuchMethodException(String name, Class<?>[] types) {
		return new IllegalArgumentException(format("No such method: %s#%s(%s)", getJavaClass().getName(), name, Stream.of(types).map(Class::getName).collect(joining(", "))));
	}

	private IllegalArgumentException noSuchFieldException(String name) {
		return new IllegalArgumentException(format("No such field: %s#%s", getJavaClass().getName(), name));
	}

}
