/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.url.pattern.mapper.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 * @author Brian Wing Shun Chan
 */
public class SimpleURLPatternMapper<T> extends BaseURLPatternMapper<T> {

	public SimpleURLPatternMapper(Map<String, T> values) {
		for (Map.Entry<String, T> entry : values.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void consumeValues(Consumer<T> consumer, String urlPath) {
		if (Objects.isNull(urlPath)) {
			return;
		}

		T value = _exactURLPatternValues.get(urlPath);

		if (Objects.nonNull(value)) {
			consumer.accept(value);
		}

		value = _wildcardURLPatternValues.get(urlPath + "/*");

		if (Objects.nonNull(value)) {
			consumer.accept(value);
		}

		int index = 0;

		for (int i = urlPath.length(); i > 0; --i) {
			if ((index < 1) && (urlPath.charAt(i - 1) == '.')) {
				index = i - 1;
			}

			if (urlPath.charAt(i - 1) != '/') {
				continue;
			}

			value = _wildcardURLPatternValues.get(
				urlPath.substring(0, i) + "*");

			if (Objects.isNull(value)) {
				continue;
			}

			consumer.accept(value);
		}

		value = _extensionURLPatternValues.get("*" + urlPath.substring(index));

		if (Objects.nonNull(value)) {
			consumer.accept(value);
		}
	}

	@Override
	public T getValue(String urlPath) {
		if (Objects.isNull(urlPath)) {
			return null;
		}

		T value = _exactURLPatternValues.get(urlPath);

		if (Objects.nonNull(value)) {
			return value;
		}

		value = _wildcardURLPatternValues.get(urlPath + "/*");

		if (Objects.nonNull(value)) {
			return value;
		}

		int index = 0;

		for (int i = urlPath.length(); i > 0; --i) {
			if ((index < 1) && (urlPath.charAt(i - 1) == '.')) {
				index = i - 1;
			}

			if (urlPath.charAt(i - 1) != '/') {
				continue;
			}

			value = _wildcardURLPatternValues.get(
				urlPath.substring(0, i) + "*");

			if (Objects.nonNull(value)) {
				return value;
			}
		}

		return _extensionURLPatternValues.get("*" + urlPath.substring(index));
	}

	@Override
	protected void put(String urlPattern, T value)
		throws IllegalArgumentException {

		if (Objects.isNull(urlPattern) || (urlPattern.length() == 0)) {
			throw new IllegalArgumentException("URL pattern is blank");
		}

		if (isWildcardURLPattern(urlPattern)) {
			_wildcardURLPatternValues.put(urlPattern, value);

			return;
		}

		if (isExtensionURLPattern(urlPattern)) {
			_extensionURLPatternValues.put(urlPattern, value);

			return;
		}

		_exactURLPatternValues.put(urlPattern, value);
	}

	private final Map<String, T> _exactURLPatternValues = new HashMap<>();
	private final Map<String, T> _extensionURLPatternValues = new HashMap<>();
	private final Map<String, T> _wildcardURLPatternValues = new HashMap<>();

}