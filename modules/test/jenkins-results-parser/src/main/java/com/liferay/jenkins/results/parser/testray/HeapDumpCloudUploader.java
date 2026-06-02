/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

/**
 * @author Charlotte Wong
 */
public class HeapDumpCloudUploader {

	public HeapDumpCloudUploader(
		String phase, String masterHostname, String jobName, String buildNumber,
		String slaveHostname) {

		_phase = phase;
		_masterHostname = masterHostname;
		_jobName = jobName;
		_buildNumber = buildNumber;
		_slaveHostname = slaveHostname;
	}

	public void upload() {
		File hprofFile = new File(
			JenkinsResultsParserUtil.combine(
				"/tmp/ant-heap-dump-", _phase, ".hprof"));

		if (!hprofFile.exists()) {
			return;
		}

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		long latestTimestamp = testrayCloudBucket.getLatestObjectTimestamp(
			"heap-dumps/");

		if (latestTimestamp != Long.MIN_VALUE) {
			long elapsedMillis =
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					latestTimestamp;

			long minutesAgo = elapsedMillis / (60 * 1000);

			if (minutesAgo < _THROTTLE_MINUTES) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"INFO: Skipping heap dump upload, last dump was ",
						String.valueOf(minutesAgo),
						" minutes ago (throttle window: ",
						String.valueOf(_THROTTLE_MINUTES),
						" min). Local file: ", hprofFile.getAbsolutePath()));

				return;
			}
		}

		File gzippedFile = _gzip(hprofFile);

		if (gzippedFile == null) {
			return;
		}

		String yearMonth = LocalDate.now(
		).format(
			DateTimeFormatter.ofPattern("yyyy-MM")
		);

		String key = JenkinsResultsParserUtil.combine(
			"heap-dumps/", yearMonth, "/", _masterHostname, "/", _jobName, "/",
			_buildNumber, "/", _phase, "/", _slaveHostname, ".hprof.gz");

		testrayCloudBucket.createTestrayCloudObject(key, gzippedFile);
	}

	private File _gzip(File file) {
		File gzippedFile = new File(file.getAbsolutePath() + ".gz");

		try (FileInputStream fileInputStream = new FileInputStream(file);
			FileOutputStream fileOutputStream = new FileOutputStream(
				gzippedFile);
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
				fileOutputStream) {

				{
					def.setLevel(Deflater.BEST_COMPRESSION);
				}
			}) {

			byte[] buffer = new byte[8192];
			int length;

			while ((length = fileInputStream.read(buffer)) > 0) {
				gzipOutputStream.write(buffer, 0, length);
			}
		}
		catch (IOException ioException) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"ERROR: Failed to gzip heap dump ", file.getAbsolutePath(),
					": ", ioException.getMessage()));

			return null;
		}

		return gzippedFile;
	}

	private static final int _THROTTLE_MINUTES = 30;

	private final String _buildNumber;
	private final String _jobName;
	private final String _masterHostname;
	private final String _phase;
	private final String _slaveHostname;

}