/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.display.context;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.web.internal.display.context.helper.CPDefinitionVirtualSettingRequestHelper;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.util.Collections;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceVirtualOrderItemEditDisplayContext {

	public CommerceVirtualOrderItemEditDisplayContext(
			CommerceOrderService commerceOrderService,
			CommerceOrderItemService commerceOrderItemService,
			CommerceVirtualOrderItem commerceVirtualOrderItem,
			DLAppService dlAppService, ItemSelector itemSelector,
			RenderRequest renderRequest)
		throws PortalException {

		_commerceOrderService = commerceOrderService;
		_commerceOrderItemService = commerceOrderItemService;
		_commerceVirtualOrderItem = commerceVirtualOrderItem;
		_dlAppService = dlAppService;
		_itemSelector = itemSelector;

		long commerceOrderId = ParamUtil.getLong(
			renderRequest, "commerceOrderId");

		if (commerceOrderId > 0) {
			_commerceOrder = commerceOrderService.getCommerceOrder(
				commerceOrderId);
		}
		else {
			_commerceOrder = null;
		}

		_cpDefinitionVirtualSettingRequestHelper =
			new CPDefinitionVirtualSettingRequestHelper(renderRequest);
	}

	public int[] getActivationStatuses() {
		return VirtualCPTypeConstants.ACTIVATION_STATUSES;
	}

	public String getActivationStatusLabel(int status) {
		return CommerceOrderConstants.getOrderStatusLabel(status);
	}

	public CommerceOrder getCommerceOrder() {
		return _commerceOrder;
	}

	public long getCommerceOrderId() {
		if (_commerceOrder == null) {
			return 0;
		}

		return _commerceOrder.getCommerceOrderId();
	}

	public CommerceOrderItem getCommerceOrderItem() throws PortalException {
		if (_commerceOrderItem != null) {
			return _commerceOrderItem;
		}

		long commerceOrderItemId = ParamUtil.getLong(
			_cpDefinitionVirtualSettingRequestHelper.getRequest(),
			"commerceOrderItemId");

		if (commerceOrderItemId > 0) {
			_commerceOrderItem = _commerceOrderItemService.getCommerceOrderItem(
				commerceOrderItemId);
		}

		return _commerceOrderItem;
	}

	public PortletURL getCommerceOrderItemsPortletURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			_cpDefinitionVirtualSettingRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_open_order_content/edit_commerce_order"
		).setParameter(
			"commerceOrderId", getCommerceOrderId()
		).setParameter(
			"screenNavigationCategoryKey", "items"
		).buildPortletURL();
	}

	public CommerceVirtualOrderItem getCommerceVirtualOrderItem() {
		return _commerceVirtualOrderItem;
	}

	public String getDownloadFileEntryURL(long fileEntryId)
		throws PortalException {

		if (_commerceVirtualOrderItem == null) {
			return null;
		}

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		return DLURLHelperUtil.getDownloadURL(
			fileEntry, fileEntry.getLatestFileVersion(),
			_cpDefinitionVirtualSettingRequestHelper.getThemeDisplay(),
			StringPool.BLANK, true, true);
	}

	public String getFileEntryItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				_cpDefinitionVirtualSettingRequestHelper.getRequest());

		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				"uploadCommerceVirtualOrderItem", fileItemSelectorCriterion));
	}

	private final CommerceOrder _commerceOrder;
	private CommerceOrderItem _commerceOrderItem;
	private final CommerceOrderItemService _commerceOrderItemService;
	private final CommerceOrderService _commerceOrderService;
	private final CommerceVirtualOrderItem _commerceVirtualOrderItem;
	private final CPDefinitionVirtualSettingRequestHelper
		_cpDefinitionVirtualSettingRequestHelper;
	private final DLAppService _dlAppService;
	private final ItemSelector _itemSelector;

}