/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.servlet.taglib;

import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.frontend.js.audiences.AudiencesDefinitionProvider;
import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.frontend.js.audiences.web.internal.configuration.FrontendJSAudiencesConfiguration;
import com.liferay.frontend.js.audiences.web.internal.util.BootstrapJavaScriptUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ServletAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = DynamicInclude.class)
public class FrontendJSAudiencesWebTopHeadDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-85746")) {
			return;
		}

		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (!Objects.equals(mode, Constants.VIEW)) {
			return;
		}

		AudiencesDefinition audiencesDefinition =
			_audiencesDefinitionProvider.getAudiencesDefinition(companyId);

		if (audiencesDefinition == null) {
			return;
		}

		FrontendJSAudiencesConfiguration frontendJSAudiencesConfiguration;

		try {
			frontendJSAudiencesConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FrontendJSAudiencesConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			throw new IOException(configurationException);
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ElementVariations elementVariations =
			_elementVariationsProvider.getElementVariations(
				themeDisplay.getPlid());

		if (elementVariations != null) {
			printWriter.print("<meta content=\"");
			printWriter.print(themeDisplay.getPlid());
			printWriter.print(StringPool.COLON);
			printWriter.print(elementVariations.getHash());
			printWriter.print("\" name=\"audiences-variations\">");
		}

		printWriter.print(
			"<script data-senna-track=\"permanent\" id=\"audiencesBootstrap\"");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" src=\"");

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		ServletAbsolutePortalURLBuilder servletAbsolutePortalURLBuilder =
			absolutePortalURLBuilder.forServlet("/audiences");

		printWriter.print(servletAbsolutePortalURLBuilder.build());

		printWriter.print("/bootstrap.(");
		printWriter.print(BootstrapJavaScriptUtil.getHash());
		printWriter.print(").js?audiencesDefinitionHash=");
		printWriter.print(audiencesDefinition.getHash());
		printWriter.print("&enableLog=");
		printWriter.print(frontendJSAudiencesConfiguration.enableLog());
		printWriter.print("\" type=\"module\"></script>");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private AudiencesDefinitionProvider _audiencesDefinitionProvider;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ElementVariationsProvider _elementVariationsProvider;

	@Reference
	private Portal _portal;

}