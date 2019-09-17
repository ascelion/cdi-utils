package ascelion.cdi.literal;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QualifierLiteral extends AnnotationLiteral<Qualifier> implements Qualifier {
	static public final Qualifier INSTANCE = new QualifierLiteral();
}
