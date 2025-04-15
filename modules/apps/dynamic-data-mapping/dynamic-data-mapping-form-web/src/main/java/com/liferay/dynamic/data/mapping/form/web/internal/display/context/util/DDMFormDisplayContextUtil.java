/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Carolina Barbosa
 */
public class DDMFormDisplayContextUtil {

	public static void addCaptchaDDMFormField(
		DDMForm ddmForm, DDMFormInstanceSettings ddmFormInstanceSettings,
		RenderRequest renderRequest) {

		if (!ddmFormInstanceSettings.requireCaptcha()) {
			return;
		}

		DDMFormField captchaDDMFormField = new DDMFormField(
			_DDM_FORM_FIELD_NAME_CAPTCHA, DDMFormFieldTypeConstants.CAPTCHA);

		captchaDDMFormField.setDataType("string");
		captchaDDMFormField.setLabel(
			new LocalizedValue() {
				{
					addString(
						LocaleUtil.getDefault(),
						DDMFormFieldTypeConstants.CAPTCHA);
				}
			});

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String captchaResourceURL =
			themeDisplay.getPathMain() + "/portal/captcha/get_image";

		String portletId = PortalUtil.getPortletId(renderRequest);

		if (Validator.isNotNull(portletId)) {
			captchaResourceURL = captchaResourceURL.concat(
				"?portletId=" + portletId);
		}

		captchaDDMFormField.setProperty("url", captchaResourceURL);
		captchaDDMFormField.setShowLabel(false);

		ddmForm.addDDMFormField(captchaDDMFormField);
	}

	public static void addCaptchaDDMFormLayoutRow(
		DDMFormInstanceSettings ddmFormInstanceSettings,
		DDMFormLayout ddmFormLayout) {

		if (!ddmFormInstanceSettings.requireCaptcha()) {
			return;
		}

		List<DDMFormLayoutPage> ddmFormLayoutPages =
			ddmFormLayout.getDDMFormLayoutPages();

		DDMFormLayoutPage ddmFormLayoutPage = ddmFormLayoutPages.get(
			ddmFormLayoutPages.size() - 1);

		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		ddmFormLayoutRow.addDDMFormLayoutColumn(
			new DDMFormLayoutColumn(
				DDMFormLayoutColumn.FULL, _DDM_FORM_FIELD_NAME_CAPTCHA));

		ddmFormLayoutPage.addDDMFormLayoutRow(ddmFormLayoutRow);
	}

	private static final String _DDM_FORM_FIELD_NAME_CAPTCHA = "_CAPTCHA_";

}