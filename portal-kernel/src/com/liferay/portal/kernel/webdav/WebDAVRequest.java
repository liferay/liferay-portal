/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.webdav;

import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public interface WebDAVRequest {

	public long getCompanyId();

	public long getGroupId();

	public HttpServletRequest getHttpServletRequest();

	public HttpServletResponse getHttpServletResponse();

	public String getLockUuid();

	public String getPath();

	public String[] getPathArray();

	public PermissionChecker getPermissionChecker();

	public String getRootPath();

	public long getUserId();

	public WebDAVStorage getWebDAVStorage();

	public boolean isAppleDoubleRequest();

	public boolean isLitmus();

	public boolean isMac();

	public boolean isManualCheckInRequired();

	public boolean isWindows();

}