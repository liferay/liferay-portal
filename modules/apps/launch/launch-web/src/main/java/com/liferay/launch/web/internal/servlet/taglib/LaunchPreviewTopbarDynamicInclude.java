/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.servlet.taglib;

import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DynamicInclude.class)
public class LaunchPreviewTopbarDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay = _getLaunchPreviewThemeDisplay(
			httpServletRequest);

		if (themeDisplay == null) {
			return;
		}

		PageContext pageContext = PageContextFactoryUtil.create(
			httpServletRequest, httpServletResponse);

		JspWriter jspWriter = pageContext.getOut();

		if (Objects.equals(key, _KEY_TOP_HEAD)) {
			_writeCSSLink(httpServletRequest, jspWriter);

			return;
		}

		try {
			String portletNamespace = _portal.getPortletNamespace(
				LaunchPortletKeys.LAUNCH);

			jspWriter.write("<div class=\"launch-preview-topbar-root\">");

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{LaunchPreviewTopbar} from launch-web",
					portletNamespace + "PreviewTopbar", null, true),
				HashMapBuilder.<String, Object>put(
					"launchSetId",
					ParamUtil.getLong(httpServletRequest, "previewLaunchSetId")
				).put(
					"noLaunchURL", _getNoLaunchURL(httpServletRequest)
				).build(),
				httpServletRequest, jspWriter);

			jspWriter.write("</div>");
		}
		catch (Exception exception) {
			_log.error("Unable to render the launch preview topbar", exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(_KEY_BODY_TOP);
		dynamicIncludeRegistry.register(_KEY_TOP_HEAD);
	}

	private ThemeDisplay _getLaunchPreviewThemeDisplay(
		HttpServletRequest httpServletRequest) {

		if (Validator.isNull(
				ParamUtil.getString(
					httpServletRequest, "previewLaunchSetId")) ||
			!Objects.equals(
				Constants.PREVIEW,
				ParamUtil.getString(httpServletRequest, "p_l_mode"))) {

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || !themeDisplay.isSignedIn() ||
			!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-72278")) {

			return null;
		}

		return themeDisplay;
	}

	private String _getNoLaunchURL(HttpServletRequest httpServletRequest) {
		return HttpComponentsUtil.setParameter(
			_portal.getCurrentURL(httpServletRequest), "previewLaunchSetId", 0);
	}

	private void _writeCSSLink(
			HttpServletRequest httpServletRequest, JspWriter jspWriter)
		throws IOException {

		jspWriter.write(
			StringBundler.concat(
				"<link href=\"",
				_portal.getStaticResourceURL(
					httpServletRequest,
					_servletContext.getContextPath() +
						"/css/LaunchPreviewTopbar.css"),
				"\" rel=\"stylesheet\" type=\"text/css\" />"));
	}

	private static final String _KEY_BODY_TOP =
		"/html/common/themes/body_top.jsp#post";

	private static final String _KEY_TOP_HEAD =
		"/html/common/themes/top_head.jsp#post";

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchPreviewTopbarDynamicInclude.class);

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.launch.web)")
	private ServletContext _servletContext;

}