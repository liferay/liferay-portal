/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Fábio Alves
 */
public class AssetTagUtil {

	public static Set<String> getAssetTagNames(
		AssetTagLocalService assetTagLocalService,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		return SetUtil.fromList(
			TransformUtil.transform(
				assetTagLocalService.getTags(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId()),
				assetTag -> {
					if (!StringUtil.startsWith(
							assetTag.getName(),
							objectDefinition.getExternalReferenceCode())) {

						return null;
					}

					return assetTag.getName();
				}));
	}

	public static Set<String> getRelatedAssetTagNames(
			AssetTagLocalService assetTagLocalService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntry objectEntry,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationship objectRelationship)
		throws PortalException {

		Set<String> assetTagNames = new HashSet<>();

		if (objectRelationship == null) {
			return assetTagNames;
		}

		for (ObjectEntry relatedObjectEntry :
				objectEntryLocalService.getOneToManyObjectEntries(
					objectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(), null, false,
					objectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			assetTagNames.addAll(
				getAssetTagNames(
					assetTagLocalService,
					objectDefinitionLocalService.fetchObjectDefinition(
						relatedObjectEntry.getObjectDefinitionId()),
					relatedObjectEntry));
		}

		return assetTagNames;
	}

}