/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 */
@Component(service = CTDisplayRenderer.class)
public class EmailAddressCTDisplayRenderer
	extends BaseCTDisplayRenderer<EmailAddress> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, EmailAddress emailAddress)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null, UsersAdminPortletKeys.USERS_ADMIN, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/common/edit_email_address.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"className", emailAddress.getClassName()
		).setParameter(
			"classPK", emailAddress.getClassPK()
		).setParameter(
			"primaryKey", emailAddress.getEmailAddressId()
		).buildString();
	}

	@Override
	public Class<EmailAddress> getModelClass() {
		return EmailAddress.class;
	}

	@Override
	public String getTitle(Locale locale, EmailAddress emailAddress) {
		return emailAddress.getAddress();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<EmailAddress> displayBuilder)
		throws PortalException {

		EmailAddress emailAddress = displayBuilder.getModel();

		displayBuilder.display(
			"name", emailAddress.getAddress()
		).display(
			"created-by",
			() -> {
				String userName = emailAddress.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", emailAddress.getCreateDate()
		).display(
			"last-modified", emailAddress.getModifiedDate()
		).display(
			"primary", emailAddress.isPrimary()
		).display(
			"type",
			() -> {
				ListType listType = emailAddress.getListType();

				if (listType != null) {
					return _language.get(
						displayBuilder.getLocale(), listType.getName());
				}

				return null;
			}
		).display(
			"address", emailAddress.getAddress()
		);
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}