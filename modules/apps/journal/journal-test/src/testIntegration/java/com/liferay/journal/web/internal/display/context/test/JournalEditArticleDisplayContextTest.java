/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.DesignLibraryTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
public class JournalEditArticleDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testGetDDMTemplate() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group1.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		DDMTemplate ddmTemplate2 = DDMTemplateTestUtil.addTemplate(
			_group1.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		Assert.assertEquals(
			ddmTemplate2,
			_getDDMTemplate(
				HashMapBuilder.put(
					"ddmStructureId",
					String.valueOf(ddmStructure.getStructureId())
				).build()));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group1.getGroupId(),
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate2.getTemplateKey());

		Assert.assertEquals(
			ddmTemplate2,
			_getDDMTemplate(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build()));

		journalArticle.setDDMTemplateKey(ddmTemplate1.getTemplateKey());

		journalArticle = JournalTestUtil.updateArticle(journalArticle);

		Assert.assertEquals(
			ddmTemplate1,
			_getDDMTemplate(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build()));
	}

	@Test
	@TestInfo("LPS-172561")
	public void testGetFriendlyURLDuplicatedWarningMessage() throws Exception {
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

		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States) already exists in other " +
				"sites or asset libraries.",
			HashMapBuilder.put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China) and " +
				"Japanese (Japan) already exists in other sites or asset " +
					"libraries.",
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China), " +
				"Japanese (Japan) and 1 more translations already exist in " +
					"other sites or asset libraries.",
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL1 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL1 + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL1 + "-ja"
			).put(
				LocaleUtil.US, friendlyURL1
			).build());
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China), " +
				"Spanish (Spain) and 2 more translations already exist in " +
					"other sites or asset libraries.",
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
		_assertGetFriendlyURLDuplicatedWarningMessage(
			StringPool.BLANK,
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

		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in Chinese (China), Japanese (Japan) and German " +
				"(Germany) already exists in other sites or asset libraries.",
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
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China) and " +
				"German (Germany) already exists in other sites or asset " +
					"libraries.",
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL2 + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL2 + "-de"
			).put(
				LocaleUtil.US, friendlyURL2
			).build());
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Spanish (Spain), " +
				"Japanese (Japan) and 2 more translations already exist in " +
					"other sites or asset libraries.",
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

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		String friendlyURL3 = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		_addArticle(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, friendlyURL3 + "-es"
			).put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build(),
			_depotEntry.getGroupId());

		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in Spanish (Spain) already exists in other sites " +
				"or asset libraries.",
			HashMapBuilder.put(
				LocaleUtil.SPAIN, friendlyURL3 + "-es"
			).put(
				LocaleUtil.US,
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
	}

	@FeatureFlag("LPD-57283")
	@Test
	public void testGetSelectAssetDisplayPageContext() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		Group designLibraryGroup = DesignLibraryTestUtil.addDesignLibraryGroup(
			_group1.getGroupId(), true);
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				designLibraryGroup.getGroupId(),
				PortalUtil.getClassNameId(JournalArticle.class),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		Map<String, Object> selectAssetDisplayPageContext =
			ReflectionTestUtil.invoke(
				_getJournalEditArticleDisplayContext(
					HashMapBuilder.put(
						"articleId", journalArticle.getArticleId()
					).put(
						"resourcePrimKey",
						String.valueOf(journalArticle.getResourcePrimKey())
					).build()),
				"getSelectAssetDisplayPageContext", new Class<?>[0]);

		Assert.assertEquals(
			designLibraryLayoutPageTemplateEntry.getName(),
			selectAssetDisplayPageContext.get("defaultDisplayPageName"));
	}

	@FeatureFlag("LPD-11228")
	@Test
	public void testIsShowPublishModal() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		Object journalEditArticleDisplayContext =
			_getJournalEditArticleDisplayContext(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build());

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				journalEditArticleDisplayContext, "_isShowPublishModal",
				new Class<?>[0]));

		journalArticle.setStatus(WorkflowConstants.STATUS_APPROVED);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				journalEditArticleDisplayContext, "_isShowPublishModal",
				new Class<?>[0]));
	}

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

	private void _assertGetFriendlyURLDuplicatedWarningMessage(
			String expectedFriendlyURLDuplicatedWarningMessage,
			Map<Locale, String> friendlyURLMap)
		throws Exception {

		JournalArticle journalArticle = _addArticle(
			friendlyURLMap, _group1.getGroupId());

		Assert.assertEquals(
			expectedFriendlyURLDuplicatedWarningMessage,
			ReflectionTestUtil.invoke(
				_getJournalEditArticleDisplayContext(
					HashMapBuilder.put(
						"articleId", journalArticle.getArticleId()
					).put(
						"resourcePrimKey",
						String.valueOf(journalArticle.getResourcePrimKey())
					).build()),
				"getFriendlyURLDuplicatedWarningMessage", new Class<?>[0]));

		_journalArticleLocalService.deleteArticles(_group1.getGroupId());
	}

	private Object _getDDMTemplate(Map<String, String> parameters)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getJournalEditArticleDisplayContext(parameters), "getDDMTemplate",
			new Class<?>[0]);
	}

	private Object _getJournalEditArticleDisplayContext(
			Map<String, String> parameters)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/journal/edit_article");
		mockHttpServletRequest.setParameters(parameters);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockHttpServletRequest.removeAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		String path = "/edit_article.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group1.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setScopeGroupId(_group1.getGroupId());
		themeDisplay.setUser(_user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"com.liferay.journal.web.internal.display.context." +
				"JournalEditArticleDisplayContext");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

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

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

	private User _user;

}