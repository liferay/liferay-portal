/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;

import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Jorge Díaz
 */
public class DBMigrationImportTest {

	@Test
	@TestInfo({"LPD-109433", "LPD-110097"})
	public void testHelp() throws Exception {
		_testHelp(
			Collections.singletonList("--help"), StringPool.BLANK, 0,
			Collections.singletonList("-Dlauncher=launcher.sh"),
			"Usage: launcher.sh <parameters>",
			"Liferay Portal Tools Database Migration Importer",
			"--include-custom-schema");
	}

	@Test
	@TestInfo("LPD-109433")
	public void testInvalidArguments() throws Exception {
		_testHelp(
			Collections.singletonList("--foo"), "Unrecognized option: --foo", 2,
			Collections.emptyList(),
			"Usage: db_migration_importer <parameters>");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testHelp(
			List<String> args, String errorMessage, int exitCode,
			List<String> jvmArgs, String... outputMessages)
		throws Exception {

		List<String> commands = new ArrayList<>();

		commands.add(
			StringBundler.concat(
				System.getProperty("java.home"), File.separator, "bin",
				File.separator, "java"));
		commands.addAll(jvmArgs);
		commands.add("-cp");
		commands.add(System.getProperty("java.class.path"));
		commands.add(DBMigrationImport.class.getName());
		commands.addAll(args);

		File errorFile = temporaryFolder.newFile();
		File outputFile = temporaryFolder.newFile();

		Process process = new ProcessBuilder(
			commands
		).redirectError(
			errorFile
		).redirectOutput(
			outputFile
		).start();

		Assert.assertEquals(exitCode, process.waitFor());

		String errorFileContent = new String(
			Files.readAllBytes(errorFile.toPath()), StringPool.UTF8);

		Assert.assertEquals(errorMessage, errorFileContent.trim());

		String outputFileContent = new String(
			Files.readAllBytes(outputFile.toPath()), StringPool.UTF8);

		for (String outputMessage : outputMessages) {
			Assert.assertTrue(outputFileContent.contains(outputMessage));
		}
	}

}