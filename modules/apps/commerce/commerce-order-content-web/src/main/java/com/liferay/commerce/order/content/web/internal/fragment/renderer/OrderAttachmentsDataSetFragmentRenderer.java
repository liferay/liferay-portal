/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFragmentFDSNames;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tancredi Covioli
 */
@Component(service = FragmentRenderer.class)
public class OrderAttachmentsDataSetFragmentRenderer
	implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"order_attachments_data_set/dependencies" +
						"/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	@Override
	public String getIcon() {
		return "paperclip";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "order-attachments-data-set");
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
				PrintWriter printWriter = httpServletResponse.getWriter();

				printWriter.write(
					StringBundler.concat(
						"<div class=\"portlet-msg-info\">",
						_language.get(
							httpServletRequest,
							"the-order-attachments-data-set-component-will-" +
								"be-shown-here"),
						"</div>"));
			}

			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/order_attachments_data_set/page.jsp");

			httpServletRequest.setAttribute(
				"liferay-commerce:order-attachments-data-set:additionalProps",
				_getFDSAdditionalProps(commerceOrder, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-attachments-data-set:apiURL",
				_getAPIURL(commerceOrder));

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			httpServletRequest.setAttribute(
				"liferay-commerce:order-attachments-data-set:displayStyle",
				GetterUtil.getString(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfigurationJSONObject(fragmentRendererContext),
						fragmentEntryLink.getEditableValuesJSONObject(),
						fragmentRendererContext.getLocale(), "displayStyle")));

			httpServletRequest.setAttribute(
				"liferay-commerce:order-attachments-data-set:" +
					"fdsActionDropdownItems",
				_getFDSActionDropdownItems(commerceOrder, httpServletRequest));
			httpServletRequest.setAttribute(
				"liferay-commerce:order-attachments-data-set:name",
				_getName(commerceOrder));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private String _getAPIURL(CommerceOrder commerceOrder) {
		if (commerceOrder.isOpen()) {
			return StringBundler.concat(
				Portal.PATH_MODULE,
				"/headless-commerce-delivery-cart/v1.0/carts/",
				commerceOrder.getCommerceOrderId(), "/attachments");
		}

		return StringBundler.concat(
			Portal.PATH_MODULE,
			"/headless-commerce-delivery-order/v1.0/placed-orders/",
			commerceOrder.getCommerceOrderId(), "/attachments");
	}

	private Map<String, Object> _getDSRequestAdditionalProps(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		Map<Long, DSRequest> dsRequests = _getDSRequests(
			commerceOrder, httpServletRequest);

		if (dsRequests.isEmpty()) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"dsRequestStatuses", _getDSRequestStatuses(dsRequests)
		).put(
			"signDSRequestURLs",
			_getSignDSRequestURLs(commerceOrder, httpServletRequest)
		).build();
	}

	private String _getDSRequestDetailsURL(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String dsRequestDetailsURL = HttpComponentsUtil.addParameter(
			StringBundler.concat(
				themeDisplay.getPortalURL(), _portal.getPathModule(),
				"/commerce-order-attachment-signature/details"),
			"backURL", themeDisplay.getURLCurrent());

		return dsRequestDetailsURL + "&commerceOrderAttachmentId={id}";
	}

	private List<FDSActionDropdownItem> _getDSRequestFDSActionDropdownItems(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		Map<Long, DSRequest> dsRequests = _getDSRequests(
			commerceOrder, httpServletRequest);

		if (dsRequests.isEmpty()) {
			return Collections.emptyList();
		}

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		Map<String, String> signDSRequestURLs = _getSignDSRequestURLs(
			commerceOrder, httpServletRequest);

		if (!signDSRequestURLs.isEmpty()) {
			fdsActionDropdownItems.add(
				FDSActionDropdownItemBuilder.setHref(
					StringPool.POUND
				).setIcon(
					"pencil"
				).setLabel(
					_language.get(httpServletRequest, "sign")
				).build(
					"sign"
				));
		}

		fdsActionDropdownItems.add(
			FDSActionDropdownItemBuilder.putData(
				"dsRequestDetailsURL",
				_getDSRequestDetailsURL(httpServletRequest)
			).setHref(
				StringPool.POUND
			).setIcon(
				"list-ul"
			).setLabel(
				_language.get(httpServletRequest, "view-signature-details")
			).build(
				"view-signature-details"
			));

		return fdsActionDropdownItems;
	}

	private Map<String, String> _getDSRequestStatuses(
		Map<Long, DSRequest> dsRequests) {

		Map<String, String> dsRequestStatuses = new HashMap<>();

		for (Map.Entry<Long, DSRequest> entry : dsRequests.entrySet()) {
			DSRequest dsRequest = entry.getValue();

			dsRequestStatuses.put(
				String.valueOf(entry.getKey()), dsRequest.getStatus());
		}

		return dsRequestStatuses;
	}

	private Map<Long, DSRequest> _getDSRequests(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		String key =
			OrderAttachmentsDataSetFragmentRenderer.class.getName() +
				commerceOrder.getCommerceOrderId();

		Map<Long, DSRequest> dsRequests =
			(Map<Long, DSRequest>)httpServletRequest.getAttribute(key);

		if (dsRequests != null) {
			return dsRequests;
		}

		Map<Long, Long> fileEntryIds = new LinkedHashMap<>();

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			for (CommerceOrderAttachment commerceOrderAttachment :
					_commerceOrderAttachmentLocalService.
						getCommerceOrderAttachments(
							commerceOrder.getCommerceOrderId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

				if (!_commerceOrderAttachmentModelResourcePermission.contains(
						themeDisplay.getPermissionChecker(),
						commerceOrderAttachment, ActionKeys.VIEW)) {

					continue;
				}

				fileEntryIds.put(
					commerceOrderAttachment.getCommerceOrderAttachmentId(),
					commerceOrderAttachment.getFileEntryId());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyMap();
		}

		Map<Long, DSRequest> fileEntryDSRequests =
			_dsRequestManager.getLatestFileEntryDSRequests(
				commerceOrder.getCompanyId(),
				ArrayUtil.toLongArray(fileEntryIds.values()));

		dsRequests = new LinkedHashMap<>();

		for (Map.Entry<Long, Long> entry : fileEntryIds.entrySet()) {
			DSRequest dsRequest = fileEntryDSRequests.get(entry.getValue());

			if (dsRequest == null) {
				continue;
			}

			dsRequests.put(entry.getKey(), dsRequest);
		}

		httpServletRequest.setAttribute(key, dsRequests);

		return dsRequests;
	}

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					StringPool.POUND
				).setIcon(
					"download"
				).setLabel(
					_language.get(httpServletRequest, "download")
				).build(
					"download"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.POUND
				).setIcon(
					"trash"
				).setLabel(
					_language.get(httpServletRequest, "delete")
				).setPermissionKey(
					"delete"
				).build(
					"delete"
				));

		fdsActionDropdownItems.addAll(
			_getDSRequestFDSActionDropdownItems(
				commerceOrder, httpServletRequest));

		return fdsActionDropdownItems;
	}

	private Map<String, Object> _getFDSAdditionalProps(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"commerceOrderId", commerceOrder.getCommerceOrderId()
		).putAll(
			_getDSRequestAdditionalProps(commerceOrder, httpServletRequest)
		).build();
	}

	private String _getName(CommerceOrder commerceOrder) {
		if (commerceOrder.isOpen()) {
			return CommerceOrderFragmentFDSNames.PENDING_ORDER_ATTACHMENTS;
		}

		return CommerceOrderFragmentFDSNames.PLACED_ORDER_ATTACHMENTS;
	}

	private Map<String, String> _getSignDSRequestURLs(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		Map<String, String> signDSRequestURLs = new HashMap<>();

		Map<Long, DSRequest> dsRequests = _getDSRequests(
			commerceOrder, httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (Map.Entry<Long, DSRequest> entry : dsRequests.entrySet()) {
			DSRequest dsRequest = entry.getValue();

			if (!dsRequest.isSignatureRequired(themeDisplay.getUserId())) {
				continue;
			}

			try {
				signDSRequestURLs.put(
					String.valueOf(entry.getKey()),
					_dsRequestURLProvider.getSignModalURL(
						themeDisplay.getURLCurrent(),
						commerceOrder.getCompanyId(),
						dsRequest.getDSRequestId(),
						dsRequest.getSiteGroupId()));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return signDSRequestURLs;
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrderAttachmentsDataSetFragmentRenderer.class);

	@Reference
	private CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderAttachment)"
	)
	private ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private DSRequestURLProvider _dsRequestURLProvider;

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