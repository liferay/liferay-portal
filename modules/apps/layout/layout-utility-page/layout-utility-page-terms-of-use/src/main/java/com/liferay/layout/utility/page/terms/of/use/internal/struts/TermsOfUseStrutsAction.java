/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.terms.of.use.internal.struts;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.terms.of.use.TermsOfUseContentProvider;
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
 * @author Eudaldo Alonso
 */
@Component(property = "path=/portal/terms_of_use", service = StrutsAction.class)
public class TermsOfUseStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		httpServletRequest.setAttribute(
			TermsOfUseContentProvider.class.getName(),
			_termsOfUseContentProvider);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			themeDisplay.getScopeGroupId(), false);

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/terms_of_use.jsp");

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

		try {
			html = ThemeUtil.include(
				httpServletRequest.getServletContext(), httpServletRequest,
				httpServletResponse, "portal_normal.ftl", layoutSet.getTheme(),
				false);
		}
		finally {
			httpServletRequest.setAttribute(
				TilesUtil.DEFINITION, originalDefinition);
		}

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
		TermsOfUseStrutsAction.class);

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
		target = "(osgi.web.symbolicname=com.liferay.layout.utility.page.terms.of.use)"
	)
	private ServletContext _servletContext;

	@Reference
	private TermsOfUseContentProvider _termsOfUseContentProvider;

}