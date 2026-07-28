/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.service.DateEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DateEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		DateEntry dateEntry = _dateEntryLocalService.createDateEntry(
			RandomTestUtil.nextLong());

		Date date = new Date();

		dateEntry.setValue(date);

		dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

		_assertDate(date, dateEntry.getValue());

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			dateEntry.getDateEntryId());

		_assertDate(date, dateEntry.getValue());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			dateEntry.getDateEntryId());

		_assertDate(date, dateEntry.getValue());

		Date newDate = new Date(date.getTime() + 1000);

		dateEntry.setValue(newDate);

		dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

		_assertDate(newDate, dateEntry.getValue());

		dateEntry = _dateEntryLocalService.getDateEntry(
			dateEntry.getDateEntryId());

		_assertDate(newDate, dateEntry.getValue());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.getDateEntry(
			dateEntry.getDateEntryId());

		_assertDate(newDate, dateEntry.getValue());
	}

	private void _assertDate(Date date1, Date date2) {
		Assert.assertEquals(Date.class, date1.getClass());
		Assert.assertEquals(Date.class, date2.getClass());

		Assert.assertEquals(date1, date2);
	}

	@Inject
	private DateEntryLocalService _dateEntryLocalService;

	@Inject
	private DateEntryPersistence _dateEntryPersistence;

}