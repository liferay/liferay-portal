/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brian Wing Shun Chan
 * @author Roberto Díaz
 */
public class FileImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAppendParentheticalSuffixWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals("test(1) (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals("test (1!$eae1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals("test (1111111).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals("test (AAAAAAA).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals("test (A B).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals("test (1).jsp", fileName);
	}

	@Test
	public void testAppendParentheticalSuffixWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals("test (A).jsp", fileName);
	}

	@Test
	public void testAppendSuffix() {
		Assert.assertEquals("test_rtl", _fileImpl.appendSuffix("test", "_rtl"));
		Assert.assertEquals(
			"test_rtl.css", _fileImpl.appendSuffix("test.css", "_rtl"));
		Assert.assertEquals(
			"/folder/test_rtl.css",
			_fileImpl.appendSuffix("/folder/test.css", "_rtl"));
	}

	@Test
	public void testCopyAndDeltreeConcurrently() throws Exception {
		File directory = new File(
			System.getProperty("java.io.tmpdir"), "tempDir");

		directory.mkdir();

		File file = new File(directory, "testFile");

		file.createNewFile();

		CountDownLatch countDownLatch1 = new CountDownLatch(1);
		CountDownLatch countDownLatch2 = new CountDownLatch(1);

		Thread thread = new Thread(
			() -> {
				try {
					countDownLatch2.await();
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				_fileImpl.deltree(directory);

				countDownLatch1.countDown();
			});

		thread.start();

		File newDirectory = new File(
			System.getProperty("java.io.tmpdir"), "newTempDir");

		_fileImpl.copyDirectory(
			new File(directory.getPath()) {

				@Override
				public File[] listFiles() {
					countDownLatch2.countDown();

					try {
						countDownLatch1.await();

						return super.listFiles();
					}
					catch (InterruptedException interruptedException) {
						throw new RuntimeException(interruptedException);
					}
				}

			},
			newDirectory);

		thread.join();

		File newFile = new File(newDirectory, "testFile");

		Assert.assertFalse(newFile.exists());
	}

	@Test
	public void testCopyDirectory() throws IOException {
		File directory1 = new File(
			System.getProperty("java.io.tmpdir"), "tempDir1");

		directory1.mkdir();

		File file1 = new File(directory1, "testFile1");

		file1.createNewFile();

		File file2 = new File(directory1, "testFile2");

		file2.createNewFile();

		File directory2 = new File(directory1, "tempDir2");

		directory2.mkdir();

		File file3 = new File(directory2, "testFile3");

		file3.createNewFile();

		File file4 = new File(directory2, "testFile4");

		file4.createNewFile();

		File newDirectory1 = new File(
			System.getProperty("java.io.tmpdir"), "newTempDir1");

		_fileImpl.copyDirectory(directory1, newDirectory1);

		Assert.assertTrue(newDirectory1.exists());

		File newFile1 = new File(newDirectory1, "testFile1");

		Assert.assertTrue(newFile1.exists());

		File newFile2 = new File(newDirectory1, "testFile2");

		Assert.assertTrue(newFile2.exists());

		File newDirectory2 = new File(newDirectory1, "tempDir2");

		Assert.assertTrue(newDirectory2.exists());

		File newFile3 = new File(newDirectory2, "testFile3");

		Assert.assertTrue(newFile3.exists());

		File newFile4 = new File(newDirectory2, "testFile4");

		Assert.assertTrue(newFile4.exists());

		_fileImpl.deltree(newDirectory1);
		_fileImpl.deltree(directory1);
	}

	@Test
	public void testDeltree() throws IOException {
		File tempFile = File.createTempFile("tempFile", null);

		_fileImpl.deltree(tempFile);

		Assert.assertFalse(tempFile.exists());

		File directory1 = new File(
			System.getProperty("java.io.tmpdir"), "tempDir1");

		directory1.mkdir();

		File file1 = new File(directory1, "testFile1");

		file1.createNewFile();

		File file2 = new File(directory1, "testFile2");

		file2.createNewFile();

		File directory2 = new File(directory1, "tempDir2");

		directory2.mkdir();

		File file3 = new File(directory2, "testFile3");

		file3.createNewFile();

		File file4 = new File(directory2, "testFile4");

		file4.createNewFile();

		_fileImpl.deltree(directory1);

		Assert.assertFalse(directory1.exists());
	}

	@Test
	public void testDeltreeConcurrently() throws Exception {
		File directory = new File(
			System.getProperty("java.io.tmpdir"), "tempDir");

		directory.mkdir();

		CountDownLatch countDownLatch1 = new CountDownLatch(1);
		CountDownLatch countDownLatch2 = new CountDownLatch(1);

		Thread thread = new Thread(
			() -> {
				try {
					countDownLatch2.await();
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				directory.delete();

				countDownLatch1.countDown();
			});

		thread.start();

		_fileImpl.deltree(
			new File(directory.getPath()) {

				@Override
				public File[] listFiles() {
					countDownLatch2.countDown();

					try {
						countDownLatch1.await();

						return super.listFiles();
					}
					catch (InterruptedException interruptedException) {
						throw new RuntimeException(interruptedException);
					}
				}

			});

		thread.join();

		Assert.assertFalse(directory.exists());
	}

	@Test
	public void testGetPathBackSlashForwardSlash() {
		Assert.assertEquals(
			"aaa\\bbb/ccc\\ddd",
			_fileImpl.getPath("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetPathForwardSlashBackSlash() {
		Assert.assertEquals(
			"aaa/bbb\\ccc/ddd", _fileImpl.getPath("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetPathNoPath() {
		Assert.assertEquals(StringPool.SLASH, _fileImpl.getPath("aaa.bbb"));
	}

	@Test
	public void testGetShortFileNameBackSlashForwardSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa\\bbb/ccc\\ddd/eee.fff"));
	}

	@Test
	public void testGetShortFileNameForwardSlashBackSlash() {
		Assert.assertEquals(
			"eee.fff", _fileImpl.getShortFileName("aaa/bbb\\ccc/ddd\\eee.fff"));
	}

	@Test
	public void testGetShortFileNameNoPath() {
		Assert.assertEquals("aaa.bbb", _fileImpl.getShortFileName("aaa.bbb"));
	}

	@Test
	public void testStripSuffixAppendedWhenFileNameHasParenthesis() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test(1).jsp", "1");

		Assert.assertEquals(
			"test(1).jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleCharacterValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1!$eae1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleNumericalValue() {
		String fileName2 = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "1111111");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName2));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "AAAAAAA");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithMultipleStringWithSpaceValue() {
		String fileName = _fileImpl.appendParentheticalSuffix(
			"test.jsp", "A B");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleNumericalValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "1");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixAppendedWithSingleStringValue() {
		String fileName = _fileImpl.appendParentheticalSuffix("test.jsp", "A");

		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix(fileName));
	}

	@Test
	public void testStripSuffixWhenFileNameHasInvertedParenthesis() {
		Assert.assertEquals(
			"test)1(.jsp", _fileImpl.stripParentheticalSuffix("test)1(.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoCloseParenthesis() {
		Assert.assertEquals(
			"test(1.jsp", _fileImpl.stripParentheticalSuffix("test(1.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoExtension() {
		Assert.assertEquals(
			"test", _fileImpl.stripParentheticalSuffix("test (1)"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasNoParentheticalSuffix() {
		Assert.assertEquals(
			"test.jsp", _fileImpl.stripParentheticalSuffix("test.jsp"));
	}

	@Test
	public void testStripSuffixWhenFileNameHasParenthesisAtStart() {
		Assert.assertEquals(
			"()test.jsp", _fileImpl.stripParentheticalSuffix("()test.jsp"));
	}

	@Test
	public void testUnzip() throws Exception {
		Path testPath = Files.createTempDirectory("testUnzip");

		File zipFile = _createZipFile(
			testPath, "test.zip", "zip/test/entry/entry.txt");

		try {
			_fileImpl.unzip(zipFile, testPath.toFile());

			Assert.assertTrue(
				Files.exists(testPath.resolve("zip/test/entry/entry.txt")));
		}
		finally {
			_fileImpl.deltree(testPath.toFile());
		}
	}

	@Test
	public void testUnzipZipSlipVulnerable() throws Exception {
		Path testPath = Files.createTempDirectory(null);

		Path testChildPath = Files.createTempDirectory(testPath, null);

		File zipFile = _createZipFile(
			testChildPath, "test_slip.zip", "../bad.txt", "good.txt");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				FileImpl.class.getName(), LoggerTestUtil.WARN)) {

			_fileImpl.unzip(zipFile, testChildPath.toFile());

			Assert.assertTrue(Files.exists(testChildPath.resolve("good.txt")));
			Assert.assertFalse(Files.exists(testPath.resolve("bad.txt")));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Invalid entry name: ../bad.txt", logEntry.getMessage());
		}
		finally {
			_fileImpl.deltree(testPath.toFile());
		}
	}

	private File _createZipFile(
			Path destinationPath, String fileName, String... entries)
		throws Exception {

		File zipFile = new File(destinationPath.toFile(), fileName);

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(zipFile))) {

			for (String entry : entries) {
				ZipEntry zipEntry = new ZipEntry(entry);

				zipOutputStream.putNextEntry(zipEntry);

				zipOutputStream.write(_ENTRY_CONTENT, 0, _ENTRY_CONTENT.length);

				zipOutputStream.closeEntry();
			}
		}

		return zipFile;
	}

	private static final byte[] _ENTRY_CONTENT = StringPool.CONTENT.getBytes();

	private final FileImpl _fileImpl = new FileImpl();

}