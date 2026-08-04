/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.sugarcrm.internal.rest.manager.v1_0;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.rest.manager.exception.ObjectEntryManagerHttpException;
import com.liferay.object.rest.manager.http.BaseObjectEntryManagerHttp;
import com.liferay.object.rest.manager.http.ObjectEntryManagerHttp;
import com.liferay.object.rest.manager.v1_0.BaseObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration;
import com.liferay.object.storage.sugarcrm.internal.web.cache.SugarCRMAccessTokenWebCacheItem;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Maurice Sepe
 */
@Component(
	property = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM,
	service = ObjectEntryManager.class
)
public class SugarCRMObjectEntryManagerImpl
	extends BaseObjectEntryManager implements ObjectEntryManager {

	@Override
	public ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		JSONObject responseJSONObject = _objectEntryManagerHttp.post(
			objectDefinition.getCompanyId(),
			getGroupId(objectDefinition, scopeKey),
			objectDefinition.getExternalReferenceCode(),
			toJSONObject(
				dtoConverterContext, objectDefinition, objectEntry,
				_getUnsafeTriConsumer(objectDefinition)));

		return toObjectEntry(
			objectDefinition.getCompanyId(), _getDateFormat(),
			_defaultObjectFieldNamesToJSONObjectKeys, dtoConverterContext,
			responseJSONObject, objectDefinition);
	}

	@Override
	public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.DELETE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		_objectEntryManagerHttp.delete(
			companyId, getGroupId(objectDefinition, scopeKey),
			objectDefinition.getExternalReferenceCode() +
				StringPool.FORWARD_SLASH + externalReferenceCode);
	}

	@Override
	public Page<ObjectEntry> getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			Aggregation aggregation, DTOConverterContext dtoConverterContext,
			String filterString, Pagination pagination, String search,
			Sort[] sorts)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		return _getObjectEntries(
			companyId, objectDefinition, scopeKey, dtoConverterContext,
			filterString, pagination, sorts);
	}

	@Override
	public ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.VIEW, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return toObjectEntry(
			companyId, _getDateFormat(),
			_defaultObjectFieldNamesToJSONObjectKeys, dtoConverterContext,
			_objectEntryManagerHttp.get(
				companyId, getGroupId(objectDefinition, scopeKey),
				StringBundler.concat(
					objectDefinition.getExternalReferenceCode(),
					StringPool.FORWARD_SLASH, externalReferenceCode)),
			objectDefinition);
	}

	@Override
	public String getStorageLabel(Locale locale) {
		return language.get(
			locale, ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM);
	}

	@Override
	public String getStorageType() {
		return ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM;
	}

	@Override
	public ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, String scopeKey)
		throws Exception {

		checkPortletResourcePermission(
			ActionKeys.UPDATE, objectDefinition, scopeKey,
			dtoConverterContext.getUser());

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		JSONObject responseJSONObject = _objectEntryManagerHttp.put(
			objectDefinition.getCompanyId(),
			getGroupId(objectDefinition, scopeKey),
			StringBundler.concat(
				objectDefinition.getExternalReferenceCode(),
				StringPool.FORWARD_SLASH, externalReferenceCode),
			toJSONObject(
				dtoConverterContext, objectDefinition, objectEntry,
				_getUnsafeTriConsumer(objectDefinition)));

		return toObjectEntry(
			objectDefinition.getCompanyId(), _getDateFormat(),
			_defaultObjectFieldNamesToJSONObjectKeys, dtoConverterContext,
			responseJSONObject, objectDefinition);
	}

	private void _appendFilter(
		StringBuilder sb, ObjectDefinition objectDefinition,
		String filterString) {

		if (Validator.isNull(filterString)) {
			return;
		}

		filterString = StringUtil.trim(filterString);

		sb.append("&filter");
		sb.append(StringPool.EQUAL);
		sb.append(StringPool.OPEN_BRACKET);
		sb.append(
			URLCodec.encodeURL(
				_filterFactory.create(filterString, objectDefinition)));
		sb.append(StringPool.CLOSE_BRACKET);
	}

	private void _appendPagination(StringBuilder sb, Pagination pagination) {
		sb.append("&max_num=");
		sb.append(pagination.getPageSize());
		sb.append("&offset=");
		sb.append((pagination.getPage() - 1) * pagination.getPageSize());
	}

	private void _appendSorts(
			StringBuilder sb, ObjectDefinition objectDefinition, Sort[] sorts)
		throws Exception {

		if (ArrayUtil.isEmpty(sorts)) {
			return;
		}

		sb.append("&order_by=");

		List<ObjectField> objectFields =
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		for (int i = 0; i < sorts.length; i++) {
			ObjectField objectField = fetchObjectFieldByName(
				sorts[i].getFieldName(), objectFields);

			if (objectField == null) {
				continue;
			}

			if (i > 0) {
				sb.append(",");
			}

			sb.append(objectField.getExternalReferenceCode());
			sb.append(":");

			if (sorts[i].isReverse()) {
				sb.append("DESC");
			}
			else {
				sb.append("ASC");
			}
		}
	}

	private DateFormat _getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	}

	private String _getLocation(
			ObjectDefinition objectDefinition, String filterString,
			Pagination pagination, Sort[] sorts, boolean count)
		throws Exception {

		StringBuilder sb = new StringBuilder();

		sb.append(objectDefinition.getExternalReferenceCode());

		if (count) {
			sb.append(StringPool.FORWARD_SLASH);
			sb.append("count");
		}

		sb.append(StringPool.QUESTION);

		_appendFilter(sb, objectDefinition, filterString);

		if (!count) {
			_appendPagination(sb, pagination);
			_appendSorts(sb, objectDefinition, sorts);
		}

		// TODO Add keyword search

		return sb.toString();
	}

	private Page<ObjectEntry> _getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			DTOConverterContext dtoConverterContext, String filterString,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		JSONObject responseJSONObject = _objectEntryManagerHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			_getLocation(
				objectDefinition, filterString, pagination, sorts, false));

		if ((responseJSONObject == null) ||
			(responseJSONObject.length() == 0)) {

			return Page.of(Collections.emptyList());
		}

		return Page.of(
			toObjectEntries(
				companyId, _getDateFormat(),
				_defaultObjectFieldNamesToJSONObjectKeys, dtoConverterContext,
				responseJSONObject.getJSONArray("records"), objectDefinition),
			pagination,
			_getTotalCount(
				companyId, objectDefinition, scopeKey, filterString, pagination,
				sorts));
	}

	private int _getTotalCount(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			String filterString, Pagination pagination, Sort[] sorts)
		throws Exception {

		JSONObject responseJSONObject = _objectEntryManagerHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			_getLocation(
				objectDefinition, filterString, pagination, sorts, true));

		return responseJSONObject.getInt("record_count", 0);
	}

	private UnsafeTriConsumer
		<Map<String, Object>, Object, ObjectField, Exception>
			_getUnsafeTriConsumer(ObjectDefinition objectDefinition) {

		return (map, value, objectField) -> {
			if (Objects.equals(
					objectField.getObjectFieldId(),
					objectDefinition.getTitleObjectFieldId())) {

				map.put("Name", value);
			}
		};
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<String, String> _defaultObjectFieldNamesToJSONObjectKeys =
		HashMapBuilder.put(
			"createDate", "date_entered"
		).put(
			"creator", "created_by"
		).put(
			"externalReferenceCode", "id"
		).put(
			"modifiedDate", "date_modified"
		).build();

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM + ")"
	)
	private FilterFactory<String> _filterFactory;

	private final ObjectEntryManagerHttp _objectEntryManagerHttp =
		new BaseObjectEntryManagerHttp() {

			@Override
			public JSONObject getAccessToken(long companyId, long groupId) {
				JSONObject jSONObject = SugarCRMAccessTokenWebCacheItem.get(
					_getSugarCRMConfiguration(companyId, groupId));

				if (jSONObject == null) {
					throw new ObjectEntryManagerHttpException(
						"Unable to authenticate with SugarCRM");
				}

				return jSONObject;
			}

			@Override
			public String getBaseURL(long companyId, long groupId) {
				SugarCRMConfiguration sugarCRMConfiguration =
					_getSugarCRMConfiguration(companyId, groupId);

				return sugarCRMConfiguration.baseURL();
			}

			private SugarCRMConfiguration _getSugarCRMConfiguration(
				long companyId, long groupId) {

				try {
					if (groupId == 0) {
						return _configurationProvider.getCompanyConfiguration(
							SugarCRMConfiguration.class, companyId);
					}

					return _configurationProvider.getGroupConfiguration(
						SugarCRMConfiguration.class, companyId, groupId);
				}
				catch (ConfigurationException configurationException) {
					return ReflectionUtil.throwException(
						configurationException);
				}
			}

		};

}