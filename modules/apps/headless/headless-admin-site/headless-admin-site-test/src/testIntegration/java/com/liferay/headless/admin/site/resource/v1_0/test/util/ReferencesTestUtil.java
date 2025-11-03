/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class ReferencesTestUtil {

	public static CollectionReference getCollectionReference(
		Object object, long scopeGroupId) {

		if (object == null) {
			return null;
		}

		if (object instanceof String) {
			return new ClassNameReference() {
				{
					setClassName(() -> GetterUtil.getString(object));
					setCollectionType(() -> CollectionType.COLLECTION_PROVIDER);
				}
			};
		}

		if (object instanceof AssetListEntry) {
			AssetListEntry assetListEntry = (AssetListEntry)object;

			return new CollectionItemExternalReference() {
				{
					setCollectionType(() -> CollectionType.COLLECTION);
					setExternalReferenceCode(
						assetListEntry::getExternalReferenceCode);
					setScope(
						() -> ScopeTestUtil.getItemScope(
							assetListEntry.getGroupId(), scopeGroupId));
				}
			};
		}

		if (object instanceof Map) {
			Map<String, String> map = (Map<String, String>)object;

			return new CollectionItemExternalReference() {
				{
					setCollectionType(() -> CollectionType.COLLECTION);
					setExternalReferenceCode(
						() -> map.get("externalReferenceCode"));
					setScope(
						() -> new Scope() {
							{
								setExternalReferenceCode(
									map.get("scopeExternalReferenceCode"));
								setType(Type.SITE);
							}
						});
				}
			};
		}

		return null;
	}

	public static ItemExternalReference getItemExternalReference(
		Object object, long scopeGroupId) {

		if (object == null) {
			return null;
		}

		if (object instanceof AssetCategory) {
			AssetCategory assetCategory = (AssetCategory)object;

			return _getItemExternalReference(
				AssetCategory.class.getName(),
				assetCategory.getExternalReferenceCode(),
				assetCategory.getGroupId(), scopeGroupId);
		}

		if (object instanceof AssetVocabulary) {
			AssetVocabulary assetVocabulary = (AssetVocabulary)object;

			return _getItemExternalReference(
				AssetVocabulary.class.getName(),
				assetVocabulary.getExternalReferenceCode(),
				assetVocabulary.getGroupId(), scopeGroupId);
		}

		if (object instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry)object;

			return _getItemExternalReference(
				FileEntry.class.getName(), fileEntry.getExternalReferenceCode(),
				fileEntry.getGroupId(), scopeGroupId);
		}

		if (object instanceof JournalArticle) {
			JournalArticle journalArticle = (JournalArticle)object;

			return _getItemExternalReference(
				JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				journalArticle.getGroupId(), scopeGroupId);
		}

		if (object instanceof Map) {
			Map<String, String> map = (Map<String, String>)object;

			return _getItemExternalReference(
				map.get("className"), map.get("externalReferenceCode"),
				map.get("scopeExternalReferenceCode"));
		}

		if (object instanceof SiteNavigationMenu) {
			SiteNavigationMenu siteNavigationMenu = (SiteNavigationMenu)object;

			return _getItemExternalReference(
				SiteNavigationMenu.class.getName(),
				siteNavigationMenu.getExternalReferenceCode(),
				siteNavigationMenu.getGroupId(), scopeGroupId);
		}

		return null;
	}

	private static ItemExternalReference _getItemExternalReference(
		String className, String externalReferenceCode, long itemGroupId,
		long scopeGroupId) {

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(className);
		itemExternalReference.setExternalReferenceCode(externalReferenceCode);
		itemExternalReference.setScope(
			() -> ScopeTestUtil.getItemScope(itemGroupId, scopeGroupId));

		return itemExternalReference;
	}

	private static ItemExternalReference _getItemExternalReference(
		String className, String externalReferenceCode,
		String scopeExternalReferenceCode) {

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(className);
		itemExternalReference.setExternalReferenceCode(externalReferenceCode);
		itemExternalReference.setScope(
			() -> new Scope() {
				{
					setExternalReferenceCode(scopeExternalReferenceCode);
					setType(Type.SITE);
				}
			});

		return itemExternalReference;
	}

}