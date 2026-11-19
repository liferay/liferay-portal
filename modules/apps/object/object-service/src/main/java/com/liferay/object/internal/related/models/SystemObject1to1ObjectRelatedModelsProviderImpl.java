/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author Carolina Barbosa
 */
public class SystemObject1to1ObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		extends BaseSystemObjectRelatedModelsProviderImpl<T> {

	public SystemObject1to1ObjectRelatedModelsProviderImpl(
		InlineSQLHelper inlineSQLHelper, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		super(
			inlineSQLHelper, objectDefinition, objectDefinitionLocalService,
			objectEntryLocalService, objectFieldLocalService,
			objectRelationshipLocalService, systemObjectDefinitionManager,
			systemObjectDefinitionManagerRegistry);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_ONE;
	}

	@Override
	public List<T> getRelatedModels(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, String search, int start,
			int end, Sort[] sorts)
		throws PortalException {

		List<T> relatedModels = super.getRelatedModels(
			groupId, objectRelationshipId, predicate, preferApproved,
			primaryKey, search, QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		if (relatedModels.size() > 1) {
			throw new PortalException(
				"More than one model is related through the one to one " +
					"object relationship " + objectRelationshipId);
		}

		if (start > 0) {
			return Collections.emptyList();
		}

		return relatedModels;
	}

}