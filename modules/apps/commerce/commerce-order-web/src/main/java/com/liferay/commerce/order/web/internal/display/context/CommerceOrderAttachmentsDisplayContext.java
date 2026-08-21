/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.commerce.order.web.internal.display.context.helper.CommerceOrderRequestHelper;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Tancredi Covioli
 */
public class CommerceOrderAttachmentsDisplayContext {

	public CommerceOrderAttachmentsDisplayContext(
		long commerceOrderId, HttpServletRequest httpServletRequest,
		Language language) {

		_commerceOrderId = commerceOrderId;
		_httpServletRequest = httpServletRequest;
		_language = language;

		_commerceOrderRequestHelper = new CommerceOrderRequestHelper(
			httpServletRequest);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"commerceOrderId", _commerceOrderId
		).build();
	}

	public String getAPIURL() {
		return _getBaseAPIURL();
	}

	public long getCommerceOrderId() {
		return _commerceOrderId;
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
						"commerceOrderId", _commerceOrderId
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
		return FDSActionDropdownItemList.of(
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
	}

	private String _getBaseAPIURL() {
		return StringBundler.concat(
			Portal.PATH_MODULE, "/headless-commerce-admin-order/v1.0/orders/",
			_commerceOrderId, "/attachments");
	}

	private String _getEditURL() {
		return PortletURLBuilder.createRenderURL(
			_commerceOrderRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order_attachment"
		).setParameter(
			"commerceOrderAttachmentId", "{id}"
		).setParameter(
			"commerceOrderId", _commerceOrderId
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final long _commerceOrderId;
	private final CommerceOrderRequestHelper _commerceOrderRequestHelper;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;

}