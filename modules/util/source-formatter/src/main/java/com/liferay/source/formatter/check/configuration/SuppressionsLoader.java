/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.configuration;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class SuppressionsLoader {

	public static SourceFormatterSuppressions loadSuppressions(
			String baseDirName, List<File> files,
			Map<String, Properties> propertiesMap)
		throws DocumentException, IOException {

		SourceFormatterSuppressions sourceFormatterSuppressions =
			new SourceFormatterSuppressions();

		for (File file : files) {
			String content = FileUtil.read(file);

			Document document = SourceUtil.readXML(content);

			if (document == null) {
				throw new DocumentException();
			}

			Element rootElement = document.getRootElement();

			String absolutePath = SourceUtil.getAbsolutePath(file);

			sourceFormatterSuppressions = _loadCheckstyleSuppressions(
				sourceFormatterSuppressions,
				rootElement.element(_CHECKSTYLE_ATTRIBUTE_NAME), absolutePath);
			sourceFormatterSuppressions = _loadSourceChecksSuppressions(
				sourceFormatterSuppressions,
				rootElement.element(_SOURCE_CHECK_ATTRIBUTE_NAME),
				absolutePath);
		}

		for (Map.Entry<String, Properties> mapEntry :
				propertiesMap.entrySet()) {

			Properties properties = mapEntry.getValue();

			for (Map.Entry<Object, Object> propertiesEntry :
					properties.entrySet()) {

				String key = (String)propertiesEntry.getKey();

				if (!key.startsWith("checkstyle.") ||
					!key.endsWith(".enabled") ||
					GetterUtil.getBoolean(propertiesEntry.getValue())) {

					continue;
				}

				sourceFormatterSuppressions.addSuppression(
					CheckType.CHECKSTYLE, mapEntry.getKey() + "/",
					key.substring(11, key.length() - 8), null);
			}
		}

		return sourceFormatterSuppressions;
	}

	private static String _getFileLocation(String absolutePath) {
		int pos = absolutePath.lastIndexOf(CharPool.SLASH);

		return absolutePath.substring(0, pos + 1);
	}

	private static SourceFormatterSuppressions _loadCheckstyleSuppressions(
			SourceFormatterSuppressions sourceFormatterSuppressions,
			Element suppressionsElement, String absolutePath)
		throws DocumentException, IOException {

		if (suppressionsElement == null) {
			return sourceFormatterSuppressions;
		}

		List<Element> suppressElements =
			(List<Element>)suppressionsElement.elements("suppress");

		String suppressionsFileLocation = _getFileLocation(absolutePath);

		for (Element suppressElement : suppressElements) {
			sourceFormatterSuppressions.addSuppression(
				CheckType.CHECKSTYLE, suppressionsFileLocation,
				suppressElement.attributeValue(_CHECK_ATTRIBUTE_NAME),
				suppressElement.attributeValue(_FILE_REGEX_ATTRIBUTE_NAME));
		}

		return sourceFormatterSuppressions;
	}

	private static SourceFormatterSuppressions _loadSourceChecksSuppressions(
			SourceFormatterSuppressions sourceFormatterSuppressions,
			Element suppressionsElement, String absolutePath)
		throws DocumentException, IOException {

		if (suppressionsElement == null) {
			return sourceFormatterSuppressions;
		}

		List<Element> suppressElements =
			(List<Element>)suppressionsElement.elements("suppress");

		String suppressionsFileLocation = _getFileLocation(absolutePath);

		for (Element suppressElement : suppressElements) {
			sourceFormatterSuppressions.addSuppression(
				CheckType.SOURCE_CHECK, suppressionsFileLocation,
				suppressElement.attributeValue(_CHECK_ATTRIBUTE_NAME),
				suppressElement.attributeValue(_FILE_REGEX_ATTRIBUTE_NAME));
		}

		return sourceFormatterSuppressions;
	}

	private static final String _CHECK_ATTRIBUTE_NAME = "checks";

	private static final String _CHECKSTYLE_ATTRIBUTE_NAME = "checkstyle";

	private static final String _FILE_REGEX_ATTRIBUTE_NAME = "files";

	private static final String _SOURCE_CHECK_ATTRIBUTE_NAME = "source-check";

}