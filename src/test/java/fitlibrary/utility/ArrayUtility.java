package fitlibrary.utility;

public class ArrayUtility {
	public static String mkString(Object[] array) {
		return mkString(array,", ");
	}

	public static String mkString(Object[] array, String separator) {
		StringBuilder s = new StringBuilder();
		boolean first = true;
		for (Object a: array) {
			if (first)
				first = false;
			else
				s.append(separator);
			s.append(a.toString());
		}
		return s.toString();
	}
}
