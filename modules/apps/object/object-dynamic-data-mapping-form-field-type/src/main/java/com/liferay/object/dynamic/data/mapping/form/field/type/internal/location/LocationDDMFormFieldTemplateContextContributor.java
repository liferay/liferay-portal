/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.location;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.dynamic.data.mapping.form.field.type.internal.BaseDDMFormFieldTemplateContextContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = "ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.LOCATION,
	service = DDMFormFieldTemplateContextContributor.class
)
public class LocationDDMFormFieldTemplateContextContributor
	extends BaseDDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				GetterUtil.getLong(
					ddmFormField.getProperty("objectDefinitionId")));

		return HashMapBuilder.<String, Object>put(
			"googleMapsAPIKey",
			() -> {
				PortletPreferences companyPortletPreferences =
					PrefsPropsUtil.getPreferences(
						objectDefinition.getCompanyId());

				String googleMapsAPIKey = companyPortletPreferences.getValue(
					"googleMapsAPIKey", null);

				Group group = _groupLocalService.fetchGroup(
					GetterUtil.getLong(ddmFormField.getProperty("groupId")));

				if ((group != null) && !group.isControlPanel()) {
					googleMapsAPIKey = GetterUtil.getString(
						group.getTypeSettingsProperty("googleMapsAPIKey"),
						googleMapsAPIKey);
				}

				return _secretResolver.resolve(
					objectDefinition.getCompanyId(), googleMapsAPIKey);
			}
		).put(
			"groupId", GetterUtil.getLong(ddmFormField.getProperty("groupId"))
		).put(
			"mapProviderKey",
			GetterUtil.getString(
				MapProviderHelperUtil.getMapProviderKey(
					_groupLocalService, objectDefinition.getCompanyId(),
					GetterUtil.getLong(ddmFormField.getProperty("groupId"))),
				"OpenStreetMap")
		).put(
			"objectDefinitionId", objectDefinition.getObjectDefinitionId()
		).putAll(
			super.getParameters(ddmFormField, ddmFormFieldRenderingContext)
		).build();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private SecretResolver _secretResolver;

}