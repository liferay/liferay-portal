/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryServiceUtil;
import com.liferay.commerce.address.CommerceAddressFormatter;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.frontend.model.StepModel;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipment.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 * @author Alec Sloan
 */
public class CommerceShipmentDisplayContext
	extends BaseCommerceShipmentDisplayContext<CommerceShipment> {

	public CommerceShipmentDisplayContext(
		ActionHelper actionHelper,
		CommerceAddressFormatter commerceAddressFormatter,
		CommerceAddressService commerceAddressService,
		CommerceChannelService commerceChannelService,
		CommerceOrderItemService commerceOrderItemService,
		CommerceOrderLocalService commerceOrderLocalService,
		CommerceShipmentItemService commerceShipmentItemService,
		CommerceShippingMethodService commerceShippingMethodService,
		CountryService countryService, HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission,
		RegionService regionService) {

		super(actionHelper, httpServletRequest, portletResourcePermission);

		_commerceAddressFormatter = commerceAddressFormatter;
		_commerceAddressService = commerceAddressService;
		_commerceChannelService = commerceChannelService;
		_commerceOrderItemService = commerceOrderItemService;
		_commerceOrderLocalService = commerceOrderLocalService;
		_commerceShipmentItemService = commerceShipmentItemService;
		_commerceShippingMethodService = commerceShippingMethodService;
		_countryService = countryService;
		_regionService = regionService;
	}

	public List<AccountEntry> getCommerceAccountsWithShippableOrders()
		throws PortalException {

		return TransformUtil.transformToList(
			ArrayUtil.unique(
				TransformUtil.transformToLongArray(
					getCommerceOrders(), CommerceOrder::getCommerceAccountId)),
			AccountEntryServiceUtil::getAccountEntry);
	}

	public String getCommerceAccountThumbnailURL(
		AccountEntry accountEntry, String pathImage) {

		StringBundler sb = new StringBundler(5);

		sb.append(pathImage);
		sb.append("/organization_logo?img_id=");
		sb.append(accountEntry.getLogoId());

		if (accountEntry.getLogoId() > 0) {
			sb.append("&t=");
			sb.append(
				WebServerServletTokenUtil.getToken(accountEntry.getLogoId()));
		}

		return sb.toString();
	}

	public String getCommerceChannelName() throws PortalException {
		CommerceShipment commerceShipment = getCommerceShipment();

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannelByOrderGroupId(
				commerceShipment.getGroupId());

		return commerceChannel.getName();
	}

	public List<CommerceChannel> getCommerceChannels() throws PortalException {
		return _commerceChannelService.search(cpRequestHelper.getCompanyId());
	}

	public List<CommerceOrder> getCommerceOrders() throws PortalException {
		SearchContext searchContext = _buildSearchContext();

		BaseModelSearchResult<CommerceOrder> baseModelSearchResult =
			_commerceOrderLocalService.searchCommerceOrders(searchContext);

		return baseModelSearchResult.getBaseModels();
	}

	public String getCommerceShippingMethodName(Locale locale) {
		try {
			CommerceShipment commerceShipment = getCommerceShipment();

			if (commerceShipment == null) {
				return StringPool.BLANK;
			}

			CommerceShippingMethod commerceShippingMethod =
				commerceShipment.getCommerceShippingMethod();

			if (commerceShippingMethod == null) {
				return StringPool.BLANK;
			}

			return commerceShippingMethod.getName(locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	public List<CommerceShippingMethod> getCommerceShippingMethods()
		throws PortalException {

		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment == null) {
			return Collections.emptyList();
		}

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(
				commerceShipment.getCommerceAddressId());

		return _commerceShippingMethodService.getCommerceShippingMethods(
			commerceShipment.getGroupId(), commerceAddress.getCountryId(),
			true);
	}

	public List<Country> getCountries() {
		return _countryService.getCompanyCountries(
			cpRequestHelper.getCompanyId(), true);
	}

	public String getDescriptiveShippingAddress(Locale locale)
		throws PortalException {

		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment.getCommerceAddressId() == 0) {
			return StringPool.BLANK;
		}

		CommerceAddress commerceAddress = getShippingAddress();

		if (commerceAddress == null) {
			return StringPool.BLANK;
		}

		return _commerceAddressFormatter.getDescriptiveAddress(
			commerceAddress, locale, true);
	}

	public String getFDSName() throws PortalException {
		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment.getStatus() >
				CommerceShipmentConstants.SHIPMENT_STATUS_READY_TO_BE_SHIPPED) {

			return CommerceShipmentFDSNames.SHIPPED_SHIPMENT_ITEMS;
		}

		return CommerceShipmentFDSNames.PROCESSING_SHIPMENT_ITEMS;
	}

	public List<HeaderActionModel> getHeaderActionModels()
		throws PortalException {

		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		CommerceShipment commerceShipment = getCommerceShipment();

		int currentShipmentStatus = commerceShipment.getStatus();

		if (currentShipmentStatus !=
				CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED) {

			int[] shipmentStatuses =
				CommerceShipmentConstants.SHIPMENT_STATUSES;

			int[] availableShipmentStatuses = new int[0];

			if (currentShipmentStatus ==
					CommerceShipmentConstants.
						SHIPMENT_STATUS_READY_TO_BE_SHIPPED) {

				availableShipmentStatuses = ArrayUtil.append(
					availableShipmentStatuses,
					CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING);
			}

			availableShipmentStatuses = ArrayUtil.append(
				availableShipmentStatuses,
				shipmentStatuses[currentShipmentStatus + 1]);

			for (int shipmentStatus : availableShipmentStatuses) {
				String label =
					CommerceShipmentConstants.getShipmentTransitionLabel(
						shipmentStatus);

				String buttonClass = "btn-primary";

				int availableStatusesLength = availableShipmentStatuses.length;

				if ((availableStatusesLength > 1) &&
					(shipmentStatus !=
						availableShipmentStatuses
							[availableStatusesLength - 1])) {

					buttonClass = "btn-secondary";
				}

				headerActionModels.add(
					new HeaderActionModel(
						buttonClass, null,
						PortletURLBuilder.create(
							PortalUtil.getControlPanelPortletURL(
								httpServletRequest,
								CommercePortletKeys.COMMERCE_SHIPMENT,
								PortletRequest.ACTION_PHASE)
						).setActionName(
							"/commerce_shipment/edit_commerce_shipment"
						).setCMD(
							"transition"
						).setRedirect(
							PortalUtil.getCurrentURL(httpServletRequest)
						).setParameter(
							"commerceShipmentId", getCommerceShipmentId()
						).setParameter(
							"transitionName", shipmentStatus
						).buildString(),
						null, label));
			}
		}

		return headerActionModels;
	}

	public String getNavigation() {
		return ParamUtil.getString(
			cpRequestHelper.getRequest(), "navigation", "all");
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setNavigation(
			getNavigation()
		).buildPortletURL();
	}

	public List<Region> getRegions(long countryId) {
		return _regionService.getRegions(countryId, true);
	}

	public List<DropdownItem> getShipmentItemBulkActions()
		throws PortalException {

		List<DropdownItem> dropdownItems = new ArrayList<>();

		CommerceShipment commerceShipment = getCommerceShipment();

		if (hasManageCommerceShipmentsPermission() &&
			(commerceShipment.getStatus() ==
				CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING)) {

			dropdownItems.add(new DropdownItem());
		}

		return dropdownItems;
	}

	public CreationMenu getShipmentItemCreationMenu()
		throws PortalException, WindowStateException {

		CreationMenu creationMenu = new CreationMenu();

		CommerceShipment commerceShipment = getCommerceShipment();

		if (hasManageCommerceShipmentsPermission() &&
			(commerceShipment.getStatus() ==
				CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING)) {

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.create(
							getPortletURL()
						).setMVCRenderCommandName(
							"/commerce_shipment/add_commerce_shipment_items"
						).setRedirect(
							PortalUtil.getCurrentURL(httpServletRequest)
						).setParameter(
							"commerceShipmentId",
							commerceShipment.getCommerceShipmentId()
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString());
					dropdownItem.setLabel(
						LanguageUtil.get(
							httpServletRequest,
							"add-products-to-this-shipment"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public List<StepModel> getShipmentSteps() throws PortalException {
		CommerceShipment commerceShipment = getCommerceShipment();

		List<StepModel> steps = new ArrayList<>();

		for (int shipmentStatus : CommerceShipmentConstants.SHIPMENT_STATUSES) {
			StepModel step = new StepModel();

			step.setId(String.valueOf(shipmentStatus));
			step.setLabel(
				LanguageUtil.get(
					httpServletRequest,
					CommerceShipmentConstants.getShipmentStatusLabel(
						shipmentStatus)));

			if ((commerceShipment.getStatus() == shipmentStatus) &&
				(shipmentStatus !=
					CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED)) {

				step.setState("active");
			}
			else if ((commerceShipment.getStatus() > shipmentStatus) ||
					 (commerceShipment.getStatus() ==
						 CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED)) {

				step.setState("completed");
			}
			else {
				step.setState("inactive");
			}

			steps.add(step);
		}

		return steps;
	}

	public CommerceAddress getShippingAddress() throws PortalException {
		CommerceShipment commerceShipment = getCommerceShipment();

		return _commerceAddressService.fetchCommerceAddress(
			commerceShipment.getCommerceAddressId());
	}

	public boolean hasMultipleShippingMethods() throws PortalException {
		long commerceShippingMethodId = 0;

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemService.getCommerceShipmentItems(
				getCommerceShipmentId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		for (CommerceShipmentItem commerceShipmentItem :
				commerceShipmentItems) {

			CommerceOrderItem commerceOrderItem =
				_commerceOrderItemService.getCommerceOrderItem(
					commerceShipmentItem.getCommerceOrderItemId());

			CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

			if ((commerceShippingMethodId != 0) &&
				(commerceShippingMethodId !=
					commerceOrder.getCommerceShippingMethodId())) {

				return true;
			}

			commerceShippingMethodId =
				commerceOrder.getCommerceShippingMethodId();
		}

		return false;
	}

	private SearchContext _buildSearchContext() throws PortalException {
		SearchContext searchContext = new SearchContext();

		int[] orderStatuses = {
			CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED
		};

		searchContext.setAttribute("orderStatuses", orderStatuses);

		searchContext.setAttribute(
			Field.STATUS, WorkflowConstants.STATUS_APPROVED);
		searchContext.setAttribute(
			"useSearchResultPermissionFilter", Boolean.FALSE);
		searchContext.setCompanyId(cpRequestHelper.getCompanyId());
		searchContext.setEnd(QueryUtil.ALL_POS);

		long[] commerceChannelGroupIds = TransformUtil.transformToLongArray(
			getCommerceChannels(), CommerceChannel::getGroupId);

		if ((commerceChannelGroupIds != null) &&
			(commerceChannelGroupIds.length > 0)) {

			searchContext.setGroupIds(commerceChannelGroupIds);
		}

		searchContext.setStart(QueryUtil.ALL_POS);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShipmentDisplayContext.class);

	private final CommerceAddressFormatter _commerceAddressFormatter;
	private final CommerceAddressService _commerceAddressService;
	private final CommerceChannelService _commerceChannelService;
	private final CommerceOrderItemService _commerceOrderItemService;
	private final CommerceOrderLocalService _commerceOrderLocalService;
	private final CommerceShipmentItemService _commerceShipmentItemService;
	private final CommerceShippingMethodService _commerceShippingMethodService;
	private final CountryService _countryService;
	private final RegionService _regionService;

}