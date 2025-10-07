/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.system;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public abstract class BaseSystemObjectDefinitionManager
	implements SystemObjectDefinitionManager {

	@Override
	public void checkModelResourcePermission(
			long objectDefinitionId, PermissionChecker permissionChecker,
			long primaryKey, String actionId)
		throws PortalException {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			objectEntryService.getModelResourcePermission(objectDefinitionId);

		modelResourcePermission.check(permissionChecker, primaryKey, actionId);
	}

	@Override
	public Map<Locale, String> getLabelMap() {
		return createLabelMap(MapUtil.getString(getLabelKeys(), "label"));
	}

	@Override
	public String getModelClassName() {
		Class<?> modelClass = getModelClass();

		return modelClass.getName();
	}

	@Override
	public String getName() {
		Table table = getTable();

		String tableName = table.getName();

		if (tableName.endsWith("_")) {
			return tableName.substring(0, tableName.length() - 1);
		}

		return tableName;
	}

	@Override
	public Map<Locale, String> getPluralLabelMap() {
		return createLabelMap(MapUtil.getString(getLabelKeys(), "pluralLabel"));
	}

	@Override
	public String getRESTDTOIdPropertyName() {
		return "id";
	}

	@Override
	public String getTitleObjectFieldName() {
		return "id";
	}

	@Override
	public long upsertBaseModel(
			String externalReferenceCode, long companyId, User user,
			Map<String, Object> values)
		throws Exception {

		BaseModel<?> baseModel = fetchBaseModelByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (baseModel != null) {
			long primaryKey = (long)baseModel.getPrimaryKeyObj();

			updateBaseModel(primaryKey, user, values);

			return primaryKey;
		}

		return addBaseModel(user, values);
	}

	protected Map<Locale, String> createLabelMap(String labelKey) {
		Map<Locale, String> labelMap = new HashMap<>();

		String defaultLabel = LanguageUtil.get(
			LocaleUtil.getDefault(), labelKey);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			labelMap.put(
				locale, LanguageUtil.get(locale, labelKey, defaultLabel));
		}

		return labelMap;
	}

	protected Map<String, String> getLanguageIdMap(
		String propertyName, Map<String, Object> values) {

		Object propertyValue = values.get(propertyName);

		if (propertyValue instanceof Map) {
			return (Map<String, String>)propertyValue;
		}
		else if (propertyValue instanceof String) {
			return LocalizedMapUtil.getLanguageIdMap(
				LocalizedMapUtil.getLocalizedMap(
					GetterUtil.getString(propertyValue)));
		}

		return null;
	}

	protected void setExtendedProperties(
			String className, Object entity, User user,
			Map<String, Object> values)
		throws Exception {

		List<ExtensionProvider> extensionProviders =
			extensionProviderRegistry.getExtensionProviders(
				user.getCompanyId(), className);

		if (ListUtil.isEmpty(extensionProviders)) {
			return;
		}

		for (ExtensionProvider extensionProvider : extensionProviders) {
			Map<String, PropertyDefinition> propertyDefinitions =
				extensionProvider.getExtendedPropertyDefinitions(
					user.getCompanyId(), className);

			Map<String, Serializable> extendedProperties = new HashMap<>();

			for (Map.Entry<String, Object> entry : values.entrySet()) {
				String propertyKey = entry.getKey();

				if (propertyDefinitions.containsKey(propertyKey)) {
					extendedProperties.put(
						propertyKey, (Serializable)entry.getValue());
				}
			}

			if (MapUtil.isNotEmpty(extendedProperties)) {
				extensionProvider.setExtendedProperties(
					user.getCompanyId(), user.getUserId(), className, entity,
					extendedProperties);
			}
		}
	}

	@Reference
	protected ExtensionProviderRegistry extensionProviderRegistry;

	@Reference
	protected ObjectEntryService objectEntryService;

}