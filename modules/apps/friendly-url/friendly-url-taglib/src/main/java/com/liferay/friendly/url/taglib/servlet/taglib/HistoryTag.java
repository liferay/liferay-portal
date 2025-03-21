/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.taglib.servlet.taglib;

import com.liferay.friendly.url.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Adolfo Pérez
 */
public class HistoryTag extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getElementId() {
		return _elementId;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isLocalizable() {
		return _localizable;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setElementId(String elementId) {
		_elementId = elementId;
	}

	public void setLocalizable(boolean localizable) {
		_localizable = localizable;
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
		_disabled = false;
		_elementId = null;
		_localizable = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute(
			"liferay-friendly-url:history:defaultLanguageId",
			_getDefaultLanguageId(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-friendly-url:history:disabled", isDisabled());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:history:elementId", getElementId());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:history:friendlyURLEntryURL",
			_getFriendlyURLEntryURL(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-friendly-url:history:localizable", isLocalizable());
	}

	private String _getDefaultLanguageId(
		HttpServletRequest httpServletRequest) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getGuestUser();

			return user.getLanguageId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return LanguageUtil.getLanguageId(LocaleUtil.getDefault());
		}
	}

	private String _getFriendlyURLEntryURL(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return StringBundler.concat(
			themeDisplay.getPortalURL(), PortalUtil.getPathContext(),
			Portal.PATH_MODULE, "/friendly-url/",
			_getGroupId(httpServletRequest), StringPool.SLASH,
			HtmlUtil.escapeURL(getClassName()), StringPool.SLASH, getClassPK());
	}

	private long _getGroupId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getSiteGroupId();
	}

	private static final String _PAGE = "/history/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(HistoryTag.class);

	private String _className;
	private long _classPK;
	private boolean _disabled;
	private String _elementId;
	private boolean _localizable = true;

}