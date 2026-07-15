/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.credential;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Closeable;
import java.io.IOException;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author Carlos Sierra Andrés
 */
public class FileWatcher implements Closeable {

	public FileWatcher(
			Consumer<WatchEvent<Path>> consumer,
			ExecutorService notificationsExecutorService,
			long notificationTimeout, TimeUnit notificationTimeUnit,
			Path... paths)
		throws IOException {

		this(
			consumer, Executors.newScheduledThreadPool(1), 1, 1,
			TimeUnit.SECONDS, notificationsExecutorService, notificationTimeout,
			notificationTimeUnit, paths);
	}

	public FileWatcher(Consumer<WatchEvent<Path>> consumer, Path... paths)
		throws IOException {

		this(
			consumer, Executors.newScheduledThreadPool(1), 1, 1,
			TimeUnit.SECONDS, Executors.newSingleThreadExecutor(), 10,
			TimeUnit.SECONDS, paths);
	}

	public FileWatcher(
			Consumer<WatchEvent<Path>> consumer,
			ScheduledExecutorService scheduledExecutorService,
			long initialDelay, long period, TimeUnit units,
			ExecutorService notificationsExecutorService,
			long notificationTimeout, TimeUnit notificationTimeUnit,
			Path... paths)
		throws IOException {

		_consumer = consumer;
		_scheduledExecutorService = scheduledExecutorService;
		_notificationsExecutorService = notificationsExecutorService;

		_paths = Arrays.asList(paths);

		FileSystem fileSystem = FileSystems.getDefault();

		_watchService = fileSystem.newWatchService();

		for (Path path : _paths) {
			if (!Files.isDirectory(path)) {
				path = path.getParent();
			}

			path.register(
				_watchService, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		}

		_scheduledExecutorService.scheduleAtFixedRate(
			() -> {
				WatchKey watchKey = null;

				try {
					watchKey = _watchService.take();
				}
				catch (ClosedWatchServiceException | InterruptedException
							exception) {

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return;
				}

				if ((watchKey == null) || !watchKey.isValid()) {
					return;
				}

				List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

				List<Future<?>> futures = new ArrayList<>();

				for (Path path : _paths) {
					for (WatchEvent<?> watchEvent : watchEvents) {
						WatchEvent<Path> watchEventPath =
							(WatchEvent<Path>)watchEvent;

						Path contextPath = watchEventPath.context();

						if (!contextPath.endsWith(path.getFileName())) {
							continue;
						}

						futures.add(
							notificationsExecutorService.submit(
								() -> _consumer.accept(watchEventPath)));
					}
				}

				try {
					long deadline =
						System.currentTimeMillis() +
							notificationTimeUnit.toMillis(notificationTimeout);

					for (Future<?> future : futures) {
						future.get(
							Math.max(0, deadline - System.currentTimeMillis()),
							TimeUnit.MILLISECONDS);
					}
				}
				catch (ExecutionException | InterruptedException |
					   TimeoutException exception) {

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return;
				}

				watchKey.reset();
			},
			initialDelay, period, units);
	}

	@Override
	public void close() {
		try {
			_watchService.close();
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		_notificationsExecutorService.shutdown();

		_scheduledExecutorService.shutdownNow();
	}

	private static final Log _log = LogFactoryUtil.getLog(FileWatcher.class);

	private final Consumer<WatchEvent<Path>> _consumer;
	private final ExecutorService _notificationsExecutorService;
	private final List<Path> _paths;
	private final ScheduledExecutorService _scheduledExecutorService;
	private final WatchService _watchService;

}