/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class CompleteDSRecipientViewMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl",
			CompleteDSRecipientViewMVCActionCommandTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition",
				".com.liferay.digital.signature.internal.batch.02.object." +
					"relationship"
			});

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				DigitalSignatureConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"embeddedViewEnabled", true
				).put(
					"enabled", true
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());
		_dsRequestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					TestPropsValues.getCompanyId());
		_dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessAction();
		_testProcessActionWhenBackURLIsBlank();
		_testProcessActionWhenBackURLIsExternal();
		_testProcessActionWhenBackURLIsRelative();
		_testProcessActionWhenDSRecipientIsDeclined();
	}

	private long _addDSRequest(String dsEnvelopeId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		ObjectEntry dsRequestObjectEntry = _addObjectEntry(
			_dsRequestObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailSubject", RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", dsEnvelopeId
			).put(
				"requestStatus", DSRequestConstants.STATUS_SENT
			).put(
				"siteGroupId", _group.getGroupId()
			).build());

		_addObjectEntry(
			_dsRequestRecipientObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailAddress",
				() -> {
					User user = TestPropsValues.getUser();

					return user.getEmailAddress();
				}
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_dsRequestToDSRequestRecipients_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).put(
				"r_userToDSRequestRecipients_userId",
				TestPropsValues.getUserId()
			).put(
				"requestRecipientStatus", DSRequestConstants.STATUS_SENT
			).build());

		return dsRequestObjectEntry.getObjectEntryId();
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, ServiceContext serviceContext,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private DSEnvelope _createDSEnvelope(
			String dsEnvelopeId, String dsRecipientStatus)
		throws Exception {

		DSEnvelope dsEnvelope = new DSEnvelope();

		DSRecipient dsRecipient = new DSRecipient();

		User user = TestPropsValues.getUser();

		dsRecipient.setDSClientUserId(String.valueOf(user.getUserId()));
		dsRecipient.setEmailAddress(user.getEmailAddress());

		dsRecipient.setStatus(dsRecipientStatus);

		dsEnvelope.setDSEnvelopeId(dsEnvelopeId);
		dsEnvelope.setDSRecipients(ListUtil.fromArray(dsRecipient));
		dsEnvelope.setStatus(DSRequestConstants.STATUS_SENT);

		return dsEnvelope;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String backURL, long dsRequestId, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setURLHome("/" + RandomTestUtil.randomString());
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.addParameter("backURL", backURL);
		mockLiferayPortletActionRequest.addParameter(
			"dsRequestId", String.valueOf(dsRequestId));

		return mockLiferayPortletActionRequest;
	}

	private HttpServletRequest _processAction(long dsRequestId, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				"/" + RandomTestUtil.randomString(), dsRequestId, user);

		_processAction(mockLiferayPortletActionRequest);

		return mockLiferayPortletActionRequest.getHttpServletRequest();
	}

	private CustomMockLiferayPortletActionResponse _processAction(
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		CustomMockLiferayPortletActionResponse
			customMockLiferayPortletActionResponse =
				new CustomMockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			customMockLiferayPortletActionResponse);

		return customMockLiferayPortletActionResponse;
	}

	private void _testProcessAction() throws Exception {
		String dsEnvelopeId = RandomTestUtil.randomString();

		long dsRequestId = _addDSRequest(dsEnvelopeId);

		DSEnvelope dsEnvelope = _createDSEnvelope(
			dsEnvelopeId, DSRequestConstants.STATUS_COMPLETED);

		List<String> methodNames = new ArrayList<>();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						methodNames.add(method.getName());

						return dsEnvelope;
					}));

		try {
			User user = TestPropsValues.getUser();

			HttpServletRequest httpServletRequest = _processAction(
				RandomTestUtil.randomLong(), user);

			Assert.assertEquals(Collections.emptyList(), methodNames);
			Assert.assertFalse(
				SessionMessages.contains(
					httpServletRequest, "requestProcessed"));

			httpServletRequest = _processAction(
				dsRequestId, UserTestUtil.addUser());

			Assert.assertEquals(Collections.emptyList(), methodNames);
			Assert.assertFalse(
				SessionMessages.contains(
					httpServletRequest, "requestProcessed"));

			httpServletRequest = _processAction(dsRequestId, user);

			Assert.assertEquals(List.of("getDSEnvelope"), methodNames);
			Assert.assertTrue(
				SessionMessages.contains(
					httpServletRequest, "requestProcessed"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}
	}

	private void _testProcessActionWhenBackURLIsBlank() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				StringPool.BLANK, RandomTestUtil.randomLong(),
				TestPropsValues.getUser());

		CustomMockLiferayPortletActionResponse
			customMockLiferayPortletActionResponse = _processAction(
				mockLiferayPortletActionRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Assert.assertEquals(
			themeDisplay.getURLHome(),
			customMockLiferayPortletActionResponse.getRedirectLocation());
	}

	private void _testProcessActionWhenBackURLIsExternal() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				"https://example.com", RandomTestUtil.randomLong(),
				TestPropsValues.getUser());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setPortalURL("http://localhost");

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.util.PortalImpl", LoggerTestUtil.WARN)) {

			CustomMockLiferayPortletActionResponse
				customMockLiferayPortletActionResponse = _processAction(
					mockLiferayPortletActionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Assert.assertEquals(
				themeDisplay.getURLHome(),
				customMockLiferayPortletActionResponse.getRedirectLocation());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Redirect URL https://example.com is not allowed",
				logEntry.getMessage());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _testProcessActionWhenBackURLIsRelative() throws Exception {
		String backURL = "/" + RandomTestUtil.randomString();

		CustomMockLiferayPortletActionResponse
			customMockLiferayPortletActionResponse = _processAction(
				_getMockLiferayPortletActionRequest(
					backURL, RandomTestUtil.randomLong(),
					TestPropsValues.getUser()));

		Assert.assertEquals(
			backURL,
			customMockLiferayPortletActionResponse.getRedirectLocation());
	}

	private void _testProcessActionWhenDSRecipientIsDeclined()
		throws Exception {

		String dsEnvelopeId = RandomTestUtil.randomString();

		long dsRequestId = _addDSRequest(dsEnvelopeId);

		DSEnvelope dsEnvelope = _createDSEnvelope(
			dsEnvelopeId, DSRequestConstants.STATUS_DECLINED);

		List<String> methodNames = new ArrayList<>();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_dsRequestManager, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						methodNames.add(method.getName());

						return dsEnvelope;
					}));

		try {
			HttpServletRequest httpServletRequest = _processAction(
				dsRequestId, TestPropsValues.getUser());

			Assert.assertEquals(List.of("getDSEnvelope"), methodNames);
			Assert.assertFalse(
				SessionMessages.contains(
					httpServletRequest, "requestProcessed"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dsRequestManager, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			TestPropsValues.getCompanyId(), dsRequestId);

		List<DSRequestRecipient> dsRequestRecipients =
			dsRequest.getDSRequestRecipients();

		DSRequestRecipient dsRequestRecipient = dsRequestRecipients.get(0);

		Assert.assertEquals(
			DSRequestConstants.STATUS_DECLINED, dsRequestRecipient.getStatus());
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/digital_signature/complete_ds_recipient_view"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private class CustomMockLiferayPortletActionResponse
		extends MockLiferayPortletActionResponse {

		public String getRedirectLocation() {
			return _redirectLocation;
		}

		@Override
		public void sendRedirect(String location) {
			_redirectLocation = location;
		}

		private String _redirectLocation;

	}

}