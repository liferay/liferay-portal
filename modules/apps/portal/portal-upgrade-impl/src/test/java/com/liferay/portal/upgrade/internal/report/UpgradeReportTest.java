/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.report;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Mariano Álvaro Sáiz
 */
public class UpgradeReportTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		_dataAccessMockedStatic.close();
		_dbUpgraderMockedStatic.close();
		_portalUpgradeProcessMockedStatic.close();
	}

	@Test
	public void testGetReportDataDiagnostics() {
		Mockito.when(
			_upgradeRecorder.getDataCleanUpMessages()
		).thenReturn(
			HashMapBuilder.<String, Map<String, Integer>>put(
				DuplicateUniqueFinderRowsCleaner.class.getName(),
				HashMapBuilder.put(
					"Deleted row from TestTable due to duplicate values", 1
				).build()
			).build()
		);

		Mockito.when(
			_upgradeRecorder.getUpgradeProcessMessages()
		).thenReturn(
			HashMapBuilder.<String, ArrayList<String>>put(
				UpgradeProcess.class.getName(),
				new ArrayList<String>() {
					{
						add(
							"Completed upgrade process a.test.UpgradeTest in " +
								"10000 ms");
						add(
							"Completed upgrade process com.test.UpgradeTest " +
								"in 20000 ms");
						add(
							StringBundler.concat(
								"Completed upgrade process ",
								"com.test.UpgradeTestTable - Modifying table ",
								"TestTable to alter the type of the ",
								"TestColumn testColumnName to testTypeName ",
								"null in 30000 ms"));
					}
				}
			).build()
		);

		Map<String, Object> reportDataDiagnostics = ReflectionTestUtil.invoke(
			new UpgradeReport(), "_getReportDataDiagnostics",
			new Class<?>[] {UpgradeRecorder.class}, _upgradeRecorder);

		List<?> dataCleanUp = (List<?>)reportDataDiagnostics.get(
			"data.clean.up");

		Assert.assertEquals(
			StringBundler.concat(
				"Class name: ",
				DuplicateUniqueFinderRowsCleaner.class.getName(),
				"\n\tDeleted row from TestTable due to duplicate values\n"),
			dataCleanUp.get(
				0
			).toString());

		List<?> runningUpgradeProcesses = (List<?>)reportDataDiagnostics.get(
			"longest.upgrade.processes");

		Assert.assertEquals(
			"com.test.UpgradeTestTable took 30000 ms to complete\n",
			runningUpgradeProcesses.get(
				0
			).toString());
		Assert.assertEquals(
			"com.test.UpgradeTest took 20000 ms to complete\n",
			runningUpgradeProcesses.get(
				1
			).toString());
		Assert.assertEquals(
			"a.test.UpgradeTest took 10000 ms to complete\n",
			runningUpgradeProcesses.get(
				2
			).toString());
		Assert.assertEquals(
			runningUpgradeProcesses.toString(), 3,
			runningUpgradeProcesses.size());
	}

	private static final MockedStatic<DataAccess> _dataAccessMockedStatic =
		Mockito.mockStatic(DataAccess.class);
	private static final MockedStatic<DBUpgrader> _dbUpgraderMockedStatic =
		Mockito.mockStatic(DBUpgrader.class);
	private static final MockedStatic<PortalUpgradeProcess>
		_portalUpgradeProcessMockedStatic = Mockito.mockStatic(
			PortalUpgradeProcess.class);

	@Mock
	private UpgradeRecorder _upgradeRecorder;

}