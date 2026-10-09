/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.source.formatter.check.comparator.ElementComparator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLBuildFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.startsWith(getBaseDirName() + "build") ||
			(fileName.contains("/build") && !fileName.contains("/tools/"))) {

			_checkBuildXML(fileName, absolutePath, content);
		}

		return content;
	}

	private void _checkBuildProjectName(String fileName, Document document) {
		Matcher matcher = _projectNamePattern.matcher(fileName);

		if (!matcher.find()) {
			return;
		}

		String expectedProjectName = matcher.group(1);

		Element rootElement = document.getRootElement();

		String projectName = rootElement.attributeValue("name");

		if (!projectName.equals(expectedProjectName)) {
			addMessage(
				fileName, "Incorrect project name \"" + projectName + "\"");
		}
	}

	private void _checkBuildXML(
			String fileName, String absolutePath, String content)
		throws IOException {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		_checkBuildProjectName(fileName, document);

		checkElementOrder(
			fileName, document.getRootElement(), "macrodef", null,
			new ElementComparator());
		checkElementOrder(
			fileName, document.getRootElement(), "target", null,
			new ElementComparator());

		int x = content.lastIndexOf("\n\t</macrodef>");
		int y = content.indexOf("\n\t<process-ivy");

		if ((y != -1) && (x > y)) {
			addMessage(fileName, "Macrodefs go before process-ivy");
		}

		int z = content.indexOf("\n\t</target>");

		if ((z != -1) && (x > z)) {
			addMessage(fileName, "Macrodefs go before targets");
		}

		if (!isModulesApp(absolutePath, true)) {
			_checkImportFiles(fileName, content);
		}

		_checkTargetNames(fileName, absolutePath, content);
	}

	private void _checkImportFiles(String fileName, String content) {
		int pos = fileName.lastIndexOf(StringPool.SLASH);

		if (pos == -1) {
			return;
		}

		String dirPath = fileName.substring(0, pos + 1);

		Matcher matcher = _importFilePattern.matcher(content);

		while (matcher.find()) {
			String importFileName = matcher.group(1);

			if (importFileName.contains("${")) {
				continue;
			}

			File file = new File(dirPath + importFileName);

			if (file.exists()) {
				continue;
			}

			addMessage(
				fileName, "Incorrect import file \"" + importFileName + "\"");
		}
	}

	private void _checkTargetName(
			String targetName, String buildFileName, String fileName)
		throws IOException {

		List<String> targetNames = _getTargetNames(
			buildFileName, fileName, null, false);

		if ((targetNames == null) || targetNames.contains(targetName)) {
			return;
		}

		int x = targetName.lastIndexOf(CharPool.PERIOD);

		if (x != -1) {
			targetName = targetName.substring(x + 1);
		}

		if (!targetNames.contains(targetName)) {
			addMessage(
				fileName, "Target \"" + targetName + "\" does not exist");
		}
	}

	private void _checkTargetNames(
			String fileName, String absolutePath, String content)
		throws IOException {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		List<Element> antCallElements = _getElementsByName(
			"antcall", rootElement, null);

		for (Element antCallElement : antCallElements) {
			String targetName = antCallElement.attributeValue("target");

			if ((targetName == null) ||
				targetName.contains(StringPool.OPEN_CURLY_BRACE)) {

				continue;
			}

			_checkTargetName(targetName, absolutePath, fileName);
		}

		String fileDirName = fileName.substring(
			0, fileName.lastIndexOf(CharPool.SLASH) + 1);

		List<Element> antElements = _getElementsByName(
			"ant", rootElement, null);

		for (Element antElement : antElements) {
			String targetName = antElement.attributeValue("target");

			if ((targetName == null) ||
				targetName.contains(StringPool.OPEN_CURLY_BRACE)) {

				continue;
			}

			String fullDirName = fileDirName;

			String dirName = antElement.attributeValue("dir");

			if (dirName != null) {
				if (dirName.contains(StringPool.OPEN_CURLY_BRACE)) {
					continue;
				}

				fullDirName = fullDirName + dirName + StringPool.SLASH;
			}

			String antFileName = antElement.attributeValue("antfile");

			if (antFileName == null) {
				antFileName = "build.xml";
			}

			_checkTargetName(targetName, fullDirName + antFileName, fileName);
		}
	}

	private List<Element> _getElementsByName(
		String name, Element element, List<Element> elements) {

		if (elements == null) {
			elements = new ArrayList<>();
		}

		List<Element> childElements = element.elements();

		for (Element childElement : childElements) {
			String elementName = childElement.getName();

			if (elementName.equals(name)) {
				elements.add(childElement);
			}

			elements = _getElementsByName(name, childElement, elements);
		}

		return elements;
	}

	private List<String> _getTargetNames(
			String buildFileName, String fileName, List<String> targetNames,
			boolean importFile)
		throws IOException {

		if (buildFileName.contains(StringPool.OPEN_CURLY_BRACE)) {
			return null;
		}

		File file = new File(buildFileName);

		if (!file.exists()) {
			if (!importFile &&
				!buildFileName.endsWith("build-working-dir.xml")) {

				addMessage(
					fileName,
					"Ant element points to nonexistent build file \"" +
						buildFileName + "\"");
			}

			return null;
		}

		Document document = SourceUtil.readXML(FileUtil.read(file));

		if (document == null) {
			return null;
		}

		Element rootElement = document.getRootElement();

		if (targetNames == null) {
			targetNames = new ArrayList<>();
		}

		List<Element> targetElements = rootElement.elements("target");

		for (Element targetElement : targetElements) {
			targetNames.add(targetElement.attributeValue("name"));
		}

		List<Element> importElements = rootElement.elements("import");

		for (Element importElement : importElements) {
			String buildDirName = buildFileName.substring(
				0, buildFileName.lastIndexOf(CharPool.SLASH) + 1);

			String importFileName =
				buildDirName + importElement.attributeValue("file");

			targetNames = _getTargetNames(
				importFileName, fileName, targetNames, true);
		}

		return targetNames;
	}

	private static final Pattern _importFilePattern = Pattern.compile(
		"<import file=\"(.*)\"");
	private static final Pattern _projectNamePattern = Pattern.compile(
		"/(\\w*-(ext|hooks|layouttpl|portlet|theme|web))/build\\.xml$");

}