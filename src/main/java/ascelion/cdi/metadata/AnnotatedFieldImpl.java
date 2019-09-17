
package ascelion.cdi.metadata;

import java.lang.reflect.Field;

import javax.enterprise.inject.spi.AnnotatedField;

final class AnnotatedFieldImpl<X> extends AnnotatedMemberImpl<AnnotatedField<X>, X> implements AnnotatedField<X> {

	AnnotatedFieldImpl(AnnotatedField<X> delegate) {
		super(delegate);
	}

	@Override
	public final Field getJavaMember() {
		return this.delegate.getJavaMember();
	}
}
