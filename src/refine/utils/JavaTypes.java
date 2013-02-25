package refine.utils;

public class JavaTypes {

	private final static String[] primitivesTypes = { "boolean", "byte",
			"char", "int", "long", "float", "double" };

	private final static String[] wrappersTypes = { "java.lang.Boolean",
			"java.lang.Byte", "java.lang.Character", "java.lang.Integer",
			"java.lang.Long", "java.lang.Float", "java.lang.Double",
			"java.lang.Short" };

	private final static String stringsTypes[] = { "java.lang.String",
			"java.lang.StringBuffer", "java.lang.StringBuilder" };
	private final static String objectType = ("java.lang.Object");

	public static boolean isPrimitive(String valor) {
		for (String primitive : primitivesTypes) {
			if (valor.equals(primitive)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isWrapper(String valor) {
		for (String wrapper : wrappersTypes) {
			if (valor.equals(wrapper)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isString(String valor) {

		for (String string : stringsTypes) {
			if (valor.equals(string)) {
				return true;
			}
		}
		return false;
	}

	public static boolean Object(String valor) {
		if (valor.equals(objectType)) {
			return true;
		}
		return false;
	}

	public static boolean canInsertTypes(String dependency) {

		if (JavaTypes.isPrimitive(dependency)) {
			return false;
		}

		if (JavaTypes.isString(dependency)) {
			return false;
		}

		if (JavaTypes.isWrapper(dependency)) {
			return false;
		}

		if (JavaTypes.Object(dependency)) {
			return false;
		}

		return true;
	}
}
