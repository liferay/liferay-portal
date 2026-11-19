/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.collection.provider;

import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;

import java.util.Collections;
import java.util.List;

/**
 * @author Carolina Barbosa
 */
public class OneToOneObjectRelationshipRelatedInfoCollectionProvider
	extends BaseObjectRelationshipRelatedInfoCollectionProvider {

	public OneToOneObjectRelationshipRelatedInfoCollectionProvider(
		Language language, ObjectDefinition objectDefinition1,
		ObjectDefinition objectDefinition2,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationship objectRelationship) {

		super(
			language, objectDefinition1, objectDefinition2,
			objectEntryLocalService, objectRelationship);
	}

	@Override
	protected InfoPage<ObjectEntry> getCollectionInfoPage(
			long groupId, Pagination pagination, long primaryKey)
		throws PortalException {

		List<ObjectEntry> relatedObjectEntries =
			objectEntryLocalService.getRelatedObjectEntries(
				groupId, objectRelationship.getObjectRelationshipId(), null,
				false, primaryKey, true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		if (relatedObjectEntries.size() > 1) {
			throw new PortalException(
				"More than one object entry is related through the one to " +
					"one object relationship " +
						objectRelationship.getObjectRelationshipId());
		}

		if (pagination.getStart() > 0) {
			return InfoPage.of(
				Collections.emptyList(), pagination,
				relatedObjectEntries.size());
		}

		return InfoPage.of(
			relatedObjectEntries, pagination, relatedObjectEntries.size());
	}

}