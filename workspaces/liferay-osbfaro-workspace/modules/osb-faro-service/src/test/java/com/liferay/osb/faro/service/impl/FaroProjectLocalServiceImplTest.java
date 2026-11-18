/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.persistence.FaroProjectPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class FaroProjectLocalServiceImplTest {

	@Test
	public void testFetchFaroProjectBySalesforceAccountId() {
		String salesforceAccountId = RandomTestUtil.randomString();

		Mockito.when(
			_faroProjectPersistence.fetchBySalesforceAccountId_First(
				salesforceAccountId, null)
		).thenReturn(
			_faroProject
		);

		FaroProjectLocalServiceImpl faroProjectLocalServiceImpl =
			new FaroProjectLocalServiceImpl();

		ReflectionTestUtil.setFieldValue(
			faroProjectLocalServiceImpl, "faroProjectPersistence",
			_faroProjectPersistence);

		Assert.assertSame(
			_faroProject,
			faroProjectLocalServiceImpl.fetchFaroProjectBySalesforceAccountId(
				salesforceAccountId));
	}

	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectPersistence _faroProjectPersistence = Mockito.mock(
		FaroProjectPersistence.class);

}