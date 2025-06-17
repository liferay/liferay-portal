/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.manager.v1_0;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.util.CreatorUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Guilherme Camacho
 */
public abstract class BaseObjectEntryManager {

	protected Map<String, String> addDeleteAction(
		ObjectDefinition objectDefinition, String scopeKey, User user) {

		if (!_hasPortletResourcePermission(
				objectDefinition, scopeKey, user, ActionKeys.DELETE)) {

			return null;
		}

		return Collections.emptyMap();
	}

	protected void checkPortletResourcePermission(
			String actionId, ObjectDefinition objectDefinition, String scopeKey,
			User user)
		throws Exception {

		PortletResourcePermission portletResourcePermission =
			getPortletResourcePermission(objectDefinition);

		portletResourcePermission.check(
			permissionCheckerFactory.create(user),
			getGroupId(objectDefinition, scopeKey), actionId);
	}

	protected ObjectField fetchObjectFieldByName(
		String name, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(name, objectField.getName())) {
				return objectField;
			}
		}

		return null;
	}

	protected long getGroupId(
		ObjectDefinition objectDefinition, String scopeKey) {

		return getGroupId(objectDefinition, scopeKey, false);
	}

	protected long getGroupId(
		ObjectDefinition objectDefinition, String scopeKey,
		boolean useCompanyGroup) {

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			return GetterUtil.getLong(
				GroupUtil.getGroupId(
					objectDefinition.getCompanyId(), scopeKey,
					groupLocalService));
		}

		if (useCompanyGroup) {
			try {
				Company company = companyLocalService.getCompany(
					objectDefinition.getCompanyId());

				return company.getGroupId();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return 0;
	}

	protected PortletResourcePermission getPortletResourcePermission(
		ObjectDefinition objectDefinition) {

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		return modelResourcePermission.getPortletResourcePermission();
	}

	protected JSONObject toJSONObject(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry,
			UnsafeTriConsumer
				<Map<String, Object>, Object, ObjectField, Exception>
					unsafeTriConsumer)
		throws Exception {

		Map<String, Object> map = new HashMap<>();

		List<ObjectField> objectFields =
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Map<String, Object> properties = objectEntry.getProperties();

		for (String key : properties.keySet()) {
			ObjectField objectField = fetchObjectFieldByName(key, objectFields);

			if (objectField == null) {
				continue;
			}

			ObjectFieldBusinessType objectFieldBusinessType =
				objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
					objectField.getBusinessType());

			Object value = objectFieldBusinessType.getValue(
				objectField, dtoConverterContext.getUserId(), properties);

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				StringBundler sb = new StringBundler();

				for (String listTypeEntryKey : (List<String>)value) {
					String listTypeEntryExternalReferenceCode =
						ListTypeEntryUtil.getListTypeEntryExternalReferenceCode(
							objectField.getListTypeDefinitionId(),
							listTypeEntryKey);

					if (Validator.isNull(listTypeEntryExternalReferenceCode)) {
						continue;
					}

					sb.append(listTypeEntryExternalReferenceCode);
					sb.append(StringPool.SEMICOLON);
				}

				if (sb.index() > 1) {
					sb.setIndex(sb.index() - 1);
				}

				value = sb.toString();
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				value = ListTypeEntryUtil.getListTypeEntryExternalReferenceCode(
					objectField.getListTypeDefinitionId(),
					GetterUtil.getString(value));
			}

			map.put(
				objectField.getExternalReferenceCode(),
				Objects.equals(value, StringPool.BLANK) ? null : value);

			unsafeTriConsumer.accept(map, value, objectField);
		}

		return jsonFactory.createJSONObject(jsonFactory.looseSerialize(map));
	}

	protected List<com.liferay.object.rest.dto.v1_0.ObjectEntry>
			toObjectEntries(
				long companyId, DateFormat dateFormat,
				Map<String, String> defaultObjectFieldNamesToJSONObjectKeys,
				DTOConverterContext dtoConverterContext, JSONArray jsonArray,
				ObjectDefinition objectDefinition)
		throws Exception {

		return JSONUtil.toList(
			jsonArray,
			jsonObject -> toObjectEntry(
				companyId, dateFormat, defaultObjectFieldNamesToJSONObjectKeys,
				dtoConverterContext, jsonObject, objectDefinition));
	}

	protected com.liferay.object.rest.dto.v1_0.ObjectEntry toObjectEntry(
			long companyId, DateFormat dateFormat,
			Map<String, String> defaultObjectFieldNamesToJSONObjectKeys,
			DTOConverterContext dtoConverterContext, JSONObject jsonObject,
			ObjectDefinition objectDefinition)
		throws Exception {

		return new com.liferay.object.rest.dto.v1_0.ObjectEntry() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						addDeleteAction(
							objectDefinition, getScopeKey(),
							dtoConverterContext.getUser())
					).build());
				setCreator(
					() -> CreatorUtil.toCreator(
						portal, null,
						userLocalService.fetchUserByExternalReferenceCode(
							jsonObject.getString(
								defaultObjectFieldNamesToJSONObjectKeys.get(
									"creator")),
							companyId)));
				setDateCreated(
					() -> dateFormat.parse(
						jsonObject.getString(
							defaultObjectFieldNamesToJSONObjectKeys.get(
								"createDate"))));
				setDateModified(
					() -> dateFormat.parse(
						jsonObject.getString(
							defaultObjectFieldNamesToJSONObjectKeys.get(
								"modifiedDate"))));
				setExternalReferenceCode(
					() -> jsonObject.getString(
						defaultObjectFieldNamesToJSONObjectKeys.get(
							"externalReferenceCode")));
				setProperties(
					() -> _toProperties(
						dtoConverterContext, jsonObject, objectDefinition,
						objectFieldLocalService.getObjectFields(
							objectDefinition.getObjectDefinitionId())));
				setStatus(
					() -> new Status() {
						{
							setCode(() -> 0);
							setLabel(() -> "approved");
							setLabel_i18n(() -> "Approved");
						}
					});
			}
		};
	}

	protected void validateReadOnlyObjectFields(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> values = new HashMap<>();

		if (externalReferenceCode != null) {
			ObjectEntry serviceBuilderObjectEntry =
				objectEntryLocalService.fetchObjectEntry(
					externalReferenceCode,
					objectDefinition.getObjectDefinitionId());

			if (serviceBuilderObjectEntry == null) {
				return;
			}

			values.putAll(
				objectEntryLocalService.getSystemValues(
					serviceBuilderObjectEntry));
			values.putAll(
				objectEntryLocalService.getValues(serviceBuilderObjectEntry));
		}

		ObjectFieldUtil.validateReadOnlyObjectFields(
			ddmExpressionFactory, values,
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId()),
			objectEntry.getProperties());
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference
	protected DDMExpressionFactory ddmExpressionFactory;

	@Reference
	protected DepotEntryLocalService depotEntryLocalService;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected ListTypeEntryLocalService listTypeEntryLocalService;

	@Reference
	protected ObjectEntryLocalService objectEntryLocalService;

	@Reference
	protected ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry;

	@Reference
	protected ObjectFieldLocalService objectFieldLocalService;

	@Reference
	protected ObjectScopeProviderRegistry objectScopeProviderRegistry;

	@Reference
	protected PermissionCheckerFactory permissionCheckerFactory;

	@Reference
	protected Portal portal;

	@Reference
	protected UserLocalService userLocalService;

	private ListEntry _fetchListEntry(
		DTOConverterContext dtoConverterContext, String externalReferenceCode,
		ObjectDefinition objectDefinition, ObjectField objectField) {

		ListTypeEntry listTypeEntry =
			listTypeEntryLocalService.fetchListTypeEntryByExternalReferenceCode(
				externalReferenceCode, objectDefinition.getCompanyId(),
				objectField.getListTypeDefinitionId());

		if (listTypeEntry == null) {
			return null;
		}

		return new ListEntry() {
			{
				setKey(listTypeEntry::getKey);
				setName(
					() -> listTypeEntry.getName(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						listTypeEntry.getNameMap()));
			}
		};
	}

	private ObjectField _fetchObjectFieldByExternalReferenceCode(
		String externalReferenceCode, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(
					externalReferenceCode,
					objectField.getExternalReferenceCode())) {

				return objectField;
			}
		}

		return null;
	}

	private boolean _hasPortletResourcePermission(
		ObjectDefinition objectDefinition, String scopeKey, User user,
		String actionId) {

		PortletResourcePermission portletResourcePermission =
			getPortletResourcePermission(objectDefinition);

		return portletResourcePermission.contains(
			permissionCheckerFactory.create(user),
			getGroupId(objectDefinition, scopeKey), actionId);
	}

	private Map<String, Object> _toProperties(
			DTOConverterContext dtoConverterContext, JSONObject jsonObject,
			ObjectDefinition objectDefinition, List<ObjectField> objectFields)
		throws Exception {

		Map<String, Object> properties = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			ObjectField objectField = _fetchObjectFieldByExternalReferenceCode(
				key, objectFields);

			if (objectField == null) {
				continue;
			}

			if (jsonObject.isNull(key)) {
				properties.put(objectField.getName(), null);

				continue;
			}

			Object value = jsonObject.get(key);

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

				String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";

				if (StringUtil.equals(
						ObjectFieldSettingUtil.getValue(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							objectField),
						ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC)) {

					pattern += "Z";
				}

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					pattern);

				value = simpleDateFormat.format(
					simpleDateFormat.parse(GetterUtil.getString(value)));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_INTEGER) ||
					 objectField.compareBusinessType(
						 ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER)) {

				if (value instanceof BigDecimal) {
					BigDecimal bigDecimalValue = (BigDecimal)value;

					value = bigDecimalValue.toBigInteger();
				}
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.
							BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				value = TransformUtil.transformToList(
					StringUtil.split(
						GetterUtil.getString(value), StringPool.SEMICOLON),
					listTypeEntryExternalReferenceCode -> _fetchListEntry(
						dtoConverterContext, listTypeEntryExternalReferenceCode,
						objectDefinition, objectField));
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				value = _fetchListEntry(
					dtoConverterContext, GetterUtil.getString(value),
					objectDefinition, objectField);
			}
			else if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

				properties.put(
					objectField.getName() + "RawText",
					HtmlParserUtil.extractText(GetterUtil.getString(value)));
			}

			properties.put(objectField.getName(), value);
		}

		return properties;
	}

}