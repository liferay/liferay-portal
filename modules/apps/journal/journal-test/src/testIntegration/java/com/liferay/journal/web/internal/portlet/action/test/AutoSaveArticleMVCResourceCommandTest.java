/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import java.io.ByteArrayOutputStream;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AutoSaveArticleMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testServeResource() throws Exception {
		JSONObject jsonObject = _serveResource(
			_getMockLiferayResourceRequest());

		Assert.assertTrue(jsonObject.getBoolean("success"));

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), jsonObject.getString("articleId"));

		Assert.assertNotNull(journalArticle);

		Assert.assertNotNull("title", jsonObject.getString("friendlyURL"));

		Date modifiedDate = journalArticle.getModifiedDate();

		Assert.assertNotNull(modifiedDate);
		Assert.assertEquals(
			modifiedDate.getTime(), jsonObject.getLong("modifiedDate"));

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());

		Assert.assertEquals("1.0", jsonObject.getString("version"));

		jsonObject = _serveResource(
			_getMockLiferayResourceRequest(
				journalArticle.getArticleId(),
				journalArticle.getFriendlyURLMap()));

		Assert.assertTrue(jsonObject.getBoolean("success"));

		journalArticle = _journalArticleLocalService.fetchArticle(
			_group.getGroupId(), jsonObject.getString("articleId"));

		Assert.assertFalse(jsonObject.has("friendlyURL"));

		modifiedDate = journalArticle.getModifiedDate();

		Assert.assertNotNull(modifiedDate);
		Assert.assertEquals(
			modifiedDate.getTime(), jsonObject.getLong("modifiedDate"));

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());

		Assert.assertEquals("1.0", jsonObject.getString("version"));
	}

	@Test
	public void testServeResourceAddDraftArticleWithOwnerPermissions()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"groupPermissions", "ADD_DISCUSSION");
		mockLiferayResourceRequest.setParameter(
			"guestPermissions", "ADD_DISCUSSION");
		mockLiferayResourceRequest.setParameter(
			"inputPermissionsViewRole", "Owner");

		_serveResource(mockLiferayResourceRequest);

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group.getGroupId(), "title");

		Assert.assertNotNull(journalArticle);
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());
		Assert.assertFalse(_hasGuestViewPermission(journalArticle));

		_serveResource(
			_getMockLiferayResourceRequest(
				journalArticle.getArticleId(),
				journalArticle.getFriendlyURLMap()));

		journalArticle = _journalArticleLocalService.fetchArticleByUrlTitle(
			_group.getGroupId(), "title");

		Assert.assertNotNull(journalArticle);
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());
		Assert.assertFalse(_hasGuestViewPermission(journalArticle));
	}

	@Test
	public void testServeResourceWithErrors() throws Exception {
		JSONObject jsonObject = _serveResource(
			_getMockLiferayResourceRequest());

		Assert.assertTrue(jsonObject.getBoolean("success"));

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticle(
				_group.getGroupId(), jsonObject.getString("articleId"));

		jsonObject = _serveResource(
			_getMockLiferayResourceRequest(
				journalArticle.getArticleId(), Collections.emptyMap()));

		Assert.assertFalse(jsonObject.getBoolean("success"));
		Assert.assertEquals(
			_language.get(
				_portal.getSiteDefaultLocale(_group),
				"you-must-define-a-friendly-url-for-the-default-language"),
			jsonObject.getString("errorMessage"));
	}

	private MockMultipartHttpServletRequest
		_createMockMultipartHttpServletRequest() {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setCharacterEncoding(StringPool.UTF8);
		mockMultipartHttpServletRequest.setContentType(
			StringBundler.concat(
				MediaType.MULTIPART_FORM_DATA_VALUE,
				"; boundary=WebKitFormBoundary", StringUtil.randomString()));

		return mockMultipartHttpServletRequest;
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest()
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_createMockMultipartHttpServletRequest();

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest(mockMultipartHttpServletRequest);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080");

		mockMultipartHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(JournalPortletKeys.JOURNAL),
				null));

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockMultipartHttpServletRequest));

		mockLiferayResourceRequest.setParameter(
			"autoArticleId", StringPool.TRUE);

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_group.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			"BASIC-WEB-CONTENT", true);

		mockLiferayResourceRequest.setParameter(
			"ddmStructureId", String.valueOf(ddmStructure.getStructureId()));

		mockLiferayResourceRequest.setParameter(
			"ddmTemplateKey", "BASIC-WEB-CONTENT");
		mockLiferayResourceRequest.setParameter(
			"descriptionMapAsXML_en_US", "description");
		mockLiferayResourceRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayResourceRequest.setParameter("titleMapAsXML_en_US", "title");
		mockLiferayResourceRequest.setParameter(
			"workflowAction", String.valueOf(WorkflowConstants.STATUS_DRAFT));

		return mockLiferayResourceRequest;
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			String articleId, Map<Locale, String> friendlyURLMap)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest();

		mockLiferayResourceRequest.setParameter("articleId", articleId);

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Locale locale = entry.getKey();

			mockLiferayResourceRequest.setParameter(
				"friendlyURL_" + locale.toString(), entry.getValue());
		}

		mockLiferayResourceRequest.setParameter("version", "1.0");

		return mockLiferayResourceRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			MockMultipartHttpServletRequest mockMultipartHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		mockMultipartHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setRequest(mockMultipartHttpServletRequest);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		User user = TestPropsValues.getUser();

		themeDisplay.setTimeZone(user.getTimeZone());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private boolean _hasGuestViewPermission(JournalArticle journalArticle)
		throws Exception {

		Role role = _roleLocalService.getRole(
			journalArticle.getCompanyId(), RoleConstants.GUEST);

		return _resourcePermissionLocalService.hasResourcePermission(
			journalArticle.getCompanyId(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(journalArticle.getResourcePrimKey()),
			role.getRoleId(), ActionKeys.VIEW);
	}

	private JSONObject _serveResource(
			MockLiferayResourceRequest mockLiferayResourceRequest)
		throws Exception {

		try {
			MockLiferayResourceResponse mockLiferayResourceResponse =
				new MockLiferayResourceResponse();

			_setUpUploadPortletRequest(mockLiferayResourceRequest);

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, mockLiferayResourceResponse);

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockLiferayResourceResponse.getPortletOutputStream();

			return JSONFactoryUtil.createJSONObject(
				byteArrayOutputStream.toString());
		}
		finally {
			if (_mvcResourceCommandPortal != null) {
				ReflectionTestUtil.setFieldValue(
					_mvcResourceCommand, "_portal", _mvcResourceCommandPortal);
			}
		}
	}

	private void _setUpUploadPortletRequest(
		MockLiferayResourceRequest mockLiferayResourceRequest) {

		_mvcResourceCommandPortal = ReflectionTestUtil.getFieldValue(
			_mvcResourceCommand, "_portal");

		ReflectionTestUtil.setFieldValue(
			_mvcResourceCommand, "_portal",
			ProxyUtil.newProxyInstance(
				AutoSaveArticleMVCResourceCommandTest.class.getClassLoader(),
				new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getUploadPortletRequest")) {

						LiferayPortletRequest liferayPortletRequest =
							_portal.getLiferayPortletRequest(
								mockLiferayResourceRequest);

						return UploadTestUtil.createUploadPortletRequest(
							_portal.getUploadServletRequest(
								liferayPortletRequest.getHttpServletRequest()),
							liferayPortletRequest,
							_portal.getPortletNamespace(
								liferayPortletRequest.getPortletName()));
					}

					return method.invoke(_portal, args);
				}));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Language _language;

	@Inject(filter = "mvc.command.name=/journal/auto_save_article")
	private MVCResourceCommand _mvcResourceCommand;

	private Portal _mvcResourceCommandPortal;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}