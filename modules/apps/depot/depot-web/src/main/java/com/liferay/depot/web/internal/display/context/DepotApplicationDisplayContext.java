/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alicia García
 */
public class DepotApplicationDisplayContext {

	public DepotApplicationDisplayContext(
		HttpServletRequest httpServletRequest, Portal portal) {

		_portal = portal;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_portletResponse = (PortletResponse)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getDepotEntryId() {
		DepotEntry depotEntry = (DepotEntry)_portletRequest.getAttribute(
			DepotAdminWebKeys.DEPOT_ENTRY);

		return depotEntry.getDepotEntryId();
	}

	public String getMessage() throws PortletException {
		String viewGroupSelectorLink =
			"<a href=\"" + HtmlUtil.escape(_getViewGroupSelectorURL()) + "\">";

		if (Validator.isNotNull(_portletId)) {
			return LanguageUtil.format(
				_themeDisplay.getLocale(),
				"x-application-is-disabled-for-this-scope.-please-go-back-to-" +
					"selection",
				new Object[] {
					getPortletTitle(), viewGroupSelectorLink, "</a>"
				});
		}

		return LanguageUtil.format(
			_themeDisplay.getLocale(),
			"application-is-not-supported.-please-go-back-to-selection",
			new Object[] {viewGroupSelectorLink, "</a>"});
	}

	public String getPortletTitle() {
		return _portal.getPortletTitle(_portletId, _themeDisplay.getLocale());
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	private String _getViewGroupSelectorURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(
				getPortletURL(),
				_portal.getLiferayPortletResponse(_portletResponse))
		).setParameter(
			"groupType", "site"
		).setParameter(
			"showGroupSelector", true
		).buildString();
	}

	private final Portal _portal;
	private String _portletId;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}