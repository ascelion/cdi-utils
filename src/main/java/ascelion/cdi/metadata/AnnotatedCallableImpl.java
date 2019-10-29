
package ascelion.cdi.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

import static java.util.Collections.unmodifiableList;

abstract class AnnotatedCallableImpl<A extends AnnotatedCallable<X>, X> extends AnnotatedMemberImpl<A, X> implements AnnotatedCallable<X> {

	final List<AnnotatedParameterImpl<X>> parameters = new ArrayList<>();

	AnnotatedCallableImpl(A delegate) {
		super(delegate);

		delegate.getParameters()
				.stream()
				.map(AnnotatedParameterImpl::new)
				.forEach(this.parameters::add);
	}

	@Override
	public List<AnnotatedParameter<X>> getParameters() {
		return unmodifiableList(this.parameters);
	}
}
