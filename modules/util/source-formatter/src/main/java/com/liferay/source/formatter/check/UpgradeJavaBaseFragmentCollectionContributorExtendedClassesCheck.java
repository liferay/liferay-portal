/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kyle Miho
 */
public class UpgradeJavaBaseFragmentCollectionContributorExtendedClassesCheck
	extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java")) {
			return content;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (!extendedClassNames.contains("BaseFragmentCollectionContributor")) {
			return content;
		}

		return content.replaceFirst(
			"@Component\\(service = FragmentCollectionContributor\\.class\\)",
			joinLines(
				"@Component(",
				String.format(
					"\tproperty = \"fragment.collection.key=%s\",",
					_getFragmentCollectionKey(javaClass)),
				"\tservice = FragmentCollectionContributor.class", ")"));
	}

	private String _getFragmentCollectionKey(JavaClass javaClass)
		throws Exception {

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			JavaSignature javaSignature = childJavaTerm.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (!Objects.equals(
					childJavaTerm.getName(), "getFragmentCollectionKey") ||
				!javaParameters.isEmpty()) {

				continue;
			}

			Matcher matcher = _pattern.matcher(javaClass.getContent());

			if (matcher.find()) {
				return matcher.group(1);
			}
		}

		throw new Exception(
			StringBundler.concat(
				"Unable to find FragmentCollectionKey in Java Class ",
				javaClass.getName(), ". Please add FragmentCollectionKey to ",
				"method getFragmentCollectionKey"));
	}

	private static final Pattern _pattern = Pattern.compile(
		"return \"(.*?)\";");

}