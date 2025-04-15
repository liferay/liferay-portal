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
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.AVAILABILITY_ESTIMATE_NAME,
	service = CPContentContributor.class
)
public class AvailabilityEstimateCPContentContributor
	implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.AVAILABILITY_ESTIMATE_NAME;
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

		boolean available = false;

		long accountEntryId = AccountConstants.ACCOUNT_ENTRY_ID_GUEST;

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			accountEntryId = accountEntry.getAccountEntryId();
		}

		BigDecimal stockQuantity = _commerceInventoryEngine.getStockQuantity(
			cpInstance.getCompanyId(), accountEntryId, cpInstance.getGroupId(),
			commerceChannel.getGroupId(), cpInstance.getSku(),
			StringPool.BLANK);

		CPDefinitionInventory cpDefinitionInventory =
			_cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpInstance.getCPDefinitionId());

		CPDefinitionInventoryEngine cpDefinitionInventoryEngine =
			_cpDefinitionInventoryEngineRegistry.getCPDefinitionInventoryEngine(
				cpDefinitionInventory);

		if (BigDecimalUtil.gt(
				stockQuantity,
				cpDefinitionInventoryEngine.getMinStockQuantity(
					commerceContext.getCPConfigurationListId(
						cpDefinitionInventory.getGroupId()),
					cpInstance))) {

			available = true;
		}

		if (!available) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			jsonObject.put(
				CPContentContributorConstants.AVAILABILITY_ESTIMATE_NAME,
				_getAvailabilityEstimateLabel(
					themeDisplay.getLocale(),
					cpDefinitionInventoryEngine.getAvailabilityEstimate(
						commerceContext.getCPConfigurationListId(
							cpInstance.getGroupId()),
						cpInstance, themeDisplay.getLocale())));
		}

		return jsonObject;
	}

	private String _getAvailabilityEstimateLabel(
		Locale locale, String availabilityEstimate) {

		if (Validator.isNull(availabilityEstimate)) {
			return StringPool.BLANK;
		}

		return _language.format(
			locale, "product-will-be-available-in-x", availabilityEstimate);
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