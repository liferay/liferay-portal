/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.capability.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.repository.authorization.oauth2.Token;
import com.liferay.document.library.repository.authorization.oauth2.TokenStore;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.Date;

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
 * @author Yash Gupta
 */
@RunWith(Arquillian.class)
public class SharepointRepositoryAuthorizationCapabilityTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_name = RandomTestUtil.randomString();

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"clientId", RandomTestUtil.randomString()
			).put(
				"clientSecret", RandomTestUtil.randomString()
			).put(
				"name", _name
			).put(
				"scope", RandomTestUtil.randomString()
			).put(
				"tenantId", RandomTestUtil.randomString()
			).build());

		_repository = _repositoryLocalService.addRepository(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_classNameLocalService.getClassNameId(
				"com.liferay.sharepoint.rest.repository.internal.document." +
					"library.repository.external.SharepointExtRepository" +
						_name),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				"library-path", RandomTestUtil.randomString()
			).put(
				"site-absolute-url", RandomTestUtil.randomString()
			).build(),
			false,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		_repositoryLocalService.deleteRepository(_repository);

		ConfigurationTestUtil.deleteFactoryConfiguration(_pid, _FACTORY_PID);

		_tokenStore.delete(_name, TestPropsValues.getUserId());
	}

	@Test
	@TestInfo("LPD-112524")
	public void testAuthorize() throws Exception {
		_testAuthorize();
		_testAuthorizeWhenRootFolderDoesNotExist();
		_testAuthorizeWhenRootFolderIsNotMountPoint();
		_testAuthorizeWhenUserHasToken();
		_testAuthorizeWhenUserIsNotSignedIn();
	}

	private Layout _addLayout(String rootFolderExternalReferenceCode)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, DLPortletKeys.DOCUMENT_LIBRARY);

		portletPreferences.setValue(
			"rootFolderExternalReferenceCode", rootFolderExternalReferenceCode);

		portletPreferences.store();

		return layout;
	}

	private void _assertRenderReadsRepository(
			Layout layout, MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		try {
			_render(
				layout, mockHttpServletRequest,
				mockLiferayPortletRenderResponse, true);

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Class<?> clazz = runtimeException.getClass();

			Assert.assertEquals(
				"com.microsoft.aad.msal4j.MsalClientException",
				clazz.getName());
		}

		Assert.assertNull(_getRedirectedURL(mockLiferayPortletRenderResponse));
	}

	private String _getRedirectedURL(
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse) {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private ThemeDisplay _getThemeDisplay(Layout layout, boolean signedIn)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(DLPortletKeys.DOCUMENT_LIBRARY);

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(signedIn);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _render(
			Layout layout, MockHttpServletRequest mockHttpServletRequest,
			MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse,
			boolean signedIn)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest) {

				@Override
				public String getPortletName() {
					return DLPortletKeys.DOCUMENT_LIBRARY;
				}

			};

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				DLPortletKeys.DOCUMENT_LIBRARY, StringPool.DASH,
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			mockLiferayPortletRenderResponse);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout, signedIn));
		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		return _dlViewMVCRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);
	}

	private void _testAuthorize() throws Exception {
		Folder mountFolder = _dlAppLocalService.getMountFolder(
			_repository.getRepositoryId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(
				_addLayout(mountFolder.getExternalReferenceCode()),
				mockHttpServletRequest, mockLiferayPortletRenderResponse,
				true));
		Assert.assertTrue(
			StringUtil.startsWith(
				_getRedirectedURL(mockLiferayPortletRenderResponse),
				"https://login.microsoftonline.com"));
	}

	private void _testAuthorizeWhenRootFolderDoesNotExist() throws Exception {
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/document_library/error.jsp",
			_render(
				_addLayout(RandomTestUtil.randomString()),
				new MockHttpServletRequest(), mockLiferayPortletRenderResponse,
				true));
		Assert.assertNull(_getRedirectedURL(mockLiferayPortletRenderResponse));
	}

	private void _testAuthorizeWhenRootFolderIsNotMountPoint()
		throws Exception {

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/document_library/view.jsp",
			_render(
				_addLayout(folder.getExternalReferenceCode()),
				new MockHttpServletRequest(), mockLiferayPortletRenderResponse,
				true));
		Assert.assertNull(_getRedirectedURL(mockLiferayPortletRenderResponse));
	}

	private void _testAuthorizeWhenUserHasToken() throws Exception {
		_tokenStore.save(
			_name, TestPropsValues.getUserId(),
			new Token() {

				@Override
				public String getAccessToken() {
					return _accessToken;
				}

				@Override
				public Date getExpirationDate() {
					return null;
				}

				@Override
				public String getRefreshToken() {
					return _refreshToken;
				}

				@Override
				public boolean isExpired() {
					return false;
				}

				private final String _accessToken =
					RandomTestUtil.randomString();
				private final String _refreshToken =
					RandomTestUtil.randomString();

			});

		Folder mountFolder = _dlAppLocalService.getMountFolder(
			_repository.getRepositoryId());

		Layout layout = _addLayout(mountFolder.getExternalReferenceCode());

		_assertRenderReadsRepository(layout, new MockHttpServletRequest());

		MockHttpServletRequest viewFolderMockHttpServletRequest =
			new MockHttpServletRequest();

		viewFolderMockHttpServletRequest.addParameter(
			"folderId", String.valueOf(mountFolder.getFolderId()));
		viewFolderMockHttpServletRequest.addParameter(
			"mvcRenderCommandName", "/document_library/view_folder");

		_assertRenderReadsRepository(layout, viewFolderMockHttpServletRequest);
	}

	private void _testAuthorizeWhenUserIsNotSignedIn() throws Exception {
		Folder mountFolder = _dlAppLocalService.getMountFolder(
			_repository.getRepositoryId());

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/document_library/error.jsp",
			_render(
				_addLayout(mountFolder.getExternalReferenceCode()),
				new MockHttpServletRequest(), mockLiferayPortletRenderResponse,
				false));
		Assert.assertNull(_getRedirectedURL(mockLiferayPortletRenderResponse));
	}

	private static final String _FACTORY_PID =
		"com.liferay.sharepoint.rest.repository.internal.configuration." +
			"SharepointRepositoryConfiguration";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.portlet.action.DLViewMVCRenderCommand"
	)
	private MVCRenderCommand _dlViewMVCRenderCommand;

	@DeleteAfterTestRun
	private Group _group;

	private String _name;
	private String _pid;
	private Repository _repository;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@Inject
	private TokenStore _tokenStore;

}