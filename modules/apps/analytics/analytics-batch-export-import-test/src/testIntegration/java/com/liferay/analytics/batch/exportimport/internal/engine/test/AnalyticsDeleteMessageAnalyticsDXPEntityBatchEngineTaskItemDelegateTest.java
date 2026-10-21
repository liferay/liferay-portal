/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine.test;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.analytics.message.storage.model.AnalyticsDeleteMessage;
import com.liferay.analytics.message.storage.service.AnalyticsDeleteMessageLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class
	AnalyticsDeleteMessageAnalyticsDXPEntityBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_batchEngineTaskItemDelegate, "contextCompany",
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));

		_newAnalyticsDeleteMessage =
			_analyticsDeleteMessageLocalService.addAnalyticsDeleteMessage(
				TestPropsValues.getCompanyId(), new Date(),
				User.class.getName(), RandomTestUtil.randomLong(),
				TestPropsValues.getUserId());
		_oldAnalyticsDeleteMessage =
			_analyticsDeleteMessageLocalService.addAnalyticsDeleteMessage(
				TestPropsValues.getCompanyId(),
				new Date(System.currentTimeMillis() - (Time.DAY * 2)),
				User.class.getName(), RandomTestUtil.randomLong(),
				TestPropsValues.getUserId());
	}

	@Test
	public void testRead() throws Exception {
		_testReadWithResourceLastModifiedDate();
		_testReadWithoutResourceLastModifiedDate();
	}

	private void _testReadWithResourceLastModifiedDate() throws Exception {
		Page<DXPEntity> page = _batchEngineTaskItemDelegate.read(
			null, Pagination.of(1, 1000), null,
			HashMapBuilder.<String, Serializable>put(
				"resourceLastModifiedDate",
				new Date(System.currentTimeMillis() - Time.DAY)
			).build(),
			null);

		List<String> ids = TransformUtil.transform(
			page.getItems(), DXPEntity::getId);

		Assert.assertTrue(
			ids.contains(
				String.valueOf(
					_newAnalyticsDeleteMessage.getAnalyticsDeleteMessageId())));
		Assert.assertFalse(
			ids.contains(
				String.valueOf(
					_oldAnalyticsDeleteMessage.getAnalyticsDeleteMessageId())));
	}

	private void _testReadWithoutResourceLastModifiedDate() throws Exception {
		Page<DXPEntity> page = _batchEngineTaskItemDelegate.read(
			null, Pagination.of(1, 1000), null, Collections.emptyMap(), null);

		List<String> ids = TransformUtil.transform(
			page.getItems(), DXPEntity::getId);

		Assert.assertTrue(
			ids.contains(
				String.valueOf(
					_newAnalyticsDeleteMessage.getAnalyticsDeleteMessageId())));
		Assert.assertTrue(
			ids.contains(
				String.valueOf(
					_oldAnalyticsDeleteMessage.getAnalyticsDeleteMessageId())));
	}

	@Inject
	private AnalyticsDeleteMessageLocalService
		_analyticsDeleteMessageLocalService;

	@Inject(
		filter = "component.name=com.liferay.analytics.batch.exportimport.internal.engine.AnalyticsDeleteMessageAnalyticsDXPEntityBatchEngineTaskItemDelegate"
	)
	private BatchEngineTaskItemDelegate<DXPEntity> _batchEngineTaskItemDelegate;

	@DeleteAfterTestRun
	private AnalyticsDeleteMessage _newAnalyticsDeleteMessage;

	@DeleteAfterTestRun
	private AnalyticsDeleteMessage _oldAnalyticsDeleteMessage;

}