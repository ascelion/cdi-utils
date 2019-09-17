
package ascelion.cdi.metadata;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

final class AnnotatedParameterImpl<X> extends AnnotatedImpl<AnnotatedParameter<X>> implements AnnotatedParameter<X> {

	AnnotatedParameterImpl(AnnotatedParameter<X> delegate) {
		super(delegate);
	}

	@Override
	public final int getPosition() {
		return this.delegate.getPosition();
	}

	@Override
	public final AnnotatedCallable<X> getDeclaringCallable() {
		return this.delegate.getDeclaringCallable();
	}
}
