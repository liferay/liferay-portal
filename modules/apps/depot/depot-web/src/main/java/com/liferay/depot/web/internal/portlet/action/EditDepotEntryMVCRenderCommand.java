/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.display.context.DepotAdminDLDisplayContext;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_SETTINGS,
		"mvc.command.name=/depot/edit_depot_entry"
	},
	service = MVCRenderCommand.class
)
public class EditDepotEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			DepotEntry depotEntry = _depotEntryService.getDepotEntry(
				ParamUtil.getLong(renderRequest, "depotEntryId"));

			renderRequest.setAttribute(
				DepotAdminDLDisplayContext.class.getName(),
				new DepotAdminDLDisplayContext(
					depotEntry, _dlSizeLimitConfigurationProvider,
					_portal.getHttpServletRequest(renderRequest)));
			renderRequest.setAttribute(
				DepotAdminWebKeys.DEPOT_ENTRY, depotEntry);

			renderRequest.setAttribute(
				DepotAdminWebKeys.ITEM_SELECTOR, _itemSelector);

			return "/edit_depot_entry.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}