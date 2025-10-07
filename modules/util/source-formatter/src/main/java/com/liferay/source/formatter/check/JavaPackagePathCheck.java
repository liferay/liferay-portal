/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaPackagePathCheck extends BaseJavaTermCheck {

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
			addMessage(fileName, "Missing package");

			return javaTerm.getContent();
		}

		_checkPackageName(
			fileName, absolutePath, packageName, javaClass.getName());

		if (isModulesFile(absolutePath)) {
			_checkModulePackageName(fileName, absolutePath, packageName);
		}

		_checkPackageNameByClassName(
			fileName, absolutePath, javaClass.getName(), packageName);

		if (absolutePath.contains("-api/")) {
			return javaTerm.getContent();
		}

		List<String> expectedInternalImplementsDataEntries = getAttributeValues(
			_EXPECTED_INTERNAL_IMPLEMENTS_DATA_KEY, absolutePath);

		for (String expectedInternalImplementsDataEntry :
				expectedInternalImplementsDataEntries) {

			String[] array = StringUtil.split(
				expectedInternalImplementsDataEntry, CharPool.COLON);

			if (array.length == 2) {
				_checkInternalPackageName(
					fileName, absolutePath, javaClass, array[0], packageName,
					array[1]);
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkInternalPackageName(
			String fileName, String absolutePath, JavaClass javaClass,
			String implementedClassName, String packageName,
			String expectedPackageName)
		throws IOException {

		if (absolutePath.contains("/test/")) {
			return;
		}

		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		if (!packageName.contains(".internal.") &&
			!packageName.endsWith(".internal") &&
			absolutePath.matches(".*/modules(/dxp)?/apps/.*")) {

			String className = javaClass.getName();

			if (implementedClassNames.contains(implementedClassName) &&
				!className.startsWith("Base")) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Class implementing \"", implementedClassName,
						"\" should be in \"internal\" package"));
			}

			return;
		}

		if (packageName.endsWith(expectedPackageName)) {
			return;
		}

		if (!implementedClassNames.contains(implementedClassName)) {
			List<String> extendedClassNames = javaClass.getExtendedClassNames();

			if (!extendedClassNames.contains("Base" + implementedClassName)) {
				return;
			}
		}
		else {
			if (implementedClassNames.size() != 1) {
				return;
			}

			for (String extendedClassName : javaClass.getExtendedClassNames()) {
				if (extendedClassName.startsWith("Base") &&
					!extendedClassName.endsWith(implementedClassName)) {

					return;
				}
			}
		}

		BNDSettings bndSettings = getBNDSettings(fileName);

		if (bndSettings == null) {
			return;
		}

		String bundleSymbolicName = BNDSourceUtil.getDefinitionValue(
			bndSettings.getContent(), "Bundle-SymbolicName");

		Matcher matcher = _apiOrServiceBundleSymbolicNamePattern.matcher(
			bundleSymbolicName);

		bundleSymbolicName = matcher.replaceAll(StringPool.BLANK);

		if (bundleSymbolicName.endsWith(expectedPackageName)) {
			return;
		}

		int x = -1;

		while (true) {
			x = expectedPackageName.indexOf(".", x + 1);

			if (x == -1) {
				break;
			}

			if (bundleSymbolicName.endsWith(
					expectedPackageName.substring(0, x))) {

				expectedPackageName = expectedPackageName.substring(x + 1);

				break;
			}
		}

		if (packageName.endsWith(expectedPackageName)) {
			return;
		}

		if (implementedClassNames.contains(implementedClassName)) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Package for class implementing \"", implementedClassName,
					"\" should end with \"", expectedPackageName, "\""));
		}
		else {
			addMessage(
				fileName,
				StringBundler.concat(
					"Package for class extending \"Base", implementedClassName,
					"\" should end with \"", expectedPackageName, "\""));
		}
	}

	private void _checkModulePackageName(
			String fileName, String absolutePath, String packageName)
		throws IOException {

		if (!packageName.startsWith("com.liferay")) {
			return;
		}

		BNDSettings bndSettings = getBNDSettings(fileName);

		if (bndSettings == null) {
			return;
		}

		String bundleSymbolicName = BNDSourceUtil.getDefinitionValue(
			bndSettings.getContent(), "Bundle-SymbolicName");

		if (!bundleSymbolicName.startsWith("com.liferay")) {
			return;
		}

		if (isAttributeValue(_CHECK_SERVICE_PATH_KEY, absolutePath) &&
			bundleSymbolicName.endsWith(".service") &&
			packageName.contains(bundleSymbolicName + ".internal")) {

			int x = absolutePath.lastIndexOf("-service/");

			if ((x != -1) &&
				FileUtil.exists(
					absolutePath.substring(0, x + 9) + "service.xml")) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Package should not contain \"", bundleSymbolicName,
						"\". It should contain \"",
						bundleSymbolicName.substring(
							0, bundleSymbolicName.length() - 8),
						"\" (without .service)"));

				return;
			}
		}

		if (bundleSymbolicName.endsWith(".test.util")) {
			bundleSymbolicName = bundleSymbolicName.substring(
				0, bundleSymbolicName.length() - 10);
		}
		else {
			bundleSymbolicName = bundleSymbolicName.replaceAll(
				"\\.(api|service|test)$", StringPool.BLANK);
		}

		if (packageName.contains(bundleSymbolicName)) {
			return;
		}

		bundleSymbolicName = bundleSymbolicName.replaceAll(
			"\\.impl$", ".internal");

		if (!packageName.contains(bundleSymbolicName)) {
			addMessage(
				fileName,
				"Package should follow Bundle-SymbolicName specified in " +
					bndSettings.getFileName());
		}
	}

	private void _checkPackageName(
		String fileName, String absolutePath, String packageName,
		String className) {

		for (String packagePart :
				StringUtil.split(packageName, CharPool.PERIOD)) {

			if (packagePart.matches("V\\d*(_\\d+)+")) {
				addMessage(
					fileName,
					"Use lower case \"v\" when it means version in the " +
						"package");

				return;
			}
		}

		int pos = fileName.lastIndexOf(CharPool.SLASH);

		String filePath = StringUtil.replace(
			fileName.substring(0, pos), CharPool.SLASH, CharPool.PERIOD);

		if (!filePath.endsWith(packageName)) {
			addMessage(
				fileName,
				"The declared package \"" + packageName +
					"\" does not match the expected package");

			return;
		}

		if (packageName.matches(".*\\.impl\\.([\\w.]+\\.)?internal") ||
			packageName.matches(".*\\.internal\\.([\\w.]+\\.)?impl")) {

			addMessage(
				fileName,
				"Do not use both \"impl\" and \"internal\" in the package");
		}

		List<String> allowedInternalPackageDirNames = getAttributeValues(
			_ALLOWED_INTERNAL_PACKAGE_DIR_NAMES_KEY, absolutePath);

		for (String allowedInternalPackageDirName :
				allowedInternalPackageDirNames) {

			if (absolutePath.contains(allowedInternalPackageDirName)) {
				return;
			}
		}

		if (absolutePath.contains("-api/src/")) {
			Matcher matcher = _internalPackagePattern.matcher(packageName);

			if (matcher.find()) {
				addMessage(
					fileName,
					"Do not use \"" + matcher.group(1) +
						"\" package in API module");
			}

			if (packageName.contains(".api.") || packageName.endsWith(".api")) {
				addMessage(
					fileName,
					"Do not use \"api\" in the package for classes in the " +
						"API module");
			}
		}

		if (className.matches(".*(?<!Display)Context") &&
			packageName.endsWith(".display.context")) {

			addMessage(
				fileName,
				"The name of Class \"" + className +
					"\" should be ending with \"DisplayContext\"");
		}
	}

	private void _checkPackageNameByClassName(
		String fileName, String absolutePath, String className,
		String packageName) {

		if (className.endsWith("Constants") &&
			absolutePath.contains("/portal-kernel/")) {

			return;
		}

		List<String> expectedPackagePathDataEntries = getAttributeValues(
			_EXPECTED_PACKAGE_PATH_DATA_KEY, absolutePath);

		for (String expectedPackagePathDataEntry :
				expectedPackagePathDataEntries) {

			String[] array = StringUtil.split(
				expectedPackagePathDataEntry, CharPool.COLON);

			if ((array.length != 2) || !className.matches(array[0])) {
				continue;
			}

			String expectedPackagePath = array[1];

			if (expectedPackagePath.startsWith(".")) {
				if (!packageName.endsWith(expectedPackagePath)) {
					addMessage(
						fileName,
						StringBundler.concat(
							"Class \"", className,
							"\" should be in package ending with \"", array[1],
							"\""));
				}

				continue;
			}

			if (!packageName.endsWith("." + expectedPackagePath) &&
				!packageName.contains("." + expectedPackagePath + ".")) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Class \"", className, "\" should be in package \".",
						array[1], "\""));
			}
		}
	}

	private static final String _ALLOWED_INTERNAL_PACKAGE_DIR_NAMES_KEY =
		"allowedInternalPackageDirNames";

	private static final String _CHECK_SERVICE_PATH_KEY = "checkServicePath";

	private static final String _EXPECTED_INTERNAL_IMPLEMENTS_DATA_KEY =
		"expectedInternalImplementsData";

	private static final String _EXPECTED_PACKAGE_PATH_DATA_KEY =
		"expectedPackagePathData";

	private static final Pattern _apiOrServiceBundleSymbolicNamePattern =
		Pattern.compile("\\.(api|service)$");
	private static final Pattern _internalPackagePattern = Pattern.compile(
		"\\.(impl|internal)(\\.|\\Z)");

}