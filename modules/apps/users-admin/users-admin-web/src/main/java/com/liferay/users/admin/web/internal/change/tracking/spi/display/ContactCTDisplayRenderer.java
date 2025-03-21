/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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
public class ContactCTDisplayRenderer extends BaseCTDisplayRenderer<Contact> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Contact contact)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null, UsersAdminPortletKeys.USERS_ADMIN, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/common/edit_address.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"className", contact.getClassName()
		).setParameter(
			"classPK", contact.getClassPK()
		).setParameter(
			"primaryKey", contact.getContactId()
		).buildString();
	}

	@Override
	public Class<Contact> getModelClass() {
		return Contact.class;
	}

	@Override
	public String getTitle(Locale locale, Contact contact) {
		return _language.format(
			locale, "x-for-x",
			new String[] {
				_language.get(
					locale, "model.resource." + Contact.class.getName()),
				contact.getFullName()
			});
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}