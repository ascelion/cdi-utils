
package ascelion.cdi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

import static java.util.Collections.unmodifiableSet;

abstract class AnnotatedImpl<A extends Annotated> implements Annotated {

	final A delegate;
	final Set<Annotation> annotations;

	AnnotatedImpl(A delegate) {
		this.delegate = delegate;
		this.annotations = new LinkedHashSet<>(delegate.getAnnotations());
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return unmodifiableSet(this.annotations);
	}

	@Override
	public final Type getBaseType() {
		return this.delegate.getBaseType();
	}

	@Override
	public final Set<Type> getTypeClosure() {
		return this.delegate.getTypeClosure();
	}

	@Override
	public final <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return this.annotations.stream()
				.filter(x -> x.annotationType() == annotationType)
				.map(annotationType::cast)
				.findFirst()
				.orElse(null);
	}

	@Override
	public final boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return this.annotations.stream()
				.anyMatch(a -> a.annotationType() == annotationType);
	}

	@Override
	public final String toString() {
		return "[ATM] " + this.delegate;
	}
}
