package ascelion.cdi.literal;

import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnyLiteral extends AnnotationLiteral<Any> implements Any {
	static public final Any INSTANCE = new AnyLiteral();
}
