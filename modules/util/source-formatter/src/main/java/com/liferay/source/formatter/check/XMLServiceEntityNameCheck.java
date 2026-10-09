/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLServiceEntityNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws DocumentException {

		if (!fileName.endsWith("/service.xml")) {
			return content;
		}

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return content;
		}

		Element rootElement = document.getRootElement();

		String packagePathName = rootElement.attributeValue("package-path");

		if (!packagePathName.startsWith("com.liferay.")) {
			return content;
		}

		String trimmedPackage = StringUtil.removeChar(
			packagePathName, CharPool.PERIOD);

		for (Element entityElement :
				(List<Element>)rootElement.elements("entity")) {

			String entityName = entityElement.attributeValue("name");

			if (!trimmedPackage.endsWith(StringUtil.lowerCase(entityName))) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Do not use entity \"", entityName, "\" when package is \"",
					packagePathName, "\""));
		}

		return content;
	}

}