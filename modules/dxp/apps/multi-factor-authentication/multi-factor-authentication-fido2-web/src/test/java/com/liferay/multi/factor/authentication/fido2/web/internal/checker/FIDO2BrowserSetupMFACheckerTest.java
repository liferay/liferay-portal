/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.checker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalService;
import com.liferay.multi.factor.authentication.fido2.web.internal.BaseMFAFIDO2WebTestCase;
import com.liferay.multi.factor.authentication.fido2.web.internal.constants.MFAFIDO2EventTypes;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Regisson Aguiar
 */
public class FIDO2BrowserSetupMFACheckerTest extends BaseMFAFIDO2WebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PrincipalThreadLocal.setName(REAL_USER_ID);

		ReflectionTestUtil.setFieldValue(
			_fido2BrowserSetupMFAChecker,
			"_mfaFIDO2CredentialEntryLocalService",
			_mfaFIDO2CredentialEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_fido2BrowserSetupMFAChecker, "_objectMapper", _objectMapper);
		ReflectionTestUtil.setFieldValue(
			_fido2BrowserSetupMFAChecker, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_fido2BrowserSetupMFAChecker, "_relyingParty", _relyingParty);
	}

	@Test
	public void testRemoveExistingSetup() throws Exception {
		long selectedUserId = RandomTestUtil.randomLong();

		_fido2BrowserSetupMFAChecker.removeExistingSetup(selectedUserId);

		Mockito.verify(
			auditRouter, Mockito.never()
		).route(
			Mockito.any()
		);

		Mockito.when(
			_mfaFIDO2CredentialEntryLocalService.
				getMFAFIDO2CredentialEntriesByUserId(selectedUserId)
		).thenReturn(
			Arrays.asList(
				Mockito.mock(MFAFIDO2CredentialEntry.class),
				Mockito.mock(MFAFIDO2CredentialEntry.class))
		);

		_fido2BrowserSetupMFAChecker.removeExistingSetup(selectedUserId);

		ArgumentCaptor<AuditMessage> argumentCaptor = ArgumentCaptor.forClass(
			AuditMessage.class);

		Mockito.verify(
			auditRouter, Mockito.times(2)
		).route(
			argumentCaptor.capture()
		);

		List<AuditMessage> auditMessages = argumentCaptor.getAllValues();

		assertAuditMessage(
			auditMessages.get(0), null,
			MFAFIDO2EventTypes.MFA_FIDO2_SETUP_REMOVED, selectedUserId);
		assertAuditMessage(
			auditMessages.get(1), null,
			MFAFIDO2EventTypes.MFA_FIDO2_SETUP_REMOVED, selectedUserId);
	}

	@Test
	public void testSetUp() throws Exception {
		Mockito.when(
			portal.getOriginalServletRequest(Mockito.any())
		).thenAnswer(
			invocation -> invocation.getArgument(0)
		);

		Mockito.when(
			_objectMapper.readValue(
				Mockito.anyString(),
				Mockito.eq(PublicKeyCredentialCreationOptions.class))
		).thenReturn(
			Mockito.mock(PublicKeyCredentialCreationOptions.class)
		);

		Mockito.when(
			_objectMapper.readValue(
				Mockito.anyString(), Mockito.any(TypeReference.class))
		).thenReturn(
			Mockito.mock(PublicKeyCredential.class)
		);

		_testSetUp();
		_testSetUpWithFailedRegistration();
	}

	private void _testSetUp() throws Exception {
		RegistrationResult registrationResult = Mockito.mock(
			RegistrationResult.class);

		PublicKeyCredentialDescriptor publicKeyCredentialDescriptor =
			Mockito.mock(PublicKeyCredentialDescriptor.class);

		Mockito.when(
			publicKeyCredentialDescriptor.getId()
		).thenReturn(
			new ByteArray(RandomTestUtil.randomBytes())
		);

		Mockito.when(
			registrationResult.getKeyId()
		).thenReturn(
			publicKeyCredentialDescriptor
		);

		Mockito.when(
			registrationResult.getPublicKeyCose()
		).thenReturn(
			new ByteArray(RandomTestUtil.randomBytes())
		);

		Mockito.doReturn(
			registrationResult
		).when(
			_relyingParty
		).finishRegistration(
			Mockito.any()
		);

		long selectedUserId = RandomTestUtil.randomLong();

		Assert.assertTrue(
			_fido2BrowserSetupMFAChecker.setUp(
				new MockHttpServletRequest(), selectedUserId));

		ArgumentCaptor<AuditMessage> argumentCaptor = ArgumentCaptor.forClass(
			AuditMessage.class);

		Mockito.verify(
			auditRouter
		).route(
			argumentCaptor.capture()
		);

		assertAuditMessage(
			argumentCaptor.getValue(), null,
			MFAFIDO2EventTypes.MFA_FIDO2_SETUP_ADDED, selectedUserId);
	}

	private void _testSetUpWithFailedRegistration() throws Exception {
		Mockito.doThrow(
			new IllegalArgumentException()
		).when(
			_relyingParty
		).finishRegistration(
			Mockito.any()
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				FIDO2BrowserSetupMFAChecker.class.getName(),
				LoggerTestUtil.ERROR)) {

			Assert.assertFalse(
				_fido2BrowserSetupMFAChecker.setUp(
					new MockHttpServletRequest(), RandomTestUtil.randomLong()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}

		Mockito.verifyNoMoreInteractions(auditRouter);
	}

	private final FIDO2BrowserSetupMFAChecker _fido2BrowserSetupMFAChecker =
		new FIDO2BrowserSetupMFAChecker();
	private final MFAFIDO2CredentialEntryLocalService
		_mfaFIDO2CredentialEntryLocalService = Mockito.mock(
			MFAFIDO2CredentialEntryLocalService.class);
	private final ObjectMapper _objectMapper = Mockito.mock(ObjectMapper.class);
	private final RelyingParty _relyingParty = Mockito.mock(RelyingParty.class);

}