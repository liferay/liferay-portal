/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.captcha;

import com.liferay.captcha.taglib.servlet.taglib.CaptchaTag;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bruno Basto
 */
@Component(
	property = "ddm.form.field.type.name=" + DDMFormFieldTypeConstants.CAPTCHA,
	service = DDMFormFieldTemplateContextContributor.class
)
public class CaptchaDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String html = StringPool.BLANK;

		try {
			html = renderCaptchaTag(ddmFormField, ddmFormFieldRenderingContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.singletonMap("html", html);
	}

	protected String renderCaptchaTag(
			DDMFormField ddmFormField,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws Exception {

		CaptchaTag captchaTag = new CaptchaTag();

		captchaTag.setUrl(
			GetterUtil.getString(ddmFormField.getProperty("url")));

		HttpServletRequest httpServletRequest =
			ddmFormFieldRenderingContext.getHttpServletRequest();
		HttpServletResponse httpServletResponse =
			ddmFormFieldRenderingContext.getHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		captchaTag.setPageContext(
			PageContextFactoryUtil.create(
				httpServletRequest,
				new PipingServletResponse(
					httpServletResponse, unsyncStringWriter)));

		captchaTag.runTag();

		return unsyncStringWriter.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CaptchaDDMFormFieldTemplateContextContributor.class);

}