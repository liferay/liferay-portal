/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryTable;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcos Martins
 */
@RunWith(Arquillian.class)
public class SegmentsEntryTypeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_segmentsEntry1 = _addSegmentsEntry(
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND);
		_segmentsEntry2 = _addSegmentsEntry(
			SegmentsEntryConstants.SOURCE_DEFAULT);
		_segmentsEntry3 = _addSegmentsEntry(
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			SegmentsEntryConstants.TYPE_REAL_TIME);
	}

	@After
	public void tearDown() throws Exception {
		_segmentsEntryLocalService.deleteSegmentsEntry(_segmentsEntry1);
		_segmentsEntryLocalService.deleteSegmentsEntry(_segmentsEntry2);
		_segmentsEntryLocalService.deleteSegmentsEntry(_segmentsEntry3);
	}

	@Test
	public void testUpgrade() throws Exception {
		Assert.assertNull(_getType(_segmentsEntry1));
		Assert.assertNull(_getType(_segmentsEntry2));
		Assert.assertEquals(
			SegmentsEntryConstants.TYPE_REAL_TIME,
			GetterUtil.getInteger(_getType(_segmentsEntry3)));

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		Assert.assertEquals(
			SegmentsEntryConstants.TYPE_BATCH,
			GetterUtil.getInteger(_getType(_segmentsEntry1)));
		Assert.assertEquals(
			SegmentsEntryConstants.TYPE_DEFAULT,
			GetterUtil.getInteger(_getType(_segmentsEntry2)));
		Assert.assertEquals(
			SegmentsEntryConstants.TYPE_REAL_TIME,
			GetterUtil.getInteger(_getType(_segmentsEntry3)));
	}

	private SegmentsEntry _addSegmentsEntry(String source) throws Exception {
		SegmentsEntry segmentsEntry = _addSegmentsEntry(
			source, SegmentsEntryConstants.TYPE_DEFAULT);

		_clearType(segmentsEntry);

		EntityCacheUtil.clearCache();

		return segmentsEntry;
	}

	private SegmentsEntry _addSegmentsEntry(String source, int type)
		throws Exception {

		return _segmentsEntryLocalService.addSegmentsEntry(
			null, RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			Collections.emptyMap(), true,
			CriteriaSerializer.serialize(new Criteria()), source, type,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _clearType(SegmentsEntry segmentsEntry) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update SegmentsEntry set type_ = null where segmentsEntryId " +
					"= ?")) {

			preparedStatement.setLong(1, segmentsEntry.getSegmentsEntryId());

			preparedStatement.executeUpdate();
		}
	}

	private Integer _getType(SegmentsEntry segmentsEntry) {
		List<Integer> types = _segmentsEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				SegmentsEntryTable.INSTANCE.type
			).from(
				SegmentsEntryTable.INSTANCE
			).where(
				SegmentsEntryTable.INSTANCE.segmentsEntryId.eq(
					segmentsEntry.getSegmentsEntryId())
			));

		return types.get(0);
	}

	private static final String _CLASS_NAME =
		"com.liferay.segments.internal.upgrade.v4_2_1." +
			"SegmentsEntryTypeUpgradeProcess";

	@DeleteAfterTestRun
	private Group _group;

	private SegmentsEntry _segmentsEntry1;
	private SegmentsEntry _segmentsEntry2;
	private SegmentsEntry _segmentsEntry3;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.segments.internal.upgrade.registry.SegmentsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}