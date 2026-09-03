/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.util.DatabaseUtil;
import com.liferay.portal.tools.db.partition.migration.validator.util.ValidatorUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Luis Ortiz
 */
public class DBPartitionMigrationValidator {

	public static void main(String[] args) {
		if (args.length != 0) {
			if (args[0].equals("export")) {
				_export(ArrayUtil.remove(args, args[0]));

				_exit(_LIFERAY_COMMON_EXIT_CODE_OK);
			}
			else if (args[0].equals("validate")) {
				_validate(ArrayUtil.remove(args, args[0]));

				_exit(_LIFERAY_COMMON_EXIT_CODE_OK);
			}
		}

		_printHelp();
	}

	private static void _exit(int code) {
		try {
			if (_connection != null) {
				_connection.close();
			}
		}
		catch (SQLException sqlException) {
			System.err.println(sqlException);
		}

		System.exit(code);
	}

	private static void _export(String[] args) {
		Options options = _getExportOptions();

		CommandLine commandLine = null;

		try {
			CommandLineParser commandLineParser = new DefaultParser();

			commandLine = commandLineParser.parse(options, args);
		}
		catch (ParseException parseException) {
			if (!ArrayUtil.contains(args, "--help")) {
				System.err.println(parseException.getMessage());
			}

			_printHelp();

			_exit(_LIFERAY_COMMON_EXIT_CODE_HELP);
		}

		String jdbcURL = DatabaseUtil.replaceSchemaName(
			commandLine.getOptionValue("jdbc-url"),
			commandLine.getOptionValue("schema-name"));

		try {
			if (DatabaseUtil.isPostgreSQL(jdbcURL)) {
				Class.forName("org.postgresql.Driver");
			}
			else {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}

			_connection = DriverManager.getConnection(
				jdbcURL, commandLine.getOptionValue("user"),
				commandLine.getOptionValue("password"));
		}
		catch (Exception exception) {
			System.err.println(
				"Unable to connect to database with the specified parameters:");

			exception.printStackTrace();

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}

		try {
			String exportFilePath = _write(
				DatabaseUtil.exportLiferayDatabase(
					_connection,
					Long.parseLong(commandLine.getOptionValue("company-id"))),
				commandLine.getOptionValue("output-dir"));

			System.out.println(
				"Export file generated successfully in " + exportFilePath);
		}
		catch (Exception exception) {
			System.out.println("Unable to generate the export file:");

			exception.printStackTrace();

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}
	}

	private static Options _getExportOptions() {
		Options options = new Options();

		options.addRequiredOption(
			null, "company-id", true, "Set the company ID.");
		options.addOption(
			null, "output-dir", true, "Set the output directory.");
		options.addRequiredOption(null, "jdbc-url", true, "Set the JDBC URL.");
		options.addRequiredOption(
			null, "password", true, "Set the database user password.");
		options.addOption(
			null, "schema-name", true, "Set the database schema name.");
		options.addRequiredOption(
			null, "user", true, "Set the database user name.");

		return options;
	}

	private static Options _getMainOptions() {
		Options options = new Options();

		options.addOption(
			new Option(null, "export", false, "Export validation file."));
		options.addOption(
			new Option(
				null, "validate", false,
				"Validate source and target validation files."));

		return options;
	}

	private static Options _getValidationOptions() {
		Options options = new Options();

		options.addRequiredOption(
			null, "source-file", true,
			"Set the path to the source validation file.");
		options.addRequiredOption(
			null, "target-file", true,
			"Set the path to the target validation file.");

		return options;
	}

	private static void _printHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

		helpFormatter.printWrapped(
			printWriter, _HELP_WIDTH,
			"Liferay Database Partition Migration Validator Tool.\n\n");
		helpFormatter.printUsage(
			printWriter, _HELP_WIDTH,
			"./db_partition_migration_validator.sh <command> [parameters]");
		helpFormatter.printWrapped(printWriter, _HELP_WIDTH, "\nCommands:");
		helpFormatter.printOptions(
			printWriter, _HELP_WIDTH, _getMainOptions(), _HELP_LEFT_PAD,
			_HELP_DESC_PAD);
		helpFormatter.printWrapped(
			printWriter, _HELP_WIDTH, _HELP_DESC_PAD, "\nExport parameters:");
		helpFormatter.printOptions(
			printWriter, _HELP_WIDTH, _getExportOptions(), _HELP_LEFT_PAD,
			_HELP_DESC_PAD);
		helpFormatter.printWrapped(
			printWriter, _HELP_WIDTH, "\nValidate parameters:");
		helpFormatter.printOptions(
			printWriter, _HELP_WIDTH, _getValidationOptions(), _HELP_LEFT_PAD,
			_HELP_DESC_PAD);

		printWriter.flush();

		String helpMessage = byteArrayOutputStream.toString();

		helpMessage = StringUtil.replace(helpMessage, "--export", "export");
		helpMessage = StringUtil.replace(helpMessage, "--validate", "validate");

		System.out.println(helpMessage);
	}

	private static LiferayDatabase _read(String path) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper() {
			{
				SimpleModule simpleModule = new SimpleModule();

				simpleModule.addDeserializer(
					Version.class, new VersionStdDeserializer());

				registerModule(simpleModule);
			}
		};

		return objectMapper.readValue(new File(path), LiferayDatabase.class);
	}

	private static void _validate(String[] args) {
		Options options = _getValidationOptions();

		CommandLine commandLine = null;

		try {
			CommandLineParser commandLineParser = new DefaultParser();

			commandLine = commandLineParser.parse(options, args);
		}
		catch (ParseException parseException) {
			if (!ArrayUtil.contains(args, "--help")) {
				System.err.println(parseException.getMessage());
			}

			_printHelp();

			_exit(_LIFERAY_COMMON_EXIT_CODE_HELP);
		}

		try {
			_sourceLiferayDatabase = _read(
				commandLine.getOptionValue("source-file"));
		}
		catch (IOException ioException) {
			System.err.println(
				"Unable to read source file with the specified parameters:");

			ioException.printStackTrace();

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}

		try {
			_targetLiferayDatabase = _read(
				commandLine.getOptionValue("target-file"));
		}
		catch (IOException ioException) {
			System.err.println(
				"Unable to read target file with the specified parameters:");

			ioException.printStackTrace();

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}

		if (!_targetLiferayDatabase.isExportedCompanyDefault()) {
			System.err.println("Target is not the default partition");

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}

		Recorder recorder = ValidatorUtil.validateDatabases(
			_sourceLiferayDatabase, _targetLiferayDatabase);

		if (recorder.hasErrors() || recorder.hasWarnings()) {
			recorder.printMessages();

			_exit(_LIFERAY_COMMON_EXIT_CODE_BAD);
		}
	}

	private static String _write(LiferayDatabase liferayDatabase, String path)
		throws IOException {

		File exportDir = null;

		if (path == null) {
			exportDir = new File(".", "exports");

			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
		}

		if (exportDir == null) {
			exportDir = new File(path);

			if ((!exportDir.exists() && !exportDir.mkdirs()) ||
				!Files.isWritable(exportDir.toPath())) {

				throw new IOException("Path " + path + " is not writable");
			}
		}

		DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();

		defaultPrettyPrinter.indentArraysWith(
			DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

		ObjectMapper objectMapper = new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setDefaultPrettyPrinter(defaultPrettyPrinter);
			}
		};

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");

		File exportFile = new File(
			exportDir,
			StringBundler.concat(
				simpleDateFormat.format(liferayDatabase.getDate()), "_export_",
				liferayDatabase.getExportedCompanyId(), ".json"));

		objectMapper.writeValue(exportFile, liferayDatabase);

		return exportFile.getCanonicalPath();
	}

	private static final int _HELP_DESC_PAD = 5;

	private static final int _HELP_LEFT_PAD = 2;

	private static final int _HELP_WIDTH = 80;

	/**
	 * https://github.com/liferay/liferay-docker/blob/master/_liferay_common.sh
	 */
	private static final int _LIFERAY_COMMON_EXIT_CODE_BAD = 1;

	private static final int _LIFERAY_COMMON_EXIT_CODE_HELP = 2;

	private static final int _LIFERAY_COMMON_EXIT_CODE_OK = 0;

	private static Connection _connection;
	private static LiferayDatabase _sourceLiferayDatabase;
	private static LiferayDatabase _targetLiferayDatabase;

	private static class VersionStdDeserializer
		extends StdDeserializer<Version> {

		public VersionStdDeserializer() {
			this(null);
		}

		@Override
		public Version deserialize(
				JsonParser jsonParser,
				DeserializationContext deserializationContext)
			throws IOException, JacksonException {

			JsonNode jsonNode = jsonParser.getCodec(
			).readTree(
				jsonParser
			);

			return new Version(
				(Integer)jsonNode.get(
					"major"
				).numberValue(),
				(Integer)jsonNode.get(
					"minor"
				).numberValue(),
				(Integer)jsonNode.get(
					"micro"
				).numberValue(),
				jsonNode.get(
					"qualifier"
				).asText());
		}

		protected VersionStdDeserializer(Class<?> clazz) {
			super(clazz);
		}

	}

}