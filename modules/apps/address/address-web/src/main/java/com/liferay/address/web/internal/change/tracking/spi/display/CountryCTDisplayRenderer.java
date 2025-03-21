/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.change.tracking.spi.display;

import com.liferay.address.web.internal.constants.AddressPortletKeys;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class CountryCTDisplayRenderer extends BaseCTDisplayRenderer<Country> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Country country)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null,
				AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/address/edit_country"
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"countryId", country.getCountryId()
		).buildString();
	}

	@Override
	public Class<Country> getModelClass() {
		return Country.class;
	}

	@Override
	public String getTitle(Locale locale, Country country)
		throws PortalException {

		return country.getTitle(locale);
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "country");
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Country> displayBuilder)
		throws PortalException {

		Country country = displayBuilder.getModel();

		displayBuilder.display(
			"key", country.getName()
		).display(
			"two-letter-iso-code", country.getA2()
		).display(
			"three-letter-iso-code", country.getA3()
		).display(
			"number", country.getNumber()
		).display(
			"priority", country.getPosition()
		).display(
			"country-calling-code", country.getIdd()
		).display(
			"shipping", country.isShippingAllowed()
		).display(
			"subject-to-vat", country.isSubjectToVAT()
		).display(
			"active", country.isActive()
		).display(
			"billing-allowed", country.isBillingAllowed()
		);
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}