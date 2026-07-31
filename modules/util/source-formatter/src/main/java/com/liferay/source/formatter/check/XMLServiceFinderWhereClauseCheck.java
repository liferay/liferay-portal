/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Jiefeng Wu
 */
public class XMLServiceFinderWhereClauseCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("/service.xml")) {
			return content;
		}

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return content;
		}

		Element rootElement = document.getRootElement();

		for (Element entityElement :
				(List<Element>)rootElement.elements("entity")) {

			for (Element finderElement :
					(List<Element>)entityElement.elements("finder")) {

				String finderWhere = finderElement.attributeValue("where");

				if (Validator.isNull(finderWhere)) {
					continue;
				}

				String s = finderWhere.replaceAll(
					"'([^'\\\\]|\\\\.)*'", StringPool.BLANK);

				if (!s.contains(StringPool.UNDERLINE)) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Finder \"", entityElement.attributeValue("name"), "#",
						finderElement.attributeValue("name"),
						"\" should use the entity property name instead of ",
						"the database column name in its where clause. See ",
						"LPD-98149."));
			}
		}

		return content;
	}

}