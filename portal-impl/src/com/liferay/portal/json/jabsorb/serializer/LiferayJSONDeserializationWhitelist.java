/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.jabsorb.serializer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Closeable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tomas Polesovsky
 */
public class LiferayJSONDeserializationWhitelist {

	public LiferayJSONDeserializationWhitelist() {
		for (String className :
				PropsUtil.getArray(
					PropsKeys.JSON_DESERIALIZATION_WHITELIST_CLASS_NAMES)) {

			_registeredClassNames.put(className, new AtomicInteger(1));
		}
	}

	public boolean isWhitelisted(String className) {
		if (_registeredClassNames.containsKey(className)) {
			return true;
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to deserialize ", className,
					" due to security restrictions"));
		}

		return false;
	}

	public Closeable register(String... classNames) {
		for (String className : classNames) {
			_registeredClassNames.compute(
				className,
				(keyClassName, counter) -> {
					if (counter == null) {
						return new AtomicInteger(1);
					}

					counter.incrementAndGet();

					return counter;
				});
		}

		return new Closeable() {

			@Override
			public void close() {
				if (!_closed.compareAndSet(false, true)) {
					return;
				}

				for (String className : classNames) {
					_registeredClassNames.compute(
						className,
						(keyClassName, counter) -> {
							if (counter.decrementAndGet() == 0) {
								return null;
							}

							return counter;
						});
				}
			}

			private final AtomicBoolean _closed = new AtomicBoolean();

		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayJSONDeserializationWhitelist.class);

	private final Map<String, AtomicInteger> _registeredClassNames =
		new ConcurrentHashMap<>();

}