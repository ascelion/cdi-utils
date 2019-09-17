
package ascelion.cdi.metadata;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;

final class AnnotatedConstructorImpl<X> extends AnnotatedCallableImpl<AnnotatedConstructor<X>, X> implements AnnotatedConstructor<X> {

	AnnotatedConstructorImpl(AnnotatedConstructor<X> delegate) {
		super(delegate);
	}

	@Override
	public final Constructor<X> getJavaMember() {
		return this.delegate.getJavaMember();
	}
}
