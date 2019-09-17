
package ascelion.cdi.metadata;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;

final class AnnotatedMethodImpl<X> extends AnnotatedCallableImpl<AnnotatedMethod<X>, X> implements AnnotatedMethod<X> {

	AnnotatedMethodImpl(AnnotatedMethod<X> delegate) {
		super(delegate);
	}

	@Override
	public final Method getJavaMember() {
		return this.delegate.getJavaMember();
	}
}
