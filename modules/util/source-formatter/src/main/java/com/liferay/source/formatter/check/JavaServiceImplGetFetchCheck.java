/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaServiceImplGetFetchCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return false;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		String content = javaTerm.getContent();

		String className = JavaSourceUtil.getClassName(fileName);

		if (!className.endsWith("ServiceImpl")) {
			return content;
		}

		JavaMethod javaMethod = (JavaMethod)javaTerm;

		String methodName = javaMethod.getName();

		if (!methodName.startsWith("get")) {
			return content;
		}

		JavaSignature javaSignature = javaMethod.getSignature();

		String returnType = javaSignature.getReturnType();

		if (returnType == null) {
			return content;
		}

		returnType = returnType.trim();

		if (returnType.endsWith("[]") || returnType.contains("<")) {
			return content;
		}

		for (String skippedReturnType : _SKIPPED_RETURN_TYPES) {
			if (returnType.equals(skippedReturnType)) {
				return content;
			}
		}

		// Flags only a method whose entire body is a plain
		// "return x.fetchY(...);". A body with any branch, throw, local
		// variable, or fallback is consciously handling the nullability, so it
		// is not a naive "get" that merely exposes a nullable fetch.

		Matcher matcher = _simpleDirectFetchPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		addMessage(
			fileName,
			StringBundler.concat(
				"Method \"", methodName, "\" returns a nullable fetch result, ",
				"which its \"get\" name promises will never be null; return a ",
				"throwing find (raising a NoSuch*Exception) or rename the ",
				"method to \"fetch\""),
			javaTerm.getLineNumber());

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private static final String[] _SKIPPED_RETURN_TYPES = {
		"boolean", "byte", "char", "double", "float", "int", "long", "Number",
		"Object", "Serializable", "short", "String", "void"
	};

	private static final Pattern _simpleDirectFetchPattern = Pattern.compile(
		"\\)\\s*(?:throws[\\w\\s,.]*)?\\{\\s*return\\s+[^;{}]*\\.fetch[A-Z]" +
			"\\w*\\([^;{}]*\\)\\s*;\\s*\\}\\s*\\z");

}