/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.InventoryAnalysis;
import com.liferay.analytics.cms.rest.dto.v1_0.InventoryAnalysisItem;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.internal.resource.v1_0.util.DateRangeUtil;
import com.liferay.analytics.cms.rest.internal.resource.v1_0.util.ObjectEntryVersionTitleExpressionUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.InventoryAnalysisResource;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRelTable;
import com.liferay.asset.kernel.model.AssetCategoryTable;
import com.liferay.asset.kernel.model.AssetEntries_AssetTagsTable;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetTagGroupRelTable;
import com.liferay.asset.kernel.model.AssetTagTable;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRelTable;
import com.liferay.asset.kernel.model.AssetVocabularyTable;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinitionTable;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.model.ObjectFolderTable;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/inventory-analysis.properties",
	scope = ServiceScope.PROTOTYPE, service = InventoryAnalysisResource.class
)
public class InventoryAnalysisResourceImpl
	extends BaseInventoryAnalysisResourceImpl {

	@Override
	public InventoryAnalysis getInventoryAnalysis(
			Long categoryId, Long depotEntryId, String groupBy,
			String languageId, String rangeEnd, Integer rangeKey,
			String rangeStart, Long structureId, Long tagId, Long vocabularyId,
			Pagination pagination)
		throws Exception {

		InventoryAnalysis inventoryAnalysis = new InventoryAnalysis();

		LicenseManagerUtil.checkFreeTier();

		List<DepotEntry> depotEntries = DepotEntryUtil.getDepotEntries(
			contextCompany.getCompanyId(), depotEntryId);

		if (depotEntries.isEmpty()) {
			inventoryAnalysis.setInventoryAnalysisItems(
				() -> new InventoryAnalysisItem[0]);
			inventoryAnalysis.setInventoryAnalysisItemsCount(() -> 0L);
			inventoryAnalysis.setTotalCount(() -> 0L);

			return inventoryAnalysis;
		}

		Long[] groupIds = DepotEntryUtil.getGroupIds(depotEntries);

		inventoryAnalysis.setInventoryAnalysisItems(
			() -> transformToArray(
				(List<Object[]>)_objectEntryLocalService.dslQuery(
					_getGroupByStep(
						categoryId,
						DSLQueryFactoryUtil.select(
							_getSelectExpressions(groupBy)),
						groupIds, languageId, rangeEnd, rangeKey, rangeStart,
						structureId, tagId, vocabularyId
					).groupBy(
						_getGroupByExpressions(groupBy)
					).orderBy(
						DSLFunctionFactoryUtil.countDistinct(
							ObjectEntryTable.INSTANCE.objectEntryId
						).descending()
					).limit(
						pagination.getStartPosition(),
						pagination.getEndPosition()
					)),
				object -> {
					InventoryAnalysisItem inventoryAnalysisItem =
						new InventoryAnalysisItem();

					inventoryAnalysisItem.setCount(() -> (Long)object[0]);
					inventoryAnalysisItem.setKey(() -> (String)object[1]);
					inventoryAnalysisItem.setTitle(
						() -> {
							Locale preferredLocale =
								contextAcceptLanguage.getPreferredLocale();

							return GetterUtil.get(
								_localization.getLocalization(
									(String)object[2],
									preferredLocale.toString()),
								LanguageUtil.get(preferredLocale, "unknown"));
						});

					return inventoryAnalysisItem;
				},
				InventoryAnalysisItem.class));
		inventoryAnalysis.setInventoryAnalysisItemsCount(
			() -> (long)_objectEntryLocalService.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					_getGroupByStep(
						categoryId,
						DSLQueryFactoryUtil.select(
							_getSelectExpressions(groupBy)),
						groupIds, languageId, rangeEnd, rangeKey, rangeStart,
						structureId, tagId, vocabularyId
					).groupBy(
						_getGroupByExpressions(groupBy)
					).as(
						"InventoryAnalysisItems"
					)
				)));
		inventoryAnalysis.setTotalCount(
			() -> (long)_objectEntryLocalService.dslQueryCount(
				_getGroupByStep(
					categoryId,
					DSLQueryFactoryUtil.countDistinct(
						ObjectEntryTable.INSTANCE.objectEntryId),
					groupIds, languageId, rangeEnd, rangeKey, rangeStart,
					structureId, tagId, vocabularyId)));

		return inventoryAnalysis;
	}

	private Expression<?>[] _getGroupByExpressions(String groupBy) {
		if (StringUtil.equalsIgnoreCase(groupBy, "category")) {
			return new Expression[] {
				AssetCategoryTable.INSTANCE.externalReferenceCode,
				AssetCategoryTable.INSTANCE.name
			};
		}
		else if (StringUtil.equalsIgnoreCase(groupBy, "tag")) {
			return new Expression[] {
				AssetTagTable.INSTANCE.externalReferenceCode,
				AssetTagTable.INSTANCE.name
			};
		}
		else if (StringUtil.equalsIgnoreCase(groupBy, "vocabulary")) {
			return new Expression[] {
				AssetVocabularyTable.INSTANCE.externalReferenceCode,
				AssetVocabularyTable.INSTANCE.name
			};
		}

		return new Expression[] {
			ObjectDefinitionTable.INSTANCE.externalReferenceCode,
			ObjectDefinitionTable.INSTANCE.label
		};
	}

	private GroupByStep _getGroupByStep(
		Long categoryId, FromStep fromStep, Long[] groupIds, String languageId,
		String rangeEnd, Integer rangeKey, String rangeStart, Long structureId,
		Long tagId, Long vocabularyId) {

		AssetCategoryTable assetCategoryTable = AssetCategoryTable.INSTANCE;
		AssetEntries_AssetTagsTable assetEntriesAssetTagsTable =
			AssetEntries_AssetTagsTable.INSTANCE;
		AssetEntryAssetCategoryRelTable assetEntryAssetCategoryRelTable =
			AssetEntryAssetCategoryRelTable.INSTANCE;
		AssetEntryTable assetEntryTable = AssetEntryTable.INSTANCE;
		AssetTagGroupRelTable assetTagGroupRelTable =
			AssetTagGroupRelTable.INSTANCE;
		AssetTagTable assetTagTable = AssetTagTable.INSTANCE;
		AssetVocabularyGroupRelTable assetVocabularyGroupRelTable =
			AssetVocabularyGroupRelTable.INSTANCE;
		AssetVocabularyTable assetVocabularyTable =
			AssetVocabularyTable.INSTANCE;
		ObjectDefinitionTable objectDefinitionTable =
			ObjectDefinitionTable.INSTANCE;
		ObjectEntryTable objectEntryTable = ObjectEntryTable.INSTANCE;
		ObjectEntryVersionTable objectEntryVersionTable =
			ObjectEntryVersionTable.INSTANCE;
		ObjectFolderTable objectFolderTable = ObjectFolderTable.INSTANCE;

		Long[] assetGroupIds = groupIds;

		if (ArrayUtil.isNotEmpty(groupIds)) {
			assetGroupIds = ArrayUtil.append(assetGroupIds, -1L);
		}

		return fromStep.from(
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
			objectEntryVersionTable,
			objectEntryVersionTable.objectEntryId.eq(
				objectEntryTable.objectEntryId
			).and(
				objectEntryVersionTable.version.eq(objectEntryTable.version)
			).and(
				objectEntryVersionTable.status.eq(
					WorkflowConstants.STATUS_APPROVED)
			)
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
			_getPredicate(
				categoryId, groupIds, languageId, rangeEnd, rangeKey,
				rangeStart, structureId, tagId, vocabularyId)
		);
	}

	private Predicate _getPredicate(
		Long categoryId, Long[] groupIds, String languageId, String rangeEnd,
		Integer rangeKey, String rangeStart, Long structureId, Long tagId,
		Long vocabularyId) {

		Predicate predicate =
			ObjectFolderTable.INSTANCE.externalReferenceCode.in(
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				});

		predicate = predicate.and(
			ObjectEntryTable.INSTANCE.status.neq(
				WorkflowConstants.STATUS_IN_TRASH));

		if (categoryId != null) {
			predicate = predicate.and(
				AssetEntryAssetCategoryRelTable.INSTANCE.assetCategoryId.eq(
					categoryId));
		}

		if (ArrayUtil.isNotEmpty(groupIds)) {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.groupId.in(groupIds));
		}

		if (Validator.isNotNull(languageId)) {
			predicate = predicate.and(
				DSLFunctionFactoryUtil.castClobText(
					ObjectEntryVersionTitleExpressionUtil.
						getLocalizedTitleExpression(languageId)
				).isNotNull());
		}

		Date startDate = DateRangeUtil.getStartDate(
			rangeEnd, rangeKey, rangeStart);

		if (startDate != null) {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.createDate.gte(startDate));
		}

		if (Validator.isNotNull(rangeEnd)) {
			predicate = predicate.and(
				ObjectEntryTable.INSTANCE.createDate.lte(
					DateRangeUtil.getEndDate(rangeEnd)));
		}

		if (structureId != null) {
			predicate = predicate.and(
				ObjectDefinitionTable.INSTANCE.objectDefinitionId.eq(
					structureId));
		}

		if (tagId != null) {
			predicate = predicate.and(
				AssetTagGroupRelTable.INSTANCE.tagId.eq(tagId));
		}

		if (vocabularyId != null) {
			predicate = predicate.and(
				AssetVocabularyGroupRelTable.INSTANCE.vocabularyId.eq(
					vocabularyId));
		}

		return predicate;
	}

	private Expression<?>[] _getSelectExpressions(String groupBy) {
		if (StringUtil.equalsIgnoreCase(groupBy, "category")) {
			return new Expression[] {
				DSLFunctionFactoryUtil.countDistinct(
					ObjectEntryTable.INSTANCE.objectEntryId
				).as(
					"count_"
				),
				AssetCategoryTable.INSTANCE.externalReferenceCode.as("key_"),
				AssetCategoryTable.INSTANCE.name.as("title")
			};
		}
		else if (StringUtil.equalsIgnoreCase(groupBy, "tag")) {
			return new Expression[] {
				DSLFunctionFactoryUtil.countDistinct(
					ObjectEntryTable.INSTANCE.objectEntryId
				).as(
					"count_"
				),
				AssetTagTable.INSTANCE.externalReferenceCode.as("key_"),
				AssetTagTable.INSTANCE.name.as("title")
			};
		}
		else if (StringUtil.equalsIgnoreCase(groupBy, "vocabulary")) {
			return new Expression[] {
				DSLFunctionFactoryUtil.countDistinct(
					ObjectEntryTable.INSTANCE.objectEntryId
				).as(
					"count_"
				),
				AssetVocabularyTable.INSTANCE.externalReferenceCode.as("key_"),
				AssetVocabularyTable.INSTANCE.name.as("title")
			};
		}

		return new Expression[] {
			DSLFunctionFactoryUtil.countDistinct(
				ObjectEntryTable.INSTANCE.objectEntryId
			).as(
				"count_"
			),
			ObjectDefinitionTable.INSTANCE.externalReferenceCode.as("key_"),
			ObjectDefinitionTable.INSTANCE.label.as("title")
		};
	}

	@Reference
	private Localization _localization;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}