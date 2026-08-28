/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.taglib.servlet.taglib.ProductNavigationControlMenuTag;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DynamicInclude.class)
public class HistoryLayoutModeControlMenuBodyTopDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String layoutMode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!Constants.HISTORY.equals(layoutMode)) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return;
		}

		Layout layout = themeDisplay.getLayout();

		if ((layout == null) || !layout.isDraftLayout() ||
			!layout.isTypeContent()) {

			return;
		}

		PageContext pageContext = PageContextFactoryUtil.create(
			httpServletRequest, httpServletResponse);

		try {
			ProductNavigationControlMenuTag productNavigationControlMenuTag =
				new ProductNavigationControlMenuTag();

			productNavigationControlMenuTag.setPageContext(pageContext);

			productNavigationControlMenuTag.doStartTag();
			productNavigationControlMenuTag.doEndTag();
		}
		catch (Exception exception) {
			_log.error(
				"Unable to include the control menu in the history layout mode",
				exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/body_top.jsp#post");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HistoryLayoutModeControlMenuBodyTopDynamicInclude.class);

}