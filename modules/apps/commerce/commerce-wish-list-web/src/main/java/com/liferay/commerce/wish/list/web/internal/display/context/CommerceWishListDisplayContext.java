/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.display.context;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.commerce.wish.list.util.CommerceWishListHttpHelper;
import com.liferay.commerce.wish.list.util.comparator.CommerceWishListNameComparator;
import com.liferay.commerce.wish.list.web.internal.display.context.helper.CommerceWishListRequestHelper;
import com.liferay.commerce.wish.list.web.internal.util.CommerceWishListPortletUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
public class CommerceWishListDisplayContext {

	public CommerceWishListDisplayContext(
		CommerceProductPriceCalculation commerceProductPriceCalculation,
		CommerceProductViewPermission commerceProductViewPermission,
		CommerceWishListHttpHelper commerceWishListHttpHelper,
		CommerceWishListItemService commerceWishListItemService,
		CommerceWishListService commerceWishListService,
		CPDefinitionHelper cpDefinitionHelper,
		CPInstanceHelper cpInstanceHelper,
		HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission) {

		_commerceProductPriceCalculation = commerceProductPriceCalculation;
		_commerceProductViewPermission = commerceProductViewPermission;
		_commerceWishListHttpHelper = commerceWishListHttpHelper;
		_commerceWishListItemService = commerceWishListItemService;
		_commerceWishListService = commerceWishListService;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpInstanceHelper = cpInstanceHelper;
		_portletResourcePermission = portletResourcePermission;

		_commerceWishListRequestHelper = new CommerceWishListRequestHelper(
			httpServletRequest);
		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
	}

	public long getCommerceAccountId() throws PortalException {
		return CommerceUtil.getCommerceAccountId(
			_commerceWishListRequestHelper.getCommerceContext());
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceContext commerceContext =
			_commerceWishListRequestHelper.getCommerceContext();

		return commerceContext.getCommerceChannelId();
	}

	public String getCommerceCurrencyCode() throws PortalException {
		CommerceContext commerceContext =
			_commerceWishListRequestHelper.getCommerceContext();

		CommerceCurrency commerceCurrency =
			commerceContext.getCommerceCurrency();

		return commerceCurrency.getCode();
	}

	public long getCommerceOrderId() throws PortalException {
		CommerceContext commerceContext =
			_commerceWishListRequestHelper.getCommerceContext();

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder != null) {
			return commerceOrder.getCommerceOrderId();
		}

		return 0;
	}

	public CommerceWishList getCommerceWishList() throws PortalException {
		if (_commerceWishList != null) {
			return _commerceWishList;
		}

		HttpServletRequest httpServletRequest =
			_commerceWishListRequestHelper.getRequest();

		long commerceWishListId = ParamUtil.getLong(
			httpServletRequest, "commerceWishListId",
			_getDefaultCommerceWishListId());

		if (commerceWishListId > 0) {
			try {
				_commerceWishList =
					_commerceWishListService.getCommerceWishList(
						commerceWishListId);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}
		else if (_isContentPortlet()) {
			_commerceWishList =
				_commerceWishListHttpHelper.getCurrentCommerceWishList(
					httpServletRequest,
					_commerceWishListRequestHelper.
						getResponseHttpServletResponse());
		}

		return _commerceWishList;
	}

	public long getCommerceWishListId() throws PortalException {
		CommerceWishList commerceWishList = getCommerceWishList();

		if (commerceWishList == null) {
			return 0;
		}

		return commerceWishList.getCommerceWishListId();
	}

	public String getCommerceWishListItemDescription(
			CommerceWishListItem commerceWishListItem)
		throws PortalException {

		CPDefinition cpDefinition = commerceWishListItem.getCPDefinition();

		List<KeyValuePair> keyValuePairs = _cpInstanceHelper.getKeyValuePairs(
			cpDefinition.getCPDefinitionId(), commerceWishListItem.getJson(),
			_commerceWishListRequestHelper.getLocale());

		StringBundler sb = new StringBundler((keyValuePairs.size() * 2) - 1);

		for (Iterator<KeyValuePair> iterator = keyValuePairs.iterator();
			 iterator.hasNext();) {

			sb.append(iterator.next());

			if (iterator.hasNext()) {
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}

		return sb.toString();
	}

	public String getCommerceWishListItemPrice(
			CommerceWishListItem commerceWishListItem)
		throws PortalException {

		CPInstance cpInstance = commerceWishListItem.fetchCPInstance();

		if (cpInstance == null) {
			return StringPool.BLANK;
		}

		CommerceMoney commerceMoney =
			_commerceProductPriceCalculation.getFinalPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				_commerceWishListRequestHelper.getCommerceContext());

		if (commerceMoney.isEmpty()) {
			return StringPool.BLANK;
		}

		return commerceMoney.format(_commerceWishListRequestHelper.getLocale());
	}

	public SearchContainer<CommerceWishListItem>
			getCommerceWishListItemsSearchContainer()
		throws PortalException {

		if (_commerceWishListItemsSearchContainer != null) {
			return _commerceWishListItemsSearchContainer;
		}

		_commerceWishListItemsSearchContainer = new SearchContainer<>(
			_commerceWishListRequestHelper.getLiferayPortletRequest(),
			getPortletURL(), null, "the-wish-list-is-empty");

		_commerceWishListItemsSearchContainer.setOrderByCol(
			_getOrderByCol("item", "create-date"));
		_commerceWishListItemsSearchContainer.setOrderByComparator(
			CommerceWishListPortletUtil.
				getCommerceWishListItemOrderByComparator(
					_getOrderByCol("item", "create-date"),
					_getOrderByType("item", "desc")));
		_commerceWishListItemsSearchContainer.setOrderByType(
			_getOrderByType("item", "desc"));

		CommerceWishList commerceWishList = getCommerceWishList();

		if (commerceWishList == null) {
			return _commerceWishListItemsSearchContainer;
		}

		_commerceWishListItemsSearchContainer.setResultsAndTotal(
			() -> _commerceWishListItemService.getCommerceWishListItems(
				commerceWishList.getCommerceWishListId(),
				_commerceWishListItemsSearchContainer.getStart(),
				_commerceWishListItemsSearchContainer.getEnd(),
				_commerceWishListItemsSearchContainer.getOrderByComparator()),
			_commerceWishListItemService.getCommerceWishListItemsCount(
				commerceWishList.getCommerceWishListId()));

		return _commerceWishListItemsSearchContainer;
	}

	public CPCatalogEntry getCPCatalogEntry(long cpDefinitionId)
		throws PortalException {

		CommerceContext commerceContext =
			_commerceWishListRequestHelper.getCommerceContext();

		long commerceAccountId = CommerceUtil.getCommerceAccountId(
			commerceContext);
		long commerceChannelGroupId =
			commerceContext.getCommerceChannelGroupId();

		if (!_commerceProductViewPermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceAccountId,
				commerceChannelGroupId, cpDefinitionId)) {

			return null;
		}

		return _cpDefinitionHelper.getCPCatalogEntry(
			commerceAccountId, commerceChannelGroupId, cpDefinitionId,
			_commerceWishListRequestHelper.getLocale());
	}

	public String getCPDefinitionURL(
			long cpDefinitionId, ThemeDisplay themeDisplay)
		throws PortalException {

		return _cpDefinitionHelper.getFriendlyURL(cpDefinitionId, themeDisplay);
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_commerceWishListRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		long commerceWishListId = ParamUtil.getLong(
			_commerceWishListRequestHelper.getRequest(), "commerceWishListId");

		if (commerceWishListId > 0) {
			portletURL.setParameter(
				"commerceWishListId", String.valueOf(commerceWishListId));
		}

		return portletURL;
	}

	public String getRowURL(long commerceWishListId) {
		return PortletURLBuilder.createRenderURL(
			_commerceWishListRequestHelper.getLiferayPortletResponse()
		).setParameter(
			"commerceWishListId", commerceWishListId
		).buildString();
	}

	public SearchContainer<CommerceWishList> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_commerceWishListRequestHelper.getLiferayPortletRequest(),
			getPortletURL(), null, "no-wish-lists-were-found");

		_searchContainer.setOrderByCol(_getOrderByCol("wish", "name"));
		_searchContainer.setOrderByComparator(
			CommerceWishListPortletUtil.getCommerceWishListOrderByComparator(
				_getOrderByCol("wish", "name"),
				_getOrderByType("wish", "asc")));
		_searchContainer.setOrderByType(_getOrderByType("wish", "asc"));
		_searchContainer.setResultsAndTotal(
			() -> _commerceWishListService.getCommerceWishLists(
				_commerceWishListRequestHelper.getScopeGroupId(),
				_searchContainer.getStart(), _searchContainer.getEnd(),
				_searchContainer.getOrderByComparator()),
			_commerceWishListService.getCommerceWishListsCount(
				_commerceWishListRequestHelper.getScopeGroupId()));

		return _searchContainer;
	}

	public boolean isProductVisibleToAccount(long cpDefinitionId)
		throws PortalException {

		return _commerceProductViewPermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			CommerceUtil.getCommerceAccountId(
				_commerceWishListRequestHelper.getCommerceContext()),
			cpDefinitionId);
	}

	private long _getDefaultCommerceWishListId() throws PortalException {
		long defaultCommerceWishListId = 0;

		CommerceWishList commerceWishList =
			_commerceWishListService.fetchCommerceWishList(
				_commerceWishListRequestHelper.getScopeGroupId(), true,
				CommerceWishListNameComparator.getInstance(true));

		if (commerceWishList != null) {
			defaultCommerceWishListId =
				commerceWishList.getCommerceWishListId();
		}

		return defaultCommerceWishListId;
	}

	private String _getOrderByCol(String prefix, String defaultOrderByCol) {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_commerceWishListRequestHelper.getRequest(),
			_commerceWishListRequestHelper.getPortletId(),
			StringBundler.concat(
				_CLASS_NAME_COMMERCE_WISH_LIST, StringPool.DASH, prefix,
				"-order-by-col"),
			defaultOrderByCol);

		return _orderByCol;
	}

	private String _getOrderByType(String prefix, String defaultOrderByType) {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_commerceWishListRequestHelper.getRequest(),
			_commerceWishListRequestHelper.getPortletId(),
			StringBundler.concat(
				_CLASS_NAME_COMMERCE_WISH_LIST, StringPool.DASH, prefix,
				"-order-by-type"),
			defaultOrderByType);

		return _orderByType;
	}

	private boolean _isContentPortlet() {
		return CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT.equals(
			_commerceWishListRequestHelper.getPortletId());
	}

	private static final String _CLASS_NAME_COMMERCE_WISH_LIST =
		TextFormatter.format(
			CommerceWishList.class.getSimpleName(), TextFormatter.K);

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceWishListDisplayContext.class);

	private final CommerceProductPriceCalculation
		_commerceProductPriceCalculation;
	private final CommerceProductViewPermission _commerceProductViewPermission;
	private CommerceWishList _commerceWishList;
	private final CommerceWishListHttpHelper _commerceWishListHttpHelper;
	private final CommerceWishListItemService _commerceWishListItemService;
	private SearchContainer<CommerceWishListItem>
		_commerceWishListItemsSearchContainer;
	private final CommerceWishListRequestHelper _commerceWishListRequestHelper;
	private final CommerceWishListService _commerceWishListService;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final CPInstanceHelper _cpInstanceHelper;
	private String _orderByCol;
	private String _orderByType;
	private final PortalPreferences _portalPreferences;
	private final PortletResourcePermission _portletResourcePermission;
	private SearchContainer<CommerceWishList> _searchContainer;

}