/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.npm.builtin;

import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.MimeTypes;

import jakarta.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=Serve Package Servlet",
		"osgi.http.whiteboard.servlet.pattern=/js/module/*",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = Servlet.class
)
public class BuiltInJSModuleServlet extends BaseBuiltInJSModuleServlet {

	@Override
	protected MimeTypes getMimeTypes() {
		return _mimeTypes;
	}

	@Override
	protected ResourceDescriptor getResourceDescriptor(String pathInfo) {
		String identifier = pathInfo.substring(1);

		int i = identifier.indexOf(StringPool.SLASH);

		if (i == -1) {
			return null;
		}

		String bundleId = identifier.substring(0, i);

		identifier = identifier.substring(i + 1);

		String packageName = ModuleNameUtil.getPackageName(identifier);

		JSPackage jsPackage = _npmRegistry.getJSPackage(
			bundleId + StringPool.SLASH + packageName);

		if (jsPackage == null) {
			return null;
		}

		return new ResourceDescriptor(
			jsPackage, ModuleNameUtil.getPackagePath(identifier));
	}

	private static final long serialVersionUID = -8753225208295935344L;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private NPMRegistry _npmRegistry;

}