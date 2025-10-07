/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.display.context.DepotAdminDLDisplayContext;
import com.liferay.depot.web.internal.display.context.DepotAdminDetailsDisplayContext;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.DynamicRenderRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-depot-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Depot",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/edit_depot_entry.jsp",
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_SETTINGS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DepotSettingsPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			renderRequest.setAttribute(
				DepotAdminWebKeys.DEPOT_ADMIN_DETAILS_DISPLAY_CONTEXT,
				new DepotAdminDetailsDisplayContext(
					_depotApplicationController, renderRequest));

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			DepotEntry depotEntry = _depotEntryService.getGroupDepotEntry(
				themeDisplay.getScopeGroupId());

			renderRequest.setAttribute(
				DepotAdminDLDisplayContext.class.getName(),
				new DepotAdminDLDisplayContext(
					depotEntry, _dlSizeLimitConfigurationProvider,
					_portal.getHttpServletRequest(renderRequest)));
			renderRequest.setAttribute(
				DepotAdminWebKeys.DEPOT_ENTRY, depotEntry);

			renderRequest.setAttribute(
				DepotAdminWebKeys.ITEM_SELECTOR, _itemSelector);
			renderRequest.setAttribute(
				DepotAdminWebKeys.SHOW_BREADCRUMB, Boolean.TRUE);

			super.doView(
				new DynamicRenderRequest(
					renderRequest,
					HashMapBuilder.put(
						"depotEntryId",
						new String[] {
							String.valueOf(depotEntry.getDepotEntryId())
						}
					).build()),
				renderResponse);
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private DepotApplicationController _depotApplicationController;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}