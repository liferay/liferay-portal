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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
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

		_setUpMFAFIDO2CredentialEntry(
			_MFA_FIDO2_CREDENTIAL_ENTRY_ID, _DO_AS_USER_ID);

		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand, "_auditRouter",
			auditRouter);
		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand,
			"_mfaFIDO2CredentialEntryLocalService",
			_mfaFIDO2CredentialEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_removeMFAFIDO2CredentialEntryMVCActionCommand,
			"_userModelResourcePermission", _userModelResourcePermission);
	}

	@Test
	public void testDoProcessAction() throws Exception {
		_testDoProcessAction();
		_testDoProcessActionWithFailedDeletion();
		_testDoProcessActionWithImpersonatePermission();
		_testDoProcessActionWithoutImpersonatePermission();
	}

	private void _setUpMFAFIDO2CredentialEntry(
			long mfaFIDO2CredentialEntryId, long userId)
		throws Exception {

		Mockito.when(
			_actionRequest.getParameter("mfaFIDO2CredentialEntryId")
		).thenReturn(
			String.valueOf(mfaFIDO2CredentialEntryId)
		);

		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry = Mockito.mock(
			MFAFIDO2CredentialEntry.class);

		Mockito.when(
			mfaFIDO2CredentialEntry.getUserId()
		).thenReturn(
			userId
		);

		Mockito.when(
			mfaFIDO2CredentialEntry.getMfaFIDO2CredentialEntryId()
		).thenReturn(
			mfaFIDO2CredentialEntryId
		);

		Mockito.when(
			_mfaFIDO2CredentialEntryLocalService.getMFAFIDO2CredentialEntry(
				mfaFIDO2CredentialEntryId)
		).thenReturn(
			mfaFIDO2CredentialEntry
		);
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

	private void _testDoProcessActionWithImpersonatePermission()
		throws Exception {

		Mockito.clearInvocations(auditRouter);

		long mfaFIDO2CredentialEntryId = RandomTestUtil.randomLong();
		long userId = RandomTestUtil.randomLong();

		_setUpMFAFIDO2CredentialEntry(mfaFIDO2CredentialEntryId, userId);

		Mockito.when(
			_userModelResourcePermission.contains(
				Mockito.any(), Mockito.eq(userId),
				Mockito.eq(ActionKeys.IMPERSONATE))
		).thenReturn(
			true
		);

		_removeMFAFIDO2CredentialEntryMVCActionCommand.doProcessAction(
			_actionRequest, Mockito.mock(ActionResponse.class));

		Mockito.verify(
			_mfaFIDO2CredentialEntryLocalService
		).deleteMFAFIDO2CredentialEntry(
			mfaFIDO2CredentialEntryId
		);

		ArgumentCaptor<AuditMessage> argumentCaptor = ArgumentCaptor.forClass(
			AuditMessage.class);

		Mockito.verify(
			auditRouter
		).route(
			argumentCaptor.capture()
		);

		assertAuditMessage(
			argumentCaptor.getValue(), String.valueOf(_DO_AS_USER_ID),
			MFAFIDO2EventTypes.MFA_FIDO2_SETUP_REMOVED, userId);
	}

	private void _testDoProcessActionWithoutImpersonatePermission()
		throws Exception {

		long mfaFIDO2CredentialEntryId = RandomTestUtil.randomLong();

		_setUpMFAFIDO2CredentialEntry(
			mfaFIDO2CredentialEntryId, RandomTestUtil.randomLong());

		Mockito.when(
			_userModelResourcePermission.contains(
				Mockito.any(), Mockito.anyLong(),
				Mockito.eq(ActionKeys.IMPERSONATE))
		).thenReturn(
			false
		);

		_removeMFAFIDO2CredentialEntryMVCActionCommand.doProcessAction(
			_actionRequest, Mockito.mock(ActionResponse.class));

		Mockito.verify(
			_mfaFIDO2CredentialEntryLocalService, Mockito.never()
		).deleteMFAFIDO2CredentialEntry(
			mfaFIDO2CredentialEntryId
		);

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
	private final ModelResourcePermission<User> _userModelResourcePermission =
		Mockito.mock(ModelResourcePermission.class);

}