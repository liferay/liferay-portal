/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Ricardo Couso
 */
public class SiteAssetRenderer extends BaseAssetRenderer<Group> {

	public SiteAssetRenderer(Group group) {
		if (group.isSite() || group.isStagingGroup()) {
			_siteGroup = group;
		}
		else {
			throw new IllegalArgumentException(
				"Only site groups are supported");
		}
	}

	@Override
	public Group getAssetObject() {
		return _siteGroup;
	}

	@Override
	public String getClassName() {
		return Group.class.getName();
	}

	@Override
	public long getClassPK() {
		return _siteGroup.getPrimaryKey();
	}

	@Override
	public long getGroupId() {
		return _siteGroup.getGroupId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			return _siteGroup.getDescriptiveName(
				PortalUtil.getLocale(portletRequest));
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get summary for group " + _siteGroup.getGroupId(),
				portalException);
		}

		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		try {
			return _siteGroup.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get descriptive name for group " +
					_siteGroup.getGroupId(),
				portalException);
		}

		return null;
	}

	@Override
	public long getUserId() {
		return _siteGroup.getCreatorUserId();
	}

	@Override
	public String getUserName() {
		return null;
	}

	@Override
	public String getUuid() {
		return _siteGroup.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteAssetRenderer.class);

	private final Group _siteGroup;

}