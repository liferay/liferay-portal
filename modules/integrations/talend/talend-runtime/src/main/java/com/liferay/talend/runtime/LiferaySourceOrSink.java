/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import com.liferay.talend.common.oas.OASException;
import com.liferay.talend.common.oas.OASSource;
import com.liferay.talend.common.util.StringUtil;
import com.liferay.talend.properties.connection.LiferayConnectionProperties;
import com.liferay.talend.properties.parameters.RequestParameter;
import com.liferay.talend.properties.parameters.RequestParameterProperties;
import com.liferay.talend.runtime.client.LiferayClient;
import com.liferay.talend.runtime.client.ResponseHandler;
import com.liferay.talend.runtime.client.exception.ResponseContentClientException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.json.JsonObject;
import javax.json.JsonValue;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.avro.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessageProvider;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 * @author Ivica Cardic
 */
public class LiferaySourceOrSink implements OASSource, SourceOrSink {

	public JsonObject doDeleteRequest(String resourceURL) {
		LiferayClient liferayClient = getLiferayClient();

		return _getResponseContentJsonObject(
			liferayClient.executeDeleteRequest(resourceURL));
	}

	public JsonObject doGetRequest(String resourceURL) {
		LiferayClient liferayClient = getLiferayClient();

		return _getResponseContentJsonObject(
			liferayClient.executeGetRequest(resourceURL));
	}

	public JsonObject doPatchRequest(String resourceURL, JsonValue jsonValue) {
		LiferayClient liferayClient = getLiferayClient();

		return _getResponseContentJsonObject(
			liferayClient.executePatchRequest(resourceURL, jsonValue));
	}

	public JsonObject doPostRequest(String resourceURL, JsonValue jsonValue) {
		LiferayClient liferayClient = getLiferayClient();

		return _getResponseContentJsonObject(
			liferayClient.executePostRequest(resourceURL, jsonValue));
	}

	public JsonObject doPutRequest(String resourceURL, JsonValue jsonValue) {
		LiferayClient liferayClient = getLiferayClient();

		return _getResponseContentJsonObject(
			liferayClient.executePutRequest(resourceURL, jsonValue));
	}

	@Override
	public Schema getEndpointSchema(
		RuntimeContainer runtimeContainer, String endpoint) {

		return null;
	}

	public LiferayClient getLiferayClient() {
		if (_liferayClient != null) {
			return _liferayClient;
		}

		LiferayClient.Builder liferayClientBuilder =
			new LiferayClient.Builder();

		liferayClientBuilder.setConnectionTimeoutMills(
			_liferayConnectionProperties.getConnectTimeout() * 1000);

		liferayClientBuilder.setAuthorizationIdentityId(
			_liferayConnectionProperties.getUserId());
		liferayClientBuilder.setAuthorizationIdentitySecret(
			_liferayConnectionProperties.getPassword());

		liferayClientBuilder.setFollowRedirects(
			_liferayConnectionProperties.isFollowRedirects());
		liferayClientBuilder.setForceHttps(
			_liferayConnectionProperties.isForceHttps());
		liferayClientBuilder.setHostURL(
			_liferayConnectionProperties.getHostURL());
		liferayClientBuilder.setOAuthAuthorization(
			_liferayConnectionProperties.isOAuth2Authorization());

		_setProxyParameters(liferayClientBuilder);

		liferayClientBuilder.setRadTimeoutMills(
			_liferayConnectionProperties.getReadTimeout() * 1000);

		_liferayClient = liferayClientBuilder.build();

		return _liferayClient;
	}

	/**
	 * @return     JsonObject
	 * @deprecated As of Athanasius (7.3.x), see {@link
	 *             #getOASJsonObject(String)}
	 */
	@Deprecated
	@Override
	public JsonObject getOASJsonObject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JsonObject getOASJsonObject(String oasUrl) {
		JsonObject jsonObject = doGetRequest(oasUrl);

		if (jsonObject != null) {
			return jsonObject;
		}

		throw new OASException(
			"Unable to get OpenAPI specification at location " + oasUrl);
	}

	@Override
	public List<NamedThing> getSchemaNames(RuntimeContainer runtimeContainer) {
		return Collections.emptyList();
	}

	@Override
	public ValidationResult initialize(
		ComponentProperties componentProperties) {

		ValidationResult validationResult = initialize(
			null, componentProperties);

		if (validationResult.getStatus() == ValidationResult.Result.ERROR) {
			return validationResult;
		}

		return validate(null);
	}

	@Override
	public ValidationResult initialize(
		RuntimeContainer runtimeContainer,
		ComponentProperties componentProperties) {

		Objects.requireNonNull(componentProperties);

		if (componentProperties instanceof LiferayConnectionProperties) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				"Connection properties are not allowed here");
		}

		Properties aggregatedProperties = componentProperties.getProperties(
			getRequestParameterPropertiesPath());

		if (aggregatedProperties instanceof RequestParameterProperties) {
			_requestParameterProperties =
				(RequestParameterProperties)aggregatedProperties;
		}

		LiferayConnectionProperties liferayConnectionProperties = null;

		aggregatedProperties = componentProperties.getProperties(
			getLiferayConnectionPropertiesPath());

		if (aggregatedProperties instanceof LiferayConnectionProperties) {
			liferayConnectionProperties =
				(LiferayConnectionProperties)aggregatedProperties;
		}

		if (liferayConnectionProperties == null) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				"Unable to locate connection properties");
		}

		_liferayConnectionProperties =
			liferayConnectionProperties.
				getEffectiveLiferayConnectionProperties();

		try {
			getLiferayClient();

			return ValidationResult.OK;
		}
		catch (TalendRuntimeException talendRuntimeException) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				talendRuntimeException.getMessage());
		}
	}

	@Override
	public ValidationResult validate(RuntimeContainer runtimeContainer) {
		if (_liferayConnectionProperties == null) {
			return new ValidationResult(
				ValidationResult.Result.ERROR,
				i18nMessages.getMessage("error.validation.connection"));
		}

		if (StringUtil.isEmpty(_liferayConnectionProperties.getPassword()) ||
			StringUtil.isEmpty(_liferayConnectionProperties.getUserId())) {

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				i18nMessages.getMessage(
					"error.validation.connection.credentials"));
		}

		return validateConnection();
	}

	public ValidationResult validateConnection() {
		try {
			LiferayClient liferayClient = getLiferayClient();

			liferayClient.executeGetRequest("/");

			return new ValidationResult(
				ValidationResult.Result.OK,
				i18nMessages.getMessage("success.validation.connection"));
		}
		catch (TalendRuntimeException talendRuntimeException) {
			_logger.error(
				talendRuntimeException.getMessage(), talendRuntimeException);

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				i18nMessages.getMessage(
					"error.validation.connection.testconnection",
					talendRuntimeException.getLocalizedMessage()));
		}
		catch (ProcessingException processingException) {
			_logger.error(
				processingException.getMessage(), processingException);

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				i18nMessages.getMessage(
					"error.validation.connection.testconnection.jersey",
					processingException.getLocalizedMessage()));
		}
		catch (Throwable throwable) {
			_logger.error(throwable.getMessage(), throwable);

			return new ValidationResult(
				ValidationResult.Result.ERROR,
				i18nMessages.getMessage(
					"error.validation.connection.testconnection.general",
					throwable.getLocalizedMessage()));
		}
	}

	protected String getLiferayConnectionPropertiesPath() {
		return "connection";
	}

	protected String getRequestParameterPropertiesPath() {
		return "resource.parameters";
	}

	protected static final I18nMessages i18nMessages;

	static {
		I18nMessageProvider i18nMessageProvider =
			GlobalI18N.getI18nMessageProvider();

		i18nMessages = i18nMessageProvider.getI18nMessages(
			LiferaySourceOrSink.class);
	}

	private JsonObject _getResponseContentJsonObject(Response response) {
		if (!_responseHandler.isSuccess(response)) {
			throw new ResponseContentClientException(
				"Request did not succeed", response.getStatus(), null);
		}

		if (((response.getLength() <= 0) && !response.hasEntity()) ||
			!_responseHandler.isApplicationJsonContentType(response)) {

			return null;
		}

		return _responseHandler.asJsonObject(response);
	}

	private void _setProxyParameters(
		LiferayClient.Builder liferayClientBuilder) {

		if (_requestParameterProperties == null) {
			return;
		}

		List<RequestParameter> proxyRequestParameters =
			_requestParameterProperties.getProxyRequestParameters();

		for (RequestParameter requestParameter : proxyRequestParameters) {
			if (Objects.equals(requestParameter.getName(), "proxyIdentityId")) {
				liferayClientBuilder.setProxyIdentityId(
					requestParameter.getValue());
			}
			else if (Objects.equals(
						requestParameter.getName(), "proxyIdentitySecret")) {

				liferayClientBuilder.setProxyIdentitySecret(
					requestParameter.getValue());
			}
		}
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferaySourceOrSink.class);

	private static final long serialVersionUID = 3109815759807236523L;

	private transient LiferayClient _liferayClient;
	private transient LiferayConnectionProperties _liferayConnectionProperties;
	private transient RequestParameterProperties _requestParameterProperties;
	private final ResponseHandler _responseHandler = new ResponseHandler();

}