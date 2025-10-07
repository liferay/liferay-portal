/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface JSONFactory {

	public String convertJSONMLArrayToXML(String jsonml);

	public String convertJSONMLObjectToXML(String jsonml);

	public String convertXMLtoJSONMLArray(String xml);

	public String convertXMLtoJSONMLObject(String xml);

	public JSONTransformer createJavaScriptNormalizerJSONTransformer(
		List<String> javaScriptAttributes);

	public JSONArray createJSONArray();

	public JSONArray createJSONArray(Collection<?> collection);

	public JSONArray createJSONArray(String json) throws JSONException;

	public <T> JSONArray createJSONArray(T[] array);

	public <T> JSONDeserializer<T> createJSONDeserializer();

	public JSONObject createJSONObject();

	public JSONObject createJSONObject(Map<?, ?> map);

	public JSONObject createJSONObject(String json) throws JSONException;

	public JSONSerializer createJSONSerializer();

	public Object deserialize(JSONObject jsonObject);

	public Object deserialize(String json);

	public String getNullJSON();

	public JSONObject getUnmodifiableJSONObject();

	public Object looseDeserialize(String json);

	public <T> T looseDeserialize(String json, Class<T> clazz);

	public String looseSerialize(Object object);

	public String looseSerialize(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz);

	public String looseSerialize(Object object, String... includes);

	public String looseSerializeDeep(Object object);

	public String looseSerializeDeep(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz);

	public JSONObject safeCreateJSONObject(String json);

	public JSONObject safeCreateJSONObject(String json, boolean strict);

	public String serialize(Object object);

	public String serializeThrowable(Throwable throwable);

	public String toString(JSONObject jsonObject);

}