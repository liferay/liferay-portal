/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.internal.field.MissingRequiredFieldsAssetSearcher;
import com.liferay.headless.cms.internal.link.BrokenLinkAssetSearcher;
import com.liferay.headless.cms.internal.link.BrokenLinkTarget;
import com.liferay.headless.cms.internal.usage.CollectionUsageResolver;
import com.liferay.headless.cms.internal.usage.UnlinkedAssetSearcher;
import com.liferay.headless.cms.internal.util.CMSGroupUtil;
import com.liferay.headless.cms.resource.v1_0.AssetStatisticsResource;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregationResult;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstanceTable;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentTable;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceTokenTable;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-statistics.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetStatisticsResource.class
)
public class AssetStatisticsResourceImpl
	extends BaseAssetStatisticsResourceImpl {

	@Override
	public AssetStatistics getAssetStatistics(Long assetLibraryId)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		Long[] selectedSpaceGroupIds = CMSGroupUtil.getSelectedSpaceGroupIds(
			assetLibraryId, contextCompany.getCompanyId(),
			_depotEntryLocalService, groupLocalService,
			CMSGroupUtil.getAdministeredSpaceGroupIds(
				contextCompany.getCompanyId(), _depotEntryService,
				contextUser.getUserId()));

		if (ArrayUtil.isEmpty(selectedSpaceGroupIds)) {
			return _toAssetStatistics();
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionService.getCMSObjectDefinitions(
				contextCompany.getCompanyId(),
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				});

		Long[] objectDefinitionIds = transformToArray(
			objectDefinitions, ObjectDefinition::getObjectDefinitionId,
			Long.class);

		if (ArrayUtil.isEmpty(objectDefinitionIds)) {
			return _toAssetStatistics();
		}

		String[] classNames = transformToArray(
			objectDefinitions, ObjectDefinition::getClassName, String.class);
		Date date = new Date();
		Long[] spaceGroupIds = CMSGroupUtil.getSpaceGroupIds(
			contextCompany.getCompanyId(), _depotEntryService,
			contextUser.getUserId());

		return new AssetStatistics() {
			{
				setApprovedCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_APPROVED)));
				setBrokenLinksCount(
					() -> _getBrokenLinksCount(
						objectDefinitionIds, selectedSpaceGroupIds,
						spaceGroupIds));
				setCompletedWorkflowTasksCount(
					() -> _getWorkflowTasksCount(
						classNames, selectedSpaceGroupIds,
						KaleoTaskInstanceTokenTable.INSTANCE.completed.eq(
							true)));
				setExpiredCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_EXPIRED)));
				setExpiringSoonCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_APPROVED
						).and(
							ObjectEntryTable.INSTANCE.expirationDate.lte(
								new Date(
									date.getTime() +
										(Time.DAY * _EXPIRING_SOON_DAYS)))
						)));
				setInDraftCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_DRAFT)));
				setLongStandingDraftsCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_DRAFT
						).and(
							ObjectEntryTable.INSTANCE.modifiedDate.lt(
								new Date(
									date.getTime() -
										(Time.DAY * _LONG_STANDING_DRAFT_DAYS)))
						)));
				setMissingRequiredFieldsCount(
					() -> _getMissingRequiredFieldsCount(
						selectedSpaceGroupIds));
				setOverdueWorkflowTasksCount(
					() -> _getWorkflowTasksCount(
						classNames, selectedSpaceGroupIds,
						KaleoTaskInstanceTokenTable.INSTANCE.completed.eq(
							false
						).and(
							KaleoTaskInstanceTokenTable.INSTANCE.dueDate.lt(
								date)
						)));
				setPendingCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_PENDING)));
				setReviewDateOverdueCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.reviewDate.lt(
							date
						).and(
							ObjectEntryTable.INSTANCE.status.in(
								CMSWorkflowConstants.STATUSES)
						)));
				setScheduledCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_SCHEDULED)));
				setSlaBreachedWorkflowTasksCount(
					() -> _getSLABreachedWorkflowTasksCount(
						classNames, selectedSpaceGroupIds));
				setTotalCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.in(
							CMSWorkflowConstants.STATUSES)));
				setUnlinkedCount(
					() -> _getUnlinkedCount(selectedSpaceGroupIds));
				setUpcomingReviewCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.reviewDate.gt(
							date
						).and(
							ObjectEntryTable.INSTANCE.reviewDate.lte(
								new Date(
									date.getTime() +
										(Time.DAY * _UPCOMING_REVIEW_DAYS)))
						).and(
							ObjectEntryTable.INSTANCE.status.in(
								CMSWorkflowConstants.STATUSES)
						)));
				setWorkflowTasksCount(
					() -> _getWorkflowTasksCount(
						classNames, selectedSpaceGroupIds,
						KaleoTaskInstanceTokenTable.INSTANCE.completed.eq(
							false)));
			}
		};
	}

	private Predicate _getAssignedWorkflowTasksPredicate(Long[] groupIds) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return null;
		}

		Predicate predicate = _getKaleoTaskAssignmentInstancesPredicate(
			User.class.getName(), new Long[] {contextUser.getUserId()});

		for (Long groupId : groupIds) {
			predicate = predicate.or(
				KaleoTaskInstanceTokenTable.INSTANCE.groupId.eq(
					groupId
				).and(
					_getKaleoTaskAssignmentInstancesPredicate(
						Role.class.getName(),
						ArrayUtil.toArray(
							permissionChecker.getRoleIds(
								contextUser.getUserId(), groupId)))
				));
		}

		return predicate.withParentheses();
	}

	private long _getBrokenLinksCount(
		Long[] objectDefinitionIds, Long[] selectedSpaceGroupIds,
		Long[] spaceGroupIds) {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-82226")) {

			return 0;
		}

		try {
			BrokenLinkAssetSearcher brokenLinkAssetSearcher =
				new BrokenLinkAssetSearcher(
					_aggregations, _objectEntryLocalService, _searcher,
					_searchRequestBuilderFactory);

			Map<String, BrokenLinkTarget> brokenLinkTargetsMap =
				brokenLinkAssetSearcher.getBrokenLinkTargetsMap(
					contextCompany.getCompanyId(), objectDefinitionIds,
					selectedSpaceGroupIds, spaceGroupIds);

			if (brokenLinkTargetsMap.isEmpty()) {
				return 0;
			}

			return brokenLinkAssetSearcher.getCount(
				contextCompany.getCompanyId(), selectedSpaceGroupIds,
				brokenLinkTargetsMap.keySet());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get the broken links count", exception);
			}

			return 0;
		}
	}

	private long _getCount(
		Long[] groupIds, Long[] objectDefinitionIds, Predicate predicate) {

		try {
			return _objectEntryLocalService.getValuesListCount(
				contextCompany.getCompanyId(), groupIds, objectDefinitionIds,
				predicate);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return 0;
		}
	}

	private Predicate _getKaleoTaskAssignmentInstancesPredicate(
		String assigneeClassName, Long[] assigneeClassPKs) {

		return KaleoTaskInstanceTokenTable.INSTANCE.kaleoTaskInstanceTokenId.in(
			DSLQueryFactoryUtil.select(
				KaleoTaskAssignmentInstanceTable.INSTANCE.
					kaleoTaskInstanceTokenId
			).from(
				KaleoTaskAssignmentInstanceTable.INSTANCE
			).where(
				KaleoTaskAssignmentInstanceTable.INSTANCE.assigneeClassName.eq(
					assigneeClassName
				).and(
					KaleoTaskAssignmentInstanceTable.INSTANCE.assigneeClassPK.
						in(assigneeClassPKs)
				)
			));
	}

	private long _getMissingRequiredFieldsCount(Long[] groupIds) {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-82226")) {

			return 0;
		}

		try {
			MissingRequiredFieldsAssetSearcher
				missingRequiredFieldsAssetSearcher =
					new MissingRequiredFieldsAssetSearcher(
						_searcher, _searchRequestBuilderFactory);

			return missingRequiredFieldsAssetSearcher.getCount(
				contextCompany.getCompanyId(), groupIds);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get the missing required fields count",
					exception);
			}

			return 0;
		}
	}

	private List<Long> _getNotifiableWorkflowTaskIds(
			Long[] groupIds, Predicate predicate)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return Collections.emptyList();
		}

		return transform(
			_kaleoTaskInstanceTokenLocalService.<List<Long>>dslQuery(
				DSLQueryFactoryUtil.select(
					KaleoTaskInstanceTokenTable.INSTANCE.
						kaleoTaskInstanceTokenId
				).from(
					KaleoTaskInstanceTokenTable.INSTANCE
				).where(
					predicate.and(
						Predicate.not(
							_getAssignedWorkflowTasksPredicate(groupIds))
					).and(
						_getNotifiableWorkflowTasksPredicate()
					)
				)),
			kaleoTaskInstanceTokenId -> {
				if (!_kaleoTaskInstanceTokenModelResourcePermission.contains(
						permissionChecker, kaleoTaskInstanceTokenId,
						ActionKeys.VIEW)) {

					return null;
				}

				return kaleoTaskInstanceTokenId;
			});
	}

	private Predicate _getNotifiableWorkflowTasksPredicate()
		throws PortalException {

		Long[] roleIds = transformToArray(
			contextUser.getAllRoles(), Role::getRoleId, Long.class);

		Predicate predicate =
			KaleoTaskAssignmentTable.INSTANCE.kaleoClassName.eq(
				KaleoTask.class.getName()
			).and(
				KaleoTaskAssignmentTable.INSTANCE.assigneeClassName.neq(
					User.class.getName())
			).and(
				KaleoTaskAssignmentTable.INSTANCE.assigneeClassName.neq(
					Role.class.getName()
				).or(
					KaleoTaskAssignmentTable.INSTANCE.assigneeClassPK.in(
						roleIds)
				).withParentheses()
			);

		return KaleoTaskInstanceTokenTable.INSTANCE.kaleoTaskId.in(
			DSLQueryFactoryUtil.select(
				KaleoTaskAssignmentTable.INSTANCE.kaleoClassPK
			).from(
				KaleoTaskAssignmentTable.INSTANCE
			).where(
				predicate
			)
		).or(
			_getKaleoTaskAssignmentInstancesPredicate(
				Role.class.getName(), roleIds)
		).withParentheses();
	}

	private long _getSLABreachedWorkflowTasksCount(
			String[] classNames, Long[] groupIds)
		throws PortalException {

		String indexName =
			_indexNameBuilder.getIndexName(contextCompany.getCompanyId()) +
				"-workflow-metrics-sla-task-results";

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(
				new IndicesExistsIndexRequest(indexName));

		if (!indicesExistsIndexResponse.isExists()) {
			return 0;
		}

		Predicate predicate = _getWorkflowTasksPredicate(
			classNames, groupIds,
			KaleoTaskInstanceTokenTable.INSTANCE.completed.eq(false));

		List<Long> kaleoTaskInstanceTokenIds = new ArrayList<>(
			_kaleoTaskInstanceTokenLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					KaleoTaskInstanceTokenTable.INSTANCE.
						kaleoTaskInstanceTokenId
				).from(
					KaleoTaskInstanceTokenTable.INSTANCE
				).where(
					predicate.and(_getAssignedWorkflowTasksPredicate(groupIds))
				)));

		kaleoTaskInstanceTokenIds.addAll(
			_getNotifiableWorkflowTaskIds(groupIds, predicate));

		if (kaleoTaskInstanceTokenIds.isEmpty()) {
			return 0;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		TermsQuery termsQuery = QueriesUtil.terms("taskId");

		termsQuery.addValues(
			transformToArray(
				kaleoTaskInstanceTokenIds, String::valueOf, Object.class));

		booleanQuery.addFilterQueryClauses(
			QueriesUtil.term("breached", true),
			QueriesUtil.term("companyId", contextCompany.getCompanyId()),
			QueriesUtil.term("deleted", false), termsQuery);

		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term("slaDefinitionId", 0),
			QueriesUtil.term("status", WorkflowMetricsSLAStatus.NEW.name()));

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.addAggregation(
			_aggregations.cardinality("taskIdCount", "taskId"));
		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setQuery(booleanQuery);
		searchSearchRequest.setSize(0);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		CardinalityAggregationResult cardinalityAggregationResult =
			(CardinalityAggregationResult)aggregationResultsMap.get(
				"taskIdCount");

		return cardinalityAggregationResult.getValue();
	}

	private long _getUnlinkedCount(Long[] groupIds) {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-82226")) {

			return 0;
		}

		try {
			CollectionUsageResolver collectionUsageResolver =
				new CollectionUsageResolver(
					_assetEntryLocalService, _assetListAssetEntryProvider,
					_assetListEntryAssetEntryRelLocalService,
					_assetListEntryLocalService,
					_assetListEntrySegmentsEntryRelLocalService,
					_assetListEntryUsageLocalService, _depotEntryLocalService,
					_layoutLocalService, _layoutPageTemplateEntryLocalService,
					_layoutPageTemplateStructureLocalService,
					_objectDefinitionLocalService, _objectFieldLocalService,
					_portal, _portletPreferencesLocalService);
			UnlinkedAssetSearcher unlinkedAssetSearcher =
				new UnlinkedAssetSearcher(
					_aggregations, _assetLinkLocalService,
					_objectDefinitionLocalService, _objectEntryLocalService,
					_objectRelationshipLocalService, _searcher,
					_searchRequestBuilderFactory,
					_siteNavigationMenuItemLocalService);

			return unlinkedAssetSearcher.getCount(
				collectionUsageResolver.getCollectionUsages(
					contextCompany.getCompanyId()),
				contextCompany.getCompanyId(), groupIds);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get the unlinked count", exception);
			}

			return 0;
		}
	}

	private long _getWorkflowTasksCount(
			String[] classNames, Long[] groupIds, Predicate predicate)
		throws PortalException {

		predicate = _getWorkflowTasksPredicate(classNames, groupIds, predicate);

		List<Long> notifiableWorkflowTaskIds = _getNotifiableWorkflowTaskIds(
			groupIds, predicate);

		long count = _kaleoTaskInstanceTokenLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				KaleoTaskInstanceTokenTable.INSTANCE
			).where(
				predicate.and(_getAssignedWorkflowTasksPredicate(groupIds))
			));

		return count + notifiableWorkflowTaskIds.size();
	}

	private Predicate _getWorkflowTasksPredicate(
		String[] classNames, Long[] groupIds, Predicate predicate) {

		return KaleoTaskInstanceTokenTable.INSTANCE.companyId.eq(
			contextCompany.getCompanyId()
		).and(
			KaleoTaskInstanceTokenTable.INSTANCE.groupId.in(groupIds)
		).and(
			KaleoTaskInstanceTokenTable.INSTANCE.className.in(classNames)
		).and(
			predicate
		);
	}

	private AssetStatistics _toAssetStatistics() {
		return new AssetStatistics() {
			{
				setApprovedCount(() -> 0L);
				setBrokenLinksCount(() -> 0L);
				setCompletedWorkflowTasksCount(() -> 0L);
				setExpiredCount(() -> 0L);
				setExpiringSoonCount(() -> 0L);
				setInDraftCount(() -> 0L);
				setLongStandingDraftsCount(() -> 0L);
				setMissingRequiredFieldsCount(() -> 0L);
				setOverdueWorkflowTasksCount(() -> 0L);
				setPendingCount(() -> 0L);
				setReviewDateOverdueCount(() -> 0L);
				setScheduledCount(() -> 0L);
				setSlaBreachedWorkflowTasksCount(() -> 0L);
				setTotalCount(() -> 0L);
				setUnlinkedCount(() -> 0L);
				setUpcomingReviewCount(() -> 0L);
				setWorkflowTasksCount(() -> 0L);
			}
		};
	}

	private static final int _EXPIRING_SOON_DAYS = 7;

	private static final int _LONG_STANDING_DRAFT_DAYS = 30;

	private static final int _UPCOMING_REVIEW_DAYS = 7;

	private static final Log _log = LogFactoryUtil.getLog(
		AssetStatisticsResourceImpl.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Reference
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Reference
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken)"
	)
	private ModelResourcePermission<KaleoTaskInstanceToken>
		_kaleoTaskInstanceTokenModelResourcePermission;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}