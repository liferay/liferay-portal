/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Kevin Lee
 */
public class XMLLog4jLoggersCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (isPortalSource() &&
			(fileName.endsWith("-log4j-ext.xml") ||
			 fileName.endsWith("-log4j.xml"))) {

			_checkLoggers(fileName, content);
		}

		return content;
	}

	private void _checkLoggers(String fileName, String content)
		throws Exception {

		Set<String> classNames = _getClassNames();

		if (classNames.isEmpty()) {
			return;
		}

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		for (Element loggersElement :
				(List<Element>)rootElement.elements("Loggers")) {

			for (Element loggerElement :
					(List<Element>)loggersElement.elements("Logger")) {

				String name = loggerElement.attributeValue("name");

				if (!name.startsWith("com.liferay") ||
					classNames.contains(name)) {

					continue;
				}

				boolean exists = false;

				for (String className : classNames) {
					if (className.startsWith(name)) {
						exists = true;

						break;
					}
				}

				if (exists) {
					continue;
				}

				addMessage(fileName, "Class/package does not exist: " + name);
			}
		}
	}

	private synchronized Set<String> _getClassNames() throws Exception {
		if (_classNames != null) {
			return _classNames;
		}

		_classNames = new HashSet<>();

		File file = getPortalDir();

		List<String> fileNames = SourceFormatterUtil.scanForFileNames(
			file.getCanonicalPath(), new String[] {"**/com/liferay/**/*.java"});

		for (String fileName : fileNames) {
			fileName = fileName.substring(
				0, fileName.lastIndexOf(CharPool.PERIOD));

			fileName = StringUtil.replace(
				fileName, CharPool.SLASH, CharPool.PERIOD);

			_classNames.add(
				fileName.substring(fileName.indexOf("com.liferay")));
		}

		return _classNames;
	}

	private Set<String> _classNames;

}