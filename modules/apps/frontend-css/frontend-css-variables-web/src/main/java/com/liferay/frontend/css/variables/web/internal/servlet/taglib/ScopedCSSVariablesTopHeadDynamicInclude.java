/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.css.variables.web.internal.servlet.taglib;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.css.variables.ScopedCSSVariablesProvider;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = DynamicInclude.class)
public class ScopedCSSVariablesTopHeadDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String dynamicIncludeKey)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<style data-senna-track=\"temporary\"");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" type=\"text/css\">\n");

		for (ScopedCSSVariablesProvider scopedCSSVariablesProvider :
				_scopedCSSVariablesProviders) {

			_writeCSSVariables(
				printWriter,
				scopedCSSVariablesProvider.getScopedCSSVariablesCollection(
					httpServletRequest));
		}

		printWriter.print("</style>\n");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_scopedCssVariablesProviderServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, ScopedCSSVariablesProvider.class,
				new PropertyServiceReferenceComparator<>("service.ranking"));

		setScopedCSSVariablesProviders(
			_scopedCssVariablesProviderServiceTrackerList);
	}

	@Deactivate
	protected void deactivate() {
		setScopedCSSVariablesProviders(null);

		_scopedCssVariablesProviderServiceTrackerList.close();

		_scopedCssVariablesProviderServiceTrackerList = null;
	}

	protected void setScopedCSSVariablesProviders(
		Iterable<ScopedCSSVariablesProvider> scopedCSSVariablesProviders) {

		_scopedCSSVariablesProviders = scopedCSSVariablesProviders;
	}

	private void _writeCSSVariables(
		PrintWriter printWriter,
		Collection<ScopedCSSVariables> scopedCSSVariablesCollection) {

		for (ScopedCSSVariables scopedCSSVariables :
				scopedCSSVariablesCollection) {

			printWriter.print(StringPool.TAB);
			printWriter.print(scopedCSSVariables.getScope());
			printWriter.print(" {\n");

			Map<String, String> cssVariables =
				scopedCSSVariables.getCSSVariables();

			for (Map.Entry<String, String> entry : cssVariables.entrySet()) {
				printWriter.print("\t\t--");
				printWriter.print(entry.getKey());
				printWriter.print(": ");
				printWriter.print(entry.getValue());
				printWriter.print(";\n");
			}

			printWriter.print("\t}\n");
		}
	}

	private Iterable<ScopedCSSVariablesProvider> _scopedCSSVariablesProviders;
	private ServiceTrackerList<ScopedCSSVariablesProvider>
		_scopedCssVariablesProviderServiceTrackerList;

}