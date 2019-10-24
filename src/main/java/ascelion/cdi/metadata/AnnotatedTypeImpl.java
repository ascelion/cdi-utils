
package ascelion.cdi.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import lombok.Getter;

final class AnnotatedTypeImpl<X> extends AnnotatedImpl<AnnotatedType<X>> implements AnnotatedType<X> {

	@Getter
	private final Set<AnnotatedField<? super X>> fields;
	@Getter
	private final Set<AnnotatedConstructor<X>> constructors;
	@Getter
	private final Set<AnnotatedMethod<? super X>> methods;
	@Getter
	private final Set<AnnotatedCallable<? super X>> callables = new HashSet<>();

	AnnotatedTypeImpl(AnnotatedType<X> delegate) {
		super(delegate);

		this.fields = delegate.getFields().stream()
				.map(AnnotatedFieldImpl::new)
				.collect(Collectors.toSet());
		this.constructors = delegate.getConstructors().stream()
				.map(AnnotatedConstructorImpl::new)
				.collect(Collectors.toSet());
		this.methods = delegate.getMethods().stream()
				.map(AnnotatedMethodImpl::new)
				.collect(Collectors.toSet());

		this.callables.addAll(this.constructors);
		this.callables.addAll(this.methods);
	}

	@Override
	public Class<X> getJavaClass() {
		return this.delegate.getJavaClass();
	}

	AnnotatedConstructor<? super X> getConstructor(Constructor<X> ct) {
		return this.constructors.stream()
				.filter(m -> ct.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", ct.getParameterTypes()));
	}

	AnnotatedConstructor<? super X> getConstructor(Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.constructors.stream()
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", types));
	}

	AnnotatedMethod<? super X> getMethod(Method method) {
		return this.methods.stream()
				.filter(m -> method.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(method.getName(), method.getParameterTypes()));
	}

	AnnotatedMethod<? super X> getMethod(String name, Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.methods.stream()
				.filter(m -> name.equals(m.getJavaMember().getName()))
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(name, types));
	}

	@SuppressWarnings("unchecked")
	<M extends AnnotatedCallable<X>> M getExecutable(Executable executable) {
		final AnnotatedConstructor<? super X> x1 = this.constructors.stream()
				.filter(c -> executable.equals(c.getJavaMember()))
				.findFirst()
				.orElse(null);

		if (x1 != null) {
			return (M) x1;
		}

		return (M) this.methods.stream()
				.filter(m -> executable.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(executable.getName(), executable.getParameterTypes()));

	}

	AnnotatedField<? super X> getField(Field field) {
		return this.fields.stream()
				.filter(f -> field.equals(f.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchFieldException(field.getName()));
	}

	AnnotatedField<? super X> getField(String name) {
		return this.fields.stream()
				.filter(f -> name.equals(f.getJavaMember().getName()))
				.findFirst()
				.orElseThrow(() -> noSuchFieldException(name));
	}

	private IllegalArgumentException noSuchMethodException(String name, Class<?>[] types) {
		return new IllegalArgumentException(format("method not found: %s#%s(%s)", getJavaClass().getName(), name, Stream.of(types).map(Class::getName).collect(joining(", "))));
	}

	private IllegalArgumentException noSuchFieldException(String name) {
		return new IllegalArgumentException(format("field not found: %s#%s", getJavaClass().getName(), name));
	}

}
