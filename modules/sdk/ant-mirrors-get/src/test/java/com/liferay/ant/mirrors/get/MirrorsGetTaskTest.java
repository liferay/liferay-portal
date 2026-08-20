/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.mirrors.get;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Calum Ragan
 */
public class MirrorsGetTaskTest extends MirrorsGetTask {

	@Before
	public void setUp() throws IOException {
		_cacheFile = new File(temporaryFolder.getRoot(), _FILE_NAME);
	}

	@Test
	public void testCreateReadLinkDeletedCacheFile() throws IOException {
		_writeFile(_cacheFile, "cached");

		File linkFile = uniqueLinkFile(_cacheFile);

		File readFile = createReadLink(_cacheFile, linkFile);

		Assert.assertEquals(linkFile, readFile);

		_cacheFile.delete();

		Assert.assertFalse(_cacheFile.exists());
		Assert.assertEquals("cached", _readFile(readFile));
	}

	@Test
	public void testCreateReadLinkMissingCacheFile() {
		Assert.assertNull(
			createReadLink(_cacheFile, uniqueLinkFile(_cacheFile)));
	}

	@Test
	public void testPublishConcurrent() throws Exception {
		AtomicInteger publishedCount = new AtomicInteger();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		List<Throwable> throwables = new CopyOnWriteArrayList<>();

		List<Thread> threads = new ArrayList<>();

		for (int i = 0; i < 16; i++) {
			String content = "process-" + i;

			Thread thread = new Thread(
				() -> {
					try {
						File tempFile = uniqueTempFile(_cacheFile);

						_writeFile(tempFile, content);

						countDownLatch.await();

						if (publish(tempFile, _cacheFile)) {
							publishedCount.incrementAndGet();
						}
					}
					catch (Throwable throwable) {
						throwables.add(throwable);
					}
				});

			threads.add(thread);

			thread.start();
		}

		countDownLatch.countDown();

		for (Thread thread : threads) {
			thread.join();
		}

		Assert.assertEquals(throwables.toString(), 0, throwables.size());

		Assert.assertEquals(1, publishedCount.get());

		String content = _readFile(_cacheFile);

		Assert.assertTrue(content, content.startsWith("process-"));
	}

	@Test
	public void testPublishFreeName() throws IOException {
		File tempFile = uniqueTempFile(_cacheFile);

		_writeFile(tempFile, "downloaded");

		Assert.assertTrue(publish(tempFile, _cacheFile));

		Assert.assertEquals("downloaded", _readFile(_cacheFile));
	}

	@Test
	public void testPublishTakenName() throws IOException {
		_writeFile(_cacheFile, "winner");

		File tempFile = uniqueTempFile(_cacheFile);

		_writeFile(tempFile, "loser");

		Assert.assertFalse(publish(tempFile, _cacheFile));

		Assert.assertEquals("winner", _readFile(_cacheFile));

		Assert.assertTrue(tempFile.exists());
	}

	@Test
	public void testSweep() throws IOException {
		_testSweep(false, _oldFileName(""));
		_testSweep(false, _oldFileName("link-"));
		_testSweep(false, _oldTime() + _FILE_NAME);
		_testSweep(
			true,
			uniqueTempFile(
				_cacheFile
			).getName());
	}

	@Test
	public void testSweepCacheFile() throws IOException {
		_writeFile(_cacheFile, "cached");

		File digitFile = new File(
			temporaryFolder.getRoot(), "1234567890123.zip");

		_writeFile(digitFile, "other");

		sweep(_cacheFile);

		Assert.assertTrue(_cacheFile.exists());
		Assert.assertTrue(digitFile.exists());

		sweep(digitFile);

		Assert.assertTrue(digitFile.exists());
	}

	@Test
	public void testSweepReadLinkToOldCacheFile() throws IOException {
		_writeFile(_cacheFile, "cached");

		Assert.assertTrue(_cacheFile.setLastModified(_oldTime()));

		File readFile = createReadLink(_cacheFile, uniqueLinkFile(_cacheFile));

		long thresholdTime = System.currentTimeMillis() - MAX_AGE_MILLIS;

		Assert.assertTrue(readFile.lastModified() < thresholdTime);

		sweep(_cacheFile);

		Assert.assertEquals("cached", _readFile(readFile));
	}

	@Test
	public void testUniqueLinkFile() {
		String fileName = uniqueLinkFile(
			_cacheFile
		).getName();

		Assert.assertTrue(fileName, fileName.contains("-link-"));
		Assert.assertTrue(fileName, fileName.endsWith(_FILE_NAME));

		Assert.assertEquals(5000, _uniqueNameCount(true));
	}

	@Test
	public void testUniqueTempFile() {
		String fileName = uniqueTempFile(
			_cacheFile
		).getName();

		Assert.assertFalse(fileName, fileName.contains("-link-"));
		Assert.assertTrue(fileName, fileName.endsWith(_FILE_NAME));

		Assert.assertEquals(5000, _uniqueNameCount(false));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _oldFileName(String linkMarker) {
		StringBuilder sb = new StringBuilder();

		sb.append(_oldTime());
		sb.append("-");
		sb.append("3963b4ea-097b-42e7-847d-bcc7c56c9f78");
		sb.append("-");
		sb.append(linkMarker);
		sb.append(_FILE_NAME);

		return sb.toString();
	}

	private long _oldTime() {
		return System.currentTimeMillis() - (2 * MAX_AGE_MILLIS);
	}

	private String _readFile(File file) throws IOException {
		byte[] bytes = Files.readAllBytes(file.toPath());

		return new String(bytes, StandardCharsets.UTF_8);
	}

	private void _testSweep(boolean expected, String fileName)
		throws IOException {

		_writeFile(_cacheFile, "cached");

		File file = new File(temporaryFolder.getRoot(), fileName);

		_writeFile(file, "orphan");

		sweep(_cacheFile);

		Assert.assertEquals(fileName, expected, file.exists());
	}

	private int _uniqueNameCount(boolean link) {
		Set<String> fileNames = new HashSet<>();

		for (int i = 0; i < 5000; i++) {
			if (link) {
				fileNames.add(
					uniqueLinkFile(
						_cacheFile
					).getName());
			}
			else {
				fileNames.add(
					uniqueTempFile(
						_cacheFile
					).getName());
			}
		}

		return fileNames.size();
	}

	private void _writeFile(File file, String content) throws IOException {
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private static final String _FILE_NAME = "7.4.13-ga1.zip";

	private File _cacheFile;

}