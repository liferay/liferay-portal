/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author Peter Yoo
 */
public class FilePropagator {

	public FilePropagator(
		String[] fileNames, String sourceDirName, String targetDirName,
		List<String> targetSlaves) {

		this(fileNames, sourceDirName, targetDirName, null, targetSlaves);
	}

	public FilePropagator(
		String[] fileNames, String sourceDirName, String targetDirName,
		String primaryTargetSlave, List<String> targetSlaves) {

		this(
			fileNames, sourceDirName, targetDirName, primaryTargetSlave,
			targetSlaves, _TIMEOUT_DEFAULT);
	}

	public FilePropagator(
		String[] fileNames, String sourceDirName, String targetDirName,
		String primaryTargetSlave, List<String> targetSlaves, long timeout) {

		_timeout = timeout;

		synchronized (_instanceCount) {
			Thread thread = Thread.currentThread();

			_id = JenkinsResultsParserUtil.combine(
				String.valueOf(thread.getId()), "-",
				String.valueOf(_instanceCount++));
		}

		for (String fileName : fileNames) {
			_filePropagatorTasks.add(
				new FilePropagatorTask(
					sourceDirName + "/" + fileName,
					targetDirName + "/" + fileName));
		}

		if (primaryTargetSlave != null) {
			targetSlaves.remove(primaryTargetSlave);

			_targetSlaves.add(primaryTargetSlave);
		}

		_targetSlaves.addAll(targetSlaves);
	}

	public long getAverageThreadDuration() {
		if (_threadsCompletedCount == 0) {
			return 0;
		}

		return _threadsDurationTotal / _threadsCompletedCount;
	}

	public List<String> getErrorSlaves() {
		return _errorSlaves;
	}

	public void setPostDistCommand(String postDistCommand) {
		_postDistCommand = postDistCommand;
	}

	public void setPreDistCommand(String preDistCommand) {
		_preDistCommand = preDistCommand;
	}

	public void start(int threadCount) {
		_copyFromSource();

		ExecutorService executorService = Executors.newFixedThreadPool(
			threadCount);

		log("File propagation starting with " + threadCount + " threads.");

		try {
			String previousString = null;
			long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			long duration = 0;

			while ((!_targetSlaves.isEmpty() || !_busySlaves.isEmpty()) &&
				   !executorService.isShutdown()) {

				duration =
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

				if (duration >= _timeout) {
					log("Timeout limit exceeded.");

					executorService.shutdownNow();
				}
				else {
					synchronized (this) {
						for (String mirrorSlave : _mirrorSlaves) {
							if (_targetSlaves.isEmpty()) {
								break;
							}

							String targetSlave = _targetSlaves.remove(0);

							executorService.execute(
								new FilePropagatorThread(
									this, mirrorSlave, targetSlave));

							_busySlaves.add(mirrorSlave);
							_busySlaves.add(targetSlave);
						}

						_mirrorSlaves.removeAll(_busySlaves);
					}
				}

				StringBuffer sb = new StringBuffer();

				sb.append("Average thread duration: ");
				sb.append(
					JenkinsResultsParserUtil.toDurationString(
						getAverageThreadDuration()));
				sb.append("\nBusy slaves:");
				sb.append(_busySlaves.size());
				sb.append("\nError slaves:");
				sb.append(_errorSlaves.size());
				sb.append("\nMirror slaves:");
				sb.append(_mirrorSlaves.size());
				sb.append("\nTarget slaves:");
				sb.append(_targetSlaves.size());

				String currentString = sb.toString();

				if (!Objects.equals(previousString, currentString) ||
					executorService.isShutdown()) {

					sb.append("\nTotal duration: ");

					sb.append(
						JenkinsResultsParserUtil.toDurationString(duration));

					sb.append("\n");

					log(sb.toString());

					previousString = currentString;
				}
				else {
					long millisSinceLastMessage =
						System.currentTimeMillis() - _lastMessageTime;

					if (millisSinceLastMessage > (1000 * 60 * 5)) {
						log(
							JenkinsResultsParserUtil.combine(
								"No change in ",
								JenkinsResultsParserUtil.toDurationString(
									millisSinceLastMessage),
								". Timeout will occur in ",
								JenkinsResultsParserUtil.toDurationString(
									_timeout - duration),
								"."));
					}
				}

				JenkinsResultsParserUtil.sleep(5000);
			}

			log(
				JenkinsResultsParserUtil.combine(
					"File propagation completed in ",
					JenkinsResultsParserUtil.toDurationString(duration), "."));

			if (!_errorSlaves.isEmpty()) {
				System.out.println(
					_errorSlaves.size() + " slaves failed to respond:\n" +
						_errorSlaves);
			}
		}
		finally {
			if (!executorService.isShutdown()) {
				executorService.shutdown();
			}
		}

		int totalSlaveCount =
			_busySlaves.size() + _errorSlaves.size() + _mirrorSlaves.size() +
				_targetSlaves.size();

		if ((totalSlaveCount > 0) &&
			((_mirrorSlaves.size() / (float)totalSlaveCount) < 0.5F)) {

			throw new FilePropagatorRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to propagate to ",
					String.valueOf(totalSlaveCount - _mirrorSlaves.size()),
					" out of ", String.valueOf(totalSlaveCount),
					" slave nodes"));
		}
	}

	protected void log(String message) {
		_lastMessageTime = System.currentTimeMillis();

		System.out.print("File propagator ID: ");
		System.out.print(_id);

		if (message.contains("\n")) {
			System.out.print("\n");
		}
		else {
			System.out.print(" - ");
		}

		System.out.println(message);
	}

	private void _copyFromSource() {
		if (_filePropagatorTasks.isEmpty() || _targetSlaves.isEmpty()) {
			return;
		}

		List<String> commands = new ArrayList<>();

		for (FilePropagatorTask filePropagatorTask : _filePropagatorTasks) {
			String sourceFileName = filePropagatorTask._sourceFileName;

			log("Copying from source " + sourceFileName);

			String targetFileName = filePropagatorTask._targetFileName;

			commands.add(_getMkdirCommand(targetFileName));

			if (sourceFileName.startsWith("http")) {
				StringBuilder sb = new StringBuilder();

				sb.append("curl ");

				try {
					if (sourceFileName.startsWith(
							"https://release.liferay.com")) {

						sb.append(" -u ");
						sb.append(
							JenkinsResultsParserUtil.getBuildProperty(
								"jenkins.admin.user.name"));
						sb.append(":");
						sb.append(
							JenkinsResultsParserUtil.getBuildProperty(
								"jenkins.admin.user.password"));
					}
				}
				catch (IOException ioException) {
					throw new FilePropagatorRuntimeException(
						this, "Unable to get jenkins-admin user credentials",
						ioException);
				}

				sb.append("-o ");
				sb.append(targetFileName);
				sb.append(" ");
				sb.append(sourceFileName);

				commands.add(sb.toString());
			}
			else {
				commands.add(
					"rsync -Iqs " + sourceFileName + " " + targetFileName);
			}

			String targetDirName = targetFileName.substring(
				0, targetFileName.lastIndexOf("/"));

			commands.add("ls -al " + targetDirName);
		}

		synchronized (this) {
			while (true) {
				String targetSlave = _targetSlaves.remove(0);

				_busySlaves.add(targetSlave);

				try {
					Process process = _executeBashCommands(
						commands, targetSlave);

					if (process.exitValue() != 0) {
						throw new Exception(
							JenkinsResultsParserUtil.readInputStream(
								process.getErrorStream()));
					}

					_busySlaves.remove(targetSlave);
					_mirrorSlaves.add(targetSlave);

					break;
				}
				catch (Exception exception) {
					_busySlaves.remove(targetSlave);

					if (!_errorSlaves.contains(targetSlave)) {
						_errorSlaves.add(targetSlave);
					}

					if (_targetSlaves.isEmpty()) {
						throw new FilePropagatorRuntimeException(
							this,
							"Unable to copy from source. Executed: " + commands,
							exception);
					}

					log(
						JenkinsResultsParserUtil.combine(
							"Unable to copy from source to ", targetSlave,
							". Retrying with another node."));
				}
			}
		}

		log("Finished copying from source.");
	}

	private Process _executeBashCommands(
			List<String> commands, String targetSlave)
		throws IOException, TimeoutException {

		StringBuffer sb = new StringBuffer();

		sb.append("ssh -o ConnectTimeout=60");
		sb.append(" -o NumberOfPasswordPrompts=0 ");
		sb.append(targetSlave);
		sb.append(" '");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_preDistCommand)) {
			sb.append(_preDistCommand);
			sb.append(" ; ");
		}

		for (int i = 0; i < commands.size(); i++) {
			sb.append(commands.get(i));

			if (i < (commands.size() - 1)) {
				sb.append(" ; ");
			}
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_postDistCommand)) {
			sb.append(" ; ");
			sb.append(_postDistCommand);
		}

		sb.append("'");

		return JenkinsResultsParserUtil.executeBashCommands(sb.toString());
	}

	private String _getMkdirCommand(String fileName) {
		String dirName = fileName.substring(0, fileName.lastIndexOf("/") + 1);

		return "mkdir -p " + dirName;
	}

	private static final long _TIMEOUT_DEFAULT = 1000 * 60 * 60 * 2;

	private static Integer _instanceCount = 0;

	private final List<String> _busySlaves = new ArrayList<>();
	private final List<String> _errorSlaves = new ArrayList<>();
	private final List<FilePropagatorTask> _filePropagatorTasks =
		new ArrayList<>();
	private final String _id;
	private long _lastMessageTime = System.currentTimeMillis();
	private final List<String> _mirrorSlaves = new ArrayList<>();
	private String _postDistCommand;
	private String _preDistCommand;
	private final List<String> _targetSlaves = new ArrayList<>();
	private int _threadsCompletedCount;
	private long _threadsDurationTotal;
	private final long _timeout;

	private static class FilePropagatorRuntimeException
		extends RuntimeException {

		public FilePropagatorRuntimeException(
			FilePropagator filePropagator, String message) {

			this(filePropagator, message, null);
		}

		public FilePropagatorRuntimeException(
			FilePropagator filePropagator, String message,
			Exception exception) {

			super(filePropagator._id + " - " + message, exception);
		}

	}

	private static class FilePropagatorTask {

		private FilePropagatorTask(
			String sourceFileName, String targetFileName) {

			_sourceFileName = _escapeParentheses(sourceFileName);
			_targetFileName = _escapeParentheses(targetFileName);
		}

		private String _escapeParentheses(String fileName) {
			fileName = fileName.replace(")", "\\)");
			fileName = fileName.replace("(", "\\(");

			return fileName;
		}

		private final String _sourceFileName;
		private final String _targetFileName;

	}

	private static class FilePropagatorThread implements Runnable {

		@Override
		public void run() {
			long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			List<FilePropagatorTask> filePropagatorTasks =
				_filePropagator._filePropagatorTasks;

			List<String> commands = new ArrayList<>(
				filePropagatorTasks.size() * 2);

			for (FilePropagatorTask filePropagatorTask : filePropagatorTasks) {
				commands.add(
					_filePropagator._getMkdirCommand(
						filePropagatorTask._targetFileName));

				commands.add(
					"rsync -Iqs " + _mirrorSlave + ":" +
						filePropagatorTask._targetFileName + " " +
							filePropagatorTask._targetFileName);
			}

			String errorMessage = null;

			Thread currentThread = Thread.currentThread();

			if (currentThread.isInterrupted()) {
				_successful = false;
			}
			else {
				try {
					Process process = _filePropagator._executeBashCommands(
						commands, _targetSlave);

					int exitValue = process.exitValue();

					errorMessage = JenkinsResultsParserUtil.readInputStream(
						process.getErrorStream(), true);

					_successful = exitValue == 0;
				}
				catch (Exception exception) {
					_successful = false;
				}
			}

			_duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

			String durationString = JenkinsResultsParserUtil.toDurationString(
				_duration);

			if (_successful) {
				_filePropagator.log(
					JenkinsResultsParserUtil.combine(
						"Propagated to ", _targetSlave, " from ", _mirrorSlave,
						" in ", durationString, "."));
			}
			else {
				_filePropagator.log(
					JenkinsResultsParserUtil.combine(
						"Unable to propagate to ", _targetSlave, " from ",
						_mirrorSlave, "."));

				if (!JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
					_filePropagator.log(errorMessage);
				}
			}

			synchronized (_filePropagator) {
				_filePropagator._busySlaves.remove(_mirrorSlave);
				_filePropagator._busySlaves.remove(_targetSlave);
				_filePropagator._mirrorSlaves.add(_mirrorSlave);
				_filePropagator._threadsCompletedCount++;
				_filePropagator._threadsDurationTotal += _duration;

				if (!_successful) {
					_filePropagator._errorSlaves.add(_targetSlave);

					return;
				}

				_filePropagator._mirrorSlaves.add(_targetSlave);
			}
		}

		private FilePropagatorThread(
			FilePropagator filePropagator, String mirrorSlave,
			String targetSlave) {

			_filePropagator = filePropagator;
			_mirrorSlave = mirrorSlave;
			_targetSlave = targetSlave;
		}

		private long _duration;
		private final FilePropagator _filePropagator;
		private final String _mirrorSlave;
		private boolean _successful;
		private final String _targetSlave;

	}

}