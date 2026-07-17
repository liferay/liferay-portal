/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @author Peter Yoo
 */
public class ParallelExecutor<T> {

	public ParallelExecutor(
		Collection<Callable<T>> callables, boolean excludeNulls,
		ExecutorService executorService, boolean failOnError,
		String description) {

		synchronized (_nextId) {
			_id = _nextId++;
		}

		_callables = callables;
		_excludeNulls = excludeNulls;
		_executorService = executorService;
		_failOnError = failOnError;
		_description = description;

		if (executorService == null) {
			_disposeExecutor = true;
			_executorService = Executors.newSingleThreadExecutor();
		}
		else {
			_disposeExecutor = false;
		}
	}

	public ParallelExecutor(
		Collection<Callable<T>> callables, boolean excludeNulls,
		ExecutorService executorService, String purpose) {

		this(callables, excludeNulls, executorService, false, purpose);
	}

	public ParallelExecutor(
		Collection<Callable<T>> callables, ExecutorService executorService,
		String purpose) {

		this(callables, false, executorService, purpose);
	}

	public List<T> execute() throws TimeoutException {
		return execute(null);
	}

	public List<T> execute(Long timeoutSeconds) throws TimeoutException {
		start();

		return waitFor(timeoutSeconds);
	}

	public String getDescription() {
		return _description;
	}

	public String getId() {
		return String.valueOf(_id);
	}

	public boolean hasFailedTask() {
		if ((_taskRunnable == null) ||
			(_taskRunnable.getFailedTaskCount() <= 0)) {

			return false;
		}

		return true;
	}

	public void shutdownNow() {
		_executorService.shutdownNow();
	}

	public synchronized void start() {
		_taskRunnable = new TaskRunnable<>(_callables, this);

		_thread = new Thread(_taskRunnable);

		_thread.start();
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.combine(
			"ParallelExecutor ", String.valueOf(getId()), " - ",
			getDescription());
	}

	public List<T> waitFor() throws TimeoutException {
		return waitFor(null);
	}

	public List<T> waitFor(Long timeoutSeconds) throws TimeoutException {
		if (timeoutSeconds == null) {
			timeoutSeconds = 60L * 90L;
		}

		if ((_taskRunnable == null) || (_thread == null)) {
			return null;
		}

		try {
			while (_thread.isAlive()) {
				if (_taskRunnable.getDurationMillis() >
						(1000 * timeoutSeconds)) {

					_taskRunnable.abort();

					String durationString =
						JenkinsResultsParserUtil.toDurationString(
							_taskRunnable.getDurationMillis());

					throw new TimeoutException(
						JenkinsResultsParserUtil.combine(
							toString(), " timed out after ", durationString));
				}

				JenkinsResultsParserUtil.sleep(100);
			}

			return _taskRunnable.getResults();
		}
		finally {
			if (_disposeExecutor) {
				_executorService.shutdownNow();

				while (!_executorService.isShutdown()) {
					JenkinsResultsParserUtil.sleep(100);
				}

				_executorService = null;
			}
		}
	}

	public abstract static class SequentialCallable<T> implements Callable<T> {

		public SequentialCallable() {
			this(_PARALLEL_QUEUE_NAME);
		}

		public SequentialCallable(String queueName) {
			_queueName = queueName;
		}

		public abstract T call() throws Exception;

		public String getQueueName() {
			return _queueName;
		}

		private String _queueName;

	}

	private static final String _PARALLEL_QUEUE_NAME =
		"PARALLEL_EXECUTOR:PARALLEL_QUEUE";

	private static Integer _nextId = 1;

	private final Collection<Callable<T>> _callables;
	private String _description;
	private final boolean _disposeExecutor;
	private boolean _excludeNulls;
	private ExecutorService _executorService;
	private boolean _failOnError;
	private int _id;
	private TaskRunnable<T> _taskRunnable;
	private Thread _thread;

	private static class TaskRunnable<T> implements Runnable {

		public TaskRunnable(
			Collection<Callable<T>> callables,
			ParallelExecutor<T> parallelExecutor) {

			if (parallelExecutor == null) {
				throw new IllegalArgumentException(
					"Parallel executor is required");
			}

			_callables = new ArrayList<>(callables);
			_parallelExecutor = parallelExecutor;

			_callablesMap = _toCallablesMap(callables);
			_executorService = parallelExecutor._executorService;
			_totalTaskCount = callables.size();
		}

		public void abort() {
			_aborted = true;
		}

		public boolean aborted() {
			return _aborted;
		}

		public String generateStatusMessage() {
			StringBuilder sb = new StringBuilder();

			sb.append(_parallelExecutor.toString());

			if ((getRemainingTaskCount() + getRunningTaskCount()) == 0) {
				sb.append(" completed in ");
			}
			else {
				sb.append(" has been running for ");
			}

			sb.append(
				JenkinsResultsParserUtil.toDurationString(getDurationMillis()));
			sb.append("\n Failed: ");
			sb.append(getFailedTaskCount());
			sb.append(" / Remaining: ");
			sb.append(getRemainingTaskCount());
			sb.append(" / Running: ");
			sb.append(getRunningTaskCount());
			sb.append(" / Succeeded: ");
			sb.append(getSucceededTaskCount());
			sb.append(" / Submitted: ");
			sb.append(getSubmittedTaskCount());
			sb.append(" / Total: ");
			sb.append(getTotalTaskCount());
			sb.append("\n Average task duration: ");
			sb.append(
				JenkinsResultsParserUtil.toDurationString(
					getAverageDurationMillis()));

			return sb.toString();
		}

		public long getAverageDurationMillis() {
			if (_completedTasks.isEmpty()) {
				return 0L;
			}

			long totalDuration = 0;

			for (Task<T> completedTask : _completedTasks) {
				TaskCallable<T> taskCallable = completedTask.getCallable();

				totalDuration += taskCallable.getDuration();
			}

			return totalDuration / _completedTasks.size();
		}

		public long getDurationMillis() {
			if (_startTimeMillis == null) {
				return 0L;
			}

			return System.currentTimeMillis() - _startTimeMillis;
		}

		public int getFailedTaskCount() {
			int failedTaskCount = 0;

			for (Task<T> completedTask : _completedTasks) {
				if (completedTask.failed()) {
					failedTaskCount++;
				}
			}

			return failedTaskCount;
		}

		public int getRemainingTaskCount() {
			return getTotalTaskCount() - getFailedTaskCount() -
				getRunningTaskCount() - getSubmittedTaskCount() -
					getSucceededTaskCount();
		}

		public List<T> getResults() {
			if (!isComplete() && !aborted()) {
				return null;
			}

			return new ArrayList<>(_resultsSortedMap.values());
		}

		public int getRunningTaskCount() {
			int runningTaskCount = 0;

			for (Task<T> runningTask : _runningTasks) {
				TaskCallable<T> taskCallable = runningTask.getCallable();

				if (taskCallable.isRunning()) {
					runningTaskCount++;
				}
			}

			return runningTaskCount;
		}

		public int getSubmittedTaskCount() {
			int submittedTaskCount = 0;

			for (Task<T> runningTask : _runningTasks) {
				TaskCallable<T> taskCallable = runningTask.getCallable();

				if (!taskCallable.isRunning()) {
					submittedTaskCount++;
				}
			}

			return submittedTaskCount;
		}

		public int getSucceededTaskCount() {
			int successfulTaskCount = 0;

			for (Task<T> completedTask : _completedTasks) {
				if (!completedTask.failed()) {
					successfulTaskCount++;
				}
			}

			return successfulTaskCount;
		}

		public int getTotalTaskCount() {
			return _totalTaskCount;
		}

		public boolean isComplete() {
			if ((getSucceededTaskCount() + getFailedTaskCount()) ==
					getTotalTaskCount()) {

				return true;
			}

			return false;
		}

		@Override
		public void run() {
			if (_callablesMap.isEmpty()) {
				return;
			}

			Set<Map.Entry<String, Collection<Callable<T>>>> entries =
				_callablesMap.entrySet();

			_startTimeMillis = System.currentTimeMillis();

			long lastOutputTimeMillis = _startTimeMillis;

			for (Map.Entry<String, Collection<Callable<T>>> entry : entries) {
				if (Objects.equals(entry.getKey(), _PARALLEL_QUEUE_NAME)) {
					continue;
				}

				Collection<Callable<T>> callables = entry.getValue();

				Iterator<Callable<T>> iterator = callables.iterator();

				_runningTasks.add(_processCallable(iterator.next(), iterator));
			}

			Collection<Callable<T>> callables = _callablesMap.get(
				_PARALLEL_QUEUE_NAME);

			if ((callables != null) && !callables.isEmpty()) {
				for (Callable<T> callable : callables) {
					_runningTasks.add(_processCallable(callable, null));
				}
			}

			while (!_runningTasks.isEmpty()) {
				List<Task<T>> newProcessorTasks = new ArrayList<>();
				List<Task<T>> latestCompletedProcessorTasks = new ArrayList<>();

				try {
					for (Task<T> processorTask : _runningTasks) {
						TaskCallable<T> taskCallable =
							processorTask.getCallable();

						int callableIndex = _callables.indexOf(
							taskCallable.getNestedCallable());

						if (aborted() || Thread.interrupted()) {
							abort();

							if (_parallelExecutor._excludeNulls == false) {
								_resultsSortedMap.put(callableIndex, null);
							}

							throw new RuntimeException(
								_parallelExecutor + " has been aborted");
						}

						Future<T> future = processorTask.getFuture();

						if (future.isDone()) {
							T result;

							try {
								result = future.get();
							}
							catch (CancellationException | ExecutionException |
								   InterruptedException exception) {

								processorTask.fail();

								RuntimeException runtimeException =
									new RuntimeException(
										"Parallel task threw an exception",
										exception);

								if (_parallelExecutor._failOnError) {
									if (_parallelExecutor._excludeNulls ==
											false) {

										_resultsSortedMap.put(
											callableIndex, null);
									}

									abort();

									throw runtimeException;
								}

								result = null;

								runtimeException.printStackTrace();
							}

							if ((result != null) ||
								(_parallelExecutor._excludeNulls == false)) {

								_resultsSortedMap.put(callableIndex, result);
							}

							latestCompletedProcessorTasks.add(processorTask);

							Iterator<Callable<T>> iterator =
								processorTask.getIterator();

							if ((iterator == null) || !iterator.hasNext()) {
								continue;
							}

							newProcessorTasks.add(
								_processCallable(iterator.next(), iterator));
						}
					}

					if (!latestCompletedProcessorTasks.isEmpty()) {
						_completedTasks.addAll(latestCompletedProcessorTasks);

						_runningTasks.removeAll(latestCompletedProcessorTasks);
					}

					if (!newProcessorTasks.isEmpty()) {
						_runningTasks.addAll(newProcessorTasks);
					}

					long millisSinceLastOutput =
						System.currentTimeMillis() - lastOutputTimeMillis;

					if (millisSinceLastOutput > (1000 * 60 * 3)) {
						System.out.println(generateStatusMessage());

						lastOutputTimeMillis = System.currentTimeMillis();
					}

					if (!_runningTasks.isEmpty()) {
						JenkinsResultsParserUtil.sleep(100);
					}
				}
				catch (Exception exception) {
					if (_parallelExecutor._failOnError || _aborted) {
						for (Task<T> processorTask : _runningTasks) {
							Future<T> future = processorTask.getFuture();

							if ((future != null) && !future.isCancelled()) {
								if (!future.isDone()) {
									future.cancel(true);

									processorTask.fail();

									if (_parallelExecutor._excludeNulls ==
											false) {

										TaskCallable<T> taskCallable =
											processorTask.getCallable();

										int callableIndex = _callables.indexOf(
											taskCallable.getNestedCallable());

										_resultsSortedMap.put(
											callableIndex, null);
									}
								}

								_completedTasks.add(processorTask);
							}
						}

						if (_parallelExecutor._excludeNulls == false) {
							for (Callable<T> callable : _callables) {
								_resultsSortedMap.putIfAbsent(
									_callables.indexOf(callable), null);
							}
						}

						_runningTasks.removeAll(_completedTasks);

						if (exception instanceof RuntimeException) {
							throw (RuntimeException)exception;
						}

						throw new RuntimeException(exception);
					}
				}
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					_parallelExecutor.toString(), " completed ",
					String.valueOf(getSucceededTaskCount()), " tasks in ",
					JenkinsResultsParserUtil.toDurationString(
						getDurationMillis()),
					" averaging ",
					JenkinsResultsParserUtil.toDurationString(
						getAverageDurationMillis()),
					" per task. "));

			int failedTaskCount = getFailedTaskCount();

			if (failedTaskCount > 0) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						String.valueOf(failedTaskCount),
						JenkinsResultsParserUtil.getNounForm(
							failedTaskCount, " tasks", " task"),
						" failed."));
			}
		}

		private Task<T> _processCallable(
			Callable<T> callable, Iterator<Callable<T>> iterator) {

			TaskCallable<T> taskCallable = new TaskCallable<>(callable);

			Future<T> future = _executorService.submit(taskCallable);

			return new Task<>(iterator, taskCallable, future);
		}

		private Map<String, Collection<Callable<T>>> _toCallablesMap(
			Collection<Callable<T>> callables) {

			Map<String, Collection<Callable<T>>> callablesMap = new HashMap<>();

			for (Callable<T> callable : callables) {
				String queueName = null;

				if (callable instanceof SequentialCallable) {
					SequentialCallable<T> groupedCallable =
						(SequentialCallable<T>)callable;

					queueName = groupedCallable.getQueueName();
				}
				else {
					queueName = _PARALLEL_QUEUE_NAME;
				}

				if (JenkinsResultsParserUtil.isNullOrEmpty(queueName)) {
					queueName = _PARALLEL_QUEUE_NAME;
				}

				Collection<Callable<T>> callablesCollection =
					callablesMap.computeIfAbsent(
						queueName, key -> new ArrayList<>());

				callablesCollection.add(callable);
			}

			return callablesMap;
		}

		private boolean _aborted;
		private List<Callable<T>> _callables;
		private final Map<String, Collection<Callable<T>>> _callablesMap;
		private List<Task<T>> _completedTasks = new ArrayList<>();
		private ExecutorService _executorService;
		private final ParallelExecutor<T> _parallelExecutor;
		private final SortedMap<Integer, T> _resultsSortedMap = new TreeMap<>();
		private List<TaskRunnable.Task<T>> _runningTasks = new ArrayList<>();
		private Long _startTimeMillis;
		private final int _totalTaskCount;

		private static class Task<T> {

			public Task(
				Iterator<Callable<T>> iterator,
				TaskCallable<T> processorCallable, Future<T> future) {

				_iterator = iterator;
				_processorCallable = processorCallable;
				_future = future;

				_failed = false;
			}

			public void fail() {
				_failed = true;
			}

			public boolean failed() {
				return _failed;
			}

			public TaskCallable<T> getCallable() {
				return _processorCallable;
			}

			public Future<T> getFuture() {
				return _future;
			}

			public Iterator<Callable<T>> getIterator() {
				return _iterator;
			}

			private boolean _failed;
			private final Future<T> _future;
			private final Iterator<Callable<T>> _iterator;
			private final TaskCallable<T> _processorCallable;

		}

		private static class TaskCallable<T> implements Callable<T> {

			public TaskCallable(Callable<T> callable) {
				_callable = callable;

				_startTimeMillis = null;
			}

			@Override
			public T call() throws Exception {
				_startTimeMillis = System.currentTimeMillis();

				try {
					return _callable.call();
				}
				finally {
					_durationMillis =
						System.currentTimeMillis() - _startTimeMillis;
				}
			}

			public Long getDuration() {
				if (isRunning()) {
					return System.currentTimeMillis() - _startTimeMillis;
				}

				if (isDone()) {
					return _durationMillis;
				}

				return null;
			}

			public Callable<T> getNestedCallable() {
				return _callable;
			}

			public boolean isDone() {
				if (_durationMillis == null) {
					return false;
				}

				return true;
			}

			public boolean isRunning() {
				if ((_startTimeMillis != null) && !isDone()) {
					return true;
				}

				return false;
			}

			private final Callable<T> _callable;
			private Long _durationMillis;
			private Long _startTimeMillis;

		}

	}

}