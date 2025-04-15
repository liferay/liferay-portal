/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet.action;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Barbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/asset_categories_admin/delete_asset_vocabulary"
	},
	service = MVCActionCommand.class
)
public class DeleteAssetVocabularyMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long vocabularyId = ParamUtil.getLong(actionRequest, "vocabularyId");

		if (vocabularyId > 0) {
			_assetVocabularyService.deleteVocabulary(vocabularyId);
		}
	}

	@Reference
	private AssetVocabularyService _assetVocabularyService;

}