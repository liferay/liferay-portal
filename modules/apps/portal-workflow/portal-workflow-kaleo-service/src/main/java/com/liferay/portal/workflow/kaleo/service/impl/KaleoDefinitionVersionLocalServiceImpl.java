/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregation;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.StringQuery;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.exception.IncompleteWorkflowInstancesException;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionVersionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTransitionLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoDefinitionVersionLocalServiceBaseImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoInstancePersistence;
import com.liferay.portal.workflow.kaleo.util.comparator.KaleoDefinitionVersionIdComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion",
	service = AopService.class
)
public class KaleoDefinitionVersionLocalServiceImpl
	extends KaleoDefinitionVersionLocalServiceBaseImpl {

	@Override
	public KaleoDefinitionVersion addKaleoDefinitionVersion(
			long kaleoDefinitionId, String name, String title,
			String description, String content, String version,
			ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition version

		Date createDate = serviceContext.getCreateDate(new Date());
		Date modifiedDate = serviceContext.getModifiedDate(new Date());
		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());

		long kaleoDefinitionVersionId = counterLocalService.increment();

		KaleoDefinitionVersion kaleoDefinitionVersion =
			kaleoDefinitionVersionPersistence.create(kaleoDefinitionVersionId);

		kaleoDefinitionVersion.setGroupId(
			_staging.getLiveGroupId(serviceContext.getScopeGroupId()));
		kaleoDefinitionVersion.setCompanyId(user.getCompanyId());
		kaleoDefinitionVersion.setUserId(user.getUserId());
		kaleoDefinitionVersion.setUserName(user.getFullName());
		kaleoDefinitionVersion.setCreateDate(createDate);
		kaleoDefinitionVersion.setModifiedDate(modifiedDate);
		kaleoDefinitionVersion.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoDefinitionVersion.setName(name);
		kaleoDefinitionVersion.setTitle(title);
		kaleoDefinitionVersion.setDescription(description);
		kaleoDefinitionVersion.setContent(content);
		kaleoDefinitionVersion.setVersion(version);
		kaleoDefinitionVersion.setStatus(
			GetterUtil.getInteger(
				serviceContext.getAttribute("status"),
				WorkflowConstants.STATUS_APPROVED));
		kaleoDefinitionVersion.setStatusByUserId(user.getUserId());
		kaleoDefinitionVersion.setStatusByUserName(user.getFullName());
		kaleoDefinitionVersion.setStatusDate(modifiedDate);

		kaleoDefinitionVersion = kaleoDefinitionVersionPersistence.update(
			kaleoDefinitionVersion);

		// Resources

		_resourceLocalService.addModelResources(
			kaleoDefinitionVersion, serviceContext);

		return kaleoDefinitionVersion;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public KaleoDefinitionVersion deleteKaleoDefinitionVersion(
			KaleoDefinitionVersion kaleoDefinitionVersion)
		throws PortalException {

		// Kaleo definition version

		int kaleoInstancesCount = _kaleoInstancePersistence.countByKDVI_C(
			kaleoDefinitionVersion.getKaleoDefinitionVersionId(), false);

		if (kaleoInstancesCount > 0) {
			throw new IncompleteWorkflowInstancesException(kaleoInstancesCount);
		}

		kaleoDefinitionVersionPersistence.remove(kaleoDefinitionVersion);

		// Resources

		_resourceLocalService.deleteResource(
			kaleoDefinitionVersion, ResourceConstants.SCOPE_INDIVIDUAL);

		// Kaleo condition

		_kaleoConditionLocalService.deleteKaleoDefinitionVersionKaleoCondition(
			kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		// Kaleo instances

		_kaleoInstanceLocalService.deleteKaleoDefinitionVersionKaleoInstances(
			kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		// Kaleo nodes

		_kaleoNodeLocalService.deleteKaleoDefinitionVersionKaleoNodes(
			kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		// Kaleo tasks

		_kaleoTaskLocalService.deleteKaleoDefinitionVersionKaleoTasks(
			kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		// Kaleo transitions

		_kaleoTransitionLocalService.
			deleteKaleoDefinitionVersionKaleoTransitions(
				kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		return kaleoDefinitionVersion;
	}

	@Override
	public void deleteKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws PortalException {

		kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
			getKaleoDefinitionVersion(companyId, name, version));
	}

	@Override
	public void deleteKaleoDefinitionVersions(KaleoDefinition kaleoDefinition)
		throws PortalException {

		int kaleoInstancesCount = _kaleoInstancePersistence.countByKDI_C(
			kaleoDefinition.getKaleoDefinitionId(), false);

		if (kaleoInstancesCount > 0) {
			throw new IncompleteWorkflowInstancesException(kaleoInstancesCount);
		}

		for (KaleoDefinitionVersion kaleoDefinitionVersion :
				kaleoDefinition.getKaleoDefinitionVersions()) {

			kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
				kaleoDefinitionVersion);
		}
	}

	@Override
	public void deleteKaleoDefinitionVersions(
			List<KaleoDefinitionVersion> kaleoDefinitionVersions)
		throws PortalException {

		for (KaleoDefinitionVersion kaleoDefinitionVersion :
				kaleoDefinitionVersions) {

			kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
				kaleoDefinitionVersion);
		}
	}

	@Override
	public void deleteKaleoDefinitionVersions(long companyId, String name)
		throws PortalException {

		kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			getKaleoDefinitionVersions(companyId, name));
	}

	@Override
	public KaleoDefinitionVersion fetchKaleoDefinitionVersion(
		long companyId, String name, String version) {

		return kaleoDefinitionVersionPersistence.fetchByC_N_V(
			companyId, name, version);
	}

	@Override
	public KaleoDefinitionVersion fetchLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return kaleoDefinitionVersionPersistence.fetchByC_N_First(
			companyId, name,
			KaleoDefinitionVersionIdComparator.getInstance(false));
	}

	@Override
	public KaleoDefinitionVersion getFirstKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return kaleoDefinitionVersionPersistence.findByC_N_First(
			companyId, name,
			KaleoDefinitionVersionIdComparator.getInstance(true));
	}

	@Override
	public KaleoDefinitionVersion getKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws PortalException {

		return kaleoDefinitionVersionPersistence.findByC_N_V(
			companyId, name, version);
	}

	@Override
	public List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		long companyId, int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return kaleoDefinitionVersionPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
			long companyId, String name)
		throws PortalException {

		return kaleoDefinitionVersionPersistence.findByC_N(companyId, name);
	}

	@Override
	public List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		long companyId, String name, int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return kaleoDefinitionVersionPersistence.findByC_N(
			companyId, name, start, end, orderByComparator);
	}

	@Override
	public int getKaleoDefinitionVersionsCount(long companyId) {
		return kaleoDefinitionVersionPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getKaleoDefinitionVersionsCount(long companyId, String name) {
		return kaleoDefinitionVersionPersistence.countByC_N(companyId, name);
	}

	@Override
	public KaleoDefinitionVersion[] getKaleoDefinitionVersionsPrevAndNext(
			long companyId, String name, String version)
		throws PortalException {

		KaleoDefinitionVersion[] kaleoDefinitionVersions =
			ListUtil.getPreviousAndNext(
				kaleoDefinitionVersionPersistence.findByC_N(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					KaleoDefinitionVersionIdComparator.getInstance(true)),
				kaleoDefinitionVersion -> Objects.equals(
					version, kaleoDefinitionVersion.getVersion()),
				KaleoDefinitionVersion[]::new);

		if (kaleoDefinitionVersions[1] != null) {
			return kaleoDefinitionVersions;
		}

		throw new NoSuchDefinitionVersionException(
			StringBundler.concat(
				"{companyId=", companyId, ", name=", name, ", version=",
				version, "}"));
	}

	@Override
	public KaleoDefinitionVersion getLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return kaleoDefinitionVersionPersistence.findByC_N_First(
			companyId, name,
			KaleoDefinitionVersionIdComparator.getInstance(false));
	}

	@Override
	public List<KaleoDefinitionVersion> getLatestKaleoDefinitionVersions(
		long companyId, String keywords, int status, Locale locale, int start,
		int end, OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		List<Long> kaleoDefinitionVersionIds = _getKaleoDefinitionVersionIds(
			companyId, keywords, status);

		if (kaleoDefinitionVersionIds.isEmpty()) {
			return Collections.emptyList();
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		String[] orderByFields = orderByComparator.getOrderByFields();

		String orderByField = orderByFields[0];

		if (Objects.equals(orderByField, "modifiedDate")) {
			orderByField = Field.MODIFIED_DATE;
		}
		else if (Objects.equals(orderByField, "title")) {
			orderByField =
				Field.TITLE + StringPool.UNDERLINE + locale.toString();
		}

		searchSearchRequest.addSorts(
			_sorts.field(Field.getSortableFieldName("active"), SortOrder.DESC),
			_sorts.field(
				Field.getSortableFieldName(orderByField),
				orderByComparator.isAscending() ? SortOrder.ASC :
					SortOrder.DESC));

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId));

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		TermsQuery termsQuery = QueriesUtil.terms(Field.ENTRY_CLASS_PK);

		termsQuery.addValues(
			ArrayUtil.toStringArray(kaleoDefinitionVersionIds));

		booleanQuery.addMustQueryClauses(
			QueriesUtil.term(Field.COMPANY_ID, companyId),
			QueriesUtil.term(
				Field.ENTRY_CLASS_NAME, KaleoDefinitionVersion.class.getName()),
			termsQuery);

		searchSearchRequest.setQuery(booleanQuery);

		if ((end != QueryUtil.ALL_POS) && (start != QueryUtil.ALL_POS)) {
			searchSearchRequest.setSize(end - start);
			searchSearchRequest.setStart(start);
		}

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		return TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return kaleoDefinitionVersionPersistence.fetchByPrimaryKey(
					document.getLong(Field.ENTRY_CLASS_PK));
			});
	}

	@Override
	public int getLatestKaleoDefinitionVersionsCount(
		long companyId, String keywords, int status) {

		List<Long> kaleoDefinitionVersionIds = _getKaleoDefinitionVersionIds(
			companyId, keywords, status);

		return kaleoDefinitionVersionIds.size();
	}

	private List<Long> _getKaleoDefinitionVersionIds(
		long companyId, String keywords, int status) {

		List<Long> kaleoDefinitionVersionIds = new ArrayList<>();

		TermsAggregation termsAggregation = _aggregations.terms(
			"processDefinitionLatestVersions",
			Field.getSortableFieldName(Field.NAME));

		termsAggregation.setSize(10000);

		TopHitsAggregation topHitsAggregation = _aggregations.topHits(
			"topHits");

		topHitsAggregation.addSortFields(
			_sorts.field(
				Field.getSortableFieldName(Field.VERSION), SortOrder.DESC));

		topHitsAggregation.setSize(1);

		termsAggregation.addChildrenAggregations(topHitsAggregation);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addMustQueryClauses(
			QueriesUtil.term("scope", WorkflowDefinitionConstants.SCOPE_ALL));

		if (Validator.isNotNull(keywords)) {
			BooleanQuery keywordsBooleanQuery = QueriesUtil.booleanQuery();

			keywordsBooleanQuery.addShouldQueryClauses(
				QueriesUtil.match(Field.DESCRIPTION, keywords),
				QueriesUtil.match(Field.NAME, keywords));

			String[] localizedFieldNames =
				_searchLocalizationHelper.getLocalizedFieldNames(
					new String[] {Field.TITLE}, new SearchContext());

			for (String localizedFieldName : localizedFieldNames) {
				StringQuery stringQuery = QueriesUtil.string(
					keywords + StringPool.STAR);

				stringQuery.setDefaultField(localizedFieldName);

				keywordsBooleanQuery.addShouldQueryClauses(
					stringQuery,
					QueriesUtil.match(localizedFieldName, keywords));
			}

			booleanQuery.addMustQueryClauses(keywordsBooleanQuery);
		}

		if (status == WorkflowConstants.STATUS_APPROVED) {
			booleanQuery.addMustQueryClauses(QueriesUtil.term("active", 1));
		}
		else if (status == WorkflowConstants.STATUS_DRAFT) {
			booleanQuery.addMustQueryClauses(QueriesUtil.term("active", 0));
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.addAggregation(
			termsAggregation
		).emptySearchEnabled(
			true
		).entryClassNames(
			KaleoDefinitionVersion.class.getName()
		).highlightEnabled(
			false
		).query(
			booleanQuery
		).size(
			0
		).withSearchContext(
			searchContext -> {
				searchContext.setCompanyId(companyId);

				PermissionChecker permissionChecker =
					PermissionThreadLocal.getPermissionChecker();

				if (permissionChecker != null) {
					searchContext.setUserId(permissionChecker.getUserId());
				}
			}
		);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		Map<String, AggregationResult> aggregationResultsMap =
			searchResponse.getAggregationResultsMap();

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResultsMap.get(
				"processDefinitionLatestVersions");

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			TopHitsAggregationResult topHitsAggregationResult =
				(TopHitsAggregationResult)bucket.getChildAggregationResult(
					"topHits");

			SearchHits searchHits = topHitsAggregationResult.getSearchHits();

			for (SearchHit searchHit : searchHits.getSearchHits()) {
				kaleoDefinitionVersionIds.add(
					MapUtil.getLong(
						searchHit.getSourcesMap(), Field.ENTRY_CLASS_PK));
			}
		}

		return kaleoDefinitionVersionIds;
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoInstancePersistence _kaleoInstancePersistence;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private KaleoTransitionLocalService _kaleoTransitionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

}