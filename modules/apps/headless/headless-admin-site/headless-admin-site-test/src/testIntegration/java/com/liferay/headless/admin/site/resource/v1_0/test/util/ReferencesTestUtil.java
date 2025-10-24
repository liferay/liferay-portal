/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class ReferencesTestUtil {

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

		if (object instanceof Map) {
			Map<String, String> map = (Map<String, String>)object;

			return _getItemExternalReference(
				map.get("className"), map.get("externalReferenceCode"),
				map.get("scopeExternalReferenceCode"));
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