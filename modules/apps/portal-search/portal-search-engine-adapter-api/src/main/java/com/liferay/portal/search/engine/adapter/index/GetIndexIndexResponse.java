/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.index;

import java.util.Map;

/**
 * @author Michael C. Han
 */
public class GetIndexIndexResponse implements IndexResponse {

	public Map<String, String> getIndexMappings() {
		return _indexMappings;
	}

	public String[] getIndexNames() {
		return _indexNames;
	}

	public Map<String, String> getSettings() {
		return _settings;
	}

	public void setIndexMappings(Map<String, String> indexMappings) {
		_indexMappings = indexMappings;
	}

	public void setIndexNames(String[] indexNames) {
		_indexNames = indexNames;
	}

	public void setSettings(Map<String, String> settings) {
		_settings = settings;
	}

	private Map<String, String> _indexMappings;
	private String[] _indexNames;
	private Map<String, String> _settings;

}