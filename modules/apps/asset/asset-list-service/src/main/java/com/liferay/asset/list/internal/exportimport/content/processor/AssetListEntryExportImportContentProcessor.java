/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.exportimport.content.processor;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.list.constants.AssetListConstants;
import com.liferay.asset.list.internal.util.AssetListFiltersUpgradeUtil;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.model.adapter.StagedGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntry",
	service = ExportImportContentProcessor.class
)
public class AssetListEntryExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content, boolean exportReferencedContent,
			boolean escapeContent)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			content
		).build();

		long[] groupIds = GetterUtil.getLongValues(
			StringUtil.split(unicodeProperties.getProperty("groupIds", null)));

		addGroupMappingsElement(portletDataContext, groupIds);

		String[] classNames = TransformUtil.transform(
			StringUtil.split(
				unicodeProperties.getProperty("classNameIds", null)),
			classNameId -> _fetchClassName(
				GetterUtil.getLong(classNameId), stagedModel),
			String.class);

		unicodeProperties.setProperty(
			"classNames", StringUtil.merge(classNames, ","));

		unicodeProperties.remove("anyAssetTypeClassName");

		long defaultClassNameId = GetterUtil.getLong(
			unicodeProperties.getProperty("anyAssetType", null));

		if (defaultClassNameId > 0) {
			String defaultClassName = _fetchClassName(
				defaultClassNameId, stagedModel);

			if (defaultClassName != null) {
				unicodeProperties.setProperty(
					"anyAssetTypeClassName", defaultClassName);
			}
		}

		List<AssetRendererFactory<?>> assetRendererFactories =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				portletDataContext.getCompanyId());

		for (AssetRendererFactory<?> assetRendererFactory :
				assetRendererFactories) {

			Class<? extends AssetRendererFactory<?>> clazz =
				_assetRendererFactoryClassProvider.getClass(
					assetRendererFactory);

			long[] classTypeIds = GetterUtil.getLongValues(
				StringUtil.split(
					unicodeProperties.getProperty(
						"classTypeIds" + clazz.getSimpleName())));

			if (ArrayUtil.isEmpty(classTypeIds)) {
				continue;
			}

			for (long classTypeId : classTypeIds) {
				DDMStructure ddmStructure =
					_ddmStructureLocalService.fetchStructure(classTypeId);

				if (ddmStructure != null) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, stagedModel, ddmStructure,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY);

					continue;
				}

				DLFileEntryType dlFileEntryType =
					_dlFileEntryTypeLocalService.fetchFileEntryType(
						classTypeId);

				if (dlFileEntryType != null) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, stagedModel, dlFileEntryType,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
				}
			}
		}

		for (JSONObject assetCategoryValueJSONObject :
				_getAssetCategoryValuesJSONObjects(
					_toFiltersJSONArray(
						unicodeProperties.getProperty("filters")))) {

			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					assetCategoryValueJSONObject.getLong("value"));

			if (assetCategory == null) {
				continue;
			}

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, assetCategory,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}

		return unicodeProperties.toString();
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			GetterUtil.getString(
				AssetListFiltersUpgradeUtil.toUpgradedTypeSettings(content),
				content)
		).build();

		Element rootElement = portletDataContext.getImportDataRootElement();

		Element groupIdMappingsElement = rootElement.element(
			"group-id-mappings");

		StagedModelDataHandler<StagedGroup> stagedModelDataHandler =
			(StagedModelDataHandler<StagedGroup>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					StagedGroup.class.getName());

		for (Element groupIdMappingElement :
				groupIdMappingsElement.elements("group-id-mapping")) {

			stagedModelDataHandler.importMissingReference(
				portletDataContext, groupIdMappingElement);
		}

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		unicodeProperties.put(
			"groupIds",
			StringUtil.merge(
				TransformUtil.transformToLongArray(
					Arrays.asList(
						StringUtil.split(
							unicodeProperties.getProperty("groupIds", null))),
					oldGroupId -> {
						long groupId = GetterUtil.getLong(oldGroupId);

						Group group = _groupLocalService.fetchGroup(
							MapUtil.getLong(groupIds, groupId, groupId));

						if (group != null) {
							return group.getGroupId();
						}

						return null;
					})));

		String classNames = unicodeProperties.getProperty("classNames");

		if (Validator.isNotNull(classNames) ||
			!Objects.equals(
				unicodeProperties.getProperty("anyAssetType"),
				Boolean.FALSE.toString())) {

			unicodeProperties.setProperty(
				"classNameIds",
				StringUtil.merge(
					TransformUtil.transformToLongArray(
						Arrays.asList(StringUtil.split(classNames)),
						className -> _portal.getClassNameId(className))));
		}
		else if (Validator.isNotNull(
					unicodeProperties.getProperty("classNameIds"))) {

			unicodeProperties.setProperty(
				"classNameIds",
				String.valueOf(AssetListConstants.CLASS_NAME_ID_NONEXISTENT));
		}

		String anyAssetTypeClassName = unicodeProperties.getProperty(
			"anyAssetTypeClassName");

		if (Validator.isNotNull(anyAssetTypeClassName)) {
			unicodeProperties.setProperty(
				"anyAssetType",
				String.valueOf(_portal.getClassNameId(anyAssetTypeClassName)));
		}
		else {
			long defaultClassNameId = GetterUtil.getLong(
				unicodeProperties.getProperty("anyAssetType"));

			if (defaultClassNameId > 0) {
				unicodeProperties.setProperty(
					"anyAssetType",
					String.valueOf(
						AssetListConstants.CLASS_NAME_ID_NONEXISTENT));
			}
		}

		Map<Long, Long> ddmStructureIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DDMStructure.class);
		Map<Long, Long> dlFileEntryTypeIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFileEntryType.class);

		List<AssetRendererFactory<?>> assetRendererFactories =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				portletDataContext.getCompanyId());

		for (AssetRendererFactory<?> assetRendererFactory :
				assetRendererFactories) {

			Class<?> clazz = _assetRendererFactoryClassProvider.getClass(
				assetRendererFactory);

			long anyClassType = GetterUtil.getLong(
				unicodeProperties.getProperty(
					"anyClassType" + clazz.getSimpleName()));

			if (anyClassType != 0L) {
				unicodeProperties.setProperty(
					"anyClassType" + clazz.getSimpleName(),
					String.valueOf(
						_getClassTypeId(
							anyClassType, ddmStructureIds,
							dlFileEntryTypeIds)));
			}

			String[] classTypeIds = StringUtil.split(
				unicodeProperties.getProperty(
					"classTypeIds" + clazz.getSimpleName()));

			if (ArrayUtil.isEmpty(classTypeIds)) {
				continue;
			}

			unicodeProperties.setProperty(
				"classTypeIds" + clazz.getSimpleName(),
				StringUtil.merge(
					TransformUtil.transformToLongArray(
						Arrays.asList(classTypeIds),
						classTypeId -> _getClassTypeId(
							GetterUtil.getLong(classTypeId), ddmStructureIds,
							dlFileEntryTypeIds))));
		}

		JSONArray filtersJSONArray = _toFiltersJSONArray(
			unicodeProperties.getProperty("filters"));

		Map<Long, Long> assetCategoryIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetCategory.class);

		for (JSONObject assetCategoryValueJSONObject :
				_getAssetCategoryValuesJSONObjects(filtersJSONArray)) {

			long assetCategoryId = assetCategoryValueJSONObject.getLong(
				"value");

			if (assetCategoryId <= 0) {
				continue;
			}

			assetCategoryValueJSONObject.put(
				"value",
				String.valueOf(
					MapUtil.getLong(
						assetCategoryIds, assetCategoryId, assetCategoryId)));
		}

		if (filtersJSONArray.length() > 0) {
			unicodeProperties.setProperty(
				"filters", filtersJSONArray.toString());
		}

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (StringUtil.startsWith(key, "orderByColumn") &&
				StringUtil.startsWith(value, "ddm__keyword__")) {

				String[] parts = StringUtil.split(
					value, StringPool.DOUBLE_UNDERLINE);

				if (parts.length < 4) {
					continue;
				}

				Long oldPrimaryKey = Long.valueOf(parts[2]);

				parts[2] = String.valueOf(
					ddmStructureIds.getOrDefault(oldPrimaryKey, oldPrimaryKey));

				unicodeProperties.setProperty(
					key, StringUtil.merge(parts, StringPool.DOUBLE_UNDERLINE));
			}
		}

		return unicodeProperties.toString();
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {
	}

	protected void addGroupMappingsElement(
		PortletDataContext portletDataContext, long[] groupIds) {

		Element rootElement = portletDataContext.getExportDataRootElement();

		Element groupIdMappingsElement = rootElement.addElement(
			"group-id-mappings");

		for (long groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				continue;
			}

			Element groupIdMappingElement = groupIdMappingsElement.addElement(
				"group-id-mapping");

			long liveGroupId = group.getLiveGroupId();

			if (group.isStagedRemotely()) {
				liveGroupId = group.getRemoteLiveGroupId();
			}

			groupIdMappingElement.addAttribute(
				"group-id", String.valueOf(groupId));
			groupIdMappingElement.addAttribute(
				"live-group-id", String.valueOf(liveGroupId));
			groupIdMappingElement.addAttribute(
				"group-key", group.getGroupKey());
		}
	}

	private String _fetchClassName(long classNameId, StagedModel stagedModel) {
		String className = _portal.fetchClassName(classNameId);

		if (Validator.isBlank(className)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Nonexistent class name ID ", classNameId,
						" referenced in type settings of staged model ",
						stagedModel.getModelClassName(), " ",
						stagedModel.getPrimaryKeyObj()));
			}

			return null;
		}

		return className;
	}

	private List<JSONObject> _getAssetCategoryValuesJSONObjects(
		JSONArray filtersJSONArray) {

		List<JSONObject> assetCategoryValuesJSONObjects = new ArrayList<>();

		for (int i = 0; i < filtersJSONArray.length(); i++) {
			JSONObject filterJSONObject = filtersJSONArray.getJSONObject(i);

			if ((filterJSONObject == null) ||
				!Objects.equals(
					filterJSONObject.getString("propertyName"),
					"assetCategories")) {

				continue;
			}

			JSONArray assetCategoryValueJSONArray =
				filterJSONObject.getJSONArray("value");

			if (assetCategoryValueJSONArray == null) {
				continue;
			}

			for (int j = 0; j < assetCategoryValueJSONArray.length(); j++) {
				JSONObject assetCategoryValueJSONObject =
					assetCategoryValueJSONArray.getJSONObject(j);

				if (assetCategoryValueJSONObject != null) {
					assetCategoryValuesJSONObjects.add(
						assetCategoryValueJSONObject);
				}
			}
		}

		return assetCategoryValuesJSONObjects;
	}

	private long _getClassTypeId(
		long classTypeId, Map<Long, Long>... primaryKeysMaps) {

		for (Map<Long, Long> primaryKeysMap : primaryKeysMaps) {
			long newClassTypeId = MapUtil.getLong(
				primaryKeysMap, classTypeId, classTypeId);

			if (newClassTypeId != classTypeId) {
				return newClassTypeId;
			}
		}

		return classTypeId;
	}

	private JSONArray _toFiltersJSONArray(String filtersJSON) {
		if (Validator.isNotNull(filtersJSON)) {
			try {
				return _jsonFactory.createJSONArray(filtersJSON);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return _jsonFactory.createJSONArray();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryExportImportContentProcessor.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference(unbind = "-")
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}