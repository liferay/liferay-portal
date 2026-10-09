/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scripting.groovy.internal;

import com.liferay.portal.kernel.scripting.ScriptingException;
import com.liferay.portal.kernel.scripting.ScriptingExecutor;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.FileUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alberto Montero
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "scripting.language=" + GroovyScriptingExecutor.LANGUAGE,
	service = ScriptingExecutor.class
)
public class GroovyScriptingExecutor implements ScriptingExecutor {

	public static final String LANGUAGE = "groovy";

	@Override
	public Map<String, Object> eval(
			Set<String> allowedClasses, Map<String, Object> inputObjects,
			Set<String> outputNames, File scriptFile)
		throws ScriptingException {

		try {
			String script = FileUtil.read(scriptFile);

			return eval(allowedClasses, inputObjects, outputNames, script);
		}
		catch (IOException ioException) {
			throw new ScriptingException(ioException);
		}
	}

	@Override
	public Map<String, Object> eval(
			Set<String> allowedClasses, Map<String, Object> inputObjects,
			Set<String> outputNames, String script)
		throws ScriptingException {

		if (allowedClasses != null) {
			throw new ScriptingException(
				"Constrained execution not supported for Groovy");
		}

		try {
			Script compiledScript = _parse(script);

			Binding binding = new Binding(inputObjects);

			compiledScript.setBinding(binding);

			compiledScript.run();

			if (outputNames == null) {
				return null;
			}

			Map<String, Object> outputObjects = new HashMap<>();

			for (String outputName : outputNames) {
				if (!binding.hasVariable(outputName)) {
					continue;
				}

				outputObjects.put(outputName, binding.getVariable(outputName));
			}

			return outputObjects;
		}
		catch (GroovyRuntimeException groovyRuntimeException) {
			throw new ScriptingException(
				groovyRuntimeException.getMessage(),
				groovyRuntimeException.getCause());
		}
	}

	@Override
	public String getLanguage() {
		return LANGUAGE;
	}

	@Override
	public ScriptingExecutor newInstance(boolean executeInSeparateThread) {
		return new GroovyScriptingExecutor();
	}

	@Override
	public void validate(String script) throws ScriptingException {
		try {
			_parse(script);
		}
		catch (Exception exception) {
			throw new ScriptingException(exception);
		}
	}

	private Script _parse(String script) {
		Thread currentThread = Thread.currentThread();

		GroovyShell groovyShell = new GroovyShell(
			AggregateClassLoader.getAggregateClassLoader(
				GroovyShell.class.getClassLoader(),
				GroovyScriptingExecutor.class.getClassLoader(),
				currentThread.getContextClassLoader()));

		return groovyShell.parse(script);
	}

}