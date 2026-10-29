/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Brian Wulbern
 */
public class TensorFlowProcessHolderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		FileUtil fileUtil = new FileUtil();

		fileUtil.setFile(Mockito.mock(File.class));

		_portalProcessConfig = PortalClassPathUtil.getPortalProcessConfig();

		ReflectionTestUtil.setFieldValue(
			PortalClassPathUtil.class, "_portalProcessConfig",
			new ProcessConfig.Builder().build());

		TensorFlowProcessHolder.resetCounter();

		_setUpTensorFlowProcessHolder();
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			PortalClassPathUtil.class, "_portalProcessConfig",
			_portalProcessConfig);
	}

	@Test
	@TestInfo("LPD-110537")
	public void testDestroy() {
		_tensorFlowProcessHolder.destroy();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TensorFlowProcessHolder.class.getName(),
				LoggerTestUtil.ERROR)) {

			Consumer<ProcessLog> processLogConsumer =
				_processConfig.getProcessLogConsumer();

			processLogConsumer.accept(
				_getProcessLog(new IOException(RandomTestUtil.randomString())));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			processLogConsumer.accept(
				_getProcessLog(
					new RuntimeException(RandomTestUtil.randomString())));

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
	}

	@Test
	@TestInfo("LPD-110537")
	public void testExecute() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TensorFlowProcessHolder.class.getName(),
				LoggerTestUtil.ERROR)) {

			Consumer<ProcessLog> processLogConsumer =
				_processConfig.getProcessLogConsumer();

			processLogConsumer.accept(
				_getProcessLog(new IOException(RandomTestUtil.randomString())));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Abort subprocess piping", logEntry.getMessage());
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ProcessLog _getProcessLog(Throwable throwable) {
		ProcessLog processLog = Mockito.mock(ProcessLog.class);

		Mockito.when(
			processLog.getLevel()
		).thenReturn(
			ProcessLog.Level.ERROR
		);

		Mockito.when(
			processLog.getMessage()
		).thenReturn(
			"Abort subprocess piping"
		);

		Mockito.when(
			processLog.getThrowable()
		).thenReturn(
			throwable
		);

		return processLog;
	}

	private void _setUpTensorFlowProcessHolder() throws Exception {
		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getDataFile("tensorflow-workdir")
		).thenReturn(
			temporaryFolder.newFolder()
		);

		Mockito.when(
			bundle.getHeaders("")
		).thenReturn(
			HashMapDictionaryBuilder.put(
				Constants.BUNDLE_CLASSPATH, "."
			).build()
		);

		ProcessExecutor processExecutor = Mockito.mock(ProcessExecutor.class);

		ProcessChannel<String> processChannel = Mockito.mock(
			ProcessChannel.class);

		Mockito.when(
			processChannel.getProcessNoticeableFuture()
		).thenReturn(
			Mockito.mock(NoticeableFuture.class)
		);

		Mockito.doReturn(
			Mockito.mock(NoticeableFuture.class)
		).when(
			processChannel
		).write(
			ArgumentMatchers.any()
		);

		ArgumentCaptor<ProcessConfig> processConfigArgumentCaptor =
			ArgumentCaptor.forClass(ProcessConfig.class);

		Mockito.doReturn(
			processChannel
		).when(
			processExecutor
		).execute(
			processConfigArgumentCaptor.capture(), ArgumentMatchers.any()
		);

		TensorFlowDownloadHelper tensorFlowDownloadHelper = Mockito.mock(
			TensorFlowDownloadHelper.class);

		Mockito.when(
			tensorFlowDownloadHelper.getNativeLibraryInputStream()
		).thenReturn(
			new ByteArrayInputStream(new byte[0])
		);

		_tensorFlowProcessHolder = new TensorFlowProcessHolder(
			bundle, processExecutor, tensorFlowDownloadHelper);

		_tensorFlowProcessHolder.execute(
			(ProcessCallable<String>)() -> null, RandomTestUtil.randomInt(),
			RandomTestUtil.randomInt());

		_processConfig = processConfigArgumentCaptor.getValue();
	}

	private ProcessConfig _portalProcessConfig;
	private ProcessConfig _processConfig;
	private TensorFlowProcessHolder _tensorFlowProcessHolder;

}