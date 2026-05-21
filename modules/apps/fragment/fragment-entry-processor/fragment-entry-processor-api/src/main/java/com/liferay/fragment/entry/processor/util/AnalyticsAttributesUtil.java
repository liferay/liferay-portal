/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectVariationProvider;
import com.liferay.info.type.WebImage;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsAttributesUtil {

	public static final String ACTION_DOWNLOAD = "download";

	public static final String ACTION_IMPRESSION = "impression";

	public static final String ACTION_VIEW = "view";

	public static Map<String, Object> getAnalyticsAttributes(
		JSONObject editableValueJSONObject,
		FragmentEntryProcessorContext fragmentEntryProcessorContext,
		FragmentEntryProcessorHelper fragmentEntryProcessorHelper,
		Map<InfoItemReference, InfoItemFieldValues> infoDisplaysFieldValues,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		JSONObject configJSONObject = editableValueJSONObject.getJSONObject(
			"config");

		if ((configJSONObject != null) &&
			StringUtil.equals(
				configJSONObject.getString("fieldId"),
				"FileEntry_downloadURL")) {

			return HashMapBuilder.<String, Object>put(
				"analytics-asset-action", ACTION_DOWNLOAD
			).put(
				"analytics-asset-field",
				() -> configJSONObject.getString("fieldId")
			).put(
				"analytics-asset-id",
				() -> configJSONObject.getString("classPK")
			).put(
				"analytics-asset-subtype",
				() -> configJSONObject.getString("itemSubtype")
			).put(
				"analytics-asset-title",
				() -> configJSONObject.getString("title")
			).put(
				"analytics-asset-type",
				_getAnalyticsAssetType(FileEntry.class.getName())
			).build();
		}

		InfoItemFieldMapped infoItemFieldMapped =
			fragmentEntryProcessorHelper.getInfoItemFieldMapped(
				editableValueJSONObject, fragmentEntryProcessorContext);

		if (infoItemFieldMapped == null) {
			return Collections.emptyMap();
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemFieldMapped.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier)) {

			return Collections.emptyMap();
		}

		InfoItemFieldValues infoItemFieldValues = infoDisplaysFieldValues.get(
			infoItemFieldMapped.getInfoItemReference());

		return HashMapBuilder.<String, Object>put(
			"analytics-asset-action",
			_getAnalyticsAssetAction(infoItemFieldMapped, infoItemFieldValues)
		).put(
			"analytics-asset-field", infoItemFieldMapped.getFieldName()
		).put(
			"analytics-asset-mime-type",
			_getAnalyticsAssetMimeType(
				fragmentEntryProcessorHelper, infoItemFieldMapped,
				infoItemFieldValues, fragmentEntryProcessorContext.getLocale())
		).putAll(
			_getAnalyticsAttributes(
				classPKInfoItemIdentifier, fragmentEntryProcessorContext,
				infoItemFieldMapped, infoItemFieldValues,
				infoItemServiceRegistry,
				fragmentEntryProcessorContext.getLocale())
		).build();
	}

	private static String _getAnalyticsAssetAction(
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues) {

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				infoItemFieldMapped.getFieldName());

		if (infoFieldValue == null) {
			return ACTION_IMPRESSION;
		}

		InfoField<?> infoField = infoFieldValue.getInfoField();

		if (Objects.equals(
				infoField.getInfoFieldType(), HTMLInfoFieldType.INSTANCE) ||
			Objects.equals(
				infoField.getInfoFieldType(), LongTextInfoFieldType.INSTANCE)) {

			return ACTION_VIEW;
		}

		return ACTION_IMPRESSION;
	}

	private static String _getAnalyticsAssetCategories(
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier,
		InfoItemFieldMapped infoItemFieldMapped, Locale locale) {

		List<AssetCategory> assetCategories =
			AssetCategoryLocalServiceUtil.getCategories(
				infoItemFieldMapped.getClassName(),
				classPKInfoItemIdentifier.getClassPK());

		if (ListUtil.isNotEmpty(assetCategories)) {
			JSONArray jsonArray = JSONUtil.toJSONArray(
				assetCategories,
				assetCategory -> {
					AssetVocabulary assetVocabulary =
						AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(
							assetCategory.getVocabularyId());

					String vocabularyName = null;

					if (assetVocabulary != null) {
						vocabularyName = assetVocabulary.getTitle(locale);
					}

					return JSONUtil.put(
						"id", assetCategory.getCategoryId()
					).put(
						"name", assetCategory.getTitle(locale)
					).put(
						"vocabularyId", assetCategory.getVocabularyId()
					).put(
						"vocabularyName", vocabularyName
					);
				},
				_log);

			return jsonArray.toString();
		}

		return null;
	}

	private static String _getAnalyticsAssetMimeType(
		FragmentEntryProcessorHelper fragmentEntryProcessorHelper,
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				infoItemFieldMapped.getFieldName());

		if (infoFieldValue == null) {
			return null;
		}

		Object value = infoFieldValue.getValue(locale);

		if (value instanceof WebImage webImage) {
			long fileEntryId = fragmentEntryProcessorHelper.getFileEntryId(
				webImage);

			try {
				FileEntry fileEntry = DLAppLocalServiceUtil.fetchFileEntry(
					fileEntryId);

				if (fileEntry == null) {
					return null;
				}

				return fileEntry.getMimeType();
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return null;
	}

	private static String _getAnalyticsAssetTags(
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier,
		InfoItemFieldMapped infoItemFieldMapped) {

		List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(
			infoItemFieldMapped.getClassName(),
			classPKInfoItemIdentifier.getClassPK());

		if (ListUtil.isNotEmpty(assetTags)) {
			JSONArray jsonArray = JSONUtil.toJSONArray(
				assetTags,
				assetTag -> JSONUtil.put(
					"id", assetTag.getTagId()
				).put(
					"name", assetTag.getName()
				),
				_log);

			return jsonArray.toString();
		}

		return null;
	}

	private static String _getAnalyticsAssetType(String className) {
		if (className.startsWith(ObjectDefinition.class.getName())) {
			return "object-entry";
		}

		return className;
	}

	private static String _getAnalyticsAssetVocabularies(
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier,
		InfoItemFieldMapped infoItemFieldMapped, Locale locale) {

		List<AssetCategory> assetCategories =
			AssetCategoryLocalServiceUtil.getCategories(
				infoItemFieldMapped.getClassName(),
				classPKInfoItemIdentifier.getClassPK());

		if (ListUtil.isEmpty(assetCategories)) {
			return null;
		}

		Set<Long> vocabularyIds = new LinkedHashSet<>();

		for (AssetCategory assetCategory : assetCategories) {
			vocabularyIds.add(assetCategory.getVocabularyId());
		}

		List<AssetVocabulary> assetVocabularies = new ArrayList<>(
			vocabularyIds.size());

		for (Long vocabularyId : vocabularyIds) {
			AssetVocabulary assetVocabulary =
				AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(
					vocabularyId);

			if (assetVocabulary != null) {
				assetVocabularies.add(assetVocabulary);
			}
		}

		if (ListUtil.isEmpty(assetVocabularies)) {
			return null;
		}

		JSONArray jsonArray = JSONUtil.toJSONArray(
			assetVocabularies,
			assetVocabulary -> JSONUtil.put(
				"id", assetVocabulary.getVocabularyId()
			).put(
				"name", assetVocabulary.getTitle(locale)
			),
			_log);

		return jsonArray.toString();
	}

	private static Map<String, Object> _getAnalyticsAttributes(
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier,
		FragmentEntryProcessorContext fragmentEntryProcessorContext,
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues,
		InfoItemServiceRegistry infoItemServiceRegistry, Locale locale) {

		HttpServletRequest httpServletRequest =
			fragmentEntryProcessorContext.getHttpServletRequest();

		if (httpServletRequest == null) {
			return _getAnalyticsAttributes(
				classPKInfoItemIdentifier, infoItemFieldMapped,
				infoItemFieldValues, infoItemServiceRegistry, locale);
		}

		Map<InfoItemReference, Map<String, Object>>
			assetAnalyticsAttributesMap =
				(Map<InfoItemReference, Map<String, Object>>)
					httpServletRequest.getAttribute(_ANALYTICS_ATTRIBUTES_MAP);

		if (assetAnalyticsAttributesMap == null) {
			assetAnalyticsAttributesMap = new HashMap<>();

			httpServletRequest.setAttribute(
				_ANALYTICS_ATTRIBUTES_MAP, assetAnalyticsAttributesMap);
		}

		return assetAnalyticsAttributesMap.computeIfAbsent(
			infoItemFieldMapped.getInfoItemReference(),
			key -> _getAnalyticsAttributes(
				classPKInfoItemIdentifier, infoItemFieldMapped,
				infoItemFieldValues, infoItemServiceRegistry, locale));
	}

	private static Map<String, Object> _getAnalyticsAttributes(
		ClassPKInfoItemIdentifier classPKInfoItemIdentifier,
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues,
		InfoItemServiceRegistry infoItemServiceRegistry, Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"analytics-asset-categories",
			() -> _getAnalyticsAssetCategories(
				classPKInfoItemIdentifier, infoItemFieldMapped, locale)
		).put(
			"analytics-asset-id",
			String.valueOf(classPKInfoItemIdentifier.getClassPK())
		).put(
			"analytics-asset-subtype",
			() -> _getAnalyticsSubtype(
				infoItemFieldMapped, infoItemServiceRegistry)
		).put(
			"analytics-asset-tags",
			() -> _getAnalyticsAssetTags(
				classPKInfoItemIdentifier, infoItemFieldMapped)
		).put(
			"analytics-asset-title",
			() -> _getAnalyticsTitle(infoItemFieldValues, locale)
		).put(
			"analytics-asset-type",
			_getAnalyticsAssetType(infoItemFieldMapped.getClassName())
		).put(
			"analytics-asset-vocabularies",
			() -> _getAnalyticsAssetVocabularies(
				classPKInfoItemIdentifier, infoItemFieldMapped, locale)
		).put(
			"analytics-external-reference-code",
			() -> _getAnalyticsExternalReferenceCode(
				infoItemFieldMapped, infoItemFieldValues, locale)
		).put(
			"analytics-object-definition-name",
			() -> {
				if (infoItemFieldMapped.getObject() instanceof
						ObjectEntry objectEntry) {

					return _getAnalyticsObjectDefinitionName(
						infoItemServiceRegistry, objectEntry);
				}

				return null;
			}
		).put(
			"analytics-object-type",
			() -> {
				if (infoItemFieldMapped.getObject() instanceof
						ObjectEntry objectEntry) {

					return _getAnalyticsObjectType(
						infoItemServiceRegistry, objectEntry);
				}

				return null;
			}
		).build();
	}

	private static String _getAnalyticsExternalReferenceCode(
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		if (infoItemFieldMapped.getObject() instanceof
				ExternalReferenceCodeModel externalReferenceCodeModel) {

			return externalReferenceCodeModel.getExternalReferenceCode();
		}

		if (infoItemFieldValues == null) {
			return null;
		}

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("externalReferenceCode");

		if (infoFieldValue == null) {
			return null;
		}

		return String.valueOf(infoFieldValue.getValue(locale));
	}

	private static String _getAnalyticsObjectDefinitionName(
		InfoItemServiceRegistry infoItemServiceRegistry,
		ObjectEntry objectEntry) {

		try {
			InfoItemObjectProvider<ObjectDefinition> infoItemObjectProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					ObjectDefinition.class.getName());

			if (infoItemObjectProvider == null) {
				return null;
			}

			ObjectDefinition objectDefinition =
				infoItemObjectProvider.getInfoItem(
					new ClassPKInfoItemIdentifier(
						objectEntry.getObjectDefinitionId()));

			if (objectDefinition == null) {
				return null;
			}

			return objectDefinition.getName();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static String _getAnalyticsObjectType(
		InfoItemServiceRegistry infoItemServiceRegistry,
		ObjectEntry objectEntry) {

		try {
			InfoItemObjectProvider<ObjectDefinition> infoItemObjectProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					ObjectDefinition.class.getName());

			if (infoItemObjectProvider == null) {
				return null;
			}

			ObjectDefinition objectDefinition =
				infoItemObjectProvider.getInfoItem(
					new ClassPKInfoItemIdentifier(
						objectEntry.getObjectDefinitionId()));

			if (objectDefinition == null) {
				return null;
			}

			String objectFolderExternalReferenceCode =
				objectDefinition.getObjectFolderExternalReferenceCode();

			if (Objects.equals(
					objectFolderExternalReferenceCode,
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES)) {

				return "content";
			}

			if (Objects.equals(
					objectFolderExternalReferenceCode,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

				return "file";
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static String _getAnalyticsSubtype(
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		InfoItemObjectVariationProvider infoItemObjectVariationProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectVariationProvider.class,
				infoItemFieldMapped.getClassName());

		if (infoItemObjectVariationProvider == null) {
			return null;
		}

		return infoItemObjectVariationProvider.getInfoItemFormVariationKey(
			infoItemFieldMapped.getObject());
	}

	private static String _getAnalyticsTitle(
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		if (infoItemFieldValues == null) {
			return null;
		}

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("title");

		if (infoFieldValue == null) {
			return null;
		}

		return String.valueOf(infoFieldValue.getValue(locale));
	}

	private static final String _ANALYTICS_ATTRIBUTES_MAP =
		"ANALYTICS_ATTRIBUTES_MAP";

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsAttributesUtil.class);

}