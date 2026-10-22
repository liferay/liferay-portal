/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
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
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Danny Situ
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class OpenDSRequestStrutsActionTest {

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
			OpenDSRequestStrutsActionTest.class,
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
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testExecute() throws Exception {
		_testExecuteWhenBackURLIsExternal();
		_testExecuteWhenUserIsGuest();
		_testExecuteWhenLayoutIsHidden();
		_testExecuteWhenStateIsPopUp();
		_testExecuteWhenLayoutHasSignDigitalSignaturePortlet();
		_testExecuteWhenDSRequestDoesNotExist();
		_testExecuteWhenUserIsNotDSRequestRecipient();
		_testExecuteWhenUserHasNoLayoutViewPermission();
		_testExecuteWhenSiteDoesNotExist();
	}

	private long _addDSRequest(long siteGroupId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		ObjectEntry dsRequestObjectEntry = _addObjectEntry(
			_dsRequestObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailSubject", RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"requestStatus", DSRequestConstants.STATUS_SENT
			).put(
				"siteGroupId", siteGroupId
			).build());

		_addObjectEntry(
			_dsRequestRecipientObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", _user.getEmailAddress()
			).put(
				"name", _user.getFullName()
			).put(
				"r_dsRequestToDSRequestRecipients_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).put(
				"r_userToDSRequestRecipients_userId", _user.getUserId()
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

	private void _assertRedirectedToLayout(
		long dsRequestId, Layout layout,
		MockHttpServletResponse mockHttpServletResponse) {

		Assert.assertEquals(
			HttpServletResponse.SC_MOVED_TEMPORARILY,
			mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			WindowState.MAXIMIZED.toString(),
			HttpComponentsUtil.getParameter(redirectedURL, "p_p_state", false));
		Assert.assertTrue(
			redirectedURL,
			redirectedURL.contains(
				layout.getFriendlyURL() + "/-/digital_signature/sign/" +
					dsRequestId));
	}

	private MockHttpServletResponse _execute(
			long dsRequestId, Map<String, String> parameters, User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"dsRequestId", String.valueOf(dsRequestId));
			mockHttpServletRequest.setQueryString("dsRequestId=" + dsRequestId);
			mockHttpServletRequest.setRequestURI(
				"/c/digital_signature/open_ds_request");

			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				mockHttpServletRequest.setParameter(
					entry.getKey(), entry.getValue());
			}

			Company company = _companyLocalService.getCompany(
				TestPropsValues.getCompanyId());

			Group guestGroup = _groupLocalService.getGroup(
				company.getCompanyId(), GroupConstants.GUEST);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(company);
			themeDisplay.setLocale(LocaleUtil.getSiteDefault());
			themeDisplay.setPermissionChecker(permissionChecker);
			themeDisplay.setPortalURL(company.getPortalURL(0));
			themeDisplay.setSignedIn(!user.isGuestUser());
			themeDisplay.setSiteGroupId(guestGroup.getGroupId());
			themeDisplay.setUser(user);

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setPortalURL(themeDisplay.getPortalURL());

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			try {
				_openDSRequestStrutsAction.execute(
					mockHttpServletRequest, mockHttpServletResponse);
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}

			return mockHttpServletResponse;
		}
	}

	private void _testExecuteWhenBackURLIsExternal() throws Exception {
		Group group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(group);

		long dsRequestId = _addDSRequest(group.getGroupId());

		String portletNamespace = _portal.getPortletNamespace(
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.util.PortalImpl", LoggerTestUtil.WARN)) {

			MockHttpServletResponse mockHttpServletResponse = _execute(
				dsRequestId,
				HashMapBuilder.put(
					portletNamespace + "backURL", "https://example.com"
				).put(
					"p_p_state", LiferayWindowState.POP_UP.toString()
				).build(),
				_user);

			String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

			Assert.assertFalse(
				redirectedURL, redirectedURL.contains("example.com"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Redirect URL https://example.com is not allowed",
				logEntry.getMessage());
		}
	}

	private void _testExecuteWhenDSRequestDoesNotExist() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _execute(
			RandomTestUtil.randomLong(), Collections.emptyMap(), _user);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());
	}

	private void _testExecuteWhenLayoutHasSignDigitalSignaturePortlet()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(group);

		Layout portletLayout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(
			portletLayout, DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

		long dsRequestId = _addDSRequest(group.getGroupId());

		_assertRedirectedToLayout(
			dsRequestId, portletLayout,
			_execute(dsRequestId, Collections.emptyMap(), _user));
	}

	private void _testExecuteWhenLayoutIsHidden() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		layout.setHidden(true);

		_layoutLocalService.updateLayout(layout);

		long dsRequestId = _addDSRequest(group.getGroupId());
		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_assertRedirectedToLayout(
			dsRequestId,
			_layoutLocalService.fetchFirstLayout(
				guestGroup.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false),
			_execute(dsRequestId, Collections.emptyMap(), _user));
	}

	private void _testExecuteWhenSiteDoesNotExist() throws Exception {
		long dsRequestId = _addDSRequest(RandomTestUtil.randomInt());
		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		_assertRedirectedToLayout(
			dsRequestId,
			_layoutLocalService.fetchFirstLayout(
				guestGroup.getGroupId(), false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false),
			_execute(dsRequestId, Collections.emptyMap(), _user));
	}

	private void _testExecuteWhenStateIsPopUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		long dsRequestId = _addDSRequest(group.getGroupId());

		String backURL = "/" + RandomTestUtil.randomString();
		String portletNamespace = _portal.getPortletNamespace(
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

		MockHttpServletResponse mockHttpServletResponse = _execute(
			dsRequestId,
			HashMapBuilder.put(
				portletNamespace + "backURL", backURL
			).put(
				"p_p_state", LiferayWindowState.POP_UP.toString()
			).build(),
			_user);

		Assert.assertEquals(
			HttpServletResponse.SC_MOVED_TEMPORARILY,
			mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			LiferayWindowState.POP_UP.toString(),
			HttpComponentsUtil.getParameter(redirectedURL, "p_p_state", false));
		Assert.assertEquals(
			backURL,
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(
					redirectedURL, portletNamespace + "backURL", false)));
		Assert.assertTrue(
			redirectedURL,
			redirectedURL.contains(
				layout.getFriendlyURL() + "/-/digital_signature/sign/" +
					dsRequestId));
	}

	private void _testExecuteWhenUserHasNoLayoutViewPermission()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout1.getPlid()), role.getRoleId(),
			ActionKeys.VIEW);

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

		long dsRequestId = _addDSRequest(_group.getGroupId());

		_assertRedirectedToLayout(
			dsRequestId, layout2,
			_execute(dsRequestId, Collections.emptyMap(), _user));
	}

	private void _testExecuteWhenUserIsGuest() throws Exception {
		long dsRequestId = _addDSRequest(_group.getGroupId());

		MockHttpServletResponse mockHttpServletResponse = _execute(
			dsRequestId, Collections.emptyMap(),
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()));

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(
			redirectedURL, redirectedURL.contains("/portal/login"));
		Assert.assertTrue(
			redirectedURL,
			redirectedURL.contains(
				URLCodec.encodeURL("dsRequestId=" + dsRequestId)));
	}

	private void _testExecuteWhenUserIsNotDSRequestRecipient()
		throws Exception {

		long dsRequestId = _addDSRequest(_group.getGroupId());

		MockHttpServletResponse mockHttpServletResponse = _execute(
			dsRequestId, Collections.emptyMap(), UserTestUtil.addUser());

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());

		mockHttpServletResponse = _execute(
			dsRequestId, Collections.emptyMap(), TestPropsValues.getUser());

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(filter = "path=/digital_signature/open_ds_request")
	private StrutsAction _openDSRequestStrutsAction;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}