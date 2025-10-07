/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.logging;

import com.liferay.portal.test.log.LoggerTestUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.util.logging.Level;

/**
 * @author Matthew Tambara
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectedLog {

	public Class<?> expectedClass();

	public Level expectedLevel();

	public String expectedLog();

	public enum Level {

		FINE(LoggerTestUtil.DEBUG), FINEST(LoggerTestUtil.TRACE),
		INFO(LoggerTestUtil.INFO), WARNING(LoggerTestUtil.WARN);

		public String getName() {
			return _name;
		}

		private Level(String name) {
			_name = name;
		}

		private final String _name;

	}

}