/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.taglib;

import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.BundleScriptAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = "service.ranking:Integer=" + (Integer.MAX_VALUE - 2),
	service = DynamicInclude.class
)
public class LiferayGlobalObjectPostAUIDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (_bundle == null) {
			_log.error("Bundle is null");

			return;
		}

		try {
			AbsolutePortalURLBuilder absolutePortalURLBuilder =
				_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
					httpServletRequest);

			BundleScriptAbsolutePortalURLBuilder
				bundleScriptAbsolutePortalURLBuilder =
					absolutePortalURLBuilder.forBundleScript(
						_bundle, "/Liferay.js");

			_renderScript(
				httpServletRequest, httpServletResponse.getWriter(),
				bundleScriptAbsolutePortalURLBuilder.build(),
				"text/javascript");
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Activate
	@Modified
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	@Deactivate
	protected void deactivate() {
		_bundle = null;
	}

	private void _renderScript(
		HttpServletRequest httpServletRequest, PrintWriter printWriter,
		String src, String type) {

		printWriter.print("<script");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));

		try {
			if (Validator.isNotNull(
					PortalUtil.getCDNHost(httpServletRequest))) {

				printWriter.print(" crossorigin=\"\"");
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		printWriter.print(" data-senna-track=\"permanent\" src=\"");
		printWriter.print(src);
		printWriter.print("\" type=\"");
		printWriter.print(type);
		printWriter.println("\"></script>");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayGlobalObjectPostAUIDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private volatile Bundle _bundle;

}