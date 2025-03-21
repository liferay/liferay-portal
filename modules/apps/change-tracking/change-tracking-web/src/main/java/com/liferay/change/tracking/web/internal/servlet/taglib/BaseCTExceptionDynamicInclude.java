/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.taglib;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.taglib.aui.ScriptTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
public abstract class BaseCTExceptionDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			ScriptTag scriptTag = new ScriptTag();

			scriptTag.setPosition("inline");

			scriptTag.doBodyTag(
				httpServletRequest, httpServletResponse,
				pageContext -> _processScriptTagBody(
					httpServletRequest, pageContext));
		}
		catch (JspException jspException) {
			ReflectionUtil.throwException(jspException);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	protected abstract String getMessage(Locale locale);

	protected abstract String getTitle(Locale locale);

	@Reference
	protected Portal portal;

	private void _processScriptTagBody(
		HttpServletRequest httpServletRequest, PageContext pageContext) {

		try {
			Writer writer = pageContext.getOut();

			writer.write(
				"Liferay.Util.openToast({autoClose: 10000, message: '");

			Locale locale = portal.getLocale(httpServletRequest);

			writer.write(getMessage(locale));

			writer.write("', title: '");
			writer.write(getTitle(locale));
			writer.write(":', type: 'danger',});");
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
	}

}