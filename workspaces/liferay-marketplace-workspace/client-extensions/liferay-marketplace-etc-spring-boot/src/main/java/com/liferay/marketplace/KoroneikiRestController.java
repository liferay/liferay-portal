/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderItemResource;
import com.liferay.marketplace.service.KoroneikiService;
import com.liferay.marketplace.service.MarketplaceService;
import com.liferay.marketplace.util.MarketplaceUtil;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Contact;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ExternalLink;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductConsumption;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductPurchase;
import com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.ProductPurchaseView;
import com.liferay.osb.koroneiki.phloem.rest.client.pagination.Page;
import com.liferay.osb.koroneiki.phloem.rest.client.resource.v1_0.ContactResource;
import com.liferay.osb.koroneiki.phloem.rest.client.resource.v1_0.ProductPurchaseViewResource;
import com.liferay.osb.koroneiki.phloem.rest.client.resource.v1_0.ProductResource;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Keven Leone
 */
@RequestMapping("/koroneiki")
@RestController
public class KoroneikiRestController extends BaseRestController {

	@GetMapping("contact/by-email-address/{emailAddress}")
	public Contact getContactByEmailAddress(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("emailAddress") String emailAddress)
		throws Exception {

		MarketplaceUtil.checkPermission(jwt);

		ContactResource contactResource =
			_koroneikiService.getContactResource();

		return contactResource.getContactByEmailAddresEmailAddress(
			emailAddress);
	}

	@GetMapping("subscriptions/{orderId}")
	public String getSubscriptions(@PathVariable("orderId") long orderId)
		throws Exception {

		JSONArray jsonArray = new JSONArray();

		Order order = _marketplaceService.getOrder(orderId);

		OrderItemResource orderItemResource =
			_marketplaceService.getOrderItemResource();

		for (OrderItem orderItem :
				orderItemResource.getOrderIdOrderItemsPage(
					orderId,
					com.liferay.headless.commerce.admin.order.client.pagination.
						Pagination.of(1, 10)
				).getItems()) {

			ProductPurchaseViewResource productPurchaseViewResource =
				_koroneikiService.getProductPurchaseViewResource();

			ProductPurchaseView productPurchaseView =
				productPurchaseViewResource.
					getAccountAccountKeyProductProductKeyProductPurchaseView(
						order.getAccountExternalReferenceCode(),
						orderItem.getSkuExternalReferenceCode());

			ProductPurchase productPurchase = null;

			outerLoop:
			for (ProductPurchase currentProductPurchase :
					productPurchaseView.getProductPurchases()) {

				for (ExternalLink externalLink :
						currentProductPurchase.getExternalLinks()) {

					if (Objects.equals(
							externalLink.getEntityId(),
							String.valueOf(orderId))) {

						productPurchase = currentProductPurchase;

						break outerLoop;
					}
				}
			}

			if (productPurchase == null) {
				continue;
			}

			String endDateString = null;

			if (!productPurchase.getPerpetual()) {
				endDateString = ZonedDateTime.ofInstant(
					productPurchase.getEndDate(
					).toInstant(),
					ZoneOffset.UTC
				).format(
					DateTimeFormatter.ISO_INSTANT
				);
			}

			String name = _marketplaceService.getSkuOptionValue(
				"dxp-license-usage-type", orderItem.getOptions());

			if (name == null) {
				name = orderItem.getSkuExternalReferenceCode();
			}

			int provisionedCount = 0;

			for (ProductConsumption productConsumption :
					productPurchaseView.getProductConsumptions()) {

				Date endDate = productConsumption.getEndDate();

				if (Objects.equals(
						productConsumption.getProductPurchaseKey(),
						productPurchase.getKey()) &&
					(((endDate != null) && endDate.after(new Date())) ||
					 productPurchase.getPerpetual())) {

					provisionedCount++;
				}
			}

			Date startDate = productPurchase.getStartDate();

			if (productPurchase.getPerpetual()) {
				startDate = order.getCreateDate();
			}

			jsonArray.put(
				new JSONObject(
				).put(
					"endDate", endDateString
				).put(
					"name", name
				).put(
					"perpetual", productPurchase.getPerpetual()
				).put(
					"productPurchasedKey", productPurchase.getKey()
				).put(
					"provisionedCount", provisionedCount
				).put(
					"purchasedCount", orderItem.getQuantity()
				).put(
					"productVersion",
					_marketplaceService.getProductVersion(orderItem.getSkuId())
				).put(
					"startDate",
					ZonedDateTime.ofInstant(
						startDate.toInstant(), ZoneOffset.UTC
					).format(
						DateTimeFormatter.ISO_INSTANT
					)
				));
		}

		return jsonArray.toString();
	}

	@PostMapping("product/{productId}")
	public void postProduct(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable("productId") long productId)
		throws Exception {

		Product product = _marketplaceService.getProduct(productId);

		SkuResource skuResource = _marketplaceService.getSkuResource();

		for (Sku sku :
				skuResource.getProductIdSkusPage(
					product.getProductId(), Pagination.of(1, 10)
				).getItems()) {

			String dxpLicenseUsageType = _marketplaceService.getSkuOptionValue(
				"dxp-license-usage-type", sku.getSkuOptions());

			if ((dxpLicenseUsageType == null) ||
				sku.getExternalReferenceCode(
				).startsWith(
					"KOR-"
				)) {

				if (_log.isInfoEnabled()) {
					_log.info(
						"Skipping POST product for sku " + sku.toString());
				}

				continue;
			}

			ProductResource productResource =
				_koroneikiService.getProductResource();

			String productName = product.getName(
			).get(
				"en_US"
			);

			String name = productName + " - " + dxpLicenseUsageType;

			Page<com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Product>
				page = productResource.getProductsPage(
					"", "name eq '" + name + "'",
					com.liferay.osb.koroneiki.phloem.rest.client.pagination.
						Pagination.of(1, 1),
					"");

			com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.Product
				koroneikiProduct = page.fetchFirstItem();

			if (koroneikiProduct == null) {
				koroneikiProduct =
					new com.liferay.osb.koroneiki.phloem.rest.client.dto.v1_0.
						Product();

				koroneikiProduct.setName(name);
				koroneikiProduct.setProperties(
					HashMapBuilder.put(
						"display-group-name", productName
					).put(
						"display-name", name
					).put(
						"licenses", "true"
					).put(
						"type", "marketplace-app"
					).build());

				koroneikiProduct = productResource.postProduct(
					jwt.getClaim("username"), jwt.getClaim("sub"),
					koroneikiProduct);

				if (_log.isInfoEnabled()) {
					_log.info("Created product " + koroneikiProduct);
				}
			}

			sku.setExternalReferenceCode(koroneikiProduct::getKey);

			skuResource.patchSku(sku.getId(), sku);
		}
	}

	private static final Log _log = LogFactory.getLog(
		KoroneikiRestController.class);

	@Autowired
	private KoroneikiService _koroneikiService;

	@Autowired
	private MarketplaceService _marketplaceService;

}