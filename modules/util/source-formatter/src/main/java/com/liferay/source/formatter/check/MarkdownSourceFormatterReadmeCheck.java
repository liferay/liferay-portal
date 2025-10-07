/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class MarkdownSourceFormatterReadmeCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/source-formatter/README.md")) {
			return content;
		}

		int x = content.indexOf("\n## Checks\n");

		if (x == -1) {
			return content;
		}

		String checksInformation = content.substring(x + 1);

		try {
			String newChecksInformation = _getChecksInformation(
				absolutePath, _getCheckInfoMap());

			if (!checksInformation.equals(newChecksInformation)) {
				return StringUtil.replaceLast(
					content, checksInformation, newChecksInformation);
			}
		}
		catch (DocumentException documentException) {
			return content;
		}

		return content;
	}

	private Map<String, CheckInfo> _addCheckstyleChecks(
		Map<String, CheckInfo> checkInfoMap, Element moduleElement,
		String sourceProcessorName) {

		String checkName = moduleElement.attributeValue("name");

		if (!checkName.endsWith("Check")) {
			for (Element childModuleElement :
					(List<Element>)moduleElement.elements("module")) {

				checkInfoMap = _addCheckstyleChecks(
					checkInfoMap, childModuleElement, sourceProcessorName);
			}

			return checkInfoMap;
		}

		int x = checkName.lastIndexOf(CharPool.PERIOD);

		if (x != -1) {
			checkName = checkName.substring(x + 1);
		}

		CheckInfo checkInfo = checkInfoMap.get(checkName);

		if (checkInfo != null) {
			checkInfo.addSourceProcessorName(sourceProcessorName);

			checkInfoMap.put(checkName, checkInfo);

			return checkInfoMap;
		}

		String category = _getPropertyValue(moduleElement, "category");

		if (Validator.isNull(category)) {
			category = "Miscellaneous";
		}

		checkInfoMap.put(
			checkName,
			new CheckInfo(
				checkName, category,
				_getPropertyValue(moduleElement, "description"),
				_getPropertyValue(moduleElement, "documentationLocation"),
				CheckType.CHECKSTYLE, sourceProcessorName));

		return checkInfoMap;
	}

	private Map<String, CheckInfo> _addCheckstyleChecks(
			Map<String, CheckInfo> checkInfoMap,
			String configurationFileLocation, String sourceProcessorName)
		throws DocumentException, IOException {

		String checkstyleConfigurationContent = getContent(
			configurationFileLocation, getMaxDirLevel());

		Document document = SourceUtil.readXML(checkstyleConfigurationContent);

		if (document == null) {
			throw new DocumentException();
		}

		return _addCheckstyleChecks(
			checkInfoMap, document.getRootElement(), sourceProcessorName);
	}

	private Map<String, CheckInfo> _addSourceChecks(
			Map<String, CheckInfo> checkInfoMap,
			String configurationFileLocation)
		throws DocumentException, IOException {

		String sourceChecksConfigurationContent = getContent(
			configurationFileLocation, getMaxDirLevel());

		Document document = SourceUtil.readXML(
			sourceChecksConfigurationContent);

		if (document == null) {
			throw new DocumentException();
		}

		Element rootElement = document.getRootElement();

		for (Element sourceProcessorElement :
				(List<Element>)rootElement.elements("source-processor")) {

			String sourceProcessorName = sourceProcessorElement.attributeValue(
				"name");

			for (Element checkElement :
					(List<Element>)sourceProcessorElement.elements("check")) {

				String checkName = checkElement.attributeValue("name");

				CheckInfo checkInfo = checkInfoMap.get(checkName);

				if (checkInfo != null) {
					checkInfo.addSourceProcessorName(sourceProcessorName);

					checkInfoMap.put(checkName, checkInfo);

					continue;
				}

				Element categoryElement = checkElement.element("category");

				String category = "Miscellaneous";

				if (categoryElement != null) {
					category = categoryElement.attributeValue("name");
				}

				Element descriptionElement = checkElement.element(
					"description");

				String description = StringPool.BLANK;

				if (descriptionElement != null) {
					description = descriptionElement.attributeValue("name");
				}

				checkInfoMap.put(
					checkName,
					new CheckInfo(
						checkName, category, description, StringPool.BLANK,
						CheckType.SOURCE_CHECK, sourceProcessorName));
			}
		}

		return checkInfoMap;
	}

	private void _createChecksTableMarkdown(
			String header, String rootFolderLocation, File file,
			Collection<CheckInfo> checkInfos, File documentationChecksDir,
			boolean displayCategory, boolean displayFileExtensions)
		throws IOException {

		String content = StringPool.BLANK;

		if (file.exists()) {
			content = FileUtil.read(file);
		}

		StringBundler sb = new StringBundler();

		sb.append("# ");
		sb.append(header);
		sb.append("\n\n");
		sb.append("Check | ");

		if (displayCategory) {
			sb.append("Category | ");
		}

		if (displayFileExtensions) {
			sb.append("File Extensions | ");
		}

		sb.append("Description\n");
		sb.append("----- | ");

		if (displayCategory) {
			sb.append("-------- | ");
		}

		if (displayFileExtensions) {
			sb.append("--------------- | ");
		}

		sb.append("-----------\n");

		for (CheckInfo checkInfo : checkInfos) {
			String checkName = checkInfo.getName();

			String documentationLink = _getDocumentationLink(
				rootFolderLocation, documentationChecksDir, checkInfo);

			if (documentationLink != null) {
				sb.append("[");
				sb.append(checkName);
				sb.append("](");
				sb.append(documentationLink);
				sb.append(") | ");
			}
			else {
				sb.append(checkName);
				sb.append(" | ");
			}

			if (displayCategory) {
				String category = checkInfo.getCategory();

				if (Validator.isNotNull(category)) {
					String categoryWithoutSpace = StringUtil.removeChar(
						category, CharPool.SPACE);

					sb.append(
						_getLink(
							SourceFormatterUtil.getMarkdownFileName(
								categoryWithoutSpace + "Checks"),
							"", category, category + " Checks"));

					sb.append(" | ");
				}
				else {
					sb.append("| ");
				}
			}

			if (displayFileExtensions) {
				String fileExtensionsString;

				if (StringUtil.equals(
						checkInfo.getCategory(), "JakartaTransform")) {

					fileExtensionsString = _getFileExtensionsString(checkName);
				}
				else {
					fileExtensionsString = _getFileExtensionsString(
						checkInfo.getSourceProcessorNames());
				}

				if (Validator.isNotNull(fileExtensionsString)) {
					sb.append(fileExtensionsString);
					sb.append(" | ");
				}
				else {
					sb.append("| ");
				}
			}

			if (Validator.isNotNull(checkInfo.getDescription())) {
				sb.append(checkInfo.getDescription());
				sb.append(" |");
			}
			else {
				sb.append("|");
			}

			sb.append("\n");
		}

		String newContent = StringUtil.trim(sb.toString());

		if (content.equals(newContent)) {
			return;
		}

		String absolutePath = SourceUtil.getAbsolutePath(file);

		if (Validator.isNull(content)) {
			System.out.println("Added " + absolutePath);
		}
		else {
			System.out.println("Updated " + absolutePath);
		}

		FileUtil.write(file, StringUtil.trim(sb.toString()));
	}

	private Set<String> _getCategories(Map<String, CheckInfo> checkInfoMap) {
		Set<String> categories = new TreeSet<>();

		for (CheckInfo checkInfo : checkInfoMap.values()) {
			categories.add(checkInfo.getCategory());
		}

		return categories;
	}

	private List<CheckInfo> _getCategoryCheckInfos(
		String category, Map<String, CheckInfo> checkInfoMap) {

		List<CheckInfo> checkInfos = new ArrayList<>();

		for (CheckInfo checkInfo : checkInfoMap.values()) {
			if (category.equals(checkInfo.getCategory())) {
				checkInfos.add(checkInfo);
			}
		}

		return checkInfos;
	}

	private Map<String, CheckInfo> _getCheckInfoMap()
		throws DocumentException, IOException {

		Map<String, CheckInfo> checkInfoMap = new TreeMap<>();

		String resourcesDirLocation =
			"modules/util/source-formatter/src/main/resources/";

		checkInfoMap = _addCheckstyleChecks(
			checkInfoMap, resourcesDirLocation + "checkstyle.xml",
			"JavaSourceProcessor");
		checkInfoMap = _addCheckstyleChecks(
			checkInfoMap, resourcesDirLocation + "checkstyle-jsp.xml",
			"JSPSourceProcessor");
		checkInfoMap = _addSourceChecks(
			checkInfoMap, resourcesDirLocation + "sourcechecks.xml");

		return checkInfoMap;
	}

	private String _getChecksInformation(
			String absolutePath, Map<String, CheckInfo> checkInfoMap)
		throws DocumentException, IOException {

		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		String rootFolderLocation = absolutePath.substring(0, x + 1);

		File documentationDir = new File(
			rootFolderLocation + _DOCUMENTATION_DIR_LOCATION);

		File documentationChecksDir = new File(
			documentationDir, _DOCUMENTATION_CHECKS_DIR_NAME);

		StringBundler sb = new StringBundler();

		sb.append("## Checks\n\n");

		String headerName = "All Checks";
		String markdownFileName = SourceFormatterUtil.getMarkdownFileName(
			"AllChecks");

		sb.append(
			_getLink(
				_DOCUMENTATION_DIR_LOCATION + markdownFileName, "- ### ",
				headerName, headerName));

		sb.append("\n\n");

		_createChecksTableMarkdown(
			headerName, rootFolderLocation,
			new File(documentationDir, markdownFileName), checkInfoMap.values(),
			documentationChecksDir, true, true);

		sb.append("- ### By Category:\n");

		for (String category : _getCategories(checkInfoMap)) {
			headerName = category + " Checks";
			markdownFileName = SourceFormatterUtil.getMarkdownFileName(
				StringUtil.removeChar(category, CharPool.SPACE) + "Checks");

			sb.append(
				_getLink(
					_DOCUMENTATION_DIR_LOCATION + markdownFileName, "   - ",
					category, headerName));

			sb.append("\n");

			_createChecksTableMarkdown(
				headerName, rootFolderLocation,
				new File(documentationDir, markdownFileName),
				_getCategoryCheckInfos(category, checkInfoMap),
				documentationChecksDir, false, true);
		}

		sb.append("\n");
		sb.append("- ### By File Extensions:\n");

		for (String sourceProcessorName :
				_getSourceProcessorNames(checkInfoMap)) {

			if (sourceProcessorName.equals("all")) {
				continue;
			}

			String fileExtensionsString = _getFileExtensionsString(
				ListUtil.fromArray(sourceProcessorName));

			headerName = "Checks for " + fileExtensionsString;

			markdownFileName = SourceFormatterUtil.getMarkdownFileName(
				sourceProcessorName + "Checks");

			sb.append(
				_getLink(
					_DOCUMENTATION_DIR_LOCATION + markdownFileName, "   - ",
					fileExtensionsString, headerName));

			sb.append("\n");

			_createChecksTableMarkdown(
				"Checks for " + fileExtensionsString, rootFolderLocation,
				new File(documentationDir, markdownFileName),
				_getSourceProcessorCheckInfos(
					sourceProcessorName, checkInfoMap),
				documentationChecksDir, true, false);
		}

		return StringUtil.trim(sb.toString());
	}

	private String _getDocumentationLink(
		File documentationChecksDir, String checkName) {

		String markdownFileName = SourceFormatterUtil.getMarkdownFileName(
			checkName);

		File markdownFile = new File(documentationChecksDir, markdownFileName);

		if (markdownFile.exists()) {
			return StringBundler.concat(
				_DOCUMENTATION_CHECKS_DIR_NAME, markdownFileName, "#",
				StringUtil.toLowerCase(checkName));
		}

		return null;
	}

	private String _getDocumentationLink(
		String rootFolderLocation, File documentationChecksDir,
		CheckInfo checkInfo) {

		String documentationLocation = checkInfo.getDocumentationLocation();

		if (Validator.isNotNull(documentationLocation)) {
			return SourceFormatterUtil.CHECKSTYLE_DOCUMENTATION_URL_BASE +
				documentationLocation;
		}

		String checkName = checkInfo.getName();

		String documentationLink = _getDocumentationLink(
			documentationChecksDir, checkName);

		if (documentationLink != null) {
			return documentationLink;
		}

		File sourceDir = null;

		CheckType checkType = checkInfo.getCheckType();

		if (checkType.equals(CheckType.CHECKSTYLE)) {
			sourceDir = new File(
				rootFolderLocation + _CHECKSTYLE_SOURCE_LOCATION);
		}
		else {
			sourceDir = new File(
				rootFolderLocation + _SOURCE_CHECKS_SOURCE_LOCATION);
		}

		File sourceFile = new File(sourceDir, checkName + ".java");

		if (!sourceFile.exists()) {
			return null;
		}

		JavaClass javaClass = null;

		try {
			javaClass = JavaClassParser.parseJavaClass(
				sourceFile.getName(), FileUtil.read(sourceFile));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (extendedClassNames.isEmpty()) {
			return null;
		}

		String extendedClassName = extendedClassNames.get(0);

		sourceFile = new File(sourceDir, extendedClassName + ".java");

		if (!sourceFile.exists()) {
			return null;
		}

		documentationLink = _getDocumentationLink(
			documentationChecksDir, extendedClassName);

		if ((documentationLink != null) ||
			!extendedClassName.startsWith("Base")) {

			return documentationLink;
		}

		return _getDocumentationLink(
			documentationChecksDir, extendedClassName.substring(4));
	}

	private List<String> _getFileExtensionsByCheckName(String checkName) {
		try {
			Class<?> clazz = Class.forName(
				"com.liferay.source.formatter.check." + checkName);

			Field field = clazz.getDeclaredField("_VALID_EXTENSIONS");

			field.setAccessible(true);

			return ListUtil.fromArray((String[])field.get(null));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return Collections.emptyList();
	}

	private List<String> _getFileExtensionsBySourceProcessName(
		String sourceProcessorName) {

		List<String> fileExtensions = _sourceProcessorFileExtensionsMap.get(
			sourceProcessorName);

		if (fileExtensions != null) {
			return fileExtensions;
		}

		fileExtensions = new ArrayList<>();

		try {
			Class<?> clazz = Class.forName(
				"com.liferay.source.formatter.processor." +
					sourceProcessorName);

			Field field = clazz.getDeclaredField("_INCLUDES");

			field.setAccessible(true);

			String[] includes = (String[])field.get(null);

			for (String include : includes) {
				int x = include.lastIndexOf(CharPool.PERIOD);
				int y = include.lastIndexOf(CharPool.SLASH);

				if (x < y) {
					fileExtensions.add(include.substring(y + 1));
				}
				else {
					fileExtensions.add(include.substring(x));
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		_sourceProcessorFileExtensionsMap.put(
			sourceProcessorName, fileExtensions);

		return fileExtensions;
	}

	private String _getFileExtensionsString(List<String> sourceProcessorNames) {
		List<String> fileExtensions = new ArrayList<>();

		for (String sourceProcessorName : sourceProcessorNames) {
			fileExtensions.addAll(
				_getFileExtensionsBySourceProcessName(sourceProcessorName));
		}

		ListUtil.distinct(fileExtensions);

		return _mergeFileExtensions(fileExtensions);
	}

	private String _getFileExtensionsString(String checkName) {
		List<String> fileExtensions = _getFileExtensionsByCheckName(checkName);

		return _mergeFileExtensions(fileExtensions);
	}

	private String _getLink(
		String markdownFileName, String prefix, String linkName,
		String headerName) {

		StringBundler sb = new StringBundler(8);

		sb.append(prefix);
		sb.append("[");
		sb.append(linkName);
		sb.append("](");
		sb.append(markdownFileName);
		sb.append("#");

		String headerLink = headerName.replaceAll("[^ \\w]", StringPool.BLANK);

		headerLink = StringUtil.replace(
			headerLink, CharPool.SPACE, CharPool.DASH);

		sb.append(StringUtil.toLowerCase(headerLink));

		sb.append(")");

		return sb.toString();
	}

	private String _getPropertyValue(
		Element moduleElement, String propertyName) {

		for (Element propertyElement :
				(List<Element>)moduleElement.elements("property")) {

			if (propertyName.equals(propertyElement.attributeValue("name"))) {
				return propertyElement.attributeValue("value");
			}
		}

		return StringPool.BLANK;
	}

	private List<CheckInfo> _getSourceProcessorCheckInfos(
		String sourceProcessorName, Map<String, CheckInfo> checkInfoMap) {

		List<CheckInfo> checkInfos = new ArrayList<>();

		for (CheckInfo checkInfo : checkInfoMap.values()) {
			List<String> sourceProcessorNames =
				checkInfo.getSourceProcessorNames();

			if (sourceProcessorNames.contains(sourceProcessorName)) {
				checkInfos.add(checkInfo);
			}
		}

		return checkInfos;
	}

	private Set<String> _getSourceProcessorNames(
		Map<String, CheckInfo> checkInfoMap) {

		Set<String> sourceProcessorNames = new TreeSet<>(
			(sourceProcessorName1, sourceProcessorName2) -> {
				String fileExtensionsString1 = _getFileExtensionsString(
					ListUtil.fromArray(sourceProcessorName1));
				String fileExtensionsString2 = _getFileExtensionsString(
					ListUtil.fromArray(sourceProcessorName2));

				return fileExtensionsString1.compareTo(fileExtensionsString2);
			});

		for (CheckInfo checkInfo : checkInfoMap.values()) {
			for (String sourceProcessorName :
					checkInfo.getSourceProcessorNames()) {

				if (!sourceProcessorName.equals("all")) {
					sourceProcessorNames.add(sourceProcessorName);
				}
			}
		}

		return sourceProcessorNames;
	}

	private String _mergeFileExtensions(List<String> fileExtensions) {
		if (fileExtensions.size() == 1) {
			return fileExtensions.get(0);
		}

		Collections.sort(fileExtensions);

		if (fileExtensions.size() == 2) {
			return fileExtensions.get(0) + " or " + fileExtensions.get(1);
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < fileExtensions.size(); i++) {
			sb.append(fileExtensions.get(i));
			sb.append(", ");

			if (i == (fileExtensions.size() - 2)) {
				sb.append("or ");
			}
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final String _CHECKSTYLE_SOURCE_LOCATION =
		"src/main/java/com/liferay/source/formatter/checkstyle/check/";

	private static final String _DOCUMENTATION_CHECKS_DIR_NAME = "check/";

	private static final String _DOCUMENTATION_DIR_LOCATION =
		"src/main/resources/documentation/";

	private static final String _SOURCE_CHECKS_SOURCE_LOCATION =
		"src/main/java/com/liferay/source/formatter/check/";

	private static final Log _log = LogFactoryUtil.getLog(
		MarkdownSourceFormatterReadmeCheck.class);

	private final Map<String, List<String>> _sourceProcessorFileExtensionsMap =
		new HashMap<>();

	private class CheckInfo implements Comparable<CheckInfo> {

		public CheckInfo(
			String name, String category, String description,
			String documentationLocation, CheckType checkType,
			String sourceProcessorName) {

			_name = name;
			_category = category;
			_description = description;
			_documentationLocation = documentationLocation;
			_checkType = checkType;

			_sourceProcessorNames.add(sourceProcessorName);
		}

		public void addSourceProcessorName(String sourceProcessorName) {
			_sourceProcessorNames.add(sourceProcessorName);
		}

		@Override
		public int compareTo(CheckInfo checkInfo) {
			return _name.compareTo(checkInfo.getName());
		}

		public String getCategory() {
			return _category;
		}

		public CheckType getCheckType() {
			return _checkType;
		}

		public String getDescription() {
			return _description;
		}

		public String getDocumentationLocation() {
			return _documentationLocation;
		}

		public String getName() {
			return _name;
		}

		public List<String> getSourceProcessorNames() {
			return _sourceProcessorNames;
		}

		private final String _category;
		private final CheckType _checkType;
		private final String _description;
		private final String _documentationLocation;
		private final String _name;
		private final List<String> _sourceProcessorNames = new ArrayList<>();

	}

}