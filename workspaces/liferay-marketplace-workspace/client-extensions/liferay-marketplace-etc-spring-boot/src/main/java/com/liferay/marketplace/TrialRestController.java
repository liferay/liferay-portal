/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.portal.instances.client.dto.v1_0.Admin;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.pagination.Page;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceResource;
import com.liferay.marketplace.constants.MarketplaceConstants;
import com.liferay.marketplace.service.ConsoleService;
import com.liferay.marketplace.service.MarketplaceService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.net.URI;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Keven Leone
 */
@RequestMapping("/trial")
@RestController
public class TrialRestController extends BaseRestController {

	@DeleteMapping("{orderId}")
	public void delete(@PathVariable long orderId) throws Exception {
		Order order = _marketplaceService.getOrder(orderId);

		JSONObject trialProvisioningContextJSONObject =
			_getTrialProvisioningContextJSONObject(order);

		_consoleService.deleteProject(
			trialProvisioningContextJSONObject.getString("projectId"));

		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		_deletePortalInstance(
			orderId, trialProvisioningContextJSONObject,
			customFields.get("trial-virtual-host"));
	}

	@GetMapping("availability")
	public String getAvailability(
			@RequestParam(defaultValue = "SOLUTIONS7", required = false) String
				orderTypeExternalReferenceCode)
		throws Exception {

		Page<PortalInstance> page = _getPortalInstancesPage(
			_getTrialProvisioningContextJSONObject(
				_getOrder(orderTypeExternalReferenceCode)));

		return new JSONObject(
		).put(
			"active", _TRIAL_MAX_INSTANCES > page.getTotalCount()
		).put(
			"available", _TRIAL_MAX_INSTANCES - page.getTotalCount()
		).put(
			"max", _TRIAL_MAX_INSTANCES
		).toString();
	}

	@GetMapping("domain-availability/{projectPrefix}")
	public ResponseEntity<Void> getDomainAvailability(
			@PathVariable String projectPrefix,
			@RequestParam(defaultValue = "SSA_SAAS", required = false) String
				orderTypeExternalReferenceCode)
		throws Exception {

		JSONObject jsonObject = _getTrialProvisioningContextJSONObject(
			_getOrder(orderTypeExternalReferenceCode));

		String virtualHost =
			projectPrefix + "." + jsonObject.getString("domain");

		Page<PortalInstance> portalInstancePage = _getPortalInstancesPage(
			jsonObject);

		for (PortalInstance portalInstance : portalInstancePage.getItems()) {
			if (Objects.equals(virtualHost, portalInstance.getVirtualHost())) {
				return ResponseEntity.status(
					HttpStatus.CONFLICT
				).build();
			}
		}

		return ResponseEntity.status(
			HttpStatus.OK
		).build();
	}

	@PostMapping("expire/{orderId}")
	public void postExpire(@PathVariable long orderId) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Expired trial " + orderId);
		}

		_marketplaceService.updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_PENDING);

		_marketplaceService.updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_PROCESSING);

		_marketplaceService.updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_COMPLETED);

		delete(orderId);
	}

	@PostMapping("extend/{id}")
	public void postExtend(@PathVariable long id) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Extend trial " + id);
		}

		JSONObject trialExtensionRequestJSONObject = new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				UriComponentsBuilder.fromPath(
					"/o/c/trialextensionrequests/" + id
				).build(
				).toUri()));

		JSONObject dueStatusJSONObject =
			trialExtensionRequestJSONObject.getJSONObject("dueStatus");

		if (!(Objects.equals(
				dueStatusJSONObject.getString("key"), "Approved") ||
			  Objects.equals(
				  dueStatusJSONObject.getString("key"), "AutoApproved"))) {

			return;
		}

		Order order = _marketplaceService.getOrder(
			trialExtensionRequestJSONObject.getLong(
				"r_orderToTrialExtensionRequest_commerceOrderId"));

		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		ZonedDateTime trialEndDateZonedDateTime = ZonedDateTime.parse(
			customFields.get("trial-end-date")
		).plusDays(
			trialExtensionRequestJSONObject.getInt("duration")
		);

		customFields.put(
			"trial-end-date",
			trialEndDateZonedDateTime.format(DateTimeFormatter.ISO_INSTANT));

		if (Objects.equals(dueStatusJSONObject.getString("key"), "Pending")) {
			patch(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				new JSONObject(
				).put(
					"dueStatus", "Approved"
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/trialextensionrequests/" + id
				).build(
				).toUri());
		}

		_marketplaceService.updateOrder(
			customFields, order.getId(), order.getOrderStatus());
	}

	@PostMapping("notify-end/{orderId}")
	public void postNotifyEnd(@PathVariable long orderId) throws Exception {
		if (_log.isInfoEnabled()) {
			_log.info("Notify end " + orderId);
		}

		Order order = _marketplaceService.getOrder(orderId);

		UserAccount userAccount = _marketplaceService.getUserAccount(
			order.getCreatorEmailAddress());
		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		_marketplaceService.postNotificationQueueEntry(
			order.getCreatorEmailAddress(), "TRIAL-EXPIRING-ORDER",
			new HashMapBuilder<String, Object>().put(
				"%TRIAL_CREATOR_FIRST_NAME%", userAccount.getGivenName()
			).put(
				"%TRIAL_END_DATE%",
				ZonedDateTime.parse(
					customFields.get("trial-end-date")
				).format(
					DateTimeFormatter.ofPattern("MMMM d, yyyy")
				)
			).build());

		customFields.put(
			"trial-notify-end-date",
			ZonedDateTime.now(
			).format(
				DateTimeFormatter.ISO_INSTANT
			));

		_marketplaceService.updateOrder(
			customFields, orderId, order.getOrderStatus());
	}

	@PostMapping("provisioning")
	public void postProvisioning(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		JSONObject jsonObject = new JSONObject(json);

		long orderId = jsonObject.getLong("classPK");

		if (_log.isInfoEnabled()) {
			_log.info("Provisioning order " + orderId);
		}

		Order order = _marketplaceService.getOrder(orderId);

		JSONObject trialProvisioningContextJSONObject =
			_getTrialProvisioningContextJSONObject(order);

		Page<PortalInstance> portalInstancesPage = _getPortalInstancesPage(
			trialProvisioningContextJSONObject);

		if (portalInstancesPage.getTotalCount() == _TRIAL_MAX_INSTANCES) {
			_log.error("Order is on hold");

			_marketplaceService.updateOrder(
				null, orderId, MarketplaceConstants.ORDER_STATUS_ON_HOLD);

			return;
		}

		JSONObject modelDTOOrderJSONObject = jsonObject.getJSONObject(
			"modelDTOOrder");

		if (modelDTOOrderJSONObject.getInt("orderStatus") ==
				MarketplaceConstants.ORDER_STATUS_OPEN) {

			_marketplaceService.updateOrder(
				null, orderId, MarketplaceConstants.ORDER_STATUS_PENDING);
		}

		_marketplaceService.updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_PROCESSING);

		UserAccount userAccount = _marketplaceService.getUserAccount(
			order.getCreatorEmailAddress());

		JSONObject trialSettingsJSONObject = _getTrialSettingsJSONObject(order);

		boolean sendNotificationEmail = trialSettingsJSONObject.optBoolean(
			"sendNotificationEmail", true);

		if (sendNotificationEmail) {
			_marketplaceService.postNotificationQueueEntry(
				order.getCreatorEmailAddress(), "TRIAL-PROCESSING-ORDER",
				new HashMapBuilder<String, Object>().put(
					"[%COMMERCEORDER_AUTHOR_FIRST_NAME%]",
					userAccount.getGivenName()
				).put(
					"[%COMMERCEORDER_ID%]", String.valueOf(orderId)
				).build());
		}

		PortalInstance portalInstance = null;

		try {
			portalInstance = _postPortalInstance(
				jwt, order.getCreatorEmailAddress(),
				trialSettingsJSONObject.optString(
					"projectId", String.valueOf(orderId)),
				trialSettingsJSONObject.optString("siteInitializerKey", null),
				trialProvisioningContextJSONObject);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to provision portal instance for order " + orderId,
				exception);

			_marketplaceService.updateOrder(
				null, orderId, MarketplaceConstants.ORDER_STATUS_CANCELLED);

			throw exception;
		}

		try {
			_consoleService.setUpProject(
				trialProvisioningContextJSONObject.getString("cluster"),
				trialProvisioningContextJSONObject.getBoolean("deployable"),
				trialProvisioningContextJSONObject.getString("dxpProjectUid"),
				portalInstance.getVirtualHost(),
				_toStringArray(
					trialSettingsJSONObject.optJSONArray(
						"consoleInviteEmailAddresses", new JSONArray())),
				orderId,
				trialProvisioningContextJSONObject.getString("projectId"));

			_marketplaceService.updateOrder(
				HashMapBuilder.put(
					"trial-end-date",
					ZonedDateTime.now(
					).plusDays(
						trialSettingsJSONObject.optInt("duration", 7)
					).format(
						DateTimeFormatter.ISO_INSTANT
					)
				).put(
					"trial-start-date",
					ZonedDateTime.now(
					).format(
						DateTimeFormatter.ISO_INSTANT
					)
				).put(
					"trial-virtual-host", portalInstance.getVirtualHost()
				).build(),
				orderId, MarketplaceConstants.ORDER_STATUS_IN_PROGRESS);

			if (sendNotificationEmail) {
				_marketplaceService.postNotificationQueueEntry(
					order.getCreatorEmailAddress(), "TRIAL-COMPLETED-ORDER",
					new HashMapBuilder<String, Object>().put(
						"%EMAIL%", order.getCreatorEmailAddress()
					).put(
						"%NAME%", userAccount.getGivenName()
					).put(
						"%URL%", portalInstance.getVirtualHost()
					).build());
			}
		}
		catch (WebClientResponseException webClientResponseException) {
			_rollBackTrial(
				webClientResponseException.getResponseBodyAsString(), orderId,
				portalInstance, trialProvisioningContextJSONObject);
		}
		catch (Exception exception) {
			_rollBackTrial(
				exception.getMessage(), orderId, portalInstance,
				trialProvisioningContextJSONObject);
		}
	}

	@PostMapping("provisioning/{orderId}")
	public void postProvisioningOrder(
			@AuthenticationPrincipal Jwt jwt, @PathVariable long orderId)
		throws Exception {

		Order order = _marketplaceService.getOrder(orderId);

		postProvisioning(
			jwt,
			new JSONObject(
			).put(
				"classPK", orderId
			).put(
				"modelDTOOrder",
				new JSONObject(
				).put(
					"accountId", String.valueOf(order.getAccountId())
				).put(
					"creatorEmailAddress", order.getCreatorEmailAddress()
				).put(
					"orderStatus", order.getOrderStatus()
				)
			).toString());
	}

	private void _deletePortalInstance(
			long orderId, JSONObject trialProvisioningContextJSONObject,
			String virtualHost)
		throws Exception {

		PortalInstanceResource portalInstanceResource =
			_getPortalInstanceResource(trialProvisioningContextJSONObject);

		Page<PortalInstance> page =
			portalInstanceResource.getPortalInstancesPage(true);

		for (PortalInstance portalInstance : page.getItems()) {
			if (Objects.equals(portalInstance.getVirtualHost(), virtualHost)) {
				portalInstanceResource.deletePortalInstance(
					portalInstance.getPortalInstanceId());

				break;
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Portal instance deleted for order " + orderId);
		}
	}

	private Order _getOrder(String orderTypeExternalReferenceCode) {
		Order order = new Order();

		order.setCustomFields(() -> new HashMap<>());
		order.setOrderTypeExternalReferenceCode(
			() -> orderTypeExternalReferenceCode);

		return order;
	}

	private PortalInstanceResource _getPortalInstanceResource(
			JSONObject trialProvisioningContextJSONObject)
		throws Exception {

		return PortalInstanceResource.builder(
		).endpoint(
			new URI(
				trialProvisioningContextJSONObject.getString("trialHomePageURL")
			).toURL()
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				trialProvisioningContextJSONObject.getString(
					"trialAuthorizationERC"))
		).build();
	}

	private Page<PortalInstance> _getPortalInstancesPage(
			JSONObject trialProvisioningContextJSONObject)
		throws Exception {

		PortalInstanceResource portalInstanceResource =
			_getPortalInstanceResource(trialProvisioningContextJSONObject);

		return portalInstanceResource.getPortalInstancesPage(true);
	}

	private JSONObject _getTrialProvisioningContextJSONObject(Order order) {
		JSONObject trialSettingsJSONObject = _getTrialSettingsJSONObject(order);

		String projectId = trialSettingsJSONObject.optString(
			"projectId", String.valueOf(order.getId()));

		if (Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "SOLUTIONS7")) {

			return new JSONObject(
			).put(
				"cluster", _consoleTrialCluster
			).put(
				"deployable", true
			).put(
				"domain", _trialDXPDomain
			).put(
				"dxpProjectUid", _consoleTrialProjectUid
			).put(
				"projectId", _consoleTrialProjectPrefix + "-ext" + projectId
			).put(
				"trialAuthorizationERC", "external-trial"
			).put(
				"trialHomePageURL", _externalTrialHomePageURL
			);
		}

		if (Objects.equals(
				order.getOrderTypeExternalReferenceCode(), "SSA_SAAS")) {

			return new JSONObject(
			).put(
				"cluster", _consoleSSACluster
			).put(
				"deployable", false
			).put(
				"domain", _trialSSADXPDomain
			).put(
				"dxpProjectUid", _consoleSSAProjectUid
			).put(
				"projectId", _consoleSSAProjectPrefix + "-ext" + projectId
			).put(
				"trialAuthorizationERC", "external-ssa"
			).put(
				"trialHomePageURL", _externalSSAHomePageURL
			);
		}

		throw new IllegalArgumentException(
			"Unsupported order type: " +
				order.getOrderTypeExternalReferenceCode());
	}

	private JSONObject _getTrialSettingsJSONObject(Order order) {
		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		return new JSONObject(
			customFields.getOrDefault("trial-settings", "{}"));
	}

	private PortalInstance _postPortalInstance(
			Jwt jwt, String emailAddress, String projectId,
			String siteInitializerKey,
			JSONObject trialProvisioningContextJSONObject)
		throws Exception {

		PortalInstanceResource portalInstanceResource =
			_getPortalInstanceResource(trialProvisioningContextJSONObject);

		PortalInstance portalInstance = new PortalInstance();

		Admin admin = new Admin();

		admin.setEmailAddress(() -> emailAddress);
		admin.setFamilyName(
			() -> jwt.getClaim(
				"username"
			).toString());
		admin.setGivenName(
			() -> jwt.getClaim(
				"username"
			).toString());

		portalInstance.setAdmin(() -> admin);

		portalInstance.setDomain(() -> "lxc.app");
		portalInstance.setSiteInitializerKey(() -> siteInitializerKey);

		String domain =
			projectId + "." +
				trialProvisioningContextJSONObject.getString("domain");

		portalInstance.setPortalInstanceId(() -> domain);
		portalInstance.setVirtualHost(() -> domain);

		portalInstance = portalInstanceResource.postPortalInstance(
			portalInstance);

		if (_log.isInfoEnabled()) {
			_log.info("Created portal instance " + portalInstance);
		}

		return portalInstance;
	}

	private void _rollBackTrial(
			String errorMessage, long orderId, PortalInstance portalInstance,
			JSONObject trialProvisioningContextJSONObject)
		throws Exception {

		_log.error(
			StringBundler.concat(
				"Unable to set up project for order ", orderId, ": \n",
				errorMessage));

		_deletePortalInstance(
			orderId, trialProvisioningContextJSONObject,
			portalInstance.getVirtualHost());

		_marketplaceService.updateOrder(
			HashMapBuilder.put(
				"trial-error", errorMessage
			).put(
				"trial-error-date",
				ZonedDateTime.now(
				).format(
					DateTimeFormatter.ISO_INSTANT
				)
			).put(
				"trial-virtual-host", portalInstance.getVirtualHost()
			).build(),
			orderId, MarketplaceConstants.ORDER_STATUS_CANCELLED);
	}

	private String[] _toStringArray(JSONArray jsonArray) {
		List<String> list = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}

		return list.toArray(new String[0]);
	}

	private static final int _TRIAL_MAX_INSTANCES = GetterUtil.getInteger(
		System.getenv(
			"LIFERAY_MARKETPLACE_ETC_SPRING_BOOT_TRIAL_MAX_INSTANCES"),
		50);

	private static final Log _log = LogFactory.getLog(
		TrialRestController.class);

	@Autowired
	private ConsoleService _consoleService;

	@Value("${liferay.marketplace.console.ssa.cluster}")
	private String _consoleSSACluster;

	@Value("${liferay.marketplace.console.ssa.project.prefix}")
	private String _consoleSSAProjectPrefix;

	@Value("${liferay.marketplace.console.ssa.project.uid}")
	private String _consoleSSAProjectUid;

	@Value("${liferay.marketplace.console.cluster}")
	private String _consoleTrialCluster;

	@Value("${liferay.marketplace.console.project.prefix}")
	private String _consoleTrialProjectPrefix;

	@Value("${liferay.marketplace.console.project.uid}")
	private String _consoleTrialProjectUid;

	@Value("${external.ssa.oauth2.headless.server.home.page.url}")
	private String _externalSSAHomePageURL;

	@Value("${external.trial.oauth2.headless.server.home.page.url}")
	private String _externalTrialHomePageURL;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	@Autowired
	private MarketplaceService _marketplaceService;

	@Value("${liferay.marketplace.trial.dxp.domain}")
	private String _trialDXPDomain;

	@Value("${liferay.marketplace.trial.ssa.dxp.domain}")
	private String _trialSSADXPDomain;

}