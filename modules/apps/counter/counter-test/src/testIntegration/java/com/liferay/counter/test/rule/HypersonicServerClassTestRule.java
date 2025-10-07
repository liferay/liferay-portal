/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.test.rule;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.sql.Connection;
import java.sql.Statement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hsqldb.jdbc.JDBCDriver;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerConstants;

import org.junit.runner.Description;

/**
 * @author William Newbury
 * @author Shuyang Zhou
 */
public class HypersonicServerClassTestRule extends ClassTestRule<Server> {

	public static final HypersonicServerClassTestRule INSTANCE;

	@Override
	public void afterClass(Description description, Server testServer)
		throws Exception {

		try (Connection testServerConnection = JDBCDriver.getConnection(
				_DATABASE_URL_BASE + _DATABASE_NAME,
				new Properties() {
					{
						put("password", "");
						put("user", "sa");
					}
				});
			Statement statement = testServerConnection.createStatement()) {

			statement.execute("SHUTDOWN COMPACT");
		}

		testServer.stop();

		_deleteFolder(Paths.get(_HYPERSONIC_TEMP_DIR_NAME));
	}

	@Override
	public Server beforeClass(Description description) throws Exception {
		final CountDownLatch startCountDownLatch = new CountDownLatch(1);

		Server testServer = new Server() {

			@Override
			public int stop() {
				try (PrintWriter logPrintWriter = getLogWriter();
					PrintWriter errPrintWriter = getErrWriter()) {

					int state = super.stop();

					if (!_shutdownCountDownLatch.await(1, TimeUnit.MINUTES)) {
						throw new IllegalStateException(
							"Unable to shut down Hypersonic " + _DATABASE_NAME);
					}

					return state;
				}
				catch (InterruptedException interruptedException) {
					return ReflectionUtil.throwException(interruptedException);
				}
			}

			@Override
			protected synchronized void setState(int state) {
				super.setState(state);

				if (state == ServerConstants.SERVER_STATE_ONLINE) {
					startCountDownLatch.countDown();
				}
				else if (state == ServerConstants.SERVER_STATE_SHUTDOWN) {
					_shutdownCountDownLatch.countDown();
				}
			}

			private final CountDownLatch _shutdownCountDownLatch =
				new CountDownLatch(1);

		};

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"BACKUP DATABASE TO '" + _HYPERSONIC_TEMP_DIR_NAME +
				"' BLOCKING AS FILES");

		testServer.setErrWriter(
			new UnsyncPrintWriter(
				new File(
					_HYPERSONIC_TEMP_DIR_NAME, _DATABASE_NAME + ".err.log")));
		testServer.setLogWriter(
			new UnsyncPrintWriter(
				new File(
					_HYPERSONIC_TEMP_DIR_NAME, _DATABASE_NAME + ".std.log")));
		testServer.setDatabaseName(0, _DATABASE_NAME);
		testServer.setDatabasePath(
			0, _HYPERSONIC_TEMP_DIR_NAME + _DATABASE_NAME);

		testServer.start();

		if (!startCountDownLatch.await(1, TimeUnit.MINUTES)) {
			throw new IllegalStateException(
				"Unable to start up Hypersonic " + _DATABASE_NAME);
		}

		try (Connection testServerConnection = JDBCDriver.getConnection(
				_DATABASE_URL_BASE + _DATABASE_NAME,
				new Properties() {
					{
						put("password", "");
						put("user", "sa");
					}
				});
			Statement statement = testServerConnection.createStatement()) {

			statement.execute("SET WRITE_DELAY FALSE");
		}

		return testServer;
	}

	public List<String> getTestServerJdbcProperties() {
		if (_HYPERSONIC) {
			return Arrays.asList(
				"portal:jdbc.default.url=" + _DATABASE_URL,
				"portal:jdbc.default.username=sa",
				"portal:jdbc.default.password=");
		}

		return Collections.emptyList();
	}

	@Override
	protected org.junit.runners.model.Statement createClassStatement(
		org.junit.runners.model.Statement statement, Description description) {

		if (!_HYPERSONIC) {
			return statement;
		}

		return super.createClassStatement(statement, description);
	}

	private HypersonicServerClassTestRule() {
	}

	private void _deleteFolder(Path folderPath) throws Exception {
		if (!Files.exists(folderPath)) {
			return;
		}

		Files.walkFileTree(
			folderPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					if (ioException != null) {
						throw ioException;
					}

					Files.delete(dirPath);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Files.delete(filePath);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static final String _DATABASE_NAME;

	private static final String _DATABASE_URL;

	private static final String _DATABASE_URL_BASE =
		"jdbc:hsqldb:hsql://localhost/";

	private static final boolean _HYPERSONIC;

	private static final String _HYPERSONIC_TEMP_DIR_NAME =
		PropsValues.LIFERAY_HOME + "/data/hypersonic_temp/";

	static {
		String className = PropsUtil.get("jdbc.default.driverClassName");

		_HYPERSONIC = className.equals(JDBCDriver.class.getName());

		if (_HYPERSONIC) {
			String jdbcURL = PropsUtil.get("jdbc.default.url");

			int index = jdbcURL.lastIndexOf(CharPool.SLASH);

			if (index < 0) {
				throw new ExceptionInInitializerError(
					"Invalid Hypersonic JDBC URL " + jdbcURL);
			}

			String databaseName = jdbcURL.substring(index + 1);

			index = databaseName.indexOf(CharPool.SEMICOLON);

			if (index >= 0) {
				databaseName = databaseName.substring(0, index);
			}

			_DATABASE_NAME = databaseName;

			_DATABASE_URL = _DATABASE_URL_BASE + _DATABASE_NAME;
		}
		else {
			_DATABASE_NAME = null;
			_DATABASE_URL = null;
		}

		INSTANCE = new HypersonicServerClassTestRule();
	}

}