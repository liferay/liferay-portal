/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class CMSDefaultPermissionUtil {

	public static ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long companyId, long userId,
			String classExternalReferenceCode, String className,
			JSONObject defaultPermissionsJSONObject, long depotGroupId,
			String treePath)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", companyId);

		return ObjectEntryLocalServiceUtil.addOrUpdateObjectEntry(
			externalReferenceCode, 0, userId,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode", classExternalReferenceCode
			).put(
				"className", className
			).put(
				"defaultPermissions", defaultPermissionsJSONObject.toString()
			).put(
				"depotGroupId", depotGroupId
			).put(
				"treePath", treePath
			).build(),
			new ServiceContext());
	}

	public static ObjectEntry fetchObjectEntry(
			long companyId, long userId, String classExternalReferenceCode,
			String className, FilterFactory<Predicate> filterFactory)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", companyId);

		Predicate predicate = filterFactory.create(
			StringBundler.concat(
				"(classExternalReferenceCode eq '", classExternalReferenceCode,
				"') and (className eq '", className, "')"),
			objectDefinition);

		List<Long> primaryKeys = ObjectEntryLocalServiceUtil.getPrimaryKeys(
			new Long[0], companyId, userId,
			objectDefinition.getObjectDefinitionId(), predicate, false, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(primaryKeys)) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(primaryKeys.get(0));
	}

	public static JSONObject getJSONObject(
			long companyId, long userId, String classExternalReferenceCode,
			String className, FilterFactory<Predicate> filterFactory)
		throws PortalException {

		ObjectEntry objectEntry = fetchObjectEntry(
			companyId, userId, classExternalReferenceCode, className,
			filterFactory);

		if (objectEntry == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONFactoryUtil.createJSONObject(
			String.valueOf(values.getOrDefault("defaultPermissions", "{}")));
	}

}