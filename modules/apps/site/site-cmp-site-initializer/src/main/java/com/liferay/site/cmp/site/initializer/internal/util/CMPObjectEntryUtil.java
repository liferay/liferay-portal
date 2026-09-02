/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Guilherme Camacho
 */
public class CMPObjectEntryUtil {

	public static long[] getCMPProjectObjectEntryIds(
			long[] cmpTaskObjectEntryIds,
			FilterFactory<Predicate> filterFactory,
			GroupLocalService groupLocalService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntry objectEntry,
			ObjectEntryLocalService objectEntryLocalService)
		throws PortalException {

		return ArrayUtil.unique(
			ArrayUtil.append(
				_getLinkedObjectEntryIds(
					filterFactory, groupLocalService, "L_CMP_PROJECT_LINK",
					objectDefinitionLocalService, objectEntry,
					objectEntryLocalService,
					"r_cmpProjectToCMPProjectLinks_c_cmpProjectId"),
				TransformUtil.transformToLongArray(
					ListUtil.fromArray(cmpTaskObjectEntryIds),
					cmpTaskObjectEntryId -> {
						ObjectEntry cmpTaskObjectEntry =
							objectEntryLocalService.fetchObjectEntry(
								cmpTaskObjectEntryId);

						if (cmpTaskObjectEntry == null) {
							return null;
						}

						long cmpProjectObjectEntryId = MapUtil.getLong(
							cmpTaskObjectEntry.getValues(),
							"r_cmpProjectToCMPTasks_c_cmpProjectId");

						if (cmpProjectObjectEntryId == 0) {
							return null;
						}

						return cmpProjectObjectEntryId;
					})));
	}

	public static long[] getCMPTaskObjectEntryIds(
			FilterFactory<Predicate> filterFactory,
			GroupLocalService groupLocalService,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntry objectEntry,
			ObjectEntryLocalService objectEntryLocalService)
		throws PortalException {

		return _getLinkedObjectEntryIds(
			filterFactory, groupLocalService, "L_CMP_TASK_LINK",
			objectDefinitionLocalService, objectEntry, objectEntryLocalService,
			"r_cmpTaskToCMPTaskLinks_c_cmpTaskId");
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

	public static boolean isCMSAsset(
		ObjectEntry objectEntry,
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
			objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntry.getObjectEntryFolderId()),
			objectEntryFolderLocalService);

		if (rootObjectEntryFolder == null) {
			return false;
		}

		String externalReferenceCode =
			rootObjectEntryFolder.getExternalReferenceCode();

		if (StringUtil.equals(
				externalReferenceCode,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			StringUtil.equals(
				externalReferenceCode,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return true;
		}

		return false;
	}

	private static long[] _getLinkedObjectEntryIds(
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

	private static ObjectEntryFolder _getRootObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder,
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		if (objectEntryFolder == null) {
			return null;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			return objectEntryFolder;
		}

		return objectEntryFolderLocalService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

}