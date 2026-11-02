/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.status.internal.struts;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(property = "path=/portal/status", service = StrutsAction.class)
public class StatusStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Exception exception = (Exception)httpServletRequest.getAttribute(
			WebKeys.PORTAL_STATUS_EXCEPTION);

		if (exception != null) {
			SessionErrors.remove(httpServletRequest, exception.getClass());
		}

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			themeDisplay.getScopeGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/status.jsp");

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		Object originalDefinition = httpServletRequest.getAttribute(
			TilesUtil.DEFINITION);

		httpServletRequest.setAttribute(TilesUtil.DEFINITION, _definition);
		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_CONTENT, unsyncStringWriter.getStringBundler());

		String html = null;

		String originalPrincipalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(themeDisplay.getUserId());

			html = ThemeUtil.include(
				httpServletRequest.getServletContext(), httpServletRequest,
				httpServletResponse, "portal_normal.ftl", layoutSet.getTheme(),
				false);
		}
		finally {
			PrincipalThreadLocal.setName(originalPrincipalName);

			httpServletRequest.setAttribute(
				TilesUtil.DEFINITION, originalDefinition);
		}

		PortalMessages.clear(httpServletRequest);
		SessionMessages.clear(httpServletRequest);

		StringBundler sb = (StringBundler)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_CONTENT);

		if (sb != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Replacing all body content because theme " +
						layoutSet.getThemeId() + " lacks a content include");
			}

			html = ThemeUtil.replaceBodyContent(html, sb.toString());
		}

		ServletResponseUtil.write(httpServletResponse, html);

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StatusStrutsAction.class);

	private static final Definition _definition = new Definition(
		StringPool.BLANK,
		HashMapBuilder.put(
			"content", "/portal/layout_content.jsp"
		).put(
			"selectable", "true"
		).build());

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.utility.page.status)"
	)
	private ServletContext _servletContext;

}