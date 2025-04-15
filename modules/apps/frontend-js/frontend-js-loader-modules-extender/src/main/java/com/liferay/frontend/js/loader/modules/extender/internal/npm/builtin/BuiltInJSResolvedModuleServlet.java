/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.npm.builtin;

import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry;
import com.liferay.portal.kernel.util.MimeTypes;

import jakarta.servlet.Servlet;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=Serve Package Servlet",
		"osgi.http.whiteboard.servlet.pattern=/js/resolved-module/*",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = Servlet.class
)
public class BuiltInJSResolvedModuleServlet extends BaseBuiltInJSModuleServlet {

	@Override
	protected MimeTypes getMimeTypes() {
		return _mimeTypes;
	}

	@Override
	protected ResourceDescriptor getResourceDescriptor(String pathInfo) {
		String identifier = pathInfo.substring(1);

		JSPackage jsPackage = _getJSPackage(
			ModuleNameUtil.getPackageName(identifier));

		if (jsPackage == null) {
			return null;
		}

		return new ResourceDescriptor(
			jsPackage, ModuleNameUtil.getPackagePath(identifier));
	}

	private JSPackage _getJSPackage(String packageName) {
		String jsPackageId = _jsPackageIdsCache.get(packageName);

		if (jsPackageId != null) {
			JSPackage jsPackage = _npmRegistry.getJSPackage(jsPackageId);

			if (jsPackage != null) {
				return jsPackage;
			}

			_jsPackageIdsCache.remove(packageName);
		}

		Collection<JSPackage> jsPackages = _npmRegistry.getResolvedJSPackages();

		for (JSPackage jsPackage : jsPackages) {
			if (packageName.equals(jsPackage.getResolvedId())) {
				_jsPackageIdsCache.put(packageName, jsPackage.getId());

				return jsPackage;
			}
		}

		return null;
	}

	private static final long serialVersionUID = 2647715401054034600L;

	private final LinkedHashMap<String, String> _jsPackageIdsCache =
		new LinkedHashMap<String, String>() {

			@Override
			protected boolean removeEldestEntry(Map.Entry eldest) {
				Collection<JSPackage> jsPackages =
					_npmRegistry.getResolvedJSPackages();

				if (size() > jsPackages.size()) {
					return true;
				}

				return false;
			}

		};

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private NPMRegistry _npmRegistry;

}