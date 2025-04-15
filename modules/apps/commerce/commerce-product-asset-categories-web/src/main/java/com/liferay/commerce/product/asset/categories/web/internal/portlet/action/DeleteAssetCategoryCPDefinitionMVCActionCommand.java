/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.portlet.action;

import com.liferay.commerce.product.asset.categories.web.internal.constants.CommerceProductAssetCategoriesPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionService;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceProductAssetCategoriesPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/commerce_product_asset_categories/delete_asset_category_cp_definition"
	},
	service = MVCActionCommand.class
)
public class DeleteAssetCategoryCPDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		long categoryId = ParamUtil.getLong(actionRequest, "categoryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinition.class.getName(), actionRequest);

		_cpDefinitionService.deleteAssetCategoryCPDefinition(
			cpDefinitionId, categoryId, serviceContext);
	}

	@Reference
	private CPDefinitionService _cpDefinitionService;

}