/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.Deserializer;
import com.liferay.portal.kernel.io.Serializer;

import jakarta.servlet.http.Cookie;

import java.nio.ByteBuffer;

import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class CookieUtil {

	public static Cookie deserialize(byte[] bytes) {
		Deserializer deserializer = new Deserializer(ByteBuffer.wrap(bytes));

		String domain = deserializer.readString();
		boolean httpOnly = deserializer.readBoolean();
		int maxAge = deserializer.readInt();
		String name = deserializer.readString();
		String path = deserializer.readString();
		boolean secure = deserializer.readBoolean();

		String value = deserializer.readString();

		if (value.isEmpty()) {
			value = null;
		}

		Cookie cookie = new Cookie(name, value);

		if (!domain.isEmpty()) {
			cookie.setDomain(domain);
		}

		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(maxAge);

		if (!path.isEmpty()) {
			cookie.setPath(path);
		}

		cookie.setSecure(secure);

		return cookie;
	}

	public static boolean equals(Cookie cookie1, Cookie cookie2) {
		if (!Objects.equals(cookie1.getDomain(), cookie2.getDomain()) ||
			(cookie1.getMaxAge() != cookie2.getMaxAge()) ||
			!Objects.equals(cookie1.getName(), cookie2.getName()) ||
			!Objects.equals(cookie1.getPath(), cookie2.getPath()) ||
			(cookie1.getSecure() != cookie2.getSecure()) ||
			!Objects.equals(cookie1.getValue(), cookie2.getValue()) ||
			(cookie1.isHttpOnly() != cookie2.isHttpOnly())) {

			return false;
		}

		return true;
	}

	public static byte[] serialize(Cookie cookie) {
		Serializer serializer = new Serializer();

		String domain = cookie.getDomain();

		if (domain == null) {
			domain = StringPool.BLANK;
		}

		serializer.writeString(domain);

		serializer.writeBoolean(cookie.isHttpOnly());
		serializer.writeInt(cookie.getMaxAge());
		serializer.writeString(cookie.getName());

		String path = cookie.getPath();

		if (path == null) {
			path = StringPool.BLANK;
		}

		serializer.writeString(path);

		serializer.writeBoolean(cookie.getSecure());

		String value = cookie.getValue();

		if (value == null) {
			value = StringPool.BLANK;
		}

		serializer.writeString(value);

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		return byteBuffer.array();
	}

	public static String toString(Cookie cookie) {
		return StringBundler.concat(
			"{domain=", cookie.getDomain(), ", httpOnly=", cookie.isHttpOnly(),
			", maxAge=", cookie.getMaxAge(), ", name=", cookie.getName(),
			", path=", cookie.getPath(), ", secure=", cookie.getSecure(),
			", value=", cookie.getValue(), "}");
	}

}