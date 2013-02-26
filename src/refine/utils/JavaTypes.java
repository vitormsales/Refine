package refine.utils;

public class JavaTypes {

	private final static String[] primitivesTypes = { "boolean", "byte",
			"char", "int", "long", "float", "double" };

	private final static String[] warppersTypes = { "java.lang.Boolean",
			"java.lang.Byte", "java.lang.Character", "java.lang.Integer",
			"java.lang.Long", "java.lang.Float", "java.lang.Double",
			"java.lang.Short" };

	private final static String stringTypes[] = { "java.lang.String",
			"java.lang.StringBuilder", "java.lang.StringBuffer" };
	private final static String object = ("java.lang.Object");

	public static boolean isPrimitive(String valor) {
		for (String primitive : primitivesTypes) {
			if (valor.equals(primitive)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isWrapper(String valor) {
		for (String wrapper : warppersTypes) {
			if (valor.equals(wrapper)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isString(String valor) {
		for (String string : stringTypes) {
			if (valor.equals(string)) {
				return true;
			}
		}
		return false;
	}

	public static boolean Object(String valor) {
		if (valor.equals(object)) {
			return true;
		}
		return false;
	}

	public static boolean mustRemoveTypes(String dependency) {

		if (JavaTypes.isPrimitive(dependency)) {
			return true;
		}

		if (JavaTypes.isString(dependency)) {
			return true;
		}

		if (JavaTypes.isWrapper(dependency)) {
			return true;
		}

		if (JavaTypes.Object(dependency)) {
			return true;
		}

		return false;
	}

	public static boolean ismethodOrAtribute(String dependency) {
		if (dependency.contains(":")) {
			return true;
		}
		return false;
	}
}
