/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.security.permission;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.security.permission.DDMPermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN
	},
	service = DDMPermissionChecker.class
)
public class DDMFormInstanceDDMPermissionChecker
	implements DDMPermissionChecker {

	@Override
	public boolean containsPermission(
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _ddmFormInstanceModelResourcePermission.contains(
			themeDisplay.getPermissionChecker(),
			ddmFormFieldRenderingContext.getDDMFormInstanceId(),
			DDMActionKeys.ADD_FORM_INSTANCE_RECORD);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceModelResourcePermission;

}