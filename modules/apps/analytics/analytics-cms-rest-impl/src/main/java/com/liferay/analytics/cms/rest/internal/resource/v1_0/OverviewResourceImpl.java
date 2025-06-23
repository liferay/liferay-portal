/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.Overview;
import com.liferay.analytics.cms.rest.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.resource.v1_0.OverviewResource;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRelTable;
import com.liferay.asset.kernel.model.AssetCategoryTable;
import com.liferay.asset.kernel.model.AssetEntries_AssetTagsTable;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetTagGroupRelTable;
import com.liferay.asset.kernel.model.AssetTagTable;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRelTable;
import com.liferay.asset.kernel.model.AssetVocabularyTable;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/overview.properties",
	scope = ServiceScope.PROTOTYPE, service = OverviewResource.class
)
public class OverviewResourceImpl extends BaseOverviewResourceImpl {

	@Override
	public Overview getContentOverview(
			String languageId, String rangeEnd, Integer rangeKey,
			String rangeStart, Long spaceId)
		throws Exception {

		List<DepotEntry> depotEntries = _getDepotEntries(spaceId);

		if (depotEntries.isEmpty()) {
			return _toOverview(0, Trend.Classification.NEUTRAL, 0.0, 0, 0, 0);
		}

		Long[] groupIds = _getGroupIds(depotEntries);

		return _toOverview(
			_getOverviewObjects(
				"L_CMS_CONTENT_STRUCTURES", groupIds, languageId, rangeEnd,
				rangeKey, rangeStart),
			_getPreviousTotalCount(
				"L_CMS_CONTENT_STRUCTURES", groupIds, languageId, rangeEnd,
				rangeKey, rangeStart));
	}

	@Override
	public Overview getFileOverview(
			String languageId, String rangeEnd, Integer rangeKey,
			String rangeStart, Long spaceId)
		throws Exception {

		List<DepotEntry> depotEntries = _getDepotEntries(spaceId);

		if (depotEntries.isEmpty()) {
			return _toOverview(0, Trend.Classification.NEUTRAL, 0.0, 0, 0, 0);
		}

		Long[] groupIds = _getGroupIds(depotEntries);

		return _toOverview(
			_getOverviewObjects(
				"L_CMS_FILE_TYPES", groupIds, languageId, rangeEnd, rangeKey,
				rangeStart),
			_getPreviousTotalCount(
				"L_CMS_FILE_TYPES", groupIds, languageId, rangeEnd, rangeKey,
				rangeStart));
	}

	private DateFormat _getDateFormat() {
		return DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");
	}

	private List<DepotEntry> _getDepotEntries(Long spaceId) throws Exception {
		List<DepotEntry> depotEntries = new ArrayList<>();

		if (spaceId == null) {
			depotEntries.addAll(_getViewableDepotEntries());
		}
		else {
			depotEntries.add(_depotEntryService.getDepotEntry(spaceId));
		}

		return depotEntries;
	}

	private Date _getEndDate(String rangeEnd) {
		try {
			Calendar calendar = Calendar.getInstance();

			DateFormat dateFormat = _getDateFormat();

			calendar.setTime(dateFormat.parse(rangeEnd));

			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MILLISECOND, 59);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);

			return calendar.getTime();
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException);
			}
		}

		return null;
	}

	private Long[] _getGroupIds(List<DepotEntry> depotEntries) {
		Long[] groupIds = new Long[0];

		for (DepotEntry depotEntry : depotEntries) {
			groupIds = ArrayUtil.append(groupIds, depotEntry.getGroupId());

			List<DepotEntryGroupRel> depotEntryGroupRels =
				_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
					depotEntry);

			for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
				groupIds = ArrayUtil.append(
					groupIds, depotEntryGroupRel.getGroupId());
			}
		}

		return groupIds;
	}

	private Object[] _getOverviewObjects(
		String externalReferenceCode, Long[] groupIds, String languageId,
		String rangeEnd, Integer rangeKey, String rangeStart) {

		AssetCategoryTable assetCategoryTable = AssetCategoryTable.INSTANCE;
		AssetEntries_AssetTagsTable assetEntriesAssetTagsTable =
			AssetEntries_AssetTagsTable.INSTANCE;
		AssetEntryAssetCategoryRelTable assetEntryAssetCategoryRelTable =
			AssetEntryAssetCategoryRelTable.INSTANCE;
		AssetEntryTable assetEntryTable = AssetEntryTable.INSTANCE;
		AssetTagTable assetTagTable = AssetTagTable.INSTANCE;
		AssetTagGroupRelTable assetTagGroupRelTable =
			AssetTagGroupRelTable.INSTANCE;
		AssetVocabularyTable assetVocabularyTable =
			AssetVocabularyTable.INSTANCE;
		AssetVocabularyGroupRelTable assetVocabularyGroupRelTable =
			AssetVocabularyGroupRelTable.INSTANCE;
		ObjectDefinitionTable objectDefinitionTable =
			ObjectDefinitionTable.INSTANCE;
		ObjectEntryTable objectEntryTable = ObjectEntryTable.INSTANCE;
		ObjectFolderTable objectFolderTable = ObjectFolderTable.INSTANCE;

		Long[] assetGroupIds = groupIds;

		if (ArrayUtil.isNotEmpty(groupIds)) {
			assetGroupIds = ArrayUtil.append(assetGroupIds, -1L);
		}

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLFunctionFactoryUtil.countDistinct(
				assetEntryAssetCategoryRelTable.assetCategoryId
			).as(
				"categoriesCount"
			),
			DSLFunctionFactoryUtil.countDistinct(
				assetEntriesAssetTagsTable.tagId
			).as(
				"tagsCount"
			),
			DSLFunctionFactoryUtil.count(
				objectEntryTable.objectEntryId
			).as(
				"totalCount"
			),
			DSLFunctionFactoryUtil.countDistinct(
				assetCategoryTable.vocabularyId
			).as(
				"vocabulariesCount"
			)
		).from(
			objectFolderTable
		).innerJoinON(
			objectDefinitionTable,
			objectDefinitionTable.objectFolderId.eq(
				objectFolderTable.objectFolderId)
		).innerJoinON(
			objectEntryTable,
			objectEntryTable.objectDefinitionId.eq(
				objectDefinitionTable.objectDefinitionId)
		).innerJoinON(
			assetEntryTable,
			assetEntryTable.classPK.eq(objectEntryTable.objectEntryId)
		).leftJoinOn(
			assetEntriesAssetTagsTable,
			assetEntriesAssetTagsTable.entryId.eq(assetEntryTable.entryId)
		).leftJoinOn(
			assetTagTable,
			assetTagTable.tagId.eq(assetEntriesAssetTagsTable.tagId)
		).leftJoinOn(
			assetTagGroupRelTable,
			assetTagGroupRelTable.tagId.eq(
				assetTagTable.tagId
			).and(
				assetTagGroupRelTable.groupId.in(assetGroupIds)
			)
		).leftJoinOn(
			assetEntryAssetCategoryRelTable,
			assetEntryAssetCategoryRelTable.assetEntryId.eq(
				assetEntryTable.entryId)
		).leftJoinOn(
			assetCategoryTable,
			assetCategoryTable.categoryId.eq(
				assetEntryAssetCategoryRelTable.assetCategoryId)
		).leftJoinOn(
			assetVocabularyTable,
			assetVocabularyTable.vocabularyId.eq(
				assetCategoryTable.vocabularyId)
		).leftJoinOn(
			assetVocabularyGroupRelTable,
			assetVocabularyGroupRelTable.vocabularyId.eq(
				assetVocabularyTable.vocabularyId
			).and(
				assetVocabularyGroupRelTable.groupId.in(assetGroupIds)
			)
		).where(
			_getWhereClause(
				externalReferenceCode, groupIds, languageId, false, rangeEnd,
				rangeKey, rangeStart)
		);

		List<Object[]> results = _objectEntryLocalService.dslQuery(dslQuery);

		if (results.isEmpty()) {
			return new Object[] {0, 0, 0, 0};
		}

		return results.get(0);
	}

	private Date _getPreviousStartDate(
		String rangeEnd, Integer rangeKey, String rangeStart) {

		Calendar calendar = Calendar.getInstance();

		if (Validator.isNotNull(rangeEnd) && Validator.isNotNull(rangeStart)) {
			try {
				calendar.setTime(_getStartDate(null, rangeStart));

				DateFormat dateFormat = _getDateFormat();

				int delta = DateUtil.getDaysBetween(
					dateFormat.parse(rangeStart), dateFormat.parse(rangeEnd));

				calendar.add(Calendar.DAY_OF_MONTH, -delta);
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException);
				}
			}
		}
		else {
			calendar.add(Calendar.DAY_OF_MONTH, -(rangeKey * 2));
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	private long _getPreviousTotalCount(
		String externalReferenceCode, Long[] groupIds, String languageId,
		String rangeEnd, Integer rangeKey, String rangeStart) {

		AssetEntryTable assetEntryTable = AssetEntryTable.INSTANCE;
		ObjectDefinitionTable objectDefinitionTable =
			ObjectDefinitionTable.INSTANCE;
		ObjectEntryTable objectEntryTable = ObjectEntryTable.INSTANCE;
		ObjectFolderTable objectFolderTable = ObjectFolderTable.INSTANCE;

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			DSLFunctionFactoryUtil.count(
				objectEntryTable.objectEntryId
			).as(
				"totalCount"
			)
		).from(
			objectFolderTable
		).innerJoinON(
			objectDefinitionTable,
			objectDefinitionTable.objectFolderId.eq(
				objectFolderTable.objectFolderId)
		).innerJoinON(
			objectEntryTable,
			objectEntryTable.objectDefinitionId.eq(
				objectDefinitionTable.objectDefinitionId)
		).innerJoinON(
			assetEntryTable,
			assetEntryTable.classPK.eq(objectEntryTable.objectEntryId)
		).where(
			_getWhereClause(
				externalReferenceCode, groupIds, languageId, true, rangeEnd,
				rangeKey, rangeStart)
		);

		List<Object[]> results = _objectEntryLocalService.dslQuery(dslQuery);

		if (results.isEmpty()) {
			return 0;
		}

		return GetterUtil.getLong(results.get(0));
	}

	private Date _getStartDate(Integer rangeKey, String rangeStart) {
		Calendar calendar = Calendar.getInstance();

		if (Validator.isNotNull(rangeStart)) {
			try {
				DateFormat dateFormat = _getDateFormat();

				calendar.setTime(dateFormat.parse(rangeStart));
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException);
				}
			}
		}
		else {
			calendar.add(Calendar.DAY_OF_MONTH, -rangeKey);
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	private List<DepotEntry> _getViewableDepotEntries() throws Exception {
		List<DepotEntry> depotEntries = new ArrayList<>();

		SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			null, DepotEntry.class.getName(), null,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			queryConfig -> {
			},
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			null,
			document -> {
				try {
					depotEntries.add(
						_depotEntryService.getDepotEntry(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK))));
				}
				catch (PortalException portalException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"User does not have access to view space " +
								document.get(Field.ENTRY_CLASS_PK),
							portalException);
					}
				}

				return null;
			});

		return depotEntries;
	}

	private Predicate _getWhereClause(
		String externalReferenceCode, Long[] groupIds, String languageId,
		boolean previous, String rangeEnd, Integer rangeKey,
		String rangeStart) {

		Predicate predicate =
			ObjectFolderTable.INSTANCE.externalReferenceCode.eq(
				externalReferenceCode);

		predicate = predicate.and(
			ObjectEntryTable.INSTANCE.status.neq(
				WorkflowConstants.STATUS_IN_TRASH));

		if (ArrayUtil.isNotEmpty(groupIds)) {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.groupId.in(groupIds));
		}

		if (!Validator.isBlank(languageId)) {
			predicate = predicate.and(
				AssetEntryTable.INSTANCE.title.like(
					"%language-id=\"" + languageId + "\"%"));
		}

		if (!previous) {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.createDate.gte(
					_getStartDate(rangeKey, rangeStart)));

			if (Validator.isNotNull(rangeEnd)) {
				predicate = predicate.and(
					ObjectEntryTable.INSTANCE.createDate.lte(
						_getEndDate(rangeEnd)));
			}
		}
		else {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.createDate.gte(
					_getPreviousStartDate(rangeEnd, rangeKey, rangeStart))
			).and(
				ObjectEntryTable.INSTANCE.createDate.lt(
					_getStartDate(rangeKey, rangeStart))
			);
		}

		return predicate;
	}

	private Overview _toOverview(
		long categoriesCount, Trend.Classification classification,
		double percentage, long tagsCount, long totalCount,
		long vocabulariesCount) {

		Overview overview = new Overview();

		overview.setCategoriesCount(() -> categoriesCount);
		overview.setTagsCount(() -> tagsCount);
		overview.setTotalCount(() -> totalCount);

		Trend trend = new Trend();

		trend.setClassification(() -> classification);
		trend.setPercentage(() -> percentage);

		overview.setTrend(() -> trend);

		overview.setVocabulariesCount(() -> vocabulariesCount);

		return overview;
	}

	private Overview _toOverview(Object[] objects, long previousTotalCount) {
		long categoriesCount = (Long)objects[0];
		long tagsCount = (Long)objects[1];
		long totalCount = (Long)objects[2];
		long vocabulariesCount = (Long)objects[3];

		Trend.Classification classification = Trend.Classification.NEUTRAL;
		double percentage = 0.0;

		if (previousTotalCount > 0) {
			double diff = totalCount - previousTotalCount;

			percentage = diff / previousTotalCount * 100.0;

			if (percentage > 0) {
				classification = Trend.Classification.POSITIVE;
			}
			else if (percentage < 0) {
				classification = Trend.Classification.NEGATIVE;
			}
		}
		else if (totalCount > 0) {
			classification = Trend.Classification.POSITIVE;
			percentage = 100.0;
		}

		return _toOverview(
			categoriesCount, classification, percentage, tagsCount, totalCount,
			vocabulariesCount);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OverviewResourceImpl.class);

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}