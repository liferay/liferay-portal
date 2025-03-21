/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav;

import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.Status;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Fabio Pezzutto
 */
public abstract class BaseWebDAVStorageImpl implements WebDAVStorage {

	@Override
	public int copyCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite, long depth)
		throws WebDAVException {

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public int copySimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public int deleteResource(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public String getRootPath() {
		return _rootPath;
	}

	@Override
	public String getToken() {
		return _token;
	}

	@Override
	public boolean isAvailable(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		if (getResource(webDAVRequest) == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isSupportsClassTwo() {
		return false;
	}

	@Override
	public Status lockResource(
			WebDAVRequest webDAVRequest, String owner, long timeout)
		throws WebDAVException {

		return null;
	}

	@Override
	public Status makeCollection(WebDAVRequest webDAVRequest)
		throws WebDAVException {

		return new Status(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public int moveCollectionResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public int moveSimpleResource(
			WebDAVRequest webDAVRequest, Resource resource, String destination,
			boolean overwrite)
		throws WebDAVException {

		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public int putResource(WebDAVRequest webDAVRequest) throws WebDAVException {
		return HttpServletResponse.SC_FORBIDDEN;
	}

	@Override
	public Lock refreshResourceLock(
			WebDAVRequest webDAVRequest, String uuid, long timeout)
		throws WebDAVException {

		return null;
	}

	@Override
	public void setRootPath(String rootPath) {
		_rootPath = rootPath;
	}

	@Override
	public void setToken(String token) {
		_token = token;
	}

	@Override
	public boolean unlockResource(WebDAVRequest webDAVRequest, String token)
		throws WebDAVException {

		return false;
	}

	protected long getPlid(long groupId) {
		return LayoutLocalServiceUtil.getDefaultPlid(groupId);
	}

	protected boolean isAddGroupPermissions(long groupId) throws Exception {
		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (!group.isUser()) {
			return true;
		}

		return false;
	}

	private String _rootPath;
	private String _token;

}