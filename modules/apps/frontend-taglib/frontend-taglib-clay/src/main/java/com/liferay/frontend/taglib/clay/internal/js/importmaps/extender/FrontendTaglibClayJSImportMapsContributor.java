/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = JSImportMapsContributor.class)
public class FrontendTaglibClayJSImportMapsContributor
	implements JSImportMapsContributor {

	@Override
	public JSONObject getImportMapsJSONObject() {
		return _importMapsJSONObject;
	}

	@Activate
	protected void activate() throws IOException, JSONException {
		_importMapsJSONObject = _jsonFactory.createJSONObject();

		JSONObject packageJSONObject = _getPackageJSONObject();

		JSONObject dependenciesJSONObject = packageJSONObject.getJSONObject(
			"dependencies");

		for (String moduleName : dependenciesJSONObject.keySet()) {
			if (!moduleName.startsWith("@clayui/")) {
				continue;
			}

			_importMapsJSONObject.put(
				moduleName,
				StringBundler.concat(
					_servletContext.getContextPath(), "/__liferay__/exports/",
					moduleName.replaceAll("\\/", "\\$"), ".js"));
		}
	}

	private JSONObject _getPackageJSONObject()
		throws IOException, JSONException {

		try (InputStream inputStream =
				FrontendTaglibClayJSImportMapsContributor.class.
					getResourceAsStream("dependencies/package.json")) {

			return _jsonFactory.createJSONObject(StringUtil.read(inputStream));
		}
	}

	private JSONObject _importMapsJSONObject;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.taglib.clay)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}