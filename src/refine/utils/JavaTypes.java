package refine.utils;

public class JavaTypes {

	private final static String[] primitivas = { "boolean", "byte", "char",
			"int", "long", "float", "double" };

	private final static String[] wrappers = { "java.lang.Boolean",
			"java.lang.Byte", "java.lang.Character", "java.lang.Integer",
			"java.lang.Long", "java.lang.Float", "java.lang.Double",
			"java.lang.Short" };

	private final static String string = ("java.lang.String");
	private final static String object = ("java.lang.Object");

	public static boolean isPrimitive(String valor) {
		for (String primitive : primitivas) {
			if (valor.equals(primitive)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isWrapper(String valor) {
		for (String wrapper : wrappers) {
			if (valor.equals(wrapper)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isString(String valor) {
		if (valor.equals(string)) {
			return true;
		}
		return false;
	}

	public static boolean Object(String valor) {
		if (valor.equals(object)) {
			return true;
		}
		return false;
	}
}
