/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.frontend.data.set.util.CommerceOrderFDSUtil;
import com.liferay.commerce.order.content.web.internal.model.OrderItem;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.PLACED_ORDER_ITEMS,
	service = FDSActionProvider.class
)
public class PlacedCommerceOrderItemFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		OrderItem orderItem = (OrderItem)model;

		if (orderItem.getParentOrderItemId() > 0) {
			return Collections.emptyList();
		}

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			orderItem.getOrderId());

		return DropdownItemListBuilder.add(
			() -> _modelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceOrder,
				ActionKeys.VIEW),
			dropdownItem -> {
				CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
					orderItem.getCPInstanceId());

				if (cpInstance == null) {
					dropdownItem.setHref(StringPool.BLANK);
				}
				else {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					dropdownItem.setHref(
						_cpDefinitionHelper.getFriendlyURL(
							cpInstance.getCPDefinitionId(), themeDisplay));
				}

				dropdownItem.setLabel(
					_language.get(httpServletRequest, "view"));
			}
		).add(
			() -> _modelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), commerceOrder,
				ActionKeys.VIEW),
			dropdownItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.setHref(
					CommerceOrderFDSUtil.getViewShipmentURL(
						orderItem.getOrderItemId(), themeDisplay));

				dropdownItem.setLabel(
					_language.get(httpServletRequest, "shipments"));
				dropdownItem.setTarget("modal");
			}
		).build();
	}

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder> _modelResourcePermission;

}