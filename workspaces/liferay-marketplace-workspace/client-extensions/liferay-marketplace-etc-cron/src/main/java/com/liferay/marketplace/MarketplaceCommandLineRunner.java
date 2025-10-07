/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserGroupResource;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Keven Leone
 * @author Wellington Barbosa
 */
@Component
public class MarketplaceCommandLineRunner
	extends BaseRestController implements CommandLineRunner {

	public void run(String... args) throws Exception {
		_processInProgressTrials();

		_processOnHoldTrials();

		_processPendingOrders();

		_processOrdersTotalAmount();

		_processProjectsUsingMarketplaceApps();

		_processPublisherSalesSummary();
	}

	private JSONObject _createPublisherSalesSummary(
		Catalog catalog, String quarter) {

		return new JSONObject(
			post(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes),
				new JSONObject(
				).put(
					"paymentStatus", "unpaid"
				).put(
					"publisherName", catalog.getName()
				).put(
					"quarter", quarter
				).put(
					"r_accountToPublisher_accountEntryId",
					catalog.getAccountId()
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/publishersalessummaries/"
				).build(
				).toUri()));
	}

	private void _forEachOrder(
			String filterString,
			UnsafeConsumer<Order, Exception> unsafeConsumer)
		throws Exception {

		for (int i = 1;; i++) {
			Page<Order> page = _getOrdersPage(filterString, i, 200);

			for (Order order : page.getItems()) {
				try {
					unsafeConsumer.accept(order);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}

			if (i == page.getLastPage()) {
				break;
			}
		}
	}

	private JSONObject _getAvailabilityJSONObject() {
		return new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes),
				UriComponentsBuilder.fromUriString(
					_liferayMarketplaceEtcSpringBootURL + "/trial/availability"
				).build(
				).toUri()));
	}

	private CatalogResource _getCatalogResource() throws Exception {
		return CatalogResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).build();
	}

	private JSONArray _getContactTeamsJSONArray(String emailAddress) {
		try {
			JSONObject jsonObject = new JSONObject(
				get(
					_liferayOAuth2AccessTokenManager.getAuthorization(
						_liferayOAuthApplicationExternalReferenceCodes),
					UriComponentsBuilder.fromUriString(
						_liferayMarketplaceEtcSpringBootURL +
							"/koroneiki/contact/by-email-address/" +
								emailAddress
					).build(
					).toUri()));

			return jsonObject.getJSONArray("teams");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to find contact teams for " + emailAddress, exception);

			return new JSONArray();
		}
	}

	private String _getCurrentQuarter() {
		Instant instant = Instant.now();

		LocalDate localDate = instant.atZone(
			ZoneOffset.UTC
		).toLocalDate();

		int quarter = ((localDate.getMonthValue() - 1) / 3) + 1;

		return localDate.getYear() + " Q" + quarter;
	}

	private Collection<UserAccount> _getCustomerUserAccounts()
		throws Exception {

		UserGroupResource userGroupResource = _getUserGroupResource();

		UserGroup userGroup = userGroupResource.getUserGroupsPage(
			"", "name eq 'Customers'",
			com.liferay.headless.admin.user.client.pagination.Pagination.of(
				-1, -1),
			""
		).fetchFirstItem();

		if (userGroup == null) {
			return Collections.emptyList();
		}

		UserAccountResource userAccountResource = _getUserAccountResource();

		com.liferay.headless.admin.user.client.pagination.Page<UserAccount>
			userAccountPage = userAccountResource.getUserGroupUsersPage(
				userGroup.getId(), "",
				"not contains(emailAddress, '@liferay.com')",
				com.liferay.headless.admin.user.client.pagination.Pagination.of(
					-1, -1),
				"");

		return userAccountPage.getItems();
	}

	private String _getKoroneikiProject(Order order) {
		JSONArray jsonArray = new JSONArray();

		JSONArray contactTeamsJSONArray = _getContactTeamsJSONArray(
			order.getCreatorEmailAddress());

		for (int i = 0; i < contactTeamsJSONArray.length(); i++) {
			String accountExternalReferenceCode =
				order.getAccountExternalReferenceCode();

			JSONObject contactTeamJSONObject =
				contactTeamsJSONArray.getJSONObject(i);

			JSONObject jsonObject = new JSONObject(
			).put(
				"key", contactTeamJSONObject.getString("key")
			).put(
				"name", contactTeamJSONObject.getString("name")
			);

			if (accountExternalReferenceCode.startsWith("KOR-")) {
				if (Objects.equals(
						accountExternalReferenceCode,
						contactTeamJSONObject.getString("key"))) {

					jsonArray.put(jsonObject);

					break;
				}

				continue;
			}

			jsonArray.put(jsonObject);
		}

		if (jsonArray.isEmpty()) {
			return null;
		}

		return jsonArray.toString();
	}

	private OrderResource _getOrderResource() throws Exception {
		return OrderResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).parameters(
			"nestedFields", "account,orderItems"
		).build();
	}

	private Page<Order> _getOrdersPage(
			String filterString, int page, int pageSize)
		throws Exception {

		OrderResource orderResource = _getOrderResource();

		return orderResource.getOrdersPage(
			"", filterString, Pagination.of(page, pageSize), "");
	}

	private JSONObject _getPaidOrdersJSONObject() {
		return new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes),
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						"/o/headless-commerce-admin-order/v1.0/orders",
						"?filter=totalAmount gt 0.0",
						"&nestedFields=orderItems",
						"&page=-1&pageSize=-1&sort=createDate:desc")
				).build(
				).toUri()));
	}

	private ProductResource _getProductResource() throws Exception {
		return ProductResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).build();
	}

	private long _getPublisherSalesSummaryId(long accountId, String quarter) {
		JSONObject jsonObject = new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes),
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						"/o/c/publishersalessummaries?filter=quarter eq '",
						quarter,
						"' and r_accountToPublisher_accountEntryId eq '",
						accountId, "'")
				).build(
				).toUri()));

		JSONArray itemsJSONArray = jsonObject.optJSONArray("items");

		if (itemsJSONArray.isEmpty()) {
			return -1;
		}

		JSONObject publisherSummaryJSONObject = itemsJSONArray.getJSONObject(0);

		return publisherSummaryJSONObject.getLong("id");
	}

	private SkuResource _getSkuResource() throws Exception {
		return SkuResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).build();
	}

	private UserAccount _getUserAccount(
		String emailAddress, Collection<UserAccount> userAccounts) {

		for (UserAccount userAccount : userAccounts) {
			if (Objects.equals(emailAddress, userAccount.getEmailAddress())) {
				return userAccount;
			}
		}

		return null;
	}

	private UserAccountResource _getUserAccountResource() throws Exception {
		return UserAccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-cron-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private UserGroupResource _getUserGroupResource() throws Exception {
		return UserGroupResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-cron-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private void _patchOrder(long orderId, long publisherSalesSummaryId) {
		patch(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			new JSONObject(
			).put(
				"r_publisherToCommerceOrder_c_publisherSalesSummaryId",
				publisherSalesSummaryId
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/headless-commerce-admin-order/v1.0/orders/" + orderId
			).build(
			).toUri());
	}

	private void _patchReport(String data, String externalReferenceCode) {
		patch(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			data,
			UriComponentsBuilder.fromPath(
				"/o/c/reports/by-external-reference-code/" +
					externalReferenceCode
			).build(
			).toUri());
	}

	private void _postTrialExpire(long orderId) throws Exception {
		post(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			"",
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL + "/trial/expire/" + orderId
			).build(
			).toUri());
	}

	private void _postTrialNotifyEnd(long orderId) throws Exception {
		post(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			"",
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL + "/trial/notify-end/" +
					orderId
			).build(
			).toUri());
	}

	private void _postTrialProvisioning(Order order) throws Exception {
		post(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			new JSONObject(
			).put(
				"classPK", order.getId()
			).put(
				"modelDTOOrder",
				new JSONObject(
				).put(
					"accountId", String.valueOf(order.getAccountId())
				)
			).toString(),
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL + "/trial/provisioning"
			).build(
			).toUri());
	}

	private void _processInProgressTrials() throws Exception {
		Page<Order> page = _getOrdersPage(
			"orderStatus/any(x:(x eq " + _ORDER_STATUS_IN_PROGRESS +
				")) and orderTypeExternalReferenceCode eq 'SOLUTIONS7'",
			-1, -1);

		if (page.getTotalCount() == 0) {
			if (_log.isInfoEnabled()) {
				_log.info("There are no in progress trials");
			}

			return;
		}

		for (Order order : page.getItems()) {
			try {
				ZonedDateTime nowZonedDateTime = ZonedDateTime.now();

				Map<String, String> customFields =
					(Map<String, String>)order.getCustomFields();

				ZonedDateTime trialEndDateZonedDateTime = ZonedDateTime.parse(
					customFields.get("trial-end-date"));

				if (nowZonedDateTime.isAfter(trialEndDateZonedDateTime)) {
					_postTrialExpire(order.getId());

					if (_log.isInfoEnabled()) {
						_log.info("Processed expired order " + order.getId());
					}

					continue;
				}

				if (customFields.get(
						"trial-notify-end-date"
					).isEmpty() &&
					Objects.equals(
						nowZonedDateTime.getDayOfMonth(),
						trialEndDateZonedDateTime.minusDays(
							1
						).getDayOfMonth())) {

					_postTrialNotifyEnd(order.getId());

					if (_log.isInfoEnabled()) {
						_log.info(
							"Processed notify end of trial for order " +
								order.getId());
					}
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private void _processOnHoldTrials() throws Exception {
		Page<Order> page = _getOrdersPage(
			"orderStatus/any(x:(x eq " + _ORDER_STATUS_ON_HOLD +
				")) and orderTypeExternalReferenceCode eq 'SOLUTIONS7'",
			-1, -1);

		if (page.getTotalCount() == 0) {
			if (_log.isInfoEnabled()) {
				_log.info("There are no on hold trials");
			}

			return;
		}

		JSONObject availabilityJSONObject = _getAvailabilityJSONObject();

		if (!availabilityJSONObject.getBoolean("active")) {
			if (_log.isInfoEnabled()) {
				_log.info("There are no available seats");
			}

			return;
		}

		long available = availabilityJSONObject.getLong("available");

		for (Order order : page.getItems()) {
			if (available == 0) {
				if (_log.isInfoEnabled()) {
					_log.info("There are no available seats");
				}

				break;
			}

			try {
				if (_log.isInfoEnabled()) {
					_log.info("Processing on hold order " + order.getId());
				}

				_postTrialProvisioning(order);

				if (_log.isInfoEnabled()) {
					_log.info("Processed on hold order " + order.getId());
				}

				available--;
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private void _processOrdersTotalAmount() throws Exception {
		_forEachOrder(
			StringBundler.concat(
				"orderStatus/any(x:(x eq ", _ORDER_STATUS_COMPLETED,
				")) and orderTypeExternalReferenceCode eq 'DXPAPP'"),
			order -> {
				String currencyCode = order.getCurrencyCode();

				if (!_totalAmount.containsKey(currencyCode)) {
					_totalAmount.put(currencyCode, 0D);
				}

				_totalAmount.put(
					currencyCode,
					_totalAmount.get(currencyCode) + order.getTotalAmount());
			});

		if (_log.isInfoEnabled()) {
			_log.info("Orders total amount " + _totalAmount);
		}

		_patchReport(
			new JSONObject(
			).put(
				"value",
				new JSONObject(
					_totalAmount
				).toString()
			).toString(),
			"TOTAL-AMOUNT");
	}

	private void _processPendingOrders() throws Exception {
		Page<Order> page = _getOrdersPage(
			"orderStatus/any(x:(x eq " + _ORDER_STATUS_PENDING +
				")) and orderTypeExternalReferenceCode ne 'SOLUTIONS7'",
			-1, -1);

		if (page.getTotalCount() == 0) {
			if (_log.isInfoEnabled()) {
				_log.info("There are no pending orders");
			}

			return;
		}

		for (Order order : page.getItems()) {
			if (order.getTotalAmount() > 0) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Paid order " + order.getId() +
							" needs to be manually reviewed");
				}

				continue;
			}

			if (_log.isInfoEnabled()) {
				_log.info("Completing free order " + order.getId());
			}

			_updateOrder(order.getId(), _ORDER_STATUS_PROCESSING);

			_updateOrder(order.getId(), _ORDER_STATUS_COMPLETED);
		}
	}

	private void _processProjectsUsingMarketplaceApps() throws Exception {
		Map<String, JSONObject> projectsUsingMarketplace = new HashMap<>();

		OrderResource orderResource = _getOrderResource();
		Collection<UserAccount> userAccounts = _getCustomerUserAccounts();

		_forEachOrder(
			StringBundler.concat(
				"createDate gt ",
				LocalDate.of(
					2025, 1, 1
				).atStartOfDay(
					ZoneOffset.UTC
				),
				" and (not contains(creatorEmailAddress, '@liferay.com')) and ",
				"orderTypeExternalReferenceCode ne 'SOLUTIONS7'"),
			order -> {
				String accountExternalReferenceCode =
					order.getAccountExternalReferenceCode();

				Map<String, String> customFields =
					(Map<String, String>)order.getCustomFields();

				String koroneikiProject = customFields.get("koroneiki-project");

				if (Validator.isNull(koroneikiProject)) {
					if (Validator.isNull(
							_getUserAccount(
								order.getCreatorEmailAddress(),
								userAccounts))) {

						return;
					}

					koroneikiProject = _getKoroneikiProject(order);

					if (Validator.isNull(koroneikiProject)) {
						return;
					}

					customFields.put("koroneiki-project", koroneikiProject);

					orderResource.patchOrder(order.getId(), order);
				}

				JSONObject jsonObject = new JSONArray(
					koroneikiProject
				).getJSONObject(
					0
				);

				String key = jsonObject.getString("key");

				if (!projectsUsingMarketplace.containsKey(key)) {
					projectsUsingMarketplace.put(
						key,
						new JSONObject(
						).put(
							"accountName", jsonObject.getString("name")
						).put(
							"orders", new JSONArray()
						));
				}

				projectsUsingMarketplace.get(
					key
				).getJSONArray(
					"orders"
				).put(
					new JSONObject(
					).put(
						"accountExternalReferenceCode",
						accountExternalReferenceCode
					).put(
						"creatorEmailAddress", order.getCreatorEmailAddress()
					).put(
						"id", order.getId()
					).put(
						"orderTypeExternalReferenceCode",
						order.getOrderTypeExternalReferenceCode()
					).put(
						"projects", new JSONArray(koroneikiProject)
					)
				);
			});

		_patchReport(
			new JSONObject(
			).put(
				"value",
				new JSONObject(
					projectsUsingMarketplace
				).toString()
			).toString(),
			"PROJECTS-USING-MARKETPLACE");

		if (_log.isInfoEnabled()) {
			_log.info(
				"There are " + projectsUsingMarketplace.size() +
					" projects with Marketplace apps");
		}
	}

	private void _processPublisherSalesSummary() throws Exception {
		CatalogResource catalogResource = _getCatalogResource();
		String currentQuarter = _getCurrentQuarter();

		JSONObject paidOrdersJSONObject = _getPaidOrdersJSONObject();

		JSONArray itemsJSONArray = paidOrdersJSONObject.getJSONArray("items");

		ProductResource productResource = _getProductResource();
		SkuResource skuResource = _getSkuResource();

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject orderJSONObject = itemsJSONArray.getJSONObject(i);

			Order order = Order.toDTO(orderJSONObject.toString());

			long publisherSalesSummaryId = orderJSONObject.optLong(
				"r_publisherToCommerceOrder_c_publisherSalesSummaryId", 0);

			if ((publisherSalesSummaryId != 0) ||
				!Objects.equals(
					order.getPaymentStatus(),
					_ORDER_PAYMENT_STATUS_COMPLETED)) {

				continue;
			}

			OrderItem[] orderItems = order.getOrderItems();

			OrderItem orderItem = orderItems[0];

			if (orderItem == null) {
				continue;
			}

			Sku sku = skuResource.getSku(orderItem.getSkuId());

			Product product = productResource.getProduct(sku.getProductId());

			Catalog catalog = catalogResource.getCatalog(
				product.getCatalogId());

			publisherSalesSummaryId = _getPublisherSalesSummaryId(
				catalog.getAccountId(), currentQuarter);

			if (publisherSalesSummaryId == -1) {
				JSONObject publisherSalesSummaryJSONObject =
					_createPublisherSalesSummary(catalog, currentQuarter);

				publisherSalesSummaryId =
					publisherSalesSummaryJSONObject.getLong("id");
			}

			_patchOrder(order.getId(), publisherSalesSummaryId);
		}
	}

	private void _updateOrder(long orderId, int orderStatus) throws Exception {
		OrderResource orderResource = _getOrderResource();

		Order order = new Order();

		order.setOrderStatus(() -> orderStatus);

		orderResource.patchOrder(orderId, order);
	}

	private static final int _ORDER_PAYMENT_STATUS_COMPLETED = 0;

	private static final int _ORDER_STATUS_COMPLETED = 0;

	private static final int _ORDER_STATUS_IN_PROGRESS = 6;

	private static final int _ORDER_STATUS_ON_HOLD = 20;

	private static final int _ORDER_STATUS_PENDING = 1;

	private static final int _ORDER_STATUS_PROCESSING = 10;

	private static final Log _log = LogFactory.getLog(
		MarketplaceCommandLineRunner.class);

	@Value("${liferay.marketplace.etc.spring.boot.url}")
	private URL _liferayMarketplaceEtcSpringBootURL;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${liferay.oauth.application.external.reference.codes}")
	private String _liferayOAuthApplicationExternalReferenceCodes;

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	private String _lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	private String _lxcDXPServerProtocol;

	private final Map<String, Double> _totalAmount = new HashMap<>();

}