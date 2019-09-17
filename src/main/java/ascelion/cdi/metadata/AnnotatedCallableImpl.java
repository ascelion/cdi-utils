
package ascelion.cdi.metadata;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

import lombok.Getter;

abstract class AnnotatedCallableImpl<I extends AnnotatedCallable<X>, X> extends AnnotatedMemberImpl<I, X> implements AnnotatedCallable<X> {

	@Getter
	private final List<AnnotatedParameter<X>> parameters;

	AnnotatedCallableImpl(I delegate) {
		super(delegate);

		this.parameters = delegate.getParameters().stream().map(AnnotatedParameterImpl::new).collect(Collectors.toList());
	}

}
