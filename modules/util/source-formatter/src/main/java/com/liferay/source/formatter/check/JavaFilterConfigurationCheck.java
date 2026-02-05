/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.util.List;
import java.util.Properties;

/**
 * @author Alan Huang
 */
public class JavaFilterConfigurationCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (!fileName.endsWith("Filter.java") || javaClass.isAbstract() ||
			javaClass.isInterface() ||
			(javaClass.getParentJavaClass() != null)) {

			return javaTerm.getContent();
		}

		List<String> extendedClassNames = javaClass.getExtendedClassNames(true);

		if (extendedClassNames.size() != 1) {
			return javaTerm.getContent();
		}

		String extendedClassName = extendedClassNames.get(0);
		String fullyQualifiedClassName =
			javaClass.getPackageName() + "." +
				JavaSourceUtil.getClassName(fileName);

		Properties properties = new Properties();

		PropertiesUtil.load(
			properties,
			getPortalContent(
				"portal-impl/src/portal.properties", absolutePath));

		if (extendedClassName.equals(
				"com.liferay.portal.kernel.servlet.BaseFilter") &&
			(properties.getProperty(fullyQualifiedClassName) != null)) {

			addMessage(
				fileName,
				"Do not add property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");
		}
		else if (extendedClassName.equals(
					"com.liferay.portal.servlet.filters.BasePortalFilter") &&
				 (properties.getProperty(fullyQualifiedClassName) == null)) {

			addMessage(
				fileName,
				"Missing property \"" + fullyQualifiedClassName +
					"\" in portal.properties, see LPD-69645");
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

}