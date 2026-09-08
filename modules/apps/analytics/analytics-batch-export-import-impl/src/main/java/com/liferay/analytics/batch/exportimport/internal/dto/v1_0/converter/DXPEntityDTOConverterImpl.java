/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.ExpandoField;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.Field;
import com.liferay.analytics.dxp.entity.rest.dto.v1_0.converter.DXPEntityDTOConverter;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.configuration.AnalyticsConfigurationRegistry;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.io.Serializable;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity"
	},
	service = DTOConverter.class
)
public class DXPEntityDTOConverterImpl implements DXPEntityDTOConverter {

	@Override
	public String getContentType() {
		return DXPEntity.class.getSimpleName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DXPEntity toDTO(
			DTOConverterContext dtoConverterContext, BaseModel<?> baseModel)
		throws Exception {

		Map<String, Function<?, Object>> attributeGetterFunctions =
			(Map<String, Function<?, Object>>)
				baseModel.getAttributeGetterFunctions();

		Function<Object, Object> modifiedDateGetterFunction =
			(Function<Object, Object>)attributeGetterFunctions.get(
				"modifiedDate");

		try {
			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> _toDXPEntity(
					_getExpandoFields(baseModel), _getFields(baseModel),
					String.valueOf(baseModel.getPrimaryKeyObj()),
					(Date)modifiedDateGetterFunction.apply(baseModel),
					baseModel.getModelClassName()));
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}
	}

	@SuppressWarnings("unchecked")
	private void _addFieldAttributes(
		BaseModel<?> baseModel, List<Field> fields,
		List<String> includeAttributeNames) {

		Map<String, Function<?, Object>> attributeGetterFunctions =
			(Map<String, Function<?, Object>>)
				baseModel.getAttributeGetterFunctions();

		for (Map.Entry<String, Function<?, Object>> entry :
				attributeGetterFunctions.entrySet()) {

			if (ListUtil.isNotEmpty(includeAttributeNames) &&
				!includeAttributeNames.contains(entry.getKey())) {

				continue;
			}

			Function<Object, Object> function =
				(Function<Object, Object>)entry.getValue();

			if (function == null) {
				continue;
			}

			Field field = new Field() {
				{
					setName(entry::getKey);
					setValue(
						() -> {
							Object value = function.apply(baseModel);

							if (value instanceof Date) {
								Date date = (Date)value;

								return String.valueOf(date.getTime());
							}

							if (Validator.isNull(value)) {
								return StringPool.BLANK;
							}

							return String.valueOf(value);
						});
				}
			};

			fields.add(field);
		}
	}

	private List<String> _filterAttributeNames(
		List<String> attributeNames, List<String> removeAttributeNames) {

		return TransformUtil.transform(
			attributeNames,
			attributeName -> {
				if (removeAttributeNames.contains(attributeName)) {
					return null;
				}

				return attributeName;
			});
	}

	private Map<String, Serializable> _getAttributes(
		ExpandoBridge expandoBridge, List<String> includeAttributeNames) {

		Map<String, Serializable> newAttributes = new HashMap<>();

		Map<String, Serializable> attributes = expandoBridge.getAttributes(
			false);

		for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
			if (ListUtil.isNotEmpty(includeAttributeNames) &&
				!includeAttributeNames.contains(entry.getKey())) {

				continue;
			}

			String dataType = ExpandoColumnConstants.getDataType(
				expandoBridge.getAttributeType(entry.getKey()));

			if (Validator.isBlank(dataType)) {
				dataType = ExpandoColumnConstants.DATA_TYPE_TEXT;
			}

			newAttributes.put(
				entry.getKey() + "-" + dataType, entry.getValue());
		}

		return newAttributes;
	}

	private Field[] _getExpandoColumnFields(
		String className, String dataType, String displayType,
		ExpandoColumn expandoColumn) {

		List<Field> fields = new ArrayList<Field>() {
			{
				add(
					new Field() {
						{
							setName(() -> "className");
							setValue(() -> className);
						}
					});
				add(
					new Field() {
						{
							setName(() -> "columnId");
							setValue(
								() -> String.valueOf(
									expandoColumn.getColumnId()));
						}
					});
				add(
					new Field() {
						{
							setName(() -> "dataType");
							setValue(() -> dataType);
						}
					});
				add(
					new Field() {
						{
							setName(() -> "displayType");
							setValue(() -> displayType);
						}
					});
				add(
					new Field() {
						{
							setName(() -> "modifiedDate");
							setValue(
								() -> {
									Date modifiedDate =
										expandoColumn.getModifiedDate();

									return String.valueOf(
										modifiedDate.getTime());
								});
						}
					});
				add(
					new Field() {
						{
							setName(() -> "name");
							setValue(
								() -> expandoColumn.getName() + "-" + dataType);
						}
					});
			}
		};

		return fields.toArray(new Field[0]);
	}

	private ExpandoField[] _getExpandoFields(BaseModel<?> baseModel) {
		if (!StringUtil.equals(
				baseModel.getModelClassName(), Organization.class.getName()) &&
			!StringUtil.equals(
				baseModel.getModelClassName(), User.class.getName())) {

			return new ExpandoField[0];
		}

		List<ExpandoField> expandoFields = new ArrayList<>();

		List<String> includeAttributeNames = new ArrayList<>();

		ShardedModel shardedModel = (ShardedModel)baseModel;

		if (StringUtil.equals(
				baseModel.getModelClassName(), User.class.getName())) {

			AnalyticsConfiguration analyticsConfiguration =
				_analyticsConfigurationRegistry.getAnalyticsConfiguration(
					shardedModel.getCompanyId());

			includeAttributeNames = ListUtil.fromArray(
				analyticsConfiguration.syncedUserFieldNames());
		}

		Map<String, Serializable> attributes = _getAttributes(
			baseModel.getExpandoBridge(), includeAttributeNames);

		for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
			String key = entry.getKey();

			ExpandoColumn expandoColumn =
				_expandoColumnLocalService.getDefaultTableColumn(
					shardedModel.getCompanyId(), baseModel.getModelClassName(),
					key.substring(0, key.lastIndexOf("-")));

			if (expandoColumn == null) {
				continue;
			}

			ExpandoField expandoField = new ExpandoField() {
				{
					setColumnId(expandoColumn::getColumnId);
					setName(() -> key);
					setValue(() -> _parseValue(entry.getValue()));
				}
			};

			expandoFields.add(expandoField);
		}

		return expandoFields.toArray(new ExpandoField[0]);
	}

	private Field[] _getFields(BaseModel<?> baseModel) throws Exception {
		if (StringUtil.equals(
				baseModel.getModelClassName(), ExpandoColumn.class.getName())) {

			ExpandoColumn expandoColumn = (ExpandoColumn)baseModel;

			String className = User.class.getName();

			if (_isCustomField(
					Organization.class.getName(), expandoColumn.getTableId())) {

				className = Organization.class.getName();
			}

			String dataType = ExpandoColumnConstants.getDataType(
				expandoColumn.getType());

			if (Validator.isBlank(dataType)) {
				dataType = ExpandoColumnConstants.DATA_TYPE_TEXT;
			}

			return _getExpandoColumnFields(
				className, dataType,
				ExpandoColumnConstants.getDefaultDisplayTypeProperty(
					expandoColumn.getType(),
					expandoColumn.getTypeSettingsProperties()),
				expandoColumn);
		}

		List<Field> fields = new ArrayList<>();

		List<String> includeAttributeNames = new ArrayList<>();

		if (StringUtil.equals(
				baseModel.getModelClassName(), AccountEntry.class.getName())) {

			AccountEntry accountEntry = (AccountEntry)baseModel;

			AnalyticsConfiguration analyticsConfiguration =
				_analyticsConfigurationRegistry.getAnalyticsConfiguration(
					accountEntry.getCompanyId());

			includeAttributeNames = ListUtil.fromArray(
				analyticsConfiguration.syncedAccountFieldNames());
		}

		if (StringUtil.equals(
				baseModel.getModelClassName(), User.class.getName())) {

			User user = (User)baseModel;

			AnalyticsConfiguration analyticsConfiguration =
				_analyticsConfigurationRegistry.getAnalyticsConfiguration(
					user.getCompanyId());

			includeAttributeNames = ListUtil.fromArray(
				analyticsConfiguration.syncedUserFieldNames());

			_addFieldAttributes(
				user.getContact(), fields,
				_filterAttributeNames(
					ListUtil.fromArray(
						analyticsConfiguration.syncedContactFieldNames()),
					includeAttributeNames));

			long[] organizationIds = user.getOrganizationIds();
			long[] userGroupIds = user.getUserGroupIds();

			fields.add(
				new Field() {
					{
						setName(() -> "groupIds");
						setValue(
							() -> _getGroupIds(
								user, organizationIds, userGroupIds));
					}
				});

			fields.add(
				new Field() {
					{
						setName(() -> "organizationIds");
						setValue(
							() ->
								"[" + StringUtil.merge(organizationIds, ",") +
									"]");
					}
				});
			fields.add(
				new Field() {
					{
						setName(() -> "roleIds");
						setValue(() -> _getRoleIds(user));
					}
				});
			fields.add(
				new Field() {
					{
						setName(() -> "teamIds");
						setValue(() -> _getTeamIds(user));
					}
				});
			fields.add(
				new Field() {
					{
						setName(() -> "userGroupIds");
						setValue(
							() ->
								"[" + StringUtil.merge(userGroupIds, ",") +
									"]");
					}
				});
		}

		_addFieldAttributes(baseModel, fields, includeAttributeNames);

		if (StringUtil.equals(
				baseModel.getModelClassName(), Group.class.getName())) {

			for (Field field : fields) {
				if (StringUtil.equals(field.getName(), "name")) {
					Group group = (Group)baseModel;

					field.setValue(group::getNameCurrentValue);

					break;
				}
			}
		}

		if (StringUtil.equals(
				baseModel.getModelClassName(), Organization.class.getName())) {

			Field field = new Field();

			field.setName(() -> "parentOrganizationName");

			Organization organization = (Organization)baseModel;

			field.setValue(organization::getParentOrganizationName);

			fields.add(field);
		}

		return fields.toArray(new Field[0]);
	}

	private String _getGroupIds(
		User user, long[] organizationIds, long[] userGroupIds) {

		try {
			long[] ids = TransformUtil.transformToLongArray(
				_getUserSitesGroups(user, organizationIds, userGroupIds),
				Group::getGroupId);

			return "[" + StringUtil.merge(ids, ",") + "]";
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get group ids for user " + user.getUserId(),
					exception);
			}

			return "[]";
		}
	}

	private String _getRoleIds(User user) {
		try {
			return "[" + StringUtil.merge(user.getRoleIds(), ",") + "]";
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get role ids for user " + user.getUserId(),
					exception);
			}

			return "[]";
		}
	}

	private String _getTeamIds(User user) {
		try {
			return "[" + StringUtil.merge(user.getTeamIds(), ",") + "]";
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get team ids for user " + user.getUserId(),
					exception);
			}

			return "[]";
		}
	}

	private List<Group> _getUserSitesGroups(
			User user, long[] organizationIds, long[] userGroupIds)
		throws PortalException {

		List<Group> userSiteGroups = new ArrayList<>();

		for (long userGroupId : user.getGroupIds()) {
			Group group = _groupPersistence.findByPrimaryKey(userGroupId);

			if (group.isSite()) {
				userSiteGroups.add(group);
			}
		}

		if ((organizationIds.length != 0) || (userGroupIds.length != 0)) {
			List<Group> userGroups = _groupLocalService.getUserGroups(
				user.getUserId(), true);

			for (Group userGroup : userGroups) {
				if (userGroup.isSite()) {
					userSiteGroups.add(userGroup);
				}
			}
		}

		userSiteGroups.sort(new GroupNameComparator(true));

		return userSiteGroups;
	}

	private boolean _isCustomField(String className, long tableId) {
		long classNameId = _classNameLocalService.getClassNameId(className);

		try {
			ExpandoTable expandoTable =
				_expandoTablePersistence.findByPrimaryKey(tableId);

			if (Objects.equals(
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					expandoTable.getName()) &&
				(expandoTable.getClassNameId() == classNameId)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get expando table " + tableId, exception);
			}
		}

		return false;
	}

	private String _parseValue(Object value) {
		if (value == null) {
			return null;
		}

		Class<?> clazz = value.getClass();

		if (!clazz.isArray()) {
			return String.valueOf(value);
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (int i = 0; i < Array.getLength(value); i++) {
			jsonArray.put(Array.get(value, i));
		}

		return jsonArray.toString();
	}

	private DXPEntity _toDXPEntity(
		ExpandoField[] expandoFields, Field[] fields, String id,
		Date modifiedDate, String type) {

		DXPEntity dxpEntity = new DXPEntity();

		dxpEntity.setExpandoFields(
			() -> {
				if (expandoFields != null) {
					return expandoFields;
				}

				return new ExpandoField[0];
			});
		dxpEntity.setFields(
			() -> {
				if (fields != null) {
					return fields;
				}

				return new Field[0];
			});
		dxpEntity.setId(() -> id);
		dxpEntity.setModifiedDate(() -> modifiedDate);
		dxpEntity.setType(() -> type);

		return dxpEntity;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DXPEntityDTOConverterImpl.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.SUPPORTS, new Class<?>[] {Exception.class});

	@Reference
	private AnalyticsConfigurationRegistry _analyticsConfigurationRegistry;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTablePersistence _expandoTablePersistence;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupPersistence _groupPersistence;

	@Reference
	private JSONFactory _jsonFactory;

}