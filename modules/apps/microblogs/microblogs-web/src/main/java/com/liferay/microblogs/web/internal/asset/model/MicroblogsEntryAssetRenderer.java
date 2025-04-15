/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.asset.model;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.web.internal.util.WebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Matthew Kong
 */
public class MicroblogsEntryAssetRenderer
	extends BaseJSPAssetRenderer<MicroblogsEntry> {

	public MicroblogsEntryAssetRenderer(
		MicroblogsEntry entry,
		ModelResourcePermission<MicroblogsEntry>
			microblogsEntryModelResourcePermission) {

		_entry = entry;
		_microblogsEntryModelResourcePermission =
			microblogsEntryModelResourcePermission;
	}

	@Override
	public MicroblogsEntry getAssetObject() {
		return _entry;
	}

	@Override
	public String getClassName() {
		return MicroblogsEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _entry.getMicroblogsEntryId();
	}

	@Override
	public long getGroupId() {
		try {
			Group group = GroupLocalServiceUtil.getCompanyGroup(
				_entry.getCompanyId());

			return group.getGroupId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/microblogs/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _entry.getContent();
	}

	@Override
	public String getTitle(Locale locale) {
		return _entry.getContent();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		try {
			User user = UserLocalServiceUtil.getUser(_entry.getUserId());

			long portletPlid = PortalUtil.getPlidFromPortletId(
				user.getGroupId(), MicroblogsPortletKeys.MICROBLOGS);

			return PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					liferayPortletRequest, MicroblogsPortletKeys.MICROBLOGS,
					portletPlid, PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/microblogs/view.jsp"
			).setParameter(
				"parentMicroblogsEntryId",
				() -> {
					long microblogsEntryId = _entry.getMicroblogsEntryId();

					if (_entry.getParentMicroblogsEntryId() > 0) {
						microblogsEntryId = _entry.getParentMicroblogsEntryId();
					}

					return microblogsEntryId;
				}
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public long getUserId() {
		return _entry.getUserId();
	}

	@Override
	public String getUserName() {
		return _entry.getUserName();
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		try {
			return _microblogsEntryModelResourcePermission.contains(
				permissionChecker, _entry, ActionKeys.VIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(WebKeys.MICROBLOGS_ENTRY, _entry);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MicroblogsEntryAssetRenderer.class);

	private final MicroblogsEntry _entry;
	private final ModelResourcePermission<MicroblogsEntry>
		_microblogsEntryModelResourcePermission;

}