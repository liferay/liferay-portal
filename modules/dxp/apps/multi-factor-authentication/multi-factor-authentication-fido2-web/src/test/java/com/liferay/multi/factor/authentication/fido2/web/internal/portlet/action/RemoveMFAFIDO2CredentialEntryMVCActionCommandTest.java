/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalService;
import com.liferay.multi.factor.authentication.fido2.web.internal.BaseMFAFIDO2WebTestCase;
import com.liferay.multi.factor.authentication.fido2.web.internal.constants.MFAFIDO2EventTypes;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Regisson Aguiar
 */
public class RemoveMFAFIDO2CredentialEntryMVCActionCommandTest
	extends BaseMFAFIDO2WebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PrincipalThreadLocal.setName(_DO_AS_USER_ID);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getUserId()
		).thenReturn(
			_DO_AS_USER_ID
		);

		Mockito.when(
			_actionRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			_actionRequest.getParameter("mfaFIDO2CredentialEntryId")
		).thenReturn(
			String.valueOf(_MFA_FIDO2_CREDENTIAL_ENTRY_ID)
		);

		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry = Mockito.mock(
			MFAFIDO2CredentialEntry.class);

		Mockito.when(
			mfaFIDO2CredentialEntry.getUserId()
		).thenReturn(
			_DO_AS_USER_ID
		);

		Mockito.when(
			mfaFIDO2CredentialEntry.getMfaFIDO2CredentialEntryId()
		).thenReturn(
			_MFA_FIDO2_CREDENTIAL_ENTRY_ID
		);

		Mockito.when(
			_mfaFIDO2CredentialEntryLocalService.getMFAFIDO2CredentialEntry(
				_MFA_FIDO2_CREDENTIAL_ENTRY_ID)
		).thenReturn(
			mfaFIDO2CredentialEntry
		);

		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand,
			"_mfaFIDO2AuditMessageBuilder", mfaFIDO2AuditMessageBuilder);
		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand,
			"_mfaFIDO2CredentialEntryLocalService",
			_mfaFIDO2CredentialEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand, "_portal", portal);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		_testDoProcessAction();
		_testDoProcessActionWithFailedDeletion();
	}

	private void _testDoProcessAction() throws Exception {
		_removeMFAFIDO2CredentialEntryMVCActionCommand.doProcessAction(
			_actionRequest, Mockito.mock(ActionResponse.class));

		ArgumentCaptor<AuditMessage> argumentCaptor = ArgumentCaptor.forClass(
			AuditMessage.class);

		Mockito.verify(
			auditRouter
		).route(
			argumentCaptor.capture()
		);

		assertAuditMessage(
			argumentCaptor.getValue(), String.valueOf(_DO_AS_USER_ID),
			MFAFIDO2EventTypes.MFA_FIDO2_SETUP_REMOVED, _DO_AS_USER_ID);
	}

	private void _testDoProcessActionWithFailedDeletion() throws Exception {
		Mockito.when(
			_mfaFIDO2CredentialEntryLocalService.deleteMFAFIDO2CredentialEntry(
				_MFA_FIDO2_CREDENTIAL_ENTRY_ID)
		).thenThrow(
			new PortalException()
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				RemoveMFAFIDO2CredentialEntryMVCActionCommand.class.getName(),
				LoggerTestUtil.WARN)) {

			_removeMFAFIDO2CredentialEntryMVCActionCommand.doProcessAction(
				_actionRequest, Mockito.mock(ActionResponse.class));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}

		Mockito.verifyNoMoreInteractions(auditRouter);
	}

	private static final long _DO_AS_USER_ID = RandomTestUtil.randomLong();

	private static final long _MFA_FIDO2_CREDENTIAL_ENTRY_ID =
		RandomTestUtil.randomLong();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private final MFAFIDO2CredentialEntryLocalService
		_mfaFIDO2CredentialEntryLocalService = Mockito.mock(
			MFAFIDO2CredentialEntryLocalService.class);
	private final RemoveMFAFIDO2CredentialEntryMVCActionCommand
		_removeMFAFIDO2CredentialEntryMVCActionCommand =
			new RemoveMFAFIDO2CredentialEntryMVCActionCommand();

}