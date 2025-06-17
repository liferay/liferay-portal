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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
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

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletException;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class UpdateArticleMVCActionCommandTest {

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
	public void testProcessAction() throws Exception {
		_processAction(_getMockLiferayPortletActionRequest());

		JournalArticle journalArticle1 =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group.getGroupId(), "title");

		Assert.assertNotNull(journalArticle1.getDisplayDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle1.getStatus());
		Assert.assertEquals(1.0, journalArticle1.getVersion(), 0.01);

		_processAction(
			_getMockLiferayPortletActionRequest(
				journalArticle1.getArticleId(),
				journalArticle1.getFriendlyURLMap()));

		JournalArticle journalArticle2 =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group.getGroupId(), "title");

		Assert.assertEquals(
			journalArticle1.getDisplayDate(), journalArticle2.getDisplayDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle2.getStatus());
		Assert.assertEquals(1.1, journalArticle2.getVersion(), 0.01);
	}

	@Test
	public void testProcessActionWithErrors() throws Exception {
		_processAction(_getMockLiferayPortletActionRequest());

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group.getGroupId(), "title");

		Assert.assertNotNull(journalArticle);

		expectedException.expect(PortletException.class);

		_processAction(
			_getMockLiferayPortletActionRequest(
				journalArticle.getArticleId(), Collections.emptyMap()));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

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

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_createMockMultipartHttpServletRequest();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080");

		mockMultipartHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(JournalPortletKeys.JOURNAL),
				null));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockMultipartHttpServletRequest));

		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, "/journal/add_article");
		mockLiferayPortletActionRequest.setParameter(
			"autoArticleId", StringPool.TRUE);

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_group.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			"BASIC-WEB-CONTENT", true);

		mockLiferayPortletActionRequest.setParameter(
			"ddmStructureId", String.valueOf(ddmStructure.getStructureId()));

		mockLiferayPortletActionRequest.setParameter(
			"ddmTemplateKey", "BASIC-WEB-CONTENT");
		mockLiferayPortletActionRequest.setParameter(
			"descriptionMapAsXML_en_US", "description");
		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"titleMapAsXML_en_US", "title");
		mockLiferayPortletActionRequest.setParameter(
			"workflowAction", String.valueOf(WorkflowConstants.STATUS_PENDING));

		return mockLiferayPortletActionRequest;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String articleId, Map<Locale, String> friendlyURLMap)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			ActionRequest.ACTION_NAME, "/journal/update_article");
		mockLiferayPortletActionRequest.setParameter("articleId", articleId);

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Locale locale = entry.getKey();

			mockLiferayPortletActionRequest.setParameter(
				"friendlyURL_" + locale.toString(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setParameter("version", "1.0");

		return mockLiferayPortletActionRequest;
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
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _processAction(
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		try {
			_setUpUploadPortletRequest(mockLiferayPortletActionRequest);

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
		finally {
			if (_mvcActionCommandPortal != null) {
				ReflectionTestUtil.setFieldValue(
					_mvcActionCommand, "_portal", _mvcActionCommandPortal);
			}
		}
	}

	private void _setUpUploadPortletRequest(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		_mvcActionCommandPortal = ReflectionTestUtil.getFieldValue(
			_mvcActionCommand, "_portal");

		ReflectionTestUtil.setFieldValue(
			_mvcActionCommand, "_portal",
			ProxyUtil.newProxyInstance(
				UpdateArticleMVCActionCommandTest.class.getClassLoader(),
				new Class<?>[] {Portal.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getUploadPortletRequest")) {

						LiferayPortletRequest liferayPortletRequest =
							_portal.getLiferayPortletRequest(
								mockLiferayPortletActionRequest);

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

	@Inject(filter = "mvc.command.name=/journal/add_article")
	private MVCActionCommand _mvcActionCommand;

	private Portal _mvcActionCommandPortal;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

}