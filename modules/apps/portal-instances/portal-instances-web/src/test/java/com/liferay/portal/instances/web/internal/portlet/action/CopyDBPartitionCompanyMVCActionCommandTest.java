/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class CopyDBPartitionCompanyMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_company.getWebId()
		).thenReturn(
			_COMPANY_WEB_ID
		);

		Mockito.when(
			_language.format(
				Mockito.nullable(Locale.class), Mockito.anyString(),
				Mockito.<Object>any())
		).thenAnswer(
			invocationOnMock ->
				invocationOnMock.getArgument(1) + ":" +
					invocationOnMock.getArgument(2)
		);

		Mockito.when(
			_language.get(Mockito.nullable(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		_jsonPortletResponseUtilMockedStatic = Mockito.mockStatic(
			JSONPortletResponseUtil.class);

		_jsonPortletResponseUtilMockedStatic.when(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class),
				Mockito.any(JSONObject.class))
		).then(
			invocationOnMock -> {
				_jsonObject = invocationOnMock.getArgument(2);

				return null;
			}
		);

		_sessionMessagesMockedStatic = Mockito.mockStatic(
			SessionMessages.class);

		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_companyService",
			_companyService);
		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_copyDBPartitionCompanyMVCActionCommand, "_portal", _portal);
	}

	@After
	public void tearDown() {
		_jsonPortletResponseUtilMockedStatic.close();
		_sessionMessagesMockedStatic.close();
	}

	@Test
	public void testClearedHiddenDefaultSuccessMessageOnSuccess()
		throws Exception {

		_sessionMessagesMockedStatic.when(
			() -> SessionMessages.contains(
				Mockito.any(PortletRequest.class), Mockito.anyString())
		).thenReturn(
			true
		);

		_assertCopyDBPartitionCompany(
			_DESTINATION_COMPANY_ID, _getMockActionRequest());

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.clear(Mockito.any(PortletRequest.class)));
	}

	@Test
	public void testDestinationCompanyIdOnSuccess() throws Exception {
		_assertCopyDBPartitionCompany(
			_DESTINATION_COMPANY_ID, _getMockActionRequest());
	}

	@Test
	public void testErrorForBlankField() throws Exception {
		_assertError(
			"please-enter-a-valid-name",
			_getMockActionRequest("name", StringPool.SPACE));
		_assertError(
			"please-enter-a-valid-virtual-host",
			_getMockActionRequest("virtualHostname", StringPool.SPACE));
		_assertError(
			"please-enter-a-valid-web-id",
			_getMockActionRequest("webId", StringPool.SPACE));

		Mockito.verifyNoInteractions(_companyService);
	}

	@Test
	public void testErrorForCompanyException() throws Exception {
		_setUpFailedCopyDBPartitionCompany(new CompanyNameException());

		_assertError("please-enter-a-valid-name", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(new CompanyVirtualHostException());

		_assertError(
			"please-enter-a-valid-virtual-host", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(new CompanyWebIdException());

		_assertError("please-enter-a-valid-web-id", _getMockActionRequest());
	}

	@Test
	public void testErrorForIllegalArgumentException() throws Exception {
		_setUpFailedCopyDBPartitionCompany(
			new IllegalArgumentException(
				"Company ID " + _SOURCE_COMPANY_ID +
					" is the default company ID"));

		_assertError(
			"the-default-instance-cannot-be-copied", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(
			new IllegalArgumentException(
				"Company ID " + _DESTINATION_COMPANY_ID + " already exists"));

		_assertError(
			"please-enter-a-valid-destination-company-id",
			_getMockActionRequest());
	}

	@Test
	public void testErrorForInvalidDestinationCompanyId() throws Exception {
		String suffix = RandomTestUtil.randomString(
			NumericStringRandomizerBumper.INSTANCE);

		_assertError(
			"please-enter-a-valid-destination-company-id",
			_getMockActionRequest(
				"destinationCompanyId", _DESTINATION_COMPANY_ID + suffix));

		_assertError(
			"please-enter-a-valid-destination-company-id",
			_getMockActionRequest(
				"destinationCompanyId",
				StringPool.DASH + RandomTestUtil.randomLong()));
		_assertError(
			"please-enter-a-valid-destination-company-id",
			_getMockActionRequest(
				"destinationCompanyId", "9" + Long.MAX_VALUE));

		Mockito.verifyNoInteractions(_companyService);
	}

	@Test
	public void testErrorForUnmappedException() throws Exception {
		_setUpFailedCopyDBPartitionCompany(new RuntimeException());

		_assertError("an-unexpected-error-occurred", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(
			new PrincipalException.MustBeOmniadmin(_permissionChecker));

		_assertError("an-unexpected-error-occurred", _getMockActionRequest());
	}

	@Test
	public void testErrorForUnsupportedOperationException() throws Exception {
		_setUpFailedCopyDBPartitionCompany(
			new UnsupportedOperationException(
				"Database partitioning must be enabled"));

		_assertError(
			"database-partitioning-must-be-enabled", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(
			new UnsupportedOperationException(
				"Company in copy process company ID is not null"));

		_assertError(
			"copying-an-instance-is-already-in-progress",
			_getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(new UnsupportedOperationException());

		_assertError("an-unexpected-error-occurred", _getMockActionRequest());
	}

	@Test
	public void testErrorForWrappedCompanyException() throws Exception {
		_setUpFailedCopyDBPartitionCompany(
			new PortalException(new CompanyNameException()));

		_assertError("please-enter-a-valid-name", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(
			new PortalException(new CompanyVirtualHostException()));

		_assertError(
			"please-enter-a-valid-virtual-host", _getMockActionRequest());

		_setUpFailedCopyDBPartitionCompany(
			new PortalException(new CompanyWebIdException()));

		_assertError("please-enter-a-valid-web-id", _getMockActionRequest());
	}

	@Test
	public void testNullDestinationCompanyIdOnSuccess() throws Exception {
		_assertCopyDBPartitionCompany(
			null,
			_getMockActionRequest("destinationCompanyId", StringPool.BLANK));
	}

	@Test
	public void testPaddedDestinationCompanyIdOnSuccess() throws Exception {
		_assertCopyDBPartitionCompany(
			_DESTINATION_COMPANY_ID,
			_getMockActionRequest(
				"destinationCompanyId",
				StringPool.SPACE + _DESTINATION_COMPANY_ID + StringPool.SPACE));
	}

	@Test
	public void testUnclearedHiddenDefaultSuccessMessageOnSuccess()
		throws Exception {

		_sessionMessagesMockedStatic.when(
			() -> SessionMessages.contains(
				Mockito.any(PortletRequest.class), Mockito.anyString())
		).thenReturn(
			false
		);

		_assertCopyDBPartitionCompany(
			_DESTINATION_COMPANY_ID, _getMockActionRequest());

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.clear(Mockito.any(PortletRequest.class)),
			Mockito.never());
	}

	private void _assertCopyDBPartitionCompany(
			Long destinationCompanyId, MockActionRequest mockActionRequest)
		throws Exception {

		Mockito.when(
			_companyService.copyDBPartitionCompany(
				_SOURCE_COMPANY_ID, destinationCompanyId, _NAME,
				_VIRTUAL_HOSTNAME, _WEB_ID)
		).thenReturn(
			_company
		);

		_copyDBPartitionCompanyMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertEquals(_COMPANY_ID, _jsonObject.getLong("companyId"));

		Assert.assertEquals(0, _hideDefaultSuccessMessageCount);

		Assert.assertFalse(_jsonObject.has("error"));

		Mockito.verify(
			_companyService
		).copyDBPartitionCompany(
			_SOURCE_COMPANY_ID, destinationCompanyId, _NAME, _VIRTUAL_HOSTNAME,
			_WEB_ID
		);

		_jsonPortletResponseUtilMockedStatic.verify(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class), Mockito.eq(_jsonObject)));

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.add(
				mockActionRequest, "requestProcessed",
				"the-instance-was-copied-to-x:" + _COMPANY_WEB_ID));
	}

	private void _assertError(
			String expectedError, MockActionRequest mockActionRequest)
		throws Exception {

		_hideDefaultSuccessMessageCount = 0;

		_copyDBPartitionCompanyMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertEquals(expectedError, _jsonObject.getString("error"));
		Assert.assertEquals(1, _hideDefaultSuccessMessageCount);
		Assert.assertFalse(_jsonObject.has("companyId"));

		_jsonPortletResponseUtilMockedStatic.verify(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class), Mockito.eq(_jsonObject)));
	}

	private MockActionRequest _getMockActionRequest() {
		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.addParameter(
			"destinationCompanyId", String.valueOf(_DESTINATION_COMPANY_ID));
		mockActionRequest.addParameter("name", _NAME);
		mockActionRequest.addParameter(
			"sourceCompanyId", String.valueOf(_SOURCE_COMPANY_ID));
		mockActionRequest.addParameter("virtualHostname", _VIRTUAL_HOSTNAME);
		mockActionRequest.addParameter("webId", _WEB_ID);
		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		return mockActionRequest;
	}

	private MockActionRequest _getMockActionRequest(String key, String value) {
		MockActionRequest mockActionRequest = _getMockActionRequest();

		mockActionRequest.setParameter(key, value);

		return mockActionRequest;
	}

	private void _setUpFailedCopyDBPartitionCompany(Exception exception)
		throws Exception {

		Mockito.doThrow(
			exception
		).when(
			_companyService
		).copyDBPartitionCompany(
			Mockito.anyLong(), Mockito.nullable(Long.class),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyString()
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _COMPANY_WEB_ID = RandomTestUtil.randomString();

	private static final long _DESTINATION_COMPANY_ID =
		RandomTestUtil.randomLong();

	private static final String _NAME = RandomTestUtil.randomString();

	private static final long _SOURCE_COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _VIRTUAL_HOSTNAME =
		RandomTestUtil.randomString();

	private static final String _WEB_ID = RandomTestUtil.randomString();

	private final Company _company = Mockito.mock(Company.class);
	private final CompanyService _companyService = Mockito.mock(
		CompanyService.class);

	private final CopyDBPartitionCompanyMVCActionCommand
		_copyDBPartitionCompanyMVCActionCommand =
			new CopyDBPartitionCompanyMVCActionCommand() {

				@Override
				protected void hideDefaultSuccessMessage(
					PortletRequest portletRequest) {

					_hideDefaultSuccessMessageCount++;
				}

			};

	private int _hideDefaultSuccessMessageCount;
	private JSONObject _jsonObject;
	private MockedStatic<JSONPortletResponseUtil>
		_jsonPortletResponseUtilMockedStatic;
	private final Language _language = Mockito.mock(Language.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private MockedStatic<SessionMessages> _sessionMessagesMockedStatic;

}