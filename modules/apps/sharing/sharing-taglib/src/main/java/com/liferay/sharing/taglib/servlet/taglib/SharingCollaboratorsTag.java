/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.display.context.util.SharingJavaScriptFactory;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.taglib.internal.permission.util.SharingPermissionUtil;
import com.liferay.sharing.taglib.internal.servlet.ServletContextUtil;
import com.liferay.sharing.taglib.internal.servlet.SharingJavaScriptFactoryUtil;
import com.liferay.sharing.taglib.internal.util.CollaboratorsUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class SharingCollaboratorsTag extends BaseSharingTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		Map<String, Object> data = new HashMap<>();

		long classNameId = PortalUtil.getClassNameId(getClassName());

		HttpServletRequest parentHttpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)parentHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean canManageCollaborators = _canManageCollaborators(
			classNameId, getClassPK(), themeDisplay);

		if (canManageCollaborators) {
			SharingJavaScriptFactory sharingJavaScriptFactory =
				SharingJavaScriptFactoryUtil.getSharingJavaScriptFactory();

			sharingJavaScriptFactory.requestSharingJavaScript();

			data.put("canManageCollaborators", true);
		}

		data.put("classNameId", classNameId);
		data.put("classPK", getClassPK());

		ResourceURL collaboratorsResourceURL = PortletURLFactoryUtil.create(
			parentHttpServletRequest, SharingPortletKeys.SHARING,
			PortletRequest.RESOURCE_PHASE);

		collaboratorsResourceURL.setParameter("className", getClassName());
		collaboratorsResourceURL.setParameter(
			"classPK", String.valueOf(getClassPK()));

		collaboratorsResourceURL.setResourceID("/sharing/collaborators");

		data.put(
			"collaboratorsResourceURL", collaboratorsResourceURL.toString());

		data.put(
			"initialData",
			CollaboratorsUtil.getCollaboratorsJSONObject(
				classNameId, getClassPK(), themeDisplay));

		httpServletRequest.setAttribute(
			"liferay-sharing:collaborators:data", data);
	}

	private boolean _canManageCollaborators(
		long classNameId, long classPK, ThemeDisplay themeDisplay) {

		SharingPermission sharingPermission =
			SharingPermissionUtil.getSharingPermission();

		try {
			return sharingPermission.containsManageCollaboratorsPermission(
				themeDisplay.getPermissionChecker(), classNameId, classPK,
				themeDisplay.getScopeGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final String _PAGE = "/collaborators/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SharingCollaboratorsTag.class);

	private String _className;
	private long _classPK;

}