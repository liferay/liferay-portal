/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
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
public class DepotAppCustomizationCTDisplayRenderer
	extends BaseCTDisplayRenderer<DepotAppCustomization> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DepotAppCustomization depotAppCustomization)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, null, DepotPortletKeys.DEPOT_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/depot/edit_depot_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"depotEntryId", depotAppCustomization.getDepotEntryId()
		).buildString();
	}

	@Override
	public Class<DepotAppCustomization> getModelClass() {
		return DepotAppCustomization.class;
	}

	@Override
	public String getTitle(
			Locale locale, DepotAppCustomization depotAppCustomization)
		throws PortalException {

		return _portal.getPortletTitle(
			depotAppCustomization.getPortletId(), locale);
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "applications");
	}

	@Override
	public boolean isHideable(DepotAppCustomization depotAppCustomization) {
		return true;
	}

	@Override
	protected void buildDisplay(
			DisplayBuilder<DepotAppCustomization> displayBuilder)
		throws PortalException {

		DepotAppCustomization depotAppCustomization = displayBuilder.getModel();

		displayBuilder.display(
			"asset-library-name",
			() -> {
				DepotEntry depotEntry = _depotEntryLocalService.getDepotEntry(
					depotAppCustomization.getDepotEntryId());

				Group group = depotEntry.getGroup();

				return group.getName(displayBuilder.getLocale());
			}
		).display(
			"enabled", depotAppCustomization.isEnabled()
		).display(
			"portlet-id", depotAppCustomization.getPortletId()
		);
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}