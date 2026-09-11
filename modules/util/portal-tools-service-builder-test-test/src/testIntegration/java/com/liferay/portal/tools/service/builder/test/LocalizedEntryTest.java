/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.LocalizedEntry;
import com.liferay.portal.tools.service.builder.test.model.LocalizedEntryLocalization;
import com.liferay.portal.tools.service.builder.test.service.LocalizedEntryLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class LocalizedEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddLocalizedEntryLocalization() throws Exception {
		_localizedEntry = _localizedEntryLocalService.addLocalizedEntry(
			_localizedEntryLocalService.createLocalizedEntry(
				RandomTestUtil.nextLong()));

		LocalizedEntryLocalization localizedEntryLocalization =
			_localizedEntryLocalService.addLocalizedEntryLocalization(
				_localizedEntry, "en_US", "Title", "Content");

		Assert.assertEquals(
			localizedEntryLocalization,
			_localizedEntryLocalService.fetchLocalizedEntryLocalization(
				_localizedEntry.getLocalizedEntryId(), "en_US"));
		Assert.assertEquals("Title", localizedEntryLocalization.getTitle());
		Assert.assertEquals("Content", localizedEntryLocalization.getContent());
	}

	@DeleteAfterTestRun
	private LocalizedEntry _localizedEntry;

	@Inject
	private LocalizedEntryLocalService _localizedEntryLocalService;

}