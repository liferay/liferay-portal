/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet;

import com.liferay.osb.faro.engine.client.util.EngineServiceURLUtil;
import com.liferay.osb.faro.engine.client.util.TokenUtil;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.util.FaroProjectThreadLocal;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URIBuilder;

/**
 * @author Matthew Kong
 */
public abstract class BaseAsahServlet extends HttpServlet {

	protected URI buildURI(HttpServletRequest httpServletRequest, String path)
		throws URISyntaxException {

		URIBuilder uriBuilder = new URIBuilder(
			EngineServiceURLUtil.getBackendURL(
				FaroProjectThreadLocal.getFaroProject(), path));

		Map<String, String[]> requestParameterMap =
			httpServletRequest.getParameterMap();

		requestParameterMap.forEach(
			(key, valueArray) -> {
				for (String value : valueArray) {
					uriBuilder.addParameter(key, value);
				}
			});

		return uriBuilder.build();
	}

	protected String getProjectId() {
		FaroProject faroProject = FaroProjectThreadLocal.getFaroProject();

		return faroProject.getProjectId();
	}

	protected String getSecuritySignature(URI uri) {
		String url = uri.toString();

		return DigestUtils.sha256Hex(
			TokenUtil.getOSBAsahSecurityToken() +
				url.substring(0, url.lastIndexOf(uri.getPath())));
	}

	private static final long serialVersionUID = 1L;

}