/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.introspect.message.body;

import com.liferay.oauth2.provider.rest.internal.endpoint.introspect.TokenIntrospection;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=TokenIntrospectionJSONProviderMessageBodyWriter"
	},
	service = MessageBodyWriter.class
)
@Produces("application/json")
@Provider
public class TokenIntrospectionJSONProviderMessageBodyWriter
	implements MessageBodyWriter<TokenIntrospection> {

	@Override
	public long getSize(
		TokenIntrospection tokenIntrospection, Class<?> clazz, Type genericType,
		Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return TokenIntrospection.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(
			TokenIntrospection tokenIntrospection, Class<?> clazz,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream outputStream)
		throws IOException, WebApplicationException {

		if (!tokenIntrospection.isActive()) {
			StringBundler sb = new StringBundler(6);

			sb.append("{");

			_append(sb, "active", false, false);

			sb.append("}");

			String string = sb.toString();

			outputStream.write(string.getBytes(StandardCharsets.UTF_8));

			outputStream.flush();

			return;
		}

		StringBundler sb = new StringBundler(72);

		sb.append("{");

		_append(sb, "active", tokenIntrospection.isActive(), false);

		if (tokenIntrospection.getAud() != null) {
			List<String> audience = new ArrayList<>(
				tokenIntrospection.getAud());

			audience.removeIf(String::isEmpty);

			if (!audience.isEmpty()) {
				StringBundler audienceSB;

				if (audience.size() == 1) {
					audienceSB = new StringBundler(7);

					Iterator<String> iterator = audience.iterator();

					_append(audienceSB, "aud", iterator.next());
				}
				else {
					audienceSB = new StringBundler(5);

					_append(audienceSB, "aud", audience);
				}

				sb.append(audienceSB);
			}
		}

		_append(sb, OAuthConstants.CLIENT_ID, tokenIntrospection.getClientId());

		_append(sb, "exp", tokenIntrospection.getExp());
		_append(sb, "iat", tokenIntrospection.getIat());
		_append(sb, "iss", tokenIntrospection.getIss());
		_append(sb, "jti", tokenIntrospection.getJti());
		_append(sb, "nbf", tokenIntrospection.getNbf());
		_append(sb, OAuthConstants.SCOPE, tokenIntrospection.getScope());
		_append(sb, "sub", tokenIntrospection.getSub());
		_append(
			sb, OAuthConstants.ACCESS_TOKEN_TYPE,
			tokenIntrospection.getTokenType());

		_append(sb, "username", tokenIntrospection.getUsername());

		Map<String, String> extensions = tokenIntrospection.getExtensions();

		if (MapUtil.isNotEmpty(extensions)) {
			StringBundler extensionSB = new StringBundler(
				extensions.size() * 7);

			for (Map.Entry<String, String> extension : extensions.entrySet()) {
				_append(extensionSB, extension.getKey(), extension.getValue());
			}

			sb.append(extensionSB);
		}

		sb.append("}");

		String result = sb.toString();

		outputStream.write(result.getBytes(StandardCharsets.UTF_8));

		outputStream.flush();
	}

	private void _append(StringBundler sb, String key, List<String> value) {
		StringBundler arraySB = new StringBundler(((value.size() * 3) - 1) + 2);

		arraySB.append("[");

		for (int i = 0; i < value.size(); i++) {
			if (i > 0) {
				arraySB.append(",");
			}

			_appendValue(arraySB, value.get(i), true);
		}

		arraySB.append("]");

		sb.append(",");

		_append(sb, key, arraySB.toString(), false);
	}

	private void _append(StringBundler sb, String key, Long value) {
		if (value == null) {
			return;
		}

		sb.append(",");

		_append(sb, key, value, false);
	}

	private void _append(
		StringBundler sb, String key, Object value, boolean quote) {

		sb.append("\"");
		sb.append(key);
		sb.append("\":");

		_appendValue(sb, value, quote);
	}

	private void _append(StringBundler sb, String key, String value) {
		if (value == null) {
			return;
		}

		sb.append(",");

		_append(sb, key, value, true);
	}

	private void _appendValue(StringBundler sb, Object value, boolean quote) {
		if (quote) {
			sb.append("\"");

			String stringValue;

			if (value == null) {
				stringValue = "null";
			}
			else {
				stringValue = StringUtil.replace(
					value.toString(), new String[] {"\\", "\""},
					new String[] {"\\\\", "\\\""});
			}

			sb.append(stringValue);

			sb.append("\"");
		}
		else {
			sb.append(value);
		}
	}

}