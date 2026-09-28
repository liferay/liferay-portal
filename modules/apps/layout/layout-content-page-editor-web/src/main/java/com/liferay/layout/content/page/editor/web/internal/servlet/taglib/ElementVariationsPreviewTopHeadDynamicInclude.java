/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.servlet.taglib;

import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = DynamicInclude.class)
public class ElementVariationsPreviewTopHeadDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		String mode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (!Objects.equals(mode, Constants.PREVIEW)) {
			return;
		}

		String[] audienceEntryERCs = ParamUtil.getStringValues(
			httpServletRequest, "audienceEntryERCs");

		if (ArrayUtil.isEmpty(audienceEntryERCs)) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			httpServletRequest, "segmentsExperienceId");

		ElementVariations elementVariations =
			_elementVariationsProvider.getElementVariations(
				themeDisplay.getPlid(), segmentsExperienceId);

		if (elementVariations == null) {
			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(
			StringUtil.replace(
				_TMPL_CONTENT, "${", "}",
				HashMapBuilder.put(
					"audienceEntryERCs",
					HtmlUtil.escapeJS(
						JSONUtil.putAll(
							audienceEntryERCs
						).toString())
				).put(
					"elementVariationsURL",
					HtmlUtil.escapeJS(
						StringBundler.concat(
							_portal.getPathModule(), "/audiences/",
							themeDisplay.getPlid(), StringPool.SLASH,
							segmentsExperienceId, "/variations.(",
							elementVariations.getHash(), ").js"))
				).put(
					"nonceAttribute",
					ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
						httpServletRequest)
				).build()));
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		ElementVariationsPreviewTopHeadDynamicInclude.class,
		"element_variations_preview.tmpl");

	@Reference
	private ElementVariationsProvider _elementVariationsProvider;

	@Reference
	private Portal _portal;

}