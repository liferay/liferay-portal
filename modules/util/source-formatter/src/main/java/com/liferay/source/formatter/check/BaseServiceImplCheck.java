/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Carlos Correa
 * @author Igor Beslic
 */
public abstract class BaseServiceImplCheck extends BaseJavaTermCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	protected String getEntityName(String className) {
		if (className.indexOf("LocalServiceImpl") > 0) {
			return className.substring(
				0, className.indexOf("LocalServiceImpl"));
		}

		return className.substring(0, className.indexOf("ServiceImpl"));
	}

	protected List<String> getErcEnabledEntityNames(Document document) {
		if (document == null) {
			return Collections.emptyList();
		}

		Element serviceXMLElement = document.getRootElement();

		Iterator<Element> iterator = serviceXMLElement.elementIterator(
			"entity");

		List<String> ercEnabledEntityNames = new ArrayList<>();

		while (iterator.hasNext()) {
			Element element = iterator.next();

			if (element.attributeValue("external-reference-code") != null) {
				ercEnabledEntityNames.add(element.attributeValue("name"));

				continue;
			}

			Iterator<Element> columnIterator = element.elementIterator(
				"column");

			while (columnIterator.hasNext()) {
				Element columnElement = columnIterator.next();

				if (StringUtil.equals(
						columnElement.attributeValue("name"),
						"externalReferenceCode")) {

					ercEnabledEntityNames.add(element.attributeValue("name"));

					break;
				}
			}
		}

		return ercEnabledEntityNames;
	}

	protected Document getServiceXmlDocument(String absolutePath)
		throws IOException {

		Path serviceXmlPath = Paths.get(absolutePath);

		do {
			serviceXmlPath = serviceXmlPath.getParent();
		}
		while (!serviceXmlPath.endsWith("src"));

		serviceXmlPath = serviceXmlPath.getParent();

		serviceXmlPath = serviceXmlPath.resolve("service.xml");

		File file = serviceXmlPath.toFile();

		if (!file.exists()) {
			return null;
		}

		return SourceUtil.readXML(FileUtil.read(file));
	}

	protected boolean isApplicableCheck(
		String entityName, String entityReturnType, String javaTermName) {

		if (javaTermName.equals("add") ||
			javaTermName.equals("add" + entityReturnType) ||
			(javaTermName.startsWith("add") &&
			 StringUtil.equals(entityName, entityReturnType))) {

			return true;
		}

		return false;
	}

	protected boolean isInsideComment(String content, int pos) {
		String s = content.substring(pos);

		int x = s.indexOf("*/");

		if (x == -1) {
			return false;
		}

		s = s.substring(0, x);

		return !s.contains("/*");
	}

}