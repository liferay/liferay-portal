/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.url.pattern.mapper.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 */
public class StaticSizeTrieURLPatternMapper<T>
	extends BaseTrieURLPatternMapper<T> {

	public StaticSizeTrieURLPatternMapper(Map<String, T> values) {
		int maxURLPatternLength = 0;

		for (Map.Entry<String, T> entry : values.entrySet()) {
			String urlPattern = entry.getKey();

			if (Objects.isNull(urlPattern) || (urlPattern.length() == 0)) {
				throw new IllegalArgumentException("URL pattern is blank");
			}

			if (urlPattern.length() <= maxURLPatternLength) {
				continue;
			}

			maxURLPatternLength = urlPattern.length();
		}

		_maxURLPatternLength = maxURLPatternLength + 2;

		_extensionTrieMatrix =
			new long[2][_maxURLPatternLength][ASCII_CHARACTER_RANGE];
		_wildCardTrieMatrix =
			new long[2][_maxURLPatternLength][ASCII_CHARACTER_RANGE];

		for (Map.Entry<String, T> entry : values.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	protected void consumeWildcardValues(Consumer<T> consumer, String urlPath) {
		long valuesBitmask = _getWildcardValuesBitmask(urlPath);

		while (valuesBitmask != 0) {
			consumer.accept(
				_wildcardValues.get(_getFirstSetBitIndex(valuesBitmask)));

			valuesBitmask &= valuesBitmask - 1;
		}
	}

	@Override
	protected T getExtensionValue(String urlPath) {
		long currentBitmask = _BITMASK;
		int maxRow = Math.min(urlPath.length(), _maxURLPatternLength - 1);

		for (int row = 0; row < maxRow; ++row) {
			char character = urlPath.charAt(urlPath.length() - 1 - row);

			if (character == '/') {
				break;
			}

			int column = character - ASCII_PRINTABLE_OFFSET;

			currentBitmask &= _extensionTrieMatrix[0][row][column];

			if (currentBitmask == 0) {
				break;
			}

			if (character == '.') {
				return _extensionValues.get(
					_getFirstSetBitIndex(currentBitmask));
			}
		}

		return null;
	}

	@Override
	protected T getWildcardValue(String urlPath) {
		boolean exact = false;
		boolean wildcard = false;

		if (urlPath.charAt(0) != '/') {
			exact = true;
		}
		else if ((urlPath.length() > 1) &&
				 (urlPath.charAt(urlPath.length() - 2) == '/') &&
				 (urlPath.charAt(urlPath.length() - 1) == '*')) {

			wildcard = true;
		}

		long bestMatchBitmask = 0;
		int column = 0;
		long currentBitmask = _BITMASK;
		int maxRow = Math.min(urlPath.length(), _maxURLPatternLength - 1);
		int row = 0;

		for (; row < maxRow; ++row) {
			char character = urlPath.charAt(row);

			column = character - ASCII_PRINTABLE_OFFSET;

			currentBitmask &= _wildCardTrieMatrix[0][row][column];

			if (currentBitmask == 0) {
				break;
			}

			if (exact || (character != '/')) {
				continue;
			}

			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row + 1][_INDEX_STAR];

			if (bitmask == 0) {
				continue;
			}

			bestMatchBitmask = bitmask;
		}

		if (currentBitmask == 0) {
			if (bestMatchBitmask == 0) {
				return null;
			}

			return _wildcardValues.get(_getFirstSetBitIndex(bestMatchBitmask));
		}

		if (exact) {
			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row - 1][column];

			if (bitmask != 0) {
				return _wildcardValues.get(_getFirstSetBitIndex(bitmask));
			}

			return null;
		}

		if (!wildcard) {
			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row - 1][column];

			if (bitmask != 0) {
				return _wildcardValues.get(_getFirstSetBitIndex(bitmask));
			}
		}

		long extraBitmask =
			currentBitmask & _wildCardTrieMatrix[0][row][_INDEX_SLASH];

		extraBitmask &= _wildCardTrieMatrix[0][row + 1][_INDEX_STAR];
		extraBitmask &= _wildCardTrieMatrix[1][row + 1][_INDEX_STAR];

		if (extraBitmask != 0) {
			return _wildcardValues.get(_getFirstSetBitIndex(extraBitmask));
		}

		if (bestMatchBitmask == 0) {
			return null;
		}

		return _wildcardValues.get(_getFirstSetBitIndex(bestMatchBitmask));
	}

	@Override
	protected void put(String urlPattern, T value, boolean wildcard) {
		List<T> values = null;
		long[][][] trieMatrix = null;

		if (wildcard) {
			values = _wildcardValues;
			trieMatrix = _wildCardTrieMatrix;
		}
		else {
			values = _extensionValues;
			trieMatrix = _extensionTrieMatrix;
		}

		if ((!wildcard && (_extensionURLPatternsCount > 63)) ||
			(wildcard && (_wildcardURLPatternCount > 63))) {

			throw new IllegalArgumentException(
				"Exceeding maximum number of allowed URL patterns");
		}

		int index = 0;

		if (wildcard) {
			index = _wildcardURLPatternCount++;
		}
		else {
			index = _extensionURLPatternsCount++;
		}

		long bitmask = 1L << index;
		int column = 0;
		int row = 0;

		for (; row < urlPattern.length(); ++row) {
			char character;

			if (wildcard) {
				character = urlPattern.charAt(row);
			}
			else {
				character = urlPattern.charAt(urlPattern.length() - 1 - row);
			}

			column = character - ASCII_PRINTABLE_OFFSET;

			trieMatrix[0][row][column] |= bitmask;
		}

		trieMatrix[1][row - 1][column] |= bitmask;

		values.add(value);
	}

	private int _getFirstSetBitIndex(long bitmask) {
		int firstSetBitIndex = 63;

		long currentBitmask = bitmask << 32;

		if (currentBitmask != 0) {
			bitmask = currentBitmask;
			firstSetBitIndex -= 32;
		}

		currentBitmask = bitmask << 16;

		if (currentBitmask != 0) {
			bitmask = currentBitmask;
			firstSetBitIndex -= 16;
		}

		currentBitmask = bitmask << 8;

		if (currentBitmask != 0) {
			bitmask = currentBitmask;
			firstSetBitIndex -= 8;
		}

		currentBitmask = bitmask << 4;

		if (currentBitmask != 0) {
			bitmask = currentBitmask;
			firstSetBitIndex -= 4;
		}

		currentBitmask = bitmask << 2;

		if (currentBitmask != 0) {
			bitmask = currentBitmask;
			firstSetBitIndex -= 2;
		}

		currentBitmask = bitmask << 1;

		if (currentBitmask != 0) {
			firstSetBitIndex -= 1;
		}

		return firstSetBitIndex;
	}

	private long _getWildcardValuesBitmask(String urlPath) {
		long valuesBitmask = 0;

		boolean exact = false;
		boolean wildcard = false;

		if (urlPath.charAt(0) != '/') {
			exact = true;
		}
		else if ((urlPath.length() > 1) &&
				 (urlPath.charAt(urlPath.length() - 2) == '/') &&
				 (urlPath.charAt(urlPath.length() - 1) == '*')) {

			wildcard = true;
		}

		int column = 0;
		long currentBitmask = _BITMASK;
		int maxRow = Math.min(urlPath.length(), _maxURLPatternLength - 1);
		int row = 0;

		for (; row < maxRow; ++row) {
			char character = urlPath.charAt(row);

			column = character - ASCII_PRINTABLE_OFFSET;

			currentBitmask &= _wildCardTrieMatrix[0][row][column];

			if (currentBitmask == 0) {
				break;
			}

			if (exact || (character != '/')) {
				continue;
			}

			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row + 1][_INDEX_STAR];

			if (bitmask == 0) {
				continue;
			}

			valuesBitmask |= bitmask;
		}

		if (currentBitmask == 0) {
			return valuesBitmask;
		}

		if (exact) {
			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row - 1][column];

			if (bitmask != 0) {
				valuesBitmask |= bitmask;
			}

			return valuesBitmask;
		}

		if (!wildcard) {
			long bitmask =
				currentBitmask & _wildCardTrieMatrix[1][row - 1][column];

			if (bitmask != 0) {
				valuesBitmask |= bitmask;
			}
		}

		long extraBitmask =
			currentBitmask & _wildCardTrieMatrix[0][row][_INDEX_SLASH];

		extraBitmask &= _wildCardTrieMatrix[0][row + 1][_INDEX_STAR];
		extraBitmask &= _wildCardTrieMatrix[1][row + 1][_INDEX_STAR];

		if (extraBitmask != 0) {
			valuesBitmask |= extraBitmask;
		}

		return valuesBitmask;
	}

	private static final long _BITMASK = ~0;

	private static final int _INDEX_SLASH = '/' - ASCII_PRINTABLE_OFFSET;

	private static final int _INDEX_STAR = '*' - ASCII_PRINTABLE_OFFSET;

	private final long[][][] _extensionTrieMatrix;
	private int _extensionURLPatternsCount;
	private final List<T> _extensionValues = new ArrayList<>(Long.SIZE);
	private final int _maxURLPatternLength;
	private final long[][][] _wildCardTrieMatrix;
	private int _wildcardURLPatternCount;
	private final List<T> _wildcardValues = new ArrayList<>(Long.SIZE);

}