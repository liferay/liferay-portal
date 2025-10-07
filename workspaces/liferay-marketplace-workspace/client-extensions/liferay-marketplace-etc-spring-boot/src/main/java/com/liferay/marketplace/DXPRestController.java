/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.marketplace.constants.MarketplaceConstants;
import com.liferay.marketplace.service.ConsoleService;
import com.liferay.marketplace.service.MarketplaceService;
import com.liferay.marketplace.util.MarketplaceUtil;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Keven Leone
 */
@CrossOrigin("*")
@RequestMapping("/dxp")
@RestController
public class DXPRestController extends BaseRestController {

	@GetMapping("project-usage")
	public String getProjectsUsage(
			@AuthenticationPrincipal Jwt jwt, @RequestParam String projectId)
		throws Exception {

		String emailAddress = String.valueOf(
			jwt.getClaims(
			).get(
				"username"
			));

		return _consoleService.getProjectUsage(emailAddress, projectId);
	}

	@PostMapping("provisioning/{orderId}")
	public void postProvisioning(
			@PathVariable("orderId") long orderId, @RequestBody String json)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Provisioning order " + orderId);
		}

		Order order = _checkoutOrder(orderId);

		_marketplaceService.deployCloudService(new JSONObject(json), order);
	}

	private Order _checkoutOrder(long orderId) throws Exception {
		Order order = _marketplaceService.getOrder(orderId);

		if (Objects.equals(
				order.getOrderStatus(),
				MarketplaceConstants.ORDER_STATUS_COMPLETED)) {

			return order;
		}

		Page<OrderItem> orderItemPage =
			_marketplaceService.getOrderItemResource(
			).getOrderIdOrderItemsPage(
				order.getId(), Pagination.of(1, 10)
			);

		Map<String, String> productSpecificationsMap =
			_marketplaceService.getProductSpecificationsMap(
				_marketplaceService.getSku(
					orderItemPage.fetchFirstItem(
					).getSkuId()
				).getProductId());

		if (Objects.equals(
				productSpecificationsMap.get("price-model"), "Free")) {

			Map<String, String> customFields =
				(Map<String, String>)order.getCustomFields();

			customFields.put(
				"cloud-provisioning",
				MarketplaceUtil.createCloudProvisioningJSONArray(
					orderItemPage
				).toString());

			_marketplaceService.updateOrder(
				null, orderId, MarketplaceConstants.ORDER_STATUS_PROCESSING);

			_marketplaceService.updateOrder(
				customFields, orderId,
				MarketplaceConstants.ORDER_STATUS_COMPLETED);

			order.setOrderStatus(
				() -> MarketplaceConstants.ORDER_STATUS_COMPLETED);
		}

		return order;
	}

	private static final Log _log = LogFactory.getLog(DXPRestController.class);

	@Autowired
	private ConsoleService _consoleService;

	@Autowired
	private MarketplaceService _marketplaceService;

}