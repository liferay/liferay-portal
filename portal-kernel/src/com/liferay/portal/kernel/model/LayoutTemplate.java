/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.Serializable;

import java.util.List;
import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public interface LayoutTemplate
	extends Comparable<LayoutTemplate>, Plugin, Serializable {

	public List<String> getColumns();

	public String getContent();

	public String getContextPath();

	public String getLayoutTemplateId();

	public String getName();

	public String getName(Locale locale);

	public String getServletContextName();

	public boolean getStandard();

	public String getStaticResourcePath();

	public String getTemplatePath();

	public String getThemeId();

	public String getThumbnailPath();

	public String getUncachedContent() throws IOException;

	public boolean getWARFile();

	public boolean hasSetContent();

	public boolean isStandard();

	public boolean isWARFile();

	public void setColumns(List<String> columns);

	public void setContent(String content);

	public void setName(String name);

	public void setServletContext(ServletContext servletContext);

	public void setServletContextName(String servletContextName);

	public void setStandard(boolean standard);

	public void setTemplatePath(String templatePath);

	public void setThemeId(String themeId);

	public void setThumbnailPath(String thumbnailPath);

}