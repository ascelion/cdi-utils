package ascelion.cdi.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.inject.spi.BeanAttributes;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class BeanAttributesModifier<X> {
	static public <X> BeanAttributesModifier<X> create(BeanAttributes<X> type) {
		return new BeanAttributesModifier<>(type);
	}

	public interface Modifier<X> {
		BeanAttributesModifier<X> and();
	}

	abstract class ModifierImpl implements Modifier<X> {
		@Override
		public BeanAttributesModifier<X> and() {
			return BeanAttributesModifier.this;
		}
	}

	public interface Qualifiers<X> extends Modifier<X> {
		Qualifiers<X> add(Annotation annotation, Annotation... annotations);

		Qualifiers<X> remove(Annotation annotation, Annotation... annotations);

		@SuppressWarnings("unchecked")
		Qualifiers<X> remove(Class<? extends Annotation> type, Class<? extends Annotation>... types);
	}

	class QualifiersImpl extends ModifierImpl implements Qualifiers<X> {
		final Set<Annotation> qualifiers = and().attributes.getQualifiers();

		@Override
		public Qualifiers<X> add(Annotation annotation, Annotation... annotations) {
			this.qualifiers.add(annotation);
			this.qualifiers.addAll(asList(annotations));

			return this;
		}

		@Override
		public Qualifiers<X> remove(Annotation annotation, Annotation... annotations) {
			this.qualifiers.remove(annotation);
			this.qualifiers.removeAll(asList(annotations));

			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Qualifiers<X> remove(Class<? extends Annotation> type, Class<? extends Annotation>... types) {
			this.qualifiers.removeIf(a -> a.annotationType() == type);

			stream(types).forEach(this::remove);

			return this;
		}
	};

	public interface Types<X> extends Modifier<X> {
		Types<X> add(Type type, Type... types);

		Types<X> addAll(Collection<Type> types);

		Types<X> remove(Type type, Type... types);

		Types<X> removeAll(Collection<Type> types);
	}

	class TypesImpl extends ModifierImpl implements Types<X> {
		final Set<Type> types = and().attributes.getTypes();

		@Override
		public Types<X> add(Type type, Type... types) {
			this.types.add(type);
			this.types.addAll(asList(types));

			return this;
		}

		@Override
		public Types<X> addAll(Collection<Type> types) {
			this.types.addAll(types);

			return this;
		}

		@Override
		public Types<X> remove(Type type, Type... types) {
			this.types.remove(type);
			this.types.removeAll(asList(types));

			return this;
		}

		@Override
		public Types<X> removeAll(Collection<Type> types) {
			this.types.removeAll(types);

			return this;
		}
	}

	private final BeanAttributesImpl<X> attributes;

	private BeanAttributesModifier(BeanAttributes<X> type) {
		this.attributes = new BeanAttributesImpl<>(type);
	}

	public Qualifiers<X> qualifiers() {
		return new QualifiersImpl();
	}

	public Types<X> types() {
		return new TypesImpl();
	}

	public BeanAttributes<X> get() {
		return this.attributes;
	}
}
