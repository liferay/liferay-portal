/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/update_asset_list_entry_manual"
	},
	service = MVCActionCommand.class
)
public class UpdateAssetListEntryManualMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		AssetListEntry assetListEntry =
			_assetListEntryService.fetchAssetListEntry(assetListEntryId);

		if (assetListEntry == null) {
			return;
		}

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		UnicodeProperties typeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		_assetListEntryService.updateAssetListEntryTypeSettings(
			assetListEntryId, segmentsEntryId,
			typeSettingsUnicodeProperties.toString());
	}

	@Reference
	private AssetListEntryService _assetListEntryService;

}