/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.render.ObjectFieldRenderingContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import java.io.Serializable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Franca
 */
@Component(
	property = "object.field.business.type.key=" + ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE,
	service = ObjectFieldBusinessType.class
)
public class AssigneeObjectFieldBusinessType
	extends BaseObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ASSIGNEE;
	}

	@Override
	public Serializable getDTOValue(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			ObjectField objectField, Serializable serializable)
		throws Exception {

		if (serializable instanceof Assignee) {
			return serializable;
		}

		if (!(serializable instanceof Map)) {
			return null;
		}

		Map<String, Long> assigneeMap = (Map<String, Long>)serializable;

		if (assigneeMap.containsKey("externalReferenceCode")) {
			return Assignee.toDTO(_jsonFactory.looseSerializeDeep(assigneeMap));
		}

		String className = _portal.fetchClassName(
			MapUtil.getLong(assigneeMap, "classNameId"));

		if (StringUtil.equals(className, Role.class.getName())) {
			Role role = _roleLocalService.fetchRole(
				MapUtil.getLong(assigneeMap, "classPK"));

			if (role == null) {
				return new Assignee();
			}

			return new Assignee() {
				{
					setExternalReferenceCode(role::getExternalReferenceCode);
					setName(role::getName);
					setType(() -> Type.ROLE);
				}
			};
		}
		else if (StringUtil.equals(className, User.class.getName())) {
			User user = _userLocalService.fetchUser(
				MapUtil.getLong(assigneeMap, "classPK"));

			if (user == null) {
				return new Assignee();
			}

			return new Assignee() {
				{
					setExternalReferenceCode(user::getExternalReferenceCode);
					setName(user::getFullName);
					setPortrait(
						() -> {
							if (user.getPortraitId() == 0) {
								return null;
							}

							return user.getPortraitURL(
								new ThemeDisplay() {
									{
										setPathImage(_portal.getPathImage());
									}
								});
						});
					setType(() -> Type.USER);
				}
			};
		}

		return new Assignee();
	}

	@Override
	public String getDescription(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getModuleAndPortalResourceBundle(
				locale, getClass()),
			"assign-the-entry-to-a-user-or-role");
	}

	@Override
	public Object getDisplayContextValue(
			ObjectField objectField, long userId, Map<String, Object> values)
		throws PortalException {

		return values.get(objectField.getName());
	}

	public String getDisplayName(long classNameId, long classPK) {
		String className = _portal.fetchClassName(classNameId);

		if (StringUtil.equals(className, Role.class.getName())) {
			Role role = _roleLocalService.fetchRole(classPK);

			if (role != null) {
				return role.getName();
			}

			return null;
		}

		if (StringUtil.equals(className, User.class.getName())) {
			User user = _userLocalService.fetchUser(classPK);

			if (user != null) {
				return user.getFullName();
			}

			return null;
		}

		return null;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getModuleAndPortalResourceBundle(
				locale, getClass()),
			"assignee");
	}

	@Override
	public String getName() {
		return ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE;
	}

	@Override
	public Map<String, Object> getProperties(
			ObjectField objectField,
			ObjectFieldRenderingContext objectFieldRenderingContext)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"portletId", objectFieldRenderingContext.getPortletId()
		).putAll(
			super.getProperties(objectField, objectFieldRenderingContext)
		).build();
	}

	@Override
	public PropertyDefinition.PropertyType getPropertyType() {
		return PropertyDefinition.PropertyType.LONG;
	}

	@Override
	public Object getValue(
			Long groupId, ObjectField objectField, long userId,
			Map<String, Object> values)
		throws PortalException {

		Object value = values.get(objectField.getName());

		if (value == null) {
			return null;
		}

		try {
			if (value instanceof Assignee) {
				Assignee assignee = (Assignee)value;

				return _getValue(
					assignee.getExternalReferenceCode(), assignee.getName(),
					objectField, assignee.getTypeAsString());
			}
			else if (value instanceof Map) {
				Map<String, Serializable> valueMap =
					(Map<String, Serializable>)value;

				return _getValue(
					MapUtil.getString(valueMap, "externalReferenceCode"),
					MapUtil.getString(valueMap, "name"), objectField,
					MapUtil.getString(valueMap, "type"));
			}
			else if (value instanceof String) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					(String)value);

				return _getValue(
					jsonObject.getString("externalReferenceCode"),
					jsonObject.getString("name"), objectField,
					jsonObject.getString("type"));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new ObjectEntryValuesException.InvalidValue(
				objectField.getName());
		}

		return null;
	}

	@Override
	public boolean isLocalizationSupported(ObjectField objectField) {
		return false;
	}

	private Object _getValue(
			String externalReferenceCode, String name, ObjectField objectField,
			String type)
		throws Exception {

		if (StringUtil.equalsIgnoreCase(type, Assignee.Type.ROLE.toString())) {
			return HashMapBuilder.put(
				"classNameId", _portal.getClassNameId(Role.class.getName())
			).put(
				"classPK",
				() -> {
					Role role = null;

					if (LazyReferencingThreadLocal.isEnabled()) {
						role = _roleService.getOrAddEmptyRole(
							externalReferenceCode, StringPool.BLANK, 0, name,
							RoleConstants.TYPE_REGULAR);
					}
					else {
						role = _roleLocalService.getRoleByExternalReferenceCode(
							externalReferenceCode, objectField.getCompanyId());
					}

					if (StringUtil.equals(
							role.getName(), RoleConstants.GUEST)) {

						throw new ObjectEntryValuesException.InvalidValue(
							objectField.getName());
					}

					return role.getRoleId();
				}
			).build();
		}
		else if (StringUtil.equalsIgnoreCase(
					type, Assignee.Type.USER.toString())) {

			return HashMapBuilder.put(
				"classNameId", _portal.getClassNameId(User.class.getName())
			).put(
				"classPK",
				() -> {
					User user =
						_userLocalService.getUserByExternalReferenceCode(
							externalReferenceCode, objectField.getCompanyId());

					return user.getUserId();
				}
			).build();
		}

		return Collections.emptyMap();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssigneeObjectFieldBusinessType.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

}