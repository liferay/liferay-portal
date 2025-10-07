/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.engine.client.BaseEngineClient;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.ActivityConstants;
import com.liferay.osb.faro.engine.client.constants.AssetConstants;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.constants.FilterConstants;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.Activity;
import com.liferay.osb.faro.engine.client.model.ActivityAggregation;
import com.liferay.osb.faro.engine.client.model.ActivityAsset;
import com.liferay.osb.faro.engine.client.model.ActivityGroup;
import com.liferay.osb.faro.engine.client.model.AsahProject;
import com.liferay.osb.faro.engine.client.model.Asset;
import com.liferay.osb.faro.engine.client.model.Author;
import com.liferay.osb.faro.engine.client.model.BlockedKeyword;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.DXPGroup;
import com.liferay.osb.faro.engine.client.model.DXPOrganization;
import com.liferay.osb.faro.engine.client.model.DXPUserGroup;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.DataSourceField;
import com.liferay.osb.faro.engine.client.model.DataSourceProgress;
import com.liferay.osb.faro.engine.client.model.Distribution;
import com.liferay.osb.faro.engine.client.model.EntityModelPagedModel;
import com.liferay.osb.faro.engine.client.model.Event;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.engine.client.model.IndividualTransformation;
import com.liferay.osb.faro.engine.client.model.Interest;
import com.liferay.osb.faro.engine.client.model.PageVisited;
import com.liferay.osb.faro.engine.client.model.PagedModel;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Provider;
import com.liferay.osb.faro.engine.client.model.Rels;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.StringPagedModel;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.engine.client.model.provider.LiferayProvider;
import com.liferay.osb.faro.engine.client.model.provider.SalesforceProvider;
import com.liferay.osb.faro.engine.client.util.FilterBuilder;
import com.liferay.osb.faro.engine.client.util.FilterUtil;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.OutputStream;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

/**
 * @author Shinn Lok
 */
@Component(service = ContactsEngineClient.class)
public class ContactsEngineClientImpl
	extends BaseEngineClient implements ContactsEngineClient {

	@Override
	public Results<BlockedKeyword> addBlockedKeywords(
		FaroProject faroProject, List<String> keywords) {

		Map<String, Object> response = post(
			faroProject, Rels.BLOCKED_KEYWORDS,
			HashMapBuilder.<String, Object>put("keywords", keywords),
			Map.class);

		List<Object> blockedKeywords = (List<Object>)response.get(
			"blocked-keywords");

		return new Results<>(
			TransformUtil.transform(
				blockedKeywords,
				blockedKeywordObject -> objectMapper.convertValue(
					blockedKeywordObject, BlockedKeyword.class)),
			blockedKeywords.size());
	}

	@Override
	public Channel addChannel(FaroProject faroProject, Channel channel) {
		return post(
			faroProject, Rels.CHANNELS, channel, Channel.class,
			getUriVariables(faroProject));
	}

	@Override
	public void addCSVIndividuals(
			FaroProject faroProject, List<Map<String, Object>> fieldsMaps,
			String dataSourceId, List<String> individualSegmentIds)
		throws Exception {

		List<Map<String, Object>> individualMaps = new ArrayList<>();

		int byteSize = 0;

		for (Map<String, Object> fieldsMap : fieldsMaps) {
			Map<String, Object> individualMap =
				HashMapBuilder.<String, Object>put(
					"dataSourceId", dataSourceId
				).put(
					"dataSourceIndividualPK", UUID.randomUUID()
				).put(
					"faroProject", faroProject.getWeDeployKey()
				).put(
					"fields", fieldsMap
				).put(
					"individualSegmentIds", individualSegmentIds
				).build();

			byte[] bytes = objectMapper.writeValueAsBytes(individualMap);

			if ((byteSize + bytes.length) > _PAYLOAD_MAX_BYTE_SIZE) {
				post(
					faroProject, Rels.CSV_INDIVIDUALS, individualMaps,
					Void.class);

				individualMaps.clear();

				byteSize = 0;
			}

			individualMaps.add(individualMap);

			byteSize += bytes.length;
		}

		if (!individualMaps.isEmpty()) {
			post(faroProject, Rels.CSV_INDIVIDUALS, individualMaps, Void.class);
		}
	}

	@Override
	public void addData(
		FaroProject faroProject, String weDeployDataServiceName,
		String collectionName, List<Map<String, Object>> objects) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("collectionName", collectionName);
		uriVariables.put("weDeployDataServiceName", weDeployDataServiceName);

		post(faroProject, Rels.ADMIN_DATA, objects, Void.class, uriVariables);
	}

	@Override
	public DataSource addDataSource(
		FaroProject faroProject, Credentials credentials, Author author,
		String name, String url, Provider provider, Event event,
		String status) {

		DataSource dataSource = new DataSource();

		dataSource.setAuthor(author);
		dataSource.setCredentials(credentials);
		dataSource.setName(name);
		dataSource.setProvider(provider);
		dataSource.setStatus(status);
		dataSource.setSubjectOf(event);
		dataSource.setUrl(url);
		dataSource.setWorkspaceURL(getWorkspaceURL(faroProject.getGroupId()));

		return post(
			faroProject, Rels.DATA_SOURCES, dataSource, DataSource.class);
	}

	@Override
	public DataSource addDataSource(
		FaroProject faroProject, Credentials credentials, long userId,
		String name, String url, Provider provider, Event event,
		String status) {

		return addDataSource(
			faroProject, credentials, getAuthor(userId), name, url, provider,
			event, status);
	}

	@Override
	public FieldMapping addFieldMapping(
		FaroProject faroProject, String context,
		Map<String, String> dataSourceFieldNames, String fieldName,
		String fieldType, String ownerType, Boolean repeatable) {

		FieldMapping fieldMapping = new FieldMapping();

		fieldMapping.setContext(context);
		fieldMapping.setDataSourceFieldNames(dataSourceFieldNames);
		fieldMapping.setDisplayName(fieldName);
		fieldMapping.setFieldName(fieldName);
		fieldMapping.setFieldType(fieldType);
		fieldMapping.setOwnerType(ownerType);
		fieldMapping.setRepeatable(repeatable);

		return post(
			faroProject, Rels.FIELD_MAPPINGS, fieldMapping, FieldMapping.class);
	}

	@Override
	public List<FieldMapping> addFieldMappings(
		FaroProject faroProject, String dataSourceId, String context,
		String ownerType, List<FieldMappingMap> fieldMappingMaps) {

		List<Object> fieldMappings = new ArrayList<>();
		List<Map<String, Object>> uriVariablesList = new ArrayList<>();

		for (FieldMappingMap fieldMappingMap : fieldMappingMaps) {
			FieldMapping fieldMapping = new FieldMapping();

			fieldMapping.setContext(context);
			fieldMapping.setDisplayName(fieldMappingMap.getName());

			Map<String, String> dataSourceFieldNames = new HashMap<>();

			if (Validator.isNotNull(dataSourceId)) {
				dataSourceFieldNames.put(
					dataSourceId, fieldMappingMap.getDataSourceFieldName());
			}

			fieldMapping.setDataSourceFieldNames(dataSourceFieldNames);

			fieldMapping.setFieldName(fieldMappingMap.getName());
			fieldMapping.setFieldType(fieldMappingMap.getType());
			fieldMapping.setOwnerType(ownerType);
			fieldMapping.setRepeatable(fieldMappingMap.getRepeatable());

			fieldMappings.add(fieldMapping);

			uriVariablesList.add(getUriVariables(faroProject));
		}

		return bulk(
			faroProject, Rels.FIELD_MAPPINGS, HttpMethod.POST, fieldMappings,
			new TypeReference<FieldMapping>() {
			},
			uriVariablesList);
	}

	@Override
	public IndividualSegment addIndividualSegment(
		FaroProject faroProject, long userId, String channelId, String filter,
		boolean includeAnonymousUsers, String name, String segmentType,
		String status) {

		IndividualSegment individualSegment = new IndividualSegment();

		individualSegment.setAuthor(getAuthor(userId));
		individualSegment.setChannelId(channelId);
		individualSegment.setDateModified(new Date());
		individualSegment.setFilter(filter);
		individualSegment.setIncludeAnonymousUsers(includeAnonymousUsers);
		individualSegment.setName(name);
		individualSegment.setSegmentType(segmentType);
		individualSegment.setStatus(status);

		return post(
			faroProject, Rels.INDIVIDUAL_SEGMENTS, individualSegment,
			IndividualSegment.class);
	}

	@Override
	public IndividualSegmentMembership addMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId) {

		IndividualSegmentMembership individualSegmentMembership =
			new IndividualSegmentMembership();

		individualSegmentMembership.setIndividualId(individualId);

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, individualSegmentId);

		return post(
			faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIPS,
			individualSegmentMembership, IndividualSegmentMembership.class,
			uriVariables);
	}

	@Override
	public void addMemberships(
		FaroProject faroProject, String individualSegmentId,
		List<String> individualIds) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, individualSegmentId);

		post(
			faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIPS,
			TransformUtil.transform(
				individualIds,
				individualId -> {
					IndividualSegmentMembership individualSegmentMembership =
						new IndividualSegmentMembership();

					individualSegmentMembership.setIndividualId(individualId);

					return individualSegmentMembership;
				}),
			Void.class, uriVariables);
	}

	@Override
	public void addNanite(
		FaroProject faroProject, String className,
		Map<String, Object> context) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("className", className);

		post(faroProject, Rels.ADMIN_NANITE, context, Void.class, uriVariables);
	}

	@Override
	public void addNanites(FaroProject faroProject, List<String> classNames) {
		Map<String, Object> uriVariables = getUriVariables(faroProject);

		post(
			faroProject, Rels.ADMIN_NANITES, classNames, Void.class,
			uriVariables);
	}

	@Override
	public String addProject(FaroProject faroProject) throws Exception {
		String projectId = faroProject.getProjectId();

		if (Validator.isNull(projectId)) {
			String uuid = StringUtil.replace(
				String.valueOf(UUID.randomUUID()), CharPool.DASH,
				StringPool.BLANK);

			String osbFaroProjectIdPrefix = GetterUtil.get(
				FaroPropsValues.FARO_PROJECT_ID_PREFIX, "asah");

			projectId = osbFaroProjectIdPrefix + uuid;
		}

		faroProject.setWeDeployKey(projectId);

		post(
			faroProject, Collections.emptyMap(), "/projects",
			Collections.emptyMap(),
			new AsahProject(projectId, faroProject.getLastAnniversaryDate()),
			Void.class);

		return projectId;
	}

	@Override
	public void assignChannelToIndividualSegment(
		FaroProject faroProject, String individualSegmentId, String channelId) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, individualSegmentId);

		uriVariables.put("channelId", channelId);

		put(
			faroProject, Rels.INDIVIDUAL_SEGMENT_ASSIGN_CHANNEL,
			individualSegmentId, Void.class, uriVariables);
	}

	@Override
	public void clearChannel(
			FaroProject faroProject, FaroUser faroUser, List<String> ids)
		throws FaroEngineClientException {

		post(
			faroProject, Rels.CHANNEL_CLEAR,
			HashMapBuilder.<String, Object>put(
				"channelIds", ids
			).put(
				"userId", faroUser.getUserId()
			).put(
				"userName", faroUser.getUserName()
			).build(),
			Void.class, getUriVariables(faroProject));
	}

	@Override
	public void deleteBlockedKeywords(FaroProject faroProject, List<String> ids)
		throws FaroEngineClientException {

		delete(faroProject, Rels.BLOCKED_KEYWORD, ids);
	}

	@Override
	public void deleteChannels(
		FaroProject faroProject, FaroUser faroUser, List<String> ids) {

		delete(
			faroProject, Rels.CHANNELS,
			HashMapBuilder.<String, Object>put(
				"channelIds", ids
			).put(
				"userId", faroUser.getUserId()
			).put(
				"userName", faroUser.getUserName()
			).build(),
			getUriVariables(faroProject));
	}

	@Override
	public void deleteData(
		FaroProject faroProject, String weDeployDataServiceName,
		String collectionName) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("collectionName", collectionName);
		uriVariables.put("weDeployDataServiceName", weDeployDataServiceName);

		delete(faroProject, Rels.ADMIN_DATA, uriVariables);
	}

	@Override
	public void deleteDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		delete(faroProject, Rels.DATA_SOURCE, id);
	}

	@Override
	public void deleteFieldMapping(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		delete(faroProject, Rels.FIELD_MAPPING, id);
	}

	@Override
	public void deleteFields(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		delete(faroProject, Rels.FIELD, id);
	}

	@Override
	public void deleteIndividualSegment(FaroProject faroProject, String id) {
		delete(faroProject, Rels.INDIVIDUAL_SEGMENT, id);
	}

	@Override
	public void deleteMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, individualSegmentId);

		uriVariables.put("individualId", individualId);

		delete(faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIP, uriVariables);
	}

	@Override
	public void deleteProject(FaroProject faroProject, boolean deleteData)
		throws Exception {

		delete(
			faroProject,
			HashMapBuilder.<String, List<String>>put(
				"deleteData",
				Collections.singletonList(String.valueOf(deleteData))
			).build());
	}

	@Override
	public void disconnectDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		post(faroProject, Rels.DATA_SOURCE_DISCONNECT, id, Void.class);
	}

	@Override
	public void disconnectDataSources(FaroProject faroProject)
		throws FaroEngineClientException {

		post(
			faroProject, Rels.DATA_SOURCE_DISCONNECT_ALL, null, Void.class,
			getUriVariables(faroProject));
	}

	@Override
	public <T> T get(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Class<T> responseType)
		throws Exception {

		return get(
			faroProject, headers, path, queryParameters, responseType,
			getUriVariables(faroProject));
	}

	@Override
	public Account getAccount(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		return get(faroProject, Rels.ACCOUNT, id, Account.class);
	}

	@Override
	public Results<IndividualSegment> getAccountIndividualSegments(
		FaroProject faroProject, String accountId, String channelId,
		String query, String status, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL_SEGMENT);

		uriVariables.put("expand", "active-membership");

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));
		filterBuilder.addFilter(
			"name", FilterConstants.STRING_FUNCTION_CONTAINS, query);
		filterBuilder.addFilter(
			"status", FilterConstants.COMPARISON_OPERATOR_EQUALS, status);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", accountId);

		PagedModel<?, IndividualSegment> pagedModel = get(
			faroProject, Rels.ACCOUNTS_INDIVIDUAL_SEGMENTS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualSegment>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Account> getAccounts(
		FaroProject faroProject, String channelId, String dataSourceId,
		String individualSegmentId, String filter, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_ACCOUNT);

		if (Validator.isNotNull(channelId)) {
			uriVariables.put("channelId", channelId);
		}

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"dataSourceId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			dataSourceId);
		filterBuilder.addFilter(filter);
		filterBuilder.addSearchFilter(
			query, fields, FilterConstants.FIELD_NAME_CONTEXT_ACCOUNT);

		uriVariables.put("filter", filterBuilder.build());

		String type = null;

		if (Validator.isNotNull(individualSegmentId)) {
			type = Rels.INDIVIDUAL_SEGMENT_ACCOUNTS;

			uriVariables.put("id", individualSegmentId);
		}
		else {
			type = Rels.ACCOUNTS;
		}

		PagedModel<?, Account> pagedModel = get(
			faroProject, type,
			new ParameterizedTypeReference<EntityModelPagedModel<Account>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Distribution> getAccountsDistribution(
		FaroProject faroProject, String channelId, String fieldMappingFieldName,
		String filter, String individualSegmentId, int count, int numberOfBins,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 0, count, orderByFields);

		uriVariables.put("channelId", channelId);
		uriVariables.put("fieldMappingFieldName", fieldMappingFieldName);
		uriVariables.put("filter", filter);
		uriVariables.put("individualSegmentId", individualSegmentId);
		uriVariables.put("numberOfBins", numberOfBins);

		PagedModel<?, Distribution> pagedModel = get(
			faroProject, Rels.ACCOUNTS_DISTRIBUTION,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Distribution>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Activity> getActivities(
		FaroProject faroProject, String ownerId, String ownerType,
		String groupId, String query, Date startDate, Date endDate, int action,
		int cur, int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		addActionFilter(filterBuilder, ActivityConstants.getActionKeys(action));

		filterBuilder.addFilter(
			"day", FilterConstants.COMPARISON_OPERATOR_GREATER_THAN_OR_EQUAL,
			getDate(startDate, false));
		filterBuilder.addFilter(
			"day", FilterConstants.COMPARISON_OPERATOR_LESS_THAN,
			getDate(endDate, true));
		filterBuilder.addFilter(
			"groupId", FilterConstants.COMPARISON_OPERATOR_EQUALS, groupId);

		addOwnerIdFilter(filterBuilder, ownerId, ownerType);

		filterBuilder.addSearchFilter(query, "object/name");

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, Activity> pagedModel = get(
			faroProject, Rels.ACTIVITIES,
			new ParameterizedTypeReference<EntityModelPagedModel<Activity>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public List<List<Activity>> getActivitiesList(
		FaroProject faroProject, List<String> groupIds, String query,
		int action, int cur, int delta, List<OrderByField> orderByFields) {

		return bulk(
			faroProject, Rels.ACTIVITIES, HttpMethod.GET,
			new TypeReference<List<Activity>>() {
			},
			TransformUtil.transform(
				groupIds,
				groupId -> {
					Map<String, Object> uriVariables = getUriVariables(
						faroProject, cur, delta, orderByFields);

					FilterBuilder filterBuilder = new FilterBuilder();

					addActionFilter(
						filterBuilder, ActivityConstants.getActionKeys(action));

					filterBuilder.addFilter(
						"groupId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
						groupId);
					filterBuilder.addSearchFilter(query, "object/name");

					uriVariables.put("filter", filterBuilder.build());

					return uriVariables;
				}));
	}

	@Override
	public Activity getActivity(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		return get(faroProject, Rels.ACTIVITY, id, Activity.class);
	}

	@Override
	public Results<ActivityAggregation> getActivityAggregations(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String rangeEnd, String rangeStart, String interval,
		int delta) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 1, delta, null);

		uriVariables.put("apply", getGroupBy("day", interval));

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));

		addActionFilter(
			filterBuilder,
			ActivityConstants.getActionKeys(ActivityConstants.ACTION_ANY));

		addOwnerIdFilter(filterBuilder, ownerId, ownerType);

		uriVariables.put("filter", filterBuilder.build());

		if (delta > 0) {
			uriVariables.put("includeToday", false);
		}

		if ((rangeEnd != null) && (rangeStart != null)) {
			uriVariables.put("rangeEnd", rangeEnd);
			uriVariables.put("rangeStart", rangeStart);
		}

		PagedModel<?, ActivityAggregation> pagedModel = get(
			faroProject, Rels.ACTIVITIES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<ActivityAggregation>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<ActivityAsset> getActivityAssets(
		FaroProject faroProject, String query, String applicationId,
		String channelId, String eventId, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"applicationId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			applicationId);
		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));
		filterBuilder.addFilter(
			"eventId", FilterConstants.COMPARISON_OPERATOR_EQUALS, eventId);
		filterBuilder.addSearchFilter(query, "assetTitle");

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, ActivityAsset> pagedModel = get(
			faroProject, Rels.ACTIVITY_ASSETS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<ActivityAsset>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<ActivityGroup> getActivityGroups(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String query, Date startDate, Date endDate, int cur,
		int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		uriVariables.put("expand", "activities,activities-count");

		FilterBuilder expandFilterBuilder = new FilterBuilder();

		addActionFilter(
			expandFilterBuilder,
			ActivityConstants.getActionKeys(ActivityConstants.ACTION_ANY));

		expandFilterBuilder.addSearchFilter(query, "object/name");

		uriVariables.put("expandFilter", expandFilterBuilder.build());

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));
		filterBuilder.addFilter(
			"day", FilterConstants.COMPARISON_OPERATOR_GREATER_THAN_OR_EQUAL,
			getDate(startDate, false));
		filterBuilder.addFilter(
			"day", FilterConstants.COMPARISON_OPERATOR_LESS_THAN,
			getDate(endDate, true));

		addOwnerIdFilter(filterBuilder, ownerId, ownerType);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, ActivityGroup> pagedModel = get(
			faroProject, Rels.ACTIVITY_GROUPS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<ActivityGroup>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Asset getAsset(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		return get(faroProject, Rels.ASSET, id, Asset.class);
	}

	@Override
	public Results<Asset> getAssets(
		FaroProject faroProject, String dataSourceId, String query, int action,
		String assetType, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		for (String actionAssetType : AssetConstants.getTypes(action)) {
			filterBuilder.addFilter(
				"assetType", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				actionAssetType, false);
		}

		if (StringUtil.equals(assetType, AssetConstants.TYPE_ASSET)) {
			filterBuilder.addFilter(
				"assetType", FilterConstants.COMPARISON_OPERATOR_NOT_EQUALS,
				Asset.AssetType.Page.name());
		}
		else {
			filterBuilder.addFilter(
				"assetType", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				assetType);
		}

		filterBuilder.addFilter(
			"dataSourceId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			dataSourceId);
		filterBuilder.addSearchFilter(query, "name");

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, Asset> pagedModel = get(
			faroProject, Rels.ASSETS,
			new ParameterizedTypeReference<EntityModelPagedModel<Asset>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public DataSource getAvailableTokenDataSource(FaroProject faroProject)
		throws FaroEngineClientException {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"credentials/type", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			TokenCredentials.TYPE);
		filterBuilder.addBlankFilter(
			"url", FilterConstants.COMPARISON_OPERATOR_EQUALS, null);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, DataSource> pagedModel = get(
			faroProject, Rels.DATA_SOURCES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DataSource>>() {
			},
			uriVariables);

		Results<DataSource> results = pagedModel.getResults();

		List<DataSource> dataSources = results.getItems();

		if (dataSources.isEmpty()) {
			return null;
		}

		return dataSources.get(0);
	}

	@Override
	public BlockedKeyword getBlockedKeyword(
		FaroProject faroProject, String id) {

		return get(faroProject, Rels.BLOCKED_KEYWORD, id, BlockedKeyword.class);
	}

	@Override
	public Results<BlockedKeyword> getBlockedKeywords(
		FaroProject faroProject, String query, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		uriVariables.put("keyword", query);

		PagedModel<?, BlockedKeyword> pagedModel = get(
			faroProject, Rels.BLOCKED_KEYWORDS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<BlockedKeyword>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Channel getChannel(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		Map<String, Object> uriVariables = getUriVariables(faroProject, id);

		uriVariables.put("expand", "data-sources");

		return get(faroProject, Rels.CHANNEL, id, Channel.class, uriVariables);
	}

	@Override
	public Results<Channel> getChannels(
		FaroProject faroProject, int cur, int delta, List<String> ids,
		List<OrderByField> orderByFields) {

		PagedModel<?, Channel> pagedModel = get(
			faroProject, Rels.CHANNELS,
			new ParameterizedTypeReference<EntityModelPagedModel<Channel>>() {
			},
			getUriVariables(faroProject, cur, delta, ids, orderByFields));

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getCoworkerIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		return new Results<>();
	}

	@Override
	public DataSource getDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		return get(faroProject, Rels.DATA_SOURCE, id, DataSource.class);
	}

	@Override
	public List<DXPGroup> getDataSourceDXPGroups(
		FaroProject faroProject, String id, List<Long> groupIds) {

		if (ListUtil.isEmpty(groupIds)) {
			return Collections.emptyList();
		}

		PagedModel<?, DXPGroup> pagedModel = post(
			faroProject, Rels.DATA_SOURCE_DXP_GROUPS, groupIds,
			new ParameterizedTypeReference<EntityModelPagedModel<DXPGroup>>() {
			},
			getUriVariables(faroProject, id));

		Results<DXPGroup> results = pagedModel.getResults();

		return results.getItems();
	}

	@Override
	public Results<DXPGroup> getDataSourceDXPGroups(
		FaroProject faroProject, String id, long parentGroupId, boolean site,
		String name, int cur, int delta) {

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			cur, delta);

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, startAndEnd[0], startAndEnd[1]);

		uriVariables.put("id", id);

		if (name != null) {
			uriVariables.put("name", getName(name));
		}

		uriVariables.put("parentGroupId", parentGroupId);
		uriVariables.put("site", site);

		PagedModel<?, DXPGroup> pagedModel = get(
			faroProject, Rels.DATA_SOURCE_DXP_GROUPS,
			new ParameterizedTypeReference<EntityModelPagedModel<DXPGroup>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public List<DXPOrganization> getDataSourceDXPOrganizations(
		FaroProject faroProject, String id, List<Long> organizationIds) {

		if (ListUtil.isEmpty(organizationIds)) {
			return Collections.emptyList();
		}

		PagedModel<?, DXPOrganization> pagedModel = post(
			faroProject, Rels.DATA_SOURCE_DXP_ORGANIZATIONS, organizationIds,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DXPOrganization>>() {
			},
			getUriVariables(faroProject, id));

		Results<DXPOrganization> results = pagedModel.getResults();

		return results.getItems();
	}

	@Override
	public Results<DXPOrganization> getDataSourceDXPOrganizations(
		FaroProject faroProject, String id, long parentOrganizationId,
		String name, int cur, int delta) {

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			cur, delta);

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, startAndEnd[0], startAndEnd[1]);

		uriVariables.put("id", id);

		if (name != null) {
			uriVariables.put("name", getName(name));
		}

		uriVariables.put("parentOrganizationId", parentOrganizationId);

		PagedModel<?, DXPOrganization> pagedModel = get(
			faroProject, Rels.DATA_SOURCE_DXP_ORGANIZATIONS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DXPOrganization>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public long getDataSourceDXPTotal(FaroProject faroProject, String id) {
		return get(
			faroProject, Rels.DATA_SOURCE_DXP_USERS_TOTAL, id, Long.class);
	}

	@Override
	public long getDataSourceDXPTotal(
		FaroProject faroProject, String id,
		LiferayProvider.ContactsConfiguration contactsConfiguration) {

		return post(
			faroProject, Rels.DATA_SOURCE_DXP_USERS_TOTAL,
			contactsConfiguration, Long.class,
			getUriVariables(faroProject, id));
	}

	@Override
	public List<DXPUserGroup> getDataSourceDXPUserGroups(
		FaroProject faroProject, String id, List<Long> userGroupIds) {

		if (ListUtil.isEmpty(userGroupIds)) {
			return Collections.emptyList();
		}

		PagedModel<?, DXPUserGroup> pagedModel = post(
			faroProject, Rels.DATA_SOURCE_DXP_USER_GROUPS, userGroupIds,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DXPUserGroup>>() {
			},
			getUriVariables(faroProject, id));

		Results<DXPUserGroup> results = pagedModel.getResults();

		return results.getItems();
	}

	@Override
	public Results<DXPUserGroup> getDataSourceDXPUserGroups(
		FaroProject faroProject, String id, String name, int cur, int delta) {

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			cur, delta);

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, startAndEnd[0], startAndEnd[1]);

		uriVariables.put("id", id);
		uriVariables.put("name", getName(name));

		PagedModel<?, DXPUserGroup> pagedModel = get(
			faroProject, Rels.DATA_SOURCE_DXP_USER_GROUPS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DXPUserGroup>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public List<DataSourceField> getDataSourceFields(
		FaroProject faroProject, String id, String context, int count) {

		String type = null;

		DataSource dataSource = getDataSource(faroProject, id);

		Provider provider = dataSource.getProvider();

		if (StringUtil.equals(provider.getType(), SalesforceProvider.TYPE)) {
			if (context.equals(FieldMappingConstants.CONTEXT_DEMOGRAPHICS)) {
				type = Rels.DATA_SOURCE_SALESFORCE_USERS_FIELDS;
			}
			else {
				type = Rels.DATA_SOURCE_SALESFORCE_ACCOUNTS_FIELDS;
			}
		}
		else {
			type = Rels.DATA_SOURCE_DXP_USERS_FIELDS;
		}

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 0, count);

		uriVariables.put("id", id);

		return get(
			faroProject, type,
			new ParameterizedTypeReference<List<DataSourceField>>() {
			},
			uriVariables);
	}

	@Override
	public Map<String, DataSourceProgress> getDataSourceProgressMap(
		FaroProject faroProject, String id) {

		return get(
			faroProject, Rels.DATA_SOURCE_PROGRESS,
			new ParameterizedTypeReference<Map<String, DataSourceProgress>>() {
			},
			getUriVariables(faroProject, id));
	}

	@Override
	public Results<DataSource> getDataSources(
		FaroProject faroProject, List<String> channelIds) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		FilterBuilder filterBuilder = new FilterBuilder();

		if (ListUtil.isNotNull(channelIds)) {
			FilterBuilder channelIdsFilterBuilder = new FilterBuilder();

			for (String channelId : channelIds) {
				channelIdsFilterBuilder.addFilter(
					"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
					channelId, false);
			}

			filterBuilder.addFilter(channelIdsFilterBuilder, true);
		}

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, DataSource> pagedModel = get(
			faroProject, Rels.DATA_SOURCES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DataSource>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<DataSource> getDataSources(
		FaroProject faroProject, String faroEntityId, String query, String name,
		String providerType, List<String> states, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		uriVariables.put("faroEntityId", faroEntityId);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addSearchFilter(query, "name");
		filterBuilder.addFilter(
			"name", FilterConstants.COMPARISON_OPERATOR_EQUALS, name);
		filterBuilder.addFilter(
			"provider/type", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			providerType);

		if (ListUtil.isNotNull(states)) {
			FilterBuilder statesFilterBuilder = new FilterBuilder();

			for (String state : states) {
				statesFilterBuilder.addFilter(
					"state", FilterConstants.COMPARISON_OPERATOR_EQUALS, state,
					false);
			}

			filterBuilder.addFilter(statesFilterBuilder, true);
		}

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, DataSource> pagedModel = get(
			faroProject, Rels.DATA_SOURCES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DataSource>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Long getEnrichedProfilesCount(
		FaroProject faroProject, Long channelId) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("channelId", channelId);

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<Long> responseEntity = restTemplate.exchange(
			getTemplatedURL(
				faroProject, Rels.INDIVIDUALS_ENRICHED_PROFILES_COUNT),
			HttpMethod.GET, HttpEntity.EMPTY, Long.class, uriVariables);

		if (responseEntity.getBody() == null) {
			return 0L;
		}

		return responseEntity.getBody();
	}

	@Override
	public Field getField(FaroProject faroProject, String id)
		throws FaroEngineClientException {

		return get(faroProject, Rels.FIELD, id, Field.class);
	}

	@Override
	public FieldMapping getFieldMapping(
			FaroProject faroProject, String fieldName)
		throws FaroEngineClientException {

		return get(
			faroProject, Rels.FIELD_MAPPING, fieldName, FieldMapping.class);
	}

	@Override
	public FieldMapping getFieldMapping(
		FaroProject faroProject, String context, String fieldName) {

		Results<FieldMapping> results = getFieldMappings(
			faroProject, context, Collections.singletonList(fieldName), 1, 1,
			null);

		List<FieldMapping> fieldMappings = results.getItems();

		if (fieldMappings.isEmpty()) {
			return null;
		}

		return fieldMappings.get(0);
	}

	@Override
	public Results<FieldMapping> getFieldMappings(
			FaroProject faroProject, String context, List<String> fieldNames,
			int cur, int delta, List<OrderByField> orderByFields)
		throws FaroEngineClientException {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);

		FilterBuilder fieldNameFilterBuilder = new FilterBuilder();

		for (String fieldName : fieldNames) {
			fieldNameFilterBuilder.addFilter(
				"fieldName", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				fieldName, false);
		}

		filterBuilder.addFilter(fieldNameFilterBuilder, true);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, FieldMapping> pagedModel = get(
			faroProject, Rels.FIELD_MAPPINGS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<FieldMapping>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<FieldMapping> getFieldMappings(
		FaroProject faroProject, String context, String dataSourceId,
		String dataSourceFieldName) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 1, 10000, null);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);

		if (Validator.isNull(dataSourceFieldName)) {
			filterBuilder.addNullFilter(
				"dataSourceFieldNames/" + dataSourceId,
				FilterConstants.COMPARISON_OPERATOR_NOT_EQUALS);
		}
		else {
			filterBuilder.addFilter(
				"dataSourceFieldNames/" + dataSourceId,
				FilterConstants.COMPARISON_OPERATOR_EQUALS,
				dataSourceFieldName);
		}

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, FieldMapping> pagedModel = get(
			faroProject, Rels.FIELD_MAPPINGS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<FieldMapping>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<FieldMapping> getFieldMappings(
		FaroProject faroProject, String context, String displayName,
		String ownerType, String query, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);
		filterBuilder.addFilter(
			"displayName", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			displayName);
		filterBuilder.addFilter(
			"displayName", FilterConstants.STRING_FUNCTION_CONTAINS, query);
		filterBuilder.addFilter(
			"ownerType", FilterConstants.COMPARISON_OPERATOR_EQUALS, ownerType);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, FieldMapping> pagedModel = get(
			faroProject, Rels.FIELD_MAPPINGS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<FieldMapping>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public List<String> getFieldNames(
		FaroProject faroProject, String label, String ownerType,
		Object values) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("label", label);
		uriVariables.put("ownerType", ownerType);
		uriVariables.put("values", values);

		return get(
			faroProject, Rels.FIELD_NAMES,
			new ParameterizedTypeReference<List<String>>() {
			},
			uriVariables);
	}

	@Override
	public List<List<String>> getFieldNamesList(
		FaroProject faroProject, List<String> labels, String ownerType,
		List<Object> valuesList) {

		List<Map<String, Object>> uriVariablesList = new ArrayList<>();

		for (int i = 0; i < labels.size(); i++) {
			Map<String, Object> uriVariables = getUriVariables(faroProject);

			uriVariables.put("label", labels.get(i));
			uriVariables.put("ownerType", ownerType);
			uriVariables.put("values", valuesList.get(i));

			uriVariablesList.add(uriVariables);
		}

		return bulk(
			faroProject, Rels.FIELD_NAMES, HttpMethod.GET,
			new TypeReference<List<String>>() {
			},
			uriVariablesList);
	}

	@Override
	public Results<Field> getFields(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields) {

		PagedModel<?, Field> pagedModel = get(
			faroProject, Rels.FIELDS,
			new ParameterizedTypeReference<EntityModelPagedModel<Field>>() {
			},
			getUriVariables(faroProject, cur, delta, orderByFields));

		return pagedModel.getResults();
	}

	@Override
	public Results<Field> getFields(
		FaroProject faroProject, String context, String name, int cur,
		int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);
		filterBuilder.addFilter(
			"name", FilterConstants.COMPARISON_OPERATOR_EQUALS, name);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, Field> pagedModel = get(
			faroProject, Rels.FIELDS,
			new ParameterizedTypeReference<EntityModelPagedModel<Field>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Field> getFields(
		FaroProject faroProject, String context, String name, String ownerId,
		String ownerType, Date startDate, Date endDate, int interval,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 1, 10000, orderByFields);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);
		filterBuilder.addFilter(
			"dateModified",
			FilterConstants.COMPARISON_OPERATOR_GREATER_THAN_OR_EQUAL,
			getDate(startDate, false));
		filterBuilder.addFilter(
			"dateModified",
			FilterConstants.COMPARISON_OPERATOR_LESS_THAN_OR_EQUAL,
			getDate(endDate, true));
		filterBuilder.addFilter(
			"name", FilterConstants.COMPARISON_OPERATOR_EQUALS, name);
		filterBuilder.addFilter(
			"ownerId", FilterConstants.COMPARISON_OPERATOR_EQUALS, ownerId);
		filterBuilder.addFilter(
			"ownerType", FilterConstants.COMPARISON_OPERATOR_EQUALS, ownerType);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, Field> pagedModel = get(
			faroProject, Rels.FIELDS,
			new ParameterizedTypeReference<EntityModelPagedModel<Field>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public List<List<Field>> getFieldsList(
		FaroProject faroProject, String context, List<String> names, int cur,
		int delta, List<OrderByField> orderByFields) {

		List<Map<String, Object>> uriVariablesList = new ArrayList<>();

		for (String name : names) {
			Map<String, Object> uriVariables = getUriVariables(
				faroProject, cur, delta, orderByFields);

			FilterBuilder filterBuilder = new FilterBuilder();

			filterBuilder.addFilter(
				"context", FilterConstants.COMPARISON_OPERATOR_EQUALS, context);
			filterBuilder.addFilter(
				"name", FilterConstants.COMPARISON_OPERATOR_EQUALS, name);

			uriVariables.put("filter", filterBuilder.build());

			uriVariablesList.add(uriVariables);
		}

		return bulk(
			faroProject, Rels.FIELDS, HttpMethod.GET,
			new TypeReference<List<Field>>() {
			},
			uriVariablesList);
	}

	@Override
	public Results<Object> getFieldValues(
		FaroProject faroProject, Long channelId, String query,
		String fieldMappingFieldName, int cur, int delta) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, null);

		FieldMapping fieldMapping = getFieldMapping(
			faroProject, fieldMappingFieldName);

		String type = null;

		if (StringUtil.equals(
				fieldMapping.getOwnerType(),
				FieldMappingConstants.OWNER_TYPE_ACCOUNT)) {

			type = Rels.ACCOUNTS;
		}
		else if (StringUtil.equals(
					fieldMapping.getOwnerType(),
					FieldMappingConstants.OWNER_TYPE_INDIVIDUAL)) {

			type = Rels.INDIVIDUALS;
		}
		else if (StringUtil.equals(
					fieldMapping.getOwnerType(),
					FieldMappingConstants.OWNER_TYPE_ORGANIZATION)) {

			type = Rels.ORGANIZATIONS;
		}
		else {
			return new Results<>();
		}

		uriVariables.put("apply", getGroupBy(fieldMapping));

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addSearchFilter(
			query, fieldMapping.getFieldName(),
			fieldMapping.getContext() + "/?/value");

		uriVariables.put("channelId", channelId);
		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, IndividualTransformation> pagedModel = get(
			faroProject, type,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualTransformation>>() {
			},
			uriVariables);

		Results<IndividualTransformation> results = pagedModel.getResults();

		List<IndividualTransformation> individualTransformations =
			results.getItems();

		return new Results<>(
			TransformUtil.transform(
				individualTransformations,
				individualTransformation -> {
					Map<String, Object> terms =
						individualTransformation.getTerms();

					List<Object> objects = new ArrayList<>(terms.values());

					objects.get(0);

					return objects.get(0);
				}),
			results.getTotal());
	}

	@Override
	public long getIdentitiesCount(FaroProject faroProject) {
		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<Long> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, Rels.IDENTITIES_COUNT), HttpMethod.GET,
			HttpEntity.EMPTY, Long.class, getUriVariables(faroProject));

		if (Validator.isNotNull(responseEntity.getBody())) {
			return responseEntity.getBody();
		}

		return 0L;
	}

	@Override
	public Individual getIndividual(
			FaroProject faroProject, String id, String channelId)
		throws FaroEngineClientException {

		Map<String, Object> uriVariables = getUriVariables(faroProject, id);

		if (Validator.isNotNull(channelId)) {
			uriVariables.put("channelId", channelId);
		}

		return get(
			faroProject, Rels.INDIVIDUAL, id, Individual.class, uriVariables);
	}

	@Override
	public List<FieldMapping> getIndividualAttributes(
		FaroProject faroProject, String displayName) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		if (Validator.isNotNull(displayName)) {
			uriVariables.put("displayName", displayName);
		}

		PagedModel<?, FieldMapping> pagedModel = get(
			faroProject, Rels.DEFINITIONS_INDIVIDUAL_ATTRIBUTES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<FieldMapping>>() {
			},
			uriVariables);

		Results<FieldMapping> results = pagedModel.getResults();

		return results.getItems();
	}

	@Override
	public Results<IndividualSegment> getIndividualIndividualSegments(
		FaroProject faroProject, String channelId, String individualId,
		String query, String status, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL_SEGMENT);

		uriVariables.put("expand", "active-membership");

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));
		filterBuilder.addFilter(
			"name", FilterConstants.STRING_FUNCTION_CONTAINS, query);
		filterBuilder.addFilter(
			"status", FilterConstants.COMPARISON_OPERATOR_EQUALS, status);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", individualId);

		PagedModel<?, IndividualSegment> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_INDIVIDUAL_SEGMENTS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualSegment>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getIndividuals(
		FaroProject faroProject, FilterBuilder filterBuilder,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("filter", filterBuilder.build());
		uriVariables.put("includeAnonymousUsers", includeAnonymousUsers);

		PagedModel<?, Individual> pagedModel = get(
			faroProject, Rels.INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Individual>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getIndividuals(
		FaroProject faroProject, String dataSourceId,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put(
			"filter",
			FilterUtil.getFilter(
				"dataSourceId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				dataSourceId));
		uriVariables.put("includeAnonymousUsers", includeAnonymousUsers);

		PagedModel<?, Individual> pagedModel = get(
			faroProject, Rels.INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Individual>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getIndividuals(
		FaroProject faroProject, String accountId, String channelId,
		String dataSourceId, String individualSegmentId,
		String notIndividualSegmentId, String interestName, String filter,
		String query, List<String> fields, boolean includeAnonymousUsers,
		int cur, int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		if (Validator.isNotNull(accountId)) {
			uriVariables.put("accountId", accountId);
		}

		if (Validator.isNotNull(channelId)) {
			uriVariables.put("channelId", channelId);
		}

		if (Validator.isNotNull(dataSourceId)) {
			uriVariables.put("dataSourceId", dataSourceId);
		}

		if (Validator.isNotNull(filter)) {
			uriVariables.put("filter", filter);
		}

		uriVariables.put("includeAnonymousUsers", includeAnonymousUsers);

		if (Validator.isNotNull(individualSegmentId)) {
			uriVariables.put("segmentId", individualSegmentId);
		}

		if (Validator.isNotNull(interestName)) {
			uriVariables.put("interestName", interestName);
		}

		if (Validator.isNotNull(notIndividualSegmentId)) {
			uriVariables.put("notSegmentId", notIndividualSegmentId);
		}

		if (Validator.isNotNull(query)) {
			uriVariables.put("query", query);
		}

		PagedModel<?, Individual> pagedModel = get(
			faroProject, Rels.INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Individual>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getIndividualsByIndividualSegment(
		FaroProject faroProject, String individualSegmentId, String query,
		List<String> fields, FilterBuilder filterBuilder,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		if (filterBuilder == null) {
			filterBuilder = new FilterBuilder();
		}

		filterBuilder.addSearchFilter(
			query, fields, FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", individualSegmentId);
		uriVariables.put("includeAnonymousUsers", includeAnonymousUsers);

		PagedModel<?, Individual> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Individual>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getIndividualsByIndividualSegment(
		FaroProject faroProject, String individualSegmentId, String filter,
		String query, List<String> fields, boolean includeAnonymousUsers,
		int cur, int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(filter);
		filterBuilder.addSearchFilter(
			query, fields, FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", individualSegmentId);
		uriVariables.put("includeAnonymousUsers", includeAnonymousUsers);

		PagedModel<?, Individual> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Individual>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public long getIndividualsCreatedBetweenCount(
		FaroProject faroProject, Date endDate, Date startDate) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("endDate", endDate);
		uriVariables.put("startDate", startDate);

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<Long> responseEntity = restTemplate.exchange(
			getTemplatedURL(
				faroProject, Rels.INDIVIDUALS_CREATED_BETWEEN_COUNT),
			HttpMethod.GET, HttpEntity.EMPTY, Long.class, uriVariables);

		if (responseEntity.getBody() == null) {
			return 0L;
		}

		return responseEntity.getBody();
	}

	@Override
	public long getIndividualsCreatedSinceCount(
		FaroProject faroProject, Date startDate) {

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("startDate", startDate);

		RestTemplate restTemplate = getRestTemplate(faroProject);

		ResponseEntity<Long> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, Rels.INDIVIDUALS_CREATED_SINCE_COUNT),
			HttpMethod.GET, HttpEntity.EMPTY, Long.class, uriVariables);

		if (responseEntity.getBody() == null) {
			return 0L;
		}

		return responseEntity.getBody();
	}

	@Override
	public Results<Distribution> getIndividualsDistribution(
		FaroProject faroProject, String channelId, String fieldMappingFieldName,
		String individualSegmentId, int count, int numberOfBins,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 0, count, orderByFields);

		uriVariables.put("channelId", channelId);
		uriVariables.put("fieldMappingFieldName", fieldMappingFieldName);
		uriVariables.put("individualSegmentId", individualSegmentId);
		uriVariables.put("numberOfBins", numberOfBins);

		PagedModel<?, Distribution> pagedModel = get(
			faroProject, Rels.INDIVIDUALS_DISTRIBUTION,
			new ParameterizedTypeReference
				<EntityModelPagedModel<Distribution>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public IndividualSegment getIndividualSegment(
			FaroProject faroProject, String id,
			boolean includeReferencedObjects)
		throws FaroEngineClientException {

		Map<String, Object> uriVariables = getUriVariables(faroProject, id);

		if (includeReferencedObjects) {
			uriVariables.put("expand", "referenced-objects");
		}

		return get(
			faroProject, Rels.INDIVIDUAL_SEGMENT, id, IndividualSegment.class,
			uriVariables);
	}

	@Override
	public IndividualSegmentMembership getIndividualSegmentMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, individualSegmentId);

		uriVariables.put("individualId", individualId);

		return get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIP,
			new ParameterizedTypeReference<IndividualSegmentMembership>() {
			},
			uriVariables);
	}

	@Override
	public Results<IndividualSegmentMembershipChangeAggregation>
		getIndividualSegmentMembershipChangeAggregations(
			FaroProject faroProject, String individualSegmentId,
			String interval, int delta) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 1, delta + 1, null);

		uriVariables.put("apply", getGroupBy("dateChanged", interval));
		uriVariables.put("id", individualSegmentId);

		PagedModel<?, IndividualSegmentMembershipChangeAggregation> pagedModel =
			get(
				faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIP_CHANGES,
				new ParameterizedTypeReference
					<EntityModelPagedModel
						<IndividualSegmentMembershipChangeAggregation>>() {
				},
				uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<IndividualSegmentMembershipChange>
		getIndividualSegmentMembershipChanges(
			FaroProject faroProject, String individualSegmentId, String query,
			Date startDate, Date endDate, int cur, int delta,
			List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("expand", "account-names");

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"dateChanged",
			FilterConstants.COMPARISON_OPERATOR_GREATER_THAN_OR_EQUAL,
			getDate(startDate, false));
		filterBuilder.addFilter(
			"dateChanged", FilterConstants.COMPARISON_OPERATOR_LESS_THAN,
			getDate(endDate, true));
		filterBuilder.addSearchFilter(
			query, Arrays.asList("individualEmail", "individualName"), null);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", individualSegmentId);

		PagedModel<?, IndividualSegmentMembershipChange> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIP_CHANGES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualSegmentMembershipChange>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<IndividualSegmentMembership> getIndividualSegmentMemberships(
		FaroProject faroProject, String individualSegmentId, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("id", individualSegmentId);

		PagedModel<?, IndividualSegmentMembership> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_MEMBERSHIPS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualSegmentMembership>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<IndividualSegment> getIndividualSegments(
		FaroProject faroProject, String channelId, String dataSourceId,
		String query, List<String> fields, String name, String segmentType,
		String state, String status, int cur, int delta,
		List<OrderByField> orderByFields) {

		PagedModel<?, IndividualSegment> pagedModel = null;

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL_SEGMENT);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"name", FilterConstants.COMPARISON_OPERATOR_EQUALS, name);

		if (channelId == null) {
			filterBuilder.addNullFilter(
				"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS);
		}
		else {
			filterBuilder.addFilter(
				"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				Long.valueOf(channelId));
		}

		filterBuilder.addFilter(
			"type", FilterConstants.COMPARISON_OPERATOR_EQUALS, segmentType);
		filterBuilder.addFilter(
			"state", FilterConstants.COMPARISON_OPERATOR_EQUALS, state);
		filterBuilder.addFilter(
			"status", FilterConstants.COMPARISON_OPERATOR_EQUALS, status);
		filterBuilder.addSearchFilter(query, fields, null);

		uriVariables.put("filter", filterBuilder.build());

		if (Validator.isNotNull(dataSourceId)) {
			uriVariables.put("dataSourceId", dataSourceId);

			pagedModel = get(
				faroProject, Rels.PREVIEW_DISABLED_SEGMENTS,
				new ParameterizedTypeReference
					<EntityModelPagedModel<IndividualSegment>>() {
				},
				uriVariables);
		}
		else {
			pagedModel = get(
				faroProject, Rels.INDIVIDUAL_SEGMENTS,
				new ParameterizedTypeReference
					<EntityModelPagedModel<IndividualSegment>>() {
				},
				uriVariables);
		}

		return pagedModel.getResults();
	}

	@Override
	public Results<IndividualTransformation> getIndividualTransformations(
		FaroProject faroProject, String individualSegmentId, String query,
		List<String> fields, String fieldMappingFieldName, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		FieldMapping fieldMapping = getFieldMapping(
			faroProject, fieldMappingFieldName);

		uriVariables.put("apply", getGroupBy(fieldMapping));

		FilterBuilder filterBuilder = new FilterBuilder();

		if (Objects.equals(
				fieldMapping.getFieldType(), FieldMappingConstants.TYPE_TEXT)) {

			filterBuilder.addBlankFilter(
				fieldMapping.getFieldName(),
				FilterConstants.COMPARISON_OPERATOR_NOT_EQUALS,
				FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);
		}

		filterBuilder.addSearchFilter(
			query, fields, FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL);

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("id", individualSegmentId);

		PagedModel<?, IndividualTransformation> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENT_INDIVIDUALS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualTransformation>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<String> getInterestKeywords(
		String channelId, FaroProject faroProject, String query, int cur,
		int delta) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, null);

		uriVariables.put("channelId", channelId);
		uriVariables.put("name", query);

		PagedModel<?, String> pagedModel = get(
			faroProject, Rels.INTEREST_KEYWORDS,
			new ParameterizedTypeReference<StringPagedModel>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Interest> getInterests(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String name, String query, String expand, int cur,
		int delta, List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		if (Validator.isNotNull(channelId)) {
			uriVariables.put("channelId", Long.valueOf(channelId));
		}

		uriVariables.put("expand", expand);
		uriVariables.put("name", name);
		uriVariables.put("ownerId", ownerId);
		uriVariables.put("query", query);

		PagedModel<?, Interest> pagedModel = get(
			faroProject, Rels.INTERESTS,
			new ParameterizedTypeReference<EntityModelPagedModel<Interest>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Date getLastSeenDate(FaroProject faroProject) {
		return get(
			faroProject, Rels.PROJECTS_LAST_SEEN_DATE,
			faroProject.getProjectId(), Date.class);
	}

	@Override
	public Results<PageVisited> getPagesVisited(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String query, String interestName, Date startDate,
		Date endDate, boolean visitedPages, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields);

		uriVariables.put("channelId", channelId);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"interestName", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			interestName);
		filterBuilder.addSearchFilter(query, "title");

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("ownerId", ownerId);
		uriVariables.put("ownerType", ownerType);
		uriVariables.put("visitedPages", visitedPages);

		PagedModel<?, PageVisited> pagedModel = get(
			faroProject, Rels.PAGES_VISITED,
			new ParameterizedTypeReference
				<EntityModelPagedModel<PageVisited>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public PageVisited getPageVisited(FaroProject faroProject, String id) {
		return get(faroProject, Rels.PAGE_VISITED, id, PageVisited.class);
	}

	@Override
	public Results<ProjectUsageMetric> getProjectUsageMetrics(
		FaroProject faroProject, Date sinceDate) {

		PagedModel<?, ProjectUsageMetric> pagedModel = get(
			faroProject, Rels.PROJECT_USAGE_METRICS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<ProjectUsageMetric>>() {
			},
			HashMapBuilder.<String, Object>put(
				"createDate", sinceDate
			).build());

		return pagedModel.getResults();
	}

	@Override
	public long getReportsExportCSVCount(
			FaroProject faroProject, String path,
			Map<String, List<String>> queryParameters)
		throws Exception {

		return get(
			faroProject, Collections.emptyMap(), path, queryParameters,
			Long.class);
	}

	@Override
	public Results<String> getSessionValues(
		FaroProject faroProject, String channelId, String fieldName,
		String filter, String query, int cur, int delta) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, null);

		uriVariables.put("fieldName", fieldName);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(filter);
		filterBuilder.addFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			Long.valueOf(channelId));

		uriVariables.put("filter", filterBuilder.build());

		uriVariables.put("value", query);

		PagedModel<?, String> pagedModel = get(
			faroProject, Rels.SESSION_VALUES,
			new ParameterizedTypeReference<StringPagedModel>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public Results<Individual> getSimilarIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields) {

		return new Results<>();
	}

	@Override
	public long getSyncedIndividualsCount(FaroProject faroProject) {
		RestTemplate restTemplate = getRestTemplate(faroProject);

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("includeSuppressed", true);

		ResponseEntity<Long> responseEntity = restTemplate.exchange(
			getTemplatedURL(faroProject, Rels.INDIVIDUALS_COUNT),
			HttpMethod.GET, HttpEntity.EMPTY, Long.class, uriVariables);

		if (responseEntity.getBody() == null) {
			return 0L;
		}

		return responseEntity.getBody();
	}

	@Override
	public void getToOutputStream(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters,
			OutputStream outputStream)
		throws Exception {

		RestTemplate restTemplate = getRestTemplate(faroProject);

		RequestCallback requestCallback = clientHttpRequest -> {
			HttpHeaders httpHeaders = clientHttpRequest.getHeaders();

			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpHeaders.set(entry.getKey(), entry.getValue());
			}
		};

		ResponseExtractor<Void> responseExtractor = clientHttpResponse -> {
			StreamUtils.copy(clientHttpResponse.getBody(), outputStream);

			return null;
		};

		restTemplate.execute(
			getUriString(faroProject, path, queryParameters), HttpMethod.GET,
			requestCallback, responseExtractor, getUriVariables(faroProject));
	}

	@Override
	public Results<IndividualSegment> getUnassignedIndividualSegments(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields) {

		Map<String, Object> uriVariables = getUriVariables(
			faroProject, cur, delta, orderByFields,
			FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL_SEGMENT);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addNullFilter(
			"channelId", FilterConstants.COMPARISON_OPERATOR_EQUALS);
		filterBuilder.addFilter(
			"status", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			IndividualSegment.Status.ACTIVE.name());

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, IndividualSegment> pagedModel = get(
			faroProject, Rels.INDIVIDUAL_SEGMENTS,
			new ParameterizedTypeReference
				<EntityModelPagedModel<IndividualSegment>>() {
			},
			uriVariables);

		return pagedModel.getResults();
	}

	@Override
	public void insertBQProjects(List<FaroProject> faroProjects)
		throws Exception {

		List<AsahProject> asahProjects = new ArrayList<>();

		for (FaroProject faroProject : faroProjects) {
			asahProjects.add(
				new AsahProject(
					faroProject.getProjectId(), faroProject.getStartDate()));
		}

		post(
			faroProjects.get(0), Collections.emptyMap(), "/bq-projects",
			Collections.emptyMap(), new AsahProject(asahProjects), Void.class,
			Collections.emptyMap());
	}

	@Override
	public Channel patchChannel(
		FaroProject faroProject, String id, String name) {

		Map<String, Object> channelPatch = new HashMap<>();

		if (Validator.isNotNull(name)) {
			channelPatch.put("name", name);
		}

		Map<String, Object> patchChannelObject = patch(
			faroProject, Rels.CHANNEL, id, channelPatch, Map.class);

		return objectMapper.convertValue(
			patchChannelObject.get("channel"), Channel.class);
	}

	@Override
	public Channel patchChannel(
		FaroProject faroProject, String id, String dataSourceId,
		List<Map<String, String>> groups) {

		Map<String, Object> patchChannelObject = patch(
			faroProject, Rels.CHANNEL, id,
			HashMapBuilder.<String, Object>put(
				"dataSourceId", dataSourceId
			).put(
				"groups", groups
			).build(),
			Map.class);

		return objectMapper.convertValue(
			patchChannelObject.get("channel"), Channel.class);
	}

	@Override
	public DataSource patchDataSource(
		FaroProject faroProject, String id, Credentials credentials,
		long userId, String name, String url, Provider provider, Event event,
		String status) {

		Map<String, Object> dataSourcePatch = new HashMap<>();

		Author author = getAuthor(userId);

		if (author != null) {
			dataSourcePatch.put("author", author);
		}

		if (credentials != null) {
			dataSourcePatch.put("credentials", credentials);
		}

		if (event != null) {
			dataSourcePatch.put("event", event);
		}

		if (Validator.isNotNull(name)) {
			dataSourcePatch.put("name", name);
		}

		if (provider != null) {
			dataSourcePatch.put("provider", provider);
		}

		if (Validator.isNotNull(status)) {
			dataSourcePatch.put("status", status);
		}

		if (Validator.isNotNull(url)) {
			dataSourcePatch.put("url", url);
		}

		return patch(
			faroProject, Rels.DATA_SOURCE, id, dataSourcePatch,
			DataSource.class);
	}

	@Override
	public FieldMapping patchFieldMapping(
		FaroProject faroProject, String id, String dataSourceId,
		String fieldName) {

		return patch(
			faroProject, Rels.FIELD_MAPPING, id,
			HashMapBuilder.<String, Object>put(
				"dataSourceId", dataSourceId
			).put(
				"fieldName", fieldName
			).build(),
			FieldMapping.class);
	}

	@Override
	public void patchFieldMappings(
		FaroProject faroProject, String dataSourceId, String context,
		String ownerType, List<FieldMappingMap> fieldMappingMaps) {

		if (ListUtil.isEmpty(fieldMappingMaps)) {
			return;
		}

		Map<String, String> fieldMappings = new HashMap<>();

		for (FieldMappingMap fieldMappingMap : fieldMappingMaps) {
			fieldMappings.put(
				fieldMappingMap.getName(),
				fieldMappingMap.getDataSourceFieldName());
		}

		Map<String, Object> uriVariables = getUriVariables(faroProject);

		uriVariables.put("context", context);
		uriVariables.put("dataSourceId", dataSourceId);
		uriVariables.put("ownerType", ownerType);

		patch(
			faroProject, Rels.FIELD_MAPPINGS, fieldMappings, Object.class,
			uriVariables);
	}

	@Override
	public <T> T post(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Object requestBody,
			Class<T> responseType)
		throws Exception {

		return post(
			faroProject, headers, path, queryParameters, requestBody,
			responseType, getUriVariables(faroProject));
	}

	@Override
	public List<Map<String, Object>> refreshLiferay(FaroProject faroProject) {
		Map<String, Object> uriVariables = getUriVariables(
			faroProject, 1, 10000, null);

		FilterBuilder filterBuilder = new FilterBuilder();

		filterBuilder.addFilter(
			"provider/type", FilterConstants.COMPARISON_OPERATOR_EQUALS,
			LiferayProvider.TYPE);
		filterBuilder.addNullFilter(
			"workspaceURL", FilterConstants.COMPARISON_OPERATOR_EQUALS);

		uriVariables.put("filter", filterBuilder.build());

		PagedModel<?, DataSource> pagedModel = get(
			faroProject, Rels.DATA_SOURCES,
			new ParameterizedTypeReference
				<EntityModelPagedModel<DataSource>>() {
			},
			uriVariables);

		Results<DataSource> results = pagedModel.getResults();

		for (DataSource dataSource : results.getItems()) {
			dataSource.setWorkspaceURL(
				getWorkspaceURL(faroProject.getGroupId()));

			put(
				faroProject, Rels.DATA_SOURCE, dataSource, DataSource.class,
				getUriVariables(faroProject, dataSource.getId()));
		}

		return post(
			faroProject, Rels.DATA_SOURCE_REFRESH_LIFERAY, null, List.class);
	}

	@Override
	public void updateBQProject(FaroProject faroProject, Date startDate)
		throws Exception {

		patch(
			faroProject, Collections.emptyMap(), "/bq-projects",
			Collections.emptyMap(),
			new AsahProject(faroProject.getProjectId(), startDate), Void.class,
			Collections.emptyMap());
	}

	@Override
	public DataSource updateDataSource(
		FaroProject faroProject, String id, Credentials credentials,
		long userId, String name, String url, Provider provider, Event event,
		String status) {

		DataSource dataSource = new DataSource();

		dataSource.setId(id);
		dataSource.setAuthor(getAuthor(userId));
		dataSource.setCredentials(credentials);
		dataSource.setName(name);
		dataSource.setProvider(provider);
		dataSource.setStatus(status);
		dataSource.setSubjectOf(event);
		dataSource.setUrl(url);
		dataSource.setWorkspaceURL(getWorkspaceURL(faroProject.getGroupId()));

		return put(
			faroProject, Rels.DATA_SOURCE, dataSource, DataSource.class,
			getUriVariables(faroProject, id));
	}

	@Override
	public FieldMapping updateFieldMapping(
		FaroProject faroProject, String context,
		Map<String, String> dataSourceFieldNames, String fieldName,
		String fieldType, String ownerType) {

		FieldMapping fieldMapping = new FieldMapping();

		fieldMapping.setContext(context);
		fieldMapping.setDataSourceFieldNames(dataSourceFieldNames);
		fieldMapping.setFieldName(fieldName);
		fieldMapping.setFieldType(fieldType);
		fieldMapping.setOwnerType(ownerType);

		return put(
			faroProject, Rels.FIELD_MAPPING, fieldMapping, FieldMapping.class,
			getUriVariables(faroProject, fieldName));
	}

	@Override
	public IndividualSegment updateIndividualSegment(
		FaroProject faroProject, String id, long userId, String channelId,
		String filter, boolean includeAnonymousUsers, String name,
		String segmentType) {

		IndividualSegment individualSegment = new IndividualSegment();

		individualSegment.setId(id);
		individualSegment.setAuthor(getAuthor(userId));
		individualSegment.setChannelId(channelId);
		individualSegment.setFilter(filter);
		individualSegment.setIncludeAnonymousUsers(includeAnonymousUsers);
		individualSegment.setName(name);
		individualSegment.setSegmentType(segmentType);
		individualSegment.setStatus(IndividualSegment.Status.ACTIVE.name());

		return put(
			faroProject, Rels.INDIVIDUAL_SEGMENT, individualSegment,
			IndividualSegment.class, getUriVariables(faroProject, id));
	}

	protected void addActionFilter(
		FilterBuilder filterBuilder, List<String> actionKeys) {

		for (String actionKey : actionKeys) {
			FilterBuilder actionFilterBuilder = new FilterBuilder();

			int index = actionKey.indexOf(StringPool.POUND);

			actionFilterBuilder.addFilter(
				"applicationId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				actionKey.substring(0, index));
			actionFilterBuilder.addFilter(
				"eventId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				actionKey.substring(index + 1));

			filterBuilder.addFilter(actionFilterBuilder, false);
		}
	}

	protected void addOwnerIdFilter(
		FilterBuilder filterBuilder, String ownerId, String ownerType) {

		if (Validator.isNull(ownerType)) {
			return;
		}

		if (ownerType.equals(FieldMappingConstants.OWNER_TYPE_ACCOUNT)) {
			filterBuilder.addFilter(
				"accountId", FilterConstants.COMPARISON_OPERATOR_EQUALS,
				ownerId);
		}
		else {
			filterBuilder.addFilter(
				"ownerId", FilterConstants.COMPARISON_OPERATOR_EQUALS, ownerId);
		}
	}

	protected Author getAuthor(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if ((user != null) && user.isDefaultUser()) {
			return null;
		}

		Author author = new Author();

		author.setId(String.valueOf(userId));

		if (user != null) {
			author.setName(user.getFullName());
		}

		return author;
	}

	protected String getDate(Date date, boolean end) {
		if (date == null) {
			return null;
		}

		LocalDateTime localDateTime = LocalDateTime.ofInstant(
			date.toInstant(), ZoneOffset.UTC);

		LocalTime localTime = LocalTime.MIN;

		if (end) {
			localTime = LocalTime.MAX;
		}

		localDateTime = localDateTime.with(localTime);

		return String.valueOf(localDateTime.toInstant(ZoneOffset.UTC));
	}

	protected String getGroupBy(FieldMapping fieldMapping) {
		StringBundler sb = new StringBundler(5);

		sb.append("groupby((");
		sb.append(fieldMapping.getContext());
		sb.append(StringPool.SLASH);
		sb.append(fieldMapping.getFieldName());
		sb.append("/value))");

		return sb.toString();
	}

	protected String getGroupBy(String fieldName, String interval) {
		StringBundler sb = new StringBundler(9);

		sb.append("compute(");
		sb.append(interval);
		sb.append(StringPool.OPEN_PARENTHESIS);
		sb.append(fieldName);
		sb.append(") as ");
		sb.append(_FARO_TEMP_FIELD);
		sb.append(")/groupby((");
		sb.append(_FARO_TEMP_FIELD);
		sb.append("))");

		return sb.toString();
	}

	protected String getName(String name) {
		if (Validator.isNull(name)) {
			return name;
		}

		return StringUtil.quote(name, StringPool.PERCENT);
	}

	protected String getWorkspaceURL(long groupId) {
		return FaroPropsValues.FARO_URL + "/workspace/" + groupId;
	}

	private static final String _FARO_TEMP_FIELD = "faro_temp_field";

	private static final int _PAYLOAD_MAX_BYTE_SIZE = 200000;

	@Reference
	private UserLocalService _userLocalService;

}