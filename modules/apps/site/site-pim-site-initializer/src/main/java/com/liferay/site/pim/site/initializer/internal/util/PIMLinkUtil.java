/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class PIMLinkUtil {

	public static void checkPermission(ObjectEntry objectEntry, String actionId)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectEntry.getModelClassName());

		modelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), objectEntry,
			actionId);
	}

	public static ObjectEntry fetchPIMLinkObjectEntry(
			long companyId, FilterFactory<Predicate> filterFactory,
			long groupId, String sourceClassExternalReferenceCode,
			String sourceClassName, String type)
		throws PortalException {

		ObjectDefinition objectDefinition = getPIMLinkObjectDefinition(
			companyId);

		Map<String, Serializable> values = _fetchValues(
			companyId, filterFactory, groupId, objectDefinition,
			sourceClassExternalReferenceCode, sourceClassName, type);

		if (MapUtil.isEmpty(values)) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(
			MapUtil.getLong(values, objectDefinition.getPKObjectFieldName()));
	}

	public static String getClusterKey(
			FilterFactory<Predicate> filterFactory, ObjectEntry objectEntry,
			String type)
		throws PortalException {

		Map<String, Serializable> values = _fetchValues(
			objectEntry.getCompanyId(), filterFactory, objectEntry.getGroupId(),
			getPIMLinkObjectDefinition(objectEntry.getCompanyId()),
			objectEntry.getExternalReferenceCode(),
			objectEntry.getModelClassName(), type);

		if (MapUtil.isEmpty(values)) {
			return null;
		}

		return MapUtil.getString(values, "clusterKey");
	}

	public static ObjectDefinition getPIMLinkObjectDefinition(long companyId) {
		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
				companyId);
	}

	public static List<Map<String, Serializable>> getValuesList(
			long companyId, FilterFactory<Predicate> filterFactory,
			String filterString, long groupId)
		throws PortalException {

		ObjectDefinition objectDefinition = getPIMLinkObjectDefinition(
			companyId);

		return ObjectEntryLocalServiceUtil.getValuesList(
			groupId, companyId, 0, objectDefinition.getObjectDefinitionId(),
			filterFactory.create(filterString, objectDefinition), null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public static List<Map<String, Serializable>>
			getValuesListByClassExternalReferenceCode(
				String classExternalReferenceCode, String className,
				long companyId, FilterFactory<Predicate> filterFactory,
				long groupId)
		throws PortalException {

		ObjectDefinition objectDefinition = getPIMLinkObjectDefinition(
			companyId);

		return ObjectEntryLocalServiceUtil.getValuesList(
			groupId, companyId, 0, objectDefinition.getObjectDefinitionId(),
			filterFactory.create(
				StringBundler.concat(
					"(sourceClassExternalReferenceCode eq '",
					classExternalReferenceCode, "' and sourceClassName eq '",
					className, "') or (",
					"targetClassExternalReferenceCode eq '",
					classExternalReferenceCode, "' and targetClassName eq '",
					className, "')"),
				objectDefinition),
			null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private static Map<String, Serializable> _fetchValues(
			long companyId, FilterFactory<Predicate> filterFactory,
			long groupId, ObjectDefinition objectDefinition,
			String sourceClassExternalReferenceCode, String sourceClassName,
			String type)
		throws PortalException {

		List<Map<String, Serializable>> valuesList =
			ObjectEntryLocalServiceUtil.getValuesList(
				groupId, companyId, 0, objectDefinition.getObjectDefinitionId(),
				filterFactory.create(
					StringBundler.concat(
						"sourceClassExternalReferenceCode eq '",
						sourceClassExternalReferenceCode,
						"' and sourceClassName eq '", sourceClassName,
						"' and type eq '", type, "'"),
					objectDefinition),
				null, 0, 1, null);

		if (valuesList.isEmpty()) {
			return null;
		}

		return valuesList.get(0);
	}

}