/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.exception;

import com.liferay.poshi.core.PoshiProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class PoshiRunnerWarningException extends Exception {

	public static void addException(
		PoshiRunnerWarningException poshiRunnerWarningException) {

		List<PoshiRunnerWarningException> poshiRunnerWarningExceptions =
			getPoshiRunnerWarningExceptions();

		if (!poshiRunnerWarningExceptions.contains(
				poshiRunnerWarningException)) {

			poshiRunnerWarningExceptions.add(poshiRunnerWarningException);
		}
	}

	public static void clear() {
		_threadBasedPoshiRunnerWarningExceptions.remove(_getThreadName());
	}

	public static List<PoshiRunnerWarningException>
		getPoshiRunnerWarningExceptions() {

		_initPoshiRunnerWarningExceptions();

		return _threadBasedPoshiRunnerWarningExceptions.get(_getThreadName());
	}

	public PoshiRunnerWarningException(String msg) {
		super(msg);

		List<PoshiRunnerWarningException> poshiRunnerWarningExceptions =
			getPoshiRunnerWarningExceptions();

		poshiRunnerWarningExceptions.add(this);
	}

	public PoshiRunnerWarningException(String msg, Throwable throwable) {
		super(msg, throwable);

		List<PoshiRunnerWarningException> poshiRunnerWarningExceptions =
			getPoshiRunnerWarningExceptions();

		poshiRunnerWarningExceptions.add(this);
	}

	private static String _getThreadName() {
		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		if (poshiProperties.testRunType.equals("parallel")) {
			Thread thread = Thread.currentThread();

			return thread.getName();
		}

		return "main";
	}

	private static void _initPoshiRunnerWarningExceptions() {
		_threadBasedPoshiRunnerWarningExceptions.putIfAbsent(
			_getThreadName(), new ArrayList<>());
	}

	private static final Map<String, List<PoshiRunnerWarningException>>
		_threadBasedPoshiRunnerWarningExceptions = Collections.synchronizedMap(
			new HashMap<>());

}