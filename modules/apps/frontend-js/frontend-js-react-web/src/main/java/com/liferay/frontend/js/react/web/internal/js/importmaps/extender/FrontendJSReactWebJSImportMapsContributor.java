/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.react.web.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = JSImportMapsContributor.class)
public class FrontendJSReactWebJSImportMapsContributor
	implements JSImportMapsContributor {

	@Override
	public JSONObject getImportMapsJSONObject() {
		return _importMapsJSONObject;
	}

	@Activate
	protected void activate() {
		_importMapsJSONObject = _jsonFactory.createJSONObject();

		String contextPath = _servletContext.getContextPath();

		for (String moduleName : _MODULE_NAMES) {
			_importMapsJSONObject.put(
				moduleName,
				StringBundler.concat(
					contextPath, "/__liferay__/exports/",
					StringUtil.replace(
						moduleName, CharPool.FORWARD_SLASH, CharPool.DOLLAR),
					".js"));
		}
	}

	private static final String[] _MODULE_NAMES = {
		"react", "react-16", "react-18", "react-dom", "react-dom/client",
		"react-dom-16", "react-dom-18", "react-dom-18/client"
	};

	private JSONObject _importMapsJSONObject;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.js.react.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}