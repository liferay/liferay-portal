/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

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
				collectionReference, companyId, infoItemServiceRegistry);
		}

		return _getCollectionItemExternalReferenceJSONObject(
			collectionReference, companyId, scopeGroupId);
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

		ClassNameReference classNameReference = new ClassNameReference();

		classNameReference.setClassName(() -> key);

		return classNameReference;
	}

	private static JSONObject _getClassNameReferenceJSONObject(
		CollectionReference collectionReference, long companyId,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		if (infoItemServiceRegistry == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		ClassNameReference classNameReference =
			(ClassNameReference)collectionReference;

		if (Validator.isNull(classNameReference.getClassName())) {
			return JSONFactoryUtil.createJSONObject();
		}

		InfoCollectionProvider infoCollectionProvider =
			infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class,
				classNameReference.getClassName());

		if (infoCollectionProvider == null) {
			LogUtil.logOptionalReference(
				InfoCollectionProvider.class, classNameReference.getClassName(),
				companyId);

			return JSONUtil.put(
				"key", classNameReference.getClassName()
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
			CollectionReference collectionReference, long companyId,
			long scopeGroupId)
		throws Exception {

		CollectionItemExternalReference collectionItemExternalReference =
			(CollectionItemExternalReference)collectionReference;

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
				collectionItemExternalReference.getScope(), groupId)
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

	private static CollectionItemExternalReference
		_toCollectionItemExternalReference(
			AssetListEntry assetListEntry, long companyId,
			JSONObject jsonObject, long scopeGroupId) {

		CollectionItemExternalReference collectionItemExternalReference =
			new CollectionItemExternalReference();

		if (assetListEntry != null) {
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

		collectionItemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		collectionItemExternalReference.setScope(
			() -> ItemScopeUtil.getItemScope(
				companyId, jsonObject.getString("scopeExternalReferenceCode"),
				scopeGroupId));

		return collectionItemExternalReference;
	}

}