/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.cache;

import com.liferay.osb.faro.engine.client.FaroClientHttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shinn Lok
 */
public class FaroCache {

	public synchronized void clear() {
		_faroClientHttpResponses.clear();
	}

	public synchronized FaroClientHttpResponse get(String key) {
		return _faroClientHttpResponses.get(key);
	}

	public synchronized void put(
		String key, FaroClientHttpResponse faroClientHttpResponse) {

		_faroClientHttpResponses.put(key, faroClientHttpResponse);
	}

	private final Map<String, FaroClientHttpResponse> _faroClientHttpResponses =
		new HashMap<>();

}