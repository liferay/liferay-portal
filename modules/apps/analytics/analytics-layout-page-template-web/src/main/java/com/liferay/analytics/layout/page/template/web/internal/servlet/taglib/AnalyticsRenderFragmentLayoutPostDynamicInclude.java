/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.layout.page.template.web.internal.servlet.taglib;

import com.liferay.analytics.layout.page.template.web.internal.servlet.taglib.util.AnalyticsRenderFragmentLayoutUtil;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.taglib.aui.ScriptTag;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = DynamicInclude.class)
public class AnalyticsRenderFragmentLayoutPostDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String dynamicIncludeKey)
		throws IOException {

		try {
			if (!_analyticsSettingsManager.isAnalyticsEnabled(
					_portal.getCompanyId(httpServletRequest))) {

				return;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if (layoutDisplayPageObjectProvider == null) {
			return;
		}

		if (!AnalyticsRenderFragmentLayoutUtil.isTrackeable(
				layoutDisplayPageObjectProvider)) {

			try {
				ScriptTag scriptTag = new ScriptTag();

				scriptTag.setPosition("inline");

				scriptTag.doBodyTag(
					httpServletRequest, httpServletResponse,
					pageContext -> _processScriptTagBody(
						layoutDisplayPageObjectProvider, pageContext));
			}
			catch (JspException jspException) {
				throw new IOException(jspException);
			}
		}
		else {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.print("</div>");
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.layout,taglib#/render_fragment_layout/page.jsp#post");
	}

	private void _processScriptTagBody(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		PageContext pageContext) {

		try {
			StringBundler sb = new StringBundler(9);

			sb.append("window.onload = function() {Analytics.track(\"");

			InfoItemClassDetails infoItemClassDetails =
				new InfoItemClassDetails(
					layoutDisplayPageObjectProvider.getClassName());

			ServletRequest servletRequest = pageContext.getRequest();

			Locale locale = servletRequest.getLocale();

			String label = infoItemClassDetails.getLabel(locale);

			sb.append(label);

			sb.append(" Viewed\", {'classPK': ");
			sb.append(
				String.valueOf(layoutDisplayPageObjectProvider.getClassPK()));
			sb.append(", 'title': '");
			sb.append(layoutDisplayPageObjectProvider.getTitle(locale));
			sb.append("', 'type': '");
			sb.append(label);
			sb.append("'})};");

			Writer writer = pageContext.getOut();

			writer.write(sb.toString());
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsRenderFragmentLayoutPostDynamicInclude.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Portal _portal;

}