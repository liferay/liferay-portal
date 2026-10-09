/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kyle Miho
 * @author Michael Cavalcanti
 */
public class UpgradeJavaFDSDataProviderCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		if (implementedClassNames.contains("FDSDataProvider")) {
			content = _updateFDSDataProviderKey(content);
			content = _updateServiceClass(content);
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			String newJavaMethodContent = javaMethodContent;

			if (implementedClassNames.contains("FDSDataProvider")) {
				newJavaMethodContent = _checkMethodDefinition(
					newJavaMethodContent);
			}

			newJavaMethodContent = _checkMethodCalls(
				content, fileName, newJavaMethodContent);

			content = StringUtil.replace(
				content, javaMethodContent, newJavaMethodContent);
		}

		return content;
	}

	private boolean _checkMethodCall(
		String content, String fileName, String javaMethodContent,
		String methodCall) {

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		String variableTypeName = getVariableTypeName(
			javaMethodContent, null, content, fileName, parameterList.get(0));

		if (variableTypeName == null) {
			return false;
		}

		if (variableTypeName.equals("HttpServletRequest") &&
			hasClassOrVariableName(
				"FDSDataProvider", javaMethodContent, content, fileName,
				methodCall)) {

			return true;
		}

		return false;
	}

	private String _checkMethodCalls(
			String content, String fileName, String javaMethodContent)
		throws Exception {

		Matcher methodCallGetItemsMatcher = _methodCallGetItemsPattern.matcher(
			javaMethodContent);

		while (methodCallGetItemsMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				javaMethodContent, methodCallGetItemsMatcher.start());

			if (!_checkMethodCall(
					content, fileName, javaMethodContent, methodCall)) {

				continue;
			}

			javaMethodContent = StringUtil.replace(
				javaMethodContent, methodCall, _reorderGetItems(methodCall));
		}

		Matcher methodCallGetItemsCountMatcher =
			_methodCallGetItemsCountPattern.matcher(javaMethodContent);

		while (methodCallGetItemsCountMatcher.find()) {
			String methodCall = JavaSourceUtil.getMethodCall(
				javaMethodContent, methodCallGetItemsCountMatcher.start());

			if (!_checkMethodCall(
					content, fileName, javaMethodContent, methodCall)) {

				continue;
			}

			javaMethodContent = StringUtil.replace(
				javaMethodContent, methodCall,
				_reorderGetItemsCount(methodCall));

			javaMethodContent = StringUtil.replace(
				javaMethodContent, "countItems", "getItemsCount");
		}

		return javaMethodContent;
	}

	private String _checkMethodDefinition(String javaMethodContent) {
		Matcher matcher = _methodGetItemsPattern.matcher(javaMethodContent);

		boolean matchedGetItems = matcher.find();

		if (!matchedGetItems) {
			matcher = _methodGetItemsCountPattern.matcher(javaMethodContent);

			if (!matcher.find()) {
				return javaMethodContent;
			}
		}

		String methodCall = JavaSourceUtil.getMethodCall(
			javaMethodContent, matcher.start());

		if (matchedGetItems) {
			return StringUtil.replace(
				javaMethodContent, methodCall, _reorderGetItems(methodCall));
		}

		return StringUtil.replace(
			javaMethodContent, methodCall, _reorderGetItemsCount(methodCall));
	}

	private String _reorderGetItems(String methodCall) {
		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		return StringUtil.replace(
			methodCall, JavaSourceUtil.getParameters(methodCall),
			StringBundler.concat(
				parameterList.get(1), StringPool.COMMA_AND_SPACE,
				parameterList.get(2), StringPool.COMMA_AND_SPACE,
				parameterList.get(0), StringPool.COMMA_AND_SPACE,
				parameterList.get(3)));
	}

	private String _reorderGetItemsCount(String methodCall) {
		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		return StringUtil.replace(
			methodCall, JavaSourceUtil.getParameters(methodCall),
			StringBundler.concat(
				parameterList.get(1), StringPool.COMMA_AND_SPACE,
				parameterList.get(0)));
	}

	private String _updateFDSDataProviderKey(String content) {
		if (content.contains("clay.data.provider.key")) {
			return StringUtil.replace(
				content, "clay.data.provider.key", "fds.data.provider.key");
		}
		else if (content.contains("commerce.data.provider.key")) {
			return StringUtil.replace(
				content, "commerce.data.provider.key", "fds.data.provider.key");
		}

		return content;
	}

	private String _updateServiceClass(String content) {
		Matcher matcher = _serviceClassPattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.replace(
				content, matcher.group(),
				matcher.group(1) + "FDSDataProvider.class");
		}

		return content;
	}

	private static final Pattern _methodCallGetItemsCountPattern =
		Pattern.compile(
			"\\w+\\.(getItemsCount|countItems)\\s*\\(\\s*.+,\\s*.+\\s*\\)");
	private static final Pattern _methodCallGetItemsPattern = Pattern.compile(
		"\\w+\\.getItems\\s*\\(\\s*.+,\\s*.+,\\s*.+,\\s*.+\\s*\\)");
	private static final Pattern _methodGetItemsCountPattern = Pattern.compile(
		"getItemsCount\\s*\\(\\s*HttpServletRequest\\s*.+,\\s*.+\\s*\\)");
	private static final Pattern _methodGetItemsPattern = Pattern.compile(
		"getItems\\s*\\(\\s*HttpServletRequest\\s*.+,\\s*.+,\\s*.+,\\s*.+" +
			"\\s*\\)");
	private static final Pattern _serviceClassPattern = Pattern.compile(
		"(service\\s*=\\s*)(ClayDataSetDataProvider|" +
			"CommerceDataSetDataProvider)\\.class");

}