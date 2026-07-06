/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.constants.CreateStrategy;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Vendel Toreki
 * @author Alejandro Tardín
 * @author Petteri Karttunen
 */
public class BatchEnginePortletDataHandlerUtil {

	public static Map<String, Serializable> buildDeleteParameters(
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		GroupLocalService groupLocalService,
		PortletDataContext portletDataContext,
		StagingGroupHelper stagingGroupHelper) {

		Map<String, Serializable> deleteParameters =
			HashMapBuilder.<String, Serializable>putAll(
				exportImportDescriptor.getParameters(portletDataContext)
			).build();

		Group group = groupLocalService.fetchGroup(
			portletDataContext.getScopeGroupId());

		if (!_isCompanyScoped(group, stagingGroupHelper)) {
			deleteParameters.put(
				"siteExternalReferenceCode", group.getExternalReferenceCode());
			deleteParameters.put("siteId", group.getGroupId());
		}

		return deleteParameters;
	}

	public static Map<String, Serializable> buildExportParameters(
		ChangesetEntryLocalService changesetEntryLocalService,
		ClassNameLocalService classNameLocalService,
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		GroupLocalService groupLocalService,
		PortletDataContext portletDataContext,
		StagingGroupHelper stagingGroupHelper) {

		Map<String, Serializable> exportImportDescriptorParameters =
			exportImportDescriptor.getParameters(portletDataContext);

		Map<String, Serializable> exportParameters =
			HashMapBuilder.<String, Serializable>put(
				"batchNestedFields",
				() -> {
					List<String> batchNestedFields = new ArrayList<>();

					batchNestedFields.add("customFields.attributeType");

					if (MapUtil.getBoolean(
							portletDataContext.getParameterMap(),
							PortletDataHandlerKeys.COMMENTS)) {

						batchNestedFields.add("systemProperties.comments");
					}

					if (MapUtil.getBoolean(
							portletDataContext.getParameterMap(),
							PortletDataHandlerKeys.PERMISSIONS)) {

						batchNestedFields.add("permissions");
					}

					if (ListUtil.isNotEmpty(
							exportImportDescriptor.getNestedFields())) {

						batchNestedFields.addAll(
							exportImportDescriptor.getNestedFields());
					}

					if (batchNestedFields.isEmpty()) {
						return null;
					}

					return StringUtil.merge(
						batchNestedFields, StringPool.COMMA);
				}
			).put(
				"filter",
				() -> {
					List<String> filterStrings = new ArrayList<>();

					_addDateFilterStrings(
						changesetEntryLocalService, classNameLocalService,
						exportImportDescriptor, filterStrings,
						portletDataContext);

					if (exportImportDescriptorParameters != null) {
						String exportImportDescriptorFilterString =
							GetterUtil.getString(
								exportImportDescriptorParameters.remove(
									"filter"));

						if (Validator.isNotNull(
								exportImportDescriptorFilterString)) {

							filterStrings.add(
								"(" + exportImportDescriptorFilterString + ")");
						}
					}

					return StringUtil.merge(filterStrings, " and ");
				}
			).put(
				"modelClassName", exportImportDescriptor.getModelClassName()
			).put(
				"modelNameLanguageKey",
				exportImportDescriptor.getLabelLanguageKey()
			).putAll(
				exportImportDescriptorParameters
			).build();

		Group group = groupLocalService.fetchGroup(
			portletDataContext.getScopeGroupId());

		if (!_isCompanyScoped(group, stagingGroupHelper)) {
			exportParameters.put(
				"siteExternalReferenceCode", group.getExternalReferenceCode());

			Map<String, String[]> map = portletDataContext.getParameterMap();

			String[] siteIds = GetterUtil.getStringValues(map.get("siteId"));

			if (ArrayUtil.isNotEmpty(siteIds)) {
				exportParameters.put("siteId", siteIds[0]);
			}
			else {
				exportParameters.put("siteId", group.getGroupId());
			}
		}

		return exportParameters;
	}

	public static Map<String, Serializable> buildImportParameters(
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		GroupLocalService groupLocalService,
		PortletDataContext portletDataContext,
		StagingGroupHelper stagingGroupHelper) {

		Map<String, Serializable> importParameters =
			HashMapBuilder.<String, Serializable>put(
				"batchRestrictFields",
				() -> {
					List<String> batchRestrictFields = new ArrayList<>();

					if (!MapUtil.getBoolean(
							portletDataContext.getParameterMap(),
							PortletDataHandlerKeys.COMMENTS)) {

						batchRestrictFields.add("systemProperties.comments");
					}

					if (!MapUtil.getBoolean(
							portletDataContext.getParameterMap(),
							PortletDataHandlerKeys.PERMISSIONS)) {

						batchRestrictFields.add("permissions");
					}

					if (batchRestrictFields.isEmpty()) {
						return null;
					}

					return StringUtil.merge(
						batchRestrictFields, StringPool.COMMA);
				}
			).put(
				"createStrategy", CreateStrategy.UPSERT.getDBOperation()
			).put(
				"importCreatorStrategy",
				() -> {
					if (!UserIdStrategy.CURRENT_USER_ID.equals(
							MapUtil.getString(
								portletDataContext.getParameterMap(),
								PortletDataHandlerKeys.USER_ID_STRATEGY))) {

						return null;
					}

					return BatchEngineImportTaskConstants.
						IMPORT_CREATOR_STRATEGY_KEEP_CREATOR;
				}
			).put(
				"modelClassName", exportImportDescriptor.getModelClassName()
			).put(
				"modelNameLanguageKey",
				exportImportDescriptor.getLabelLanguageKey()
			).putAll(
				exportImportDescriptor.getParameters(portletDataContext)
			).build();

		Group group = groupLocalService.fetchGroup(
			portletDataContext.getScopeGroupId());

		if (!_isCompanyScoped(group, stagingGroupHelper)) {
			importParameters.put(
				"siteExternalReferenceCode", group.getExternalReferenceCode());
			importParameters.put("siteId", group.getGroupId());
		}

		return importParameters;
	}

	private static void _addDateFilterStrings(
		ChangesetEntryLocalService changesetEntryLocalService,
		ClassNameLocalService classNameLocalService,
		ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor
			exportImportDescriptor,
		List<String> filterStrings, PortletDataContext portletDataContext) {

		if (Objects.equals(
				exportImportDescriptor.getModelClassName(),
				Layout.class.getName())) {

			return;
		}

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext) &&
			!ExportImportThreadLocal.isInitialLayoutStagingInProcess()) {

			String lastPublishDateFilterString =
				_getLastPublishDateFilterString(
					changesetEntryLocalService, classNameLocalService,
					exportImportDescriptor.getModelClassName(),
					portletDataContext);

			if (Validator.isNotNull(lastPublishDateFilterString)) {
				filterStrings.add(lastPublishDateFilterString);
			}

			return;
		}

		String dateRangeFilterString = _getDateRangeFilterString(
			portletDataContext);

		if (Validator.isNotNull(dateRangeFilterString)) {
			filterStrings.add(dateRangeFilterString);
		}
	}

	private static String _getDateRangeFilterString(
		PortletDataContext portletDataContext) {

		Date endDate = portletDataContext.getEndDate();
		Date startDate = portletDataContext.getStartDate();

		if ((endDate == null) && (startDate == null)) {
			return null;
		}

		List<String> filterStrings = new ArrayList<>();

		if (endDate != null) {
			filterStrings.add("dateModified le " + _format.format(endDate));
		}

		if (startDate != null) {
			filterStrings.add("dateModified ge " + _format.format(startDate));
		}

		return StringUtil.merge(filterStrings, " and ");
	}

	private static String _getLastPublishDateFilterString(
		ChangesetEntryLocalService changesetEntryLocalService,
		ClassNameLocalService classNameLocalService, String modelClassName,
		PortletDataContext portletDataContext) {

		Set<String> externalReferenceCodes = new HashSet<>();

		externalReferenceCodes.add("");

		long changesetCollectionId = MapUtil.getLong(
			portletDataContext.getParameterMap(), "changesetCollectionId");

		if (changesetCollectionId > 0) {
			List<ChangesetEntry> changesetEntries =
				changesetEntryLocalService.getChangesetEntries(
					changesetCollectionId,
					classNameLocalService.getClassNameId(modelClassName));

			for (ChangesetEntry changesetEntry : changesetEntries) {
				if (Validator.isBlank(
						changesetEntry.getClassExternalReferenceCode())) {

					continue;
				}

				externalReferenceCodes.add(
					changesetEntry.getClassExternalReferenceCode());
			}
		}

		return StringBundler.concat(
			"externalReferenceCode in (",
			com.liferay.portal.kernel.util.StringUtil.merge(
				TransformUtil.transform(
					externalReferenceCodes,
					layoutExternalReferenceCode ->
						"'" + layoutExternalReferenceCode + "'")),
			")");
	}

	private static boolean _isCompanyScoped(
		Group group, StagingGroupHelper stagingGroupHelper) {

		if ((group == null) || stagingGroupHelper.isCompanyGroup(group)) {
			return true;
		}

		return false;
	}

	private static final Format _format =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

}