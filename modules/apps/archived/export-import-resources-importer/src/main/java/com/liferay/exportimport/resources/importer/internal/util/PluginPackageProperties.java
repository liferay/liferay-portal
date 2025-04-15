/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * @author Michael C. Han
 */
public class PluginPackageProperties {

	public PluginPackageProperties(ServletContext servletContext)
		throws IOException {

		InputStream inputStream = servletContext.getResourceAsStream(
			"/WEB-INF/liferay-plugin-package.properties");

		if (inputStream == null) {
			return;
		}

		String propertiesString = StringUtil.read(inputStream);

		String contextPath = servletContext.getRealPath(StringPool.SLASH);

		if (contextPath == null) {
			contextPath = servletContext.getContextPath();
		}

		contextPath = StringUtil.replace(
			contextPath, CharPool.BACK_SLASH, CharPool.SLASH);

		propertiesString = StringUtil.replace(
			propertiesString, "${context.path}", contextPath);

		PropertiesUtil.load(_properties, propertiesString);
	}

	public String getProperty(String key) {
		return _properties.getProperty(key);
	}

	public String getResourcesDir() {
		return _properties.getProperty("resources-importer-external-dir");
	}

	public String getTargetClassName() {
		return _properties.getProperty(
			"resources-importer-target-class-name",
			LayoutSetPrototype.class.getName());
	}

	public String getTargetValue() {
		return _properties.getProperty("resources-importer-target-value");
	}

	public boolean indexAfterImport() {
		return GetterUtil.getBoolean(
			_properties.getProperty("resources-importer-index-after-import"),
			true);
	}

	public boolean isAppendVersion() {
		return GetterUtil.getBoolean(
			_properties.getProperty("resources-importer-append-version"), true);
	}

	public boolean isDeveloperModeEnabled() {
		if (GetterUtil.getBoolean(
				_properties.getProperty(
					"resources-importer-developer-mode-enabled")) ||
			PortalRunMode.isTestMode()) {

			return true;
		}

		return false;
	}

	public boolean isUpdateModeEnabled() {
		return GetterUtil.getBoolean(
			_properties.getProperty("resources-importer-update-mode-enabled"));
	}

	private final Properties _properties = new Properties();

}