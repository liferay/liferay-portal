/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.RoleResource;
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
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
		_invoke(this::_processInProgressTrials, "In Progress Trials");

		_invoke(
			this::_processLiferayStaffUserGroups, "Liferay Staff User Groups");

		_invoke(this::_processOnHoldTrials, "On Hold Trials");

		_invoke(this::_processPendingOrders, "Pending Orders");

		_invoke(this::_processMostPurchasedProducts, "Most Purchased Products");

		_invoke(
			this::_processProjectsUsingMarketplaceApps,
			"Projects Using Marketplace Apps");

		_invoke(this::_processPublisherSalesSummary, "Publisher Sales Summary");

		_invoke(
			this::_processRequestProductFeedback, "Request Product Feedback");
	}

	private void _assignAccountToUserAccount(
			Account account, UserAccount userAccount)
		throws Exception {

		for (AccountBrief accountBrief : userAccount.getAccountBriefs()) {
			if (Objects.equals(
					accountBrief.getExternalReferenceCode(),
					account.getExternalReferenceCode())) {

				return;
			}
		}

		UserAccountResource userAccountResource = _getUserAccountResource();

		userAccountResource.postAccountUserAccountByEmailAddress(
			account.getId(), userAccount.getEmailAddress());

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Assigned account ", account.getName(), " to user ",
					userAccount.getName()));
		}
	}

	private void _assignRoleToUserAccount(Role role, UserAccount userAccount)
		throws Exception {

		for (RoleBrief roleBrief : userAccount.getRoleBriefs()) {
			if (Objects.equals(roleBrief.getName(), role.getName())) {
				return;
			}
		}

		RoleResource roleResource = _getRoleResource();

		roleResource.postRoleUserAccountAssociation(
			role.getId(), userAccount.getId());

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Assigned role ", role.getName(), " to user ",
					userAccount.getName()));
		}
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

	private AccountResource _getAccountResource() throws Exception {
		return AccountResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).build();
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

	private RoleResource _getRoleResource() throws Exception {
		return RoleResource.builder(
		).endpoint(
			new URL(_lxcDXPServerProtocol + "://" + _lxcDXPMainDomain)
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes)
		).build();
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

	private Collection<UserAccount> _getUserAccounts(
			String userAccountFilterString, String userGroupFilterString)
		throws Exception {

		UserGroupResource userGroupResource = _getUserGroupResource();

		com.liferay.headless.admin.user.client.pagination.Page<UserGroup>
			userGroupsPage = userGroupResource.getUserGroupsPage(
				"", userGroupFilterString,
				com.liferay.headless.admin.user.client.pagination.Pagination.of(
					-1, -1),
				"");

		UserGroup userGroup = userGroupsPage.fetchFirstItem();

		if (userGroup == null) {
			return Collections.emptyList();
		}

		UserAccountResource userAccountResource = _getUserAccountResource();

		com.liferay.headless.admin.user.client.pagination.Page<UserAccount>
			userAccountsPage = userAccountResource.getUserGroupUsersPage(
				userGroup.getId(), "", userAccountFilterString,
				com.liferay.headless.admin.user.client.pagination.Pagination.of(
					-1, -1),
				"");

		return userAccountsPage.getItems();
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

	private void _invoke(UnsafeRunnable<?> task, String name) {
		if (_log.isInfoEnabled()) {
			_log.info("Processing \"" + name + "\"");
		}

		try {
			task.run();
		}
		catch (Throwable throwable) {
			_log.error("Unable to process " + name, throwable);
		}
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

	private void _postRequestProductFeedback(long orderId) throws Exception {
		post(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			"",
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL +
					"/marketplace/request-product-feedback/" + orderId
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
			StringBundler.concat(
				"orderStatus/any(x:(x eq ", _ORDER_STATUS_IN_PROGRESS,
				")) and orderTypeExternalReferenceCode in (",
				"'SSA_SAAS', 'SOLUTIONS7')"),
			-1, -1);

		if (page.getTotalCount() == 0) {
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

	private void _processLiferayStaffUserGroups() throws Exception {
		AccountResource accountResource = _getAccountResource();

		Account account = accountResource.getAccountByExternalReferenceCode(
			"SSA-ACCOUNT");

		if (account == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Account is null");
			}

			return;
		}

		RoleResource roleResource = _getRoleResource();

		com.liferay.headless.admin.user.client.pagination.Page<Role> rolesPage =
			roleResource.getRolesPage(
				null, null, "name eq 'Liferay Staff'",
				com.liferay.headless.admin.user.client.pagination.Pagination.of(
					-1, -1));

		Role role = rolesPage.fetchFirstItem();

		if (role == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Role is null");
			}

			return;
		}

		for (UserAccount userAccount :
				_getUserAccounts(null, "name eq 'Employees'")) {

			_assignRoleToUserAccount(role, userAccount);
			_assignAccountToUserAccount(account, userAccount);
		}
	}

	private void _processMostPurchasedProducts() throws Exception {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);

		if ((zonedDateTime.getHour() / _WINDOW_SIZE_HOURS) != 0) {
			return;
		}

		Map<String, JSONObject> productPurchases = new HashMap<>();

		String filterString = StringBundler.concat(
			"orderStatus/any(x:(x eq ", _ORDER_STATUS_COMPLETED,
			")) or orderTypeExternalReferenceCode eq 'AI_HUB'");

		_forEachOrder(
			filterString,
			order -> {
				OrderItem[] orderItems = order.getOrderItems();

				if (ArrayUtil.isEmpty(orderItems)) {
					return;
				}

				OrderItem orderItem = orderItems[0];

				String productName = orderItem.getName(
				).get(
					"en_US"
				);

				JSONObject productJSONObject = productPurchases.get(
					productName);

				if (productJSONObject == null) {
					productJSONObject = new JSONObject(
					).put(
						"orderTypeExternalReferenceCode",
						order.getOrderTypeExternalReferenceCode()
					).put(
						"productId", orderItem.getProductId()
					).put(
						"total", 1
					);

					productPurchases.put(productName, productJSONObject);
				}
				else {
					productJSONObject.put(
						"total", productJSONObject.getInt("total") + 1);
				}
			});

		_putReportByExternalReferenceCode(
			new JSONObject(
			).put(
				"value",
				new JSONArray(
					productPurchases.values()
				).toString()
			).toString(),
			"PRODUCT-PURCHASES-COUNT");

		if (_log.isInfoEnabled()) {
			_log.info("Processed most purchased products");
		}
	}

	private void _processOnHoldTrials() throws Exception {
		Page<Order> page = _getOrdersPage(
			"orderStatus/any(x:(x eq " + _ORDER_STATUS_ON_HOLD +
				")) and orderTypeExternalReferenceCode eq 'SOLUTIONS7'",
			-1, -1);

		if (page.getTotalCount() == 0) {
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

	private void _processPendingOrders() throws Exception {
		String filterString = StringBundler.concat(
			"orderStatus/any(x:(x eq ", _ORDER_STATUS_PENDING,
			")) and not (orderTypeExternalReferenceCode in (",
			"'AI_HUB', 'DXP', 'SOLUTIONS7'))");

		Page<Order> page = _getOrdersPage(filterString, -1, -1);

		if (page.getTotalCount() == 0) {
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

		Collection<UserAccount> userAccounts = _getUserAccounts(
			"not contains(emailAddress, '@liferay.com')",
			"name eq 'Customers'");

		_forEachOrder(
			StringBundler.concat(
				"createDate ge ",
				LocalDate.of(
					ZonedDateTime.now(
						ZoneOffset.UTC
					).getYear(),
					1, 1
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

				projectsUsingMarketplace.putIfAbsent(
					key,
					new JSONObject(
					).put(
						"accountName", jsonObject.getString("name")
					).put(
						"orders", new JSONArray()
					));

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

		for (Map.Entry<String, JSONObject> entry :
				projectsUsingMarketplace.entrySet()) {

			String name = "KORONEIKI-PROJECT-" + entry.getKey();

			try {
				_putReportByExternalReferenceCode(
					new JSONObject(
					).put(
						"name", name
					).put(
						"value",
						entry.getValue(
						).toString()
					).toString(),
					name);
			}
			catch (Exception exception) {
				_log.error("Unable to put report " + name, exception);
			}
		}

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

	private void _processRequestProductFeedback() throws Exception {
		ZonedDateTime nowZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);

		ZonedDateTime windowStartZonedDateTime = nowZonedDateTime.withHour(
			(nowZonedDateTime.getHour() / _WINDOW_SIZE_HOURS) *
				_WINDOW_SIZE_HOURS
		).withMinute(
			0
		).withNano(
			0
		).withSecond(
			0
		);

		DateTimeFormatter dateTimeFormatter =
			DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		String filterString = StringBundler.concat(
			"createDate ge ",
			dateTimeFormatter.format(
				windowStartZonedDateTime.minusDays(
					7
				).minusHours(
					_WINDOW_SIZE_HOURS
				)),
			" and createDate le ",
			dateTimeFormatter.format(windowStartZonedDateTime.minusDays(7)),
			" and orderTypeExternalReferenceCode in ('ADDONS', 'CMP_BETA')");

		Page<Order> page = _getOrdersPage(filterString, -1, -1);

		if (page.getTotalCount() == 0) {
			if (_log.isInfoEnabled()) {
				_log.info("There are no request product feedback to be sent");
			}

			return;
		}

		for (Order order : page.getItems()) {
			try {
				OrderItem[] orderItems = order.getOrderItems();

				OrderItem orderItem = orderItems[0];

				if (orderItem == null) {
					continue;
				}

				_postRequestProductFeedback(order.getId());
			}
			catch (Exception exception) {
				_log.error(
					"Unable to process request product feedback for order " +
						order.getId(),
					exception);
			}
		}
	}

	private void _putReportByExternalReferenceCode(
		String body, String externalReferenceCode) {

		put(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			body,
			UriComponentsBuilder.fromPath(
				"/o/c/reports/by-external-reference-code/" +
					externalReferenceCode
			).build(
			).toUri());
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

	private static final int _WINDOW_SIZE_HOURS = 6;

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

}