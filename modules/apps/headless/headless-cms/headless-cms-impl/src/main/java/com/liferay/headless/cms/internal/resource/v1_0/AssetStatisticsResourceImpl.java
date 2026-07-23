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
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;

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

}