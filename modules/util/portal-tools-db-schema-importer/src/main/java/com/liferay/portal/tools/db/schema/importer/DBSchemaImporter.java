/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.schema.importer;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.db.schema.importer.jdbc.ConnectionConfigUtil;
import com.liferay.portal.tools.db.schema.importer.jdbc.DataSourceFactoryUtil;

import java.io.File;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBSchemaImporter {

	public static void main(String[] args) throws Exception {
		Options options = _getOptions();

		if ((args.length != 0) && args[0].equals("--help")) {
			_printHelpAndExit(options);
		}

		CommandLineParser commandLineParser = new DefaultParser();

		CommandLine commandLine = null;

		try {
			commandLine = commandLineParser.parse(options, args);
		}
		catch (ParseException parseException) {
			System.err.println(parseException.getMessage());

			_printHelpAndExit(options);
		}

		if (!DataSourceFactoryUtil.isValidSourceDatabase(
				commandLine.getOptionValue("source-jdbc-url"))) {

			System.err.println(
				"Source database must be DB2, MariaDB, MySQL, Oracle, or SQL " +
					"Server.");

			_printHelpAndExit(options);
		}

		if (!DataSourceFactoryUtil.isValidTargetDatabase(
				commandLine.getOptionValue("target-jdbc-url"))) {

			System.err.println("Target database must be PostgreSQL.");

			_printHelpAndExit(options);
		}

		try {
			System.out.println(
				"This tool is a beta feature. It is experimental and not " +
					"supported.");

			ConnectionConfigUtil.setBatchSize(
				commandLine.getOptionValue("jdbc-batch-size"));
			ConnectionConfigUtil.setFetchSize(
				commandLine.getOptionValue("jdbc-fetch-size"));

			DBSchemaImporterProcess dbSchemaImporterProcess =
				new DBSchemaImporterProcess(
					commandLine.getOptionValue("path"),
					commandLine.getOptionValue("source-jdbc-url"),
					commandLine.getOptionValue("source-password"),
					commandLine.getOptionValue("source-user"),
					commandLine.getOptionValue("target-jdbc-url"),
					commandLine.getOptionValue("target-password"),
					commandLine.getOptionValue("target-user"));

			dbSchemaImporterProcess.run();

			try (PrintWriter printWriter = new PrintWriter(
					new File(
						commandLine.getOptionValue("path"),
						"db_schema_import_report.txt"))) {

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					DateUtil.ISO_8601_PATTERN);

				printWriter.println(
					StringUtil.merge(
						new Object[] {
							"Export date: " +
								simpleDateFormat.format(new Date()),
							dbSchemaImporterProcess.getReleaseInfo(),
							StringPool.NEW_LINE, StringPool.NEW_LINE,
							dbSchemaImporterProcess.getDataSourceInfos()
						},
						StringPool.NEW_LINE));
			}

			System.exit(_LIFERAY_COMMON_EXIT_CODE_OK);
		}
		catch (Exception exception) {
			exception.printStackTrace(System.err);

			System.exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}
	}

	private static Options _getOptions() {
		Options options = new Options();

		options.addOption(null, "help", false, "Print help message.");
		options.addOption(
			null, "jdbc-batch-size", true,
			"Set the JDBC batch size. The default value is 2500.");
		options.addOption(
			null, "jdbc-fetch-size", true,
			"Set the JDBC result set fetch size. The default value is 2500.");
		options.addRequiredOption(
			null, "path", true, "Set the path of the source SQL files.");
		options.addRequiredOption(
			null, "source-jdbc-url", true, "Set the source JDBC URL.");
		options.addRequiredOption(
			null, "source-password", true,
			"Set the source database user password.");
		options.addRequiredOption(
			null, "source-user", true, "Set the source database user.");
		options.addRequiredOption(
			null, "target-jdbc-url", true, "Set the target JDBC URL.");
		options.addRequiredOption(
			null, "target-password", true,
			"Set the target database user password.");
		options.addRequiredOption(
			null, "target-user", true, "Set the target database user.");

		return options;
	}

	private static void _printHelpAndExit(Options options) {
		new HelpFormatter(
		).printHelp(
			"Liferay Portal Tools Database Schema Importer. This tool is a " +
				"beta feature. It is experimental and not supported.",
			options
		);

		System.exit(_LIFERAY_COMMON_EXIT_CODE_HELP);
	}

	/**
	 * https://github.com/liferay/liferay-docker/blob/master/_liferay_common.sh
	 */
	private static final int _LIFERAY_COMMON_EXIT_CODE_BAD = 1;

	private static final int _LIFERAY_COMMON_EXIT_CODE_HELP = 2;

	private static final int _LIFERAY_COMMON_EXIT_CODE_OK = 0;

}