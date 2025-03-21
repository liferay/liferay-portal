/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.importmaps.extender.internal.servlet.taglib;

import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.frontend.js.importmaps.extender.internal.configuration.JSImportMapsConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.frontend.esm.FrontendESMUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	configurationPid = "com.liferay.frontend.js.importmaps.extender.internal.configuration.JSImportMapsConfiguration",
	property = "service.ranking:Integer=" + Integer.MAX_VALUE,
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

		if (_jsImportMapsConfiguration.enableImportMaps()) {
			printWriter.print("<script");
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
			printWriter.print(" type=\"");

			if (_jsImportMapsConfiguration.enableESModuleShims()) {
				printWriter.print("importmap-shim");
			}
			else {
				printWriter.print("importmap");
			}

			printWriter.print("\">");

			_jsImportMapsCache.writeImportMaps(
				_portal.getCompanyId(httpServletRequest), printWriter);

			printWriter.print("</script>");
		}

		if (_jsImportMapsConfiguration.enableESModuleShims()) {
			printWriter.print("<script");
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
			printWriter.print(" type=\"esms-options\">{\"shimMode\": ");
			printWriter.print("true}</script><script");
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
			printWriter.print(" src=\"");

			AbsolutePortalURLBuilder absolutePortalURLBuilder =
				_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
					httpServletRequest);

			printWriter.print(
				absolutePortalURLBuilder.forBundleScript(
					_bundleContext.getBundle(),
					"/es-module-shims/es-module-shims.js"
				).build());

			printWriter.print("\"></script>\n");
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		modified();

		_jsImportMapsCache = new JSImportMapsCache();

		_serviceTracker = new ServiceTracker<>(
			bundleContext, JSImportMapsContributor.class,
			_serviceTrackerCustomizer);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_serviceTracker = null;

		_bundleContext = null;
	}

	@Modified
	protected void modified() {

		// See LPS-165021

		_jsImportMapsConfiguration = ConfigurableUtil.createConfigurable(
			JSImportMapsConfiguration.class,
			HashMapBuilder.put(
				"enable-es-module-shims", false
			).put(
				"enable-import-maps", true
			).build());

		FrontendESMUtil.setScriptType(
			_jsImportMapsConfiguration.enableESModuleShims() ? "module-shim" :
				"module");
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private volatile BundleContext _bundleContext;
	private JSImportMapsCache _jsImportMapsCache;
	private volatile JSImportMapsConfiguration _jsImportMapsConfiguration;

	@Reference
	private Portal _portal;

	private ServiceTracker<JSImportMapsContributor, JSImportMapsRegistration>
		_serviceTracker;

	private final ServiceTrackerCustomizer
		<JSImportMapsContributor, JSImportMapsRegistration>
			_serviceTrackerCustomizer =
				new ServiceTrackerCustomizer
					<JSImportMapsContributor, JSImportMapsRegistration>() {

					@Override
					public JSImportMapsRegistration addingService(
						ServiceReference<JSImportMapsContributor>
							serviceReference) {

						Long companyId = (Long)serviceReference.getProperty(
							"com.liferay.frontend.js.importmaps.company.id");

						if (companyId == null) {
							companyId = Long.valueOf(
								JSImportMapsCache.COMPANY_ID_ALL);
						}

						JSImportMapsContributor jsImportMapsContributor =
							_bundleContext.getService(serviceReference);

						return _jsImportMapsCache.register(
							companyId,
							jsImportMapsContributor.getImportMapsJSONObject(),
							jsImportMapsContributor.getScope());
					}

					@Override
					public void modifiedService(
						ServiceReference serviceReference,
						JSImportMapsRegistration jsImportMapsRegistration) {
					}

					@Override
					public void removedService(
						ServiceReference serviceReference,
						JSImportMapsRegistration jsImportMapsRegistration) {

						jsImportMapsRegistration.unregister();

						_bundleContext.ungetService(serviceReference);
					}

				};

}