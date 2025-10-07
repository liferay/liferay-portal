/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public abstract class BaseGitRepository implements GitRepository {

	@Override
	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	@Override
	public String getName() {
		return getString("name");
	}

	@Override
	public boolean isSubrepository() {
		String name = getName();

		return name.startsWith("com-liferay-");
	}

	protected BaseGitRepository(JSONObject jsonObject) {
		_jsonObject = jsonObject;

		validateKeys(_KEYS_REQUIRED);
	}

	protected BaseGitRepository(String name) {
		_jsonObject = new JSONObject();

		_setName(name);

		validateKeys(_KEYS_REQUIRED);
	}

	protected boolean getBoolean(String key) {
		return _jsonObject.optBoolean(key);
	}

	protected File getFile(String key) {
		return new File(getString(key));
	}

	protected JSONArray getJSONArray(String key) {
		return _jsonObject.getJSONArray(key);
	}

	protected String getString(String key) {
		return _jsonObject.getString(key);
	}

	protected boolean has(String key) {
		return _jsonObject.has(key);
	}

	protected String optString(String key) {
		return _jsonObject.optString(key);
	}

	protected String optString(String key, String defaultValue) {
		return _jsonObject.optString(key, defaultValue);
	}

	protected void put(String key, Object value) {
		_jsonObject.put(key, value);
	}

	protected void validateKeys(String[] requiredKeys) {
		for (String requiredKey : requiredKeys) {
			if (!has(requiredKey)) {
				throw new RuntimeException("Missing " + requiredKey);
			}
		}
	}

	private void _setName(String name) {
		if ((name == null) || name.isEmpty()) {
			throw new IllegalArgumentException("Name is null");
		}

		put("name", name);
	}

	private static final String[] _KEYS_REQUIRED = {"name"};

	private final JSONObject _jsonObject;

}