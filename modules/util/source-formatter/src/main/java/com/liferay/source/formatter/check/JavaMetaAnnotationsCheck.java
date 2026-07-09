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
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaMetaAnnotationsCheck extends JavaAnnotationsCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		return formatAnnotations(
			fileName, absolutePath, (JavaClass)javaTerm, fileContent);
	}

	@Override
	protected String formatAnnotation(
		String fileName, String absolutePath, JavaClass javaClass,
		String fileContent, String annotation, String indent) {

		if (!annotation.contains("@Meta.")) {
			return annotation;
		}

		_checkDelimiters(fileName, fileContent, annotation);
		_checkMissingNameAttribute(
			fileName, absolutePath, fileContent, annotation);

		if (isAttributeValue(_CHECK_CONFIGURATION_NAME_KEY, absolutePath)) {
			_checkConfigurationNameValue(fileName, fileContent, annotation);
		}

		annotation = _fixOCDId(
			fileName, annotation, javaClass.getPackageName());
		annotation = _fixTypeProperties(annotation);

		return annotation;
	}

	private void _checkConfigurationNameValue(
		String fileName, String content, String annotation) {

		if (!annotation.contains("@Meta.OCD")) {
			return;
		}

		Matcher matcher = _annotationNameValueKeyPattern.matcher(annotation);

		if (matcher.find()) {
			String nameValue = matcher.group(1);

			if (!nameValue.endsWith("-configuration-name")) {
				addMessage(
					fileName,
					"Value for \"name\" should end with \"-configuration-" +
						"name\"",
					getLineNumber(content, content.indexOf(matcher.group())));
			}
		}
	}

	private void _checkDelimiter(
		String fileName, String content, Matcher matcher, String key,
		String correctDelimiter, String incorrectDelimiter) {

		if (!key.equals(matcher.group(1))) {
			return;
		}

		String value = matcher.group(2);

		if (!value.contains(incorrectDelimiter)) {
			return;
		}

		StringBundler sb = new StringBundler(7);

		sb.append("Value \"");
		sb.append(value);
		sb.append("\" for key \"");
		sb.append(key);
		sb.append("\" should use \"");
		sb.append(correctDelimiter);
		sb.append("\" as delimiter");

		addMessage(
			fileName, sb.toString(),
			getLineNumber(content, content.indexOf(matcher.group())));
	}

	private void _checkDelimiters(
		String fileName, String content, String annotation) {

		Matcher matcher = _annotationMetaValueKeyPattern.matcher(annotation);

		while (matcher.find()) {
			_checkDelimiter(
				fileName, content, matcher, "description", StringPool.DASH,
				StringPool.PERIOD);
			_checkDelimiter(
				fileName, content, matcher, "id", StringPool.PERIOD,
				StringPool.DASH);
			_checkDelimiter(
				fileName, content, matcher, "name", StringPool.DASH,
				StringPool.PERIOD);
		}
	}

	private void _checkMissingNameAttribute(
		String fileName, String absolutePath, String content,
		String annotation) {

		if (!annotation.contains("@Meta.AD") ||
			!content.contains("@Meta.OCD") ||
			content.contains("generateUI = false") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		if (!annotation.contains("name = ")) {
			addMessage(
				fileName, "Missing attribute \"name\" in \"@Meta.AD\"",
				getLineNumber(content, content.indexOf(annotation)));
		}
	}

	private String _fixOCDId(
		String fileName, String annotation, String packageName) {

		return annotation.replaceFirst(
			"(@Meta\\.OCD\\([^\\{]+id = )\".+?\"",
			StringBundler.concat(
				"$1\"", packageName, StringPool.PERIOD,
				JavaSourceUtil.getClassName(fileName), StringPool.QUOTE));
	}

	private String _fixTypeProperties(String annotation) {
		if (!annotation.contains("@Meta.")) {
			return annotation;
		}

		Matcher matcher = _annotationMetaTypePattern.matcher(annotation);

		if (!matcher.find()) {
			return annotation;
		}

		return StringUtil.replaceFirst(
			annotation, StringPool.PERCENT, StringPool.BLANK, matcher.start());
	}

	private static final String _CHECK_CONFIGURATION_NAME_KEY =
		"checkConfigurationName";

	private static final Pattern _annotationMetaTypePattern = Pattern.compile(
		"[\\s\\(](name|description) = \"%");
	private static final Pattern _annotationMetaValueKeyPattern =
		Pattern.compile("\\s(\\w+) = \"([\\w\\.\\-]+?)\"");
	private static final Pattern _annotationNameValueKeyPattern =
		Pattern.compile("\\sname = \"([\\w\\.\\-]+?)\"");

}