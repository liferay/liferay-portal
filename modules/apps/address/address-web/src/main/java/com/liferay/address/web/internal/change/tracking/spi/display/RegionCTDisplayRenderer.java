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
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class RegionCTDisplayRenderer extends BaseCTDisplayRenderer<Region> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Region region)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null,
				AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/address/edit_region"
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"countryId", region.getCountryId()
		).setParameter(
			"regionId", region.getRegionId()
		).buildString();
	}

	@Override
	public Class<Region> getModelClass() {
		return Region.class;
	}

	@Override
	public String getTitle(Locale locale, Region region)
		throws PortalException {

		return region.getTitle(LocaleUtil.toLanguageId(locale));
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "region");
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Region> displayBuilder)
		throws PortalException {

		Region region = displayBuilder.getModel();

		displayBuilder.display(
			"key", region.getName()
		).display(
			"priority", region.getPosition()
		).display(
			"region-code", region.getRegionCode()
		).display(
			"active", region.isActive()
		);
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}