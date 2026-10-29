/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.headless.admin.site.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.RepeatableFieldsCollectionProviderReference;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.InfoItemServiceRegistryUtil;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionSettingLocalServiceUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lourdes Fernández Besada
 */
public class CollectionUtil {

	public static JSONObject getCollectionJSONObject(
			CollectionReference collectionReference, long companyId,
			InfoItemServiceRegistry infoItemServiceRegistry, long scopeGroupId)
		throws Exception {

		if (collectionReference == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		if (collectionReference instanceof ClassNameReference) {
			return _getClassNameReferenceJSONObject(
				(ClassNameReference)collectionReference, companyId,
				infoItemServiceRegistry);
		}
		else if (collectionReference instanceof
					CollectionItemExternalReference) {

			return _getCollectionItemExternalReferenceJSONObject(
				(CollectionItemExternalReference)collectionReference, companyId,
				scopeGroupId);
		}

		return _getRepeatableFieldsCollectionProviderReferenceJSONObject(
			companyId,
			(RepeatableFieldsCollectionProviderReference)collectionReference,
			scopeGroupId);
	}

	public static CollectionReference getCollectionReference(
		long companyId, JSONObject jsonObject, long scopeGroupId) {

		if (jsonObject == null) {
			return null;
		}

		String type = jsonObject.getString("type");

		if (Validator.isNull(type)) {
			return null;
		}

		if (Objects.equals(
				type, InfoListItemSelectorReturnType.class.getName())) {

			return _toCollectionItemExternalReference(
				AssetListEntryLocalServiceUtil.fetchAssetListEntry(
					jsonObject.getLong("classPK")),
				companyId, jsonObject, scopeGroupId);
		}

		String key = jsonObject.getString("key", null);

		if (Validator.isNull(key)) {
			return null;
		}

		if (Objects.equals(
				key,
				RepeatableFieldInfoItemCollectionProvider.class.getName())) {

			return _toRepeatableFieldsCollectionProviderReference(
				jsonObject, scopeGroupId);
		}

		ClassNameReference classNameReference = new ClassNameReference();

		classNameReference.setClassName(() -> key);
		classNameReference.setCollectionType(
			() -> CollectionReference.CollectionType.COLLECTION_PROVIDER);

		return classNameReference;
	}

	private static String _getClassName(String className, long companyId) {
		if (ExportImportThreadLocal.isImportInProcess()) {
			Matcher matcher = _objectDefinitionClassNamePattern.matcher(
				className);

			if (matcher.find()) {
				ObjectDefinition objectDefinition = _getObjectDefinition(
					companyId, matcher.group(0));

				if (objectDefinition != null) {
					className = matcher.replaceFirst(
						Matcher.quoteReplacement(
							objectDefinition.getClassName()));
				}
			}
		}

		return className;
	}

	private static JSONObject _getClassNameReferenceJSONObject(
		ClassNameReference classNameReference, long companyId,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		if (infoItemServiceRegistry == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		String className = _getClassName(
			classNameReference.getClassName(), companyId);

		if (Validator.isNull(className)) {
			return JSONFactoryUtil.createJSONObject();
		}

		InfoCollectionProvider infoCollectionProvider =
			_getInfoCollectionProvider(className, infoItemServiceRegistry);

		if (infoCollectionProvider == null) {
			LogUtil.logOptionalReference(
				InfoCollectionProvider.class, className, companyId);

			return JSONUtil.put(
				"key", className
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			);
		}

		return JSONUtil.put(
			"itemSubtype",
			() -> {
				if (!(infoCollectionProvider instanceof
						SingleFormVariationInfoCollectionProvider)) {

					return null;
				}

				SingleFormVariationInfoCollectionProvider<?>
					singleFormVariationInfoCollectionProvider =
						(SingleFormVariationInfoCollectionProvider<?>)
							infoCollectionProvider;

				return singleFormVariationInfoCollectionProvider.
					getFormVariationKey();
			}
		).put(
			"itemType", infoCollectionProvider.getCollectionItemClassName()
		).put(
			"key", infoCollectionProvider.getKey()
		).put(
			"title",
			() -> infoCollectionProvider.getLabel(LocaleUtil.getDefault())
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private static JSONObject _getCollectionItemExternalReferenceJSONObject(
			CollectionItemExternalReference collectionItemExternalReference,
			long companyId, long scopeGroupId)
		throws Exception {

		if (Validator.isNull(
				collectionItemExternalReference.getExternalReferenceCode())) {

			return JSONFactoryUtil.createJSONObject();
		}

		Long groupId = ItemScopeUtil.getItemGroupId(
			companyId, collectionItemExternalReference.getScope(),
			scopeGroupId);

		if (groupId == null) {
			return _getCollectionItemExternalReferenceMissingReferenceJSONObject(
				collectionItemExternalReference, scopeGroupId);
		}

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.
				fetchAssetListEntryByExternalReferenceCode(
					collectionItemExternalReference.getExternalReferenceCode(),
					groupId);

		if (assetListEntry == null) {
			return _getCollectionItemExternalReferenceMissingReferenceJSONObject(
				collectionItemExternalReference, scopeGroupId);
		}

		return JSONUtil.put(
			"classNameId",
			String.valueOf(PortalUtil.getClassNameId(AssetListEntry.class))
		).put(
			"classPK", assetListEntry.getAssetListEntryId()
		).put(
			"externalReferenceCode",
			collectionItemExternalReference.getExternalReferenceCode()
		).put(
			"itemSubtype", assetListEntry.getAssetEntrySubtype()
		).put(
			"itemType", assetListEntry.getAssetEntryType()
		).put(
			"scopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				collectionItemExternalReference.getScope(), scopeGroupId)
		).put(
			"title", assetListEntry.getTitle()
		).put(
			"type", InfoListItemSelectorReturnType.class.getName()
		);
	}

	private static JSONObject
			_getCollectionItemExternalReferenceMissingReferenceJSONObject(
				CollectionItemExternalReference collectionItemExternalReference,
				long groupId)
		throws Exception {

		LogUtil.logOptionalReference(
			AssetListEntry.class.getName(),
			collectionItemExternalReference.getExternalReferenceCode(),
			collectionItemExternalReference.getScope(), groupId);

		return JSONUtil.put(
			"externalReferenceCode",
			collectionItemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				collectionItemExternalReference.getScope(), groupId)
		).put(
			"type", InfoListItemSelectorReturnType.class.getName()
		);
	}

	private static String _getFormVariationKey(
		String className, ItemExternalReference itemExternalReference,
		long scopeGroupId) {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			InfoItemServiceRegistryUtil.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		if (infoItemFormVariationsProvider == null) {
			LogUtil.logOptionalReference(
				className, itemExternalReference.getExternalReferenceCode(),
				itemExternalReference.getScope(), scopeGroupId);

			return null;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.
				getInfoItemFormVariationByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(),
					scopeGroupId);

		if (infoItemFormVariation == null) {
			LogUtil.logOptionalReference(
				infoItemFormVariationsProvider.
					getInfoItemFormVariationClassName(),
				itemExternalReference.getExternalReferenceCode(),
				itemExternalReference.getScope(), scopeGroupId);

			return null;
		}

		return infoItemFormVariation.getKey();
	}

	private static InfoCollectionProvider _getInfoCollectionProvider(
		String className, InfoItemServiceRegistry infoItemServiceRegistry) {

		InfoCollectionProvider infoCollectionProvider =
			infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, className);

		if (infoCollectionProvider == null) {
			infoCollectionProvider = infoItemServiceRegistry.getInfoItemService(
				RelatedInfoItemCollectionProvider.class, className);
		}

		return infoCollectionProvider;
	}

	private static ObjectDefinition _getObjectDefinition(
		long companyId, String objectDefinitionSettingValue) {

		ObjectDefinitionSetting objectDefinitionSetting =
			ObjectDefinitionSettingLocalServiceUtil.
				fetchObjectDefinitionSetting(
					companyId,
					ObjectDefinitionSettingConstants.NAME_OLD_CLASS_NAME,
					objectDefinitionSettingValue);

		if (objectDefinitionSetting == null) {
			return null;
		}

		return ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
			objectDefinitionSetting.getObjectDefinitionId());
	}

	private static JSONObject
		_getRepeatableFieldsCollectionProviderReferenceJSONObject(
			long companyId,
			RepeatableFieldsCollectionProviderReference
				repeatableFieldsCollectionProviderReference,
			long scopeGroupId) {

		String className = _getClassName(
			repeatableFieldsCollectionProviderReference.getClassName(),
			companyId);

		if (Validator.isNull(className)) {
			return JSONFactoryUtil.createJSONObject();
		}

		ItemExternalReference subTypeItemExternalReference =
			repeatableFieldsCollectionProviderReference.
				getSubTypeExternalReference();

		if ((subTypeItemExternalReference == null) ||
			(subTypeItemExternalReference.getExternalReferenceCode() == null)) {

			return JSONFactoryUtil.createJSONObject();
		}

		return JSONUtil.put(
			"fieldName",
			repeatableFieldsCollectionProviderReference.getFieldName()
		).put(
			"itemSubtypeKey",
			SubtypeUtil.getClassTypeKey(
				className, scopeGroupId, subTypeItemExternalReference)
		).put(
			"itemType", className
		).put(
			"key", RepeatableFieldInfoItemCollectionProvider.class.getName()
		).put(
			"title",
			_getTitle(
				className, companyId,
				repeatableFieldsCollectionProviderReference.getFieldName(),
				subTypeItemExternalReference, scopeGroupId)
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private static String _getTitle(
		String className, long companyId, String fieldName,
		ItemExternalReference itemExternalReference, long scopeGroupId) {

		RepeatableFieldsInfoItemFormProvider<?>
			repeatableFieldsInfoItemFormProvider =
				InfoItemServiceRegistryUtil.getFirstInfoItemService(
					RepeatableFieldsInfoItemFormProvider.class, className);

		if (repeatableFieldsInfoItemFormProvider == null) {
			LogUtil.logOptionalReference(
				RepeatableFieldsInfoItemFormProvider.class, className,
				companyId);

			return null;
		}

		try {
			String formVariationKey = _getFormVariationKey(
				className, itemExternalReference, scopeGroupId);

			InfoForm infoForm =
				repeatableFieldsInfoItemFormProvider.
					getRepeatableFieldsInfoForm(formVariationKey);

			if (infoForm == null) {
				LogUtil.logOptionalReference(
					InfoForm.class, formVariationKey, companyId);

				return null;
			}

			InfoField infoField = infoForm.getInfoField(fieldName);

			if (infoField == null) {
				LogUtil.logOptionalReference(
					InfoField.class, fieldName, companyId);

				return null;
			}

			if (infoField.isRepeatable()) {
				return infoField.getLabel(LocaleUtil.getDefault());
			}
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			LogUtil.logOptionalReference(
				InfoForm.class,
				itemExternalReference.getExternalReferenceCode(), companyId);

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}
		}

		return null;
	}

	private static CollectionItemExternalReference
		_toCollectionItemExternalReference(
			AssetListEntry assetListEntry, long companyId,
			JSONObject jsonObject, long scopeGroupId) {

		CollectionItemExternalReference collectionItemExternalReference =
			new CollectionItemExternalReference();

		if (assetListEntry != null) {
			collectionItemExternalReference.setCollectionType(
				() -> CollectionReference.CollectionType.COLLECTION);
			collectionItemExternalReference.setExternalReferenceCode(
				assetListEntry::getExternalReferenceCode);
			collectionItemExternalReference.setScope(
				() -> ItemScopeUtil.getItemScope(
					assetListEntry.getGroupId(), scopeGroupId));

			return collectionItemExternalReference;
		}

		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		collectionItemExternalReference.setCollectionType(
			() -> CollectionReference.CollectionType.COLLECTION);
		collectionItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		collectionItemExternalReference.setScope(
			() -> ItemScopeUtil.getItemScope(
				companyId, jsonObject.getString("scopeExternalReferenceCode"),
				scopeGroupId));

		return collectionItemExternalReference;
	}

	private static RepeatableFieldsCollectionProviderReference
		_toRepeatableFieldsCollectionProviderReference(
			JSONObject jsonObject, long scopeGroupId) {

		RepeatableFieldsCollectionProviderReference
			repeatableFieldsCollectionProviderReference =
				new RepeatableFieldsCollectionProviderReference();

		repeatableFieldsCollectionProviderReference.setClassName(
			() -> jsonObject.getString("itemType"));
		repeatableFieldsCollectionProviderReference.setCollectionType(
			() ->
				CollectionReference.CollectionType.
					REPEATABLE_FIELDS_COLLECTION_PROVIDER);
		repeatableFieldsCollectionProviderReference.setFieldName(
			() -> jsonObject.getString("fieldName"));
		repeatableFieldsCollectionProviderReference.setSubTypeExternalReference(
			() -> SubtypeUtil.getSubtypeItemExternalReference(
				jsonObject.getString("itemType"),
				jsonObject.getLong("itemSubtype"),
				jsonObject.getString("itemSubtypeKey"), scopeGroupId));

		return repeatableFieldsCollectionProviderReference;
	}

	private static final Log _log = LogFactoryUtil.getLog(CollectionUtil.class);

	private static final Pattern _objectDefinitionClassNamePattern =
		Pattern.compile(
			"(com\\.liferay\\.object\\.model\\.ObjectDefinition#" +
				"[a-zA-Z]\\d[a-zA-Z]\\d)");

}