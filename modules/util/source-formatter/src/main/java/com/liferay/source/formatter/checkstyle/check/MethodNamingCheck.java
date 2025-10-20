/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class MethodNamingCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {
			return;
		}

		String methodName = getName(detailAST);

		if (isAttributeValue(_CHECK_SEARCH_METHOD_NAMES) &&
			methodName.startsWith("search")) {

			_checkSearchMethodName(detailAST, methodName);
		}

		String className = JavaSourceUtil.getClassName(getAbsolutePath());

		if (isAttributeValue(_CHECK_SET_WITH_SAFE_CLOSEABLE_METHOD_NAMES) &&
			!className.equals("CentralizedThreadLocal")) {

			_checkSetWithSafeCloseableMethodName(detailAST, methodName);
		}

		if (AnnotationUtil.containsAnnotation(detailAST, "Override")) {
			return;
		}

		_checkMethodNamePrefix(detailAST, methodName);
		_checkTypeName(detailAST, methodName);
	}

	private void _checkMethodNamePrefix(
		DetailAST detailAST, String methodName) {

		String typeName = getTypeName(
			detailAST.findFirstToken(TokenTypes.TYPE), false);
		Matcher matcher = null;

		for (String[] array : _METHOD_NAME_PREFIXS) {
			if (array[0].equals("get") && !typeName.equals("boolean")) {
				continue;
			}

			Pattern pattern = Pattern.compile("^_" + array[0] + "([A-Z])(.*)$");

			matcher = pattern.matcher(methodName);

			if (!matcher.find()) {
				continue;
			}

			String newMethodName = StringPool.UNDERLINE + array[1];

			if (array[0].equals("get")) {
				newMethodName = newMethodName + matcher.group(1);
			}
			else {
				newMethodName =
					newMethodName + StringUtil.toLowerCase(matcher.group(1));
			}

			newMethodName = newMethodName + matcher.group(2);

			String noUnderscoreMethodName = StringPool.BLANK;

			if (array[1].equals(StringPool.BLANK)) {
				noUnderscoreMethodName = methodName.substring(1);
			}
			else {
				noUnderscoreMethodName = newMethodName.substring(1);
			}

			DetailAST parentDetailAST = detailAST.getParent();

			List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
				parentDetailAST, false, TokenTypes.METHOD_DEF);

			for (DetailAST methodDefinitionDetailAST :
					methodDefinitionDetailASTs) {

				String curMethodName = getName(methodDefinitionDetailAST);

				if (curMethodName.equals(noUnderscoreMethodName) ||
					(curMethodName.equals(newMethodName) &&
					 Objects.equals(
						 getSignature(detailAST),
						 getSignature(methodDefinitionDetailAST)))) {

					return;
				}
			}

			log(detailAST, _MSG_RENAME_METHOD, methodName, newMethodName);
		}
	}

	private void _checkSearchMethodName(
		DetailAST detailAST, String methodName) {

		DetailAST parentDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF);

		if (parentDetailAST == null) {
			return;
		}

		DetailAST identDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.IDENT);

		if (identDetailAST == null) {
			return;
		}

		DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

		if (typeDetailAST == null) {
			return;
		}

		String className = identDetailAST.getText();

		String objectName = null;

		if (className.endsWith("LocalServiceImpl")) {
			objectName = className.substring(0, className.length() - 16);
		}
		else if (className.endsWith("ServiceImpl")) {
			objectName = className.substring(0, className.length() - 11);
		}
		else {
			return;
		}

		String returnTypeName = getTypeName(typeDetailAST, true);

		String pluralObjectName = TextFormatter.formatPlural(objectName);

		if (returnTypeName.equals("List<" + objectName + ">")) {
			if (methodName.equals("search" + pluralObjectName) ||
				methodName.startsWith("search" + pluralObjectName + "By")) {

				String expectedMethodName = StringUtil.replaceFirst(
					methodName, "search" + pluralObjectName, "search");

				log(
					detailAST, _MSG_RENAME_METHOD, methodName,
					expectedMethodName);

				return;
			}

			if (!methodName.matches("search(By.*)?")) {
				log(detailAST, _MSG_INCORRECT_SEARCH_METHOD, methodName);
			}
		}
		else if (returnTypeName.equals(
					"BaseModelSearchResult<" + objectName + ">") &&
				 methodName.matches("search(By.*)?")) {

			String expectedMethodName = StringUtil.replaceFirst(
				methodName, "search", "search" + pluralObjectName);

			log(detailAST, _MSG_RENAME_METHOD, methodName, expectedMethodName);
		}
	}

	private void _checkSetWithSafeCloseableMethodName(
		DetailAST detailAST, String methodName) {

		String typeName = getTypeName(
			detailAST.findFirstToken(TokenTypes.TYPE), false);

		if (!typeName.equals("SafeCloseable") ||
			!methodName.startsWith("set")) {

			return;
		}

		if (!methodName.matches("set.+WithSafeCloseable\\d*")) {
			log(
				detailAST, _MSG_INCORRECT_SET_SAFE_CLOSEABLE_METHOD,
				methodName);
		}
	}

	private void _checkTypeName(DetailAST detailAST, String methodName) {
		String absolutePath = getAbsolutePath();

		if ((!methodName.matches("get[A-Z].*") ||
			 !absolutePath.contains("/internal/")) &&
			!methodName.matches("_get[A-Z].*")) {

			return;
		}

		String returnTypeName = getTypeName(detailAST, true);

		if ((returnTypeName.contains(StringPool.LESS_THAN) &&
			 returnTypeName.contains(StringPool.GREATER_THAN)) ||
			returnTypeName.contains("[]")) {

			return;
		}

		String innerClassName = returnTypeName;

		int x = returnTypeName.indexOf(".");

		if (x != -1) {
			innerClassName = returnTypeName.substring(x + 1);
		}

		if (methodName.matches(".*" + innerClassName + "[0-9]*") ||
			methodName.matches("_?get.*" + innerClassName + ".*")) {

			return;
		}

		List<String> enforceTypeNames = getAttributeValues(
			_ENFORCE_TYPE_NAMES_KEY);

		for (String enforceTypeName : enforceTypeNames) {
			if (innerClassName.matches(enforceTypeName)) {
				log(
					detailAST, _MSG_INCORRECT_ENDING_METHOD, returnTypeName,
					innerClassName);

				return;
			}
		}
	}

	private static final String _CHECK_SEARCH_METHOD_NAMES =
		"checkSearchMethodNames";

	private static final String _CHECK_SET_WITH_SAFE_CLOSEABLE_METHOD_NAMES =
		"checkSetWithSafeCloseableMethodNames";

	private static final String _ENFORCE_TYPE_NAMES_KEY = "enforceTypeNames";

	private static final String[][] _METHOD_NAME_PREFIXS = {
		{"do", StringPool.BLANK}, {"get", "is"}
	};

	private static final String _MSG_INCORRECT_ENDING_METHOD =
		"method.incorrect.ending";

	private static final String _MSG_INCORRECT_SEARCH_METHOD =
		"search.method.incorrect";

	private static final String _MSG_INCORRECT_SET_SAFE_CLOSEABLE_METHOD =
		"set.safe.closeable.method.incorrect";

	private static final String _MSG_RENAME_METHOD = "method.rename";

}