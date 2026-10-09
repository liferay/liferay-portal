/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author Peter Yoo
 */
public class RemoteExecutor {

	public static long getTimeout() {
		return _timeout;
	}

	public static void setTimeout(long timeout) {
		_timeout = _timeout;
	}

	public int execute(
		int threadCount, String[] targetSlaves, String[] commands) {

		_busySlaves.clear();
		_commands = commands;
		_errorSlaves.clear();
		_finishedSlaves.clear();
		_targetSlaves = targetSlaves;
		_threadsDurationTotal = 0;

		ExecutorService executorService = Executors.newFixedThreadPool(
			threadCount);

		System.out.println(
			"Remote execution starting with " + threadCount + " threads.");

		try {
			_start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			for (String targetSlave : _targetSlaves) {
				executorService.execute(
					new RemoteExecutorThread(this, targetSlave));
			}

			while ((_finishedSlaves.size() + _errorSlaves.size()) <
						_targetSlaves.length) {

				JenkinsResultsParserUtil.sleep(1000);
			}

			if (!_errorSlaves.isEmpty()) {
				System.out.println(
					_errorSlaves.size() + " slaves failed to respond:\n" +
						_errorSlaves);
			}

			return _errorSlaves.size();
		}
		finally {
			executorService.shutdown();
		}
	}

	private long _getAverageThreadDuration() {
		int threadsCompletedCount =
			_finishedSlaves.size() + _errorSlaves.size();

		if (threadsCompletedCount == 0) {
			return 0;
		}

		return _threadsDurationTotal / threadsCompletedCount;
	}

	private void _onThreadComplete(RemoteExecutorThread remoteExecutorThread) {
		_busySlaves.remove(remoteExecutorThread._targetSlave);

		if (!remoteExecutorThread._error) {
			_finishedSlaves.add(remoteExecutorThread._targetSlave);
		}
		else {
			_errorSlaves.add(remoteExecutorThread._targetSlave);
		}

		_threadsDurationTotal += remoteExecutorThread._duration;

		StringBuffer sb = new StringBuffer();

		sb.append("Average thread duration: ");
		sb.append(_getAverageThreadDuration());
		sb.append("ms\nBusy slaves:");
		sb.append(_busySlaves.size());
		sb.append("\nFinished slaves:");
		sb.append(_finishedSlaves.size());
		sb.append("\nTarget slaves:");
		sb.append(_targetSlaves.length);
		sb.append("\nTotal duration: ");
		sb.append(JenkinsResultsParserUtil.getCurrentTimeMillis() - _start);
		sb.append("\n");

		System.out.println(sb.toString());

		if ((_finishedSlaves.size() + _errorSlaves.size()) ==
				_targetSlaves.length) {

			System.out.println(
				"Remote execution completed in " +
					(JenkinsResultsParserUtil.getCurrentTimeMillis() - _start) +
						"ms.");
		}
	}

	private void _onThreadStart(RemoteExecutorThread remoteExecutorThread) {
		_busySlaves.add(remoteExecutorThread._targetSlave);
	}

	private static long _timeout = 1000 * 60 * 60;

	private final List<String> _busySlaves = new ArrayList<>();
	private String[] _commands;
	private final List<String> _errorSlaves = new ArrayList<>();
	private final List<String> _finishedSlaves = new ArrayList<>();
	private long _start;
	private String[] _targetSlaves;
	private long _threadsDurationTotal;

	private static class RemoteExecutorThread implements Runnable {

		@Override
		public void run() {
			synchronized (_remoteExecutor) {
				_remoteExecutor._onThreadStart(this);
			}

			_error = false;

			long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			try {
				int returnCode = _executeBashCommands();

				_duration =
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

				if (returnCode != 0) {
					_handleError(null);
				}
			}
			catch (Exception exception) {
				_handleError(exception.getMessage());
			}
			finally {
				synchronized (_remoteExecutor) {
					_remoteExecutor._onThreadComplete(this);
				}
			}
		}

		private RemoteExecutorThread(
			RemoteExecutor remoteExecutor, String targetSlave) {

			_remoteExecutor = remoteExecutor;
			_targetSlave = targetSlave;
		}

		private int _executeBashCommands()
			throws IOException, TimeoutException {

			StringBuffer sb = new StringBuffer(
				"ssh -o NumberOfPasswordPrompts=0 ");

			sb.append(_targetSlave);
			sb.append(" '");

			String[] commands = _remoteExecutor._commands;

			for (int i = 0; i < commands.length; i++) {
				sb.append(commands[i]);

				if (i >= (commands.length - 1)) {
					continue;
				}

				sb.append(" ; ");
			}

			sb.append("'");

			Process process = JenkinsResultsParserUtil.executeBashCommands(
				true, new File("."), getTimeout(), sb.toString());

			return process.exitValue();
		}

		private void _handleError(String errorMessage) {
			_error = true;

			System.out.println(
				"Remote execution failed on target slave: " + _targetSlave +
					".\n");

			if ((errorMessage != null) && !errorMessage.isEmpty()) {
				System.out.println(errorMessage);
			}
		}

		private long _duration;
		private boolean _error;
		private final RemoteExecutor _remoteExecutor;
		private final String _targetSlave;

	}

}