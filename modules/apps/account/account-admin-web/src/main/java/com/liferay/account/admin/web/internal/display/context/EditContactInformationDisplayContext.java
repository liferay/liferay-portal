/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Danny Situ
 */
public class EditContactInformationDisplayContext {

	public EditContactInformationDisplayContext(
		String contactInfoTypeName, HttpServletRequest httpServletRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_className = ParamUtil.getString(httpServletRequest, "className");
		_classPK = ParamUtil.getLong(httpServletRequest, "classPK");
		_primaryKey = ParamUtil.getLong(httpServletRequest, "primaryKey");
		_redirect = PortalUtil.escapeRedirect(
			ParamUtil.getString(httpServletRequest, "redirect"));

		if (_primaryKey > 0) {
			_sheetTitle = LanguageUtil.get(
				httpServletRequest, "edit-" + contactInfoTypeName);
		}
		else {
			_sheetTitle = LanguageUtil.get(
				httpServletRequest, "add-" + contactInfoTypeName);
		}
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public long getPrimaryKey() {
		return _primaryKey;
	}

	public String getRedirect() {
		return _redirect;
	}

	public String getSheetTitle() {
		return _sheetTitle;
	}

	public void setPortletDisplay(
			PortletDisplay portletDisplay, String portletName)
		throws PortalException {

		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(getRedirect());

		AccountEntry accountEntry = AccountEntryServiceUtil.getAccountEntry(
			_classPK);

		_renderResponse.setTitle(
			LanguageUtil.format(
				_httpServletRequest, "edit-x", accountEntry.getName(), false));
	}

	private final String _className;
	private final long _classPK;
	private final HttpServletRequest _httpServletRequest;
	private final long _primaryKey;
	private final String _redirect;
	private final RenderResponse _renderResponse;
	private final String _sheetTitle;

}