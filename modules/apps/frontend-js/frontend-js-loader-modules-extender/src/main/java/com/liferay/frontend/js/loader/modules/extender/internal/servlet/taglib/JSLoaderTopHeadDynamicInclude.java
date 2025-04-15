/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProvider;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.frontend.esm.FrontendESMUtil;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	configurationPid = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	property = "service.ranking:Integer=" + (Integer.MAX_VALUE - 3),
	service = DynamicInclude.class
)
public class JSLoaderTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-48372")) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.write(" data-senna-track=\"temporary\" type=\"");
		printWriter.write(ContentTypes.TEXT_JAVASCRIPT);
		printWriter.write("\">window.__CONFIG__= {basePath: '', combine: ");
		printWriter.write(Boolean.toString(themeDisplay.isThemeJsFastLoad()));
		printWriter.write(", defaultURLParams: ");
		printWriter.write(_getDefaultURLParams(themeDisplay));
		printWriter.write(", explainResolutions: ");
		printWriter.write(Boolean.toString(_details.explainResolutions()));
		printWriter.write(", exposeGlobal: ");
		printWriter.write(Boolean.toString(_details.exposeGlobal()));
		printWriter.write(", logLevel: '");
		printWriter.write(_details.logLevel());
		printWriter.write("', moduleType: '");
		printWriter.write(FrontendESMUtil.getScriptType());
		printWriter.write("', namespace:'Liferay', nonce: '");
		printWriter.write(
			_contentSecurityPolicyNonceProvider.getNonce(httpServletRequest));
		printWriter.write(
			"', reportMismatchedAnonymousModules: 'warn', resolvePath: '");
		printWriter.write(_getResolvePath(httpServletRequest));
		printWriter.write("', url: '");
		printWriter.write(_getURL(httpServletRequest, themeDisplay));
		printWriter.write("', waitTimeout: ");
		printWriter.write(String.valueOf(_details.waitTimeout() * 1000));
		printWriter.write("};</script><script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.write(" data-senna-track=\"permanent\" src=\"");

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		printWriter.write(
			absolutePortalURLBuilder.forBundleScript(
				_bundle, "/loader.js"
			).build());

		printWriter.write("\" type=\"");
		printWriter.write(ContentTypes.TEXT_JAVASCRIPT);
		printWriter.write("\"></script>");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundle = bundleContext.getBundle();

		_details = ConfigurableUtil.createConfigurable(
			Details.class, properties);
		_lastModified = String.valueOf(System.currentTimeMillis());
	}

	private String _getDefaultURLParams(ThemeDisplay themeDisplay) {
		if (themeDisplay.isThemeJsFastLoad()) {
			return "null";
		}

		return "{languageId: '" + themeDisplay.getLanguageId() + "'}";
	}

	private String _getResolvePath(HttpServletRequest httpServletRequest) {
		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		return absolutePortalURLBuilder.forServlet(
			"/js_resolve_modules"
		).build();
	}

	private String _getURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (themeDisplay.isThemeJsFastLoad()) {
			String url = _portal.getStaticResourceURL(
				httpServletRequest,
				themeDisplay.getCDNDynamicResourcesHost() +
					themeDisplay.getPathContext() + "/combo/",
				"minifierType=js",
				PortalWebResourcesUtil.getLastModified(
					PortalWebResourceConstants.RESOURCE_TYPE_JS));

			return url + StringPool.AMPERSAND;
		}

		return themeDisplay.getCDNBaseURL();
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private volatile Bundle _bundle;

	@Reference
	private ContentSecurityPolicyNonceProvider
		_contentSecurityPolicyNonceProvider;

	private volatile Details _details;
	private volatile String _lastModified;

	@Reference
	private Portal _portal;

}