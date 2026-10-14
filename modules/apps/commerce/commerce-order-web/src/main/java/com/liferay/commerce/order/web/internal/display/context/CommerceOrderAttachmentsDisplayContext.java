/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderAttachment;
import com.liferay.commerce.order.web.internal.display.context.helper.CommerceOrderRequestHelper;
import com.liferay.commerce.order.web.internal.util.DSCommerceOrderAttachmentUtil;
import com.liferay.commerce.service.CommerceOrderAttachmentLocalService;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tancredi Covioli
 */
public class CommerceOrderAttachmentsDisplayContext {

	public CommerceOrderAttachmentsDisplayContext(
		CommerceOrder commerceOrder,
		CommerceOrderAttachmentLocalService commerceOrderAttachmentLocalService,
		ModelResourcePermission<CommerceOrderAttachment>
			commerceOrderAttachmentModelResourcePermission,
		ModelResourcePermission<CommerceOrder>
			commerceOrderModelResourcePermission,
		DSRequestManager dsRequestManager,
		HttpServletRequest httpServletRequest, Language language, Portal portal,
		PortletURLFactory portletURLFactory,
		UserLocalService userLocalService) {

		_commerceOrder = commerceOrder;
		_commerceOrderAttachmentLocalService =
			commerceOrderAttachmentLocalService;
		_commerceOrderAttachmentModelResourcePermission =
			commerceOrderAttachmentModelResourcePermission;
		_commerceOrderModelResourcePermission =
			commerceOrderModelResourcePermission;
		_dsRequestManager = dsRequestManager;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
		_portletURLFactory = portletURLFactory;
		_userLocalService = userLocalService;

		_commerceOrderRequestHelper = new CommerceOrderRequestHelper(
			httpServletRequest);
	}

	public String getAPIURL() {
		return _getBaseAPIURL();
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"commerceOrderId", _commerceOrder.getCommerceOrderId()
		).putAll(
			_getDSRequestAdditionalProps()
		).build();
	}

	public long getCommerceOrderId() {
		return _commerceOrder.getCommerceOrderId();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_commerceOrderRequestHelper.getLiferayPortletResponse()
					).setMVCRenderCommandName(
						"/commerce_order/edit_commerce_order_attachment"
					).setParameter(
						"commerceOrderId", _commerceOrder.getCommerceOrderId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "add-attachment"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					_getEditURL()
				).setIcon(
					"pencil"
				).setLabel(
					_language.get(_httpServletRequest, "edit")
				).setPermissionKey(
					"update"
				).setTarget(
					"sidePanel"
				).build(
					"edit"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.POUND
				).setIcon(
					"download"
				).setLabel(
					_language.get(_httpServletRequest, "download")
				).build(
					"download"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.POUND
				).setIcon(
					"trash"
				).setLabel(
					_language.get(_httpServletRequest, "delete")
				).setPermissionKey(
					"delete"
				).build(
					"delete"
				));

		fdsActionDropdownItems.addAll(_getDSRequestFDSActionDropdownItems());

		return fdsActionDropdownItems;
	}

	private Map<String, Object> _getAddDSRequestModalProps() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_commerceOrderModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), _commerceOrder,
				ActionKeys.UPDATE)) {

			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"accountUsers",
			JSONUtil.toJSONArray(
				DSCommerceOrderAttachmentUtil.getAccountUsers(_commerceOrder),
				DSCommerceOrderAttachmentUtil::getUserJSONObject)
		).put(
			"allowedFileExtensions",
			DigitalSignatureConstants.ALLOWED_FILE_EXTENSIONS
		).put(
			"buyer",
			() -> {
				User user = _userLocalService.fetchUser(
					_commerceOrder.getUserId());

				if ((user == null) ||
					(user.getType() != UserConstants.TYPE_REGULAR) ||
					!user.isActive()) {

					return null;
				}

				return DSCommerceOrderAttachmentUtil.getUserJSONObject(user);
			}
		).put(
			"commerceOrderId", _commerceOrder.getCommerceOrderId()
		).put(
			"getDSRequestRecipientUsersURL",
			ResourceURLBuilder.createResourceURL(
				_portletURLFactory.create(
					_httpServletRequest, CommercePortletKeys.COMMERCE_ORDER,
					PortletRequest.RESOURCE_PHASE)
			).setResourceID(
				"/commerce_order/get_ds_request_recipient_users"
			).buildString()
		).put(
			"portletNamespace",
			_portal.getPortletNamespace(CommercePortletKeys.COMMERCE_ORDER)
		).build();
	}

	private String _getBaseAPIURL() {
		return StringBundler.concat(
			Portal.PATH_MODULE, "/headless-commerce-admin-order/v1.0/orders/",
			_commerceOrder.getCommerceOrderId(), "/attachments");
	}

	private Map<String, Object> _getDSRequestAdditionalProps() {
		if (!DSCommerceOrderAttachmentUtil.isEnabled(_commerceOrder)) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Object>put(
			"addDSRequestModalProps", this::_getAddDSRequestModalProps
		).put(
			"dsRequestStatuses", _getDSRequestStatuses()
		).put(
			"replaceableCommerceOrderAttachmentIds",
			TransformUtil.transform(
				_getDSRequests().entrySet(),
				entry -> {
					DSRequest dsRequest = entry.getValue();

					if (!dsRequest.isReplaceable()) {
						return null;
					}

					return String.valueOf(entry.getKey());
				})
		).build();
	}

	private List<FDSActionDropdownItem> _getDSRequestFDSActionDropdownItems() {
		if (!DSCommerceOrderAttachmentUtil.isEnabled(_commerceOrder)) {
			return Collections.emptyList();
		}

		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.putData(
				"addDSRequestURL",
				_getResourceURL("/commerce_order/add_ds_request")
			).setHref(
				StringPool.POUND
			).setIcon(
				"signature"
			).setLabel(
				_language.get(_httpServletRequest, "request-signature")
			).setPermissionKey(
				"update"
			).build(
				"request-signature"
			),
			FDSActionDropdownItemBuilder.putData(
				"resendDSRequestURL",
				_getResourceURL("/commerce_order/resend_ds_request")
			).setHref(
				StringPool.POUND
			).setIcon(
				"envelope-closed"
			).setLabel(
				_language.get(_httpServletRequest, "resend")
			).setPermissionKey(
				"update"
			).build(
				"resend"
			),
			FDSActionDropdownItemBuilder.putData(
				"voidDSRequestURL",
				_getResourceURL("/commerce_order/void_ds_request")
			).setHref(
				StringPool.POUND
			).setIcon(
				"times-circle"
			).setLabel(
				_language.get(_httpServletRequest, "void")
			).setPermissionKey(
				"update"
			).build(
				"void"
			));
	}

	private Map<String, String> _getDSRequestStatuses() {
		Map<String, String> dsRequestStatuses = new HashMap<>();

		for (Map.Entry<Long, DSRequest> entry : _getDSRequests().entrySet()) {
			DSRequest dsRequest = entry.getValue();

			dsRequestStatuses.put(
				String.valueOf(entry.getKey()), dsRequest.getStatus());
		}

		return dsRequestStatuses;
	}

	private Map<Long, DSRequest> _getDSRequests() {
		if (_dsRequests != null) {
			return _dsRequests;
		}

		Map<Long, Long> fileEntryIds = new LinkedHashMap<>();

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			for (CommerceOrderAttachment commerceOrderAttachment :
					_commerceOrderAttachmentLocalService.
						getCommerceOrderAttachments(
							_commerceOrder.getCommerceOrderId(),
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
				_commerceOrder.getCompanyId(),
				ArrayUtil.toLongArray(fileEntryIds.values()));

		_dsRequests = new LinkedHashMap<>();

		for (Map.Entry<Long, Long> entry : fileEntryIds.entrySet()) {
			DSRequest dsRequest = fileEntryDSRequests.get(entry.getValue());

			if (dsRequest == null) {
				continue;
			}

			_dsRequests.put(entry.getKey(), dsRequest);
		}

		return _dsRequests;
	}

	private String _getEditURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order_attachment"
		).setParameter(
			"commerceOrderAttachmentId", "{id}"
		).setParameter(
			"commerceOrderId", _commerceOrder.getCommerceOrderId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private String _getResourceURL(String resourceID) {
		return ResourceURLBuilder.createResourceURL(
			_portletURLFactory.create(
				_httpServletRequest, CommercePortletKeys.COMMERCE_ORDER,
				PortletRequest.RESOURCE_PHASE)
		).setParameter(
			"commerceOrderAttachmentId", "{id}"
		).setResourceID(
			resourceID
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderAttachmentsDisplayContext.class);

	private final CommerceOrder _commerceOrder;
	private final CommerceOrderAttachmentLocalService
		_commerceOrderAttachmentLocalService;
	private final ModelResourcePermission<CommerceOrderAttachment>
		_commerceOrderAttachmentModelResourcePermission;
	private final ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;
	private final CommerceOrderRequestHelper _commerceOrderRequestHelper;
	private final DSRequestManager _dsRequestManager;
	private Map<Long, DSRequest> _dsRequests;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final PortletURLFactory _portletURLFactory;
	private final UserLocalService _userLocalService;

}