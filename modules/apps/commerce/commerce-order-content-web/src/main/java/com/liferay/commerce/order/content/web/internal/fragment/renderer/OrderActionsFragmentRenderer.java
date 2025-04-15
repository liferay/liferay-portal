/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceReturn;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterType;
import com.liferay.commerce.order.importer.type.CommerceOrderImporterTypeRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.friendly.url.provider.FriendlyURLSeparatorProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Gianmarco Brunialti Masera
 * @author Michele Vigilante
 */
@Component(service = FragmentRenderer.class)
public class OrderActionsFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"order_actions/dependencies/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "order-actions");
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
			if (_isEditMode(httpServletRequest)) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse);
			}

			return;
		}

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
					"/fragment/renderer/order_actions/page.jsp");

			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:checkoutURL",
				String.valueOf(
					_commerceOrderHttpHelper.getCommerceCheckoutPortletURL(
						httpServletRequest)));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:commerceOrderId",
				commerceOrder.getCommerceOrderId());

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:dropdownItems",
				_getDropdownItems(
					commerceOrder, fragmentEntryLink.getEditableValues(),
					fragmentRendererContext, httpServletRequest));

			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:namespace",
				StringUtil.randomId() + StringPool.UNDERLINE);
			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:open", commerceOrder.isOpen());
			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:orderSummaryURL",
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest,
						CommercePortletKeys.COMMERCE_CHECKOUT,
						PortletProvider.Action.VIEW)
				).setParameter(
					"checkoutStepName",
					() -> {
						CommerceCheckoutStep commerceCheckoutStep =
							_commerceCheckoutStepRegistry.
								getCommerceCheckoutStep("order-summary");

						return commerceCheckoutStep.getName();
					}
				).setParameter(
					"commerceOrderUuid", commerceOrder.getUuid()
				).buildString());
			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:quickCheckoutEnabled",
				_isQuickCheckoutEnabled(commerceChannel));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-actions:reorderURL",
				CommerceOrderInfoItemUtil.getCommerceOrderFriendlyURL(
					_friendlyURLSeparatorProviderSnapshot.get(),
					httpServletRequest));

			if (FeatureFlagManagerUtil.isEnabled("LPD-10562") &&
				(commerceOrder.getOrderStatus() ==
					CommerceOrderConstants.ORDER_STATUS_COMPLETED)) {

				httpServletRequest.setAttribute(
					"liferay-commerce:order-actions:" +
						"returnableOrderItemsContextParams",
					_getReturnableOrderItemsContextParams(
						commerceChannel, httpServletRequest));
				httpServletRequest.setAttribute(
					"liferay-commerce:order-actions:" +
						"viewReturnableOrderItemsURL",
					_getViewReturnableOrderItemsURL(httpServletRequest));
			}

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<CommerceOrderImporterType> _getCommerceImporterTypes(
			CommerceOrder commerceOrder)
		throws PortalException {

		return _commerceOrderImporterTypeRegistry.getCommerceOrderImporterTypes(
			commerceOrder);
	}

	private List<DropdownItem> _getDropdownItems(
		CommerceOrder commerceOrder, String editableValues,
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		List<DropdownItem> dropdownItems = new ArrayList<>();

		String commerceOrderFriendlyURL =
			CommerceOrderInfoItemUtil.getCommerceOrderFriendlyURL(
				_friendlyURLSeparatorProviderSnapshot.get(),
				httpServletRequest);

		try {
			if (commerceOrder.isOpen()) {
				for (CommerceOrderImporterType commerceOrderImporterType :
						_getCommerceImporterTypes(commerceOrder)) {

					if (!GetterUtil.getBoolean(
							_fragmentEntryConfigurationParser.getFieldValue(
								getConfiguration(fragmentRendererContext),
								editableValues,
								fragmentRendererContext.getLocale(),
								StringUtil.removeSubstring(
									commerceOrderImporterType.getKey(),
									StringPool.DASH)),
							true)) {

						continue;
					}

					dropdownItems.add(
						DropdownItemBuilder.setHref(
							PortletURLBuilder.create(
								PortletURLFactoryUtil.create(
									httpServletRequest,
									CommercePortletKeys.
										COMMERCE_OPEN_ORDER_CONTENT,
									PortletRequest.RENDER_PHASE)
							).setMVCRenderCommandName(
								"/commerce_open_order_content" +
									"/view_commerce_order_importer_type"
							).setRedirect(
								commerceOrderFriendlyURL
							).setParameter(
								"commerceOrderId",
								commerceOrder.getCommerceOrderId()
							).setParameter(
								"commerceOrderImporterTypeKey",
								commerceOrderImporterType.getKey()
							).setParameter(
								"orderDetailURL", commerceOrderFriendlyURL
							).setWindowState(
								LiferayWindowState.POP_UP
							).buildString()
						).setLabel(
							_language.get(
								httpServletRequest,
								commerceOrderImporterType.getLabel(
									_portal.getLocale(httpServletRequest)))
						).setTarget(
							"modal"
						).build());
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if (GetterUtil.getBoolean(
				_fragmentEntryConfigurationParser.getFieldValue(
					getConfiguration(fragmentRendererContext), editableValues,
					fragmentRendererContext.getLocale(), "printOrder"),
				true)) {

			dropdownItems.add(
				DropdownItemBuilder.setHref(
					ResourceURLBuilder.createResourceURL(
						PortletURLFactoryUtil.create(
							httpServletRequest,
							CommercePortletKeys.COMMERCE_ORDER_CONTENT,
							PortletRequest.RESOURCE_PHASE)
					).setParameter(
						"commerceOrderId", commerceOrder.getCommerceOrderId()
					).setParameter(
						"orderDetailURL",
						commerceOrderFriendlyURL +
							commerceOrder.getCommerceOrderId()
					).setResourceID(
						"/commerce_order_content/export_commerce_order_report"
					).buildString()
				).setLabel(
					_language.get(httpServletRequest, "print")
				).build());
		}

		try {
			if (commerceOrder.isOpen() &&
				_hasModelPermission(commerceOrder, ActionKeys.DELETE)) {

				dropdownItems.add(
					DropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"confirmationMessage",
							_language.get(
								httpServletRequest,
								"are-you-sure-you-want-to-remove-all-items")
						).build()
					).setHref(
						PortletURLBuilder.create(
							PortletURLFactoryUtil.create(
								httpServletRequest,
								CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
								PortletRequest.ACTION_PHASE)
						).setActionName(
							"/commerce_open_order_content" +
								"/edit_commerce_order_item"
						).setCMD(
							Constants.RESET
						).setParameter(
							"commerceOrderId",
							commerceOrder.getCommerceOrderId()
						).setParameter(
							"orderDetailURL",
							commerceOrderFriendlyURL +
								commerceOrder.getCommerceOrderId()
						).buildString()
					).setLabel(
						_language.get(httpServletRequest, "remove-all-items")
					).setTarget(
						"submitWithConfirmation"
					).build());
				dropdownItems.add(
					DropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"confirmationMessage",
							_language.format(
								httpServletRequest,
								"are-you-sure-you-want-to-delete-order-x",
								String.valueOf(
									commerceOrder.getCommerceOrderId()),
								false)
						).build()
					).setHref(
						PortletURLBuilder.create(
							PortletURLFactoryUtil.create(
								httpServletRequest,
								CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
								PortletRequest.ACTION_PHASE)
						).setActionName(
							"/commerce_open_order_content/edit_commerce_order"
						).setCMD(
							Constants.DELETE
						).setParameter(
							"commerceOrderId",
							commerceOrder.getCommerceOrderId()
						).setParameter(
							"orderDetailURL",
							commerceOrderFriendlyURL +
								commerceOrder.getCommerceOrderId()
						).buildString()
					).setLabel(
						_language.get(httpServletRequest, "delete")
					).setTarget(
						"submitWithConfirmation"
					).build());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return dropdownItems;
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

	private boolean _hasModelPermission(
			CommerceOrder commerceOrder, String action)
		throws PortalException {

		return _commerceOrderModelResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(), commerceOrder,
			action);
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private boolean _isQuickCheckoutEnabled(CommerceChannel commerceChannel) {
		try {
			CommerceOrderCheckoutConfiguration
				commerceOrderCheckoutConfiguration =
					_configurationProvider.getConfiguration(
						CommerceOrderCheckoutConfiguration.class,
						new GroupServiceSettingsLocator(
							commerceChannel.getGroupId(),
							CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderCheckoutConfiguration.quickCheckoutEnabled();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return false;
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
					"the-order-actions-component-will-be-shown-here"));

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
		OrderActionsFragmentRenderer.class);

	private static final Snapshot<FriendlyURLSeparatorProvider>
		_friendlyURLSeparatorProviderSnapshot = new Snapshot<>(
			OrderActionsFragmentRenderer.class,
			FriendlyURLSeparatorProvider.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderImporterTypeRegistry
		_commerceOrderImporterTypeRegistry;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

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