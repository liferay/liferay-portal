/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.headless.admin.user.client.custom.field.CustomField;
import com.liferay.headless.admin.user.client.custom.field.CustomValue;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserGroupResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.pagination.Page;
import com.liferay.headless.commerce.admin.order.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

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

		_processMarketplaceProjects();
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

	private UserAccountResource _getUserAccountResource() throws Exception {
		return UserAccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-cron-oauth-application-headless-" +
					"server")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private UserGroupResource _getUserGroupResource() throws Exception {
		return UserGroupResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-cron-oauth-application-headless-" +
					"server")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private void _postTrialExpire(long orderId) throws Exception {
		post(
			null,
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION,
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes)),
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL + "/trial/expire/" + orderId
			).build(
			).toUri());
	}

	private void _postTrialNotifyEnd(long orderId) throws Exception {
		post(
			null,
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION,
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes)),
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL + "/trial/notify-end/" +
					orderId
			).build(
			).toUri());
	}

	private void _postTrialProvisioning(Order order) throws Exception {
		post(
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
			Collections.singletonMap(
				HttpHeaders.AUTHORIZATION,
				_liferayOAuth2AccessTokenManager.getAuthorization(
					_liferayOAuthApplicationExternalReferenceCodes)),
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

	private void _processMarketplaceProjects() throws Exception {
		Map<String, UserAccount> customerUserAccounts = new HashMap<>();
		String filterString = StringBundler.concat(
			"createDate gt ",
			LocalDate.of(
				2025, 1, 1
			).atStartOfDay(
				ZoneOffset.UTC
			),
			"and (not contains(creatorEmailAddress, '@liferay.com')) and ",
			"orderTypeExternalReferenceCode ne 'SOLUTIONS7'");
		Collection<UserAccount> userAccounts = _getCustomerUserAccounts();

		for (int i = 1;; i++) {
			Page<Order> page = _getOrdersPage(filterString, i, 200);

			for (Order order : page.getItems()) {
				String creatorEmailAddress = order.getCreatorEmailAddress();

				if (customerUserAccounts.containsKey(creatorEmailAddress)) {
					continue;
				}

				for (UserAccount userAccount : userAccounts) {
					if (Objects.equals(
							creatorEmailAddress,
							userAccount.getEmailAddress())) {

						customerUserAccounts.put(
							creatorEmailAddress, userAccount);

						break;
					}
				}
			}

			if (i > page.getLastPage()) {
				break;
			}
		}

		JSONArray jsonArray = new JSONArray();

		for (UserAccount userAccount : customerUserAccounts.values()) {
			for (CustomField customField : userAccount.getCustomFields()) {
				if (!Objects.equals(
						customField.getName(), "customer-project")) {

					continue;
				}

				CustomValue customValue = customField.getCustomValue();

				if (Validator.isNull(customValue.getData())) {
					JSONObject jsonObject = new JSONObject(
						get(
							_liferayOAuth2AccessTokenManager.getAuthorization(
								_liferayOAuthApplicationExternalReferenceCodes),
							UriComponentsBuilder.fromUriString(
								_liferayMarketplaceEtcSpringBootURL +
									"/koroneiki/contact/by-email-address/" +
										userAccount.getEmailAddress()
							).build(
							).toUri()));

					customValue.setData(
						String.valueOf(
							jsonObject.getJSONArray(
								"teams"
							).get(
								0
							)));

					UserAccountResource userAccountResource =
						_getUserAccountResource();

					userAccountResource.patchUserAccount(
						userAccount.getId(), userAccount);
				}

				jsonArray.put(
					new JSONObject(
					).put(
						"customer-project",
						new JSONObject(String.valueOf(customValue.getData()))
					).put(
						"emailAddress", userAccount.getEmailAddress()
					));
			}
		}

		post(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				_liferayOAuthApplicationExternalReferenceCodes),
			jsonArray.toString(),
			UriComponentsBuilder.fromUriString(
				_liferayMarketplaceEtcSpringBootURL +
					"/marketplace/projects/kpi"
			).build(
			).toUri());

		if (_log.isInfoEnabled()) {
			_log.info(
				"There are " + customerUserAccounts.size() +
					" projects with Marketplace apps");
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

	private void _updateOrder(long orderId, int orderStatus) throws Exception {
		OrderResource orderResource = _getOrderResource();

		Order order = new Order();

		order.setOrderStatus(() -> orderStatus);

		orderResource.patchOrder(orderId, order);
	}

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

}