/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.headless.admin.address.client.dto.v1_0.Country;
import com.liferay.headless.admin.address.client.resource.v1_0.CountryResource;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountGroupResource;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.client.resource.v1_0.PostalAddressResource;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.commerce.admin.catalog.client.custom.field.CustomField;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Product;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.AttachmentResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CatalogResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.CurrencyResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductVirtualSettingsFileEntryResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductVirtualSettingsResource;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.BillingAddress;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.file.Files;

import java.util.Base64;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Keven Leone
 */
@Component
public class MarketplaceService extends BaseService {

	public void completeOrder(long orderId, int paymentStatus)
		throws Exception {

		completeOrder(null, orderId, paymentStatus);
	}

	public void completeOrder(
			Map<String, ?> customFields, long orderId, int paymentStatus)
		throws Exception {

		updateOrder(
			customFields, orderId, MarketplaceConstants.ORDER_STATUS_PENDING);

		updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_PROCESSING);

		updateOrder(
			null, orderId, MarketplaceConstants.ORDER_STATUS_COMPLETED,
			paymentStatus);
	}

	public void deployCloudService(JSONObject jsonObject, Order order)
		throws Exception {

		Map<String, String> customFields =
			(Map<String, String>)order.getCustomFields();

		JSONArray cloudProvisioningJSONArray = null;

		String cloudProvisioningJSON = customFields.get("cloud-provisioning");

		if (cloudProvisioningJSON != null) {
			cloudProvisioningJSONArray = new JSONArray(cloudProvisioningJSON);
		}
		else {
			cloudProvisioningJSONArray =
				MarketplaceUtil.createCloudProvisioningJSONArray(
					order.getOrderItems());
		}

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

	public JSONObject getAIHubApplicationJSONObject(
		String externalReferenceCode) {

		return new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				UriComponentsBuilder.fromPath(
					"/o/c/aihubapplications/by-external-reference-code/" +
						externalReferenceCode +
							"?nestedFields=orderToAIHubApplication"
				).build(
				).toUri()));
	}

	public AccountGroupResource getAccountGroupResource() throws Exception {
		return AccountGroupResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public AccountResource getAccountResource() throws Exception {
		return AccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).parameters(
			"nestedFields", "postalAddresses"
		).build();
	}

	public AccountRoleResource getAccountRoleResource() throws Exception {
		return AccountRoleResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public AttachmentResource getAttachmentResource() throws Exception {
		return AttachmentResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public BillingAddress getBillingAddress(long id) throws Exception {
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

	public Catalog getCatalog(long catalogId) throws Exception {
		CatalogResource catalogResource = _getCatalogResource();

		return catalogResource.getCatalog(catalogId);
	}

	public ChannelResource getChannelResource() throws Exception {
		return ChannelResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public Country getCountryByA2(String a2) throws Exception {
		CountryResource countryResource = _getCountryResource();

		return countryResource.getCountryByA2(a2);
	}

	public CurrencyResource getCurrencyResource() throws Exception {
		return CurrencyResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	public UserAccount getMyUserAccount(Jwt jwt) throws Exception {
		UserAccountResource userAccountResource = UserAccountResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue()
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();

		return userAccountResource.getMyUserAccount();
	}

	public Order getOrder(long id) throws Exception {
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

	public Long getOrderProductId(Order order) throws Exception {
		OrderItem[] orderItems = order.getOrderItems();

		OrderItem orderItem = orderItems[0];

		if (orderItem == null) {
			return null;
		}

		Sku sku = getSku(orderItem.getSkuId());

		return sku.getProductId();
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
			"nestedFields", "account,billingAddress,orderItems,shippingAddress"
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

	public Product getProduct(long id) throws Exception {
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
		).parameters(
			"nestedFields", "catalog"
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

	public String getProductThumbnail(Product product) throws Exception {
		return new URL(
			StringBundler.concat(
				lxcDXPServerProtocol, "://", lxcDXPMainDomain,
				product.getThumbnail())
		).toString(
		).replaceAll(
			"(?<=accounts/)-?\\d+(?=/images)", "-1"
		);
	}

	public String getProductVersion(long skuId) {
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

	public ProductVirtualSettings getProductVirtualSettings(long productId)
		throws Exception {

		ProductVirtualSettingsResource productVirtualSettingsResource =
			_getProductVirtualSettingsResource();

		return productVirtualSettingsResource.
			getProductIdProductVirtualSettings(productId);
	}

	public InputStream getPublisherAssetInputStream(String publisherAssetURL)
		throws Exception {

		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest httpRequest = HttpRequest.newBuilder(
		).uri(
			URI.create(
				StringBundler.concat(
					lxcDXPServerProtocol, "://", lxcDXPMainDomain,
					publisherAssetURL))
		).header(
			"Authorization",
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).GET(
		).build();

		HttpResponse<InputStream> httpResponse = httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofInputStream());

		if (httpResponse.statusCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
			throw new IOException(
				StringBundler.concat(
					"Unable to download ", publisherAssetURL, ": ",
					httpResponse.statusCode()));
		}

		return httpResponse.body();
	}

	public JSONObject getPublisherAssetsJSONObject(long productId)
		throws Exception {

		return new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				UriComponentsBuilder.fromPath(
					"/o/c/publisherassetses"
				).queryParam(
					"filter",
					"r_productEntryToPublisherAssets_CPDefinitionId eq '" +
						productId + "'"
				).queryParam(
					"pageSize", 20
				).queryParam(
					"nestedFields", "publisherAssetsToAttachment"
				).build(
				).toUri()));
	}

	public Sku getSku(long id) throws Exception {
		SkuResource skuResource = getSkuResource();

		return skuResource.getSku(id);
	}

	public Sku getSku(String externalReferenceCode) throws Exception {
		SkuResource skuResource = getSkuResource();

		return skuResource.getSkuByExternalReferenceCode(externalReferenceCode);
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
		UserAccountResource userAccountResource = getUserAccountResource();

		return userAccountResource.getUserAccountByEmailAddress(emailAddress);
	}

	public UserAccountResource getUserAccountResource() throws Exception {
		return UserAccountResource.builder(
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).header(
			org.apache.http.HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).build();
	}

	public Page<UserAccount> getUserAccountsPage(
			String filterString,
			com.liferay.headless.admin.user.client.pagination.Pagination
				pagination,
			String searchString, String sortString)
		throws Exception {

		UserAccountResource userAccountResource = getUserAccountResource();

		return userAccountResource.getUserAccountsPage(
			searchString, filterString, pagination, sortString);
	}

	public void patchPublisherAssetAttachment(String body, long id)
		throws Exception {

		patch(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs"),
			body,
			UriComponentsBuilder.fromPath(
				"/o/c/publisherassetattachments/" + id
			).build(
			).toUri());
	}

	public void postAccountUserAccountByEmailAddress(
			Long accountId, String emailAddress)
		throws Exception {

		UserAccountResource userAccountResource = getUserAccountResource();

		userAccountResource.postAccountUserAccountByEmailAddress(
			accountId, emailAddress);
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

		if (Validator.isNull(emailAddress)) {
			emailAddress = _replace(
				jsonObject.getJSONObject(
					"to"
				).getString(
					"en_US"
				),
				map);
		}

		String finalEmailAddress = emailAddress;

		notificationQueueEntry.setRecipients(
			() -> new Object[] {
				new HashMapBuilder<String, Object>().put(
					"cc", jsonObject.getString("cc")
				).put(
					"ccType", jsonObject.optString("ccType")
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
					"to", finalEmailAddress
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

		try {
			notificationQueueEntryResource.postNotificationQueueEntry(
				notificationQueueEntry);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Sent ", externalReferenceCode, " notification to ",
						finalEmailAddress));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to post notification queue entry " +
					externalReferenceCode,
				exception);
		}
	}

	public void postProductAttachment(
			File file, String fileName, long productId)
		throws Exception {

		AttachmentResource attachmentResource = getAttachmentResource();

		attachmentResource.postProductIdAttachmentByBase64(
			productId,
			new AttachmentBase64() {
				{
					setAttachment(
						() -> Base64.getEncoder(
						).encodeToString(
							Files.readAllBytes(file.toPath())
						));
					setContentType(() -> "application/zip");
					setTitle(
						() -> HashMapBuilder.put(
							"en_US", fileName
						).build());
				}
			});
	}

	public void postUserAccount(UserAccount userAccount) throws Exception {
		UserAccountResource userAccountResource = getUserAccountResource();

		userAccountResource.postUserAccount(userAccount);
	}

	public HttpInvoker.HttpResponse postUserAccountHttpResponse(
			UserAccount userAccount)
		throws Exception {

		UserAccountResource userAccountResource = getUserAccountResource();

		return userAccountResource.postUserAccountHttpResponse(userAccount);
	}

	public void postVirtualFileEntry(File file, long productId, String version)
		throws Exception {

		ProductVirtualSettingsFileEntryResource
			productVirtualSettingsFileEntryResource =
				_getProductVirtualSettingsFileEntryResource();

		ProductVirtualSettings productVirtualSettings =
			getProductVirtualSettings(productId);

		productVirtualSettingsFileEntryResource.
			postProductVirtualSettingIdProductVirtualSettingsFileEntry(
				productVirtualSettings.getId(),
				ProductVirtualSettingsFileEntry.toDTO(
					new JSONObject(
					).put(
						"version", version
					).toString()),
				HashMapBuilder.put(
					"file", file
				).build());
	}

	public JSONObject putAIHubApplication(
		String externalReferenceCode, JSONObject jsonObject) {

		return new JSONObject(
			put(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				jsonObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/aihubapplications/by-external-reference-code/" +
						externalReferenceCode
				).build(
				).toUri()));
	}

	public JSONObject putSalesforceProject(
		String externalReferenceCode, JSONObject jsonObject) {

		return new JSONObject(
			put(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-marketplace-etc-spring-boot-oahs"),
				jsonObject.toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/salesforceprojects/by-external-reference-code/" +
						externalReferenceCode
				).build(
				).toUri()));
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

	public void updateOrder(
			Map<String, ?> customFields, long orderId, int orderStatus,
			int paymentStatus)
		throws Exception {

		OrderResource orderResource = getOrderResource();

		Order order = new Order();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Updating status for order ", orderId, " to ",
					MarketplaceConstants.getOrderStatusLabel(orderStatus),
					" and payment status to ",
					MarketplaceConstants.getOrderPaymentStatusLabel(
						paymentStatus)));
		}

		order.setCustomFields(() -> customFields);
		order.setOrderStatus(() -> orderStatus);
		order.setPaymentStatus(() -> paymentStatus);

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

	private CountryResource _getCountryResource() throws Exception {
		return CountryResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
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

	private ProductVirtualSettingsFileEntryResource
			_getProductVirtualSettingsFileEntryResource()
		throws Exception {

		return ProductVirtualSettingsFileEntryResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
		).build();
	}

	private ProductVirtualSettingsResource _getProductVirtualSettingsResource()
		throws Exception {

		return ProductVirtualSettingsResource.builder(
		).header(
			HttpHeaders.AUTHORIZATION,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-marketplace-etc-spring-boot-oahs")
		).endpoint(
			new URL(lxcDXPServerProtocol + "://" + lxcDXPMainDomain)
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