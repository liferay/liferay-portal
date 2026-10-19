/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Eudaldo Alonso
 */
public class CheckFaroProjectsMessageListenerTest {

	@BeforeClass
	public static void setUpClass() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_mailService", _mailService);
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_portal", _portal);
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_roleLocalService",
			_roleLocalService);
		ReflectionTestUtils.setField(
			_checkFaroProjectsMessageListener, "_userLocalService",
			_userLocalService);

		Mockito.when(
			_roleLocalService.getRole(Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Mockito.mock(Role.class)
		);

		Mockito.when(
			_userLocalService.getRoleUsers(Mockito.anyLong())
		).thenReturn(
			Collections.emptyList()
		);
	}

	@After
	public void tearDown() {
		ReflectionTestUtils.setField(
			CheckFaroProjectsMessageListener.class, "_systemsDownStartTime",
			0L);
	}

	@Test
	public void testDoReceive() throws Exception {
		FaroProject faroProject1 = _mockFaroProject(
			"ac1", FaroProjectConstants.STATE_READY);
		FaroProject faroProject2 = _mockFaroProject(
			"ac1", FaroProjectConstants.STATE_READY);
		FaroProject faroProject3 = _mockFaroProject(
			"ac2", FaroProjectConstants.STATE_READY);
		FaroProject faroProject4 = _mockFaroProject(
			"ac3", FaroProjectConstants.STATE_UNAVAILABLE);

		_mockFaroProjects(
			faroProject1, faroProject2, faroProject3, faroProject4);

		_checkFaroProjectsMessageListener.doReceive(new Message());

		Mockito.verify(
			_contactsEngineClient
		).validateConnection(
			faroProject1
		);

		Mockito.verify(
			_contactsEngineClient
		).validateConnection(
			faroProject3
		);

		Mockito.verifyNoMoreInteractions(_contactsEngineClient);

		Mockito.verify(
			_mailService, Mockito.never()
		).sendEmail(
			Mockito.any(MailMessage.class)
		);
	}

	@Test
	public void testDoReceiveWithClusterDown() throws Exception {
		FaroProject faroProject1 = _mockFaroProject(
			"ac1", FaroProjectConstants.STATE_READY);

		String serverLocation = RandomTestUtil.randomString();

		FaroProject faroProject2 = _mockFaroProject(
			serverLocation, FaroProjectConstants.STATE_READY);

		_mockFaroProjects(faroProject1, faroProject2);

		String message = RandomTestUtil.randomString();

		Mockito.doThrow(
			new Exception(message)
		).when(
			_contactsEngineClient
		).validateConnection(
			faroProject2
		);

		_checkFaroProjectsMessageListener.doReceive(new Message());

		MailMessage mailMessage = _getMailMessage();

		Assert.assertEquals(
			serverLocation + "\n" + message + "\n\n", mailMessage.getBody());
		Assert.assertEquals(
			"SYSTEM ALERT: 1 clusters down", mailMessage.getSubject());
	}

	@Test
	public void testDoReceiveWithClustersUp() throws Exception {
		ReflectionTestUtils.setField(
			CheckFaroProjectsMessageListener.class, "_systemsDownStartTime",
			System.currentTimeMillis());

		_mockFaroProjects(
			_mockFaroProject("ac1", FaroProjectConstants.STATE_READY));

		_checkFaroProjectsMessageListener.doReceive(new Message());

		MailMessage mailMessage = _getMailMessage();

		Assert.assertEquals(
			"SYSTEM ALERT: All clusters up", mailMessage.getSubject());
	}

	private MailMessage _getMailMessage() {
		ArgumentCaptor<MailMessage> argumentCaptor = ArgumentCaptor.forClass(
			MailMessage.class);

		Mockito.verify(
			_mailService
		).sendEmail(
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private FaroProject _mockFaroProject(String serverLocation, String state) {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getServerLocation()
		).thenReturn(
			serverLocation
		);

		Mockito.when(
			faroProject.getState()
		).thenReturn(
			state
		);

		return faroProject;
	}

	private void _mockFaroProjects(FaroProject... faroProjects) {
		Mockito.when(
			_faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Arrays.asList(faroProjects)
		);
	}

	private final CheckFaroProjectsMessageListener
		_checkFaroProjectsMessageListener =
			new CheckFaroProjectsMessageListener();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final MailService _mailService = Mockito.mock(MailService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final RoleLocalService _roleLocalService = Mockito.mock(
		RoleLocalService.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}