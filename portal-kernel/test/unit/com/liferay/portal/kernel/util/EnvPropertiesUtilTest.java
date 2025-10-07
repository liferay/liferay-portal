/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class EnvPropertiesUtilTest {

	@Test
	public void testDecode() {

		// Nothing to decode

		Assert.assertEquals("abcDEF", EnvPropertiesUtil.decode("abcDEF"));

		// Incompleted encoded content

		Assert.assertEquals("abc_DEF", EnvPropertiesUtil.decode("abc_DEF"));

		// Encoded with CharPool chars

		Assert.assertEquals(
			"abc:D,^E[F]g_H",
			EnvPropertiesUtil.decode(
				"abc_COLON_D_COMMA__CARET_E_OPENBRACKET_F_CLOSEBRACKET_" +
					"_LOWERCASEG__UNDERLINE__UPPERCASEH_"));

		// Encoded with unicode chars

		Assert.assertEquals(
			"abc:D,^E[F]",
			EnvPropertiesUtil.decode("abc_58_D_44__94_E_91_F_93_"));

		// Encoded with illegal content

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				EnvPropertiesUtil.class.getName(), LoggerTestUtil.WARN)) {

			String s = "abc_xyz_D_-1__DEF__GH";

			Assert.assertEquals(s, EnvPropertiesUtil.decode(s));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to decode part \"xyz\" from \"" + s +
					"\", preserve it literally",
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"Unable to decode part \"-1\" from \"" + s +
					"\", preserve it literally",
				logEntry.getMessage());

			logEntry = logEntries.get(2);

			Assert.assertEquals(
				"Unable to decode part \"DEF\" from \"" + s +
					"\", preserve it literally",
				logEntry.getMessage());
		}
	}

}