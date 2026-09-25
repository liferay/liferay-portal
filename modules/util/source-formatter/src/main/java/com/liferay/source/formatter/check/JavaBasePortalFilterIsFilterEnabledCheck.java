/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Objects;

/**
 * @author Marco Leo
 */
public class JavaBasePortalFilterIsFilterEnabledCheck
	extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (!isDerivedFrom(
				absolutePath, fileContent,
				"com.liferay.portal.servlet.filters.BasePortalFilter")) {

			return javaTerm.getContent();
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			if (Objects.equals(childJavaTerm.getName(), "isFilterEnabled")) {
				addMessage(
					fileName,
					"Classes extending BasePortalFilter must not override " +
						"\"isFilterEnabled()\", either remove the method to " +
							"use portal.properties configuration or extend " +
								"BaseFilter instead",
					childJavaTerm.getLineNumber());
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

}
