/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.js.importmaps.extender;

import com.liferay.frontend.js.importmaps.extender.JSImportMapsContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = JSImportMapsContributor.class)
public class FragmentImplJSImportMapsContributor
	implements JSImportMapsContributor {

	@Override
	public JSONObject getImportMapsJSONObject() {
		return _importMapsJSONObject;
	}

	@Activate
	protected void activate() {
		_importMapsJSONObject = JSONUtil.put(
			"@liferay/fragment-impl",
			_servletContext.getContextPath() + "/__liferay__/index.js");
	}

	private JSONObject _importMapsJSONObject;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.impl)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}