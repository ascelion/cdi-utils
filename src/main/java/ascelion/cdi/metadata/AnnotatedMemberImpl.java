
package ascelion.cdi.metadata;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

abstract class AnnotatedMemberImpl<A extends AnnotatedMember<X>, X> extends AnnotatedImpl<A> implements AnnotatedMember<X> {

	AnnotatedMemberImpl(A delegate) {
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
