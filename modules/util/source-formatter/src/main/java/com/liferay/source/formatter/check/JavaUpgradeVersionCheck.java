/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import aQute.bnd.version.Version;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaUpgradeVersionCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		List<String> implementedClassNames =
			javaClass.getImplementedClassNames();

		String content = javaClass.getContent();

		if (!implementedClassNames.contains("UpgradeStepRegistrator")) {
			return content;
		}

		BNDSettings bndSettings = getBNDSettings(fileName);

		boolean liferayService = false;

		if ((bndSettings != null) &&
			GetterUtil.getBoolean(
				BNDSourceUtil.getDefinitionValue(
					bndSettings.getContent(), "Liferay-Service"))) {

			liferayService = true;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			String name = childJavaTerm.getName();

			if (!name.equals("register")) {
				continue;
			}

			_checkLatestUpgradeVersion(
				fileName, absolutePath, childJavaTerm,
				javaClass.getImportNames(), javaClass.getPackageName());

			if (liferayService) {
				_checkServiceUpgradeStepVersion(fileName, childJavaTerm);
			}
			else {
				content = _fixMissingRegisterInitialization(
					absolutePath, content, childJavaTerm, fileName);
			}
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _adjustIncrementType(
			String absolutePath, String content, String className,
			String upgradePackageName, String incrementType)
		throws IOException {

		if (content == null) {
			return incrementType;
		}

		if ((className != null) &&
			content.contains("dependencies/update.sql")) {

			String sqlContent = _getSQLFileContent(absolutePath, className);

			incrementType = _adjustIncrementTypeForSQL(
				sqlContent, incrementType);

			if (incrementType.equals(_INCREMENT_TYPE_MAJOR)) {
				return incrementType;
			}
		}

		return _adjustIncrementTypeForJava(
			absolutePath, content, upgradePackageName, incrementType);
	}

	private String _adjustIncrementTypeForJava(
			String absolutePath, String content, String upgradePackageName,
			String incrementType)
		throws IOException {

		incrementType = _adjustIncrementTypeForSQL(content, incrementType);

		if (incrementType.equals(_INCREMENT_TYPE_MAJOR) ||
			content.contains("AlterColumnName") ||
			content.contains("AlterTableDropColumn") ||
			_hasColumnTypeAlteration(
				absolutePath, content, upgradePackageName)) {

			return _INCREMENT_TYPE_MAJOR;
		}

		if (incrementType.equals(_INCREMENT_TYPE_MINOR) ||
			content.contains("AlterTableAddColumn")) {

			return _INCREMENT_TYPE_MINOR;
		}

		return incrementType;
	}

	private String _adjustIncrementTypeForSQL(
		String content, String incrementType) {

		if (content == null) {
			return incrementType;
		}

		if (content.contains("drop table ")) {
			return _INCREMENT_TYPE_MAJOR;
		}

		Matcher matcher = _dropColumnPattern.matcher(content);

		if (matcher.find()) {
			return _INCREMENT_TYPE_MAJOR;
		}

		if (incrementType.equals(_INCREMENT_TYPE_MINOR) ||
			content.contains("create table ")) {

			return _INCREMENT_TYPE_MINOR;
		}

		matcher = _addColumnPattern.matcher(content);

		if (matcher.find()) {
			return _INCREMENT_TYPE_MINOR;
		}

		return incrementType;
	}

	private void _checkLatestUpgradeVersion(
			String fileName, String absolutePath, JavaTerm javaTerm,
			List<String> imports, String upgradePackageName)
		throws IOException {

		String content = javaTerm.getContent();

		int x = content.lastIndexOf("registry.register(");

		if (x == -1) {
			return;
		}

		List<String> parameterList = JavaSourceUtil.getParameterList(
			content.substring(x));

		try {
			Version toSchemaVersion = new Version(
				StringUtil.removeChar(parameterList.get(1), CharPool.QUOTE));

			if (isExcludedPath(
					_JAVA_UPGRADE_PROCESS_EXCLUDES, absolutePath,
					toSchemaVersion.toString())) {

				return;
			}

			Version fromSchemaVersion = new Version(
				StringUtil.removeChar(parameterList.get(0), CharPool.QUOTE));

			Version expectedSchemaVersion = _getExpectedSchemaVersion(
				fromSchemaVersion,
				_getExpectedIncrementType(
					absolutePath,
					parameterList.subList(2, parameterList.size()), imports,
					upgradePackageName));

			if (expectedSchemaVersion.compareTo(toSchemaVersion) > 0) {
				addMessage(
					fileName,
					"Expected new schema version: " + expectedSchemaVersion,
					javaTerm.getLineNumber(x));
			}
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}
		}
	}

	private void _checkServiceUpgradeStepVersion(
		String fileName, JavaTerm javaTerm) {

		String methodContent = javaTerm.getContent();

		int x = 0;

		while (true) {
			x = methodContent.indexOf("registry.register(", x + 1);

			if (x == -1) {
				return;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				methodContent.substring(x));

			String fromVersion = StringUtil.removeChar(
				parameterList.get(0), CharPool.QUOTE);

			if (!fromVersion.equals("0.0.0")) {
				continue;
			}

			addMessage(
				fileName,
				"Upgrades from version 0.0.0 for service builder modules are " +
					"not allowed",
				javaTerm.getLineNumber(x));
		}
	}

	private String _fixMissingRegisterInitialization(
		String absolutePath, String content, JavaTerm javaTerm,
		String fileName) {

		if (!isAttributeValue(
				_CHECK_MISSING_REGISTER_INITIALIZATION_KEY, absolutePath)) {

			return content;
		}

		String methodContent = javaTerm.getContent();

		if (methodContent.contains("registry.registerInitialization()")) {
			return content;
		}

		int x = methodContent.indexOf("registry.register(");

		if (x == -1) {
			return content;
		}

		String methodCall = JavaSourceUtil.getMethodCall(methodContent, x);

		List<String> parameterList = JavaSourceUtil.getParameterList(
			methodCall);

		String newMethodContent = null;

		if (!Objects.equals(parameterList.get(0), "\"0.0.0\"")) {
			String version = StringUtil.unquote(parameterList.get(0));

			if (!version.matches("\\d+\\.\\d+\\.\\d+")) {
				return content;
			}

			int y = methodContent.lastIndexOf(StringPool.NEW_LINE, x);

			String precedingPlaceholder = methodContent.substring(y, x);

			newMethodContent = StringUtil.insert(
				methodContent,
				precedingPlaceholder + "registry.registerInitialization();\n",
				y);
		}
		else {
			if (parameterList.size() != 3) {
				return content;
			}

			if (Objects.equals(
					parameterList.get(2), "new DummyUpgradeProcess()") ||
				Objects.equals(
					parameterList.get(2), "new DummyUpgradeStep()")) {

				newMethodContent = StringUtil.replaceFirst(
					methodContent, methodCall,
					"registry.registerInitialization()", x);
			}
		}

		if (Validator.isNotNull(newMethodContent)) {
			return StringUtil.replaceFirst(
				content, methodContent, newMethodContent);
		}

		addMessage(
			fileName,
			"The upgrade process from version 0.0.0 should be replaced by " +
				"\"registry.registerInitialization()\"");

		return content;
	}

	private String _getColumnType(
		String sql, String tableName, String columnName) {

		int x = sql.indexOf("create table " + tableName);

		if (x == -1) {
			return null;
		}

		int y = sql.indexOf(");", x);

		if (y == -1) {
			return null;
		}

		String tableSQL = sql.substring(x, y + 1);

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"\n\\s*", columnName, "\\s+([\\w\\(\\)]+)[\\s,]"));

		Matcher matcher = pattern.matcher(tableSQL);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private String _getExpectedIncrementType(
			String absolutePath, List<String> upgradeSteps,
			List<String> imports, String upgradePackageName)
		throws IOException {

		String incrementType = _INCREMENT_TYPE_MICRO;

		for (String upgradeStep : upgradeSteps) {
			if (upgradeStep.contains("{\n")) {
				incrementType = _adjustIncrementType(
					absolutePath, upgradeStep, null, upgradePackageName,
					incrementType);

				if (incrementType.equals(_INCREMENT_TYPE_MAJOR)) {
					return incrementType;
				}

				continue;
			}

			Matcher matcher = _classNamePattern.matcher(upgradeStep);

			if (!matcher.find()) {
				continue;
			}

			String className = StringUtil.removeChars(
				matcher.group(1), CharPool.NEW_LINE, CharPool.SPACE,
				CharPool.TAB);

			if (!className.contains(StringPool.PERIOD)) {
				for (String importName : imports) {
					if (importName.endsWith(StringPool.PERIOD + className)) {
						className = importName;

						break;
					}
				}
			}

			if (!className.contains(StringPool.PERIOD)) {
				className = StringBundler.concat(
					upgradePackageName, StringPool.PERIOD, className);
			}

			String javaFileContent = _getJavaFileContent(
				absolutePath, className);

			incrementType = _adjustIncrementType(
				absolutePath, javaFileContent, className, upgradePackageName,
				incrementType);

			if (incrementType.equals(_INCREMENT_TYPE_MAJOR)) {
				return incrementType;
			}
		}

		return incrementType;
	}

	private Version _getExpectedSchemaVersion(
		Version version, String incrementType) {

		int major = version.getMajor();
		int micro = version.getMicro();
		int minor = version.getMinor();

		if (incrementType.equals(_INCREMENT_TYPE_MAJOR)) {
			major++;
			micro = 0;
			minor = 0;
		}
		else if (incrementType.equals(_INCREMENT_TYPE_MINOR)) {
			micro = 0;
			minor++;
		}
		else if (incrementType.equals(_INCREMENT_TYPE_MICRO)) {
			micro++;
		}

		return new Version(major, minor, micro);
	}

	private String _getJavaFileContent(String absolutePath, String className)
		throws IOException {

		int x = absolutePath.lastIndexOf("/com/liferay/");

		String fileLocation = StringBundler.concat(
			absolutePath.substring(0, x + 1),
			StringUtil.replace(className, CharPool.PERIOD, CharPool.SLASH),
			".java");

		File file = new File(fileLocation);

		if (file.exists()) {
			return FileUtil.read(file);
		}

		return null;
	}

	private String _getSQLFileContent(String absolutePath, String className)
		throws IOException {

		String fileLocation = StringUtil.replaceLast(
			absolutePath, "/java/", "/resources/");

		int x = fileLocation.lastIndexOf("/com/liferay/");

		int y = className.lastIndexOf(CharPool.PERIOD);

		String packageName = className.substring(0, y);

		fileLocation = StringBundler.concat(
			fileLocation.substring(0, x + 1),
			StringUtil.replace(packageName, CharPool.PERIOD, CharPool.SLASH),
			"/dependencies/update.sql");

		File file = new File(fileLocation);

		if (file.exists()) {
			return FileUtil.read(file);
		}

		return null;
	}

	private String _getTableName(
			String absolutePath, String content, String upgradePackageName,
			String tableClassName)
		throws IOException {

		if (!tableClassName.endsWith(".class")) {
			return null;
		}

		tableClassName = tableClassName.substring(
			0, tableClassName.length() - 6);

		Pattern pattern = Pattern.compile(
			StringBundler.concat("import (.*\\.", tableClassName, ");"));

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		tableClassName = matcher.group(1);

		if (!tableClassName.startsWith(upgradePackageName)) {
			return null;
		}

		String tableClassContent = _getJavaFileContent(
			absolutePath, tableClassName);

		if (tableClassContent == null) {
			return null;
		}

		matcher = _tableNamePattern.matcher(tableClassContent);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private boolean _hasColumnTypeAlteration(
			String absolutePath, String content, String upgradePackageName)
		throws IOException {

		int x = -1;

		while (true) {
			x = content.indexOf("\talter(", x + 1);

			if (x == -1) {
				break;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(x));

			String tableName = _getTableName(
				absolutePath, content, upgradePackageName,
				parameterList.get(0));

			if (tableName == null) {
				continue;
			}

			for (int i = 1; i < parameterList.size(); i++) {
				String parameter = parameterList.get(i);

				Matcher matcher = _alterColumnTypePattern.matcher(parameter);

				if (matcher.find() &&
					_hasColumnTypeAlteration(
						absolutePath, tableName, matcher.group(1),
						matcher.group(2))) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _hasColumnTypeAlteration(
			String absolutePath, String tableName, String columnName,
			String newType)
		throws IOException {

		int x = absolutePath.lastIndexOf("/modules/");
		int y = absolutePath.lastIndexOf("/src/");

		String tablesSQLFileLocation =
			absolutePath.substring(x + 1, y + 1) +
				"src/main/resources/META-INF/sql/tables.sql";

		// Retrieve from git. The content in tables.sql of the current branch
		// could already contain the new column type.

		String tablesSQLContent = getPortalContent(
			tablesSQLFileLocation, absolutePath, true);

		if (tablesSQLContent == null) {
			return false;
		}

		String oldType = _getColumnType(
			tablesSQLContent, tableName, columnName);

		if ((oldType == null) || oldType.equals(newType) ||
			((oldType.startsWith("STRING") || oldType.startsWith("VARCHAR")) &&
			 newType.equals("TEXT"))) {

			return false;
		}

		if (oldType.startsWith("VARCHAR") && newType.startsWith("VARCHAR")) {
			NaturalOrderStringComparator comparator =
				new NaturalOrderStringComparator();

			if (comparator.compare(oldType, newType) < 0) {
				return false;
			}
		}

		return true;
	}

	private static final String _CHECK_MISSING_REGISTER_INITIALIZATION_KEY =
		"checkMissingRegisterInitialization";

	private static final String _INCREMENT_TYPE_MAJOR = "MAJOR";

	private static final String _INCREMENT_TYPE_MICRO = "MICRO";

	private static final String _INCREMENT_TYPE_MINOR = "MINOR";

	private static final String _JAVA_UPGRADE_PROCESS_EXCLUDES =
		"java.upgrade.process.excludes";

	private static final Log _log = LogFactoryUtil.getLog(
		JavaUpgradeVersionCheck.class);

	private static final Pattern _addColumnPattern = Pattern.compile(
		"alter table \\w+ add ");
	private static final Pattern _alterColumnTypePattern = Pattern.compile(
		"AlterColumnType\\(\\s*\"(.+?)\",\\s*\"(\\S+) .+\"\\)");
	private static final Pattern _classNamePattern = Pattern.compile(
		"^new ([\\s\\w.]+)\\(");
	private static final Pattern _dropColumnPattern = Pattern.compile(
		"alter table \\w+ drop column");
	private static final Pattern _tableNamePattern = Pattern.compile(
		"String TABLE_NAME =\\s+\"(.+)\";");

}