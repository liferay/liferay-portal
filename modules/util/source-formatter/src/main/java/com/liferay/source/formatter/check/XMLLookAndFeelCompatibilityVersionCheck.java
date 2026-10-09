/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.IOException;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Peter Shin
 */
public class XMLLookAndFeelCompatibilityVersionCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.endsWith("-look-and-feel.xml")) {
			_checkCompatibility(fileName, absolutePath, content);
		}

		return content;
	}

	private void _checkCompatibility(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!isPortalSource() || !isModulesApp(absolutePath, false)) {
			return;
		}

		boolean privateApp = isModulesApp(absolutePath, true);

		String portalVersion = getPortalVersion(privateApp);

		if (Validator.isNull(portalVersion)) {
			return;
		}

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		List<Element> compatibilityElements = rootElement.elements(
			"compatibility");

		for (Element compatibilityElement : compatibilityElements) {
			if (_hasPortalVersions(compatibilityElement, portalVersion)) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Missing version: <version>", portalVersion,
					"+</version>"));
		}
	}

	private boolean _hasPortalVersions(
		Element compatibilityElement, String portalVersion) {

		List<Element> versionElements = compatibilityElement.elements(
			"version");

		for (Element versionElement : versionElements) {
			String version = versionElement.getText();

			if (StringUtil.equalsIgnoreCase(version, portalVersion + "+")) {
				return true;
			}
		}

		return false;
	}

}