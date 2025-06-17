/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.DataLimitExceededException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.DataLimitEntry;
import com.liferay.portal.tools.service.builder.test.service.DataLimitEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.DataLimitEntryPersistence;
import com.liferay.portal.util.PropsUtil;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DataLimitTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		_setDataLimitModelMaxCount(3);

		try {
			_test();

			// Asserting limit is per company

			_company = CompanyTestUtil.addCompany(false);

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						_company.getCompanyId())) {

				_test();
			}
		}
		finally {
			_setDataLimitModelMaxCount(0);
		}
	}

	private void _setDataLimitModelMaxCount(long dataLimit) {
		ReflectionTestUtil.setFieldValue(
			_dataLimitEntryPersistence, "_dataLimitModelMaxCount", 0);

		PropsUtil.set(
			"data.limit.model.max.count[" + DataLimitEntry.class.getName() +
				"]",
			String.valueOf(dataLimit));

		ReflectionTestUtil.invoke(
			_dataLimitEntryPersistence, "setModelClass",
			new Class<?>[] {Class.class}, DataLimitEntry.class);
	}

	private void _test() {

		// Within data limit

		DataLimitEntry dataLimitEntry1 = _dataLimitEntryPersistence.create(
			_counterLocalService.increment());

		_dataLimitEntryLocalService.updateDataLimitEntry(dataLimitEntry1);

		DataLimitEntry dataLimitEntry2 = _dataLimitEntryPersistence.create(
			_counterLocalService.increment());

		_dataLimitEntryLocalService.updateDataLimitEntry(dataLimitEntry2);

		DataLimitEntry dataLimitEntry3 = _dataLimitEntryPersistence.create(
			_counterLocalService.increment());

		dataLimitEntry3 = _dataLimitEntryLocalService.updateDataLimitEntry(
			dataLimitEntry3);

		// Exceeding data limit

		try {
			_dataLimitEntryLocalService.updateDataLimitEntry(
				_dataLimitEntryPersistence.create(
					_counterLocalService.increment()));

			Assert.fail();
		}
		catch (DataLimitExceededException dataLimitExceededException) {
			Assert.assertEquals(
				"Unable to exceed maximum number of allowed " +
					DataLimitEntry.class.getName(),
				dataLimitExceededException.getMessage());
		}

		// Modification is always allowed

		dataLimitEntry3.setModifiedDate(new Date());

		_dataLimitEntryLocalService.updateDataLimitEntry(dataLimitEntry3);
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DataLimitEntryLocalService _dataLimitEntryLocalService;

	@Inject
	private DataLimitEntryPersistence _dataLimitEntryPersistence;

}