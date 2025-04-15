/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.dependencies.web.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = JSImportMapsContributor.class)
public class FrontendJSDependenciesWebJSImportMapsContributor
	implements JSImportMapsContributor {

	@Override
	public JSONObject getImportMapsJSONObject() {
		return _importMapsJSONObject;
	}

	@Activate
	protected void activate() {
		_importMapsJSONObject = _jsonFactory.createJSONObject();

		_importMapsJSONObject.put(
			"@liferay/frontend-js-api",
			_servletContext.getContextPath() +
				"/__liferay__/exports/@liferay$js-api.js"
		).put(
			"@liferay/frontend-js-api/data-set",
			_servletContext.getContextPath() +
				"/__liferay__/exports/@liferay$js-api$data-set.js"
		);
	}

	private JSONObject _importMapsJSONObject;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.frontend.js.dependencies.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}