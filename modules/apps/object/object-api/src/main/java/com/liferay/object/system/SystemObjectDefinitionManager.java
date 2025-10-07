/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.system;

import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public interface SystemObjectDefinitionManager {

	public long addBaseModel(User user, Map<String, Object> values)
		throws Exception;

	public void checkModelResourcePermission(
			long objectDefinitionId, PermissionChecker permissionChecker,
			long primaryKey, String actionId)
		throws PortalException;

	public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
		throws PortalException;

	public BaseModel<?> fetchBaseModelByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	public default Set<String> getAllowedObjectRelationshipTypes() {
		return ObjectRelationshipUtil.getDefaultObjectRelationshipTypes();
	}

	public BaseModel<?> getBaseModelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	public String getBaseModelExternalReferenceCode(long primaryKey)
		throws PortalException;

	public String getExternalReferenceCode();

	public JaxRsApplicationDescriptor getJaxRsApplicationDescriptor();

	public default Map<String, String> getLabelKeys() {
		return Collections.emptyMap();
	}

	public Map<Locale, String> getLabelMap();

	public default Table getLocalizationTable() {
		return null;
	}

	public Class<?> getModelClass();

	public String getModelClassName();

	public String getName();

	public default List<ObjectAction> getObjectActions() {
		return new ArrayList<>();
	}

	public List<ObjectField> getObjectFields();

	public default BaseModel<?> getOrAddEmptyBaseModel(
			String externalReferenceCode, User user)
		throws PortalException {

		return getBaseModelByExternalReferenceCode(
			externalReferenceCode, user.getCompanyId());
	}

	public default Page<?> getPage(
			User user, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return null;
	}

	public Map<Locale, String> getPluralLabelMap();

	public Column<?, Long> getPrimaryKeyColumn();

	public String getRESTDTOIdPropertyName();

	public String getScope();

	public Table getTable();

	public String getTitleObjectFieldName();

	public default Map<String, Object> getVariables(
		String contentType, ObjectDefinition objectDefinition,
		boolean oldValues, JSONObject payloadJSONObject) {

		Class<?> modelClass = getModelClass();

		Object object = payloadJSONObject.get(
			"model" + modelClass.getSimpleName());

		if (oldValues) {
			object = payloadJSONObject.get(
				"original" + modelClass.getSimpleName());
		}

		if (object == null) {
			object = payloadJSONObject.get(
				StringUtil.lowerCaseFirstLetter(objectDefinition.getName()));
		}

		if (object == null) {
			return null;
		}

		Map<String, Object> variables = new HashMap<>();

		if (object instanceof JSONObject) {
			Map<String, Object> map = ObjectMapperUtil.readValue(
				Map.class, object);

			Map<String, Object> jsonObjectMap = (Map<String, Object>)map.get(
				"_jsonObject");

			variables.putAll((Map<String, Object>)jsonObjectMap.get("map"));
		}
		else if (object instanceof Map) {
			variables.putAll((Map<String, Object>)object);
		}

		Map<String, Object> map = (Map<String, Object>)payloadJSONObject.get(
			"modelDTO" + contentType);

		if (oldValues) {
			map = (Map<String, Object>)payloadJSONObject.get(
				"originalDTO" + contentType);
		}

		if (map != null) {
			variables.putAll(map);
		}

		Map<String, Object> extendedProperties =
			(Map<String, Object>)payloadJSONObject.get("extendedProperties");

		if (oldValues) {
			extendedProperties = (Map<String, Object>)payloadJSONObject.get(
				"originalExtendedProperties");
		}

		if (extendedProperties != null) {
			variables.putAll(extendedProperties);
		}

		variables.computeIfAbsent("id", id -> payloadJSONObject.get("classPK"));

		return variables;
	}

	public int getVersion();

	public default boolean isEnableLocalization() {
		return false;
	}

	public void updateBaseModel(
			long primaryKey, User user, Map<String, Object> values)
		throws Exception;

	public long upsertBaseModel(
			String externalReferenceCode, long companyId, User user,
			Map<String, Object> values)
		throws Exception;

}