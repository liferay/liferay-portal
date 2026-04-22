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
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.jsoup.nodes.Element;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsAttributesUtil {

	public static final String ACTION_DOWNLOAD = "download";

	public static final String ACTION_IMPRESSION = "impression";

	public static final String ACTION_VIEW = "view";

	public static void addAnalyticsAttributes(
		JSONObject editableValueJSONObject, Element element,
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

			ElementAttributeBuilder.of(
				element
			).attr(
				"data-analytics-asset-action", ACTION_DOWNLOAD
			).attr(
				"data-analytics-asset-field",
				() -> configJSONObject.getString("fieldId")
			).attr(
				"data-analytics-asset-id",
				() -> configJSONObject.getString("classPK")
			).attr(
				"data-analytics-asset-subtype",
				() -> configJSONObject.getString("itemSubtype")
			).attr(
				"data-analytics-asset-title",
				() -> configJSONObject.getString("title")
			).attr(
				"data-analytics-asset-type",
				() -> _getAnalyticsAssetType(FileEntry.class.getName())
			);

			return;
		}

		_addAnalyticsAttributes(
			element, fragmentEntryProcessorContext,
			fragmentEntryProcessorHelper, infoDisplaysFieldValues,
			fragmentEntryProcessorHelper.getInfoItemFieldMapped(
				editableValueJSONObject, fragmentEntryProcessorContext),
			infoItemServiceRegistry);
	}

	private static void _addAnalyticsAttributes(
		Element element,
		FragmentEntryProcessorContext fragmentEntryProcessorContext,
		FragmentEntryProcessorHelper fragmentEntryProcessorHelper,
		Map<InfoItemReference, InfoItemFieldValues> infoDisplaysFieldValues,
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		if (infoItemFieldMapped == null) {
			return;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemFieldMapped.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier)) {

			return;
		}

		InfoItemFieldValues infoItemFieldValues = infoDisplaysFieldValues.get(
			infoItemFieldMapped.getInfoItemReference());

		ElementAttributeBuilder.of(
			element
		).attr(
			"data-analytics-asset-action",
			() -> _getAnalyticsAssetAction(
				infoItemFieldMapped, infoItemFieldValues)
		).attr(
			"data-analytics-external-reference-code",
			() -> _getAnalyticsExternalReferenceCode(
				infoItemFieldMapped, infoItemFieldValues,
				fragmentEntryProcessorContext.getLocale())
		).attr(
			"data-analytics-asset-categories",
			() -> _getAnalyticsAssetCategories(
				classPKInfoItemIdentifier, infoItemFieldMapped,
				fragmentEntryProcessorContext.getLocale())
		).attr(
			"data-analytics-asset-field", infoItemFieldMapped.getFieldName()
		).attr(
			"data-analytics-asset-id",
			String.valueOf(classPKInfoItemIdentifier.getClassPK())
		).attr(
			"data-analytics-asset-mime-type",
			() -> _getAnalyticsAssetMimeType(
				fragmentEntryProcessorHelper, infoItemFieldMapped,
				infoItemFieldValues, fragmentEntryProcessorContext.getLocale())
		).attr(
			"data-analytics-asset-subtype",
			() -> _getAnalyticsSubtype(
				infoItemFieldMapped, infoItemServiceRegistry)
		).attr(
			"data-analytics-asset-tags",
			() -> _getAnalyticsAssetTags(
				classPKInfoItemIdentifier, infoItemFieldMapped)
		).attr(
			"data-analytics-asset-title",
			() -> _getAnalyticsTitle(
				infoItemFieldValues, fragmentEntryProcessorContext.getLocale())
		).attr(
			"data-analytics-asset-type",
			() -> _getAnalyticsAssetType(infoItemFieldMapped.getClassName())
		).attr(
			"data-analytics-asset-vocabularies",
			() -> _getAnalyticsAssetVocabularies(
				classPKInfoItemIdentifier, infoItemFieldMapped,
				fragmentEntryProcessorContext.getLocale())
		).attr(
			"data-analytics-object-definition-name",
			() -> {
				Object object = infoItemFieldMapped.getObject();

				if (object instanceof ObjectEntry) {
					return _getAnalyticsObjectDefinitionName(
						infoItemServiceRegistry, (ObjectEntry)object);
				}

				return StringPool.BLANK;
			}
		);
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
				assetCategory -> JSONUtil.put(
					"id", assetCategory.getCategoryId()
				).put(
					"name", assetCategory.getTitle(locale)
				).put(
					"vocabularyId", assetCategory.getVocabularyId()
				),
				_log);

			return jsonArray.toString();
		}

		return StringPool.BLANK;
	}

	private static String _getAnalyticsAssetMimeType(
		FragmentEntryProcessorHelper fragmentEntryProcessorHelper,
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				infoItemFieldMapped.getFieldName());

		if (infoFieldValue == null) {
			return StringPool.BLANK;
		}

		Object value = infoFieldValue.getValue(locale);

		if (value instanceof WebImage webImage) {
			long fileEntryId = fragmentEntryProcessorHelper.getFileEntryId(
				webImage);

			try {
				FileEntry fileEntry = DLAppLocalServiceUtil.fetchFileEntry(
					fileEntryId);

				if (fileEntry == null) {
					return StringPool.BLANK;
				}

				return fileEntry.getMimeType();
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return StringPool.BLANK;
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

		return StringPool.BLANK;
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
			return StringPool.BLANK;
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
			return StringPool.BLANK;
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

	private static String _getAnalyticsExternalReferenceCode(
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		if (infoItemFieldMapped.getObject() instanceof
				ExternalReferenceCodeModel externalReferenceCodeModel) {

			return externalReferenceCodeModel.getExternalReferenceCode();
		}

		if (infoItemFieldValues == null) {
			return StringPool.BLANK;
		}

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("externalReferenceCode");

		if (infoFieldValue == null) {
			return StringPool.BLANK;
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
				return StringPool.BLANK;
			}

			Object infoItem = infoItemObjectProvider.getInfoItem(
				new ClassPKInfoItemIdentifier(
					objectEntry.getObjectDefinitionId()));

			if (infoItem == null) {
				return StringPool.BLANK;
			}

			ObjectDefinition objectDefinition = (ObjectDefinition)infoItem;

			return objectDefinition.getName();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	private static String _getAnalyticsSubtype(
		InfoItemFieldMapped infoItemFieldMapped,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		InfoItemObjectVariationProvider infoItemObjectVariationProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectVariationProvider.class,
				infoItemFieldMapped.getClassName());

		if (infoItemObjectVariationProvider == null) {
			return StringPool.BLANK;
		}

		return infoItemObjectVariationProvider.getInfoItemFormVariationKey(
			infoItemFieldMapped.getObject());
	}

	private static String _getAnalyticsTitle(
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		if (infoItemFieldValues == null) {
			return StringPool.BLANK;
		}

		InfoFieldValue<?> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("title");

		if (infoFieldValue == null) {
			return StringPool.BLANK;
		}

		return String.valueOf(infoFieldValue.getValue(locale));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsAttributesUtil.class);

	private static class ElementAttributeBuilder {

		public static ElementAttributeBuilder of(Element element) {
			return new ElementAttributeBuilder(element);
		}

		public ElementAttributeBuilder attr(String name, String value) {
			if (Validator.isNotNull(value)) {
				_element.attr(name, value);
			}

			return this;
		}

		public ElementAttributeBuilder attr(
			String name, Supplier<String> supplier) {

			String value = supplier.get();

			if (Validator.isNotNull(value)) {
				_element.attr(name, value);
			}

			return this;
		}

		private ElementAttributeBuilder(Element element) {
			_element = element;
		}

		private final Element _element;

	}

}