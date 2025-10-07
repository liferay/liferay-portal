/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.BillingAddress;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.marketplace.constants.MarketplaceConstants;
import com.liferay.marketplace.service.KoroneikiService;
import com.liferay.marketplace.service.MarketplaceService;
import com.liferay.marketplace.util.MarketplaceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.math.BigDecimal;

import java.net.URL;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * @author Keven Leone
 */
@RequestMapping("/marketplace")
@RestController
public class MarketplaceRestController extends BaseRestController {

	@GetMapping("orders/export")
	public ResponseEntity<StreamingResponseBody> getOrdersExport(
			@RequestParam(defaultValue = "", name = "filters", required = false)
				String filterString)
		throws Exception {

		StreamingResponseBody streamingResponseBody = outputStream -> {
			try (CSVPrinter csvPrinter = new CSVPrinter(
					new BufferedWriter(new OutputStreamWriter(outputStream)),
					CSVFormat.DEFAULT.builder(
					).setHeader(
						"Account ERC", "Account Name", "Create Date",
						"Creator Email", "Order ID", "Order Type",
						"Product Name", "Total"
					).build())) {

				OrderResource orderResource =
					_marketplaceService.getOrderResource();

				for (int i = 1;; i++) {
					Page<Order> page = orderResource.getOrdersPage(
						"", filterString, Pagination.of(i, 200), "");

					for (Order order : page.getItems()) {
						String orderItemName = "";

						for (OrderItem orderItem : order.getOrderItems()) {
							orderItemName = orderItem.getName(
							).get(
								"en_US"
							);

							break;
						}

						com.liferay.headless.commerce.admin.order.client.dto.
							v1_0.Account account = order.getAccount();

						csvPrinter.printRecord(
							account.getExternalReferenceCode(),
							account.getName(), order.getCreateDate(),
							order.getCreatorEmailAddress(), order.getId(),
							order.getOrderTypeExternalReferenceCode(),
							orderItemName, order.getTotalFormatted());
					}

					if (i >= page.getLastPage()) {
						break;
					}
				}

				csvPrinter.flush();
			}
			catch (Exception exception) {
				throw new IOException(exception);
			}
		};

		return ResponseEntity.ok(
		).header(
			HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv"
		).contentType(
			MediaType.TEXT_PLAIN
		).body(
			streamingResponseBody
		);
	}

	@PostMapping("product/purchase")
	public void postProductPurchase(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("POST product purchase " + json);
		}

		JSONObject jsonObject = new JSONObject(json);

		JSONObject commerceOrderJSONObject = jsonObject.getJSONObject(
			"commerceOrder");

		String paymentMethod = commerceOrderJSONObject.getString(
			"paymentMethod");
		int paymentStatus = commerceOrderJSONObject.getInt("paymentStatus");

		Order order = _marketplaceService.getOrder(
			commerceOrderJSONObject.getLong("id"));

		if ((Objects.equals(
				paymentMethod,
				MarketplaceConstants.ORDER_PAYMENT_METHOD_MONEY_ORDER) &&
			 (paymentStatus ==
				 MarketplaceConstants.ORDER_PAYMENT_STATUS_PENDING)) ||
			(Objects.equals(
				paymentMethod,
				MarketplaceConstants.ORDER_PAYMENT_METHOD_PAYPAL) &&
			 (paymentStatus ==
				 MarketplaceConstants.ORDER_PAYMENT_STATUS_COMPLETED))) {

			_sendOrderPurchasedNotification(order);
		}

		if ((paymentStatus !=
				MarketplaceConstants.ORDER_PAYMENT_STATUS_COMPLETED) &&
			(paymentStatus !=
				MarketplaceConstants.ORDER_PAYMENT_STATUS_NOT_REQUIRED)) {

			if (_log.isInfoEnabled()) {
				_log.info(
					"Skipping POST product purchase for order " +
						commerceOrderJSONObject.getLong("id") +
							" because payment status is not completed");
			}

			return;
		}

		_marketplaceService.updateOrder(
			null, order.getId(), MarketplaceConstants.ORDER_STATUS_PROCESSING);

		Page<OrderItem> orderItemPage =
			_marketplaceService.getOrderItemResource(
			).getOrderIdOrderItemsPage(
				order.getId(), Pagination.of(1, 10)
			);

		if (Objects.equals(
				order.getOrderTypeExternalReferenceCode(),
				"CLIENT_EXTENSION") ||
			Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "CLOUDAPP")) {

			_setUpCloudProductPurchase(order, orderItemPage);
		}

		if (Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "COMPOSITE_APP") ||
			Objects.equals(
				order.getOrderTypeExternalReferenceCode(),
				"LOW_CODE_CONFIGURATION") ||
			Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "OTHER")) {

			_marketplaceService.updateOrder(
				null, order.getId(),
				MarketplaceConstants.ORDER_STATUS_COMPLETED);
		}

		if (Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "DXPAPP")) {

			_setUpDxpProductPurchase(jwt, order, orderItemPage);
		}
	}

	@PostMapping("product/submit")
	public void postProductSubmit(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("POST product submit " + json);
		}

		JSONObject jsonObject = new JSONObject(json);

		JSONObject modelCPDefinitionJSONObject = jsonObject.getJSONObject(
			"modelCPDefinition");

		Product product = _marketplaceService.getProduct(
			modelCPDefinitionJSONObject.getLong("CProductId"));

		_marketplaceService.postNotificationQueueEntry(
			null, "MARKETPLACE-PRODUCT-SUBMIT-TEMPLATE",
			new HashMapBuilder<String, Object>().put(
				"[%CPDEFINITION_NAME%]",
				product.getName(
				).get(
					modelCPDefinitionJSONObject.getString("defaultLanguageId")
				)
			).put(
				"[%CPDEFINITION_THUMBNAIL%]",
				new URL(
					"http://" + lxcDXPMainDomain + product.getThumbnail()
				).toString()
			).put(
				"[%CPDEFINITION_DEVELOPER_NAME%]",
				_marketplaceService.getCatalog(
					product.getCatalogId()
				).getName()
			).put(
				"[%CPDEFINITION_URL%]",
				new URL(
					StringBundler.concat(
						lxcDXPServerProtocol, "://", lxcDXPMainDomain,
						"/web/marketplace/administrator-dashboard#/apps/",
						modelCPDefinitionJSONObject.getLong("CProductId"))
				).toString()
			).put(
				"[%CPDEFINITION_CREATEDATE%]",
				ZonedDateTime.ofInstant(
					product.getCreateDate(
					).toInstant(),
					ZoneOffset.UTC
				).format(
					DateTimeFormatter.ofPattern("MMMM d, yyyy")
				)
			).put(
				"[%CPDEFINITION_ID%]",
				String.valueOf(
					modelCPDefinitionJSONObject.getLong("CPDefinitionId"))
			).build());
	}

	@PostMapping("/tax-calculate/{orderId}")
	public void postTaxCalculate(@PathVariable long orderId) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("POST tax calculate for order " + orderId);
		}

		Order order = _marketplaceService.getOrder(orderId);

		BillingAddress billingAddress = _marketplaceService.getBillingAddress(
			orderId);

		if (billingAddress == null) {
			return;
		}

		OrderResource orderResource = _marketplaceService.getOrderResource();

		com.liferay.headless.commerce.admin.order.client.dto.v1_0.Account
			account = order.getAccount();

		BigDecimal subtotalAmount = BigDecimal.valueOf(
			order.getSubtotalAmount());

		BigDecimal taxAmount = BigDecimal.ZERO;

		BigDecimal total = subtotalAmount.add(taxAmount);

		if ((Objects.equals(account.getType(), _ACCOUNT_TYPE_BUSINESS) &&
			 _europeanCountriesISOCode.contains(
				 billingAddress.getCountryISOCode())) ||
			(Objects.equals(account.getType(), _ACCOUNT_TYPE_PERSON) &&
			 Objects.equals(billingAddress.getCountryISOCode(), "IE"))) {

			taxAmount = subtotalAmount.multiply(
				BigDecimal.valueOf(_MARKETPLACE_TAX_PERCENTAGE));

			total = subtotalAmount.add(taxAmount);
		}

		BigDecimal finalTaxAmount = taxAmount;
		BigDecimal finalTotal = total;

		orderResource.patchOrder(
			orderId,
			new Order() {
				{
					setTaxAmount(() -> finalTaxAmount);
					setTotal(() -> finalTotal);
				}
			});
	}

	private void _sendOrderPurchasedNotification(Order order) throws Exception {
		OrderItem[] orderItems = order.getOrderItems();

		OrderItem orderItem = orderItems[0];

		if (orderItem == null) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Sending purchased order notification for order " +
					order.getId());
		}

		com.liferay.headless.commerce.admin.order.client.dto.v1_0.Account
			account = order.getAccount();

		BillingAddress billingAddress = _marketplaceService.getBillingAddress(
			order.getId());

		Product product = _marketplaceService.getProductBySkuId(
			orderItem.getSkuId());

		Catalog catalog = _marketplaceService.getCatalog(
			product.getCatalogId());

		Map<String, String> productSpecificationsMap =
			_marketplaceService.getProductSpecificationsMap(
				product.getProductId());

		_marketplaceService.postNotificationQueueEntry(
			null, "MARKETPLACE-ORDER-PURCHASED-NOTIFICATION",
			new HashMapBuilder<String, String>().put(
				"[%ACCOUNT_ID%]", String.valueOf(account.getId())
			).put(
				"[%ACCOUNT_NAME%]", account.getName()
			).put(
				"[%APP_NAME%]",
				product.getName(
				).get(
					"en_US"
				)
			).put(
				"[%APP_TYPE%]",
				productSpecificationsMap.get(
					"type"
				).replace(
					"-", " "
				)
			).put(
				"[%BILLING_ADDRESS_FORMATTED%]",
				String.join(
					", ", billingAddress.getStreet1(), billingAddress.getCity(),
					billingAddress.getRegionISOCode(),
					billingAddress.getCountryISOCode())
			).put(
				"[%BILLING_ADDRESS_NAME%]", billingAddress.getName()
			).put(
				"[%BILLING_ADDRESS_PHONE%]", billingAddress.getPhoneNumber()
			).put(
				"[%CATALOG_NAME%]", catalog.getName()
			).put(
				"[%EMAIL_ADDRESS%]", order.getCreatorEmailAddress()
			).put(
				"[%LICENSE_TYPE%]", productSpecificationsMap.get("license-type")
			).put(
				"[%NET_PRICE_FORMATTED%]", order.getSubtotalFormatted()
			).put(
				"[%ORDER_DATE%]",
				ZonedDateTime.ofInstant(
					order.getCreateDate(
					).toInstant(),
					ZoneOffset.UTC
				).format(
					DateTimeFormatter.ofPattern("MMMM d, yyyy")
				)
			).put(
				"[%ORDER_ID%]", String.valueOf(order.getId())
			).put(
				"[%ORDER_PAYMENT_METHOD%]",
				MarketplaceConstants.getOrderPaymentMethodLabel(
					order.getPaymentMethod())
			).put(
				"[%ORDER_STATUS%]",
				MarketplaceConstants.getOrderStatusLabel(order.getOrderStatus())
			).put(
				"[%PAYMENT_TERMS%]", order.getPaymentTermDescription()
			).put(
				"[%PRODUCT_THUMBNAIL%]",
				new URL(
					StringBundler.concat(
						lxcDXPServerProtocol, "://", lxcDXPMainDomain,
						product.getThumbnail())
				).toString(
				).replaceAll(
					"(?<=accounts/)-?\\d+(?=/images)", "-1"
				)
			).put(
				"[%TOTAL_FORMATTED%]", order.getTotalFormatted()
			).put(
				"[%VAT_FORMATTED%]", order.getTaxAmountFormatted()
			).put(
				"[%VAT_NUMBER%]", account.getTaxId()
			).build());
	}

	private void _setUpCloudProductPurchase(
			Order order, Page<OrderItem> orderItemPage)
		throws Exception {

		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		customFields.put(
			"cloud-provisioning",
			MarketplaceUtil.createCloudProvisioningJSONArray(
				orderItemPage
			).toString());

		_marketplaceService.updateOrder(
			customFields, order.getId(),
			MarketplaceConstants.ORDER_STATUS_COMPLETED);
	}

	private void _setUpDxpProductPurchase(
			Jwt jwt, Order order, Page<OrderItem> orderItemPage)
		throws Exception {

		Map<String, String> productSpecificationsMap =
			_marketplaceService.getProductSpecificationsMap(
				_marketplaceService.getSku(
					orderItemPage.fetchFirstItem(
					).getSkuId()
				).getProductId());

		if (Objects.equals(
				productSpecificationsMap.get("price-model"), "Free")) {

			_marketplaceService.updateOrder(
				null, order.getId(),
				MarketplaceConstants.ORDER_STATUS_COMPLETED);

			return;
		}

		AccountResource accountResource =
			_marketplaceService.getAccountResource();

		Account account = accountResource.getAccount(order.getAccountId());

		if (!account.getExternalReferenceCode(
			).startsWith(
				"KOR-"
			)) {

			account.setExternalReferenceCode(
				() -> _koroneikiService.postKoroneikiAccount(
					account, jwt
				).getKey());

			accountResource.patchAccount(account.getId(), account);
		}

		try {
			for (OrderItem orderItem : orderItemPage.getItems()) {
				_koroneikiService.postAccountAccountKeyProductPurchase(
					account, jwt,
					_marketplaceService.getSkuOptionValue(
						"dxp-license-usage-type", orderItem.getOptions()),
					orderItem, productSpecificationsMap);
			}

			_marketplaceService.updateOrder(
				null, order.getId(),
				MarketplaceConstants.ORDER_STATUS_COMPLETED);
		}
		catch (Exception exception) {
			_log.error("Unable to create account product purchase", exception);
		}
	}

	private static final int _ACCOUNT_TYPE_BUSINESS = 2;

	private static final int _ACCOUNT_TYPE_PERSON = 1;

	private static final double _MARKETPLACE_TAX_PERCENTAGE = 0.23;

	private static final Log _log = LogFactory.getLog(
		MarketplaceRestController.class);

	private final Set<String> _europeanCountriesISOCode = Set.of(
		"AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR",
		"HR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO",
		"SE", "SI", "SK");

	@Autowired
	private KoroneikiService _koroneikiService;

	@Autowired
	private MarketplaceService _marketplaceService;

}