/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.comparator.JavaTermComparator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 * @author Hugo Huijser
 */
public class JavaTermOrderCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws DocumentException, IOException {

		String className = javaTerm.getName();

		String customSQLContent = null;

		if (absolutePath.contains("/persistence/") &&
			className.endsWith("FinderImpl")) {

			Document customSQLDocument = getCustomSQLDocument(
				fileName, absolutePath,
				getPortalCustomSQLDocument(absolutePath));

			if ((customSQLDocument != null) && customSQLDocument.hasContent()) {
				customSQLContent = customSQLDocument.asXML();
			}
		}

		return _sortJavaTerms(
			fileName, absolutePath, (JavaClass)javaTerm, customSQLContent);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _sortJavaTerms(
		JavaClass javaClass, List<JavaTerm> javaTerms,
		JavaTermComparator javaTermComparator) {

		String content = javaClass.getContent();

		List<JavaTerm> sortedJavaTerms = new ArrayList<>(javaTerms);

		Collections.sort(sortedJavaTerms, javaTermComparator);

		for (int i = javaTerms.size() - 1; i >= 0; i--) {
			JavaTerm javaTerm1 = javaTerms.get(i);
			JavaTerm javaTerm2 = sortedJavaTerms.get(i);

			if (!Objects.equals(
					javaTerm1.getContent(), javaTerm2.getContent())) {

				content = StringUtil.replaceFirst(
					content, javaTerm1.getContent(), javaTerm2.getContent(),
					SourceUtil.getLineStartPos(
						content,
						javaTerm1.getLineNumber() - javaClass.getLineNumber()));
			}
		}

		return content;
	}

	private String _sortJavaTerms(
		String fileName, String absolutePath, JavaClass javaClass,
		String customSQLContent) {

		List<JavaTerm> childJavaTerms = new ArrayList<>(
			javaClass.getChildJavaTerms());

		Iterator<JavaTerm> iterator = childJavaTerms.iterator();

		while (iterator.hasNext()) {
			JavaTerm javaTerm = iterator.next();

			if (javaTerm.isJavaStaticBlock() || javaTerm.isDefault()) {
				iterator.remove();
			}
		}

		if (childJavaTerms.size() < 2) {
			return javaClass.getContent();
		}

		JavaTermComparator javaTermComparator = new JavaTermComparator(
			customSQLContent,
			isAttributeValue(_CASE_SENSITIVE_KEY, absolutePath, true));

		JavaTerm previousJavaTerm = null;

		for (JavaTerm javaTerm : childJavaTerms) {
			if (previousJavaTerm == null) {
				previousJavaTerm = javaTerm;

				continue;
			}

			int compare = javaTermComparator.compare(
				previousJavaTerm, javaTerm);

			if (compare == 0) {
				addMessage(fileName, "Duplicate " + javaTerm.getName());

				return javaClass.getContent();
			}

			if (!isExcludedPath(
					JAVATERM_SORT_EXCLUDES, absolutePath,
					previousJavaTerm.getName()) &&
				!isExcludedPath(
					JAVATERM_SORT_EXCLUDES, absolutePath, javaTerm.getName()) &&
				(compare > 0)) {

				return _sortJavaTerms(
					javaClass, childJavaTerms, javaTermComparator);
			}

			previousJavaTerm = javaTerm;
		}

		return javaClass.getContent();
	}

	private static final String _CASE_SENSITIVE_KEY = "caseSensitive";

}