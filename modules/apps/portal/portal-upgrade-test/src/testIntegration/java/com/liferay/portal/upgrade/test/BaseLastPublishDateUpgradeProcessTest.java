/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.BaseLastPublishDateUpgradeProcess;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class BaseLastPublishDateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetLayoutSetLastPublishDate() throws Exception {
		Assert.assertNull(_getLayoutSetLastPublishDate());

		Date privateLayoutSetLastPublishDate = new Date(
			RandomTestUtil.nextLong());

		_updateLastPublishDate(true, privateLayoutSetLastPublishDate);

		Assert.assertEquals(
			privateLayoutSetLastPublishDate, _getLayoutSetLastPublishDate());

		Date publicLayoutSetLastPublishDate = new Date(
			RandomTestUtil.nextLong());

		_updateLastPublishDate(false, publicLayoutSetLastPublishDate);

		Assert.assertEquals(
			publicLayoutSetLastPublishDate, _getLayoutSetLastPublishDate());
	}

	private Date _getLayoutSetLastPublishDate() throws Exception {
		AtomicReference<Date> lastPublishDateAtomicReference =
			new AtomicReference<>();

		BaseLastPublishDateUpgradeProcess baseLastPublishDateUpgradeProcess =
			new BaseLastPublishDateUpgradeProcess() {

				@Override
				protected void doUpgrade() throws Exception {
					lastPublishDateAtomicReference.set(
						getLayoutSetLastPublishDate(_group.getGroupId()));
				}

			};

		baseLastPublishDateUpgradeProcess.upgrade();

		return lastPublishDateAtomicReference.get();
	}

	private void _updateLastPublishDate(
			boolean privateLayout, Date lastPublishDate)
		throws Exception {

		_layoutSetLocalService.updateSettings(
			_group.getGroupId(), privateLayout,
			UnicodePropertiesBuilder.put(
				"last-publish-date", lastPublishDate.getTime()
			).buildString());
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

}