
package ascelion.cdi.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import lombok.Getter;

final class AnnotatedTypeImpl<X> extends AnnotatedImpl<AnnotatedType<X>> implements AnnotatedType<X> {

	@Getter
	private final Set<AnnotatedConstructor<X>> constructors;
	@Getter
	private final Set<AnnotatedField<? super X>> fields;
	@Getter
	private final Set<AnnotatedMethod<? super X>> methods;

	AnnotatedTypeImpl(AnnotatedType<X> delegate) {
		super(delegate);

		this.constructors = delegate.getConstructors().stream()
				.map(AnnotatedConstructorImpl::new)
				.collect(Collectors.toSet());
		this.fields = delegate.getFields().stream()
				.map(AnnotatedFieldImpl::new)
				.collect(Collectors.toSet());
		this.methods = delegate.getMethods().stream()
				.map(AnnotatedMethodImpl::new)
				.collect(Collectors.toSet());
	}

	@Override
	public final Class<X> getJavaClass() {
		return this.delegate.getJavaClass();
	}

	final AnnotatedConstructor<X> getConstructor(Constructor<X> ct) {
		return this.constructors.stream()
				.filter(m -> ct.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", ct.getParameterTypes()));
	}

	final AnnotatedConstructor<X> getConstructor(Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.constructors.stream()
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException("<init>", types));
	}

	final AnnotatedMethod<? super X> getMethod(Method method) {
		return this.methods.stream()
				.filter(m -> method.equals(m.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(method.getName(), method.getParameterTypes()));
	}

	final AnnotatedMethod<? super X> getMethod(String name, Class<?>... types) {
		final Class<?>[] tv = types != null ? types : new Class<?>[0];

		return this.methods.stream()
				.filter(m -> name.equals(m.getJavaMember().getName()))
				.filter(m -> Arrays.equals(tv, m.getJavaMember().getParameterTypes()))
				.findFirst()
				.orElseThrow(() -> noSuchMethodException(name, types));
	}

	final AnnotatedField<? super X> getField(Field field) {
		return this.fields.stream()
				.filter(f -> field.equals(f.getJavaMember()))
				.findFirst()
				.orElseThrow(() -> noSuchFieldException(field.getName()));
	}

	final AnnotatedField<? super X> getField(String name) {
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
