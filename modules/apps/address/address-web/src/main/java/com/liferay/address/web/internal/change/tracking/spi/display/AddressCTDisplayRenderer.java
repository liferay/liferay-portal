/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
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
 * @author Gislayne Vitorino
 */
@Component(service = CTDisplayRenderer.class)
public class AddressCTDisplayRenderer extends BaseCTDisplayRenderer<Address> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Address address)
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
			"className", address.getClassName()
		).setParameter(
			"classPK", address.getClassPK()
		).setParameter(
			"primaryKey", address.getAddressId()
		).buildString();
	}

	@Override
	public Class<Address> getModelClass() {
		return Address.class;
	}

	@Override
	public String getTitle(Locale locale, Address address)
		throws PortalException {

		return address.getName();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Address> displayBuilder)
		throws PortalException {

		Address address = displayBuilder.getModel();

		displayBuilder.display(
			"username", address.getUserName()
		).display(
			"userId", address.getUserId()
		).display(
			"create-date", address.getCreateDate()
		).display(
			"name", address.getName()
		).display(
			"description", address.getDescription()
		).display(
			"street1", address.getStreet1()
		).display(
			"street2", address.getStreet2()
		).display(
			"street3", address.getStreet3()
		).display(
			"city", address.getCity()
		).display(
			"zip", address.getZip()
		).display(
			"country",
			() -> {
				Country country = address.getCountry();

				return country.getName(displayBuilder.getLocale());
			}
		).display(
			"region",
			() -> {
				Region region = address.getRegion();

				return region.getName();
			}
		).display(
			"primary", address.isPrimary()
		).display(
			"mailing", address.isMailing()
		);
	}

	@Reference
	private Portal _portal;

}