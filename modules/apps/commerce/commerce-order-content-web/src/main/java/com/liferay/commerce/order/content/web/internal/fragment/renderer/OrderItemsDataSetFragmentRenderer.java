/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFragmentFDSNames;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
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
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.Collections;
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
public class OrderItemsDataSetFragmentRenderer implements FragmentRenderer {

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
					"order_items_data_set/dependencies/configuration.json"));

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
		return _language.get(locale, "order-items-data-set");
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

		CommerceOrder commerceOrder =
			CommerceOrderInfoItemUtil.getCommerceOrder(
				_commerceOrderService, httpServletRequest);

		if (commerceOrder == null) {
			if (_isEditMode(httpServletRequest)) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse);
			}

			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/order_items_data_set/page.jsp");

			String fdsName = commerceOrder.isOpen() ?
				CommerceOrderFragmentFDSNames.PENDING_ORDER_ITEMS :
					CommerceOrderFragmentFDSNames.PLACED_ORDER_ITEMS;

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:additionalProps",
				_getFDSAdditionalProps(
					commerceOrder.getCommerceOrderId(), httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:apiURL",
				_getAPIURL(commerceOrder.getCommerceOrderId(), fdsName));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:name", fdsName);

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			String displayStyle = _getConfigurationValue(
				fragmentRendererContext, fragmentEntryLink, "displayStyle");

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:displayStyle", displayStyle);

			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:fdsActionDropdownItems",
				_getFDSActionDropdownItems(fdsName, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:fdsBulkActionDropdownItems",
				_getFDSBulkActionDropdownItems(fdsName, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-data-set:propsTransformer",
				"{OrderDataSetPropsTransformer} from " +
					"commerce-order-content-web");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private String _getAPIURL(long commerceOrderId, String fdsName) {
		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDER_ITEMS)) {
			return StringBundler.concat(
				"/o/headless-commerce-delivery-cart/v1.0/carts/",
				commerceOrderId, "/items");
		}
		else if (fdsName.equals(
					CommerceOrderFragmentFDSNames.PLACED_ORDER_ITEMS)) {

			return StringBundler.concat(
				"/o/headless-commerce-delivery-order/v1.0/placed-orders/",
				commerceOrderId, "/placed-order-items");
		}

		return StringPool.BLANK;
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

		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDER_ITEMS)) {
			return Arrays.asList(
				new FDSActionDropdownItem(
					StringPool.BLANK, "view", "view",
					_language.get(httpServletRequest, "view"), null, null,
					"link"),
				new FDSActionDropdownItem(
					StringPool.BLANK, "pencil", "edit",
					_language.get(httpServletRequest, "edit"), null, null,
					"link"),
				new FDSActionDropdownItem(
					StringPool.BLANK, "trash", "delete",
					_language.get(httpServletRequest, "remove"), "delete", null,
					"link"));
		}
		else if (fdsName.equals(
					CommerceOrderFragmentFDSNames.PLACED_ORDER_ITEMS)) {

			return Arrays.asList(
				new FDSActionDropdownItem(
					StringPool.BLANK, "view", "view",
					_language.get(httpServletRequest, "view"), null, null,
					"link"),
				new FDSActionDropdownItem(
					StringPool.BLANK, "truck", "shipments",
					_language.get(httpServletRequest, "shipments"), null, null,
					"link"));
		}

		return Collections.emptyList();
	}

	private Map<String, Object> _getFDSAdditionalProps(
		long commerceOrderId, HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"commerceOrderId", commerceOrderId
		).put(
			"productURLSeparator",
			_cpFriendlyURL.getProductURLSeparator(
				_portal.getCompanyId(httpServletRequest))
		).put(
			"siteDefaultURL", _getSiteDefaultURL(httpServletRequest)
		).put(
			"viewShipmentsURL", _getViewShipmentsURL(httpServletRequest)
		).build();
	}

	private List<DropdownItem> _getFDSBulkActionDropdownItems(
		String fdsName, HttpServletRequest httpServletRequest) {

		if (fdsName.equals(CommerceOrderFragmentFDSNames.PENDING_ORDER_ITEMS)) {
			return Arrays.asList(
				new FDSActionDropdownItem(
					StringPool.BLANK, "trash", "delete",
					_language.get(httpServletRequest, "delete"), "delete", null,
					"async"));
		}

		return Collections.emptyList();
	}

	private String _getSiteDefaultURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Group group = layout.getGroup();

		return HtmlUtil.escape(
			group.getDisplayURL(themeDisplay, layout.isPrivateLayout()));
	}

	private String _getViewShipmentsURL(HttpServletRequest httpServletRequest) {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, CommercePortletKeys.COMMERCE_ORDER_CONTENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_order_content/view_commerce_order_item_shipments"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private void _printPortletMessageInfo(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			StringBundler sb = new StringBundler(3);

			sb.append("<div class=\"portlet-msg-info\">");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			sb.append(
				themeDisplay.translate(
					"the-order-items-data-set-component-will-be-shown-here"));

			sb.append("</div>");

			printWriter.write(sb.toString());
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrderItemsDataSetFragmentRenderer.class);

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

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