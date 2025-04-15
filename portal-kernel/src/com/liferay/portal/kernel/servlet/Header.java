/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Objects;

/**
 * @author Michael Young
 * @author Shuyang Zhou
 */
public class Header implements Externalizable {

	/**
	 * The empty constructor is required by {@link Externalizable}. Do not use
	 * this for any other purpose.
	 */
	public Header() {
	}

	public Header(Cookie cookieValue) {
		if (cookieValue == null) {
			throw new IllegalArgumentException("Cookie is null");
		}

		_type = Type.COOKIE;

		_cookieValue = cookieValue;
	}

	public Header(int intValue) {
		_type = Type.INTEGER;

		_intValue = intValue;
	}

	public Header(long dateValue) {
		_type = Type.DATE;

		_dateValue = dateValue;
	}

	public Header(String stringValue) {
		if (stringValue == null) {
			throw new IllegalArgumentException("String is null");
		}

		_type = Type.STRING;

		_stringValue = stringValue;
	}

	public void addToResponse(
		String key, HttpServletResponse httpServletResponse) {

		if (_type == Type.COOKIE) {
			CookiesManagerUtil.addCookie(
				_cookieValue, null, httpServletResponse);
		}
		else if (_type == Type.DATE) {
			httpServletResponse.addDateHeader(key, _dateValue);
		}
		else if (_type == Type.INTEGER) {
			httpServletResponse.addIntHeader(key, _intValue);
		}
		else if (_type == Type.STRING) {
			httpServletResponse.addHeader(key, _stringValue);
		}
		else {
			throw new IllegalStateException("Invalid type " + _type);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Header)) {
			return false;
		}

		Header header = (Header)object;

		if (_type != header._type) {
			return false;
		}

		if (_type == Type.COOKIE) {
			return _equals(_cookieValue, header._cookieValue);
		}
		else if (_type == Type.DATE) {
			if (_dateValue == header._dateValue) {
				return true;
			}

			return false;
		}
		else if (_type == Type.INTEGER) {
			if (_intValue == header._intValue) {
				return true;
			}

			return false;
		}
		else if (_type == Type.STRING) {
			return _stringValue.equals(header._stringValue);
		}

		throw new IllegalStateException("Invalid type " + _type);
	}

	@Override
	public int hashCode() {
		if (_type == Type.COOKIE) {
			return _hashCode(_cookieValue);
		}
		else if (_type == Type.DATE) {
			return (int)(_dateValue ^ (_dateValue >>> 32));
		}
		else if (_type == Type.INTEGER) {
			return _intValue;
		}
		else if (_type == Type.STRING) {
			return _stringValue.hashCode();
		}

		throw new IllegalStateException("Invalid type " + _type);
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		if (objectInput.readBoolean()) {
			int size = objectInput.readInt();

			byte[] data = new byte[size];

			objectInput.readFully(data);

			_cookieValue = CookieUtil.deserialize(data);
		}

		_dateValue = objectInput.readLong();
		_intValue = objectInput.readInt();

		String stringValue = objectInput.readUTF();

		if (!stringValue.isEmpty()) {
			_stringValue = stringValue;
		}

		_type = Type.values()[objectInput.readInt()];
	}

	public void setToResponse(
		String key, HttpServletResponse httpServletResponse) {

		if (_type == Type.COOKIE) {
			CookiesManagerUtil.addCookie(
				_cookieValue, null, httpServletResponse);
		}
		else if (_type == Type.DATE) {
			httpServletResponse.setDateHeader(key, _dateValue);
		}
		else if (_type == Type.INTEGER) {
			httpServletResponse.setIntHeader(key, _intValue);
		}
		else if (_type == Type.STRING) {
			httpServletResponse.setHeader(key, _stringValue);
		}
		else {
			throw new IllegalStateException("Invalid type " + _type);
		}
	}

	@Override
	public String toString() {
		if (_type == Type.COOKIE) {
			return CookieUtil.toString(_cookieValue);
		}
		else if (_type == Type.DATE) {
			return String.valueOf(_dateValue);
		}
		else if (_type == Type.INTEGER) {
			return String.valueOf(_intValue);
		}
		else if (_type == Type.STRING) {
			return _stringValue;
		}

		throw new IllegalStateException("Invalid type " + _type);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (_cookieValue == null) {
			objectOutput.writeBoolean(false);
		}
		else {
			objectOutput.writeBoolean(true);

			byte[] data = CookieUtil.serialize(_cookieValue);

			objectOutput.writeInt(data.length);
			objectOutput.write(data);
		}

		objectOutput.writeLong(_dateValue);
		objectOutput.writeInt(_intValue);

		if (_stringValue == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(_stringValue);
		}

		objectOutput.writeInt(_type.ordinal());
	}

	private boolean _equals(Cookie cookie1, Cookie cookie2) {
		if (cookie1 == cookie2) {
			return true;
		}

		if (!Objects.equals(cookie1.getComment(), cookie2.getComment()) ||
			!Objects.equals(cookie1.getDomain(), cookie2.getDomain()) ||
			(cookie1.getMaxAge() != cookie2.getMaxAge()) ||
			!Objects.equals(cookie1.getName(), cookie2.getName()) ||
			!Objects.equals(cookie1.getPath(), cookie2.getPath()) ||
			(cookie1.getSecure() != cookie2.getSecure()) ||
			!Objects.equals(cookie1.getValue(), cookie2.getValue()) ||
			(cookie1.getVersion() != cookie2.getVersion())) {

			return false;
		}

		return true;
	}

	private int _hashCode(Cookie cookie) {
		int hashCode = HashUtil.hash(0, cookie.getComment());

		hashCode = HashUtil.hash(hashCode, cookie.getDomain());
		hashCode = HashUtil.hash(hashCode, cookie.getMaxAge());
		hashCode = HashUtil.hash(hashCode, cookie.getName());
		hashCode = HashUtil.hash(hashCode, cookie.getPath());
		hashCode = HashUtil.hash(hashCode, cookie.getSecure());
		hashCode = HashUtil.hash(hashCode, cookie.getValue());
		hashCode = HashUtil.hash(hashCode, cookie.getVersion());

		return hashCode;
	}

	private Cookie _cookieValue;
	private long _dateValue;
	private int _intValue;
	private String _stringValue;
	private Type _type;

	private static enum Type {

		COOKIE, DATE, INTEGER, STRING

	}

}