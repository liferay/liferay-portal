/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.taglib.servlet.taglib;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.display.context.util.SharingJavaScriptFactory;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.taglib.internal.permission.util.SharingPermissionUtil;
import com.liferay.sharing.taglib.internal.servlet.ServletContextUtil;
import com.liferay.sharing.taglib.internal.servlet.SharingJavaScriptFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Alejandro Tardín
 */
public class SharingButtonTag extends BaseSharingTag {

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
		SharingJavaScriptFactory sharingJavaScriptFactory =
			SharingJavaScriptFactoryUtil.getSharingJavaScriptFactory();

		long classNameId = PortalUtil.getClassNameId(getClassName());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (_containsSharePermission(classNameId, getClassPK(), themeDisplay)) {
			sharingJavaScriptFactory.requestSharingJavaScript();
		}

		try {
			httpServletRequest.setAttribute(
				"liferay-sharing:button:onClick",
				sharingJavaScriptFactory.createSharingOnClickMethod(
					_className, _classPK, httpServletRequest));
		}
		catch (PortalException portalException) {
			_log.error("Unable to set onclick method", portalException);
		}
	}

	private boolean _containsSharePermission(
		long classNameId, long classPK, ThemeDisplay themeDisplay) {

		SharingPermission sharingPermission =
			SharingPermissionUtil.getSharingPermission();

		try {
			return sharingPermission.containsSharePermission(
				themeDisplay.getPermissionChecker(), classNameId, classPK,
				themeDisplay.getScopeGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final String _PAGE = "/button/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SharingButtonTag.class);

	private String _className;
	private long _classPK;

}