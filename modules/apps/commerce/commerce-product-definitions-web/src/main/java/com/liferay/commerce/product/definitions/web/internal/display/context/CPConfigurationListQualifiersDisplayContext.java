/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.inventory.CPDefinitionInventoryEngineRegistry;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPConfigurationEntryService;
import com.liferay.commerce.product.service.CPConfigurationListRelService;
import com.liferay.commerce.product.service.CPConfigurationListService;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.service.CommerceAvailabilityEstimateService;
import com.liferay.commerce.stock.activity.CommerceLowStockActivityRegistry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danny Situ
 */
public class CPConfigurationListQualifiersDisplayContext
	extends CPConfigurationListDisplayContext {

	public CPConfigurationListQualifiersDisplayContext(
		CommerceAvailabilityEstimateService commerceAvailabilityEstimateService,
		CommerceCatalogService commerceCatalogService,
		CommerceChannelRelService commerceChannelRelService,
		CommerceLowStockActivityRegistry commerceLowStockActivityRegistry,
		CPConfigurationEntryService cpConfigurationEntryService,
		ModelResourcePermission<CPConfigurationList>
			cpConfigurationListModelResourcePermission,
		CPConfigurationListRelService cpConfigurationListRelService,
		CPConfigurationListService cpConfigurationListService,
		CPDefinitionInventoryEngineRegistry cpDefinitionInventoryEngineRegistry,
		CPDefinitionService cpDefinitionService,
		CPMeasurementUnitLocalService cpMeasurementUnitLocalService,
		CPTaxCategoryLocalService cpTaxCategoryLocalService,
		HttpServletRequest httpServletRequest, Portal portal) {

		super(
			commerceAvailabilityEstimateService, commerceCatalogService,
			commerceLowStockActivityRegistry, cpConfigurationEntryService,
			cpConfigurationListService, cpDefinitionInventoryEngineRegistry,
			cpDefinitionService, cpMeasurementUnitLocalService,
			cpTaxCategoryLocalService, httpServletRequest);

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_commerceChannelRelService = commerceChannelRelService;
		_cpConfigurationListModelResourcePermission =
			cpConfigurationListModelResourcePermission;
		_cpConfigurationListRelService = cpConfigurationListRelService;
		_portal = portal;
	}

	public String getActiveAccountEligibility() throws PortalException {
		long cpConfigurationListId = getCPConfigurationListId();

		long count =
			_cpConfigurationListRelService.getCPConfigurationListRelsCount(
				AccountEntry.class.getName(), cpConfigurationListId);

		if (count > 0) {
			return "accounts";
		}

		count = _cpConfigurationListRelService.getCPConfigurationListRelsCount(
			AccountGroup.class.getName(), cpConfigurationListId);

		if (count > 0) {
			return "accountGroups";
		}

		return "all";
	}

	public String getActiveChannelEligibility() throws PortalException {
		long commerceChannelRelsCount =
			_commerceChannelRelService.getCommerceChannelRelsCount(
				CPConfigurationList.class.getName(),
				getCPConfigurationListId());

		if (commerceChannelRelsCount > 0) {
			return "channels";
		}

		return "all";
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		int cpConfigurationListRelsCount =
			_cpConfigurationListRelService.getCPConfigurationListRelsCount(
				CommerceOrderType.class.getName(), getCPConfigurationListId());

		if (cpConfigurationListRelsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public CPConfigurationList getCPConfigurationList() throws PortalException {
		if (_cpConfigurationList != null) {
			return _cpConfigurationList;
		}

		long cpConfigurationListId = ParamUtil.getLong(
			cpRequestHelper.getRenderRequest(), "cpConfigurationListId");

		if (cpConfigurationListId > 0) {
			_cpConfigurationList =
				cpConfigurationListService.getCPConfigurationList(
					cpConfigurationListId);
		}

		return _cpConfigurationList;
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationListAccountFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest,
					AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/account_admin/edit_account_entry"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"accountEntryId", "{account.id}"
			).buildString(),
			false);
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationListAccountGroupFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, AccountPortletKeys.ACCOUNT_GROUPS_ADMIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/account_admin/edit_account_group"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"accountGroupId", "{accountGroup.id}"
			).buildString(),
			false);
	}

	public String getCPConfigurationListAccountGroupsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-catalog/v1.0",
			"/product-configuration-lists/", getCPConfigurationListId(),
			"/product-configuration-list-account-groups");
	}

	public String getCPConfigurationListAccountsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-catalog/v1.0",
			"/product-configuration-lists/", getCPConfigurationListId(),
			"/product-configuration-list-accounts");
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationListChannelFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceChannel.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_channels/edit_commerce_channel"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"commerceChannelId", "{channel.id}"
			).buildString(),
			false);
	}

	public String getCPConfigurationListChannelsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-catalog/v1.0",
			"/product-configuration-lists/", getCPConfigurationListId(),
			"/product-configuration-list-channels");
	}

	public List<FDSActionDropdownItem>
			getCPConfigurationListOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionDropdownItems(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					httpServletRequest, CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				cpRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	public String getCPConfigurationListOrderTypesAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-catalog/v1.0",
			"/product-configuration-lists/", getCPConfigurationListId(),
			"/product-configuration-list-order-types");
	}

	public PortletURL getPortletCPConfigurationListURL() {
		LiferayPortletResponse liferayPortletResponse =
			cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long cpConfigurationListId = ParamUtil.getLong(
			httpServletRequest, "cpConfigurationListId");

		if (cpConfigurationListId > 0) {
			portletURL.setParameter(
				"cpConfigurationListId", String.valueOf(cpConfigurationListId));
		}

		return portletURL;
	}

	public boolean hasPermission() throws PortalException {
		CPConfigurationList cpConfigurationList = getCPConfigurationList();

		return _cpConfigurationListModelResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(),
			cpConfigurationList.getCPConfigurationListId(), ActionKeys.UPDATE);
	}

	protected List<FDSActionDropdownItem> getFDSActionDropdownItems(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(httpServletRequest, "edit"), "get", null, null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "headless"));

		return fdsActionDropdownItems;
	}

	protected final CPRequestHelper cpRequestHelper;

	private final CommerceChannelRelService _commerceChannelRelService;
	private CPConfigurationList _cpConfigurationList;
	private final ModelResourcePermission<CPConfigurationList>
		_cpConfigurationListModelResourcePermission;
	private final CPConfigurationListRelService _cpConfigurationListRelService;
	private final Portal _portal;

}