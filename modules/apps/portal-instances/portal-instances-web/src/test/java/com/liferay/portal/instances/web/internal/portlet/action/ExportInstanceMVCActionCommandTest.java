/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.portal.instances.exporter.PortalInstanceExporter;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
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
public class ExportInstanceMVCActionCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_language.format(
				Mockito.nullable(Locale.class), Mockito.anyString(),
				Mockito.<Object>any())
		).thenAnswer(
			invocationOnMock ->
				invocationOnMock.getArgument(1) + ":" +
					invocationOnMock.getArgument(2)
		);

		_htmlUtilMockedStatic = Mockito.mockStatic(HtmlUtil.class);

		_htmlUtilMockedStatic.when(
			() -> HtmlUtil.escape(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0)
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

		ReflectionTestUtil.setFieldValue(
			_exportInstanceMVCActionCommand, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_exportInstanceMVCActionCommand, "_portalInstanceExporter",
			_portalInstanceExporter);
	}

	@After
	public void tearDown() {
		_htmlUtilMockedStatic.close();
		_jsonPortletResponseUtilMockedStatic.close();
	}

	@Test
	public void testErrorForEscapedExceptionMessage() throws Exception {
		_assertError(
			new RequiredCompanyException(_MESSAGE),
			"export-failed-with-message-x:" + _MESSAGE);

		_htmlUtilMockedStatic.verify(() -> HtmlUtil.escape(_MESSAGE));
	}

	@Test
	public void testErrorForMustBeOmniadminException() throws Exception {
		PrincipalException.MustBeOmniadmin mustBeOmniadminException =
			new PrincipalException.MustBeOmniadmin(_permissionChecker);

		_assertError(
			mustBeOmniadminException,
			"export-failed-with-message-x:" +
				GetterUtil.getString(mustBeOmniadminException.getMessage()));
	}

	@Test
	public void testErrorForNullExceptionMessage() throws Exception {
		_assertError(
			new RequiredCompanyException(), "export-failed-with-message-x:");
	}

	@Test
	public void testSchemaNameOnSuccess() throws Exception {
		Mockito.when(
			_portalInstanceExporter.exportPortalInstance(_COMPANY_ID)
		).thenReturn(
			_EXPORTED_PARTITION_NAME
		);

		_exportInstanceMVCActionCommand.doProcessAction(
			_getMockActionRequest(_COMPANY_ID), new MockActionResponse());

		Assert.assertEquals(
			"the-instance-was-exported-to-the-schema-x:" +
				_EXPORTED_PARTITION_NAME,
			_jsonObject.getString("successMessage"));

		Assert.assertFalse(_jsonObject.has("error"));

		Mockito.verify(
			_portalInstanceExporter
		).exportPortalInstance(
			_COMPANY_ID
		);

		_jsonPortletResponseUtilMockedStatic.verify(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class), Mockito.eq(_jsonObject)));
	}

	private void _assertError(Exception exception, String expectedError)
		throws Exception {

		Mockito.when(
			_portalInstanceExporter.exportPortalInstance(_COMPANY_ID)
		).thenThrow(
			exception
		);

		_exportInstanceMVCActionCommand.doProcessAction(
			_getMockActionRequest(_COMPANY_ID), new MockActionResponse());

		Assert.assertEquals(expectedError, _jsonObject.getString("error"));
		Assert.assertFalse(_jsonObject.has("successMessage"));

		_jsonPortletResponseUtilMockedStatic.verify(
			() -> JSONPortletResponseUtil.writeJSON(
				Mockito.any(ActionRequest.class),
				Mockito.any(ActionResponse.class), Mockito.eq(_jsonObject)));
	}

	private MockActionRequest _getMockActionRequest(long companyId) {
		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.addParameter("companyId", String.valueOf(companyId));
		mockActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));

		return mockActionRequest;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _EXPORTED_PARTITION_NAME =
		RandomTestUtil.randomString();

	private static final String _MESSAGE = RandomTestUtil.randomString();

	private final ExportInstanceMVCActionCommand
		_exportInstanceMVCActionCommand = new ExportInstanceMVCActionCommand() {

			@Override
			protected void hideDefaultSuccessMessage(
				PortletRequest portletRequest) {
			}

		};

	private MockedStatic<HtmlUtil> _htmlUtilMockedStatic;
	private JSONObject _jsonObject;
	private MockedStatic<JSONPortletResponseUtil>
		_jsonPortletResponseUtilMockedStatic;
	private final Language _language = Mockito.mock(Language.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PortalInstanceExporter _portalInstanceExporter = Mockito.mock(
		PortalInstanceExporter.class);

}