/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.product.content.contributor;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.BACK_ORDER_ALLOWED,
	service = CPContentContributor.class
)
public class BackOrderabilityCPContentContributor
	implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.BACK_ORDER_ALLOWED;
	}

	@Override
	public JSONObject getValue(
			CPInstance cpInstance, HttpServletRequest httpServletRequest)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (cpInstance == null) {
			return jsonObject;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_portal.getScopeGroupId(httpServletRequest));

		if (commerceChannel == null) {
			return jsonObject;
		}

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpInstance.getCPDefinitionId());

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpDefinitionInventory);

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		jsonObject.put(
			CPContentContributorConstants.BACK_ORDER_ALLOWED,
			cpDefinitionInventoryEngine.isBackOrderAllowed(
				commerceContext.getCPConfigurationListId(
					cpInstance.getGroupId()),
				cpInstance));

		return jsonObject;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}