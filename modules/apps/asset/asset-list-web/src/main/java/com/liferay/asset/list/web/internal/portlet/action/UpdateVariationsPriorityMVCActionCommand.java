/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/update_variations_priority"
	},
	service = MVCActionCommand.class
)
public class UpdateVariationsPriorityMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] variationsPriority = ParamUtil.getLongValues(
			actionRequest, "variationsPriority");

		_assetListEntrySegmentsEntryRelLocalService.updateVariationsPriority(
			variationsPriority);
	}

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

}