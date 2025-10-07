/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.PostalAddressResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.CustomField;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.BillingAddress;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.BillingAddressResource;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderItemResource;
import com.liferay.headless.commerce.admin.order.client.resource.v1_0.OrderResource;
import com.liferay.marketplace.constants.MarketplaceConstants;
import com.liferay.marketplace.util.MarketplaceUtil;
import com.liferay.notification.rest.client.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.client.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.client.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.rest.client.resource.v1_0.NotificationTemplateResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * @author Keven Leone
 */
@Component
public class MarketplaceService extends BaseService {

	public void deployCloudService(JSONObject jsonObject, Order order)
		throws Exception {

		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		JSONArray cloudProvisioningJSONArray = new JSONArray(
			customFields.get("cloud-provisioning"));

		JSONObject cloudProvisioningJSONObject =
			_getCloudProvisioningJSONObject(
				cloudProvisioningJSONArray, jsonObject.getLong("orderItemId"));

		if (cloudProvisioningJSONObject.getLong("shippedQuantity") >=
				cloudProvisioningJSONObject.getLong("quantity")) {

			throw new Exception(
				"Unable to install app for order item " +
					cloudProvisioningJSONObject.getLong("orderItemId") +
						" because there are no available resources");
		}

		String projectId = jsonObject.getString("projectId");

		String temporaryDeploymentId =
			MarketplaceUtil.createTemporaryDeployment(
				customFields, cloudProvisioningJSONArray,
				cloudProvisioningJSONObject, projectId);

		updateOrder(customFields, order.getId(), order.getOrderStatus());

		try {
			JSONObject appJSONObject = _consoleService.deployApp(
				order.getCreatorEmailAddress(), String.valueOf(order.getId()),
				projectId);

			cloudProvisioningJSONObject.put(
				"deployments",
				cloudProvisioningJSONObject.getJSONArray(
					"deployments"
				).put(
					appJSONObject
				)
			).put(
				"shippedQuantity",
				cloudProvisioningJSONObject.getInt("shippedQuantity") + 1
			);
		}
		catch (Exception exception) {
			_log.error(exception);

			_log.error("Unable to install app for order " + order.getId());
		}

		MarketplaceUtil.deleteDeployment(
			temporaryDeploymentId, cloudProvisioningJSONObject);

		customFields.put(
			"cloud-provisioning", cloudProvisioningJSONArray.toString());

		updateOrder(customFields, order.getId(), order.getOrderStatus());
	}

	public AccountResource getAccountResource() throws Exception {
		return AccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public BillingAddress getBillingAddress(Long id) throws Exception {
		BillingAddressResource billingAddressResource =
			getBillingAddressResource();

		return billingAddressResource.getOrderIdBillingAddress(id);
	}

	public BillingAddressResource getBillingAddressResource() throws Exception {
		return BillingAddressResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public Catalog getCatalog(Long catalogId) throws Exception {
		CatalogResource catalogResource = _getCatalogResource();

		return catalogResource.getCatalog(catalogId);
	}

	public Order getOrder(Long id) throws Exception {
		OrderResource orderResource = getOrderResource();

		return orderResource.getOrder(id);
	}

	public OrderItemResource getOrderItemResource() throws Exception {
		return OrderItemResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public OrderResource getOrderResource() throws Exception {
		return OrderResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).parameters(
			"nestedFields", "account,billingAddress,orderItems"
		).build();
	}

	public PostalAddressResource getPostalAddressResource() throws Exception {
		return PostalAddressResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public Product getProduct(Long id) throws Exception {
		ProductResource productResource = getProductResource();

		return productResource.getProduct(id);
	}

	public Product getProductBySkuId(long skuId) throws Exception {
		Sku sku = getSku(skuId);

		return getProduct(sku.getProductId());
	}

	public ProductResource getProductResource() throws Exception {
		return ProductResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public Map<String, String> getProductSpecificationsMap(long productId)
		throws Exception {

		ProductSpecificationResource productSpecificationResource =
			_getProductSpecificationResource();

		Collection<ProductSpecification> productSpecifications =
			productSpecificationResource.getProductIdProductSpecificationsPage(
				productId, Pagination.of(1, 50)
			).getItems();

		Map<String, String> map = new HashMap<>();

		for (ProductSpecification productSpecification :
				productSpecifications) {

			map.put(
				productSpecification.getSpecificationKey(),
				productSpecification.getValue(
				).get(
					"en_US"
				));
		}

		return map;
	}

	public String getProductVersion(Long skuId) {
		String version = "1.0.0";

		try {
			Sku sku = getSku(skuId);

			for (CustomField customField : sku.getCustomFields()) {
				if (Objects.equals(customField.getName(), "Version")) {
					version = customField.getCustomValue(
					).getData(
					).toString();

					break;
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get product version " + exception.getMessage());
		}

		return version;
	}

	public Sku getSku(Long id) throws Exception {
		SkuResource skuResource = getSkuResource();

		return skuResource.getSku(id);
	}

	public String getSkuOptionValue(String key, SkuOption[] skuOptions) {
		for (SkuOption skuOption : skuOptions) {
			if (!Objects.equals(key, skuOption.getKey())) {
				continue;
			}

			String value = skuOption.getValue();

			String firstCharUpperCase = value.substring(
				0, 1
			).toUpperCase();

			return firstCharUpperCase + value.substring(1);
		}

		return null;
	}

	public String getSkuOptionValue(String key, String options) {
		JSONArray optionsJSONArray = new JSONArray(options);

		for (int i = 0; i < optionsJSONArray.length(); i++) {
			JSONObject jsonObject = optionsJSONArray.getJSONObject(i);

			if (!Objects.equals(key, jsonObject.getString("key"))) {
				continue;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("value");

			return jsonArray.getString(0);
		}

		return null;
	}

	public SkuResource getSkuResource() throws Exception {
		return SkuResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public UserAccount getUserAccount(String emailAddress) throws Exception {
		UserAccountResource userAccountResource = _getUserAccountResource();

		return userAccountResource.getUserAccountByEmailAddress(emailAddress);
	}

	public void postNotificationQueueEntry(
			String emailAddress, String externalReferenceCode,
			Map<String, String> map)
		throws Exception {

		String authorization =
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs");
		URL liferayDXPURL = new URL(
			lxcDXPServerProtocol + "://" + lxcDXPMainDomain);

		NotificationTemplateResource notificationTemplateResource =
			NotificationTemplateResource.builder(
			).endpoint(
				liferayDXPURL
			).header(
				org.apache.http.HttpHeaders.AUTHORIZATION, authorization
			).build();

		NotificationTemplate notificationTemplate;

		try {
			notificationTemplate =
				notificationTemplateResource.
					getNotificationTemplateByExternalReferenceCode(
						externalReferenceCode);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get notification template " + externalReferenceCode,
				exception);

			return;
		}

		NotificationQueueEntryResource notificationQueueEntryResource =
			NotificationQueueEntryResource.builder(
			).endpoint(
				liferayDXPURL
			).header(
				org.apache.http.HttpHeaders.AUTHORIZATION, authorization
			).build();

		NotificationQueueEntry notificationQueueEntry =
			new NotificationQueueEntry();

		notificationQueueEntry.setBody(
			() -> _replace(
				notificationTemplate.getBody(
				).get(
					"en_US"
				),
				map));

		JSONArray jsonArray = new JSONObject(
			String.valueOf(notificationTemplate)
		).getJSONArray(
			"recipients"
		);

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		notificationQueueEntry.setRecipients(
			() -> new Object[] {
				new HashMapBuilder<String, Object>().put(
					"cc", jsonObject.getString("cc")
				).put(
					"ccType", jsonObject.getString("ccType")
				).put(
					"from", jsonObject.getString("from")
				).put(
					"fromName",
					jsonObject.getJSONObject(
						"fromName"
					).getString(
						"en_US"
					)
				).put(
					"to",
					() -> {
						if (Validator.isNotNull(emailAddress)) {
							return emailAddress;
						}

						return jsonObject.getJSONObject(
							"to"
						).getString(
							"en_US"
						);
					}
				).build()
			});

		notificationQueueEntry.setSubject(
			() -> _replace(
				notificationTemplate.getSubject(
				).get(
					"en_US"
				),
				map));
		notificationQueueEntry.setType(notificationTemplate::getType);

		notificationQueueEntryResource.postNotificationQueueEntry(
			notificationQueueEntry);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Sent ", externalReferenceCode, " notification to ",
					emailAddress));
		}
	}

	public void updateOrder(
			Map<String, ?> customFields, long orderId, int orderStatus)
		throws Exception {

		Order order = new Order();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Updating status for order ", orderId, " to ",
					MarketplaceConstants.getOrderStatusLabel(orderStatus)));
		}

		order.setCustomFields(() -> customFields);
		order.setOrderStatus(() -> orderStatus);

		OrderResource orderResource = getOrderResource();

		orderResource.patchOrder(orderId, order);
	}

	private CatalogResource _getCatalogResource() throws Exception {
		return CatalogResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private JSONObject _getCloudProvisioningJSONObject(
		JSONArray jsonArray, long orderItemId) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (Objects.equals(
					jsonObject.getLong("orderItemId"), orderItemId)) {

				return jsonObject;
			}
		}

		return new JSONObject();
	}

	private ProductSpecificationResource _getProductSpecificationResource()
		throws Exception {

		return ProductSpecificationResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private UserAccountResource _getUserAccountResource() throws Exception {
		return UserAccountResource.builder(
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).header(
			org.apache.http.HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).build();
	}

	private String _replace(String string, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			string = StringUtil.replace(
				string, entry.getKey(), entry.getValue());
		}

		return string;
	}

	private static final Log _log = LogFactory.getLog(MarketplaceService.class);

	@Autowired
	private ConsoleService _consoleService;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}