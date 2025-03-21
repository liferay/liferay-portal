/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.FilterAggregation;
import com.liferay.portal.search.aggregation.bucket.FilterAggregationResult;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregation;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregationResult;
import com.liferay.portal.search.aggregation.pipeline.BucketSelectorPipelineAggregation;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.NestedSort;
import com.liferay.portal.search.sort.SortMode;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Creator;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.SLAResult;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Transition;
import com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util.AssigneeUtil;
import com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util.SLAResultUtil;
import com.liferay.portal.workflow.metrics.rest.internal.odata.entity.v1_0.InstanceEntityModel;
import com.liferay.portal.workflow.metrics.rest.internal.resource.exception.NoSuchInstanceException;
import com.liferay.portal.workflow.metrics.rest.internal.resource.helper.ResourceHelper;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.InstanceResource;
import com.liferay.portal.workflow.metrics.search.index.InstanceWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.service.WorkflowMetricsSLADefinitionLocalService;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rafael Praxedes
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/instance.properties",
	scope = ServiceScope.PROTOTYPE, service = InstanceResource.class
)
public class InstanceResourceImpl extends BaseInstanceResourceImpl {

	@Override
	public void deleteProcessInstance(Long processId, Long instanceId)
		throws Exception {

		_instanceWorkflowMetricsIndexer.deleteInstance(
			contextCompany.getCompanyId(), instanceId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Instance getProcessInstance(Long processId, Long instanceId)
		throws Exception {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			"instanceId", "instanceId");

		FilterAggregation indexFilterAggregation = _aggregations.filter(
			"tasksIndex",
			_queries.term(
				"_index",
				_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TASK));

		TermsAggregation assigneeTypeTermsAggregation = _aggregations.terms(
			"assigneeType", "assigneeType");

		TermsAggregation assigneeIdTermsAggregation = _aggregations.terms(
			"assigneeId", "assigneeIds");

		assigneeIdTermsAggregation.setSize(10000);

		assigneeTypeTermsAggregation.addChildAggregation(
			assigneeIdTermsAggregation);

		indexFilterAggregation.addChildAggregation(
			assigneeTypeTermsAggregation);

		FilterAggregation onTimeFilterAggregation = _aggregations.filter(
			"onTime", _resourceHelper.createMustNotBooleanQuery());

		onTimeFilterAggregation.addChildAggregation(
			_resourceHelper.createOnTimeScriptedMetricAggregation());

		FilterAggregation overdueFilterAggregation = _aggregations.filter(
			"overdue", _resourceHelper.createMustNotBooleanQuery());

		overdueFilterAggregation.addChildAggregation(
			_resourceHelper.createOverdueScriptedMetricAggregation());

		BooleanQuery slaInstanceResultBooleanQuery = _queries.booleanQuery();

		String indexName = _indexNameBuilder.getIndexName(
			contextCompany.getCompanyId());

		FilterAggregation slaInstanceResultFilterAggregation =
			_aggregations.filter(
				"slaInstanceResult",
				slaInstanceResultBooleanQuery.addMustQueryClauses(
					_queries.term(
						"_index",
						indexName +
							WorkflowMetricsIndexNameConstants.
								SUFFIX_SLA_INSTANCE_RESULT)));

		TopHitsAggregation topHitsAggregation = _aggregations.topHits(
			"topHits");

		topHitsAggregation.addSortFields(
			_sorts.field("remainingTime", SortOrder.ASC));
		topHitsAggregation.setSize(100);

		slaInstanceResultFilterAggregation.addChildAggregation(
			topHitsAggregation);

		TermsAggregation taskNameTermsAggregation = _aggregations.terms(
			"name", "name");

		taskNameTermsAggregation.setSize(10000);

		termsAggregation.addChildrenAggregations(
			indexFilterAggregation, onTimeFilterAggregation,
			overdueFilterAggregation, slaInstanceResultFilterAggregation,
			taskNameTermsAggregation);

		searchSearchRequest.addAggregation(termsAggregation);
		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_SLA_INSTANCE_RESULT,
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_TASK);

		BooleanQuery booleanQuery = _createInstancesBooleanQuery(
			new Long[0], new Long[0], null, null, processId, new String[0],
			null, new String[0], new String[0]);

		searchSearchRequest.setQuery(
			booleanQuery.addMustQueryClauses(
				_queries.term("instanceId", instanceId)));

		searchSearchRequest.setSelectedFieldNames(StringPool.BLANK);
		searchSearchRequest.setSize(10000);

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		Instance instance = null;

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			String uid = document.getString("uid");

			if (uid.startsWith("WorkflowMetricsInstance")) {
				instance = _createInstance(document);

				break;
			}
		}

		if (instance == null) {
			throw new NoSuchInstanceException(
				"No instance exists with the instance ID " + instanceId);
		}

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResultsMap.get("instanceId");

		Bucket firstBucket = null;

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			firstBucket = bucket;

			break;
		}

		if (firstBucket == null) {
			return instance;
		}

		_setAssignees(firstBucket, instance);
		_setSLAResults(firstBucket, instance);
		_setTaskNames(firstBucket, instance);
		_setTransitions(instance);

		return instance;
	}

	@Override
	public Page<Instance> getProcessInstancesPage(
			Long processId, Long[] assigneeIds, Long[] classPKs, Date dateEnd,
			Date dateStart, String[] slaStatuses, String[] statuses,
			String[] taskNames, Pagination pagination, Sort[] sorts)
		throws Exception {

		long instanceCount = _getInstanceCount(
			assigneeIds, classPKs, dateEnd, dateStart, processId, slaStatuses,
			statuses, taskNames);

		if (instanceCount > 0) {
			long startInstanceId = 0;

			if (pagination.getEndPosition() > 10000) {
				int endPosition = pagination.getEndPosition();

				while (endPosition > 10000) {
					startInstanceId = _getInstanceId(
						assigneeIds, classPKs, dateEnd, dateStart, processId,
						slaStatuses, sorts, startInstanceId, statuses,
						taskNames);

					endPosition = endPosition - 10000;
				}

				pagination = Pagination.of(
					endPosition / pagination.getPageSize(),
					pagination.getPageSize());
			}

			return Page.of(
				_getInstances(
					assigneeIds, classPKs, dateEnd, dateStart, pagination,
					processId, slaStatuses, sorts, startInstanceId, statuses,
					taskNames),
				pagination, instanceCount);
		}

		return Page.of(Collections.emptyList());
	}

	@Override
	public void patchProcessInstance(
			Long processId, Long instanceId, Instance instance)
		throws Exception {

		getProcessInstance(processId, instanceId);

		_instanceWorkflowMetricsIndexer.updateInstance(
			instance.getActive(),
			LocalizedMapUtil.getLocalizedMap(instance.getAssetTitle_i18n()),
			LocalizedMapUtil.getLocalizedMap(instance.getAssetType_i18n()),
			contextCompany.getCompanyId(), instanceId,
			instance.getDateModified());
	}

	@Override
	public void patchProcessInstanceComplete(
			Long processId, Long instanceId, Instance instance)
		throws Exception {

		getProcessInstance(processId, instanceId);

		_instanceWorkflowMetricsIndexer.completeInstance(
			contextCompany.getCompanyId(), instance.getDateCompletion(),
			instance.getDuration(), instanceId, instance.getDateModified());
	}

	@Override
	public Instance postProcessInstance(Long processId, Instance instance)
		throws Exception {

		Creator creator = instance.getCreator();

		return _createInstance(
			_instanceWorkflowMetricsIndexer.addInstance(
				LocalizedMapUtil.getLocalizedMap(instance.getAssetTitle_i18n()),
				LocalizedMapUtil.getLocalizedMap(instance.getAssetType_i18n()),
				instance.getClassName(), instance.getClassPK(),
				contextCompany.getCompanyId(), null, instance.getDateCreated(),
				instance.getId(), instance.getDateModified(), processId,
				instance.getProcessVersion(), creator.getId(),
				creator.getName()));
	}

	private Assignee _createAssignee(boolean reviewer) {
		Assignee assignee = new Assignee() {
			{
				setId(() -> -1L);
				setName(
					() -> _language.get(
						ResourceBundleUtil.getModuleAndPortalResourceBundle(
							contextAcceptLanguage.getPreferredLocale(),
							InstanceResourceImpl.class),
						"unassigned"));
			}
		};

		assignee.setReviewer(() -> reviewer);

		return assignee;
	}

	private BooleanQuery _createBooleanQuery(long processId) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		return booleanQuery.addMustQueryClauses(
			_queries.term("companyId", contextCompany.getCompanyId()),
			_queries.term("deleted", Boolean.FALSE),
			_queries.term("processId", processId));
	}

	private BooleanQuery _createBooleanQuery(long processId, long instanceId) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		BooleanQuery tasksBooleanQuery = _queries.booleanQuery();

		tasksBooleanQuery.addFilterQueryClauses(
			_queries.term(
				"_index",
				_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TASK));
		tasksBooleanQuery.addMustQueryClauses(
			_createTasksBooleanQuery(processId, instanceId));

		BooleanQuery transitionsBooleanQuery = _queries.booleanQuery();

		transitionsBooleanQuery.addFilterQueryClauses(
			_queries.term(
				"_index",
				_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TRANSITION));
		transitionsBooleanQuery.addMustQueryClauses(
			_createBooleanQuery(processId));

		return booleanQuery.addShouldQueryClauses(
			tasksBooleanQuery, transitionsBooleanQuery);
	}

	private Instance _createInstance(Document document) {
		Instance instance = new Instance() {
			{
				setAssetTitle(
					() -> document.getString(_getLocalizedName("assetTitle")));
				setAssetType(
					() -> document.getString(_getLocalizedName("assetType")));
				setClassPK(() -> document.getLong("classPK"));
				setCompleted(() -> document.getBoolean("completed"));
				setCreator(() -> _toCreator(document.getLong("userId")));
				setDateCompletion(
					() -> _parseDate(document.getDate("completionDate")));
				setDateCreated(
					() -> _parseDate(document.getDate("createDate")));
				setDateModified(
					() -> _parseDate(document.getDate("modifiedDate")));
				setId(() -> document.getLong("instanceId"));
				setProcessId(() -> document.getLong("processId"));
				setSLAStatus(
					() -> Instance.SLAStatus.create(
						document.getString("slaStatus")));
			}
		};

		_populateWithTasks(document, instance);

		return instance;
	}

	private BooleanQuery _createInstancesBooleanQuery(
		Long[] assigneeIds, Long[] classPKs, Date dateEnd, Date dateStart,
		long processId, String[] slaStatuses, Long startInstanceId,
		String[] statuses, String[] taskNames) {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustNotQueryClauses(_queries.term("instanceId", 0));

		if (assigneeIds.length > 0) {
			BooleanQuery nestedBooleanQuery = _queries.booleanQuery();

			TermsQuery termsQuery = _queries.terms("tasks.assigneeIds");

			termsQuery.addValues(
				transform(
					assigneeIds,
					assigneeId -> {
						if (assigneeId <= 0) {
							return null;
						}

						return String.valueOf(assigneeId);
					},
					Object.class));

			nestedBooleanQuery.addShouldQueryClauses(termsQuery);

			if (ArrayUtil.contains(assigneeIds, -1L)) {
				nestedBooleanQuery.addShouldQueryClauses(
					_queries.term("tasks.assigneeType", Role.class.getName()));
			}

			booleanQuery.addMustQueryClauses(
				_queries.nested("tasks", nestedBooleanQuery));
		}

		if (ArrayUtil.isNotEmpty(classPKs)) {
			TermsQuery termsQuery = _queries.terms("classPK");

			termsQuery.addValues(
				transform(classPKs, String::valueOf, Object.class));

			booleanQuery.addMustQueryClauses(termsQuery);
		}

		if (ArrayUtil.isNotEmpty(statuses)) {
			BooleanQuery shouldBooleanQuery = _queries.booleanQuery();

			shouldBooleanQuery.setMinimumShouldMatch(1);

			if (ArrayUtil.contains(statuses, "Completed")) {
				BooleanQuery mustBooleanQuery = _queries.booleanQuery();

				mustBooleanQuery.addMustQueryClauses(
					_queries.term("completed", true));

				if ((dateEnd != null) && (dateStart != null)) {
					mustBooleanQuery.addMustQueryClauses(
						_queries.rangeTerm(
							"completionDate", true, true,
							_resourceHelper.getDate(dateStart),
							_resourceHelper.getDate(dateEnd)));
				}

				shouldBooleanQuery.addShouldQueryClauses(mustBooleanQuery);
			}

			if (ArrayUtil.contains(statuses, "Pending")) {
				shouldBooleanQuery.addShouldQueryClauses(
					_queries.term("completed", false));
			}

			booleanQuery.addMustQueryClauses(shouldBooleanQuery);
		}

		if (startInstanceId != null) {
			booleanQuery.addMustQueryClauses(
				_queries.rangeTerm(
					"instanceId", false, false, startInstanceId, null));
		}

		if (ArrayUtil.isNotEmpty(slaStatuses)) {
			TermsQuery termsQuery = _queries.terms("slaStatus");

			termsQuery.addValues(slaStatuses);

			booleanQuery.addMustQueryClauses(termsQuery);
		}

		if (ArrayUtil.isNotEmpty(taskNames)) {
			TermsQuery termsQuery = _queries.terms("tasks.taskName");

			termsQuery.addValues(taskNames);

			booleanQuery.addMustQueryClauses(
				_queries.nested("tasks", termsQuery));
		}

		return booleanQuery.addMustQueryClauses(
			_queries.term("active", Boolean.TRUE),
			_queries.term("deleted", Boolean.FALSE),
			_queries.term("processId", processId));
	}

	private BooleanQuery _createTasksBooleanQuery(
		long processId, long instanceId) {

		BooleanQuery booleanQuery = _queries.booleanQuery();

		booleanQuery.addMustNotQueryClauses(_queries.term("taskId", 0));

		return booleanQuery.addMustQueryClauses(
			_queries.term("companyId", contextCompany.getCompanyId()),
			_queries.term("completed", Boolean.FALSE),
			_queries.term("deleted", Boolean.FALSE),
			_queries.term("instanceId", instanceId),
			_queries.term("processId", processId));
	}

	private List<Assignee> _getAssignees(Bucket bucket) {
		List<Assignee> assignees = new ArrayList<>();

		FilterAggregationResult filterAggregationResult =
			(FilterAggregationResult)bucket.getChildAggregationResult(
				"tasksIndex");

		TermsAggregationResult assigneeTypeAggregationResult =
			(TermsAggregationResult)
				filterAggregationResult.getChildAggregationResult(
					"assigneeType");

		Bucket userBucket = assigneeTypeAggregationResult.getBucket(
			User.class.getName());

		if (userBucket != null) {
			TermsAggregationResult termsAggregationResult =
				(TermsAggregationResult)userBucket.getChildAggregationResult(
					"assigneeId");

			assignees.addAll(
				transform(
					termsAggregationResult.getBuckets(),
					assigneeBucket -> AssigneeUtil.toAssignee(
						_language, _portal,
						ResourceBundleUtil.getModuleAndPortalResourceBundle(
							contextAcceptLanguage.getPreferredLocale(),
							InstanceResourceImpl.class),
						GetterUtil.getLong(assigneeBucket.getKey()),
						_userLocalService::fetchUser)));

			assignees.sort(
				Comparator.comparing(
					Assignee::getName,
					Comparator.nullsLast(String::compareToIgnoreCase)));
		}

		Bucket roleBucket = assigneeTypeAggregationResult.getBucket(
			Role.class.getName());

		if (roleBucket != null) {
			boolean reviewer = false;

			TermsAggregationResult termsAggregationResult =
				(TermsAggregationResult)roleBucket.getChildAggregationResult(
					"assigneeId");

			for (Bucket assigneeIdBucket :
					termsAggregationResult.getBuckets()) {

				long roleId = GetterUtil.getLong(assigneeIdBucket.getKey());

				if (ArrayUtil.contains(contextUser.getRoleIds(), roleId)) {
					reviewer = true;

					break;
				}
			}

			assignees.add(_createAssignee(reviewer));
		}

		return assignees;
	}

	private long _getInstanceCount(
		Long[] assigneeIds, Long[] classPKs, Date dateEnd, Date dateStart,
		long processId, String[] slaStatuses, String[] statuses,
		String[] taskNames) {

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		countSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				_createInstancesBooleanQuery(
					assigneeIds, classPKs, dateEnd, dateStart, processId,
					slaStatuses, null, statuses, taskNames)));

		CountSearchResponse countSearchResponse =
			_searchRequestExecutor.executeSearchRequest(countSearchRequest);

		return countSearchResponse.getCount();
	}

	private long _getInstanceId(
		Long[] assigneeIds, Long[] classPKs, Date dateEnd, Date dateStart,
		long processId, String[] slaStatuses, Sort[] sorts,
		long startInstanceId, String[] statuses, String[] taskNames) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.addSorts(_toFieldSort(sorts));
		searchSearchRequest.setSelectedFieldNames("instanceId");
		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				_createInstancesBooleanQuery(
					assigneeIds, classPKs, dateEnd, dateStart, processId,
					slaStatuses, startInstanceId, statuses, taskNames)));

		searchSearchRequest.setSize(1);
		searchSearchRequest.setStart(9999);

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		if (searchHitsList.isEmpty()) {
			return startInstanceId;
		}

		SearchHit searchHit = searchHitsList.get(0);

		Document document = searchHit.getDocument();

		Long instanceId = document.getLong("instanceId");

		if (instanceId == null) {
			return startInstanceId;
		}

		return instanceId;
	}

	private Collection<Instance> _getInstances(
		Long[] assigneeIds, Long[] classPKs, Date dateEnd, Date dateStart,
		Pagination pagination, long processId, String[] slaStatuses,
		Sort[] sorts, Long startInstanceId, String[] statuses,
		String[] taskNames) {

		Map<Long, Instance> instancesMap = new LinkedHashMap<>();

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.addSorts(_toFieldSort(sorts));
		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				_createInstancesBooleanQuery(
					assigneeIds, classPKs, dateEnd, dateStart, processId,
					slaStatuses, startInstanceId, statuses, taskNames)));

		searchSearchRequest.setSelectedFieldNames(StringPool.BLANK);
		searchSearchRequest.setSize(pagination.getPageSize());
		searchSearchRequest.setStart(pagination.getStartPosition());

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Instance instance = _createInstance(searchHit.getDocument());

			instancesMap.put(instance.getId(), instance);
		}

		_setSLAResults(instancesMap, processId);

		return instancesMap.values();
	}

	private String _getLocalizedName(String name) {
		return Field.getLocalizedName(
			contextAcceptLanguage.getPreferredLocale(), name);
	}

	private List<String> _getNextTransitionNames(
		long processId, long instanceId) {

		List<String> names = new ArrayList<>();

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			"nodeId", "nodeId");

		termsAggregation.setSize(10000);

		FilterAggregation countFilterAggregation = _aggregations.filter(
			"countFilter",
			_queries.term(
				"_index",
				_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TASK));

		countFilterAggregation.addChildrenAggregations(
			_aggregations.valueCount("nodeCount", "nodeId"));

		FilterAggregation nameFilterAggregation = _aggregations.filter(
			"nameFilter",
			_queries.term(
				"_index",
				_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TRANSITION));

		TermsAggregation nameTermsAggregation = _aggregations.terms(
			"name", "name");

		nameTermsAggregation.setSize(10000);

		nameFilterAggregation.addChildAggregation(nameTermsAggregation);

		termsAggregation.addChildrenAggregations(
			countFilterAggregation, nameFilterAggregation);

		BucketSelectorPipelineAggregation bucketSelectorPipelineAggregation =
			_aggregations.bucketSelector(
				"bucketSelector", _scripts.script("params.nodeCount > 0"));

		bucketSelectorPipelineAggregation.addBucketPath(
			"nodeCount", "countFilter>nodeCount.value");

		termsAggregation.addPipelineAggregations(
			bucketSelectorPipelineAggregation);

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_TRANSITION);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(
				_createBooleanQuery(processId, instanceId)));

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResultsMap.get("nodeId");

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			FilterAggregationResult filterAggregationResult =
				(FilterAggregationResult)bucket.getChildAggregationResult(
					"nameFilter");

			TermsAggregationResult childAggregationResult =
				(TermsAggregationResult)
					filterAggregationResult.getChildAggregationResult("name");

			names.addAll(
				transform(childAggregationResult.getBuckets(), Bucket::getKey));
		}

		names.sort(Comparator.naturalOrder());

		return names;
	}

	private List<String> _getTaskNames(Bucket bucket) {
		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)bucket.getChildAggregationResult("name");

		return transform(
			termsAggregationResult.getBuckets(),
			nameBucket -> _language.get(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					contextAcceptLanguage.getPreferredLocale(),
					InstanceResourceImpl.class),
				nameBucket.getKey()));
	}

	private boolean _isReviewer(Map<String, Object> task) {
		if (task.get("assigneeIds") == null) {
			return false;
		}

		List<Long> assigneeIds = ListUtil.toList(
			(List<?>)task.get("assigneeIds"), GetterUtil::getLong);

		for (long assigneeId : assigneeIds) {
			if (ArrayUtil.contains(
					contextUser.getRoleIds(), GetterUtil.getLong(assigneeId))) {

				return true;
			}
		}

		if (task.get("assigneeGroupIds") == null) {
			return false;
		}

		List<Long> assigneeGroupIds = ListUtil.toList(
			(List<?>)task.get("assigneeGroupIds"), GetterUtil::getLong);

		Map<Long, List<Long>> mapGroupRoleIds = new HashMap<>();

		for (long groupId : assigneeGroupIds) {
			mapGroupRoleIds.put(
				groupId,
				transform(
					ListUtil.concat(
						roleLocalService.getUserGroupRoles(
							contextUser.getUserId(), groupId),
						roleLocalService.getUserGroupGroupRoles(
							contextUser.getUserId(), groupId)),
					Role::getRoleId));
		}

		if (mapGroupRoleIds.isEmpty()) {
			return false;
		}

		for (int i = 0; i < assigneeGroupIds.size(); i++) {
			List<Long> roleIds = mapGroupRoleIds.get(assigneeGroupIds.get(i));

			if (roleIds.contains(assigneeIds.get(i))) {
				return true;
			}
		}

		return false;
	}

	private Date _parseDate(String dateString) {
		try {
			return DateUtil.parseDate(
				"yyyyMMddHHmmss", dateString, LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private void _populateWithTasks(Document document, Instance instance) {
		SortedSet<Assignee> assignees = new TreeSet<>(
			Comparator.comparing(
				Assignee::getName,
				Comparator.nullsLast(String::compareToIgnoreCase)));

		SortedSet<String> taskNames = new TreeSet<>();

		for (Object taskObject : document.getValues("tasks")) {
			Map<String, Object> task = (Map<String, Object>)taskObject;

			if (Objects.equals(
					task.get("assigneeType"), User.class.getName())) {

				for (Object assigneeId : (List<?>)task.get("assigneeIds")) {
					assignees.add(
						AssigneeUtil.toAssignee(
							_language, _portal,
							ResourceBundleUtil.getModuleAndPortalResourceBundle(
								contextAcceptLanguage.getPreferredLocale(),
								InstanceResourceImpl.class),
							GetterUtil.getLong(assigneeId),
							_userLocalService::fetchUser));
				}
			}
			else if (Objects.equals(
						task.get("assigneeType"), Role.class.getName())) {

				assignees.add(_createAssignee(_isReviewer(task)));
			}

			taskNames.add(
				_language.get(
					ResourceBundleUtil.getModuleAndPortalResourceBundle(
						contextAcceptLanguage.getPreferredLocale(),
						InstanceResourceImpl.class),
					(String)task.get("taskName")));
		}

		instance.setAssignees(() -> assignees.toArray(new Assignee[0]));
		instance.setTaskNames(() -> taskNames.toArray(new String[0]));

		if ((assignees.size() == 1) && (taskNames.size() == 1)) {
			Assignee assignee = assignees.first();

			if (Objects.equals(assignee.getId(), contextUser.getUserId())) {
				instance.setTransitions(() -> _toTransitions(instance));
			}
		}
	}

	private void _setAssignees(Bucket bucket, Instance instance) {
		List<Assignee> assignees = _getAssignees(bucket);

		if (ListUtil.isNull(assignees)) {
			return;
		}

		instance.setAssignees(() -> assignees.toArray(new Assignee[0]));
	}

	private void _setSLAResults(Bucket bucket, Instance instance) {
		FilterAggregationResult filterAggregationResult =
			(FilterAggregationResult)bucket.getChildAggregationResult(
				"slaInstanceResult");

		TopHitsAggregationResult topHitsAggregationResult =
			(TopHitsAggregationResult)
				filterAggregationResult.getChildAggregationResult("topHits");

		SearchHits searchHits = topHitsAggregationResult.getSearchHits();

		instance.setSlaResults(
			() -> transformToArray(
				searchHits.getSearchHits(),
				searchHit -> SLAResultUtil.toSLAResult(
					searchHit.getSourcesMap(),
					_workflowMetricsSLADefinitionLocalService::
						fetchWorkflowMetricsSLADefinition),
				SLAResult.class));
	}

	private void _setSLAResults(
		Map<Long, Instance> instancesMap, long processId) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation instanceIdtermsAggregation = _aggregations.terms(
			"instanceId", "instanceId");

		TopHitsAggregation topHitsAggregation = _aggregations.topHits(
			"topHits");

		topHitsAggregation.addSortFields(
			_sorts.field("remainingTime", SortOrder.ASC));

		instanceIdtermsAggregation.addChildAggregation(topHitsAggregation);

		searchSearchRequest.addAggregation(instanceIdtermsAggregation);

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_SLA_INSTANCE_RESULT);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		BooleanQuery filterBooleanQuery = _queries.booleanQuery();

		filterBooleanQuery.addMustNotQueryClauses(
			_queries.term("instanceId", 0));

		TermsQuery termsQuery = _queries.terms("instanceId");

		termsQuery.addValues(
			transformToArray(
				instancesMap.keySet(), String::valueOf, String.class));

		filterBooleanQuery.addMustQueryClauses(
			_queries.term("blocked", Boolean.FALSE),
			_queries.term("deleted", Boolean.FALSE),
			_queries.term("processId", processId),
			_queries.term("status", WorkflowMetricsSLAStatus.RUNNING.name()),
			termsQuery);

		booleanQuery.addFilterQueryClauses(filterBooleanQuery);

		searchSearchRequest.setQuery(booleanQuery);

		SearchSearchResponse searchSearchResponse =
			_searchRequestExecutor.executeSearchRequest(searchSearchRequest);

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResultsMap.get("instanceId");

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			Instance instance = instancesMap.get(
				GetterUtil.getLong(bucket.getKey()));

			TopHitsAggregationResult topHitsAggregationResult =
				(TopHitsAggregationResult)bucket.getChildAggregationResult(
					"topHits");

			SearchHits searchHits = topHitsAggregationResult.getSearchHits();

			instance.setSlaResults(
				() -> transformToArray(
					searchHits.getSearchHits(),
					searchHit -> SLAResultUtil.toSLAResult(
						searchHit.getSourcesMap(),
						_workflowMetricsSLADefinitionLocalService::
							fetchWorkflowMetricsSLADefinition),
					SLAResult.class));
		}
	}

	private void _setTaskNames(Bucket bucket, Instance instance) {
		List<String> taskNames = _getTaskNames(bucket);

		if (ListUtil.isNull(taskNames)) {
			return;
		}

		instance.setTaskNames(() -> taskNames.toArray(new String[0]));
	}

	private void _setTransitions(Instance instance) {
		if (ArrayUtil.isEmpty(instance.getAssignees()) ||
			(ArrayUtil.getLength(instance.getTaskNames()) != 1)) {

			return;
		}

		Assignee[] assignees = instance.getAssignees();

		if (!Objects.equals(assignees[0].getId(), contextUser.getUserId())) {
			return;
		}

		instance.setTransitions(() -> _toTransitions(instance));
	}

	private Creator _toCreator(Long userId) {
		if (Objects.isNull(userId)) {
			return null;
		}

		User user = _userLocalService.fetchUser(userId);

		return new Creator() {
			{
				setId(() -> userId);
				setName(
					() -> {
						if (user == null) {
							return String.valueOf(userId);
						}

						return user.getFullName();
					});
			}
		};
	}

	private FieldSort _toFieldSort(Sort[] sorts) {
		FieldSort fieldSort = _sorts.field("instanceId", SortOrder.ASC);

		if (ArrayUtil.isEmpty(sorts)) {
			return fieldSort;
		}

		Sort sort = (Sort)ArrayUtil.getValue(sorts, 0);

		if (StringUtil.startsWith(sort.getFieldName(), "assetType")) {
			fieldSort = _sorts.field(
				sort.getFieldName(),
				sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);
		}
		else if (StringUtil.equals(sort.getFieldName(), "assigneeName")) {
			fieldSort = _sorts.field(
				"tasks.assigneeName",
				sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);

			fieldSort.setMissing("_last");

			NestedSort nestedSort = _sorts.nested("tasks");

			nestedSort.setFilterQuery(
				_queries.term("tasks.assigneeType", User.class.getName()));

			fieldSort.setNestedSort(nestedSort);

			fieldSort.setSortMode(SortMode.MIN);
		}
		else if (StringUtil.equals(sort.getFieldName(), "createDate")) {
			fieldSort = _sorts.field(
				"createDate",
				sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);
		}
		else if (StringUtil.equals(sort.getFieldName(), "overdueDate")) {
			fieldSort = _sorts.field(
				"slaResults.overdueDate",
				sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);

			fieldSort.setMissing("_last");

			NestedSort nestedSort = _sorts.nested("slaResults");

			nestedSort.setFilterQuery(
				_queries.term(
					"slaResults.status",
					WorkflowMetricsSLAStatus.RUNNING.name()));

			fieldSort.setNestedSort(nestedSort);

			fieldSort.setSortMode(SortMode.MIN);
		}
		else if (StringUtil.equals(sort.getFieldName(), "userName")) {
			fieldSort = _sorts.field(
				"userName", sort.isReverse() ? SortOrder.DESC : SortOrder.ASC);
		}

		return fieldSort;
	}

	private Transition _toTransition(String name) {
		Transition transition = new Transition();

		transition.setLabel(
			() -> _language.get(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					contextAcceptLanguage.getPreferredLocale(),
					InstanceResourceImpl.class),
				name));
		transition.setName(() -> name);

		return transition;
	}

	private Transition[] _toTransitions(Instance instance) {
		return transformToArray(
			_getNextTransitionNames(instance.getProcessId(), instance.getId()),
			this::_toTransition, Transition.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceResourceImpl.class);

	private static final EntityModel _entityModel = new InstanceEntityModel();

	@Reference
	private Aggregations _aggregations;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private ResourceHelper _resourceHelper;

	@Reference
	private Scripts _scripts;

	@Reference
	private SearchRequestExecutor _searchRequestExecutor;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowMetricsSLADefinitionLocalService
		_workflowMetricsSLADefinitionLocalService;

}