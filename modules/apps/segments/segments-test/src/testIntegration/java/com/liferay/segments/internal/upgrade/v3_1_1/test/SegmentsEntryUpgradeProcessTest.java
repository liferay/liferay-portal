/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v3_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntryTable;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
public class SegmentsEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Locale locale = LocaleUtil.fromLanguageId(
			UpgradeProcessUtil.getDefaultLanguageId(_group.getCompanyId()));

		_addSegmentsEntry(null, locale);
		_addSegmentsEntry(
			JSONUtil.put(
				"operatorName", "eq"
			).put(
				"propertyName", "deviceBrand"
			).put(
				"value", "test"
			).toString(),
			locale);
		_addSegmentsEntry(
			JSONUtil.put(
				"operatorName", "eq"
			).put(
				"propertyName", "deviceModel"
			).put(
				"value", "test"
			).toString(),
			locale);
		_addSegmentsEntry(
			JSONUtil.put(
				"operatorName", "eq"
			).put(
				"propertyName", "deviceScreenResolutionHeight"
			).put(
				"value", "1000.0"
			).toString(),
			locale);
		_addSegmentsEntry(
			JSONUtil.put(
				"operatorName", "eq"
			).put(
				"propertyName", "deviceScreenResolutionWidth"
			).put(
				"value", "1000.0"
			).toString(),
			locale);
	}

	@After
	public void tearDown() throws Exception {
		_segmentsEntryLocalService.deleteSegmentsEntries(
			SegmentsEntryConstants.SOURCE_DEFAULT);
	}

	@Test
	public void testUpgrade() throws Exception {
		Assert.assertEquals(5, _getActiveSegmentEntriesCount());

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();

		Assert.assertEquals(1, _getActiveSegmentEntriesCount());

		Assert.assertFalse(_isActive("%deviceBrand%"));
		Assert.assertFalse(_isActive("%deviceModel%"));
		Assert.assertFalse(_isActive("%deviceScreenResolutionHeight%"));
		Assert.assertFalse(_isActive("%deviceScreenResolutionWidth%"));
	}

	private void _addSegmentsEntry(String criteria, Locale locale)
		throws Exception {

		_segmentsEntryLocalService.addSegmentsEntry(
			null, RandomTestUtil.randomString(),
			Collections.singletonMap(locale, RandomTestUtil.randomString()),
			Collections.singletonMap(locale, RandomTestUtil.randomString()),
			true, criteria, SegmentsEntryConstants.SOURCE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private int _getActiveSegmentEntriesCount() {
		return _segmentsEntryLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				SegmentsEntryTable.INSTANCE
			).where(
				SegmentsEntryTable.INSTANCE.active.eq(true)
			));
	}

	private boolean _isActive(String criteria) {
		Expression<String> criteriaExpression =
			DSLFunctionFactoryUtil.castClobText(
				SegmentsEntryTable.INSTANCE.criteria);

		List<Boolean> results = _segmentsEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				SegmentsEntryTable.INSTANCE.active
			).from(
				SegmentsEntryTable.INSTANCE
			).where(
				criteriaExpression.like(criteria)
			));

		return results.get(0);
	}

	private static final String _CLASS_NAME =
		"com.liferay.segments.internal.upgrade.v3_1_1." +
			"SegmentsEntryUpgradeProcess";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.segments.internal.upgrade.registry.SegmentsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}