/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class UnmodifiableJSONObjectImpl extends JSONObjectImpl {

	@Override
	public Set<String> keySet() {
		return Collections.emptySet();
	}

	@Override
	public Iterator<String> keys() {
		List<String> list = Collections.emptyList();

		return list.iterator();
	}

	@Override
	public JSONObject put(String key, boolean value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, Date value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, double value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, int value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, JSONArray jsonArray) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, JSONObject jsonObject) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, long value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject put(String key, String value) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public JSONObject putException(Exception exception) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return this;
	}

	@Override
	public Object remove(String key) {
		if (_log.isWarnEnabled()) {
			_log.warn("Modifications are unsupported");
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnmodifiableJSONObjectImpl.class);

}