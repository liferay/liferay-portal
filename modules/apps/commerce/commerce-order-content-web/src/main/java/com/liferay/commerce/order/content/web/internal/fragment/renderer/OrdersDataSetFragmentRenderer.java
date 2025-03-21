/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceReturn;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFragmentFDSNames;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.friendly.url.provider.FriendlyURLSeparatorProvider;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
@Component(service = FragmentRenderer.class)
public class OrdersDataSetFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", getClass());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"orders_data_set/dependencies/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject, resourceBundle);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getIcon() {
		return "catalog";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "orders-data-set");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPD-20379");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				themeDisplay.getScopeGroupId());

		if (commerceChannel == null) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/orders_data_set/page.jsp");

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			String displayStyle = _getConfigurationValue(
				fragmentRendererContext, fragmentEntryLink, "displayStyle");

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:displayStyle", displayStyle);

			String fdsName = _getConfigurationValue(
				fragmentRendererContext, fragmentEntryLink, "fdsName");

			String namespace = StringUtil.randomId() + StringPool.UNDERLINE;

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:additionalProps",
				_getFDSAdditionalProps(
					commerceChannel, fdsName, namespace, httpServletRequest));

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:apiURL",
				_getAPIURL(commerceChannel.getCommerceChannelId(), fdsName));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:fdsActionDropdownItems",
				_getFDSActionDropdownItems(fdsName, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:fdsBulkActionDropdownItems",
				_getFDSBulkActionDropdownItems(fdsName, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:fdsCreationMenu",
				_getFDSCreationMenu(fdsName, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:name", fdsName);
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:namespace", namespace);
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:orderTypes",
				_getCommerceOrderTypesJSONArray(
					commerceChannel.getCommerceChannelId(),
					httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:propsTransformer",
				"{OrderDataSetPropsTransformer} from " +
					"commerce-order-content-web");

			if (FeatureFlagManagerUtil.isEnabled("LPD-10562")) {
				httpServletRequest.setAttribute(
					"liferay-commerce:order-data-set:" +
						"returnableOrderItemsContextParams",
					_getReturnableOrderItemsContextParams(
						commerceChannel, httpServletRequest));
				httpServletRequest.setAttribute(
					"liferay-commerce:order-data-set:" +
						"viewReturnableOrderItemsURL",
					_getViewReturnableOrderItemsURL(httpServletRequest));
			}

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private String _getAPIURL(long commerceChannelId, String fdsName) {
		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS)) {
			return StringBundler.concat(
				"/o/headless-commerce-delivery-cart/v1.0/channels/",
				commerceChannelId, "/carts");
		}
		else if (fdsName.equals(CommerceOrderFragmentFDSNames.PLACED_ORDERS)) {
			return StringBundler.concat(
				"/o/headless-commerce-delivery-order/v1.0/channels/",
				commerceChannelId, "/placed-orders");
		}

		return StringPool.BLANK;
	}

	private AccountEntry _getCommerceAccount(
		HttpServletRequest httpServletRequest) {

		try {
			long groupId = _portal.getScopeGroupId(httpServletRequest);

			return _commerceAccountHelper.getCurrentAccountEntry(
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private JSONArray _getCommerceOrderTypesJSONArray(
			long commerceChannelId, HttpServletRequest httpServletRequest)
		throws PortalException {

		List<CommerceOrderType> commerceOrderTypes =
			_commerceOrderTypeLocalService.getCommerceOrderTypes(
				_portal.getCompanyId(httpServletRequest),
				CommerceChannel.class.getName(), commerceChannelId, true,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		JSONArray commerceOrderTypesJSONArray = _jsonFactory.createJSONArray();

		for (CommerceOrderType commerceOrderType : commerceOrderTypes) {
			JSONObject commerceOrderTypeJSONObject =
				_jsonFactory.createJSONObject();

			commerceOrderTypeJSONObject.put(
				"name_i18n",
				commerceOrderType.getName(_portal.getLocale(httpServletRequest))
			).put(
				"orderTypeId", commerceOrderType.getCommerceOrderTypeId()
			);

			commerceOrderTypesJSONArray.put(commerceOrderTypeJSONObject);
		}

		return commerceOrderTypesJSONArray;
	}

	private String _getConfigurationValue(
		FragmentRendererContext fragmentRendererContext,
		FragmentEntryLink fragmentEntryLink, String name) {

		return GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				getConfiguration(fragmentRendererContext),
				fragmentEntryLink.getEditableValues(),
				fragmentRendererContext.getLocale(), name));
	}

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems(
		String fdsName, HttpServletRequest httpServletRequest) {

		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS)) {
			return Arrays.asList(
				new FDSActionDropdownItem(
					StringPool.BLANK, "view", "view",
					_language.get(httpServletRequest, "view"), null, null,
					"link"),
				new FDSActionDropdownItem(
					StringPool.BLANK, "trash", "delete",
					_language.get(httpServletRequest, "delete"), "delete", null,
					"link"),
				new FDSActionDropdownItem(
					StringPool.BLANK, "pencil", "rename",
					_language.get(httpServletRequest, "rename"), null, null,
					"link"),
				new FDSActionDropdownItem(
					null, "shopping-cart", "place-order",
					_language.get(httpServletRequest, "place-order"), null,
					null, null));
		}
		else if (fdsName.equals(CommerceOrderFragmentFDSNames.PLACED_ORDERS)) {
			List<FDSActionDropdownItem> fdsActionDropdownItems =
				new ArrayList<>();

			String commerceOrderFriendlyURL =
				CommerceOrderInfoItemUtil.getCommerceOrderFriendlyURL(
					_friendlyURLSeparatorProviderSnapshot.get(),
					httpServletRequest);

			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					commerceOrderFriendlyURL + "{id}", "view", "view",
					_language.get(httpServletRequest, "view"), null, null,
					"link"));

			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					StringPool.BLANK, "pencil", "rename",
					_language.get(httpServletRequest, "rename"), null, null,
					"link"));
			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					StringPool.BLANK, "reset", "reorder",
					_language.get(httpServletRequest, "reorder"), null, null,
					"link"));

			if (FeatureFlagManagerUtil.isEnabled("LPD-10562")) {
				fdsActionDropdownItems.add(
					new FDSActionDropdownItem(
						StringPool.BLANK, "undo", "return",
						_language.get(httpServletRequest, "make-a-return"),
						null, null, "link"));
			}

			return fdsActionDropdownItems;
		}

		return Collections.emptyList();
	}

	private Map<String, Object> _getFDSAdditionalProps(
			CommerceChannel commerceChannel, String fdsName, String namespace,
			HttpServletRequest httpServletRequest)
		throws Exception {

		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS)) {
			return HashMapBuilder.<String, Object>put(
				"accountId",
				() -> {
					AccountEntry accountEntry = _getCommerceAccount(
						httpServletRequest);

					if (accountEntry == null) {
						return null;
					}

					return accountEntry.getAccountEntryId();
				}
			).put(
				"checkoutActionURL",
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						httpServletRequest,
						CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_open_order_content/checkout_open_order"
				).buildString()
			).put(
				"commerceChannelId", commerceChannel.getCommerceChannelId()
			).put(
				"currencyCode", commerceChannel.getCommerceCurrencyCode()
			).put(
				"orderDetailURL",
				_commerceOrderHttpHelper.getCommerceCartBaseURL(
					httpServletRequest)
			).build();
		}
		else if (fdsName.equals(CommerceOrderFragmentFDSNames.PLACED_ORDERS)) {
			return HashMapBuilder.<String, Object>put(
				"namespace", namespace
			).put(
				"orderDetailURL",
				_commerceOrderHttpHelper.getCommerceCartBaseURL(
					httpServletRequest)
			).build();
		}

		return Collections.emptyMap();
	}

	private List<DropdownItem> _getFDSBulkActionDropdownItems(
		String fdsName, HttpServletRequest httpServletRequest) {

		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS)) {
			return Arrays.asList(
				new FDSActionDropdownItem(
					StringPool.BLANK, "trash", "delete",
					_language.get(httpServletRequest, "delete"), "delete", null,
					"async"));
		}

		return Collections.emptyList();
	}

	private CreationMenu _getFDSCreationMenu(
		String fdsName, HttpServletRequest httpServletRequest) {

		if (!fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDERS) ||
			(_getCommerceAccount(httpServletRequest) == null)) {

			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("createCommerceCart");
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "add-order"));
				dropdownItem.setTarget("event");
			}
		).build();
	}

	private Map<String, Object> _getReturnableOrderItemsContextParams(
		CommerceChannel commerceChannel,
		HttpServletRequest httpServletRequest) {

		try {
			return HashMapBuilder.<String, Object>put(
				"channelGroupId", commerceChannel.getGroupId()
			).put(
				"channelId", commerceChannel.getCommerceChannelId()
			).put(
				"channelName", commerceChannel.getName()
			).put(
				"redirect",
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CommerceReturn.class.getName(),
						PortletProvider.Action.EDIT)
				).setMVCRenderCommandName(
					"/commerce_return_content/view_commerce_return"
				).setParameter(
					"commerceReturnId", 0
				).buildString()
			).build();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return new HashMap<>();
		}
	}

	private String _getViewReturnableOrderItemsURL(
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, CommercePortletKeys.COMMERCE_ORDER_CONTENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_order_content/view_returnable_commerce_order_items"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrdersDataSetFragmentRenderer.class);

	private static final Snapshot<FriendlyURLSeparatorProvider>
		_friendlyURLSeparatorProviderSnapshot = new Snapshot<>(
			OrdersDataSetFragmentRenderer.class,
			FriendlyURLSeparatorProvider.class);

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.content.web)"
	)
	private ServletContext _servletContext;

}