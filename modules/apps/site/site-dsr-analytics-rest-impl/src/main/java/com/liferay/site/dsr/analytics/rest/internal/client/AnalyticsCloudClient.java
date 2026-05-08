/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.DocumentMetrics;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.Events;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.IdentityActivity;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.MostActiveVisitors;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.SiteHistogramMetric;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.SiteVisitorBehaviorMetric;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.UserSessions;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.VisitFrequency;

import java.io.IOException;

import java.net.HttpURLConnection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Gianmarco Brunialti
 */
public class AnalyticsCloudClient {

	public AnalyticsCloudClient(
		ChannelResource.Factory channelResourceFactory, Http http, User user) {

		_channelResourceFactory = channelResourceFactory;
		_http = http;
		_user = user;
	}

	public DocumentMetrics getDocumentMetrics(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			String keywords, String rangeEnd, Integer rangeKey,
			String rangeStart, Integer size, String sortColumn, String sortType,
			Integer start)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		variablesObjectNode.put("keywords", keywords);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		variablesObjectNode.put("size", size);
		variablesObjectNode.put("start", start);

		ObjectNode sortObjectNode = variablesObjectNode.putObject("sort");

		sortObjectNode.put("column", sortColumn);
		sortObjectNode.put("type", sortType);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_DOCUMENT_METRICS, "documents",
			variablesObjectNode);

		_renameKey(dataJsonNode, "documentMetrics", "assetMetrics");

		return _objectMapper.treeToValue(dataJsonNode, DocumentMetrics.class);
	}

	public Events getEvents(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			Boolean includeAnonymousUsers, String individualId, String keywords,
			Integer page, String rangeEnd, Integer rangeKey, String rangeStart,
			Integer size)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		_putNullable(
			"includeAnonymousUsers", variablesObjectNode,
			includeAnonymousUsers);
		_putNullable("individualId", variablesObjectNode, individualId);
		_putNullable("keywords", variablesObjectNode, keywords);

		variablesObjectNode.put("page", page);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		variablesObjectNode.put("size", size);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_EVENTS, "events",
			variablesObjectNode);

		_renameKey(dataJsonNode, "eventEntries", "events");

		return _objectMapper.treeToValue(dataJsonNode, Events.class);
	}

	public IdentityActivity getIdentityActivity(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			String[] includedEventIds, Integer rangeKey)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		if (ArrayUtil.isEmpty(includedEventIds)) {
			variablesObjectNode.putNull("includedEventIds");
		}
		else {
			ArrayNode includedEventIdsArrayNode = variablesObjectNode.putArray(
				"includedEventIds");

			for (String includedEventId : includedEventIds) {
				includedEventIdsArrayNode.add(includedEventId);
			}
		}

		_putNullable("rangeKey", variablesObjectNode, rangeKey);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_IDENTITY_ACTIVITY,
			"identityActivityCount", variablesObjectNode);

		return _objectMapper.treeToValue(dataJsonNode, IdentityActivity.class);
	}

	public MostActiveVisitors getMostActiveVisitors(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			String rangeEnd, Integer rangeKey, String rangeStart, Integer size,
			Integer start)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		variablesObjectNode.put("size", size);
		variablesObjectNode.put("start", start);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_MOST_ACTIVE_VISITORS,
			"mostActiveVisitors", variablesObjectNode);

		return _objectMapper.treeToValue(
			dataJsonNode, MostActiveVisitors.class);
	}

	public SiteHistogramMetric getSessionsSiteHistogramMetric(
			AnalyticsConfiguration analyticsConfiguration,
			String[] emailAddresses, String[] groupIds, String interval,
			String rangeEnd, Integer rangeKey, String rangeStart)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		if (ArrayUtil.isEmpty(emailAddresses)) {
			variablesObjectNode.putNull("emailAddresses");
		}
		else {
			ArrayNode emailAddressesArrayNode = variablesObjectNode.putArray(
				"emailAddresses");

			for (String emailAddress : emailAddresses) {
				emailAddressesArrayNode.add(emailAddress);
			}
		}

		_putGroupIds(variablesObjectNode, groupIds);

		variablesObjectNode.put("interval", interval);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_SESSIONS_SITE_HISTOGRAM_METRIC,
			"site", variablesObjectNode);

		return _toSiteHistogramMetric("sessionsMetric", dataJsonNode);
	}

	public SiteVisitorBehaviorMetric getSiteVisitorBehaviorMetric(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			Integer rangeKey)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		_putNullable("rangeKey", variablesObjectNode, rangeKey);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_SITE_VISITOR_BEHAVIOR_METRIC,
			"siteVisitor", variablesObjectNode);

		return _objectMapper.treeToValue(
			dataJsonNode, SiteVisitorBehaviorMetric.class);
	}

	public UserSessions getUserSessions(
			AnalyticsConfiguration analyticsConfiguration, String entityId,
			String entityType, String[] groupIds, String keywords, Integer page,
			String rangeEnd, Integer rangeKey, String rangeStart, Integer size)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putNullable("entityId", variablesObjectNode, entityId);

		variablesObjectNode.put("entityType", entityType);

		_putGroupIds(variablesObjectNode, groupIds);

		_putNullable("keywords", variablesObjectNode, keywords);

		variablesObjectNode.put("page", page);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		variablesObjectNode.put("size", size);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_USER_SESSIONS,
			"eventsByUserSessions", variablesObjectNode);

		_renameKey(dataJsonNode, "userSessionEvents", "events");

		return _objectMapper.treeToValue(dataJsonNode, UserSessions.class);
	}

	public VisitFrequency getVisitFrequency(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			String rangeEnd, Integer rangeKey, String rangeStart)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_VISIT_FREQUENCY, "visitFrequency",
			variablesObjectNode);

		_renameKey(dataJsonNode, "visitFrequencyItems", "visitFrequency");

		return _objectMapper.treeToValue(dataJsonNode, VisitFrequency.class);
	}

	public SiteHistogramMetric getVisitorsSiteHistogramMetric(
			AnalyticsConfiguration analyticsConfiguration, String[] groupIds,
			String interval, String rangeEnd, Integer rangeKey,
			String rangeStart)
		throws Exception {

		ObjectNode variablesObjectNode = _objectMapper.createObjectNode();

		_putGroupIds(variablesObjectNode, groupIds);

		variablesObjectNode.put("interval", interval);

		_putRangeVariables(variablesObjectNode, rangeEnd, rangeKey, rangeStart);

		JsonNode dataJsonNode = _executeQuery(
			analyticsConfiguration, _QUERY_VISITORS_SITE_HISTOGRAM_METRIC,
			"site", variablesObjectNode);

		return _toSiteHistogramMetric("visitorsMetric", dataJsonNode);
	}

	private static String _readQuery(String fileName) throws IOException {
		return StringUtil.read(
			AnalyticsCloudClient.class, "dependencies/" + fileName);
	}

	private JsonNode _executeQuery(
			AnalyticsConfiguration analyticsConfiguration, String query,
			String rootField, ObjectNode variablesObjectNode)
		throws Exception {

		try {
			ObjectNode bodyObjectNode = _objectMapper.createObjectNode();

			bodyObjectNode.put("query", query);

			Channel channel = _getOrAddAnalyticsChannel();

			variablesObjectNode.put("channelId", channel.getChannelId());

			bodyObjectNode.set("variables", variablesObjectNode);

			Http.Options options = new Http.Options();

			options.addHeader("Content-Type", "application/json");
			options.addHeader(
				"OSB-Asah-Data-Source-ID",
				analyticsConfiguration.liferayAnalyticsDataSourceId());
			options.addHeader(
				"OSB-Asah-Faro-Backend-Security-Signature",
				analyticsConfiguration.
					liferayAnalyticsFaroBackendSecuritySignature());
			options.addHeader(
				"OSB-Asah-Project-ID",
				analyticsConfiguration.liferayAnalyticsProjectId());
			options.setBody(
				_objectMapper.writeValueAsString(bodyObjectNode),
				"application/json", "UTF-8");
			options.setLocation(
				analyticsConfiguration.liferayAnalyticsFaroBackendURL() +
					"/api/1.0/graphql");
			options.setPost(true);

			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug("Response code " + response.getResponseCode());
				}

				throw new PortalException(
					"Unable to execute DSR analytics query " + rootField);
			}

			JsonNode rootJsonNode = _objectMapper.readTree(content);

			return rootJsonNode.path(
				"data"
			).path(
				rootField
			);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new PortalException(
				"Unable to execute DSR analytics query " + rootField,
				exception);
		}
	}

	private Channel _getOrAddAnalyticsChannel() throws Exception {
		ChannelResource channelResource = _channelResourceFactory.create(
		).checkPermissions(
			false
		).user(
			_user
		).build();

		Page<Channel> channelsPage = channelResource.getChannelsPage(
			_DSR_CHANNEL_NAME, Pagination.of(1, 1), null);

		List<Channel> channels = ListUtil.fromCollection(
			channelsPage.getItems());

		if (!channels.isEmpty()) {
			return channels.get(0);
		}

		Channel channel = new Channel();

		channel.setName(() -> _DSR_CHANNEL_NAME);

		return channelResource.postChannel(channel);
	}

	private void _putGroupIds(ObjectNode objectNode, String[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			objectNode.putNull("groupIds");
		}
		else {
			ArrayNode groupIdsArrayNode = objectNode.putArray("groupIds");

			for (String groupId : groupIds) {
				groupIdsArrayNode.add(groupId);
			}
		}
	}

	private void _putNullable(
		String key, ObjectNode objectNode, Boolean value) {

		if (value == null) {
			objectNode.putNull(key);
		}
		else {
			objectNode.put(key, value);
		}
	}

	private void _putNullable(
		String key, ObjectNode objectNode, Integer value) {

		if (value == null) {
			objectNode.putNull(key);
		}
		else {
			objectNode.put(key, value);
		}
	}

	private void _putNullable(String key, ObjectNode objectNode, String value) {
		if (value == null) {
			objectNode.putNull(key);
		}
		else {
			objectNode.put(key, value);
		}
	}

	private void _putRangeVariables(
		ObjectNode objectNode, String rangeEnd, Integer rangeKey,
		String rangeStart) {

		_putNullable("rangeEnd", objectNode, rangeEnd);

		if (Validator.isNotNull(rangeEnd) || Validator.isNotNull(rangeStart)) {
			objectNode.putNull("rangeKey");
		}
		else {
			_putNullable("rangeKey", objectNode, rangeKey);
		}

		_putNullable("rangeStart", objectNode, rangeStart);
	}

	private void _renameKey(JsonNode jsonNode, String newKey, String oldKey) {
		if (jsonNode == null) {
			return;
		}

		if (jsonNode.isObject()) {
			ObjectNode objectNode = (ObjectNode)jsonNode;

			if (objectNode.has(oldKey)) {
				JsonNode valueJsonNode = objectNode.remove(oldKey);

				objectNode.set(newKey, valueJsonNode);
			}

			Iterator<Map.Entry<String, JsonNode>> fieldsIterator =
				objectNode.fields();

			fieldsIterator.forEachRemaining(
				entry -> _renameKey(entry.getValue(), newKey, oldKey));
		}
		else if (jsonNode.isArray()) {
			for (JsonNode elementJsonNode : jsonNode) {
				_renameKey(elementJsonNode, newKey, oldKey);
			}
		}
	}

	private SiteHistogramMetric _toSiteHistogramMetric(
			String metricField, JsonNode siteJsonNode)
		throws Exception {

		JsonNode metricJsonNode = siteJsonNode.path(metricField);

		if (metricJsonNode.isMissingNode() || metricJsonNode.isNull()) {
			return new SiteHistogramMetric();
		}

		_renameKey(metricJsonNode, "histogramMetrics", "metrics");

		ObjectNode wrapperObjectNode = _objectMapper.createObjectNode();

		wrapperObjectNode.set("histogram", metricJsonNode.path("histogram"));

		return _objectMapper.treeToValue(
			wrapperObjectNode, SiteHistogramMetric.class);
	}

	private static final String _DSR_CHANNEL_NAME = "DSR";

	private static final String _QUERY_DOCUMENT_METRICS;

	private static final String _QUERY_EVENTS;

	private static final String _QUERY_IDENTITY_ACTIVITY;

	private static final String _QUERY_MOST_ACTIVE_VISITORS;

	private static final String _QUERY_SESSIONS_SITE_HISTOGRAM_METRIC;

	private static final String _QUERY_SITE_VISITOR_BEHAVIOR_METRIC;

	private static final String _QUERY_USER_SESSIONS;

	private static final String _QUERY_VISIT_FREQUENCY;

	private static final String _QUERY_VISITORS_SITE_HISTOGRAM_METRIC;

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsCloudClient.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		}
	};

	static {
		try {
			_QUERY_DOCUMENT_METRICS = _readQuery("document_metrics.graphql");
			_QUERY_EVENTS = _readQuery("events.graphql");
			_QUERY_IDENTITY_ACTIVITY = _readQuery("identity_activity.graphql");
			_QUERY_MOST_ACTIVE_VISITORS = _readQuery(
				"most_active_visitors.graphql");
			_QUERY_SESSIONS_SITE_HISTOGRAM_METRIC = _readQuery(
				"sessions_site_histogram_metric.graphql");
			_QUERY_SITE_VISITOR_BEHAVIOR_METRIC = _readQuery(
				"site_visitor_behavior_metric.graphql");
			_QUERY_VISITORS_SITE_HISTOGRAM_METRIC = _readQuery(
				"visitors_site_histogram_metric.graphql");
			_QUERY_USER_SESSIONS = _readQuery("user_sessions.graphql");
			_QUERY_VISIT_FREQUENCY = _readQuery("visit_frequency.graphql");
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

	private final ChannelResource.Factory _channelResourceFactory;
	private final Http _http;
	private final User _user;

}