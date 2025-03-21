/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.product.content.contributor;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngine;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.inventory.constants.CommerceInventoryAvailabilityConstants;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.service.CPDefinitionInventoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 * @author Ivica Cardic
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.AVAILABILITY_NAME,
	service = CPContentContributor.class
)
public class AvailabilityCPContentContributor implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.AVAILABILITY_NAME;
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

		boolean displayAvailability =
			cpDefinitionInventoryEngine.isDisplayAvailability(
				commerceContext.getCPConfigurationListId(
					cpInstance.getGroupId()),
				cpInstance);

		if (displayAvailability) {
			long accountEntryId = AccountConstants.ACCOUNT_ENTRY_ID_ANY;

			AccountEntry accountEntry = commerceContext.getAccountEntry();

			if (accountEntry != null) {
				accountEntryId = accountEntry.getAccountEntryId();
			}

			String availabilityStatus =
				_commerceInventoryEngine.getAvailabilityStatus(
					cpInstance.getCompanyId(), accountEntryId,
					cpInstance.getGroupId(), commerceChannel.getGroupId(),
					cpDefinitionInventoryEngine.getMinStockQuantity(
						commerceContext.getCPConfigurationListId(
							cpInstance.getGroupId()),
						cpInstance),
					cpInstance.getSku(), StringPool.BLANK);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			jsonObject.put(
				CPContentContributorConstants.AVAILABILITY_NAME,
				_language.get(themeDisplay.getLocale(), availabilityStatus));

			String availabilityDisplayType = "success";

			if (!Objects.equals(
					availabilityStatus,
					CommerceInventoryAvailabilityConstants.AVAILABLE)) {

				availabilityDisplayType = "danger";
			}

			jsonObject.put(
				CPContentContributorConstants.AVAILABILITY_DISPLAY_TYPE,
				availabilityDisplayType);
		}

		return jsonObject;
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CPDefinitionInventoryEngineRegistry
		_cpDefinitionInventoryEngineRegistry;

	@Reference
	private CPDefinitionInventoryLocalService
		_cpDefinitionInventoryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}