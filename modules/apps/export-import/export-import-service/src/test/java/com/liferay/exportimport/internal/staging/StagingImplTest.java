/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging;

import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Vendel Töreki
 */
public class StagingImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidateRemoteGroupIsSameWhenRemoteHostIsNotAllowed() {
		RemoteOptionsException remoteOptionsException =
			new RemoteOptionsException(RemoteOptionsException.REMOTE_ADDRESS);

		Mockito.when(
			_stagingURLHelper.buildRemoteURL(
				Mockito.anyString(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean())
		).thenThrow(
			new SystemException(remoteOptionsException)
		);

		ReflectionTestUtil.setFieldValue(
			_stagingImpl, "_stagingURLHelper", _stagingURLHelper);

		Assert.assertSame(
			remoteOptionsException,
			Assert.assertThrows(
				RemoteOptionsException.class,
				() -> _stagingImpl.validateRemoteGroupIsSame(
					RandomTestUtil.randomLong(),
					RandomTestUtil.randomInt(1, Integer.MAX_VALUE),
					RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
					null, false)));
	}

	private final StagingImpl _stagingImpl = new StagingImpl();
	private final StagingURLHelper _stagingURLHelper = Mockito.mock(
		StagingURLHelper.class);

}