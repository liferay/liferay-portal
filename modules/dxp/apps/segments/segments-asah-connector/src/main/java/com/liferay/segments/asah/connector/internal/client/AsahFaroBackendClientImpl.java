/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NestableRuntimeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.asah.connector.internal.client.constants.FilterConstants;
import com.liferay.segments.asah.connector.internal.client.data.binding.ExperimentJSONObjectMapper;
import com.liferay.segments.asah.connector.internal.client.data.binding.IndividualJSONObjectMapper;
import com.liferay.segments.asah.connector.internal.client.data.binding.IndividualSegmentJSONObjectMapper;
import com.liferay.segments.asah.connector.internal.client.data.binding.InterestTermsJSONObjectMapper;
import com.liferay.segments.asah.connector.internal.client.model.DXPVariants;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentSettings;
import com.liferay.segments.asah.connector.internal.client.model.Individual;
import com.liferay.segments.asah.connector.internal.client.model.IndividualSegment;
import com.liferay.segments.asah.connector.internal.client.model.Results;
import com.liferay.segments.asah.connector.internal.client.model.Topic;
import com.liferay.segments.asah.connector.internal.client.util.FilterBuilder;
import com.liferay.segments.asah.connector.internal.client.util.OrderByField;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author David Arques
 */
public class AsahFaroBackendClientImpl implements AsahFaroBackendClient {

	public AsahFaroBackendClientImpl(
		AnalyticsSettingsManager analyticsSettingsManager, Http http) {

		_analyticsSettingsManager = analyticsSettingsManager;
		_http = http;
	}

	@Override
	public Experiment addExperiment(long companyId, Experiment experiment) {
		if (experiment == null) {
			return null;
		}

		try {
			return _post(
				companyId, _PATH_EXPERIMENTS, experiment,
				response -> _objectMapper.readValue(
					response, Experiment.class));
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Long calculateExperimentEstimatedDaysDuration(
		long companyId, String experimentId,
		ExperimentSettings experimentSettings) {

		try {
			return _post(
				companyId,
				StringUtil.replace(
					_PATH_EXPERIMENTS_ESTIMATED_DAYS_DURATION, "{experimentId}",
					experimentId),
				experimentSettings, Long::valueOf);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public void deleteExperiment(long companyId, String experimentId) {
		if (experimentId == null) {
			return;
		}

		try {
			_delete(
				companyId,
				StringUtil.replace(
					_PATH_EXPERIMENTS_EXPERIMENT, "{experimentId}",
					experimentId));
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Experiment getExperiment(long companyId, String experimentId) {
		if (experimentId == null) {
			return null;
		}

		try {
			return _get(
				companyId, new MultivaluedHashMap<>(),
				StringUtil.replace(
					_PATH_EXPERIMENTS_EXPERIMENT, "{experimentId}",
					experimentId),
				_experimentJSONObjectMapper::map);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Individual getIndividual(long companyId, String individualPK) {
		try {
			AnalyticsConfiguration analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

			FilterBuilder filterBuilder = new FilterBuilder();

			filterBuilder.addFilter(
				"dataSourceId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				analyticsConfiguration.liferayAnalyticsDataSourceId());
			filterBuilder.addFilter(
				"dataSourceIndividualPKs/individualPKs",
				FilterConstants.COMPARISON_OPERATOR_EQUALS, individualPK);

			MultivaluedHashMap<String, Object> uriVariables =
				new MultivaluedHashMap<>();

			uriVariables.putSingle("includeAnonymousUsers", true);

			Results<Individual> individualResults = _get(
				companyId,
				_getParameters(
					filterBuilder,
					FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL, 1, 1,
					Collections.emptyList(), uriVariables),
				_PATH_INDIVIDUALS, _individualJSONObjectMapper::mapToResults);

			List<Individual> items = individualResults.getItems();

			if (ListUtil.isNotEmpty(items)) {
				return items.get(0);
			}

			return null;
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Results<Individual> getIndividualResults(
		long companyId, String individualSegmentId, int cur, int delta,
		List<OrderByField> orderByFields) {

		try {
			return _get(
				companyId,
				_getParameters(
					new FilterBuilder(),
					FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL, cur, delta,
					orderByFields),
				StringUtil.replace(
					_PATH_INDIVIDUAL_SEGMENTS_INDIVIDUALS, "{id}",
					individualSegmentId),
				_individualJSONObjectMapper::mapToResults);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Results<IndividualSegment> getIndividualSegmentResults(
		long companyId, int cur, int delta, List<OrderByField> orderByFields) {

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"status", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			IndividualSegment.Status.ACTIVE.name());

		try {
			AnalyticsConfiguration analyticsConfiguration =
				_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

			MultivaluedMap<String, Object> parameters = _getParameters(
				filterBuilder,
				FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL_SEGMENT, cur,
				delta, orderByFields);

			parameters.putSingle(
				"dataSourceId",
				analyticsConfiguration.liferayAnalyticsDataSourceId());

			return _get(
				companyId, parameters, _PATH_INDIVIDUAL_SEGMENTS,
				_individualSegmentJSONObjectMapper::mapToResults);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				_ERROR_MSG + exception.getMessage(), exception);
		}
	}

	@Override
	public Results<Topic> getInterestTermsResults(
		long companyId, String userId) {

		try {
			return _get(
				companyId, new MultivaluedHashMap<>(),
				StringUtil.replace(_PATH_INTERESTS_TERMS, "{userId}", userId),
				_interestTermsJSONObjectMapper::mapToResults);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				"Unable to handle JSON response: " + exception.getMessage(),
				exception);
		}
	}

	@Override
	public void updateExperiment(long companyId, Experiment experiment) {
		if (Validator.isNull(experiment.getId())) {
			throw new IllegalArgumentException("Experiment ID is null");
		}

		try {
			_patch(
				companyId,
				StringUtil.replace(
					_PATH_EXPERIMENTS_EXPERIMENT, "{experimentId}",
					experiment.getId()),
				experiment);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				"Unable to handle JSON response: " + exception.getMessage(),
				exception);
		}
	}

	@Override
	public void updateExperimentDXPVariants(
		long companyId, String experimentId, DXPVariants dxpVariants) {

		if (Validator.isNull(experimentId)) {
			throw new IllegalArgumentException("Experiment ID is null");
		}

		if (dxpVariants == null) {
			throw new IllegalArgumentException("DXPVariants is null");
		}

		try {
			_put(
				companyId,
				StringUtil.replace(
					_PATH_EXPERIMENTS_DXP_VARIANTS, "{experimentId}",
					experimentId),
				dxpVariants);
		}
		catch (Exception exception) {
			throw new NestableRuntimeException(
				"Unable to handle JSON response: " + exception.getMessage(),
				exception);
		}
	}

	private String _delete(long companyId, String path) throws Exception {
		return _invoke(
			_getHttpOptions(
				companyId, Http.Method.DELETE, new MultivaluedHashMap<>(),
				path));
	}

	private <T> T _get(
			long companyId, MultivaluedMap<String, Object> parameters,
			String path, UnsafeFunction<String, T, Exception> unsafeFunction)
		throws Exception {

		return unsafeFunction.apply(
			_invoke(
				_getHttpOptions(companyId, Http.Method.GET, parameters, path)));
	}

	private Map<String, String> _getHeaders(
		AnalyticsConfiguration analyticsConfiguration) {

		return HashMapBuilder.put(
			"Accept", "application/json"
		).put(
			"Content-Type", "application/json"
		).put(
			"OSB-Asah-Faro-Backend-Security-Signature",
			analyticsConfiguration.
				liferayAnalyticsFaroBackendSecuritySignature()
		).put(
			"OSB-Asah-Project-ID",
			analyticsConfiguration.liferayAnalyticsProjectId()
		).build();
	}

	private Http.Options _getHttpOptions(
			long companyId, Http.Method method,
			MultivaluedMap<String, Object> parameters, String path)
		throws Exception {

		Http.Options httpOptions = new Http.Options();

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(companyId);

		httpOptions.setHeaders(_getHeaders(analyticsConfiguration));

		String url = StringBundler.concat(
			analyticsConfiguration.liferayAnalyticsFaroBackendURL(),
			StringPool.SLASH, path);

		for (MultivaluedMap.Entry<String, List<Object>> entry :
				parameters.entrySet()) {

			for (Object value : entry.getValue()) {
				url = HttpComponentsUtil.addParameter(
					url, entry.getKey(), value.toString());
			}
		}

		httpOptions.setLocation(url);

		httpOptions.setMethod(method);

		return httpOptions;
	}

	private MultivaluedMap<String, Object> _getParameters(
		FilterBuilder filterBuilder, String fieldNameContext, int cur,
		int delta, List<OrderByField> orderByFields) {

		MultivaluedMap<String, Object> uriVariables = _getUriVariables(
			cur, delta, orderByFields, fieldNameContext);

		uriVariables.putSingle("filter", filterBuilder.build());

		return uriVariables;
	}

	private MultivaluedMap<String, Object> _getParameters(
		FilterBuilder filterBuilder, String fieldNameContext, int cur,
		int delta, List<OrderByField> orderByFields,
		MultivaluedMap<String, Object> initialUriVariables) {

		MultivaluedMap<String, Object> uriVariables = _getUriVariables(
			cur, delta, orderByFields, fieldNameContext, initialUriVariables);

		uriVariables.putSingle("filter", filterBuilder.build());

		return uriVariables;
	}

	private MultivaluedMap<String, Object> _getUriVariables(
		int cur, int delta, List<OrderByField> orderByFields,
		String fieldNameContext) {

		return _getUriVariables(
			cur, delta, orderByFields, fieldNameContext,
			new MultivaluedHashMap<>());
	}

	private MultivaluedMap<String, Object> _getUriVariables(
		int cur, int delta, List<OrderByField> orderByFields,
		String fieldNameContext, MultivaluedMap<String, Object> uriVariables) {

		uriVariables.putSingle("page", cur - 1);
		uriVariables.putSingle("size", delta);

		if (ListUtil.isEmpty(orderByFields)) {
			return uriVariables;
		}

		List<Object> sort = new ArrayList<>();

		for (OrderByField orderByField : orderByFields) {
			String fieldName = orderByField.getFieldName();

			if (!orderByField.isSystem() && (fieldNameContext != null)) {
				fieldName = StringUtil.replace(
					fieldNameContext, CharPool.QUESTION, fieldName);
			}

			sort.add(fieldName + StringPool.COMMA + orderByField.getOrderBy());
		}

		uriVariables.put("sort", sort);

		return uriVariables;
	}

	private String _invoke(Http.Options httpOptions) throws Exception {
		String response = _http.URLtoString(httpOptions);

		Http.Response httpResponse = httpOptions.getResponse();

		if (httpResponse.getResponseCode() != HttpURLConnection.HTTP_OK) {
			if (_log.isDebugEnabled()) {
				_log.debug("Response code " + httpResponse.getResponseCode());
			}

			throw new NestableRuntimeException(
				StringBundler.concat(
					"Unexpected response status ",
					httpResponse.getResponseCode(), " with response message: ",
					response));
		}

		return response;
	}

	private String _patch(long companyId, String path, Object object)
		throws Exception {

		Http.Options httpOptions = _getHttpOptions(
			companyId, Http.Method.PATCH, new MultivaluedHashMap<>(), path);

		httpOptions.setBody(
			_objectMapper.writeValueAsString(object),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);

		return _invoke(httpOptions);
	}

	private <T> T _post(
			long companyId, String path, Object object,
			UnsafeFunction<String, T, Exception> unsafeFunction)
		throws Exception {

		Http.Options httpOptions = _getHttpOptions(
			companyId, Http.Method.POST, new MultivaluedHashMap<>(), path);

		httpOptions.setBody(
			_objectMapper.writeValueAsString(object),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);

		String response = _invoke(httpOptions);

		if (Validator.isNull(response)) {
			return null;
		}

		return unsafeFunction.apply(response);
	}

	private String _put(long companyId, String path, Object object)
		throws Exception {

		Http.Options httpOptions = _getHttpOptions(
			companyId, Http.Method.PUT, new MultivaluedHashMap<>(), path);

		httpOptions.setBody(
			_objectMapper.writeValueAsString(object),
			ContentTypes.APPLICATION_JSON, StringPool.UTF8);

		return _invoke(httpOptions);
	}

	private static final String _ERROR_MSG = "Unable to handle response: ";

	private static final String _PATH_EXPERIMENTS = "api/1.0/experiments";

	private static final String _PATH_EXPERIMENTS_DXP_VARIANTS =
		_PATH_EXPERIMENTS + "/{experimentId}/dxp-variants";

	private static final String _PATH_EXPERIMENTS_ESTIMATED_DAYS_DURATION =
		_PATH_EXPERIMENTS + "/{experimentId}/estimated-days-duration";

	private static final String _PATH_EXPERIMENTS_EXPERIMENT =
		_PATH_EXPERIMENTS + "/{experimentId}";

	private static final String _PATH_INDIVIDUAL_SEGMENTS =
		"api/1.0/individual-segments";

	private static final String _PATH_INDIVIDUAL_SEGMENTS_INDIVIDUALS =
		_PATH_INDIVIDUAL_SEGMENTS + "/{id}/individuals";

	private static final String _PATH_INDIVIDUALS = "api/1.0/individuals";

	private static final String _PATH_INTERESTS_TERMS =
		"api/1.0/interests/terms/{userId}";

	private static final Log _log = LogFactoryUtil.getLog(
		AsahFaroBackendClientImpl.class);

	private static final ExperimentJSONObjectMapper
		_experimentJSONObjectMapper = new ExperimentJSONObjectMapper();
	private static final IndividualJSONObjectMapper
		_individualJSONObjectMapper = new IndividualJSONObjectMapper();
	private static final IndividualSegmentJSONObjectMapper
		_individualSegmentJSONObjectMapper =
			new IndividualSegmentJSONObjectMapper();
	private static final InterestTermsJSONObjectMapper
		_interestTermsJSONObjectMapper = new InterestTermsJSONObjectMapper();
	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		}
	};

	private final AnalyticsSettingsManager _analyticsSettingsManager;
	private final Http _http;

}