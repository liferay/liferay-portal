/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.security.key.KeyReference;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;

import javax.security.auth.Destroyable;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
public final class Secret implements AutoCloseable, Destroyable {

	public Secret(byte[] bytes, KeyReference keyReference) {
		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		if (bytes == null) {
			_bytes = new byte[0];
		}
		else {
			_bytes = Arrays.copyOf(bytes, bytes.length);
		}

		_keyReference = keyReference;
	}

	public Secret(char[] chars, KeyReference keyReference) {
		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		if (chars != null) {
			_bytes = _encode(chars);
		}
		else {
			_bytes = new byte[0];
		}

		_keyReference = keyReference;
	}

	public Secret(KeyReference keyReference, String value) {
		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		_keyReference = keyReference;

		if (value == null) {
			_bytes = new byte[0];

			return;
		}

		char[] chars = value.toCharArray();

		try {
			_bytes = _encode(chars);
		}
		finally {
			Arrays.fill(chars, '\0');
		}
	}

	@Override
	public void close() {
		destroy();
	}

	@Override
	public synchronized void destroy() {
		if (_bytes != null) {
			Arrays.fill(_bytes, (byte)0);
		}

		if (_chars != null) {
			Arrays.fill(_chars, '\0');
		}

		_destroyed = true;
	}

	public synchronized byte[] getBytes() {
		if (_destroyed) {
			throw new IllegalStateException("Secret is destroyed");
		}

		return _bytes;
	}

	public synchronized char[] getChars() {
		if (_destroyed) {
			throw new IllegalStateException("Secret is destroyed");
		}

		if (_chars == null) {
			_chars = _decode(_bytes);
		}

		return _chars;
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	@Override
	public synchronized boolean isDestroyed() {
		return _destroyed;
	}

	private char[] _decode(byte[] bytes) {
		CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();

		char[] tempChars = new char
			[(int)Math.ceil(bytes.length * charsetDecoder.maxCharsPerByte())];

		try {
			CharBuffer charBuffer = CharBuffer.wrap(tempChars);

			CoderResult coderResult = charsetDecoder.decode(
				ByteBuffer.wrap(bytes), charBuffer, true);

			if (coderResult.isError()) {
				coderResult.throwException();
			}

			coderResult = charsetDecoder.flush(charBuffer);

			if (coderResult.isError()) {
				coderResult.throwException();
			}

			charBuffer.flip();

			char[] chars = new char[charBuffer.remaining()];

			charBuffer.get(chars);

			return chars;
		}
		catch (CharacterCodingException characterCodingException) {
			throw new IllegalArgumentException(
				"Stored secret is not valid UTF-8", characterCodingException);
		}
	}

	private byte[] _encode(char[] chars) {
		CharsetEncoder charsetEncoder = StandardCharsets.UTF_8.newEncoder();

		byte[] tempBytes = new byte
			[(int)Math.ceil(chars.length * charsetEncoder.maxBytesPerChar())];

		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(tempBytes);

			CoderResult coderResult = charsetEncoder.encode(
				CharBuffer.wrap(chars), byteBuffer, true);

			if (coderResult.isError()) {
				coderResult.throwException();
			}

			coderResult = charsetEncoder.flush(byteBuffer);

			if (coderResult.isError()) {
				coderResult.throwException();
			}

			byteBuffer.flip();

			byte[] bytes = new byte[byteBuffer.remaining()];

			byteBuffer.get(bytes);

			return bytes;
		}
		catch (CharacterCodingException characterCodingException) {
			throw new IllegalArgumentException(
				"Input character sequence is not valid UTF-16",
				characterCodingException);
		}
	}

	private final byte[] _bytes;
	private char[] _chars;
	private boolean _destroyed;
	private final KeyReference _keyReference;

}