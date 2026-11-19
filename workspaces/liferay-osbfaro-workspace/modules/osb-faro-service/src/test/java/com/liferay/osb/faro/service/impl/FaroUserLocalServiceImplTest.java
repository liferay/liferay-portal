/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.persistence.FaroUserPersistence;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class FaroUserLocalServiceImplTest {

	@Test
	public void testDeleteFaroUsersByLiveUserId() throws Exception {
		long faroUserId1 = RandomTestUtil.randomLong();

		FaroUser faroUser1 = _mockFaroUser(faroUserId1);

		long faroUserId2 = RandomTestUtil.randomLong();

		FaroUser faroUser2 = _mockFaroUser(faroUserId2);

		long liveUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_faroUserPersistence.findByLiveUserId(liveUserId)
		).thenReturn(
			Arrays.asList(faroUser1, faroUser2)
		);

		FaroUserLocalServiceImpl faroUserLocalServiceImpl = Mockito.spy(
			new FaroUserLocalServiceImpl());

		ReflectionTestUtil.setFieldValue(
			faroUserLocalServiceImpl, "faroUserPersistence",
			_faroUserPersistence);

		Mockito.doThrow(
			new PortalException()
		).when(
			faroUserLocalServiceImpl
		).deleteFaroUser(
			faroUserId1
		);

		Mockito.doReturn(
			faroUser2
		).when(
			faroUserLocalServiceImpl
		).deleteFaroUser(
			faroUserId2
		);

		faroUserLocalServiceImpl.deleteFaroUsersByLiveUserId(liveUserId);

		Mockito.verify(
			faroUserLocalServiceImpl
		).deleteFaroUser(
			faroUserId2
		);
	}

	private FaroUser _mockFaroUser(long faroUserId) {
		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getFaroUserId()
		).thenReturn(
			faroUserId
		);

		return faroUser;
	}

	private final FaroUserPersistence _faroUserPersistence = Mockito.mock(
		FaroUserPersistence.class);

}