
package ascelion.cdi.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

import lombok.Getter;

abstract class AnnotatedImpl<I extends Annotated> implements Annotated {

	final I delegate;
	@Getter
	final Set<Annotation> annotations;

	AnnotatedImpl(I delegate) {
		this.delegate = delegate;
		this.annotations = new LinkedHashSet<>(delegate.getAnnotations());
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
		return this.annotations.stream().anyMatch(a -> a.annotationType() == annotationType);
	}

	@Override
	public final String toString() {
		return "[ATM] " + this.delegate;
	}

	final void add(Annotation a) {
		this.annotations.add(a);
	}

	final void remove(Class<? extends Annotation> t) {
		this.annotations.removeIf(a -> a.annotationType() == t);
	}
}
