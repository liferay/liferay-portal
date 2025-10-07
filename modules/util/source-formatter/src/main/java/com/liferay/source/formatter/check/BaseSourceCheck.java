/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JSPSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.processor.CSPSourceProcessor;
import com.liferay.source.formatter.processor.JSPSourceProcessor;
import com.liferay.source.formatter.processor.JavaSourceProcessor;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterCheckUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public abstract class BaseSourceCheck implements SourceCheck {

	@Override
	public Set<SourceFormatterMessage> getSourceFormatterMessages(
		String fileName) {

		if (_sourceFormatterMessagesMap.containsKey(fileName)) {
			return _sourceFormatterMessagesMap.get(fileName);
		}

		return Collections.emptySet();
	}

	@Override
	public int getWeight() {
		return _weight;
	}

	@Override
	public boolean isEnabled(String absolutePath) {
		Class<?> clazz = getClass();

		if (_filterCheckNames.contains(clazz.getSimpleName()) ||
			isAttributeValue(
				SourceFormatterCheckUtil.ENABLED_KEY, absolutePath, true)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isJavaSource(String content, int pos) {
		if (_sourceProcessor instanceof JavaSourceProcessor) {
			return true;
		}

		if (_sourceProcessor instanceof CSPSourceProcessor ||
			_sourceProcessor instanceof JSPSourceProcessor) {

			return JSPSourceUtil.isJavaSource(content, pos);
		}

		return false;
	}

	@Override
	public boolean isJavaSource(
		String content, int pos, boolean checkInsideTags) {

		if (_sourceProcessor instanceof JavaSourceProcessor) {
			return true;
		}

		if (_sourceProcessor instanceof CSPSourceProcessor ||
			_sourceProcessor instanceof JSPSourceProcessor) {

			return JSPSourceUtil.isJavaSource(content, pos, checkInsideTags);
		}

		return false;
	}

	@Override
	public boolean isLiferaySourceCheck() {
		return false;
	}

	@Override
	public boolean isModuleSourceCheck() {
		return false;
	}

	@Override
	public void setAllFileNames(List<String> allFileNames) {
	}

	@Override
	public void setAttributes(String attributes) throws JSONException {
		_attributesJSONObject = new JSONObjectImpl(attributes);
	}

	@Override
	public void setBaseDirName(String baseDirName) {
		_baseDirName = baseDirName;
	}

	@Override
	public void setExcludes(String excludes) throws JSONException {
		_excludesJSONObject = new JSONObjectImpl(excludes);
	}

	@Override
	public void setFileExtensions(List<String> fileExtensions) {
		_fileExtensions = fileExtensions;
	}

	@Override
	public void setFilterCheckNames(List<String> filterCheckNames) {
		_filterCheckNames = filterCheckNames;
	}

	@Override
	public void setMaxDirLevel(int maxDirLevel) {
		_maxDirLevel = maxDirLevel;
	}

	@Override
	public void setMaxLineLength(int maxLineLength) {
		_maxLineLength = maxLineLength;
	}

	@Override
	public void setPluginsInsideModulesDirectoryNames(
		List<String> pluginsInsideModulesDirectoryNames) {

		_pluginsInsideModulesDirectoryNames =
			pluginsInsideModulesDirectoryNames;
	}

	@Override
	public void setPortalSource(boolean portalSource) {
		_portalSource = portalSource;
	}

	@Override
	public void setProjectPathPrefix(String projectPathPrefix) {
		_projectPathPrefix = projectPathPrefix;
	}

	@Override
	public void setSourceFormatterExcludes(
		SourceFormatterExcludes sourceFormatterExcludes) {

		_sourceFormatterExcludes = sourceFormatterExcludes;
	}

	@Override
	public void setSourceProcessor(SourceProcessor sourceProcessor) {
		_sourceProcessor = sourceProcessor;
	}

	@Override
	public void setSubrepository(boolean subrepository) {
		_subrepository = subrepository;
	}

	@Override
	public void setWeight(int weight) {
		_weight = weight;
	}

	protected void addMessage(String fileName, String message) {
		addMessage(fileName, message, -1);
	}

	protected void addMessage(String fileName, String message, int lineNumber) {
		Set<SourceFormatterMessage> sourceFormatterMessages =
			_sourceFormatterMessagesMap.get(fileName);

		if (sourceFormatterMessages == null) {
			sourceFormatterMessages = new TreeSet<>();
		}

		Class<?> clazz = getClass();

		sourceFormatterMessages.add(
			new SourceFormatterMessage(
				fileName, message, CheckType.SOURCE_CHECK,
				clazz.getSimpleName(),
				SourceFormatterUtil.getDocumentationURLString(clazz),
				lineNumber));

		_sourceFormatterMessagesMap.put(fileName, sourceFormatterMessages);
	}

	protected void clearSourceFormatterMessages(String fileName) {
		_sourceFormatterMessagesMap.remove(fileName);
	}

	protected String getAttributeValue(
		String attributeKey, String absolutePath) {

		return getAttributeValue(attributeKey, StringPool.BLANK, absolutePath);
	}

	protected String getAttributeValue(
		String attributeKey, String defaultValue, String absolutePath) {

		return SourceFormatterCheckUtil.getJSONObjectValue(
			_attributesJSONObject, _attributeValueMap, attributeKey,
			defaultValue, absolutePath, _baseDirName);
	}

	protected List<String> getAttributeValues(
		String attributeKey, String absolutePath) {

		return SourceFormatterCheckUtil.getJSONObjectValues(
			_attributesJSONObject, attributeKey, _attributeValuesMap,
			absolutePath, _baseDirName);
	}

	protected String getBaseDirName() {
		return _baseDirName;
	}

	protected BNDSettings getBNDSettings(String fileName) throws IOException {
		String bndFileLocation = fileName;

		while (true) {
			int pos = bndFileLocation.lastIndexOf(StringPool.SLASH);

			if (pos == -1) {
				return null;
			}

			bndFileLocation = bndFileLocation.substring(0, pos + 1);

			BNDSettings bndSettings = _bndSettingsMap.get(bndFileLocation);

			if (bndSettings != null) {
				return bndSettings;
			}

			File file = new File(bndFileLocation + "bnd.bnd");

			if (file.exists()) {
				bndSettings = new BNDSettings(
					bndFileLocation + "bnd.bnd", FileUtil.read(file));

				_bndSettingsMap.put(bndFileLocation, bndSettings);

				return bndSettings;
			}

			bndFileLocation = StringUtil.replaceLast(
				bndFileLocation, CharPool.SLASH, StringPool.BLANK);
		}
	}

	protected String getBuildGradleContent(String absolutePath)
		throws IOException {

		int x = absolutePath.length();

		while (true) {
			x = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

			if (x == -1) {
				return null;
			}

			String buildGradleFileName =
				absolutePath.substring(0, x + 1) + "build.gradle";

			File file = new File(buildGradleFileName);

			if (file.exists()) {
				return FileUtil.read(file);
			}
		}
	}

	protected synchronized Map<String, String> getBundleSymbolicNamesMap(
		String absolutePath) {

		if (_bundleSymbolicNamesMap != null) {
			return _bundleSymbolicNamesMap;
		}

		_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
			SourceUtil.getRootDirName(absolutePath));

		return _bundleSymbolicNamesMap;
	}

	protected String getContent(String fileName, int level) throws IOException {
		File file = getFile(fileName, level);

		if (file != null) {
			String content = FileUtil.read(file);

			if (Validator.isNotNull(content)) {
				return content;
			}
		}

		return StringPool.BLANK;
	}

	protected Document getCustomSQLDocument(
		String fileName, String absolutePath,
		Document portalCustomSQLDocument) {

		if (isPortalSource() && !isModulesFile(absolutePath)) {
			return portalCustomSQLDocument;
		}

		int i = fileName.lastIndexOf("/src/");

		if (i == -1) {
			return null;
		}

		File customSQLFile = new File(
			fileName.substring(0, i) + "/src/custom-sql/default.xml");

		if (!customSQLFile.exists()) {
			customSQLFile = new File(
				fileName.substring(0, i) +
					"/src/main/resources/META-INF/custom-sql/default.xml");
		}

		if (!customSQLFile.exists()) {
			customSQLFile = new File(
				fileName.substring(0, i) +
					"/src/main/resources/custom-sql/default.xml");
		}

		if (!customSQLFile.exists()) {
			return null;
		}

		return SourceUtil.readXML(customSQLFile);
	}

	protected File getFile(String fileName, int level) {
		return SourceFormatterUtil.getFile(_baseDirName, fileName, level);
	}

	protected List<String> getFileExtensions() {
		return _fileExtensions;
	}

	protected List<String> getFileNames(
			String baseDirName, String[] excludes, String[] includes)
		throws IOException {

		return SourceFormatterUtil.scanForFileNames(
			baseDirName, excludes, includes, _sourceFormatterExcludes, true);
	}

	protected String getGitContent(String fileName, String branchName) {
		return SourceFormatterUtil.getGitContent(fileName, branchName);
	}

	protected int getLeadingTabCount(String line) {
		int leadingTabCount = 0;

		while (line.startsWith(StringPool.TAB)) {
			line = line.substring(1);

			leadingTabCount++;
		}

		return leadingTabCount;
	}

	protected int getLevel(String s) {
		return ToolsUtil.getLevel(s);
	}

	protected int getLevel(
		String s, String increaseLevelString, String decreaseLevelString) {

		return ToolsUtil.getLevel(s, increaseLevelString, decreaseLevelString);
	}

	protected int getLevel(
		String s, String[] increaseLevelStrings,
		String[] decreaseLevelStrings) {

		return ToolsUtil.getLevel(
			s, increaseLevelStrings, decreaseLevelStrings);
	}

	protected int getLevel(
		String s, String[] increaseLevelStrings, String[] decreaseLevelStrings,
		int startLevel) {

		return ToolsUtil.getLevel(
			s, increaseLevelStrings, decreaseLevelStrings, startLevel);
	}

	protected String getLine(String content, int lineNumber) {
		return SourceUtil.getLine(content, lineNumber);
	}

	protected int getLineNumber(String content, int pos) {
		return SourceUtil.getLineNumber(content, pos);
	}

	protected int getLineStartPos(String content, int lineNumber) {
		return SourceUtil.getLineStartPos(content, lineNumber);
	}

	protected int getMaxDirLevel() {
		return _maxDirLevel;
	}

	protected int getMaxLineLength() {
		return _maxLineLength;
	}

	protected Object[] getModelInformation(String packagePath) {
		return _modelInformationsMap.get(packagePath);
	}

	protected String getModulesPropertiesContent(String absolutePath)
		throws IOException {

		if (!isPortalSource()) {
			return getPortalContent(
				_MODULES_PROPERTIES_FILE_NAME, absolutePath);
		}

		return getContent(_MODULES_PROPERTIES_FILE_NAME, _maxDirLevel);
	}

	protected List<String> getPluginsInsideModulesDirectoryNames() {
		return _pluginsInsideModulesDirectoryNames;
	}

	protected String getPortalContent(String fileName, String absolutePath)
		throws IOException {

		return getPortalContent(fileName, absolutePath, false);
	}

	protected String getPortalContent(
			String fileName, String absolutePath, boolean forceRetrieveFromGit)
		throws IOException {

		String portalBranchName = getAttributeValue(
			SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH, absolutePath);

		if (forceRetrieveFromGit) {
			return getGitContent(fileName, portalBranchName);
		}

		String content = getContent(fileName, _maxDirLevel);

		if (Validator.isNotNull(content)) {
			return content;
		}

		return getGitContent(fileName, portalBranchName);
	}

	protected synchronized Document getPortalCustomSQLDocument(
			String absolutePath)
		throws IOException {

		if (_portalCustomSQLDocument != null) {
			return _portalCustomSQLDocument;
		}

		_portalCustomSQLDocument = DocumentHelper.createDocument();

		if (!isPortalSource()) {
			return _portalCustomSQLDocument;
		}

		String portalCustomSQLDefaultContent = getPortalContent(
			"portal-impl/src/custom-sql/default.xml", absolutePath);

		if (portalCustomSQLDefaultContent == null) {
			return _portalCustomSQLDocument;
		}

		Element rootElement = _portalCustomSQLDocument.addElement("custom-sql");

		Document customSQLDefaultDocument = SourceUtil.readXML(
			portalCustomSQLDefaultContent);

		if (customSQLDefaultDocument == null) {
			return null;
		}

		Element customSQLDefaultRootElement =
			customSQLDefaultDocument.getRootElement();

		for (Element sqlElement :
				(List<Element>)customSQLDefaultRootElement.elements("sql")) {

			String customSQLFileContent = getPortalContent(
				"portal-impl/src/" + sqlElement.attributeValue("file"),
				absolutePath);

			if (customSQLFileContent == null) {
				continue;
			}

			Document customSQLDocument = SourceUtil.readXML(
				customSQLFileContent);

			if (customSQLDocument == null) {
				continue;
			}

			Element customSQLRootElement = customSQLDocument.getRootElement();

			for (Element customSQLElement :
					(List<Element>)customSQLRootElement.elements("sql")) {

				rootElement.add(customSQLElement.detach());
			}
		}

		return _portalCustomSQLDocument;
	}

	protected File getPortalDir() {
		File portalImplDir = SourceFormatterUtil.getFile(
			getBaseDirName(), "portal-impl", _maxDirLevel);

		if (portalImplDir == null) {
			return null;
		}

		return portalImplDir.getParentFile();
	}

	protected InputStream getPortalInputStream(
			String fileName, String absolutePath)
		throws IOException {

		File file = getFile(fileName, _maxDirLevel);

		if (file != null) {
			return new FileInputStream(file);
		}

		return null;
	}

	protected List<String> getPrimaryKeys(String tableContent) {
		List<String> primaryKeys = new ArrayList<>();

		for (String line : StringUtil.splitLines(tableContent)) {
			String trimmedLine = StringUtil.trimLeading(line);

			if (!trimmedLine.contains("primary key")) {
				continue;
			}

			if (trimmedLine.startsWith("primary key")) {
				String keys = trimmedLine.replaceFirst(
					"primary key \\((.+)\\)", "$1");

				for (String key : StringUtil.split(keys)) {
					primaryKeys.add(key.trim());
				}
			}
			else if (trimmedLine.matches("(\\w+) .+ primary key,?")) {
				int x = trimmedLine.indexOf(" ");

				primaryKeys.add(trimmedLine.substring(0, x));
			}
		}

		return primaryKeys;
	}

	protected String getProjectName() {
		if (_projectName != null) {
			return _projectName;
		}

		if (Validator.isNull(_projectPathPrefix) ||
			!_projectPathPrefix.contains(StringPool.COLON)) {

			_projectName = StringPool.BLANK;

			return _projectName;
		}

		int pos = _projectPathPrefix.lastIndexOf(StringPool.COLON);

		_projectName = _projectPathPrefix.substring(pos + 1);

		return _projectName;
	}

	protected String getProjectPathPrefix() {
		return _projectPathPrefix;
	}

	protected SourceFormatterExcludes getSourceFormatterExcludes() {
		return _sourceFormatterExcludes;
	}

	protected SourceProcessor getSourceProcessor() {
		return _sourceProcessor;
	}

	protected String getVariableName(String methodCall) {
		if (methodCall != null) {
			return methodCall.substring(0, methodCall.indexOf(CharPool.PERIOD));
		}

		return StringPool.BLANK;
	}

	protected String getVariableTypeName(
		String content, JavaTerm javaTerm, String fileContent, String fileName,
		String variableName) {

		return getVariableTypeName(
			content, javaTerm, fileContent, fileName, variableName, false,
			false);
	}

	protected String getVariableTypeName(
		String content, JavaTerm javaTerm, String fileContent, String fileName,
		String variableName, boolean includeArrayOrCollectionTypes,
		boolean includeFullyQualifiedName) {

		if (variableName == null) {
			return null;
		}

		String variableTypeName = _getVariableTypeName(
			content, variableName, includeArrayOrCollectionTypes,
			includeFullyQualifiedName);

		if ((variableTypeName != null) || content.equals(fileContent)) {
			return variableTypeName;
		}

		JavaClass javaClass = null;

		try {
			javaClass = _getJavaClass(javaTerm, fileName, fileContent);

			if (javaClass == null) {
				return variableTypeName;
			}

			for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
				if (childJavaTerm.isJavaVariable()) {
					JavaVariable javaVariable = (JavaVariable)childJavaTerm;

					String variableContent = javaVariable.getContent();

					variableTypeName = _getVariableTypeName(
						variableContent, variableName,
						includeArrayOrCollectionTypes,
						includeFullyQualifiedName);

					if (variableTypeName != null) {
						return variableTypeName;
					}
				}
			}
		}
		catch (Exception exception) {
			return variableTypeName;
		}

		return variableTypeName;
	}

	protected boolean hasClassOrVariableName(
		String className, String content, String fileContent, String fileName,
		String methodCall) {

		String variableName = getVariableName(methodCall);

		if (variableName.isEmpty()) {
			return false;
		}

		String variableTypeName = getVariableTypeName(
			content, null, fileContent, fileName, variableName.trim(), true,
			false);

		if (variableTypeName == null) {
			return false;
		}

		variableTypeName = StringUtil.trim(
			variableTypeName.replaceAll("<[^>]+>", ""));

		String defaultVariableName = StringUtil.lowerCaseFirstLetter(
			variableTypeName);

		if (StringUtil.equalsIgnoreCase(className, variableTypeName) ||
			className.startsWith(defaultVariableName) ||
			className.startsWith("_" + defaultVariableName)) {

			return true;
		}

		return false;
	}

	protected boolean isAttributeValue(
		String attributeKey, String absolutePath) {

		return GetterUtil.getBoolean(
			getAttributeValue(attributeKey, absolutePath));
	}

	protected boolean isAttributeValue(
		String attributeKey, String absolutePath, boolean defaultValue) {

		String attributeValue = getAttributeValue(attributeKey, absolutePath);

		if (Validator.isNull(attributeValue)) {
			return defaultValue;
		}

		return GetterUtil.getBoolean(attributeValue);
	}

	protected boolean isExcludedPath(String key, String path) {
		return isExcludedPath(key, path, -1);
	}

	protected boolean isExcludedPath(String key, String path, int lineNumber) {
		return SourceFormatterCheckUtil.isExcludedPath(
			_excludesJSONObject, _excludesValuesMap, key, path, lineNumber,
			null, _baseDirName);
	}

	protected boolean isExcludedPath(
		String key, String path, String parameter) {

		return SourceFormatterCheckUtil.isExcludedPath(
			_excludesJSONObject, _excludesValuesMap, key, path, -1, parameter,
			_baseDirName);
	}

	protected boolean isModulesApp(String absolutePath, boolean privateOnly) {
		if (absolutePath.contains("/modules/dxp/apps") ||
			absolutePath.contains("/modules/private/apps/") ||
			(!privateOnly && absolutePath.contains("/modules/apps/"))) {

			return true;
		}

		if (_projectPathPrefix == null) {
			return false;
		}

		if (_projectPathPrefix.startsWith(":private:apps") ||
			_projectPathPrefix.startsWith(":dxp:apps") ||
			(!privateOnly && _projectPathPrefix.startsWith(":apps:"))) {

			return true;
		}

		return false;
	}

	protected boolean isModulesFile(String absolutePath) {
		return isModulesFile(absolutePath, null);
	}

	protected boolean isModulesFile(
		String absolutePath, List<String> pluginsInsideModulesDirectoryNames) {

		if (_subrepository) {
			return true;
		}

		if (pluginsInsideModulesDirectoryNames == null) {
			return absolutePath.contains("/modules/");
		}

		try {
			for (String directoryName : pluginsInsideModulesDirectoryNames) {
				if (absolutePath.contains(directoryName)) {
					return false;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return absolutePath.contains("/modules/");
	}

	protected boolean isPortalSource() {
		return _portalSource;
	}

	protected boolean isSubrepository() {
		return _subrepository;
	}

	protected boolean isUpgradeProcess(String absolutePath, String content) {
		Pattern pattern = Pattern.compile(
			" class " + JavaSourceUtil.getClassName(absolutePath) +
				"\\s+extends\\s+([\\w.]+) ");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		String extendedClassName = matcher.group(1);

		if (extendedClassName.equals("UpgradeProcess")) {
			return true;
		}

		pattern = Pattern.compile("\nimport (.*\\." + extendedClassName + ");");

		matcher = pattern.matcher(content);

		if (matcher.find()) {
			extendedClassName = matcher.group(1);
		}

		if (!extendedClassName.contains(StringPool.PERIOD)) {
			extendedClassName =
				JavaSourceUtil.getPackageName(content) + StringPool.PERIOD +
					extendedClassName;
		}

		if (!extendedClassName.startsWith("com.liferay.")) {
			return false;
		}

		File file = JavaSourceUtil.getJavaFile(
			extendedClassName, SourceUtil.getRootDirName(absolutePath),
			getBundleSymbolicNamesMap(absolutePath));

		if (file == null) {
			return false;
		}

		return isUpgradeProcess(file.getAbsolutePath(), FileUtil.read(file));
	}

	protected synchronized void populateModelInformations() throws IOException {
		if (_modelInformationsMap != null) {
			return;
		}

		_modelInformationsMap = new HashMap<>();

		File portalDir = getPortalDir();

		if (portalDir == null) {
			return;
		}

		List<String> serviceXMLFileNames = SourceFormatterUtil.scanForFileNames(
			portalDir.getCanonicalPath(), new String[] {"**/service.xml"});

		for (String serviceXMLFileName : serviceXMLFileNames) {
			Document serviceXMLDocument = SourceUtil.readXML(
				FileUtil.read(new File(serviceXMLFileName)));

			if (serviceXMLDocument == null) {
				continue;
			}

			Element serviceXMLElement = serviceXMLDocument.getRootElement();

			serviceXMLFileName = StringUtil.replace(
				serviceXMLFileName, CharPool.BACK_SLASH, CharPool.SLASH);

			String packagePath = serviceXMLElement.attributeValue(
				"api-package-path");

			if (packagePath == null) {
				packagePath = serviceXMLElement.attributeValue("package-path");
			}

			if (packagePath == null) {
				continue;
			}

			String tablesSQLFilePath = "";

			if (serviceXMLFileName.contains("/portal-impl/")) {
				tablesSQLFilePath = portalDir + "/sql/portal-tables.sql";
			}
			else {
				int x = serviceXMLFileName.lastIndexOf("/");

				tablesSQLFilePath =
					serviceXMLFileName.substring(0, x) +
						"/src/main/resources/META-INF/sql/tables.sql";
			}

			_modelInformationsMap.put(
				packagePath,
				new Object[] {serviceXMLElement, tablesSQLFilePath});
		}
	}

	protected static final String RUN_OUTSIDE_PORTAL_EXCLUDES =
		"run.outside.portal.excludes";

	private JavaClass _getJavaClass(
			JavaTerm javaTerm, String fileName, String fileContent)
		throws Exception {

		if (javaTerm == null) {
			return JavaClassParser.parseJavaClass(fileName, fileContent);
		}

		if (javaTerm.isJavaClass()) {
			return (JavaClass)javaTerm;
		}

		return javaTerm.getParentJavaClass();
	}

	private String _getVariableTypeName(
		String content, String variableName,
		boolean includeArrayOrCollectionTypes,
		boolean includeFullyQualifiedName) {

		Pattern pattern = null;

		if (includeFullyQualifiedName) {
			pattern = Pattern.compile(
				"\\W((\\w+\\.)*\\w+)\\s+" + variableName + "\\s*[;=),:]");
		}
		else {
			pattern = Pattern.compile(
				"\\W(\\w+)\\s+" + variableName + "\\s*[;=),:]");
		}

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String group = matcher.group(1);

			if (!group.equals("return")) {
				return group;
			}
		}

		if (!includeArrayOrCollectionTypes) {
			return null;
		}

		pattern = Pattern.compile("[\\]>]\\s+" + variableName + "\\s*[;=),:]");

		matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		int i = matcher.start() + 1;

		for (int j = i - 2; j > 0; j--) {
			if (Character.isLetterOrDigit(content.charAt(j)) ||
				!Character.isLetterOrDigit(content.charAt(j + 1))) {

				continue;
			}

			String typeName = content.substring(j + 1, i);

			if ((getLevel(typeName, "<", ">") == 0) &&
				(getLevel(typeName, "[", "]") == 0)) {

				return typeName;
			}
		}

		return null;
	}

	private static final String _MODULES_PROPERTIES_FILE_NAME =
		"modules/modules.properties";

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSourceCheck.class);

	private JSONObject _attributesJSONObject = new JSONObjectImpl();
	private final Map<String, String> _attributeValueMap =
		new ConcurrentHashMap<>();
	private final Map<String, List<String>> _attributeValuesMap =
		new ConcurrentHashMap<>();
	private String _baseDirName;
	private final Map<String, BNDSettings> _bndSettingsMap =
		new ConcurrentHashMap<>();
	private Map<String, String> _bundleSymbolicNamesMap;
	private JSONObject _excludesJSONObject;
	private final Map<String, List<String>> _excludesValuesMap =
		new ConcurrentHashMap<>();
	private List<String> _fileExtensions;
	private List<String> _filterCheckNames;
	private int _maxDirLevel;
	private int _maxLineLength;
	private Map<String, Object[]> _modelInformationsMap;
	private List<String> _pluginsInsideModulesDirectoryNames;
	private Document _portalCustomSQLDocument;
	private boolean _portalSource;
	private String _projectName;
	private String _projectPathPrefix;
	private SourceFormatterExcludes _sourceFormatterExcludes;
	private final Map<String, Set<SourceFormatterMessage>>
		_sourceFormatterMessagesMap = new ConcurrentHashMap<>();
	private SourceProcessor _sourceProcessor;
	private boolean _subrepository;
	private int _weight;

}