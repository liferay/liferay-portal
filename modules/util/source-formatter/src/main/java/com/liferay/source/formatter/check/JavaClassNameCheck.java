/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaClassNameCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		if (javaTerm.getParentJavaClass() != null) {
			return javaTerm.getContent();
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		if (javaClass.isAnonymous() || javaClass.hasAnnotation("Deprecated")) {
			return javaTerm.getContent();
		}

		String packageName = javaClass.getPackageName();

		if (Validator.isNull(packageName)) {
			return javaTerm.getContent();
		}

		String className = javaClass.getName();

		_checkTypo(fileName, className, packageName, 1);

		List<String> expectedPackagePathDataEntries = getAttributeValues(
			_EXPECTED_PACKAGE_PATH_DATA_KEY, absolutePath);

		for (String expectedPackagePathDataEntry :
				expectedPackagePathDataEntries) {

			String[] array = StringUtil.split(
				expectedPackagePathDataEntry, CharPool.COLON);

			if (array.length != 2) {
				continue;
			}

			String expectedClassNameEnding = array[1];

			if (!packageName.endsWith("." + array[0]) ||
				className.endsWith(expectedClassNameEnding)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Name of class in package \"", packageName,
					"\" should end with \"", expectedClassNameEnding, "\""));
		}

		List<String> extendedClassNames = javaClass.getExtendedClassNames();
		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		if (implementedClassNames.isEmpty()) {
			_checkClassNameByExtendedClasses(
				fileName, absolutePath, className, extendedClassNames);
		}
		else {
			_checkClassNameByImplementedClasses(
				fileName, absolutePath, className, extendedClassNames,
				implementedClassNames);
		}

		_checkClassNameByUnimplementedClasses(
			javaClass, fileName, absolutePath, className,
			implementedClassNames);

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkClassNameByExtendedClasses(
		String fileName, String absolutePath, String className,
		List<String> extendedClassNames) {

		if (className.startsWith("Base") || extendedClassNames.isEmpty()) {
			return;
		}

		List<String> enforceExtendedClassNames = getAttributeValues(
			_ENFORCE_EXTENDED_CLASS_NAMES_KEY, absolutePath);

		for (String enforceExtendedClassName : enforceExtendedClassNames) {
			if (!extendedClassNames.contains(enforceExtendedClassName)) {
				continue;
			}

			String trimmedEnforceExtendedClassName = enforceExtendedClassName;

			if (trimmedEnforceExtendedClassName.startsWith("Base")) {
				trimmedEnforceExtendedClassName =
					trimmedEnforceExtendedClassName.substring(4);
			}

			if (trimmedEnforceExtendedClassName.endsWith("TestCase")) {
				trimmedEnforceExtendedClassName =
					trimmedEnforceExtendedClassName.substring(
						0, trimmedEnforceExtendedClassName.length() - 4);
			}

			if (!className.endsWith(trimmedEnforceExtendedClassName)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Name of class extending \"", enforceExtendedClassName,
						"\" should end with \"",
						trimmedEnforceExtendedClassName, "\""));

				break;
			}
		}
	}

	private void _checkClassNameByImplementedClasses(
		String fileName, String absolutePath, String className,
		List<String> extendedClassNames, List<String> implementedClassNames) {

		List<String> enforceImplementedClassNames = getAttributeValues(
			_ENFORCE_IMPLEMENTED_CLASS_NAMES_KEY, absolutePath);

		outerLoop:
		for (String enforceImplementedClassName :
				enforceImplementedClassNames) {

			if (!implementedClassNames.contains(enforceImplementedClassName)) {
				continue;
			}

			for (String extendedClassName : extendedClassNames) {
				if (extendedClassName.startsWith("Base") &&
					!extendedClassName.endsWith(enforceImplementedClassName)) {

					continue outerLoop;
				}
			}

			if (!className.endsWith(enforceImplementedClassName) &&
				((implementedClassNames.size() == 1) ||
				 enforceImplementedClassName.equals(
					 "ScreenNavigationCategory"))) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Name of class implementing \"",
						enforceImplementedClassName, "\" should end with \"",
						enforceImplementedClassName, "\""));

				break;
			}
		}
	}

	private void _checkClassNameByUnimplementedClasses(
		JavaClass javaClass, String fileName, String absolutePath,
		String className, List<String> implementedClassNames) {

		if (javaClass.isInterface()) {
			return;
		}

		List<String> unimplementedClassNames = getAttributeValues(
			_UNIMPLEMENTED_CLASS_NAMES_KEY, absolutePath);

		for (String unimplementedClassName : unimplementedClassNames) {
			if (className.endsWith(unimplementedClassName) &&
				!implementedClassNames.contains(unimplementedClassName)) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Name of class not implementing \"",
						unimplementedClassName, "\" should not end with \"",
						unimplementedClassName, "\""));

				break;
			}
		}
	}

	private void _checkTypo(
		String fileName, String className, String packageName, int level) {

		StringBundler sb = new StringBundler(level + 2);

		sb.append("(.*[a-z0-9]|\\A)");

		for (int i = 0; i < level; i++) {
			sb.append("([A-Z][a-z0-9]+)");
		}

		sb.append("$");

		Pattern pattern = Pattern.compile(sb.toString());

		Matcher matcher = pattern.matcher(className);

		if (!matcher.find()) {
			return;
		}

		String classNamePart = StringUtil.toLowerCase(matcher.group(2));

		String[] packageNameParts = StringUtil.split(
			packageName, CharPool.PERIOD);

		String packageNamePart =
			packageNameParts[packageNameParts.length - level];

		if (classNamePart.equals(packageNamePart)) {
			if (packageNameParts.length == 1) {
				return;
			}

			_checkTypo(fileName, className, packageName, level + 1);
		}
		else if (SourceUtil.hasTypo(classNamePart, packageNamePart)) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Typo in either class name \"", className,
					"\" or package \"", packageName, "\""));
		}
	}

	private static final String _ENFORCE_EXTENDED_CLASS_NAMES_KEY =
		"enforceExtendedClassNames";

	private static final String _ENFORCE_IMPLEMENTED_CLASS_NAMES_KEY =
		"enforceImplementedClassNames";

	private static final String _EXPECTED_PACKAGE_PATH_DATA_KEY =
		"expectedPackagePathData";

	private static final String _UNIMPLEMENTED_CLASS_NAMES_KEY =
		"unimplementedClassNames";

}