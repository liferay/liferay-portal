/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.theme.ThemeCompanyLimit;
import com.liferay.portal.kernel.theme.ThemeGroupLimit;

import jakarta.servlet.ServletContext;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
@ProviderType
public interface Theme extends Comparable<Theme>, Plugin, Serializable {

	public void addSetting(
		String key, String value, boolean configurable, String type,
		String[] options, String script);

	public List<ColorScheme> getColorSchemes();

	public Map<String, ColorScheme> getColorSchemesMap();

	public Map<String, ThemeSetting> getConfigurableSettings();

	public String getContextPath();

	public String getCssPath();

	public String getDevice();

	public String getFreeMarkerTemplateLoader();

	public String getImagesPath();

	public String getJavaScriptPath();

	public boolean getLoadFromServletContext();

	public String getName();

	public List<PortletDecorator> getPortletDecorators();

	public Map<String, PortletDecorator> getPortletDecoratorsMap();

	public String getResourcePath(
		ServletContext servletContext, String portletId, String path);

	public String getRootPath();

	public String getServletContextName();

	public String getSetting(String key);

	public String[] getSettingOptions(String key);

	public Map<String, ThemeSetting> getSettings();

	public Properties getSettingsProperties();

	public String getStaticResourcePath();

	public String getTemplateExtension();

	public String getTemplatesPath();

	public ThemeCompanyLimit getThemeCompanyLimit();

	public ThemeGroupLimit getThemeGroupLimit();

	public String getThemeId();

	public long getTimestamp();

	public String getVelocityResourceListener();

	public String getVirtualPath();

	public boolean getWARFile();

	public boolean hasColorSchemes();

	public boolean isCompanyAvailable(long companyId);

	public boolean isControlPanelTheme();

	public boolean isGroupAvailable(long groupId);

	public boolean isLoadFromServletContext();

	public boolean isPageTheme();

	public boolean isWARFile();

	public boolean resourceExists(
			ServletContext servletContext, String portletId, String path)
		throws Exception;

	public void setControlPanelTheme(boolean controlPanelTheme);

	public void setCssPath(String cssPath);

	public void setImagesPath(String imagesPath);

	public void setJavaScriptPath(String javaScriptPath);

	public void setLoadFromServletContext(boolean loadFromServletContext);

	public void setName(String name);

	public void setPageTheme(boolean pageTheme);

	public void setRootPath(String rootPath);

	public void setServletContextName(String servletContextName);

	public void setSetting(String key, String value);

	public void setTemplateExtension(String templateExtension);

	public void setTemplatesPath(String templatesPath);

	public void setThemeCompanyLimit(ThemeCompanyLimit themeCompanyLimit);

	public void setThemeGroupLimit(ThemeGroupLimit themeGroupLimit);

	public void setTimestamp(long timestamp);

	public void setVirtualPath(String virtualPath);

}