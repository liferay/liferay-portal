/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.ContactServiceUtil;
import com.liferay.portal.kernel.service.OrganizationServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Samuel Trong Tran
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

		if (!portletName.equals(UsersAdminPortletKeys.MY_ACCOUNT)) {
			portletDisplay.setShowBackIcon(true);
			portletDisplay.setURLBack(getRedirect());

			String portletTitle = StringPool.BLANK;

			if (_className.equals(Organization.class.getName())) {
				Organization organization =
					OrganizationServiceUtil.getOrganization(_classPK);

				portletTitle = LanguageUtil.format(
					_httpServletRequest, "edit-x", organization.getName(),
					false);
			}
			else if (_className.equals(Contact.class.getName())) {
				Contact contact = ContactServiceUtil.getContact(_classPK);

				portletTitle = LanguageUtil.format(
					_httpServletRequest, "edit-user-x", contact.getFullName(),
					false);
			}

			portletDisplay.setURLBackTitle(portletTitle);

			_renderResponse.setTitle(portletTitle);
		}
	}

	private final String _className;
	private final long _classPK;
	private final HttpServletRequest _httpServletRequest;
	private final long _primaryKey;
	private final String _redirect;
	private final RenderResponse _renderResponse;
	private final String _sheetTitle;

}