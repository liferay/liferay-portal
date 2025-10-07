/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.oas;

import java.net.MalformedURLException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Matija Petanjek
 */
public class OASURLParser {

	public OASURLParser(String oasURL) throws MalformedURLException {
		Matcher matcher = oasURLPattern.matcher(oasURL);

		if (!matcher.matches()) {
			throw new MalformedURLException(
				"Unable to parse OpenAPI specification endpoint URL: " +
					oasURL);
		}

		host = matcher.group(2);
		jaxRSAppBase = matcher.group(5);

		if (matcher.group(4) == null) {
			port = "";
		}
		else {
			port = matcher.group(4);
		}

		scheme = matcher.group(1);
	}

	public String getAuthorityWithScheme() {
		StringBuilder sb = new StringBuilder();

		sb.append(scheme);
		sb.append("://");
		sb.append(host);
		sb.append(":");
		sb.append(port);

		return sb.toString();
	}

	public String getHost() {
		return host;
	}

	public String getJaxRSAppBase() {
		return jaxRSAppBase;
	}

	public String getPort() {
		return port;
	}

	public String getScheme() {
		return scheme;
	}

	public String getServerBaseURL() {
		return getServerBaseURL(jaxRSAppBase);
	}

	public String getServerBaseURL(String jaxRSAppBase) {
		StringBuilder sb = new StringBuilder();

		sb.append(getAuthorityWithScheme());
		sb.append("/o");
		sb.append(jaxRSAppBase);

		return sb.toString();
	}

	private static final Pattern oasURLPattern = Pattern.compile(
		"(.*)://(.+?)(:(\\d+))?/o(/.+)/v(.+)/openapi\\.(yaml|json)");

	private final String host;
	private final String jaxRSAppBase;
	private final String port;
	private final String scheme;

}