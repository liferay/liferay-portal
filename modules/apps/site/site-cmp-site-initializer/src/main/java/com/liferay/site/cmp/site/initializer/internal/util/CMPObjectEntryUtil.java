/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Guilherme Camacho
 */
public class CMPObjectEntryUtil {

	public static long[] getLinkedObjectEntryIds(
			FilterFactory<Predicate> filterFactory,
			GroupLocalService groupLocalService,
			String objectDefinitionExternalReferenceCode,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntry objectEntry,
			ObjectEntryLocalService objectEntryLocalService,
			String relationshipObjectFieldName)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			getObjectEntryIds(
				filterFactory, groupLocalService,
				objectDefinitionExternalReferenceCode,
				objectDefinitionLocalService, objectEntry,
				objectEntryLocalService),
			objectEntryId -> MapUtil.getLong(
				objectEntryLocalService.getValues(objectEntryId),
				relationshipObjectFieldName));
	}

	public static List<Long> getObjectEntryIds(
			FilterFactory<Predicate> filterFactory,
			GroupLocalService groupLocalService,
			String objectDefinitionExternalReferenceCode,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntry objectEntry,
			ObjectEntryLocalService objectEntryLocalService)
		throws PortalException {

		Group group = groupLocalService.fetchGroup(objectEntry.getGroupId());

		if (group == null) {
			return Collections.emptyList();
		}

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					objectEntry.getCompanyId());

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		return objectEntryLocalService.getPrimaryKeys(
			new Long[0], objectEntry.getCompanyId(), 0,
			objectDefinition.getObjectDefinitionId(),
			filterFactory.create(
				StringBundler.concat(
					"className eq '", objectEntry.getModelClassName(),
					"' and classExternalReferenceCode eq '",
					objectEntry.getExternalReferenceCode(),
					"' and groupExternalReferenceCode eq '",
					group.getExternalReferenceCode(), "'"),
				objectDefinition),
			false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

}