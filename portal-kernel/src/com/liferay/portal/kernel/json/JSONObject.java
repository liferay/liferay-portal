/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.json;

import com.liferay.petra.function.UnsafeSupplier;

import java.io.Externalizable;
import java.io.Writer;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface JSONObject extends Externalizable, JSONSerializable {

	public Object get(String key);

	public boolean getBoolean(String key);

	public boolean getBoolean(String key, boolean defaultValue);

	public double getDouble(String key);

	public double getDouble(String key, double defaultValue);

	public int getInt(String key);

	public int getInt(String key, int defaultValue);

	public JSONArray getJSONArray(String key);

	public JSONObject getJSONObject(String key);

	public long getLong(String key);

	public long getLong(String key, long defaultValue);

	public String getString(String key);

	public String getString(String key, String defaultValue);

	public boolean has(String key);

	public boolean isNull(String key);

	public Set<String> keySet();

	public Iterator<String> keys();

	public int length();

	public JSONArray names();

	public Object opt(String key);

	public JSONObject put(String key, boolean value);

	public JSONObject put(String key, Date value);

	public JSONObject put(String key, double value);

	public JSONObject put(String key, int value);

	public JSONObject put(String key, JSONArray jsonArray);

	public JSONObject put(String key, JSONObject jsonObject);

	public JSONObject put(String key, long value);

	public JSONObject put(String key, Object value);

	public JSONObject put(String key, String value);

	public JSONObject put(
		String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	public JSONObject putException(Exception exception);

	public Object remove(String key);

	public Map<String, Object> toMap();

	@Override
	public String toString();

	public String toString(int indentFactor) throws JSONException;

	public Writer write(Writer writer) throws JSONException;

}