/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.field.business.type;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
	implements ObjectFieldBusinessType {

	@Override
	public String getDBType() {
		return ObjectFieldConstants.DB_TYPE_LONG;
	}

	@Override
	public String getDDMFormFieldTypeName() {
		return ObjectDDMFormFieldTypeConstants.ASSIGNEE;
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
					objectField.getCompanyId(),
					assignee.getExternalReferenceCode(), assignee.getName(),
					assignee.getTypeAsString());
			}
			else if (value instanceof Map) {
				Map<String, Serializable> valueMap =
					(Map<String, Serializable>)value;

				return _getValue(
					objectField.getCompanyId(),
					MapUtil.getString(valueMap, "externalReferenceCode"),
					MapUtil.getString(valueMap, "name"),
					MapUtil.getString(valueMap, "type"));
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

	@Override
	public boolean isVisible(ObjectDefinition objectDefinition) {
		return FeatureFlagManagerUtil.isEnabled(
			objectDefinition.getCompanyId(), "LPD-6233");
	}

	private Object _getValue(
			long companyId, String externalReferenceCode, String name,
			String type)
		throws Exception {

		if (StringUtil.equals(type, Assignee.Type.ROLE.toString())) {
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
							externalReferenceCode, companyId);
					}

					return role.getRoleId();
				}
			).build();
		}
		else if (StringUtil.equals(type, Assignee.Type.USER.toString())) {
			return HashMapBuilder.put(
				"classNameId", _portal.getClassNameId(User.class.getName())
			).put(
				"classPK",
				() -> {
					User user =
						_userLocalService.getUserByExternalReferenceCode(
							externalReferenceCode, companyId);

					return user.getUserId();
				}
			).build();
		}

		return Collections.emptyMap();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssigneeObjectFieldBusinessType.class);

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

}