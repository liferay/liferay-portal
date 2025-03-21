/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import jakarta.servlet.ServletContext;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public interface Importer {

	public void afterPropertiesSet() throws Exception;

	public long getGroupId();

	public String getTargetClassName();

	public long getTargetClassPK();

	public void importResources() throws Exception;

	public boolean isCompanyGroup() throws Exception;

	public boolean isDeveloperModeEnabled();

	public boolean isExisting();

	public boolean isIndexAfterImport();

	public void setAppendVersion(boolean appendVersion);

	public void setCompanyId(long companyId);

	public void setDeveloperModeEnabled(boolean developerModeEnabled);

	public void setGroupId(long groupId);

	public void setIndexAfterImport(boolean indexAfterImport);

	public void setResourcesDir(String resourcesDir);

	public void setServletContext(ServletContext servletContext);

	public void setServletContextName(String servletContextName);

	public void setTargetClassName(String className);

	public void setTargetValue(String targetValue);

	public void setUpdateModeEnabled(boolean updateModeEnabled);

	public void setVersion(String version);

}