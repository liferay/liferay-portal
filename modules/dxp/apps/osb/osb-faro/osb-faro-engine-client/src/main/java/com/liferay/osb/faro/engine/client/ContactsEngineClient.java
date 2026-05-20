/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.AccountDetails;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleMetric;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStatus;
import com.liferay.osb.faro.engine.client.model.AccountMetric;
import com.liferay.osb.faro.engine.client.model.Activity;
import com.liferay.osb.faro.engine.client.model.ActivityAggregation;
import com.liferay.osb.faro.engine.client.model.ActivityAsset;
import com.liferay.osb.faro.engine.client.model.ActivityGroup;
import com.liferay.osb.faro.engine.client.model.Asset;
import com.liferay.osb.faro.engine.client.model.AssetSummary;
import com.liferay.osb.faro.engine.client.model.AssetSummaryCategory;
import com.liferay.osb.faro.engine.client.model.AssetSummaryMimeType;
import com.liferay.osb.faro.engine.client.model.AssetSummaryTag;
import com.liferay.osb.faro.engine.client.model.AssetSummaryType;
import com.liferay.osb.faro.engine.client.model.AssetSummaryVocabulary;
import com.liferay.osb.faro.engine.client.model.Author;
import com.liferay.osb.faro.engine.client.model.BlockedKeyword;
import com.liferay.osb.faro.engine.client.model.Channel;
import com.liferay.osb.faro.engine.client.model.ChannelDataSource;
import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.DXPGroup;
import com.liferay.osb.faro.engine.client.model.DXPOrganization;
import com.liferay.osb.faro.engine.client.model.DXPUserGroup;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.engine.client.model.DataSourceField;
import com.liferay.osb.faro.engine.client.model.DataSourceProgress;
import com.liferay.osb.faro.engine.client.model.Distribution;
import com.liferay.osb.faro.engine.client.model.Event;
import com.liferay.osb.faro.engine.client.model.Field;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.engine.client.model.FieldMappingMap;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.engine.client.model.IndividualSegmentRealTimeMembership;
import com.liferay.osb.faro.engine.client.model.IndividualTransformation;
import com.liferay.osb.faro.engine.client.model.Interest;
import com.liferay.osb.faro.engine.client.model.PageExperience;
import com.liferay.osb.faro.engine.client.model.PageVisited;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Provider;
import com.liferay.osb.faro.engine.client.model.RealTimeMembershipMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.engine.client.model.SegmentActivation;
import com.liferay.osb.faro.engine.client.model.provider.LiferayProvider;
import com.liferay.osb.faro.engine.client.util.FilterBuilder;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;

import java.io.OutputStream;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Shinn Lok
 */
public interface ContactsEngineClient {

	public AccountLifecycle addAccountLifecycle(
		FaroProject faroProject, String description, String name,
		String segmentId);

	public Results<BlockedKeyword> addBlockedKeywords(
		FaroProject faroProject, List<String> keywords);

	public Channel addChannel(FaroProject faroProject, Channel channel);

	public void addCSVIndividuals(
			FaroProject faroProject, List<Map<String, Object>> fieldsMaps,
			String dataSourceId, List<String> individualSegmentIds)
		throws Exception;

	public void addData(
		FaroProject faroProject, String weDeployDataServiceName,
		String collectionName, List<Map<String, Object>> objects);

	public DataSource addDataSource(
		FaroProject faroProject, Credentials credentials, Author author,
		String name, String url, Provider provider, Event event, String status);

	public DataSource addDataSource(
		FaroProject faroProject, Credentials credentials, long userId,
		String name, String url, Provider provider, Event event, String status);

	public FieldMapping addFieldMapping(
		FaroProject faroProject, String context,
		Map<String, String> dataSourceFieldNames, String fieldName,
		String fieldType, String ownerType, Boolean repeatable);

	public List<FieldMapping> addFieldMappings(
		FaroProject faroProject, String dataSourceId, String context,
		String ownerType, List<FieldMappingMap> fieldMappingMaps);

	public IndividualSegment addIndividualSegment(
		FaroProject faroProject, long userId, String channelId,
		String externalReferenceCode, String filterString,
		boolean includeAnonymousUsers, String name, String segmentType,
		boolean sequential, String status);

	public IndividualSegmentMembership addMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId);

	public void addMemberships(
		FaroProject faroProject, String individualSegmentId,
		List<String> individualIds);

	public void addNanite(
		FaroProject faroProject, String className, Map<String, Object> context);

	public void addNanites(FaroProject faroProject, List<String> classNames);

	public String addProject(FaroProject faroProject) throws Exception;

	public void assignChannelToIndividualSegment(
		FaroProject faroProject, String individualSegmentId, String channelId);

	public void clearChannel(
		FaroProject faroProject, FaroUser faroUser, List<String> ids);

	public void deleteBlockedKeywords(FaroProject faroProject, List<String> ids)
		throws FaroEngineClientException;

	public void deleteChannels(
		FaroProject faroProject, FaroUser faroUser, List<String> ids);

	public void deleteData(
		FaroProject faroProject, String weDeployDataServiceName,
		String collectionName);

	public void deleteDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public void deleteFieldMapping(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public void deleteFields(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public void deleteIndividualSegments(
			FaroProject faroProject, List<String> ids)
		throws Exception;

	public void deleteMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId);

	public void deleteProject(FaroProject faroProject, boolean deleteData)
		throws Exception;

	public void disconnectDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public void disconnectDataSources(FaroProject faroProject)
		throws FaroEngineClientException;

	public <T> T get(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Class<T> returnType)
		throws Exception;

	public Account getAccount(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public AccountDetails getAccountDetails(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public Results<Object> getAccountFieldValues(
		FaroProject faroProject, Long channelId, String fieldMappingFieldName,
		String query, int cur, int delta);

	public Results<Individual> getAccountIndividuals(
		FaroProject faroProject, String accountId, String query, int cur,
		int delta, String sortString);

	public Results<IndividualSegment> getAccountIndividualSegments(
		FaroProject faroProject, String accountId, String channelId,
		String query, String status, int cur, int delta,
		List<OrderByField> orderByFields);

	public AccountLifecycle getAccountLifecycle(
			FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public Results<Account> getAccountLifecycleAccounts(
			FaroProject faroProject, String country, String id, String industry,
			String query, String stageType, int cur, int delta,
			String sortString)
		throws FaroEngineClientException;

	public List<AccountLifecycleMetric> getAccountLifecycleMetrics(
			FaroProject faroProject, String country, String id, String industry)
		throws FaroEngineClientException;

	public List<AccountLifecycle> getAccountLifecycles(FaroProject faroProject)
		throws FaroEngineClientException;

	public List<AccountLifecycleStageMetric> getAccountLifecycleStageMetrics(
			FaroProject faroProject, String country, String id, String industry)
		throws FaroEngineClientException;

	public AccountLifecycleStatus getAccountLifecycleStatus(
			FaroProject faroProject, String accountLifecycleId, String id)
		throws FaroEngineClientException;

	public List<AccountMetric> getAccountMetrics(
		FaroProject faroProject, Long channelId);

	public Results<Account> getAccounts(
		FaroProject faroProject, String channelId, String filterString,
		String query, int cur, int delta, String sortString);

	public Results<Distribution> getAccountsDistribution(
		FaroProject faroProject, String channelId, String fieldMappingFieldName,
		String filterString, String individualSegmentId, int count,
		int numberOfBins, List<OrderByField> orderByFields);

	public Results<Activity> getActivities(
		FaroProject faroProject, String ownerId, String ownerType,
		String groupId, String query, Date startDate, Date endDate, int action,
		int cur, int delta, List<OrderByField> orderByFields);

	public List<List<Activity>> getActivitiesList(
		FaroProject faroProject, List<String> groupIds, String query,
		int action, int cur, int delta, List<OrderByField> orderByFields);

	public Activity getActivity(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public Results<ActivityAggregation> getActivityAggregations(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String rangeEnd, String rangeStart, String interval,
		int delta);

	public Results<ActivityAsset> getActivityAssets(
		FaroProject faroProject, String query, String applicationId,
		String channelId, String eventId, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<ActivityGroup> getActivityGroups(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String query, Date startDate, Date endDate, int cur,
		int delta, List<OrderByField> orderByFields);

	public Asset getAsset(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public Results<Asset> getAssets(
		FaroProject faroProject, String dataSourceId, String query, int action,
		String assetType, int cur, int delta, List<OrderByField> orderByFields);

	public Results<AssetSummary> getAssetSummaries(
		FaroProject faroProject, long channelId, String filterString,
		String keywords, int rangeKey, int cur, int delta, String sortString);

	public Results<AssetSummaryCategory> getAssetSummaryCategories(
		FaroProject faroProject, String accountId, long channelId,
		String keywords, String rangeEnd, int rangeKey, String rangeStart,
		String selectedMetric, String sort, String vocabularyId, int cur,
		int delta);

	public Results<AssetSummaryMimeType> getAssetSummaryMimeTypes(
		FaroProject faroProject, long channelId, String rangeEnd, int rangeKey,
		String rangeStart, int cur, int delta);

	public Results<AssetSummaryTag> getAssetSummaryTags(
		FaroProject faroProject, String accountId, long channelId,
		String keywords, String rangeEnd, int rangeKey, String rangeStart,
		String selectedMetric, String sort, int cur, int delta);

	public Results<AssetSummaryType> getAssetSummaryTypes(
		FaroProject faroProject, long channelId, String rangeEnd, int rangeKey,
		String rangeStart, int cur, int delta);

	public Results<AssetSummaryVocabulary> getAssetSummaryVocabularies(
		FaroProject faroProject, long channelId, String keywords,
		String rangeEnd, int rangeKey, String rangeStart, String sort, int cur,
		int delta);

	public DataSource getAvailableTokenDataSource(FaroProject faroProject);

	public BlockedKeyword getBlockedKeyword(FaroProject faroProject, String id);

	public Results<BlockedKeyword> getBlockedKeywords(
		FaroProject faroProject, String query, int cur, int delta,
		List<OrderByField> orderByFields);

	public Channel getChannel(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public Results<ChannelDataSource> getChannelDataSources(
		FaroProject faroProject, Long dataSourceId, Boolean enabled,
		String name, int cur, int delta, List<OrderByField> orderByFields);

	public Results<Channel> getChannels(
		FaroProject faroProject, int cur, int delta, List<String> ids,
		List<OrderByField> orderByFields);

	public Results<Individual> getCoworkerIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields);

	public DataSource getDataSource(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public List<DXPGroup> getDataSourceDXPGroups(
		FaroProject faroProject, String id, List<Long> groupIds);

	public Results<DXPGroup> getDataSourceDXPGroups(
		FaroProject faroProject, String id, long parentGroupId, boolean site,
		String name, int cur, int delta);

	public List<DXPOrganization> getDataSourceDXPOrganizations(
		FaroProject faroProject, String id, List<Long> organizationIds);

	public Results<DXPOrganization> getDataSourceDXPOrganizations(
		FaroProject faroProject, String id, long parentOrganizationId,
		String name, int cur, int delta);

	public long getDataSourceDXPTotal(FaroProject faroProject, String id);

	public long getDataSourceDXPTotal(
		FaroProject faroProject, String id,
		LiferayProvider.ContactsConfiguration contactsConfiguration);

	public List<DXPUserGroup> getDataSourceDXPUserGroups(
		FaroProject faroProject, String id, List<Long> userGroupIds);

	public Results<DXPUserGroup> getDataSourceDXPUserGroups(
		FaroProject faroProject, String id, String name, int cur, int delta);

	public List<DataSourceField> getDataSourceFields(
		FaroProject faroProject, String id, String context, int count);

	public long getDataSourceMetricsAccountsCount(
		FaroProject faroProject, String dataSourceId);

	public long getDataSourceMetricsEventsCount(
		FaroProject faroProject, String dataSourceId);

	public long getDataSourceMetricsUsersCount(
		FaroProject faroProject, String dataSourceId);

	public Map<String, DataSourceProgress> getDataSourceProgressMap(
		FaroProject faroProject, String id);

	public Results<DataSource> getDataSources(
		FaroProject faroProject, List<String> channelIds);

	public List<DataSource> getDataSources(
		FaroProject faroProject, long channelId, String provideTypeExclude);

	public Results<DataSource> getDataSources(
		FaroProject faroProject, String faroEntityId, String query, String name,
		String providerType, List<String> states, int cur, int delta,
		List<OrderByField> orderByFields);

	public long getDXPUsersCount(FaroProject faroProject, String id);

	public Long getEnrichedProfilesCount(
		FaroProject faroProject, Long channelId);

	public Field getField(FaroProject faroProject, String id)
		throws FaroEngineClientException;

	public FieldMapping getFieldMapping(FaroProject faroProject, String id);

	public FieldMapping getFieldMapping(
		FaroProject faroProject, String context, String fieldName);

	public Results<FieldMapping> getFieldMappings(
		FaroProject faroProject, String context, List<String> fieldNames,
		int cur, int delta, List<OrderByField> orderByFields);

	public Results<FieldMapping> getFieldMappings(
		FaroProject faroProject, String context, String dataSourceId,
		String dataSourceFieldName);

	public Results<FieldMapping> getFieldMappings(
		FaroProject faroProject, String context, String displayName,
		String ownerType, String query, int cur, int delta,
		List<OrderByField> orderByFields);

	public List<String> getFieldNames(
		FaroProject faroProject, String label, String ownerType, Object values);

	public List<List<String>> getFieldNamesList(
		FaroProject faroProject, List<String> labels, String ownerType,
		List<Object> valuesList);

	public Results<Field> getFields(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Field> getFields(
		FaroProject faroProject, String context, String name, int cur,
		int delta, List<OrderByField> orderByFields);

	public Results<Field> getFields(
		FaroProject faroProject, String context, String name, String ownerId,
		String ownerType, Date startDate, Date endDate, int interval,
		List<OrderByField> orderByFields);

	public List<List<Field>> getFieldsList(
		FaroProject faroProject, String context, List<String> names, int cur,
		int delta, List<OrderByField> orderByFields);

	public Results<Object> getFieldValues(
		FaroProject faroProject, Long channelId, String query,
		String fieldMappingFieldName, int cur, int delta);

	public long getIdentitiesCount(FaroProject faroProject);

	public Individual getIndividual(
			FaroProject faroProject, String id, String channelId)
		throws FaroEngineClientException;

	public List<FieldMapping> getIndividualAttributes(
		FaroProject faroProject, String displayName);

	public Results<IndividualSegment> getIndividualIndividualSegments(
		FaroProject faroProject, String channelId, String individualId,
		String query, String status, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Individual> getIndividuals(
		FaroProject faroProject, FilterBuilder filterBuilder,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Individual> getIndividuals(
		FaroProject faroProject, String dataSourceId,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Individual> getIndividuals(
		FaroProject faroProject, String accountId, String channelId,
		String dataSourceId, String individualSegmentId,
		String notIndividualSegmentId, String interestName, String filterString,
		List<String> profileTypes, String query, List<String> fields,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Individual> getIndividualsByIndividualSegment(
		FaroProject faroProject, String individualSegmentsId, String query,
		List<String> fields, FilterBuilder filterBuilder,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<Individual> getIndividualsByIndividualSegment(
		FaroProject faroProject, String individualSegmentId,
		String filterString, String query, List<String> fields,
		boolean includeAnonymousUsers, int cur, int delta,
		List<OrderByField> orderByFields);

	public long getIndividualsCreatedBetweenCount(
		FaroProject faroProject, Date endDate, Date startDate);

	public long getIndividualsCreatedSinceCount(
		FaroProject faroProject, Date startDate);

	public Results<Distribution> getIndividualsDistribution(
		FaroProject faroProject, String channelId, String fieldMappingFieldName,
		String individualSegmentId, int count, int numberOfBins,
		List<OrderByField> orderByFields);

	public IndividualSegment getIndividualSegment(
		FaroProject faroProject, String id, boolean includeReferencedObjects);

	public IndividualSegmentMembership getIndividualSegmentMembership(
		FaroProject faroProject, String individualSegmentId,
		String individualId);

	public Results<IndividualSegmentMembershipChangeAggregation>
		getIndividualSegmentMembershipChangeAggregations(
			FaroProject faroProject, String individualSegmentId,
			String interval, int delta);

	public Results<IndividualSegmentMembershipChange>
		getIndividualSegmentMembershipChanges(
			FaroProject faroProject, String individualSegmentId, String query,
			Date startDate, Date endDate, int cur, int delta,
			List<OrderByField> orderByFields);

	public Results<IndividualSegmentMembership> getIndividualSegmentMemberships(
		FaroProject faroProject, String individualSegmentId, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<IndividualSegmentRealTimeMembership>
		getIndividualSegmentRealTimeMemberships(
			FaroProject faroProject, String day, String individualSegmentId,
			List<String> profileTypes, String query, List<String> types,
			int cur, int delta, List<OrderByField> orderByFields);

	public Results<IndividualSegment> getIndividualSegments(
		FaroProject faroProject, String channelId, String dataSourceId,
		String query, List<String> fields, String name,
		List<String> segmentTypes, String state, String status, int cur,
		int delta, List<OrderByField> orderByFields);

	public Results<IndividualTransformation> getIndividualTransformations(
		FaroProject faroProject, String individualSegmentId, String query,
		List<String> fields, String fieldMappingFieldName, int cur, int delta,
		List<OrderByField> orderByFields);

	public Results<String> getInterestKeywords(
		String channelId, FaroProject faroProject, String query, int cur,
		int delta);

	public Results<Interest> getInterests(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String name, String query, String expand, int cur,
		int delta, List<OrderByField> orderByFields);

	public Date getLastSeenDate(FaroProject faroProject);

	public List<PageExperience> getPageExperiences(
			FaroProject faroProject, String canonicalUrl, String channelId,
			String pageTitle)
		throws Exception;

	public Results<PageVisited> getPagesVisited(
		FaroProject faroProject, String channelId, String ownerId,
		String ownerType, String query, String interestName, Date startDate,
		Date endDate, boolean visitedPages, int cur, int delta,
		List<OrderByField> orderByFields);

	public PageVisited getPageVisited(FaroProject faroProject, String id);

	public Results<ProjectUsageMetric> getProjectUsageMetrics(
		FaroProject faroProject, Date sinceDate);

	public RealTimeMembershipMetric getRealTimeMembershipMetric(
		FaroProject faroProject, String individualSegmentId);

	public long getReportsExportCSVCount(
			FaroProject faroProject, String path,
			Map<String, List<String>> queryParameters)
		throws Exception;

	public Results<String> getSessionValues(
		FaroProject faroProject, String channelId, String fieldName,
		String filterString, String query, int cur, int delta);

	public Results<Individual> getSimilarIndividuals(
		FaroProject faroProject, String individualId, String query,
		List<String> fields, int cur, int delta,
		List<OrderByField> orderByFields);

	public long getSyncedIndividualsCount(FaroProject faroProject);

	public void getToOutputStream(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters,
			OutputStream outputStream)
		throws Exception;

	public Results<IndividualSegment> getUnassignedIndividualSegments(
		FaroProject faroProject, int cur, int delta,
		List<OrderByField> orderByFields);

	public void insertBQProjects(List<FaroProject> faroProjects)
		throws Exception;

	public Channel patchChannel(
		FaroProject faroProject, String id, String name);

	public Channel patchChannel(
		FaroProject faroProject, String id, String dataSourceId,
		List<Map<String, String>> groups);

	public DataSource patchDataSource(
		FaroProject faroProject, String id, Credentials credentials,
		long userId, String name, String url, Provider provider, Event event,
		String status);

	public FieldMapping patchFieldMapping(
		FaroProject faroProject, String id, String dataSourceId,
		String fieldName);

	public void patchFieldMappings(
		FaroProject faroProject, String dataSourceId, String context,
		String ownerType, List<FieldMappingMap> fieldMappingMaps);

	public <T> T post(
			FaroProject faroProject, Map<String, String> headers, String path,
			Map<String, List<String>> queryParameters, Object requestBody,
			Class<T> returnType)
		throws Exception;

	public List<Map<String, Object>> refreshLiferay(FaroProject faroProject);

	public void setEngineURL(String engineURL);

	public AccountLifecycle updateAccountLifecycle(
		FaroProject faroProject, String description, String id, String name,
		String segmentId);

	public void updateAccountLifecycleStageRule(
		FaroProject faroProject, String filterMetadata, String filterString,
		String id, String name, String stageId);

	public void updateBQProject(FaroProject faroProject, Date startDate)
		throws Exception;

	public DataSource updateDataSource(
		FaroProject faroProject, String id, Credentials credentials,
		long userId, String name, String url, Provider provider, Event event,
		String status);

	public FieldMapping updateFieldMapping(
		FaroProject faroProject, String context,
		Map<String, String> dataSourceFieldNames, String fieldName,
		String fieldType, String ownerType);

	public IndividualSegment updateIndividualSegment(
		FaroProject faroProject, String id, long userId, String channelId,
		String externalReferenceCode, String filterString,
		boolean includeAnonymousUsers, String name, String segmentType,
		boolean sequential);

	public SegmentActivation updateSegmentActivation(
		FaroProject faroProject, String cronExpression, String frequencyType,
		Date scheduleEndDate, Date scheduleStartDate, String scheduleType,
		Long segmentId);

}