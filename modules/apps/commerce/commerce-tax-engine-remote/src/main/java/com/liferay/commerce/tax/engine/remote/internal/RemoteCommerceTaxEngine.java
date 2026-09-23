/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.remote.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.commerce.exception.CommerceTaxEngineException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.tax.CommerceTaxCalculateRequest;
import com.liferay.commerce.tax.CommerceTaxEngine;
import com.liferay.commerce.tax.CommerceTaxValue;
import com.liferay.commerce.tax.engine.remote.internal.configuration.RemoteCommerceTaxConfiguration;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.IOException;

import java.math.BigDecimal;

import java.net.URISyntaxException;

import java.nio.charset.StandardCharsets;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	property = "commerce.tax.engine.key=" + RemoteCommerceTaxEngine.KEY,
	service = CommerceTaxEngine.class
)
public class RemoteCommerceTaxEngine implements CommerceTaxEngine {

	public static final String KEY = "remote";

	@Override
	public CommerceTaxValue getCommerceTaxValue(
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws CommerceTaxEngineException {

		try (CloseableHttpResponse closeableHttpResponse =
				_closeableHttpClient.execute(
					_getHttpGet(commerceTaxCalculateRequest))) {

			if (_log.isTraceEnabled()) {
				StatusLine statusLine = closeableHttpResponse.getStatusLine();

				_log.trace(
					"Server returned status " + statusLine.getStatusCode());
			}

			return _getCommerceTaxValue(
				EntityUtils.toString(
					closeableHttpResponse.getEntity(), StandardCharsets.UTF_8));
		}
		catch (Exception exception) {
			throw new CommerceTaxEngineException(exception);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		return _language.get(_getResourceBundle(locale), "remote-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(_getResourceBundle(locale), KEY);
	}

	@Activate
	protected void activate() {
		_poolingHttpClientConnectionManager =
			new PoolingHttpClientConnectionManager();

		_poolingHttpClientConnectionManager.setMaxTotal(20);
		_poolingHttpClientConnectionManager.setValidateAfterInactivity(30000);

		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		httpClientBuilder.setConnectionManager(
			_poolingHttpClientConnectionManager);

		httpClientBuilder.useSystemProperties();

		_closeableHttpClient = httpClientBuilder.build();
	}

	@Deactivate
	protected void deactivate() {
		if (_closeableHttpClient != null) {
			try {
				_closeableHttpClient.close();
			}
			catch (IOException ioException) {
				_log.error("Unable to close client", ioException);
			}

			_closeableHttpClient = null;
		}

		if (_poolingHttpClientConnectionManager != null) {
			_poolingHttpClientConnectionManager.close();

			_poolingHttpClientConnectionManager = null;
		}
	}

	protected CommerceAddress getCommerceAddress(long commerceAddressId)
		throws PortalException {

		return _commerceAddressService.getCommerceAddress(commerceAddressId);
	}

	protected CommerceTaxMethod getCommerceTaxMethod(long commerceTaxMethodId)
		throws PortalException {

		return _commerceTaxMethodService.getCommerceTaxMethod(
			commerceTaxMethodId);
	}

	protected RemoteCommerceTaxConfiguration getRemoteCommerceTaxConfiguration(
			long commerceChannelGroupId)
		throws CommerceTaxEngineException {

		try {
			return _configurationProvider.getConfiguration(
				RemoteCommerceTaxConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannelGroupId,
					RemoteCommerceTaxConfiguration.class.getName()));
		}
		catch (ConfigurationException configurationException) {
			throw new CommerceTaxEngineException(configurationException);
		}
	}

	private void _addCommerceAddressParameters(
			long commerceAddressId, String prefix, URIBuilder uriBuilder)
		throws PortalException {

		CommerceAddress commerceAddress = getCommerceAddress(commerceAddressId);

		_addParameter(
			prefix + "AddressCity", commerceAddress.getCity(), uriBuilder);

		Country country = commerceAddress.getCountry();

		_addParameter(
			prefix + "AddressCountryISOCode", country.getA3(), uriBuilder);

		_addParameter(
			prefix + "AddressExternalReferenceCode",
			commerceAddress.getExternalReferenceCode(), uriBuilder);
		_addParameter(
			prefix + "AddressId", String.valueOf(commerceAddressId),
			uriBuilder);
		_addParameter(
			prefix + "AddressLatitude",
			String.valueOf(commerceAddress.getLatitude()), uriBuilder);
		_addParameter(
			prefix + "AddressLongitude",
			String.valueOf(commerceAddress.getLongitude()), uriBuilder);
		_addParameter(
			prefix + "AddressPhoneNumber", commerceAddress.getPhoneNumber(),
			uriBuilder);

		Region region = commerceAddress.getRegion();

		_addParameter(
			prefix + "AddressRegionISOCode", region.getRegionCode(),
			uriBuilder);

		_addParameter(
			prefix + "AddressStreet1", commerceAddress.getStreet1(),
			uriBuilder);
		_addParameter(
			prefix + "AddressStreet2", commerceAddress.getStreet2(),
			uriBuilder);
		_addParameter(
			prefix + "AddressStreet3", commerceAddress.getStreet3(),
			uriBuilder);
		_addParameter(
			prefix + "AddressType", String.valueOf(commerceAddress.getType()),
			uriBuilder);
		_addParameter(
			prefix + "AddressZip", commerceAddress.getZip(), uriBuilder);
	}

	private void _addParameter(
		String parameterName, String parameterValue, URIBuilder uriBuilder) {

		if (Validator.isNotNull(parameterValue)) {
			uriBuilder.addParameter(parameterName, parameterValue);
		}
	}

	private CommerceTaxValue _getCommerceTaxValue(String result)
		throws IOException {

		JsonNode jsonNode = _objectMapper.readTree(result);

		JsonNode nameJsonNode = jsonNode.get("name");
		JsonNode labelJsonNode = jsonNode.get("label");
		JsonNode amountJsonNode = jsonNode.get("amount");

		return new CommerceTaxValue(
			nameJsonNode.textValue(), labelJsonNode.textValue(),
			BigDecimal.valueOf(amountJsonNode.doubleValue()));
	}

	private HttpGet _getHttpGet(
			CommerceTaxCalculateRequest commerceTaxCalculateRequest)
		throws PortalException, URISyntaxException {

		RemoteCommerceTaxConfiguration remoteCommerceTaxConfiguration =
			getRemoteCommerceTaxConfiguration(
				commerceTaxCalculateRequest.getCommerceChannelGroupId());

		URIBuilder uriBuilder = new URIBuilder(
			remoteCommerceTaxConfiguration.taxValueEndpointURL());

		_addCommerceAddressParameters(
			commerceTaxCalculateRequest.getCommerceBillingAddressId(),
			"billing", uriBuilder);

		_addParameter(
			"percentage",
			String.valueOf(commerceTaxCalculateRequest.isPercentage()),
			uriBuilder);
		_addParameter(
			"price", String.valueOf(commerceTaxCalculateRequest.getPrice()),
			uriBuilder);

		_addCommerceAddressParameters(
			commerceTaxCalculateRequest.getCommerceShippingAddressId(),
			"shipping", uriBuilder);

		_addParameter(
			"taxCategoryId",
			String.valueOf(commerceTaxCalculateRequest.getTaxCategoryId()),
			uriBuilder);

		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod(
			commerceTaxCalculateRequest.getCommerceTaxMethodId());

		_addParameter(
			"taxMethod", commerceTaxMethod.getEngineKey(), uriBuilder);
		_addParameter(
			"taxMethodPercentage",
			String.valueOf(commerceTaxMethod.isPercentage()), uriBuilder);

		HttpGet httpGet = new HttpGet(uriBuilder.build());

		if (Validator.isNotNull(
				remoteCommerceTaxConfiguration.
					taxValueEndpointAuthorizationToken())) {

			Group group = _groupLocalService.getGroup(
				commerceTaxCalculateRequest.getCommerceChannelGroupId());

			String taxValueEndpointAuthorizationToken = _secretResolver.resolve(
				group.getCompanyId(),
				remoteCommerceTaxConfiguration.
					taxValueEndpointAuthorizationToken());

			httpGet.addHeader(
				"Authorization", "token " + taxValueEndpointAuthorizationToken);
		}

		return httpGet;
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RemoteCommerceTaxEngine.class);

	private CloseableHttpClient _closeableHttpClient;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceTaxMethodService _commerceTaxMethodService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	private final ObjectMapper _objectMapper = new ObjectMapper();
	private PoolingHttpClientConnectionManager
		_poolingHttpClientConnectionManager;

	@Reference
	private SecretResolver _secretResolver;

}