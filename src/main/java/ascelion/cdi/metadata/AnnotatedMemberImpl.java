
package ascelion.cdi.metadata;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

abstract class AnnotatedMemberImpl<I extends AnnotatedMember<X>, X> extends AnnotatedImpl<I> implements AnnotatedMember<X> {

	AnnotatedMemberImpl(I delegate) {
		super(delegate);
	}

	@Override
	public final boolean isStatic() {
		return this.delegate.isStatic();
	}

	@Override
	public final AnnotatedType<X> getDeclaringType() {
		return this.delegate.getDeclaringType();
	}
}
