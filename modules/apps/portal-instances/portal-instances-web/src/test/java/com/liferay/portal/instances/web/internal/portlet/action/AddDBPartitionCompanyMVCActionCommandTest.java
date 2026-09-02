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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
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
public class AddDBPartitionCompanyMVCActionCommandTest {

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

		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand, "_companyService",
			_companyService);
		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_addDBPartitionCompanyMVCActionCommand, "_portal", _portal);
	}

	@After
	public void tearDown() {
		_jsonPortletResponseUtilMockedStatic.close();
		_sessionMessagesMockedStatic.close();
	}

	@Test
	public void testDoProcessAction() throws Exception {
		Mockito.when(
			_companyService.addDBPartitionCompany(
				_SCHEMA_NAME, _NAME, _VIRTUAL_HOSTNAME, _WEB_ID)
		).thenReturn(
			_company
		);

		MockActionRequest mockActionRequest = _getMockActionRequest();

		_addDBPartitionCompanyMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertEquals(_COMPANY_ID, _jsonObject.getLong("companyId"));

		Assert.assertEquals(0, _hideDefaultSuccessMessageCount);

		Assert.assertFalse(_jsonObject.has("error"));

		Mockito.verify(
			_companyService
		).addDBPartitionCompany(
			_SCHEMA_NAME, _NAME, _VIRTUAL_HOSTNAME, _WEB_ID
		);

		_jsonPortletResponseUtilMockedStatic.verify(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class), Mockito.eq(_jsonObject)));

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.add(
				mockActionRequest, "requestProcessed",
				"the-instance-was-imported-to-x:" + _COMPANY_WEB_ID));
	}

	@Test
	public void testDoProcessActionWithBlankOptionalFields() throws Exception {
		Mockito.when(
			_companyService.addDBPartitionCompany(
				_SCHEMA_NAME, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK)
		).thenReturn(
			_company
		);

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.addParameter("schemaName", _SCHEMA_NAME);
		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		_addDBPartitionCompanyMVCActionCommand.doProcessAction(
			mockActionRequest, new MockActionResponse());

		Assert.assertEquals(_COMPANY_ID, _jsonObject.getLong("companyId"));

		Assert.assertFalse(_jsonObject.has("error"));

		Mockito.verify(
			_companyService
		).addDBPartitionCompany(
			_SCHEMA_NAME, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK
		);
	}

	@Test
	public void testDoProcessActionWithClearedHiddenDefaultSuccessMessage()
		throws Exception {

		Mockito.when(
			_companyService.addDBPartitionCompany(
				_SCHEMA_NAME, _NAME, _VIRTUAL_HOSTNAME, _WEB_ID)
		).thenReturn(
			_company
		);

		_sessionMessagesMockedStatic.when(
			() -> SessionMessages.contains(
				Mockito.any(PortletRequest.class), Mockito.anyString())
		).thenReturn(
			true
		);

		_addDBPartitionCompanyMVCActionCommand.doProcessAction(
			_getMockActionRequest(), new MockActionResponse());

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.clear(Mockito.any(PortletRequest.class)));
	}

	@Test
	public void testDoProcessActionWithCompanyException() throws Exception {
		_assertError(new CompanyNameException(), "please-enter-a-valid-name");
		_assertError(
			new CompanyVirtualHostException(),
			"please-enter-a-valid-virtual-host");
		_assertError(
			new CompanyWebIdException(), "please-enter-a-valid-web-id");
	}

	@Test
	public void testDoProcessActionWithErrorLogLevel() throws Exception {
		Log log = Mockito.mock(Log.class);

		Mockito.when(
			log.isDebugEnabled()
		).thenReturn(
			true
		);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					AddDBPartitionCompanyMVCActionCommand.class, "_log", log)) {

			_assertError(
				new IllegalArgumentException(
					"Database partition " + _SCHEMA_NAME + " already exists"),
				"an-instance-for-this-schema-already-exists");

			Mockito.verify(
				log, Mockito.never()
			).error(
				Mockito.anyString(), Mockito.any(Exception.class)
			);

			Mockito.verify(
				log
			).debug(
				Mockito.anyString(), Mockito.any(Exception.class)
			);

			_assertError(
				new IllegalArgumentException(_SCHEMA_NAME),
				"an-unexpected-error-occurred");

			Mockito.verify(
				log
			).error(
				Mockito.anyString(), Mockito.any(Exception.class)
			);
		}
	}

	@Test
	public void testDoProcessActionWithIllegalArgumentException()
		throws Exception {

		_assertError(
			new IllegalArgumentException(
				"Invalid schema name \"" + _SCHEMA_NAME + "\""),
			"please-enter-a-valid-schema-name");

		_assertError(
			new IllegalArgumentException(
				"Database partition " + _SCHEMA_NAME + " already exists"),
			"an-instance-for-this-schema-already-exists");

		_assertError(
			new IllegalArgumentException(
				"Unable to insert the database partition " + _SCHEMA_NAME +
					" because it does not exist"),
			"the-exported-schema-does-not-exist");

		_assertError(
			new IllegalArgumentException(
				"Company ID " + _COMPANY_ID + " is the default company ID"),
			"please-enter-a-valid-schema-name");

		_assertError(
			new IllegalArgumentException(_SCHEMA_NAME),
			"an-unexpected-error-occurred");
	}

	@Test
	public void testDoProcessActionWithUnclearedHiddenDefaultSuccessMessage()
		throws Exception {

		Mockito.when(
			_companyService.addDBPartitionCompany(
				_SCHEMA_NAME, _NAME, _VIRTUAL_HOSTNAME, _WEB_ID)
		).thenReturn(
			_company
		);

		_sessionMessagesMockedStatic.when(
			() -> SessionMessages.contains(
				Mockito.any(PortletRequest.class), Mockito.anyString())
		).thenReturn(
			false
		);

		_addDBPartitionCompanyMVCActionCommand.doProcessAction(
			_getMockActionRequest(), new MockActionResponse());

		_sessionMessagesMockedStatic.verify(
			() -> SessionMessages.clear(Mockito.any(PortletRequest.class)),
			Mockito.never());
	}

	@Test
	public void testDoProcessActionWithUnmappedException() throws Exception {
		_assertError(new RuntimeException(), "an-unexpected-error-occurred");
		_assertError(
			new PrincipalException.MustBeOmniadmin(_permissionChecker),
			"an-unexpected-error-occurred");
	}

	@Test
	public void testDoProcessActionWithUnsupportedOperationException()
		throws Exception {

		_assertError(
			new UnsupportedOperationException(
				"Database partitioning must be enabled"),
			"database-partitioning-must-be-enabled");

		_assertError(
			new UnsupportedOperationException(
				"Company in import process company ID is not null"),
			"importing-an-instance-is-already-in-progress");

		_assertError(
			new UnsupportedOperationException("Unsupported SQL: select 1"),
			"an-unexpected-error-occurred");
	}

	@Test
	public void testDoProcessActionWithWrappedCompanyException()
		throws Exception {

		_assertError(
			new PortalException(new CompanyNameException()),
			"please-enter-a-valid-name");
		_assertError(
			new PortalException(new CompanyVirtualHostException()),
			"please-enter-a-valid-virtual-host");
		_assertError(
			new PortalException(new CompanyWebIdException()),
			"please-enter-a-valid-web-id");
	}

	private void _assertError(Exception exception, String expectedError)
		throws Exception {

		_hideDefaultSuccessMessageCount = 0;

		Mockito.doThrow(
			exception
		).when(
			_companyService
		).addDBPartitionCompany(
			_SCHEMA_NAME, _NAME, _VIRTUAL_HOSTNAME, _WEB_ID
		);

		_addDBPartitionCompanyMVCActionCommand.doProcessAction(
			_getMockActionRequest(), new MockActionResponse());

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

		mockActionRequest.addParameter("name", _NAME);
		mockActionRequest.addParameter("schemaName", _SCHEMA_NAME);
		mockActionRequest.addParameter("virtualHostname", _VIRTUAL_HOSTNAME);
		mockActionRequest.addParameter("webId", _WEB_ID);
		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		return mockActionRequest;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _COMPANY_WEB_ID = RandomTestUtil.randomString();

	private static final String _NAME = RandomTestUtil.randomString();

	private static final String _SCHEMA_NAME = RandomTestUtil.randomString();

	private static final String _VIRTUAL_HOSTNAME =
		RandomTestUtil.randomString();

	private static final String _WEB_ID = RandomTestUtil.randomString();

	private final AddDBPartitionCompanyMVCActionCommand
		_addDBPartitionCompanyMVCActionCommand =
			new AddDBPartitionCompanyMVCActionCommand() {

				@Override
				protected void hideDefaultSuccessMessage(
					PortletRequest portletRequest) {

					_hideDefaultSuccessMessageCount++;
				}

			};

	private final Company _company = Mockito.mock(Company.class);
	private final CompanyService _companyService = Mockito.mock(
		CompanyService.class);
	private int _hideDefaultSuccessMessageCount;
	private JSONObject _jsonObject;
	private final MockedStatic<JSONPortletResponseUtil>
		_jsonPortletResponseUtilMockedStatic = Mockito.mockStatic(
			JSONPortletResponseUtil.class);
	private final Language _language = Mockito.mock(Language.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final MockedStatic<SessionMessages> _sessionMessagesMockedStatic =
		Mockito.mockStatic(SessionMessages.class);

}