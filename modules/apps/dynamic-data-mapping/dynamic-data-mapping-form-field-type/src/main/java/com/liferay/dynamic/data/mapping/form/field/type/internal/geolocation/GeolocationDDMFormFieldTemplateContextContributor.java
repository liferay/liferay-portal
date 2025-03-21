/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.geolocation;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcela Cunha
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.GEOLOCATION,
	service = DDMFormFieldTemplateContextContributor.class
)
public class GeolocationDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _getGroup(httpServletRequest, themeDisplay);

		String mapProviderKey = GetterUtil.getString(
			MapProviderHelperUtil.getMapProviderKey(
				_groupLocalService, themeDisplay.getCompanyId(),
				group.getGroupId()),
			"OpenStreetMap");

		return HashMapBuilder.<String, Object>put(
			"googleMapsAPIKey", _getGoogleMapsAPIKey(group, themeDisplay)
		).put(
			"mapProviderKey", mapProviderKey
		).put(
			"predefinedValue",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext.getLocale(),
				"predefinedValue")
		).put(
			"value",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormFieldRenderingContext, "value")
		).put(
			"viewMode",
			GetterUtil.getBoolean(ddmFormFieldRenderingContext.isViewMode())
		).build();
	}

	private String _getGoogleMapsAPIKey(
		Group group, ThemeDisplay themeDisplay) {

		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(themeDisplay.getCompanyId());

		if (group == null) {
			return companyPortletPreferences.getValue("googleMapsAPIKey", null);
		}

		return GetterUtil.getString(
			group.getTypeSettingsProperty("googleMapsAPIKey"),
			companyPortletPreferences.getValue("googleMapsAPIKey", null));
	}

	private Group _getGroup(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		Group group = (Group)httpServletRequest.getAttribute("site.liveGroup");

		if (group != null) {
			return group;
		}

		group = themeDisplay.getScopeGroup();

		if (!group.isControlPanel()) {
			return group;
		}

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

}