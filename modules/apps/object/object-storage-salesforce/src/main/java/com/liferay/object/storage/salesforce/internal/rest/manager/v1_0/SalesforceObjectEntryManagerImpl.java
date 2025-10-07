/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.rest.manager.v1_0;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntryModel;
import com.liferay.account.service.AccountEntryLocalService;
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
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.storage.salesforce.configuration.SalesforceConfiguration;
import com.liferay.object.storage.salesforce.internal.web.cache.SalesforceAccessTokenWebCacheItem;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
 * @author Guilherme Camacho
 */
@Component(
	property = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
	service = ObjectEntryManager.class
)
public class SalesforceObjectEntryManagerImpl
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
			"sobjects/" + objectDefinition.getExternalReferenceCode(),
			toJSONObject(
				dtoConverterContext, objectDefinition, objectEntry,
				_getUnsafeTriConsumer(objectDefinition)));

		return getObjectEntry(
			objectDefinition.getCompanyId(), dtoConverterContext,
			responseJSONObject.getString("id"), objectDefinition, scopeKey);
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
			StringBundler.concat(
				"sobjects/", objectDefinition.getExternalReferenceCode(), "/",
				externalReferenceCode));
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
			pagination, filterString, search, sorts);
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
					"sobjects/", objectDefinition.getExternalReferenceCode(),
					"/", externalReferenceCode)),
			objectDefinition);
	}

	@Override
	public String getStorageLabel(Locale locale) {
		return language.get(
			locale, ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE);
	}

	@Override
	public String getStorageType() {
		return ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE;
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

		_objectEntryManagerHttp.patch(
			companyId, getGroupId(objectDefinition, scopeKey),
			StringBundler.concat(
				"sobjects/", objectDefinition.getExternalReferenceCode(), "/",
				externalReferenceCode),
			toJSONObject(
				dtoConverterContext, objectDefinition, objectEntry,
				_getUnsafeTriConsumer(objectDefinition)));

		return getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			objectDefinition, scopeKey);
	}

	private String _getAccountRestrictionSOSQLString(
			long companyId, DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		if (!_inlineSQLHelper.isEnabled(
				companyId, getGroupId(objectDefinition, scopeKey)) ||
			!objectDefinition.isAccountEntryRestricted()) {

			return StringPool.BLANK;
		}

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getAccountEntryRestrictedObjectFieldId());

		return StringBundler.concat(
			objectField.getExternalReferenceCode(), " IN ('",
			StringUtil.merge(
				TransformUtil.transform(
					_accountEntryLocalService.getUserAccountEntries(
						dtoConverterContext.getUserId(),
						AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, null,
						AccountConstants.
							ACCOUNT_ENTRY_TYPES_DEFAULT_ALLOWED_TYPES,
						WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS),
					AccountEntryModel::getExternalReferenceCode),
				"', '"),
			"')");
	}

	private DateFormat _getDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	private String _getLocation(
		ObjectDefinition objectDefinition, Pagination pagination,
		String predicateString, String search, Sort[] sorts) {

		if (Validator.isNotNull(search)) {
			return HttpComponentsUtil.addParameter(
				"search", "q",
				StringBundler.concat(
					"FIND {`", search, "`} IN ALL FIELDS RETURNING ",
					objectDefinition.getExternalReferenceCode(), "(FIELDS(ALL)",
					predicateString,
					_getSorts(objectDefinition.getObjectDefinitionId(), sorts),
					_getSalesforcePagination(pagination), ")"));
		}

		return HttpComponentsUtil.addParameter(
			"query", "q",
			StringBundler.concat(
				"SELECT FIELDS(ALL) FROM ",
				objectDefinition.getExternalReferenceCode(), predicateString,
				_getSorts(objectDefinition.getObjectDefinitionId(), sorts),
				_getSalesforcePagination(pagination)));
	}

	private Page<ObjectEntry> _getObjectEntries(
			long companyId, ObjectDefinition objectDefinition, String scopeKey,
			DTOConverterContext dtoConverterContext, Pagination pagination,
			String filterString, String search, Sort[] sorts)
		throws Exception {

		JSONObject responseJSONObject = _objectEntryManagerHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			_getLocation(
				objectDefinition, pagination,
				_getSOSQLString(
					companyId, dtoConverterContext, objectDefinition,
					filterString, scopeKey),
				search, sorts));

		if ((responseJSONObject == null) ||
			(responseJSONObject.length() == 0)) {

			return Page.of(Collections.emptyList());
		}

		JSONArray jsonArray = Validator.isNotNull(search) ?
			responseJSONObject.getJSONArray("searchRecords") :
				responseJSONObject.getJSONArray("records");

		return Page.of(
			toObjectEntries(
				companyId, _getDateFormat(),
				_defaultObjectFieldNamesToJSONObjectKeys, dtoConverterContext,
				jsonArray, objectDefinition),
			pagination,
			_getTotalCount(
				companyId, objectDefinition,
				_getSOSQLString(
					companyId, dtoConverterContext, objectDefinition,
					filterString, scopeKey),
				scopeKey, search));
	}

	private String _getSalesforcePagination(Pagination pagination) {
		return StringBundler.concat(
			" LIMIT ", pagination.getPageSize(), " OFFSET ",
			pagination.getStartPosition());
	}

	private String _getSorts(long objectDefinitionId, Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(objectDefinitionId);

		for (Sort sort : sorts) {
			String fieldName = sort.getFieldName();

			if (fieldName.startsWith("nestedFieldArray.")) {
				String[] parts = StringUtil.split(
					sort.getFieldName(), StringPool.POUND);

				fieldName = parts[1];
			}

			if (Objects.equals(fieldName, "status")) {
				continue;
			}

			if (sb.length() == 0) {
				sb.append(" ORDER BY ");
			}
			else {
				sb.append(StringPool.COMMA_AND_SPACE);
			}

			String defaultFieldName =
				_defaultObjectFieldNamesToJSONObjectKeys.get(fieldName);

			if (defaultFieldName != null) {
				sb.append(defaultFieldName);
			}
			else {
				ObjectField objectField = fetchObjectFieldByName(
					fieldName, objectFields);

				if (objectField == null) {
					continue;
				}

				sb.append(objectField.getExternalReferenceCode());
			}

			if (sort.isReverse()) {
				sb.append(" DESC");
			}
		}

		return sb.toString();
	}

	private String _getSOSQLString(
			long companyId, DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, String filterString,
			String scopeKey)
		throws Exception {

		String accountRestrictionSOSQLString =
			_getAccountRestrictionSOSQLString(
				companyId, dtoConverterContext, objectDefinition, scopeKey);

		String filterSOSQLString = _filterFactory.create(
			filterString, objectDefinition);

		String sosqlString = StringPool.BLANK;

		if (Validator.isNull(accountRestrictionSOSQLString) &&
			Validator.isNotNull(filterSOSQLString)) {

			sosqlString = " WHERE " + filterSOSQLString;
		}
		else if (Validator.isNotNull(accountRestrictionSOSQLString) &&
				 Validator.isNull(filterSOSQLString)) {

			sosqlString = " WHERE " + accountRestrictionSOSQLString;
		}
		else if (Validator.isNotNull(accountRestrictionSOSQLString) &&
				 Validator.isNotNull(filterSOSQLString)) {

			sosqlString = StringBundler.concat(
				" WHERE (", filterSOSQLString, ") AND ",
				accountRestrictionSOSQLString);
		}

		return sosqlString;
	}

	private int _getTotalCount(
			long companyId, ObjectDefinition objectDefinition,
			String predicateString, String scopeKey, String search)
		throws Exception {

		if (Validator.isNotNull(search)) {
			JSONObject responseJSONObject = _objectEntryManagerHttp.get(
				companyId, getGroupId(objectDefinition, scopeKey),
				_getLocation(
					objectDefinition, Pagination.of(1, 200), predicateString,
					search, null));

			JSONArray jsonArray = responseJSONObject.getJSONArray(
				"searchRecords");

			return jsonArray.length();
		}

		JSONObject responseJSONObject = _objectEntryManagerHttp.get(
			companyId, getGroupId(objectDefinition, scopeKey),
			HttpComponentsUtil.addParameter(
				"query", "q",
				StringBundler.concat(
					"SELECT COUNT(Id) FROM ",
					objectDefinition.getExternalReferenceCode(),
					predicateString)));

		JSONArray jsonArray = responseJSONObject.getJSONArray("records");

		return jsonArray.getJSONObject(
			0
		).getInt(
			"expr0"
		);
	}

	private UnsafeTriConsumer
		<Map<String, Object>, Object, ObjectField, Exception>
			_getUnsafeTriConsumer(ObjectDefinition objectDefinition) {

		return (map, value, objectField) -> {
			if (StringUtil.endsWith(
					objectDefinition.getExternalReferenceCode(),
					_CUSTOM_OBJECT_SUFFIX) &&
				Objects.equals(
					objectField.getObjectFieldId(),
					objectDefinition.getTitleObjectFieldId())) {

				map.put("Name", value);
			}
		};
	}

	private static final String _CUSTOM_OBJECT_SUFFIX = "__c";

	private static final Log _log = LogFactoryUtil.getLog(
		SalesforceObjectEntryManagerImpl.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<String, String> _defaultObjectFieldNamesToJSONObjectKeys =
		HashMapBuilder.put(
			"createDate", "CreatedDate"
		).put(
			"creator", "OwnerId"
		).put(
			"externalReferenceCode", "Id"
		).put(
			"id", "Id"
		).put(
			"modifiedDate", "LastModifiedDate"
		).put(
			"userName", "OwnerId"
		).build();

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE + ")"
	)
	private FilterFactory<String> _filterFactory;

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	private final ObjectEntryManagerHttp _objectEntryManagerHttp =
		new BaseObjectEntryManagerHttp() {

			@Override
			public JSONObject getAccessToken(long companyId, long groupId) {
				int retry = 0;

				while (retry < 3) {
					JSONObject jSONObject =
						SalesforceAccessTokenWebCacheItem.get(
							_getSalesforceConfiguration(companyId, groupId));

					if (jSONObject != null) {
						return jSONObject;
					}

					try {
						Thread.sleep(500);
					}
					catch (InterruptedException interruptedException) {
						if (_log.isDebugEnabled()) {
							_log.debug(interruptedException);
						}
					}

					retry++;
				}

				throw new ObjectEntryManagerHttpException(
					"Unable to authenticate with Salesforce");
			}

			@Override
			public String getBaseURL(long companyId, long groupId) {
				JSONObject jsonObject = getAccessToken(companyId, groupId);

				return jsonObject.getString("instance_url") +
					"/services/data/v54.0";
			}

			private SalesforceConfiguration _getSalesforceConfiguration(
				long companyId, long groupId) {

				try {
					if (groupId == 0) {
						return _configurationProvider.getCompanyConfiguration(
							SalesforceConfiguration.class, companyId);
					}

					return _configurationProvider.getGroupConfiguration(
						SalesforceConfiguration.class, groupId);
				}
				catch (ConfigurationException configurationException) {
					return ReflectionUtil.throwException(
						configurationException);
				}
			}

		};

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

}