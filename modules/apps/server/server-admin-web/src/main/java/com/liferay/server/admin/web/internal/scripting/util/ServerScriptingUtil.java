/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.scripting.util;

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.UnsupportedLanguageException;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.SetUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.LineNumberReader;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Carolina Barbosa
 */
public class ServerScriptingUtil {

	public static void execute(
			Map<String, Object> inputObjects, String language, String script)
		throws ScriptingException {

		Set<String> supportedLanguages = getSupportedLanguages();

		if (!supportedLanguages.contains(language)) {
			throw new UnsupportedLanguageException(language);
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			if (language.equals("groovy")) {
				_executeGroovyScript(inputObjects, script);
			}
		}
		catch (Exception exception) {
			throw new ScriptingException(
				_getErrorMessage(exception, script), exception);
		}
		finally {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Evaluated script in " + stopWatch.getTime() + " ms");
			}
		}
	}

	public static Set<String> getSupportedLanguages() {
		return SetUtil.fromArray(new String[] {"groovy"});
	}

	private static void _executeGroovyScript(
			Map<String, Object> inputObjects, String script)
		throws Exception {

		Class<?> clazz = ServerScriptingUtil.class;

		Thread currentThread = Thread.currentThread();

		GroovyShell groovyShell = new GroovyShell(
			AggregateClassLoader.getAggregateClassLoader(
				clazz.getClassLoader(), currentThread.getContextClassLoader()));

		Script compiledScript = groovyShell.parse(script);

		compiledScript.setBinding(new Binding(inputObjects));

		compiledScript.run();
	}

	private static String _getErrorMessage(Exception exception, String script) {
		String errorMessage = "Unable to execute script: \n";

		try {
			LineNumberReader lineNumberReader = new LineNumberReader(
				new UnsyncStringReader(script));

			while (true) {
				String line = lineNumberReader.readLine();

				if (line == null) {
					break;
				}

				errorMessage = StringBundler.concat(
					errorMessage, "Line ", lineNumberReader.getLineNumber(),
					": ", line, StringPool.NEW_LINE);
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			errorMessage = errorMessage + script;
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		exception.printStackTrace(new UnsyncPrintWriter(unsyncStringWriter));

		return errorMessage + unsyncStringWriter;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServerScriptingUtil.class);

}