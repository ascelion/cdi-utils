package ascelion.cdi.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanAttributes;

class BeanAttributesImpl<T> implements BeanAttributes<T> {

	private final BeanAttributes<T> delegate;
	private final Set<Type> types;
	private final Set<Annotation> qualifiers;

	BeanAttributesImpl(BeanAttributes<T> delegate) {
		this.delegate = delegate;

		this.types = new HashSet<>(delegate.getTypes());
		this.qualifiers = new HashSet<>(delegate.getQualifiers());
	}

	@Override
	public Set<Type> getTypes() {
		return this.types;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return this.qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return this.delegate.getScope();
	}

	@Override
	public String getName() {
		return this.delegate.getName();
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return this.delegate.getStereotypes();
	}

	@Override
	public boolean isAlternative() {
		return this.delegate.isAlternative();
	}
}
