/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Phone;
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
public class PhoneCTDisplayRenderer extends BaseCTDisplayRenderer<Phone> {

	@Override
	public String getEditURL(HttpServletRequest httpServletRequest, Phone phone)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null, UsersAdminPortletKeys.USERS_ADMIN, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/common/edit_phone_number.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"className", phone.getClassName()
		).setParameter(
			"classPK", phone.getClassPK()
		).setParameter(
			"primaryKey", phone.getPhoneId()
		).buildString();
	}

	@Override
	public Class<Phone> getModelClass() {
		return Phone.class;
	}

	@Override
	public String getTitle(Locale locale, Phone phone) {
		return phone.getNumber();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Phone> displayBuilder)
		throws PortalException {

		Phone phone = displayBuilder.getModel();

		displayBuilder.display(
			"name", phone.getNumber()
		).display(
			"created-by",
			() -> {
				String userName = phone.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", phone.getCreateDate()
		).display(
			"last-modified", phone.getModifiedDate()
		).display(
			"primary", phone.isPrimary()
		).display(
			"type",
			() -> {
				ListType listType = phone.getListType();

				if (listType != null) {
					return _language.get(
						displayBuilder.getLocale(), listType.getName());
				}

				return null;
			}
		).display(
			"number", phone.getNumber()
		);
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}