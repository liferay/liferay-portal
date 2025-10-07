/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.lang.reflect.Modifier;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaMapBuilderGenericsCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		return _formatGenerics(fileContent, fileName, javaTerm);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD, JAVA_VARIABLE};
	}

	private String _formatGenerics(
		String fileContent, String fileName, JavaTerm javaTerm) {

		String content = javaTerm.getContent();

		Matcher matcher = _mapBuilderPattern.matcher(content);

		while (matcher.find()) {
			String[] genericTypesArray = null;

			String genericTypes = matcher.group(7);

			if (genericTypes != null) {
				genericTypesArray = _getGenericTypesArray(genericTypes);
			}
			else {
				genericTypesArray = _getGenericTypesArray(
					fileContent, fileName, javaTerm, matcher);
			}

			if (genericTypesArray == null) {
				continue;
			}

			boolean requiresGenerics = false;

			String keyTypeName = genericTypesArray[0];
			String valueTypeName = genericTypesArray[1];

			if (keyTypeName.contains("<") || valueTypeName.contains("<")) {
				requiresGenerics = true;
			}
			else {
				Class<?> keyClazz = _getClass(keyTypeName, javaTerm);
				Class<?> valueClazz = _getClass(valueTypeName, javaTerm);

				if (_requiresGenerics(keyClazz) ||
					_requiresGenerics(valueClazz)) {

					requiresGenerics = true;
				}
				else if ((keyClazz == null) || (valueClazz == null)) {
					continue;
				}
			}

			if (!requiresGenerics && (genericTypes != null)) {
				return StringUtil.replaceFirst(
					content, matcher.group(6), StringPool.BLANK,
					matcher.start());
			}

			if (requiresGenerics && (genericTypes == null)) {
				String methodName = matcher.group(8);

				return StringUtil.replaceFirst(
					content, methodName + "(",
					StringBundler.concat(
						"<", keyTypeName, ", ", valueTypeName, ">", methodName,
						"("),
					matcher.end(4));
			}
		}

		return content;
	}

	private Class<?> _getClass(String typeName, JavaTerm javaTerm) {
		typeName = StringUtil.removeChars(typeName, '[', ']');

		JavaClass javaClass = javaTerm.getParentJavaClass();

		while (javaClass.getParentJavaClass() != null) {
			javaClass = javaClass.getParentJavaClass();
		}

		for (String importName : javaClass.getImportNames()) {
			if (!importName.endsWith("." + typeName)) {
				continue;
			}

			try {
				return Class.forName(importName);
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable, throwable);
				}
			}
		}

		try {
			return Class.forName(typeName);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}
		}

		try {
			return Class.forName("java.lang." + typeName);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}
		}

		try {
			return Class.forName(javaClass.getPackageName() + "." + typeName);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}
		}

		return null;
	}

	private String[] _getGenericTypesArray(String genericTypes) {
		int x = -1;

		while (true) {
			x = genericTypes.indexOf(",", x + 1);

			if (x == -1) {
				return null;
			}

			if (getLevel(genericTypes.substring(0, x), "<", ">") != 0) {
				continue;
			}

			return new String[] {
				StringUtil.trim(genericTypes.substring(0, x)),
				StringUtil.trim(genericTypes.substring(x + 1))
			};
		}
	}

	private String[] _getGenericTypesArray(
		String fileContent, String fileName, JavaTerm javaTerm,
		Matcher matcher) {

		if (matcher.group(1) == null) {
			return null;
		}

		String mapTypeName = null;

		if (Objects.equals(matcher.group(2), "return")) {
			if (_isInsideLambdaStatement(javaTerm.getContent(), matcher)) {
				return null;
			}

			JavaSignature javaSignature = javaTerm.getSignature();

			if (javaSignature == null) {
				return null;
			}

			mapTypeName = javaSignature.getReturnType();
		}
		else {
			mapTypeName = getVariableTypeName(
				javaTerm.getContent(), javaTerm, fileContent, fileName,
				matcher.group(3), true, false);
		}

		if (mapTypeName == null) {
			return null;
		}

		int x = mapTypeName.indexOf("<");

		if (x == -1) {
			return null;
		}

		return _getGenericTypesArray(
			mapTypeName.substring(x + 1, mapTypeName.length() - 1));
	}

	private boolean _isInsideLambdaStatement(String content, Matcher matcher) {
		int x = -1;

		while (true) {
			x = content.indexOf(" -> {", x + 1);

			if (x == -1) {
				return false;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			if (x > matcher.start()) {
				return false;
			}

			int y = x + 5;

			while (true) {
				y = content.indexOf("}", y + 1);

				if (y == -1) {
					return false;
				}

				if (y < matcher.start()) {
					continue;
				}

				if (!ToolsUtil.isInsideQuotes(content, y) &&
					(getLevel(content.substring(x, y + 1), "{", "}") == 0)) {

					return true;
				}
			}
		}
	}

	private boolean _requiresGenerics(Class<?> clazz) {
		if ((clazz == null) ||
			(Modifier.isFinal(clazz.getModifiers()) &&
			 ArrayUtil.isEmpty(clazz.getTypeParameters()))) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaMapBuilderGenericsCheck.class);

	private static final Pattern _mapBuilderPattern = Pattern.compile(
		StringBundler.concat(
			"((return|(\\w+) =)\\s*)?\\s(ConcurrentHash|Hash|LinkedHash|Tree)",
			"Map(Dictionary)?Builder\\.\\s*(<([<>\\[\\],\\s\\.\\w\\?]+)>)?\\s*",
			"(put(All)?)\\("));

}