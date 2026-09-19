/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.JavaImportsFormatter;
import com.liferay.portal.tools.ToolsUtil;

import java.io.File;

import java.security.InvalidParameterException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaSourceUtil extends SourceUtil {

	public static String addImports(String content, String... newImports) {
		if (newImports.length == 0) {
			return content;
		}

		Set<String> missingImports = new TreeSet<>();

		Collections.addAll(missingImports, newImports);

		for (String importName : getImportNames(content)) {
			missingImports.remove(importName);
		}

		if (missingImports.isEmpty()) {
			return content;
		}

		String packageName = getPackageName(content);

		StringBundler sb = new StringBundler();

		sb.append(packageName);
		sb.append(StringPool.SEMICOLON);
		sb.append(StringPool.NEW_LINE);
		sb.append(StringPool.NEW_LINE);

		for (String missingImport : missingImports) {
			sb.append("import ");
			sb.append(missingImport);
			sb.append(";\n");
		}

		return StringUtil.replace(content, packageName + ";\n", sb.toString());
	}

	public static String addMethodNewParameters(
		String indent, int[] indexNewParameters, String methodStart,
		String[] newParameters, List<String> parameterList) {

		for (int i = 0; i < indexNewParameters.length; i++) {
			parameterList.add(indexNewParameters[i], newParameters[i]);
		}

		StringBundler sb = new StringBundler(6);

		sb.append(methodStart);
		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append(StringPool.TAB);
		sb.append(parameterList.get(0));

		for (int i = 1; i < parameterList.size(); i++) {
			if ((i % 4) == 0) {
				sb.append(StringPool.COMMA);
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.TAB);
				sb.append(parameterList.get(i));

				continue;
			}

			sb.append(StringPool.COMMA_AND_SPACE);
			sb.append(parameterList.get(i));
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	public static String getClassName(String fileName) {
		fileName = StringUtil.replace(
			fileName, CharPool.BACK_SLASH, CharPool.SLASH);

		int x = fileName.lastIndexOf(CharPool.SLASH);
		int y = fileName.lastIndexOf(CharPool.PERIOD);

		return fileName.substring(x + 1, y);
	}

	public static List<String> getImportNames(String content) {
		List<String> importNames = new ArrayList<>();

		String[] importLines = StringUtil.splitLines(
			JavaImportsFormatter.getImports(content));

		for (String importLine : importLines) {
			if (Validator.isNotNull(importLine)) {
				importNames.add(
					importLine.substring(7, importLine.length() - 1));
			}
		}

		return importNames;
	}

	public static File getJavaFile(
		String fullyQualifiedName, String rootDirName,
		Map<String, String> bundleSymbolicNamesMap) {

		if (fullyQualifiedName.contains(".kernel.")) {
			File file = _getJavaFile(
				fullyQualifiedName, rootDirName, "portal-kernel/src/",
				"portal-test/src/", "portal-impl/test/integration/",
				"portal-impl/test/unit/");

			if (file != null) {
				return file;
			}
		}

		if (fullyQualifiedName.startsWith("com.liferay.portal.") ||
			fullyQualifiedName.startsWith("com.liferay.portlet.")) {

			File file = _getJavaFile(
				fullyQualifiedName, rootDirName, "portal-impl/src/",
				"portal-test/src/", "portal-test-integration/src/",
				"portal-impl/test/integration/", "portal-impl/test/unit/");

			if (file != null) {
				return file;
			}
		}

		if (fullyQualifiedName.contains(".taglib.")) {
			File file = _getJavaFile(
				fullyQualifiedName, rootDirName, "util-taglib/src/");

			if (file != null) {
				return file;
			}
		}

		try {
			File file = _getModuleJavaFile(
				fullyQualifiedName, bundleSymbolicNamesMap);

			if (file != null) {
				return file;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static String getMethodCall(String s, int start) {
		int x = start;

		while (true) {
			x = s.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

			if (ToolsUtil.isInsideQuotes(s, x + 1)) {
				continue;
			}

			String methodCall = s.substring(start, x + 1);

			if (ToolsUtil.getLevel(methodCall) == 0) {
				return methodCall;
			}
		}
	}

	public static String getPackageName(String content) {
		Matcher matcher = _packagePattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(2);
		}

		return StringPool.BLANK;
	}

	public static String getPackageName(
		String className, String classPackageName, List<String> importNames) {

		if (className.matches("[a-z].*") || (className.length() == 1)) {
			return StringPool.BLANK;
		}

		for (String importName : importNames) {
			if (importName.endsWith("." + className)) {
				return StringUtil.removeLast(importName, "." + className);
			}

			int x = className.length();

			while (true) {
				x = className.lastIndexOf(".", x - 1);

				if (x == -1) {
					break;
				}

				if (importName.endsWith("." + className.substring(0, x))) {
					return StringUtil.removeLast(
						importName, "." + className.substring(0, x));
				}
			}
		}

		if (ArrayUtil.contains(_JAVA_LANG_CLASS_NAMES, className)) {
			return "java.lang";
		}

		return classPackageName;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 * #getParameterNames(String)}
	 */
	@Deprecated
	public static List<String> getParameterList(String methodCall) {
		return splitParameters(getParameters(methodCall));
	}

	public static List<String> getParameterNames(String methodCall) {
		List<String> parameterNames = new ArrayList<>();

		List<String> parameters = splitParameters(getParameters(methodCall));

		for (String parameter : parameters) {
			int index = parameter.lastIndexOf(CharPool.SPACE);

			if (index == -1) {
				return parameters;
			}

			parameterNames.add(parameter.substring(index + 1));
		}

		return parameterNames;
	}

	public static String getParameters(String methodCall) {
		String parameters = null;

		int x = -1;

		while (true) {
			x = methodCall.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

			parameters = methodCall.substring(0, x + 1);

			if ((ToolsUtil.getLevel(parameters, "(", ")") == 0) &&
				(ToolsUtil.getLevel(parameters, "{", "}") == 0)) {

				break;
			}
		}

		x = parameters.indexOf(StringPool.OPEN_PARENTHESIS);

		return parameters.substring(x + 1, parameters.length() - 1);
	}

	public static List<String> getParameterTypes(String methodCall) {
		List<String> parameterTypes = new ArrayList<>();

		List<String> parameters = splitParameters(getParameters(methodCall));

		for (String parameter : parameters) {
			int index = parameter.lastIndexOf(CharPool.SPACE);

			if (index == -1) {
				throw new InvalidParameterException(
					"Unable to get parameter type");
			}

			parameterTypes.add(parameter.substring(0, index));
		}

		return parameterTypes;
	}

	public static boolean isValidJavaParameter(String javaParameter) {
		if (javaParameter.contains(" implements ") ||
			javaParameter.contains(" throws ")) {

			return false;
		}

		if ((ToolsUtil.getLevel(javaParameter, "(", ")") == 0) &&
			(ToolsUtil.getLevel(javaParameter, "<", ">") == 0) &&
			(ToolsUtil.getLevel(javaParameter, "{", "}") == 0)) {

			return true;
		}

		return false;
	}

	public static List<String> splitParameters(String parameters) {
		List<String> parametersList = new ArrayList<>();

		parameters = StringUtil.trim(parameters);

		if (parameters.equals(StringPool.BLANK)) {
			return parametersList;
		}

		int x = -1;

		while (true) {
			x = parameters.indexOf(StringPool.COMMA, x + 1);

			if (x == -1) {
				parametersList.add(StringUtil.trim(parameters));

				return parametersList;
			}

			if (ToolsUtil.isInsideQuotes(parameters, x)) {
				continue;
			}

			String linePart = StringUtil.replace(
				parameters.substring(0, x), "->", _LAMBDA_SYMBOL_PLACEHOLDER);

			if ((ToolsUtil.getLevel(linePart, "(", ")") == 0) &&
				(ToolsUtil.getLevel(linePart, "<", ">") == 0) &&
				(ToolsUtil.getLevel(linePart, "{", "}") == 0)) {

				linePart = StringUtil.replace(
					linePart, _LAMBDA_SYMBOL_PLACEHOLDER, "->");

				parametersList.add(StringUtil.trim(linePart));

				parameters = parameters.substring(x + 1);

				x = -1;
			}
		}
	}

	private static File _getJavaFile(
		String fullyQualifiedName, String rootDirName, String... dirNames) {

		if (Validator.isNull(rootDirName)) {
			return null;
		}

		for (String dirName : dirNames) {
			StringBundler sb = new StringBundler(5);

			sb.append(rootDirName);
			sb.append("/");
			sb.append(dirName);
			sb.append(StringUtil.replace(fullyQualifiedName, '.', '/'));
			sb.append(".java");

			File file = new File(sb.toString());

			if (file.exists()) {
				return file;
			}
		}

		return null;
	}

	private static File _getModuleJavaFile(
		String fullyQualifiedName, Map<String, String> bundleSymbolicNamesMap) {

		for (Map.Entry<String, String> entry :
				bundleSymbolicNamesMap.entrySet()) {

			String bundleSymbolicName = entry.getKey();

			String modifiedBundleSymbolicName = bundleSymbolicName.replaceAll(
				"\\.(api|impl|service|test)$", StringPool.BLANK);

			if (!fullyQualifiedName.startsWith(modifiedBundleSymbolicName)) {
				continue;
			}

			StringBundler sb = new StringBundler(4);

			sb.append(entry.getValue());
			sb.append("/src/main/java/");
			sb.append(StringUtil.replace(fullyQualifiedName, '.', '/'));
			sb.append(".java");

			File file = new File(sb.toString());

			if (file.exists()) {
				return file;
			}

			sb = new StringBundler(4);

			sb.append(entry.getValue());
			sb.append("/src/testIntegration/java/");
			sb.append(StringUtil.replace(fullyQualifiedName, '.', '/'));
			sb.append(".java");

			file = new File(sb.toString());

			if (file.exists()) {
				return file;
			}
		}

		return null;
	}

	private static final String[] _JAVA_LANG_CLASS_NAMES = {
		"AbstractMethodError", "Appendable", "ArithmeticException",
		"ArrayIndexOutOfBoundsException", "ArrayStoreException",
		"AssertionError", "AutoCloseable", "Boolean", "BootstrapMethodError",
		"Byte", "Character", "CharSequence", "Class", "ClassCastException",
		"ClassCircularityError", "ClassFormatError", "ClassLoader",
		"ClassNotFoundException", "ClassValue", "Cloneable",
		"CloneNotSupportedException", "Comparable", "Compiler", "Deprecated",
		"Double", "Enum", "EnumConstantNotPresentException", "Error",
		"Exception", "ExceptionInInitializerError", "Float",
		"IllegalAccessError", "IllegalAccessException",
		"IllegalArgumentException", "IllegalMonitorStateException",
		"IllegalStateException", "IllegalThreadStateException",
		"IncompatibleClassChangeError", "IndexOutOfBoundsException",
		"InheritableThreadLocal", "InstantiationError",
		"InstantiationException", "Integer", "InternalError",
		"InterruptedException", "Iterable", "LinkageError", "Long", "Math",
		"NegativeArraySizeException", "NoClassDefFoundError",
		"NoSuchFieldError", "NoSuchFieldException", "NoSuchMethodError",
		"NoSuchMethodException", "NullPointerException", "Number",
		"NumberFormatException", "Object", "OutOfMemoryError", "Override",
		"Package", "Process", "ProcessBuilder", "Readable",
		"ReflectiveOperationException", "Runnable", "Runtime",
		"RuntimeException", "RuntimePermission", "SafeVarargs",
		"SecurityException", "SecurityManager", "Short", "StackOverflowError",
		"StackTraceElement", "StrictMath", "String", "StringBuffer",
		"StringBuilder", "StringIndexOutOfBoundsException", "SuppressWarnings",
		"System", "Thread", "Thread", "ThreadDeath", "ThreadGroup",
		"ThreadLocal", "Throwable", "TypeNotPresentException", "UnknownError",
		"UnsatisfiedLinkError", "UnsupportedClassVersionError",
		"UnsupportedOperationException", "VerifyError", "VirtualMachineError",
		"Void"
	};

	private static final String _LAMBDA_SYMBOL_PLACEHOLDER =
		"LAMBDA_SYMBOL_PLACEHOLDER";

	private static final Log _log = LogFactoryUtil.getLog(JavaSourceUtil.class);

	private static final Pattern _packagePattern = Pattern.compile(
		"(\n|^)\\s*package (.*);\n");

}