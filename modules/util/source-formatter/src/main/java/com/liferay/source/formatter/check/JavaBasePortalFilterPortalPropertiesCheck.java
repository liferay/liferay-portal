/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

/**
 * @author Marco Leo
 */
public class JavaBasePortalFilterPortalPropertiesCheck
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

		if (!fileName.endsWith("Filter.java")) {
			return javaTerm.getContent();
		}

		String portalPropertiesContent = getPortalContent(
			"portal-impl/src/portal.properties", absolutePath);

		if (Validator.isNull(portalPropertiesContent)) {
			return javaTerm.getContent();
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		String fullyQualifiedClassName =
			javaClass.getPackageName() + StringPool.PERIOD +
				javaClass.getName();

		boolean isBasePortalFilter = isDerivedFrom(
			absolutePath, fileContent,
			"com.liferay.portal.servlet.filters.BasePortalFilter");

		if (isBasePortalFilter) {
			if (!_isInServletFiltersSection(
					portalPropertiesContent, fullyQualifiedClassName)) {

				addMessage(
					fileName,
					"Classes extending BasePortalFilter must be listed in " +
						"portal.properties Servlet Filters section");
			}
		}
		else if (isDerivedFrom(
					absolutePath, fileContent,
					"com.liferay.portal.kernel.servlet.BaseFilter")) {

			if (_isInServletFiltersSection(
					portalPropertiesContent, fullyQualifiedClassName)) {

				addMessage(
					fileName,
					"Classes extending BaseFilter (not BasePortalFilter) " +
						"must not be listed in portal.properties Servlet " +
							"Filters section");
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _isInServletFiltersSection(
		String portalPropertiesContent, String fullyQualifiedClassName) {

		int servletFiltersIndex = portalPropertiesContent.indexOf(
			"## Servlet Filters");

		if (servletFiltersIndex == -1) {
			return false;
		}

		String servletFiltersSection = portalPropertiesContent.substring(
			servletFiltersIndex);

		return servletFiltersSection.contains(fullyQualifiedClassName);
	}

}
