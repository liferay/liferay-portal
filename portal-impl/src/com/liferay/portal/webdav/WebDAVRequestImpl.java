/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class WebDAVRequestImpl implements WebDAVRequest {

	public WebDAVRequestImpl(
			WebDAVStorage storage, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String userAgent,
			PermissionChecker permissionChecker)
		throws WebDAVException {

		_storage = storage;
		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_userAgent = userAgent;

		_lockUuid = WebDAVUtil.getLockUuid(httpServletRequest);

		String pathInfo = HttpComponentsUtil.fixPath(
			httpServletRequest.getPathInfo(), false, true);

		String strippedPathInfo = WebDAVUtil.stripManualCheckInRequiredPath(
			pathInfo);

		if (strippedPathInfo.length() != pathInfo.length()) {
			pathInfo = strippedPathInfo;

			_manualCheckInRequired = true;
		}
		else {
			_manualCheckInRequired = false;
		}

		_path = WebDAVUtil.stripOfficeExtension(pathInfo);

		_companyId = PortalUtil.getCompanyId(httpServletRequest);

		_groupId = WebDAVUtil.getGroupId(_companyId, _path);

		_permissionChecker = permissionChecker;

		_userId = GetterUtil.getLong(httpServletRequest.getRemoteUser());
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	@Override
	public String getLockUuid() {
		return _lockUuid;
	}

	@Override
	public String getPath() {
		return _path;
	}

	@Override
	public String[] getPathArray() {
		return WebDAVUtil.getPathArray(_path);
	}

	@Override
	public PermissionChecker getPermissionChecker() {
		return _permissionChecker;
	}

	@Override
	public String getRootPath() {
		return _storage.getRootPath();
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public WebDAVStorage getWebDAVStorage() {
		return _storage;
	}

	@Override
	public boolean isAppleDoubleRequest() {
		String name = WebDAVUtil.getResourceName(getPathArray());

		if (isMac() && name.startsWith(_APPLE_DOUBLE_PREFIX)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLitmus() {
		return _userAgent.contains("litmus");
	}

	@Override
	public boolean isMac() {
		return _userAgent.contains("WebDAVFS");
	}

	@Override
	public boolean isManualCheckInRequired() {
		return _manualCheckInRequired;
	}

	@Override
	public boolean isWindows() {
		return _userAgent.contains(
			"Microsoft Data Access Internet Publishing Provider");
	}

	private static final String _APPLE_DOUBLE_PREFIX = "._";

	private final long _companyId;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final String _lockUuid;
	private final boolean _manualCheckInRequired;
	private final String _path;
	private final PermissionChecker _permissionChecker;
	private final WebDAVStorage _storage;
	private final String _userAgent;
	private final long _userId;

}