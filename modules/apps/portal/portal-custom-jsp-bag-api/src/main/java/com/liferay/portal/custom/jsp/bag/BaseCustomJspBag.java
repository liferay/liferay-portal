/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.custom.jsp.bag;

import com.liferay.portal.deploy.hot.CustomJspBag;
import com.liferay.portal.kernel.url.URLContainer;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Jenny Chen
 * @author Brian Wing Shun Chan
 */
public abstract class BaseCustomJspBag implements CustomJspBag {

	@Override
	public String getCustomJspDir() {
		return "META-INF/resources/custom_jsps";
	}

	@Override
	public List<String> getCustomJsps() {
		return _customJsps;
	}

	@Override
	public URLContainer getURLContainer() {
		return _urlContainer;
	}

	@Override
	public boolean isCustomJspGlobal() {
		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();

		_customJsps = new ArrayList<>();

		Enumeration<URL> enumeration = _bundle.findEntries(
			getCustomJspDir(), "*.jsp", true);

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			_customJsps.add(url.getPath());
		}
	}

	private Bundle _bundle;
	private List<String> _customJsps;

	private final URLContainer _urlContainer = new URLContainer() {

		@Override
		public URL getResource(String name) {
			return _bundle.getEntry(name);
		}

		@Override
		public Set<String> getResources(String path) {
			Set<String> paths = new HashSet<>();

			for (String customJsp : _customJsps) {
				if (!customJsp.startsWith(path)) {
					continue;
				}

				paths.add(customJsp);
			}

			return paths;
		}

	};

}