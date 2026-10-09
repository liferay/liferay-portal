/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmarks;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.tools.benchmarks.task.BenchmarksTask;
import com.liferay.portal.tools.benchmarks.task.LoginBenchmarksTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Tina Tian
 */
public class BenchmarksTest {

	public BenchmarksTest() {
		_excludedCompanyWebIds = StringUtil.split(
			System.getProperty(
				"benchmarks.test.excluded.company.web.ids", "liferay.com"));
		_jdbcDriverClassName = System.getProperty(
			"benchmarks.test.jdbc.driverClassName", "com.mysql.cj.jdbc.Driver");
		_jdbcPassword = System.getProperty(
			"benchmarks.test.jdbc.password", StringPool.BLANK);
		_jdbcURL = System.getProperty(
			"benchmarks.test.jdbc.url",
			StringBundler.concat(
				"jdbc:mysql://localhost/lportal?characterEncoding=UTF-8&",
				"dontTrackOpenResources=true&holdResultsOpenOverStatementClose",
				"=true&serverTimezone=GMT&useFastDateParsing=false&",
				"useUnicode=true"));
		_jdbcUserName = System.getProperty(
			"benchmarks.test.jdbc.username", StringPool.BLANK);
		_runCount = GetterUtil.getInteger(
			System.getProperty("benchmarks.test.run.count", "1"));
		_skipWarmUp = Boolean.parseBoolean(
			System.getProperty("benchmarks.test.skip.warm.up", "false"));
		_threadCount = GetterUtil.getInteger(
			System.getProperty("benchmarks.test.thread.count", "1"));
		_userPassword = System.getProperty(
			"benchmarks.test.user.password", "test");

		StringBundler sb = new StringBundler(16);

		sb.append("\nCurrent properties:\n\n\tExcluded company web IDs:");
		sb.append(_excludedCompanyWebIds);
		sb.append("\n\tJDBC driver class name: ");
		sb.append(_jdbcDriverClassName);
		sb.append("\n\tJDBC password: ");
		sb.append(_jdbcPassword);
		sb.append("\n\tJDBC URL: ");
		sb.append(_jdbcURL);
		sb.append("\n\tJDBC user name: ");
		sb.append(_jdbcUserName);
		sb.append("\n\tRun count: ");
		sb.append(_runCount);
		sb.append("\n\tThread count: ");
		sb.append(_threadCount);
		sb.append("\n\tUser password: ");
		sb.append(_userPassword);

		System.out.println(sb);
	}

	public void execute() throws Exception {
		System.out.println("Running Login test ...");

		Function<String[], BenchmarksTask> benchmarksTaskFunction =
			testData -> new LoginBenchmarksTask(
				testData[0], testData[1], _userPassword, 8080);

		String[][] data = _getData();

		if (!_skipWarmUp) {
			System.out.println("\nStarting warm up");

			_execute(
				benchmarksTaskFunction, data, index -> index, data.length, 1);

			System.out.println("\nFinished warm up");
		}

		System.out.println("\nStarting test");

		_execute(
			benchmarksTaskFunction, data, index -> index % data.length,
			_runCount, _threadCount);

		System.out.println("\nFinished test");
	}

	private void _execute(
			Function<String[], BenchmarksTask> benchmarksTaskFunction,
			String[][] data, Function<Integer, Integer> indexFunction,
			int runCount, int threadCount)
		throws Exception {

		ExecutorService executorService = new ThreadPoolExecutor(
			threadCount, threadCount, 0, TimeUnit.SECONDS,
			new LinkedBlockingDeque<>());
		List<Future<Void>> futures = new ArrayList<>();
		Statistics statistics = new Statistics(runCount);

		for (int i = 0; i < runCount; i++) {
			int finalI = i;

			futures.add(
				executorService.submit(
					() -> {
						BenchmarksTask benchmarksTask =
							benchmarksTaskFunction.apply(
								data[indexFunction.apply(finalI)]);

						statistics.addResults(benchmarksTask.execute());

						return null;
					}));
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		statistics.printStatistics();

		executorService.shutdown();
	}

	private String[][] _getData() throws Exception {
		StringBundler sb = new StringBundler();

		sb.append("select hostname, emailAddress from Company, VirtualHost, ");
		sb.append("User_ where Company.companyId = User_.companyId and ");
		sb.append("Company.companyId = VirtualHost.companyId and ");

		if (!_excludedCompanyWebIds.isEmpty()) {
			sb.append("Company.webId not in (");

			for (int i = 0; i < _excludedCompanyWebIds.size(); i++) {
				sb.append("'");
				sb.append(_excludedCompanyWebIds.get(i));
				sb.append("'");

				if (i >= (_excludedCompanyWebIds.size() - 1)) {
					continue;
				}

				sb.append(", ");
			}

			sb.append(") and ");
		}

		sb.append("User_.type_ = 1;");

		String sql = sb.toString();

		System.out.println("\nSQL : " + sql);

		Class.forName(_jdbcDriverClassName);

		List<String[]> data = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(
				_jdbcURL, _jdbcUserName, _jdbcPassword);

			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				data.add(
					new String[] {
						resultSet.getString("emailAddress"),
						resultSet.getString("hostname")
					});
			}
		}

		return data.toArray(new String[0][0]);
	}

	private final List<String> _excludedCompanyWebIds;
	private final String _jdbcDriverClassName;
	private final String _jdbcPassword;
	private final String _jdbcURL;
	private final String _jdbcUserName;
	private final int _runCount;
	private final boolean _skipWarmUp;
	private final int _threadCount;
	private final String _userPassword;

}