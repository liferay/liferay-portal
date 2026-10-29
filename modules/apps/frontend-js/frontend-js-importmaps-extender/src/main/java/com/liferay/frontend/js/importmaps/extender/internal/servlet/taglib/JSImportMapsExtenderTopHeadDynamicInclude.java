/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.importmaps.extender.internal.servlet.taglib;

import com.liferay.frontend.js.importmaps.extender.DynamicJSImportMapsContributor;
import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.frontend.js.importmaps.extender.internal.osgi.util.tracker.DynamicJSImportMapsContributorServiceTrackerCustomizer;
import com.liferay.frontend.js.importmaps.extender.internal.osgi.util.tracker.JSImportMapsContributorServiceTrackerCustomizer;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"dynamic.include.key=/html/common/themes/top_head.jsp#pre",
		"service.ranking:Integer=" + Integer.MAX_VALUE
	},
	service = DynamicInclude.class
)
public class JSImportMapsExtenderTopHeadDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" type=\"importmap\">");

		_jsImportMapsCache.writeImportMaps(httpServletRequest, printWriter);

		printWriter.print("</script>");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_jsImportMapsCache = new JSImportMapsCache(_portal);

		_dynamicJSImportMapsContributorServiceTracker = new ServiceTracker<>(
			bundleContext, DynamicJSImportMapsContributor.class,
			new DynamicJSImportMapsContributorServiceTrackerCustomizer(
				_bundleContext, _jsImportMapsCache));

		_dynamicJSImportMapsContributorServiceTracker.open();

		_jsImportMapsContributorServiceTracker = new ServiceTracker<>(
			bundleContext, JSImportMapsContributor.class,
			new JSImportMapsContributorServiceTrackerCustomizer(
				_bundleContext, _jsImportMapsCache));

		_jsImportMapsContributorServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;

		_dynamicJSImportMapsContributorServiceTracker.close();

		_dynamicJSImportMapsContributorServiceTracker = null;

		_jsImportMapsContributorServiceTracker.close();

		_jsImportMapsContributorServiceTracker = null;
	}

	private volatile BundleContext _bundleContext;
	private ServiceTracker
		<DynamicJSImportMapsContributor, JSImportMapsRegistration>
			_dynamicJSImportMapsContributorServiceTracker;
	private JSImportMapsCache _jsImportMapsCache;
	private ServiceTracker<JSImportMapsContributor, JSImportMapsRegistration>
		_jsImportMapsContributorServiceTracker;

	@Reference
	private Portal _portal;

}