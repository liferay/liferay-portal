/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.process.local;

import com.liferay.petra.io.ClassLoaderObjectInputStream;
import com.liferay.petra.io.unsync.UnsyncBufferedOutputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Shuyang Zhou
 */
public class LocalProcessLauncher {

	public static void main(String[] arguments) throws IOException {
		PrintStream oldOutPrintStream = System.out;

		ObjectOutputStream objectOutputStream = null;
		ProcessOutputStream outProcessOutputStream = null;

		synchronized (oldOutPrintStream) {
			oldOutPrintStream.flush();

			FileOutputStream fileOutputStream = new FileOutputStream(
				FileDescriptor.out);

			objectOutputStream = new ObjectOutputStream(
				new UnsyncBufferedOutputStream(fileOutputStream));

			outProcessOutputStream = new ProcessOutputStream(
				objectOutputStream, false);

			ProcessContext._setProcessOutputStream(outProcessOutputStream);

			PrintStream newOutPrintStream = new PrintStream(
				outProcessOutputStream, true, StringPool.UTF8);

			System.setOut(newOutPrintStream);
		}

		ProcessOutputStream errProcessOutputStream = new ProcessOutputStream(
			objectOutputStream, true);

		PrintStream errPrintStream = new PrintStream(
			errProcessOutputStream, true, StringPool.UTF8);

		System.setErr(errPrintStream);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			ObjectInputStream bootstrapObjectInputStream =
				new ObjectInputStream(System.in);

			String processCallableName =
				(String)bootstrapObjectInputStream.readObject();

			String logPrefixString = StringPool.OPEN_BRACKET.concat(
				processCallableName
			).concat(
				StringPool.CLOSE_BRACKET
			);

			byte[] logPrefix = logPrefixString.getBytes(StringPool.UTF8);

			outProcessOutputStream._setLogPrefix(logPrefix);
			errProcessOutputStream._setLogPrefix(logPrefix);

			String classPath = (String)bootstrapObjectInputStream.readObject();

			ClassLoader classLoader = new URLClassLoader(
				_getClassPathURLs(classPath));

			currentThread.setContextClassLoader(classLoader);

			ObjectInputStream objectInputStream =
				new ClassLoaderObjectInputStream(
					bootstrapObjectInputStream, classLoader);

			ProcessCallable<?> processCallable =
				(ProcessCallable<?>)objectInputStream.readObject();

			Thread thread = new Thread(
				new ProcessCallableDispatcher(objectInputStream),
				"Process Callable Dispatcher");

			thread.setDaemon(true);

			thread.start();

			Serializable result = processCallable.call();

			System.out.flush();

			outProcessOutputStream._writeProcessCallable(
				new ResultProcessCallable<>(result, null));

			outProcessOutputStream.flush();
		}
		catch (Throwable throwable) {
			errPrintStream.flush();

			ProcessException processException = null;

			if (throwable instanceof ProcessException) {
				processException = (ProcessException)throwable;
			}
			else {
				processException = new ProcessException(throwable);
			}

			errProcessOutputStream._writeProcessCallable(
				new ResultProcessCallable<>(null, processException));

			errProcessOutputStream.flush();
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public static class ProcessContext {

		public static boolean attach(
			String message, long interval, ShutdownHook shutdownHook) {

			HeartbeatThread heartbeatThread = new HeartbeatThread(
				message, interval, shutdownHook);

			boolean value = _heartbeatThreadAtomicReference.compareAndSet(
				null, heartbeatThread);

			if (value) {
				heartbeatThread.start();
			}

			return value;
		}

		public static void detach() throws InterruptedException {
			HeartbeatThread heartbeatThread =
				_heartbeatThreadAtomicReference.getAndSet(null);

			if (heartbeatThread != null) {
				heartbeatThread._detach();
				heartbeatThread.join();
			}
		}

		public static ConcurrentMap<String, Object> getAttributes() {
			return _attributes;
		}

		public static boolean isAttached() {
			HeartbeatThread heartbeatThread =
				_heartbeatThreadAtomicReference.get();

			if (heartbeatThread != null) {
				return true;
			}

			return false;
		}

		public static void writeProcessCallable(
				ProcessCallable<?> processCallable)
			throws IOException {

			_processOutputStream._writeProcessCallable(processCallable);
		}

		private static void _setProcessOutputStream(
			ProcessOutputStream processOutputStream) {

			_processOutputStream = processOutputStream;
		}

		private ProcessContext() {
		}

		private static final ConcurrentMap<String, Object> _attributes =
			new ConcurrentHashMap<>();
		private static final AtomicReference<HeartbeatThread>
			_heartbeatThreadAtomicReference = new AtomicReference<>();
		private static ProcessOutputStream _processOutputStream;

	}

	public interface ShutdownHook {

		public static final int BROKEN_PIPE_CODE = 1;

		public static final int INTERRUPTION_CODE = 2;

		public static final int UNKNOWN_CODE = 3;

		public boolean shutdown(int shutdownCode, Throwable throwable);

	}

	private static URL[] _getClassPathURLs(String classPath)
		throws MalformedURLException {

		Set<URL> urls = new LinkedHashSet<>();

		List<String> paths = StringUtil.split(
			classPath, File.pathSeparatorChar);

		for (String path : paths) {
			File file = new File(path);

			URI uri = file.toURI();

			urls.add(uri.toURL());
		}

		return urls.toArray(new URL[0]);
	}

	private static class HeartbeatThread extends Thread {

		@Override
		public void run() {
			int shutdownCode = 0;
			Throwable shutdownThrowable = null;

			while (!_detach) {
				try {
					sleep(_interval);

					ProcessContext.writeProcessCallable(
						_pringBackProcessCallable);
				}
				catch (InterruptedException interruptedException) {
					if (_detach) {
						return;
					}

					shutdownThrowable = interruptedException;

					shutdownCode = ShutdownHook.INTERRUPTION_CODE;
				}
				catch (IOException ioException) {
					shutdownThrowable = ioException;

					shutdownCode = ShutdownHook.BROKEN_PIPE_CODE;
				}
				catch (Throwable throwable) {
					shutdownThrowable = throwable;

					shutdownCode = ShutdownHook.UNKNOWN_CODE;
				}

				if (shutdownCode == 0) {
					continue;
				}

				_detach = _shutdownHook.shutdown(
					shutdownCode, shutdownThrowable);
			}

			AtomicReference<HeartbeatThread> heartBeatThreadReference =
				ProcessContext._heartbeatThreadAtomicReference;

			heartBeatThreadReference.compareAndSet(this, null);
		}

		private HeartbeatThread(
			String message, long interval, ShutdownHook shutdownHook) {

			if (shutdownHook == null) {
				throw new IllegalArgumentException("Shutdown hook is null");
			}

			_interval = interval;
			_shutdownHook = shutdownHook;

			_pringBackProcessCallable = new PingbackProcessCallable(message);

			setDaemon(true);
			setName(HeartbeatThread.class.getSimpleName());
		}

		private void _detach() {
			_detach = true;

			interrupt();
		}

		private volatile boolean _detach;
		private final long _interval;
		private final ProcessCallable<String> _pringBackProcessCallable;
		private final ShutdownHook _shutdownHook;

	}

	private static class LoggingProcessCallable
		implements ProcessCallable<String> {

		@Override
		public String call() {
			if (_error) {
				System.err.print(_message);
			}
			else {
				System.out.print(_message);
			}

			return StringPool.BLANK;
		}

		private LoggingProcessCallable(String message, boolean error) {
			_message = message;
			_error = error;
		}

		private static final long serialVersionUID = 1L;

		private final boolean _error;
		private final String _message;

	}

	private static class PingbackProcessCallable
		implements ProcessCallable<String> {

		@Override
		public String call() {
			return _message;
		}

		private PingbackProcessCallable(String message) {
			_message = message;
		}

		private static final long serialVersionUID = 1L;

		private final String _message;

	}

	private static class ProcessCallableDispatcher implements Runnable {

		@Override
		public void run() {
			ExecutorService executorService = Executors.newCachedThreadPool(
				new ThreadFactory() {

					@Override
					public Thread newThread(Runnable runnable) {
						Thread thread = new Thread(
							runnable,
							"ProcessCallable-runner-" +
								_counter.getAndIncrement());

						thread.setDaemon(true);

						return thread;
					}

					private final AtomicLong _counter = new AtomicLong();

				});

			while (true) {
				try {
					ProcessCallable<?> processCallable =
						(ProcessCallable<?>)_objectInputStream.readObject();

					executorService.submit(processCallable::call);
				}
				catch (Exception exception) {
					UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
						new UnsyncByteArrayOutputStream();

					UnsyncPrintWriter unsyncPrintWriter = new UnsyncPrintWriter(
						unsyncByteArrayOutputStream);

					unsyncPrintWriter.println(exception);

					exception.printStackTrace(unsyncPrintWriter);

					unsyncPrintWriter.println();

					unsyncPrintWriter.flush();

					System.err.write(
						unsyncByteArrayOutputStream.unsafeGetByteArray(), 0,
						unsyncByteArrayOutputStream.size());
					System.err.flush();
				}
			}
		}

		private ProcessCallableDispatcher(ObjectInputStream objectInputStream) {
			_objectInputStream = objectInputStream;
		}

		private final ObjectInputStream _objectInputStream;

	}

	private static class ProcessOutputStream
		extends UnsyncByteArrayOutputStream {

		@Override
		public void close() throws IOException {
			_objectOutputStream.close();
		}

		@Override
		public void flush() throws IOException {
			synchronized (System.out) {
				if (size() > 0) {
					byte[] bytes = toByteArray();

					reset();

					byte[] logData = new byte[_logPrefix.length + bytes.length];

					System.arraycopy(
						_logPrefix, 0, logData, 0, _logPrefix.length);
					System.arraycopy(
						bytes, 0, logData, _logPrefix.length, bytes.length);

					String message = new String(logData, StringPool.UTF8);

					_objectOutputStream.writeObject(
						new LoggingProcessCallable(message, _error));
				}

				_objectOutputStream.flush();

				_objectOutputStream.reset();
			}
		}

		private ProcessOutputStream(
			ObjectOutputStream objectOutputStream, boolean error) {

			_objectOutputStream = objectOutputStream;
			_error = error;
		}

		private void _setLogPrefix(byte[] logPrefix) {
			_logPrefix = logPrefix;
		}

		private void _writeProcessCallable(ProcessCallable<?> processCallable)
			throws IOException {

			synchronized (System.out) {
				try {
					_objectOutputStream.writeObject(processCallable);
				}
				catch (NotSerializableException notSerializableException) {
					_objectOutputStream.reset();

					throw notSerializableException;
				}
				finally {
					_objectOutputStream.flush();
				}
			}
		}

		private final boolean _error;
		private byte[] _logPrefix;
		private final ObjectOutputStream _objectOutputStream;

	}

}