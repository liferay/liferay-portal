/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroOAuthReceiveFragmentRenderer extends BaseFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "liferay-data-platform";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "authentication");
	}

	@Override
	protected String getModule() {
		return "{OAuthReceive} from osb-faro-web/workspaces";
	}

	@Override
	protected Map<String, String> getParams(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.putAll(
			super.getParams(httpServletRequest)
		).put(
			"code", ParamUtil.getString(httpServletRequest, "code")
		).put(
			"error", ParamUtil.getString(httpServletRequest, "error")
		).put(
			"error_description",
			ParamUtil.getString(httpServletRequest, "error_description")
		).put(
			"oauth_token",
			ParamUtil.getString(httpServletRequest, "oauth_token")
		).put(
			"oauth_verifier",
			ParamUtil.getString(httpServletRequest, "oauth_verifier")
		).build();
	}

}