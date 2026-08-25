/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.internal.links.BrokenLinkAssetSearcher;
import com.liferay.headless.cms.internal.util.CMSGroupUtil;
import com.liferay.headless.cms.resource.v1_0.AssetStatisticsResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;

import java.util.Date;
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

		Long[] spaceGroupIds = CMSGroupUtil.getSpaceGroupIds(
			contextCompany.getCompanyId(), _depotEntryService,
			contextUser.getUserId());

		Long[] selectedSpaceGroupIds = CMSGroupUtil.getSelectedSpaceGroupIds(
			assetLibraryId, contextCompany.getCompanyId(),
			_depotEntryLocalService, groupLocalService, spaceGroupIds);

		if (ArrayUtil.isEmpty(selectedSpaceGroupIds)) {
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
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_APPROVED)));
				setBrokenLinksCount(
					() -> _getBrokenLinksCount(
						objectDefinitionIds, selectedSpaceGroupIds,
						spaceGroupIds));
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
				setTotalCount(
					() -> _getCount(
						selectedSpaceGroupIds, objectDefinitionIds,
						ObjectEntryTable.INSTANCE.status.in(
							CMSWorkflowConstants.STATUSES)));
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
			}
		};
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
					_objectEntryLocalService, _searcher,
					_searchRequestBuilderFactory);

			Map<String, Long> expiredAssetObjectEntryIdsMap =
				brokenLinkAssetSearcher.getExpiredAssetObjectEntryIdsMap(
					contextCompany.getCompanyId(), objectDefinitionIds,
					spaceGroupIds);

			if (expiredAssetObjectEntryIdsMap.isEmpty()) {
				return 0;
			}

			return brokenLinkAssetSearcher.getCount(
				contextCompany.getCompanyId(), selectedSpaceGroupIds,
				expiredAssetObjectEntryIdsMap.keySet());
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