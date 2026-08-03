/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;

import com.liferay.osb.faro.engine.client.model.Credentials;
import com.liferay.osb.faro.engine.client.model.Interest;
import com.liferay.osb.faro.engine.client.model.Metric;
import com.liferay.osb.faro.engine.client.model.PageVisited;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth1Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.OAuth2Credentials;
import com.liferay.osb.faro.engine.client.model.credentials.TokenCredentials;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.BaseMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.CredentialsMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.MetricMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.OAuth1CredentialsMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.OAuth2CredentialsMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.PageVisitMixin;
import com.liferay.osb.faro.web.internal.model.display.contacts.mixin.TokenCredentialsMixin;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Type;

/**
 * @author Matthew Kong
 */
public class JSONUtil {

	public static <T> T convertValue(Object object, Class<T> clazz) {
		return _objectMapper.convertValue(object, clazz);
	}

	public static <T> T convertValue(
		Object object, TypeReference<T> typeReference) {

		return _objectMapper.convertValue(object, typeReference);
	}

	public static ObjectMapper getObjectMapper() {
		return _objectMapper;
	}

	public static <T> T readContainedTypeValue(String content, Type type)
		throws Exception {

		JavaType javaType = _objectMapper.constructType(type);

		javaType = javaType.containedType(0);

		return _objectMapper.readValue(content, javaType);
	}

	public static <T> T readValue(String content, Class<T> valueType)
		throws Exception {

		return _objectMapper.readValue(content, valueType);
	}

	public static <T> T readValue(
			String content, TypeReference<T> typeReference)
		throws Exception {

		return _objectMapper.readValue(content, typeReference);
	}

	public static String writeValueAsString(Object value) throws Exception {
		return _objectMapper.writeValueAsString(value);
	}

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			addMixIn(Credentials.class, CredentialsMixin.class);
			addMixIn(Interest.class, BaseMixin.class);
			addMixIn(Metric.class, MetricMixin.class);
			addMixIn(OAuth1Credentials.class, OAuth1CredentialsMixin.class);
			addMixIn(OAuth2Credentials.class, OAuth2CredentialsMixin.class);
			addMixIn(PageVisited.class, PageVisitMixin.class);
			addMixIn(TokenCredentials.class, TokenCredentialsMixin.class);
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			setPropertyNamingStrategy(
				new PropertyNamingStrategy() {

					@Override
					public String nameForField(
						MapperConfig<?> config, AnnotatedField field,
						String defaultName) {

						if (defaultName.startsWith(StringPool.UNDERLINE)) {
							defaultName = defaultName.substring(1);
						}

						defaultName = StringUtil.removeSubstring(
							defaultName, "Display");

						return super.nameForField(config, field, defaultName);
					}

				});
			setVisibility(
				PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		}
	};

}