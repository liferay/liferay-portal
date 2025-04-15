/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/add_asset_entry_selection"
	},
	service = MVCActionCommand.class
)
public class AddAssetEntrySelectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		long[] assetEntryIds = ParamUtil.getLongValues(
			actionRequest, "assetEntryIds");
		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		_assetListEntryService.addAssetEntrySelections(
			assetListEntryId, assetEntryIds, segmentsEntryId, serviceContext);
	}

	@Reference
	private AssetListEntryService _assetListEntryService;

}