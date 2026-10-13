/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Caio Pinheiro
 */
public class AccountLifecycleFaroControllerTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_accountLifecycleFaroController, "contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_accountLifecycleFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetAccountLifecyclesFaroFDSResultsDisplay()
		throws Exception {

		long channelId = RandomTestUtil.randomLong();
		Boolean defaultAccountLifecycle = RandomTestUtil.randomBoolean();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();
		String sortString = RandomTestUtil.randomString();
		int total = RandomTestUtil.randomInt();

		Mockito.when(
			_contactsEngineClient.getAccountLifecycles(
				_faroProject, channelId, defaultAccountLifecycle, search, page,
				pageSize, sortString)
		).thenReturn(
			new Results<>(Collections.emptyList(), total)
		);

		long groupId = RandomTestUtil.randomLong();

		FaroFDSResultsDisplay<AccountLifecycle> faroFDSResultsDisplay =
			_accountLifecycleFaroController.
				getAccountLifecyclesFaroFDSResultsDisplay(
					groupId, channelId, defaultAccountLifecycle, page, pageSize,
					search, sortString);

		Assert.assertEquals(total, faroFDSResultsDisplay.getTotalCount());

		Mockito.verify(
			_faroProjectLocalService
		).getFaroProjectByGroupId(
			groupId
		);
	}

	@Test
	public void testPatchAccountLifecycle() throws Exception {
		AccountLifecycle accountLifecycle = new AccountLifecycle();
		Boolean defaultAccountLifecycle = RandomTestUtil.randomBoolean();
		String id = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.patchAccountLifecycle(
				_faroProject, id, defaultAccountLifecycle)
		).thenReturn(
			accountLifecycle
		);

		Assert.assertSame(
			accountLifecycle,
			_accountLifecycleFaroController.patchAccountLifecycle(
				RandomTestUtil.randomLong(), id, defaultAccountLifecycle));
	}

	private final AccountLifecycleFaroController
		_accountLifecycleFaroController = new AccountLifecycleFaroController();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}