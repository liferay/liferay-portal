/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.CounterDataCleanupPreupgradeProcess;

import java.sql.Connection;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class CounterDataCleanupPreupgradeProcessTest
	extends CounterDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		upgrade();
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testUpgradeCompanyDoesNotAffectKernelCounter()
		throws Exception {

		long companyId;

		if (PropsValues.COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED) {
			companyId =
				CounterLocalServiceUtil.getCurrentId(Company.class.getName()) +
					1;
		}
		else {
			companyId = Long.MAX_VALUE;
		}

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from Company where companyId = " + companyId),
			(UnsafeRunnable<Exception>)() -> runSQL(
				"insert into Company (mvccVersion, companyId) values (0 ," +
					companyId + ")"),
			(UnsafeConsumer<List<String>, Exception>)messages -> {
				if (PropsValues.COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED) {
					Assert.assertEquals(
						messages.toString(), 1, messages.size());
					Assert.assertTrue(
						messages.contains(
							StringBundler.concat(
								"Counter ", Company.class.getName(),
								" has been reset to value ", companyId)));
				}
				else {
					Assert.assertTrue(messages.toString(), messages.isEmpty());
				}
			});
	}

	@Test
	public void testUpgradeDLFileEntryKernelCounter() throws Exception {
		long fileEntryId =
			CounterLocalServiceUtil.getCurrentId(Counter.class.getName()) + 10;
		long name = CounterLocalServiceUtil.increment(
			DLFileEntry.class.getName());

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId),
			(UnsafeRunnable<Exception>)() -> runSQL(
				StringBundler.concat(
					"insert into DLFileEntry (mvccVersion, ctCollectionId, ",
					"fileEntryId, name) values (0, 0,", fileEntryId, ", '",
					name, "')")),
			(UnsafeConsumer<List<String>, Exception>)messages -> {
				Assert.assertEquals(messages.toString(), 1, messages.size());
				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Counter ", Counter.class.getName(),
							" has been reset to value ", fileEntryId,
							" due to table ",
							_dbInspector.normalizeName("DLFileEntry"))));
			});
	}

	@Test
	public void testUpgradeDLFileEntrySpecificCounter() throws Exception {
		long fileEntryId = CounterLocalServiceUtil.increment();
		long name =
			CounterLocalServiceUtil.getCurrentId(DLFileEntry.class.getName()) +
				100;

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId),
			(UnsafeRunnable<Exception>)() -> runSQL(
				StringBundler.concat(
					"insert into DLFileEntry (mvccVersion, ctCollectionId, ",
					"fileEntryId, name) values (0, 0,", fileEntryId, ", '",
					name, "')")),
			(UnsafeConsumer<List<String>, Exception>)messages -> {
				Assert.assertEquals(messages.toString(), 1, messages.size());
				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Counter ", DLFileEntry.class.getName(),
							" has been reset to value ", name)));
			});
	}

	@Test
	public void testUpgradeKernelCounter() throws Exception {
		long roleId =
			CounterLocalServiceUtil.getCurrentId(Counter.class.getName()) + 10;

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from Role_ where roleId = " + roleId),
			(UnsafeRunnable<Exception>)() -> runSQL(
				StringBundler.concat(
					"insert into Role_ (mvccVersion, ctCollectionId, roleId) ",
					"values (0, 0,", roleId, ")")),
			(UnsafeConsumer<List<String>, Exception>)messages -> {
				Assert.assertEquals(messages.toString(), 1, messages.size());
				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Counter ", Counter.class.getName(),
							" has been reset to value ", roleId,
							" due to table ",
							_dbInspector.normalizeName("Role_"))));
			});
	}

	@Test
	public void testUpgradeLayoutSpecificCounterUnused() throws Exception {
		String counterName = StringBundler.concat(
			Layout.class.getName(), "#", RandomTestUtil.nextLong(), "#true");

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from Counter where name = '" + counterName + "'"),
			(UnsafeRunnable<Exception>)() -> runSQL(
				"insert into Counter (name, currentId) values ('" +
					counterName + "', 100 )"),
			(UnsafeConsumer<List<String>, Exception>)
				messages -> Assert.assertTrue(
					messages.toString(),
					messages.contains(
						"Deleted counter " + counterName +
							" because it is unused")));
	}

	@Test
	public void testUpgradeLayoutSpecificCounterUpdated() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		String counterName = StringBundler.concat(
			Layout.class.getName(), "#", groupId, "#true");

		long plid = CounterLocalServiceUtil.increment(Layout.class.getName());

		_test(
			(UnsafeRunnable<Exception>)() -> {
				runSQL(
					"delete from Counter where name = '" + counterName + "'");
				runSQL("delete from Layout where plid = " + plid);
			},
			(UnsafeRunnable<Exception>)() -> {
				runSQL(
					"insert into Counter (name, currentId) values ('" +
						counterName + "', 100 )");
				runSQL(
					StringBundler.concat(
						"insert into Layout (mvccVersion, ctCollectionId, ",
						"plid, groupId, privateLayout, layoutId ) values (0, ",
						"0, ", plid, ", ", groupId, ", [$TRUE$], 1000)"));
			},
			(UnsafeConsumer<List<String>, Exception>)
				messages -> Assert.assertTrue(
					messages.toString(),
					messages.contains(
						"Counter " + counterName +
							" has been reset to value 1000")));
	}

	@Test
	public void testUpgradeSpecificCounter() throws Exception {
		long regionId =
			CounterLocalServiceUtil.getCurrentId(Region.class.getName()) + 10;

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from Region where regionId = " + regionId),
			(UnsafeRunnable<Exception>)() -> runSQL(
				StringBundler.concat(
					"insert into Region (mvccVersion, ctCollectionId, ",
					"regionId) values (0, 0,", regionId, ")")),
			(UnsafeConsumer<List<String>, Exception>)messages -> {
				Assert.assertEquals(messages.toString(), 1, messages.size());
				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Counter ", Region.class.getName(),
							" has been reset to value ", regionId)));
			});
	}

	@Test
	public void testUpgradeSpecificCounterNotMatchingAnyTables()
		throws Exception {

		String counterName =
			"com.liferay.portal.kernel." + RandomTestUtil.randomString();

		_test(
			(UnsafeRunnable<Exception>)() -> runSQL(
				"delete from Counter where name = '" + counterName + "'"),
			(UnsafeRunnable<Exception>)() -> runSQL(
				"insert into Counter (name, currentId) values ('" +
					counterName + "', 100 )"),
			(UnsafeConsumer<List<String>, Exception>)
				messages -> Assert.assertTrue(
					messages.toString(), messages.isEmpty()));
	}

	private void _test(
			UnsafeRunnable<Exception> postUnsafeRunnable,
			UnsafeRunnable<Exception> preupgradeUnsafeRunnable,
			UnsafeConsumer<List<String>, Exception> verifyUnsafeConsumer)
		throws Exception {

		preupgradeUnsafeRunnable.run();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CounterDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			upgrade();

			verifyUnsafeConsumer.accept(logCapture.getMessages());
		}
		finally {
			postUnsafeRunnable.run();
		}
	}

	private Connection _connection;
	private DBInspector _dbInspector;

}