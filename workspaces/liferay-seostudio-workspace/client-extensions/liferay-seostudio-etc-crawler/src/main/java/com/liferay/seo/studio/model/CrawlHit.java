/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brooke Dalton
 */
public class CrawlHit {

	public CrawlHit(JSONObject jsonObject) {
		_canonicalURL = jsonObject.optString("canonicalUrl", null);

		JSONArray linksJSONArray = jsonObject.optJSONArray("links");

		if (linksJSONArray != null) {
			for (Object object : linksJSONArray) {
				if (object instanceof String) {
					_links.add((String)object);
				}
			}
		}

		_url = jsonObject.optString("url", null);
	}

	public String getCanonicalURL() {
		return _canonicalURL;
	}

	public List<String> getLinks() {
		return _links;
	}

	public String getURL() {
		return _url;
	}

	private final String _canonicalURL;
	private final List<String> _links = new ArrayList<>();
	private final String _url;

}