/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.sequence.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.ReleaseLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.sequence.model.SequenceEntry;
import com.liferay.portal.tools.service.builder.test.sequence.service.SequenceEntryLocalService;

import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Hai.Yu
 */
@RunWith(Arquillian.class)
public class SequenceEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		DBType dbType = DBManagerUtil.getDBType();

		Assume.assumeTrue(
			(dbType == DBType.DB2) || (dbType == DBType.ORACLE) ||
			(dbType == DBType.POSTGRESQL));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dropSequenceEntryTables();

		_deleteRelease();

		Bundle bundle = FrameworkUtil.getBundle(
			SequenceEntryLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String location =
			"/com.liferay.portal.tools.service.builder.test.sequence.service." +
				"jar";

		try (InputStream inputStream =
				SequenceEntryLocalServiceTest.class.getResourceAsStream(
					location)) {

			_bundle = bundleContext.installBundle(location, inputStream);
		}

		_bundle.start();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_bundle.uninstall();

		_dropSequenceEntryTables();

		_deleteRelease();
	}

	@Test
	public void testAddSequenceEntry() {
		Assert.assertNotNull(_addSequenceEntry());
	}

	@Test
	public void testAddSequenceEntryIncrementsPrimaryKeyByOne() {
		SequenceEntry sequenceEntry1 = _addSequenceEntry();
		SequenceEntry sequenceEntry2 = _addSequenceEntry();
		SequenceEntry sequenceEntry3 = _addSequenceEntry();

		Assert.assertEquals(
			sequenceEntry1.getSequenceEntryId() + 1,
			sequenceEntry2.getSequenceEntryId());
		Assert.assertEquals(
			sequenceEntry2.getSequenceEntryId() + 1,
			sequenceEntry3.getSequenceEntryId());
	}

	private static void _deleteRelease() {
		Release release = ReleaseLocalServiceUtil.fetchRelease(
			"com.liferay.portal.tools.service.builder.test.sequence.service");

		if (release != null) {
			ReleaseLocalServiceUtil.deleteRelease(release);
		}
	}

	private static void _dropSequenceEntryTables() {
		_runSQL("drop sequence id_sequence");
		_runSQL("drop table SequenceEntry");
	}

	private static void _runSQL(String sql) {
		try {
			DB db = DBManagerUtil.getDB();

			db.runSQL(sql);
		}
		catch (IOException | SQLException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private SequenceEntry _addSequenceEntry() {
		return _sequenceEntryLocalService.addSequenceEntry(
			_sequenceEntryLocalService.createSequenceEntry(0));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SequenceEntryLocalServiceTest.class);

	private static Bundle _bundle;

	@Inject
	private SequenceEntryLocalService _sequenceEntryLocalService;

}