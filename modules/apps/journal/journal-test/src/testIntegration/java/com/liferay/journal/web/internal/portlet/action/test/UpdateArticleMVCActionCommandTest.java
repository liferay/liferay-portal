/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
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
import jakarta.portlet.PortletPreferences;

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
		_group1 = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessAction() throws Exception {
		_processAction(_getMockLiferayPortletActionRequest());

		JournalArticle journalArticle1 =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group1.getGroupId(), "title");

		Assert.assertNotNull(journalArticle1.getDisplayDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle1.getStatus());
		Assert.assertEquals(1.0, journalArticle1.getVersion(), 0.01);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group1);

		String portletResource = LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT,
			HashMapBuilder.put(
				"groupExternalReferenceCode",
				() -> {
					Company company = _companyLocalService.getCompany(
						_group1.getCompanyId());

					Group companyGroup = company.getGroup();

					return new String[] {
						companyGroup.getExternalReferenceCode()
					};
				}
			).build());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				journalArticle1.getArticleId(),
				journalArticle1.getFriendlyURLMap());

		mockLiferayPortletActionRequest.setParameter(
			"portletResource", portletResource);
		mockLiferayPortletActionRequest.setParameter(
			"refererPlid", String.valueOf(layout.getPlid()));

		_processAction(mockLiferayPortletActionRequest);

		JournalArticle journalArticle2 =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group1.getGroupId(), "title");

		Assert.assertEquals(
			journalArticle1.getDisplayDate(), journalArticle2.getDisplayDate());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle2.getStatus());
		Assert.assertEquals(1.1, journalArticle2.getVersion(), 0.01);

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, portletResource);

		Assert.assertEquals(
			journalArticle2.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertNull(
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	@Test
	@TestInfo("LPS-172561")
	public void testProcessActionWithDuplicatedFriendlyURL() throws Exception {
		String friendlyURL1 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		_group2 = GroupTestUtil.addGroup();

		_addArticle(
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL1 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.SPAIN, friendlyURL1 + "-es"
			).put(
				LocaleUtil.US, friendlyURL1
			).build(),
			_group2.getGroupId());

		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States) already exists in other ",
				"sites or asset libraries."),
			HashMapBuilder.put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States), Chinese (China) and ",
				"Japanese (Japan) already exists in other sites or asset ",
				"libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States), Chinese (China), ",
				"Japanese (Japan) and 1 more translations already exist in ",
				"other sites or asset libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL1 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States), Chinese (China), ",
				"Spanish (Spain) and 2 more translations already exist in ",
				"other sites or asset libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL1 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.SPAIN, friendlyURL1 + "-es"
			).put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			null,
			HashMapBuilder.put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());

		String friendlyURL2 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		_addArticle(
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL2 + "-zh"
			).put(
				LocaleUtil.JAPAN, friendlyURL2 + "-ja"
			).put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build(),
			_group2.getGroupId());

		_group3 = GroupTestUtil.addGroup();

		_addArticle(
			HashMapBuilder.put(
				LocaleUtil.FRANCE, friendlyURL2 + "-fr"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.SPAIN, friendlyURL2 + "-es"
			).put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build(),
			_group3.getGroupId());

		_group4 = GroupTestUtil.addGroup();

		_addArticle(
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL2 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.US, friendlyURL2
			).build(),
			_group4.getGroupId());

		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in Chinese (China), Japanese (Japan) and German ",
				"(Germany) already exists in other sites or asset libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL2 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL2 + "-ja"
			).put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States), Chinese (China) and ",
				"German (Germany) already exists in other sites or asset ",
				"libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL2 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.US, friendlyURL2
			).build());
		_assertProcessActionWithDuplicatedFriendlyURL(
			StringBundler.concat(
				"The content has been published but might cause errors. The ",
				"URL used in English (United States), Spanish (Spain), ",
				"Japanese (Japan) and 2 more translations already exist in ",
				"other sites or asset libraries."),
			HashMapBuilder.put(
				LocaleUtil.CHINA, StringPool.BLANK
			).put(
				LocaleUtil.FRANCE, friendlyURL2 + "-fr"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL2 + "-ja"
			).put(
				LocaleUtil.SPAIN, friendlyURL2 + "-es"
			).put(
				LocaleUtil.US, friendlyURL2
			).build());
	}

	@Test
	public void testProcessActionWithErrors() throws Exception {
		_processAction(_getMockLiferayPortletActionRequest());

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group1.getGroupId(), "title");

		Assert.assertNotNull(journalArticle);

		expectedException.expect(PortletException.class);

		_processAction(
			_getMockLiferayPortletActionRequest(
				journalArticle.getArticleId(), Collections.emptyMap()));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private JournalArticle _addArticle(
			Map<Locale, String> friendlyURLMap, long groupId)
		throws Exception {

		Map<Locale, String> contentMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		return JournalTestUtil.addArticle(
			null, groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, null, true,
			HashMapBuilder.put(
				LocaleUtil.CHINA, RandomTestUtil.randomString()
			).put(
				LocaleUtil.FRANCE, RandomTestUtil.randomString()
			).put(
				LocaleUtil.GERMANY, RandomTestUtil.randomString()
			).put(
				LocaleUtil.JAPAN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			contentMap, friendlyURLMap, contentMap, null, LocaleUtil.US, null,
			null, false, true,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _assertProcessActionWithDuplicatedFriendlyURL(
			String expectedFriendlyURLDuplicatedWarningMessage,
			Map<Locale, String> friendlyURLMap)
		throws Exception {

		_processAction(_getMockLiferayPortletActionRequest());

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchArticleByUrlTitle(
				_group1.getGroupId(), "title");

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				journalArticle.getArticleId(), friendlyURLMap);

		_processAction(mockLiferayPortletActionRequest);

		Assert.assertEquals(
			expectedFriendlyURLDuplicatedWarningMessage,
			SessionMessages.get(
				mockLiferayPortletActionRequest.getHttpServletRequest(),
				"friendlyURLDuplicated_requestProcessedWarning"));

		_journalArticleLocalService.deleteArticles(_group1.getGroupId());
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

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_createMockMultipartHttpServletRequest();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest(
				mockMultipartHttpServletRequest);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.CURRENT_URL,
			"http://localhost:" + PortalUtil.getPortalServerPort(false));

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
			_group1.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			"BASIC-WEB-CONTENT", true);

		mockLiferayPortletActionRequest.setParameter(
			"ddmStructureId", String.valueOf(ddmStructure.getStructureId()));

		mockLiferayPortletActionRequest.setParameter(
			"ddmTemplateKey", "BASIC-WEB-CONTENT");
		mockLiferayPortletActionRequest.setParameter(
			"descriptionMapAsXML_en_US", "description");
		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(_group1.getGroupId()));
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
			_companyLocalService.getCompany(_group1.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setRequest(mockMultipartHttpServletRequest);
		themeDisplay.setScopeGroupId(_group1.getGroupId());
		themeDisplay.setSiteGroupId(_group1.getGroupId());
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
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@DeleteAfterTestRun
	private Group _group3;

	@DeleteAfterTestRun
	private Group _group4;

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