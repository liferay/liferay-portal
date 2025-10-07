/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.scripting.util;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.UnsupportedLanguageException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Jerry Adriano
 */
public class ServerScriptingUtilTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Test
	public void testExecuteGroovyScript() throws ScriptingException {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServerScriptingUtil.execute(
			HashMapBuilder.<String, Object>put(
				"out", unsyncStringWriter
			).build(),
			"groovy", "print 1 + 1");

		Assert.assertEquals("2", unsyncStringWriter.toString());
	}

	@Test
	public void testExecuteGroovyScriptWithException() {
		try {
			ServerScriptingUtil.execute(
				Collections.emptyMap(), "groovy",
				"throw new UnsupportedOperationException();");

			Assert.fail();
		}
		catch (ScriptingException scriptingException) {
			String message = scriptingException.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith(
					"Unable to execute script: \nLine 1: throw new " +
						"UnsupportedOperationException();\njava.lang." +
							"UnsupportedOperationException"));

			Assert.assertTrue(
				scriptingException.getCause() instanceof
					UnsupportedOperationException);
		}
	}

	@Test
	public void testExecuteWithUnsupportedLanguage() throws Exception {
		try {
			ServerScriptingUtil.execute(
				Collections.emptyMap(), "shell", "return 1 + 1");

			Assert.fail();
		}
		catch (UnsupportedLanguageException unsupportedLanguageException) {
			Assert.assertSame(
				"shell", unsupportedLanguageException.getMessage());
		}
	}

}