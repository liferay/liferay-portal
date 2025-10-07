/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.partner;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;

import com.rabbitmq.client.Channel;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Jair Medeiros
 * @author Thaynam Lazaro
 */
@Component
public class QueueListener extends BaseRestController {

	@RabbitListener(
		bindings = {
			@QueueBinding(
				exchange = @Exchange(type = ExchangeTypes.TOPIC, value = "koroneiki_exchange"),
				value = @Queue("${spring.rabbitmq.template.default-receive-queue}")
			)
		}
	)
	public void listener(
		Message message, Channel channel,
		@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

		String routingKey = message.getMessageProperties(
		).getReceivedRoutingKey();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Received message ", deliveryTag, " with routing key ",
					routingKey));
		}

		try {
			String body = new String(message.getBody(), "UTF-8");

			JSONObject jsonObject = new JSONObject(body);

			JSONObject koroneikiAccountJSONObject = jsonObject.getJSONObject(
				"account");

			if (!_isPartner(koroneikiAccountJSONObject)) {
				channel.basicReject(deliveryTag, false);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Account with name ",
							koroneikiAccountJSONObject.getString("name"),
							" is not a partner account"));
				}

				return;
			}

			String salesforceAccountKey = _getSalesforceAccountKey(
				koroneikiAccountJSONObject);

			if (salesforceAccountKey.equals("")) {
				channel.basicReject(deliveryTag, false);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Account with name ",
							koroneikiAccountJSONObject.getString("name"),
							" does not have a Salesforce account key"));
				}

				return;
			}

			if (routingKey.equals("koroneiki.account.contactrole.assigned")) {
				JSONObject contactJSONObject = jsonObject.getJSONObject(
					"contact");

				JSONObject contactRoleJSONObject = jsonObject.getJSONObject(
					"contactRole");

				_assignUser(
					salesforceAccountKey,
					koroneikiAccountJSONObject.getString("name"),
					contactJSONObject.optString("emailAddress"),
					contactRoleJSONObject.getString("name"));
			}
			else if (routingKey.equals(
						"koroneiki.account.contactrole.unassigned")) {

				JSONObject contactJSONObject = jsonObject.getJSONObject(
					"contact");

				JSONObject contactRoleJSONObject = jsonObject.getJSONObject(
					"contactRole");

				_unassignUser(
					salesforceAccountKey,
					koroneikiAccountJSONObject.getString("name"),
					contactJSONObject.optString("emailAddress"),
					contactRoleJSONObject.getString("name"));
			}
			else if (routingKey.equals("koroneiki.account.update")) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Updating account ", salesforceAccountKey,
							" with name ",
							koroneikiAccountJSONObject.getString("name")));
				}

				String countryISOCode = _getCountryISOCode(
					koroneikiAccountJSONObject);

				_updateAccount(
					salesforceAccountKey,
					koroneikiAccountJSONObject.getString("name"),
					countryISOCode);
			}

			channel.basicAck(deliveryTag, false);
		}
		catch (Exception exception1) {
			_log.error(exception1.getMessage(), exception1);

			try {
				channel.basicReject(deliveryTag, false);
			}
			catch (Exception exception2) {
				_log.error(exception2);
			}
		}
	}

	private void _assignUser(
			String accountExternalReferenceCode, String accountName,
			String contactEmailAddress, String contactRoleName)
		throws Exception {

		if (contactRoleName.equals(_KORONEIKI_ROLE_PARTNER_MANAGER_NAME)) {
			_assignUserToAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MANAGER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MANAGER_NAME);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_MARKETING_USER_NAME)) {

			_assignUserToAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MARKETING_USER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MARKETING_USER_NAME);
		}
		else if (contactRoleName.equals(_KORONEIKI_ROLE_PARTNER_MEMBER_NAME)) {
			_assignUserToAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MARKETING_USER_NAME);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_SALES_USER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MARKETING_USER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_SALES_USER_NAME);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_SALES_USER_NAME)) {

			_assignUserToAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_SALES_USER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_SALES_USER_NAME);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_TECHNICAL_USER_NAME)) {

			_assignUserToAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
			_assignUserToAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_TECHNICAL_USER_NAME);
			_assignUserToRegularRole(
				contactEmailAddress, _ROLE_PARTNER_TECHNICAL_USER_NAME);
		}
	}

	private void _assignUserToAccount(
		String accountExternalReferenceCode, String accountName,
		String contactEmailAddress) {

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Assigning user ", contactEmailAddress, " to account ",
					accountExternalReferenceCode, " with name ", accountName));
		}

		post(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/",
					accountExternalReferenceCode,
					"/user-accounts/by-email-address/", contactEmailAddress)
			).build(
			).toUri());
	}

	private void _assignUserToAccountRole(
		String accountExternalReferenceCode, String contactEmailAddress,
		String accountRoleName) {

		long accountRoleId = _getAccountRoleId(
			accountExternalReferenceCode, accountRoleName);

		if (accountRoleId <= 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Assigning user ", contactEmailAddress, " to account role ",
					accountRoleName));
		}

		post(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/",
					accountExternalReferenceCode, "/account-roles/",
					accountRoleId, "/user-accounts/by-email-address/",
					contactEmailAddress)
			).build(
			).toUri());
	}

	private void _assignUserToRegularRole(String emailAddress, String name) {
		JSONObject userAccountJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/user-accounts" +
						"/by-email-address/" + emailAddress
				).build(
				).toUri()));

		Long userAccountId = userAccountJSONObject.getLong("id");

		if (userAccountId <= 0) {
			return;
		}

		long roleId = _getRegularRoleId(name);

		if (roleId <= 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Assigning user ", emailAddress, " to regular role ",
					name));
		}

		post(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/roles/", roleId,
					"/association/user-account/", userAccountId)
			).build(
			).toUri());
	}

	private long _getAccountRoleId(
		String accountExternalReferenceCode, String accountRoleName) {

		JSONObject accountRolesResponseJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						"/o/headless-admin-user/v1.0/accounts",
						"/by-external-reference-code/",
						accountExternalReferenceCode, "/account-roles")
				).queryParam(
					"pageSize", "-1"
				).build(
				).toUri()));

		JSONArray accountRolesJSONArray =
			accountRolesResponseJSONObject.getJSONArray("items");

		for (int i = 0; i < accountRolesJSONArray.length(); i++) {
			JSONObject accountRoleJSONObject =
				accountRolesJSONArray.getJSONObject(i);

			if (accountRoleName.equals(
					accountRoleJSONObject.getString("name"))) {

				return accountRoleJSONObject.getLong("id");
			}
		}

		return 0;
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-partner-etc-spring-boot-oahs");
	}

	private String _getCountryISOCode(JSONObject koroneikiAccountJSONObject) {
		JSONArray postalAddressesJSONArray =
			koroneikiAccountJSONObject.getJSONArray("postalAddresses");

		for (int i = 0; i < postalAddressesJSONArray.length(); i++) {
			JSONObject postalAddressesJSONObject =
				postalAddressesJSONArray.getJSONObject(i);

			if (postalAddressesJSONObject.getBoolean("primary") &&
				postalAddressesJSONObject.has("addressCountry")) {

				for (String countryISOCode : Locale.getISOCountries()) {
					Locale countryNameLocale = new Locale("", countryISOCode);

					if (StringUtil.equalsIgnoreCase(
							postalAddressesJSONObject.getString(
								"addressCountry"),
							countryNameLocale.getDisplayCountry())) {

						return countryISOCode;
					}
				}
			}
		}

		return "";
	}

	private JSONObject _getJSONObject(String json) {
		try {
			return new JSONObject(json);
		}
		catch (JSONException jsonException) {
			_log.error("Unable to create JSON object: " + json);

			throw jsonException;
		}
	}

	private long _getRegionOrganizationId(String regionName) {
		JSONObject globalOrganizationJSONObject = _getJSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/organizations" +
						"/by-external-reference-code/PRM-ORG-GLOBAL"
				).build(
				).toUri()));

		JSONObject organizationsJSONObject = _getJSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/organizations/" +
						globalOrganizationJSONObject.getLong("id") +
							"/child-organizations"
				).queryParam(
					"pageSize", "-1"
				).build(
				).toUri()));

		JSONArray organizationsJSONArray = organizationsJSONObject.getJSONArray(
			"items");

		for (int i = 0; i < organizationsJSONArray.length(); i++) {
			JSONObject organizationJSONObject =
				organizationsJSONArray.getJSONObject(i);

			if (regionName.equals(organizationJSONObject.getString("name"))) {
				return organizationJSONObject.getLong("id");
			}
		}

		return 0;
	}

	private long _getRegularRoleId(String name) {
		JSONObject regularRolesResponseJSONObject = _getJSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/roles"
				).queryParam(
					"filter", "name eq '" + name + "'"
				).queryParam(
					"pageSize", "-1"
				).build(
				).toUri()));

		JSONArray regularRolesJSONArray =
			regularRolesResponseJSONObject.getJSONArray("items");

		for (int i = 0; i < regularRolesJSONArray.length(); i++) {
			JSONObject regularRoleJSONObject =
				regularRolesJSONArray.getJSONObject(i);

			if (name.equals(regularRoleJSONObject.getString("name"))) {
				return regularRoleJSONObject.getLong("id");
			}
		}

		return 0;
	}

	private String _getSalesforceAccountKey(
		JSONObject koroneikiAccountJSONObject) {

		JSONArray externalLinksJSONArray =
			koroneikiAccountJSONObject.getJSONArray("externalLinks");

		for (int i = 0; i < externalLinksJSONArray.length(); i++) {
			JSONObject externalLinkJSONObject =
				externalLinksJSONArray.getJSONObject(i);

			if ((StringUtil.equalsIgnoreCase(
					externalLinkJSONObject.getString("domain"), "salesforce") ||
				 StringUtil.equalsIgnoreCase(
					 externalLinkJSONObject.getString("domain"), "dossiera")) &&
				StringUtil.equalsIgnoreCase(
					externalLinkJSONObject.getString("entityName"),
					"account")) {

				return externalLinkJSONObject.getString("entityId");
			}
		}

		return "";
	}

	private boolean _isPartner(JSONObject koroneikiAccountJSONObject) {
		JSONArray entitlementsJSONArray =
			koroneikiAccountJSONObject.getJSONArray("entitlements");

		for (int i = 0; i < entitlementsJSONArray.length(); i++) {
			JSONObject entitlementJSONObject =
				entitlementsJSONArray.getJSONObject(i);

			if (StringUtil.equalsIgnoreCase(
					entitlementJSONObject.getString("name"), "Partner")) {

				return true;
			}
		}

		return false;
	}

	private void _unassignUser(
			String accountExternalReferenceCode, String accountName,
			String contactEmailAddress, String contactRoleName)
		throws Exception {

		if (contactRoleName.equals(_KORONEIKI_ROLE_PARTNER_MANAGER_NAME)) {
			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MANAGER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MANAGER_NAME);
			_unassignUserFromAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_MARKETING_USER_NAME)) {

			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MARKETING_USER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MARKETING_USER_NAME);
			_unassignUserFromAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
		}
		else if (contactRoleName.equals(_KORONEIKI_ROLE_PARTNER_MEMBER_NAME)) {
			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_MARKETING_USER_NAME);
			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_SALES_USER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_MARKETING_USER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_SALES_USER_NAME);
			_unassignUserFromAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_SALES_USER_NAME)) {

			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_SALES_USER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_SALES_USER_NAME);
			_unassignUserFromAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
		}
		else if (contactRoleName.equals(
					_KORONEIKI_ROLE_PARTNER_TECHNICAL_USER_NAME)) {

			_unassignUserFromRegularRole(
				contactEmailAddress, _ROLE_PARTNER_TECHNICAL_USER_NAME);
			_unassignUserFromAccountRole(
				accountExternalReferenceCode, contactEmailAddress,
				_ACCOUNT_ROLE_PARTNER_TECHNICAL_USER_NAME);
			_unassignUserFromAccount(
				accountExternalReferenceCode, accountName, contactEmailAddress);
		}
	}

	private void _unassignUserFromAccount(
		String accountExternalReferenceCode, String accountName,
		String contactEmailAddress) {

		JSONObject jsonObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						"/o/headless-admin-user/v1.0/accounts",
						"/by-external-reference-code/",
						accountExternalReferenceCode,
						"/user-accounts/by-email-address/", contactEmailAddress,
						"/account-roles")
				).build(
				).toUri()));

		if (jsonObject.getLong("totalCount") > 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Unassigning user ", contactEmailAddress, " from account ",
					accountExternalReferenceCode, " with name ", accountName));
		}

		delete(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/",
					accountExternalReferenceCode,
					"/user-accounts/by-email-address/", contactEmailAddress)
			).build(
			).toUri());
	}

	private void _unassignUserFromAccountRole(
		String accountExternalReferenceCode, String contactEmailAddress,
		String accountRoleName) {

		long accountRoleId = _getAccountRoleId(
			accountExternalReferenceCode, accountRoleName);

		if (accountRoleId <= 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Unassigning user ", contactEmailAddress,
					" from account role ", accountRoleName));
		}

		delete(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/accounts",
					"/by-external-reference-code/",
					accountExternalReferenceCode, "/account-roles/",
					accountRoleId, "/user-accounts/by-email-address/",
					contactEmailAddress)
			).build(
			).toUri());
	}

	private void _unassignUserFromRegularRole(
		String emailAddress, String name) {

		JSONObject userAccountJSONObject = _getJSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/user-accounts" +
						"/by-email-address/" + emailAddress
				).build(
				).toUri()));

		Long userAccountId = userAccountJSONObject.getLong("id");

		if (userAccountId <= 0) {
			return;
		}

		long roleId = _getRegularRoleId(name);

		if (roleId <= 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Unassigning user ", emailAddress, " from regular role ",
					name));
		}

		delete(
			_getAuthorization(), "",
			UriComponentsBuilder.fromPath(
				StringBundler.concat(
					"/o/headless-admin-user/v1.0/roles/", roleId,
					"/association/user-account/", userAccountId)
			).build(
			).toUri());
	}

	private void _updateAccount(
			String externalReferenceCode, String name, String countryISOCode)
		throws Exception {

		JSONObject accountJSONObject = new JSONObject() {
			{
				put("externalReferenceCode", externalReferenceCode);
				put("name", name);
			}
		};

		JSONObject proxyAccountJSONObject = new JSONObject(
			get(
				_getAuthorization(),
				UriComponentsBuilder.fromPath(
					"/o/c/proxyaccounts/by-external-reference-code/" +
						externalReferenceCode
				).build(
				).toUri()));

		if (proxyAccountJSONObject.has("currency")) {
			JSONObject proxyCurrencyJSONObject =
				proxyAccountJSONObject.getJSONObject("currency");

			accountJSONObject.put(
				"currency", proxyCurrencyJSONObject.getString("key"));
		}

		if (!countryISOCode.equals("")) {
			accountJSONObject.put("partnerCountry", countryISOCode);
		}

		JSONObject updatedAccountJSONObject = _getJSONObject(
			put(
				_getAuthorization(), accountJSONObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/headless-admin-user/v1.0/accounts" +
						"/by-external-reference-code/" + externalReferenceCode
				).build(
				).toUri()));

		if (proxyAccountJSONObject.has("region")) {
			_updateAccountRegion(
				updatedAccountJSONObject,
				proxyAccountJSONObject.getString("region"));
		}
	}

	private void _updateAccountRegion(
		JSONObject accountJSONObject, String proxyAccountRegion) {

		long regionOrganizationId = _getRegionOrganizationId(
			proxyAccountRegion);

		if (regionOrganizationId <= 0) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Assigning account to " + proxyAccountRegion);
		}

		JSONArray organizationIdsJSONArray = accountJSONObject.getJSONArray(
			"organizationIds");

		if (organizationIdsJSONArray.isEmpty()) {
			post(
				_getAuthorization(), "",
				UriComponentsBuilder.fromPath(
					StringBundler.concat(
						"/o/headless-admin-user/v1.0/accounts",
						"/by-external-reference-code/",
						accountJSONObject.getString("externalReferenceCode"),
						"/organizations/", regionOrganizationId)
				).build(
				).toUri());
		}
		else {
			Long organizationId = organizationIdsJSONArray.getLong(0);

			if (organizationId != regionOrganizationId) {
				JSONArray accountExternalReferenceCodeJSONArray =
					new JSONArray();

				accountExternalReferenceCodeJSONArray.put(
					accountJSONObject.getString("externalReferenceCode"));

				patch(
					_getAuthorization(),
					accountExternalReferenceCodeJSONArray.toString(),
					UriComponentsBuilder.fromPath(
						StringBundler.concat(
							"/o/headless-admin-user/v1.0/organizations",
							"/move-accounts/", organizationId, "/",
							regionOrganizationId, "/by-external-reference-code")
					).build(
					).toUri());
			}
		}
	}

	private static final String _ACCOUNT_ROLE_PARTNER_MANAGER_NAME =
		"[Account] Partner Manager (PM)";

	private static final String _ACCOUNT_ROLE_PARTNER_MARKETING_USER_NAME =
		"[Account] Partner Marketing User (PMU)";

	private static final String _ACCOUNT_ROLE_PARTNER_SALES_USER_NAME =
		"[Account] Partner Sales User (PSU)";

	private static final String _ACCOUNT_ROLE_PARTNER_TECHNICAL_USER_NAME =
		"[Account] Partner Technical User (PTU)";

	private static final String _KORONEIKI_ROLE_PARTNER_MANAGER_NAME =
		"Partner Manager";

	private static final String _KORONEIKI_ROLE_PARTNER_MARKETING_USER_NAME =
		"Partner Marketing User";

	private static final String _KORONEIKI_ROLE_PARTNER_MEMBER_NAME =
		"Partner Member";

	private static final String _KORONEIKI_ROLE_PARTNER_SALES_USER_NAME =
		"Partner Sales User";

	private static final String _KORONEIKI_ROLE_PARTNER_TECHNICAL_USER_NAME =
		"Partner Technical User";

	private static final String _ROLE_PARTNER_MANAGER_NAME =
		"Partner Manager (PM)";

	private static final String _ROLE_PARTNER_MARKETING_USER_NAME =
		"Partner Marketing User (PMU)";

	private static final String _ROLE_PARTNER_SALES_USER_NAME =
		"Partner Sales User (PSU)";

	private static final String _ROLE_PARTNER_TECHNICAL_USER_NAME =
		"Partner Technical User (PTU)";

	private static final Log _log = LogFactory.getLog(QueueListener.class);

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Value("${com.liferay.lxc.dxp.mainDomain}")
	private String _lxcDXPMainDomain;

	@Value("${com.liferay.lxc.dxp.server.protocol}")
	private String _lxcDXPServerProtocol;

}