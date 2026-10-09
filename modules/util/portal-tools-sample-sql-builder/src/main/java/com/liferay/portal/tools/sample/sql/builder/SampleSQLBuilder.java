/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.sample.sql.builder;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncBufferedWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.tools.sample.sql.builder.io.CharPipe;
import com.liferay.portal.tools.sample.sql.builder.io.UnsyncTeeWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import java.nio.channels.FileChannel;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class SampleSQLBuilder {

	public SampleSQLBuilder() {
		ToolDependencies.wireBasic();

		// Generic

		File tempDir = new File(_OUTPUT_DIR, "temp");

		tempDir.mkdirs();

		Reader reader = generateSQL();

		try {

			// Specific

			compressSQL(reader, tempDir);

			// Merge

			if (BenchmarksPropsValues.OUTPUT_MERGE) {
				File sqlFile = new File(
					_OUTPUT_DIR,
					"sample-" + BenchmarksPropsValues.DB_TYPE + ".sql");

				FileUtil.delete(sqlFile);

				mergeSQL(tempDir, sqlFile);
			}
			else {
				File outputDir = new File(_OUTPUT_DIR, "output");

				FileUtil.deltree(outputDir);

				if (!tempDir.renameTo(outputDir)) {

					// This will only happen when temp and output directories
					// are on different file systems

					FileUtil.copyDirectory(tempDir, outputDir);
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			FileUtil.deltree(tempDir);
		}
	}

	protected void compressSQL(
			DB db, File directory, Map<String, Writer> sqlWriters,
			Map<String, StringBundler> sqls, String sql)
		throws IOException, SQLException {

		String tableName = null;

		if (sql.startsWith("create")) {
			if (sql.startsWith("create table ")) {
				tableName = sql.substring(
					13, sql.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
			}
			else {
				int index = sql.indexOf(" on ");

				tableName = sql.substring(
					index + 4, sql.indexOf(StringPool.OPEN_PARENTHESIS) - 1);
			}

			sql = db.buildSQL(sql) + StringPool.NEW_LINE;

			writeToSQLFile(directory, tableName, sqlWriters, sql);

			return;
		}

		sql = sql.substring(12);

		tableName = sql.substring(0, sql.indexOf(' '));

		int index = sql.indexOf(" values ") + 8;

		StringBundler sb = sqls.get(tableName);

		if ((sb == null) || (sb.index() == 0)) {
			sb = new StringBundler();

			sqls.put(tableName, sb);

			sb.append("insert into ");
			sb.append(sql.substring(0, index));
			sb.append(StringPool.NEW_LINE);
		}
		else {
			sb.append(StringPool.COMMA);
			sb.append(StringPool.NEW_LINE);
		}

		String values = sql.substring(index, sql.length() - 1);

		sb.append(values);

		if (sb.index() >= BenchmarksPropsValues.OPTIMIZE_BUFFER_SIZE) {
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);

			sql = db.buildSQL(sb.toString());

			sb.setIndex(0);

			writeToSQLFile(directory, tableName, sqlWriters, sql);
		}
	}

	protected void compressSQL(Reader reader, File dir) throws Exception {
		DB db = DBManagerUtil.getDB(BenchmarksPropsValues.DB_TYPE, null);

		if ((BenchmarksPropsValues.DB_TYPE == DBType.MARIADB) ||
			(BenchmarksPropsValues.DB_TYPE == DBType.MYSQL)) {

			db = new SampleMySQLDB(db.getMajorVersion(), db.getMinorVersion());
		}
		else if (BenchmarksPropsValues.DB_TYPE == DBType.POSTGRESQL) {
			db = new SamplePostgreSQLDB(
				db.getMajorVersion(), db.getMinorVersion());
		}

		Map<String, Writer> sqlWriters = new HashMap<>();
		Map<String, StringBundler> insertSQLs = new HashMap<>();
		List<String> counterSQLs = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String s = null;

			while ((_freeMarkerThrowable == null) &&
				   ((s = unsyncBufferedReader.readLine()) != null)) {

				s = s.trim();

				if (s.length() <= 0) {
					continue;
				}

				if (s.startsWith("create") || s.startsWith("insert into ")) {
					if (!s.endsWith(");")) {
						StringBundler sb = new StringBundler();

						while (!s.endsWith(");")) {
							sb.append(s);
							sb.append(StringPool.NEW_LINE);

							s = unsyncBufferedReader.readLine();
						}

						sb.append(s);

						s = sb.toString();
					}

					compressSQL(db, dir, sqlWriters, insertSQLs, s);
				}
				else if (!s.contains("##")) {
					counterSQLs.add(s);
				}
			}
		}

		if (_freeMarkerThrowable != null) {
			throw new Exception(
				"Unable to process FreeMarker template ", _freeMarkerThrowable);
		}

		for (Map.Entry<String, StringBundler> entry : insertSQLs.entrySet()) {
			String tableName = entry.getKey();
			StringBundler sb = entry.getValue();

			if (sb.index() > 0) {
				String insertSQL = db.buildSQL(sb.toString());

				writeToSQLFile(dir, tableName, sqlWriters, insertSQL);
			}

			try (Writer insertSQLWriter = sqlWriters.remove(tableName)) {
				insertSQLWriter.write(StringPool.SEMICOLON);
				insertSQLWriter.write(StringPool.NEW_LINE);
			}
		}

		for (Map.Entry<String, Writer> entry : sqlWriters.entrySet()) {
			Writer writer = entry.getValue();

			writer.close();
		}

		try (Writer counterSQLWriter = new FileWriter(
				new File(dir, "Counter.sql"), true)) {

			for (String counterSQL : counterSQLs) {
				counterSQL = db.buildSQL(counterSQL);

				counterSQLWriter.write(counterSQL);

				counterSQLWriter.write(StringPool.NEW_LINE);
			}
		}
	}

	protected Writer createFileWriter(File file) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		Writer writer = new OutputStreamWriter(fileOutputStream);

		return new UnsyncBufferedWriter(writer, _WRITER_BUFFER_SIZE);
	}

	protected Reader generateSQL() {
		CharPipe charPipe = new CharPipe(_PIPE_BUFFER_SIZE);

		Thread thread = new Thread(
			() -> {
				try (CSVFileWriter csvFileWriter = new CSVFileWriter(
						new File(_OUTPUT_DIR));
					Writer sampleSQLWriter = new UnsyncTeeWriter(
						new UnsyncBufferedWriter(
							charPipe.getWriter(), _WRITER_BUFFER_SIZE),
						createFileWriter(
							new File(_OUTPUT_DIR, "sample.sql")))) {

					_loadCreateSQLs(sampleSQLWriter);

					FreeMarkerUtil.process(
						BenchmarksPropsValues.SCRIPT,
						HashMapBuilder.<String, Object>put(
							"csvFileWriter", csvFileWriter
						).put(
							"dataFactory", new DataFactory()
						).build(),
						sampleSQLWriter);
				}
				catch (Throwable throwable) {
					_freeMarkerThrowable = throwable;
				}
				finally {
					charPipe.close();
				}
			});

		thread.start();

		return charPipe.getReader();
	}

	protected void mergeSQL(File inputDir, File outputSQLFile)
		throws IOException {

		FileOutputStream outputSQLFileOutputStream = new FileOutputStream(
			outputSQLFile);

		try (FileChannel outputFileChannel =
				outputSQLFileOutputStream.getChannel()) {

			File counterSQLFile = null;

			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();

				if (inputFileName.equals("Counter.sql")) {
					counterSQLFile = inputFile;

					continue;
				}

				mergeSQL(inputFile, outputFileChannel);
			}

			if (counterSQLFile != null) {
				mergeSQL(counterSQLFile, outputFileChannel);
			}
		}
	}

	protected void mergeSQL(File inputFile, FileChannel outputFileChannel)
		throws IOException {

		FileInputStream inputFileInputStream = new FileInputStream(inputFile);

		try (FileChannel inputFileChannel = inputFileInputStream.getChannel()) {
			inputFileChannel.transferTo(
				0, inputFileChannel.size(), outputFileChannel);
		}

		inputFile.delete();
	}

	protected void writeToSQLFile(
			File dir, String tableName, Map<String, Writer> sqlWriters,
			String sql)
		throws IOException {

		Writer writer = sqlWriters.get(tableName);

		if (writer == null) {
			File file = new File(dir, tableName + ".sql");

			writer = createFileWriter(file);

			sqlWriters.put(tableName, writer);
		}

		writer.write(sql);
	}

	private void _loadCreateSQL(URL url, Writer writer) throws IOException {
		try (InputStream inputStream = url.openStream();

			Reader reader = new InputStreamReader(inputStream);

			BufferedReader bufferedReader = new BufferedReader(reader)) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				writer.append(line);
				writer.append(System.lineSeparator());
			}
		}
	}

	private void _loadCreateSQLs(Writer writer) throws IOException {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		for (String sqlFileName : _createSQLTemplateFileNames) {
			if (sqlFileName.contains("META-INF")) {
				Enumeration<URL> enumeration = classLoader.getResources(
					sqlFileName);

				while (enumeration.hasMoreElements()) {
					_loadCreateSQL(enumeration.nextElement(), writer);
				}
			}
			else {
				_loadCreateSQL(classLoader.getResource(sqlFileName), writer);
			}
		}

		writer.flush();
	}

	private static final String _OUTPUT_DIR = System.getProperty("user.dir");

	private static final int _PIPE_BUFFER_SIZE = 16 * 1024 * 1024;

	private static final int _WRITER_BUFFER_SIZE = 16 * 1024;

	private static final List<String> _createSQLTemplateFileNames =
		Arrays.asList(
			"com/liferay/portal/tools/sql/dependencies/portal-tables.sql",
			"com/liferay/portal/tools/sql/dependencies/portal-data-counter.sql",
			"com/liferay/portal/tools/sql/dependencies/indexes.sql",
			"META-INF/sql/tables.sql", "META-INF/sql/indexes.sql");

	private volatile Throwable _freeMarkerThrowable;

}