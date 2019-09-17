package ascelion.cdi.literal;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NonbindingLiteral extends AnnotationLiteral<Nonbinding> implements Nonbinding {
	static public final Nonbinding INSTANCE = new NonbindingLiteral();
}
