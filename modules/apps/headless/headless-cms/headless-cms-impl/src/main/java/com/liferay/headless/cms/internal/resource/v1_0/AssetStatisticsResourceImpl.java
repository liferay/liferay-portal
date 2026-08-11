/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.resource.v1_0.AssetStatisticsResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;
import com.liferay.site.cms.site.initializer.util.CMSOutboundLinksUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

		Long[] groupIds = _getGroupIds(assetLibraryId);

		if (ArrayUtil.isEmpty(groupIds)) {
			return _toAssetStatistics();
		}

		Long[] objectDefinitionIds = transformToArray(
			_objectDefinitionService.getCMSObjectDefinitions(
				contextCompany.getCompanyId(),
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				}),
			ObjectDefinition::getObjectDefinitionId, Long.class);

		if (ArrayUtil.isEmpty(objectDefinitionIds)) {
			return _toAssetStatistics();
		}

		Date date = new Date();

		return new AssetStatistics() {
			{
				setApprovedCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_APPROVED)));
				setBrokenLinksCount(
					() -> _getBrokenLinksCount(groupIds, objectDefinitionIds));
				setExpiredCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_EXPIRED)));
				setExpiringSoonCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
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
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_DRAFT)));
				setPendingCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_PENDING)));
				setReviewDateOverdueCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.reviewDate.lt(
							date
						).and(
							ObjectEntryTable.INSTANCE.status.in(
								CMSWorkflowConstants.STATUSES)
						)));
				setScheduledCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_SCHEDULED)));
				setTotalCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.in(
							CMSWorkflowConstants.STATUSES)));
				setUpcomingReviewCount(
					() -> _getCount(
						groupIds, objectDefinitionIds,
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
			}
		};
	}

	private BooleanQuery _createOutboundLinksBooleanQuery(
		List<String> outboundLinkTokens) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		for (int i = 0; i < outboundLinkTokens.size(); i += _MAX_TERMS_COUNT) {
			List<String> values = outboundLinkTokens.subList(
				i, Math.min(i + _MAX_TERMS_COUNT, outboundLinkTokens.size()));

			booleanQuery.addShouldQueryClauses(
				_createTermsQuery(
					CMSOutboundLinksUtil.FIELD_NAME,
					values.toArray(new String[0])));
		}

		booleanQuery.setMinimumShouldMatch(1);

		return booleanQuery;
	}

	private TermsQuery _createTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private long _getBrokenLinksCount(
		Long[] groupIds, Long[] objectDefinitionIds) {

		try {
			if (!FeatureFlagManagerUtil.isEnabled(
					contextCompany.getCompanyId(), "LPD-82226")) {

				return 0;
			}

			List<String> expiredAssetTokens = _getExpiredAssetTokens(
				objectDefinitionIds);

			if (expiredAssetTokens.isEmpty()) {
				return 0;
			}

			BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

			booleanQuery.addFilterQueryClauses(
				_createOutboundLinksBooleanQuery(expiredAssetTokens),
				_createTermsQuery("cms_section", "contents", "files"),
				_createTermsQuery(
					Field.STATUS,
					ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
				QueriesUtil.term("rootDescendantNode", false));

			SearchResponse searchResponse = _searcher.search(
				_searchRequestBuilderFactory.builder(
				).companyId(
					contextCompany.getCompanyId()
				).emptySearchEnabled(
					true
				).groupIds(
					ArrayUtil.toArray(groupIds)
				).query(
					booleanQuery
				).withSearchContext(
					searchContext -> searchContext.setAttribute(
						Field.STATUS, WorkflowConstants.STATUS_ANY)
				).build());

			return searchResponse.getCount();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
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

	private List<String> _getExpiredAssetTokens(Long[] objectDefinitionIds) {
		List<String> expiredAssetTokens = new ArrayList<>();

		List<Object[]> results = _objectEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectEntryTable.INSTANCE.externalReferenceCode,
				ObjectEntryTable.INSTANCE.objectEntryId
			).from(
				ObjectEntryTable.INSTANCE
			).where(
				ObjectEntryTable.INSTANCE.companyId.eq(
					contextCompany.getCompanyId()
				).and(
					ObjectEntryTable.INSTANCE.objectDefinitionId.in(
						objectDefinitionIds)
				).and(
					ObjectEntryTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_EXPIRED)
				)
			));

		for (Object[] objects : results) {
			expiredAssetTokens.add(
				CMSOutboundLinksUtil.getObjectEntryExternalReferenceCodeToken(
					GetterUtil.getString(objects[0])));
			expiredAssetTokens.add(
				CMSOutboundLinksUtil.getObjectEntryIdToken(
					GetterUtil.getLong(objects[1])));
		}

		return expiredAssetTokens;
	}

	private Long[] _getGroupIds(Long assetLibraryId) {
		List<Long> depotEntryGroupIds =
			_depotEntryService.getDepotEntryGroupIds(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				DepotConstants.TYPE_SPACE);

		if (assetLibraryId == null) {
			return depotEntryGroupIds.toArray(new Long[0]);
		}

		Long groupId = GroupUtil.getDepotGroupId(
			String.valueOf(assetLibraryId), contextCompany.getCompanyId(),
			_depotEntryLocalService, groupLocalService);

		if ((groupId == null) || !depotEntryGroupIds.contains(groupId)) {
			return new Long[0];
		}

		return new Long[] {groupId};
	}

	private AssetStatistics _toAssetStatistics() {
		return new AssetStatistics() {
			{
				setApprovedCount(() -> 0L);
				setBrokenLinksCount(() -> 0L);
				setExpiredCount(() -> 0L);
				setExpiringSoonCount(() -> 0L);
				setInDraftCount(() -> 0L);
				setPendingCount(() -> 0L);
				setReviewDateOverdueCount(() -> 0L);
				setScheduledCount(() -> 0L);
				setTotalCount(() -> 0L);
				setUpcomingReviewCount(() -> 0L);
			}
		};
	}

	private static final int _EXPIRING_SOON_DAYS = 7;

	private static final int _MAX_TERMS_COUNT = 65536;

	private static final int _UPCOMING_REVIEW_DAYS = 7;

	private static final Log _log = LogFactoryUtil.getLog(
		AssetStatisticsResourceImpl.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}