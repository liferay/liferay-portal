/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.item.selector.JournalArticleItemSelectorView;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Jürgen Kappler
 */
public class JournalArticleItemSelectorViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAncestorJournalFolder();
		_setUpBeanProperties();
		_setUpGroup();
		_setUpGroupLocalServiceUtil(_group);
		_setUpHttpServletRequest();
		_setUpJournalFolder(_ancestorJournalFolder);
		_setUpJournalFolderLocalServiceUtil(_journalFolder);
		_setUpJournalFolderServiceUtil();
		_setUpLanguage();
		_setUpPortal();
		_setUpPortletURLBuilder();
		_setUpPortletURLUtil();
		_setUpSearchDisplayStyleUtil();
		_setUpSearchOrderByUtil();
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
		_journalFolderLocalServiceUtilMockedStatic.close();
		_journalFolderServiceUtilMockedStatic.close();
		_portletURLBuilderMockedStatic.close();
		_portletURLUtilMockedStatic.close();
		_searchDisplayStyleUtilMockedStatic.close();
		_searchOrderByUtilMockedStatic.close();
	}

	@Test
	public void testGetPortletBreadcrumbEntries() throws PortalException {
		_testGetPortletBreadcrumbEntries("false");
		_testGetPortletBreadcrumbEntries("true");
	}

	@Test
	public void testSearchContainerWithScope() throws Exception {
		_testSearchContainerWithScope("false");
		_testSearchContainerWithScope("true");
	}

	private void _setUpAncestorJournalFolder() {
		Mockito.when(
			_ancestorJournalFolder.getFolderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_ancestorJournalFolder.getName()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpBeanProperties() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(_beanProperties);
	}

	private void _setUpGroup() throws Exception {
		Mockito.when(
			_group.getDescriptiveName()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpGroupLocalServiceUtil(Group group) throws Exception {
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

		Mockito.when(
			GroupLocalServiceUtil.getGroup(Mockito.anyLong())
		).thenReturn(
			group
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.getDefault()
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		RenderRequest renderRequest = new MockLiferayPortletRenderRequest();

		Mockito.when(
			_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST)
		).thenReturn(
			renderRequest
		);

		RenderResponse renderResponse = new MockLiferayPortletRenderResponse();

		Mockito.when(
			_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE)
		).thenReturn(
			renderResponse
		);
	}

	private void _setUpJournalFolder(JournalFolder ancestorJournalFolder)
		throws Exception {

		String name = RandomTestUtil.randomString();

		Mockito.when(
			_journalFolder.getAncestors()
		).thenReturn(
			Arrays.asList(ancestorJournalFolder)
		);

		Mockito.when(
			_journalFolder.getFolderId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_journalFolder.getName()
		).thenReturn(
			name
		);

		JournalFolder unescapeJournalFolder = Mockito.mock(JournalFolder.class);

		Mockito.when(
			unescapeJournalFolder.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			_journalFolder.toUnescapedModel()
		).thenReturn(
			unescapeJournalFolder
		);
	}

	private void _setUpJournalFolderLocalServiceUtil(
		JournalFolder journalFolder) {

		_journalFolderLocalServiceUtilMockedStatic = Mockito.mockStatic(
			JournalFolderLocalServiceUtil.class);

		Mockito.when(
			JournalFolderLocalServiceUtil.fetchFolder(Mockito.anyLong())
		).thenReturn(
			journalFolder
		);
	}

	private void _setUpJournalFolderServiceUtil() {
		_journalFolderServiceUtilMockedStatic = Mockito.mockStatic(
			JournalFolderServiceUtil.class);

		Mockito.when(
			JournalFolderServiceUtil.getFoldersAndArticles(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt(),
				Mockito.any(OrderByComparator.class))
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			JournalFolderServiceUtil.getFoldersAndArticlesCount(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyInt())
		).thenReturn(
			0
		);
	}

	private void _setUpLanguage() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(_language);
	}

	private void _setUpPortal() {
		PortalUtil portalUtil = new PortalUtil();

		LiferayPortletResponse mockLiferayPortletResponse = Mockito.mock(
			LiferayPortletResponse.class);

		Mockito.when(
			_portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_portal.getLiferayPortletResponse(
				Mockito.any(PortletResponse.class))
		).thenReturn(
			mockLiferayPortletResponse
		);

		portalUtil.setPortal(_portal);
	}

	private void _setUpPortletURLBuilder() {
		_portletURLBuilderMockedStatic = Mockito.mockStatic(
			PortletURLBuilder.class);

		Mockito.when(
			PortletURLBuilder.create(Mockito.any())
		).thenReturn(
			new PortletURLBuilder.PortletURLStep(new MockLiferayPortletURL())
		);
	}

	private void _setUpPortletURLUtil() throws Exception {
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

		Mockito.when(
			PortletURLUtil.clone(
				Mockito.any(LiferayPortletURL.class), Mockito.anyString(),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private void _setUpSearchDisplayStyleUtil() {
		_searchDisplayStyleUtilMockedStatic = Mockito.mockStatic(
			SearchDisplayStyleUtil.class);

		Mockito.when(
			SearchDisplayStyleUtil.getDisplayStyle(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _setUpSearchOrderByUtil() {
		_searchOrderByUtilMockedStatic = Mockito.mockStatic(
			SearchOrderByUtil.class);

		Mockito.when(
			SearchOrderByUtil.getOrderByCol(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	private void _testGetPortletBreadcrumbEntries(String scopeGroupType)
		throws PortalException {

		Mockito.when(
			_httpServletRequest.getParameter("scopeGroupType")
		).thenReturn(
			scopeGroupType
		);

		JournalArticleItemSelectorView journalArticleItemSelectorView =
			Mockito.mock(JournalArticleItemSelectorView.class);

		Mockito.when(
			journalArticleItemSelectorView.getTitle(Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		JournalArticleItemSelectorViewDisplayContext
			journalArticleItemSelectorViewDisplayContext =
				new JournalArticleItemSelectorViewDisplayContext(
					_httpServletRequest,
					Mockito.mock(InfoItemItemSelectorCriterion.class),
					RandomTestUtil.randomString(),
					journalArticleItemSelectorView,
					Mockito.mock(JournalWebConfiguration.class), _portal,
					new MockLiferayPortletURL(),
					Mockito.mock(ResourcePermissionLocalService.class),
					Mockito.mock(RoleLocalService.class), false,
					Mockito.mock(StagingGroupHelper.class));

		List<BreadcrumbEntry> breadcrumbEntries =
			journalArticleItemSelectorViewDisplayContext.
				getPortletBreadcrumbEntries();

		Assert.assertEquals(
			breadcrumbEntries.toString(), 4, breadcrumbEntries.size());

		BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(0);

		Assert.assertEquals("sites-and-libraries", breadcrumbEntry.getTitle());

		String url = breadcrumbEntry.getURL();

		Assert.assertTrue(url.contains("param_groupType=site"));
		Assert.assertTrue(
			url.contains("param_scopeGroupType=" + scopeGroupType));
		Assert.assertTrue(url.contains("param_showGroupSelector=true"));

		breadcrumbEntry = breadcrumbEntries.get(1);

		Assert.assertEquals(
			_group.getDescriptiveName(LocaleUtil.getDefault()),
			breadcrumbEntry.getTitle());

		url = breadcrumbEntry.getURL();

		Assert.assertTrue(url.contains("param_groupType=site"));
		Assert.assertTrue(
			url.contains("param_scopeGroupType=" + scopeGroupType));
		Assert.assertTrue(
			url.contains(
				"param_folderId=" +
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID));

		breadcrumbEntry = breadcrumbEntries.get(2);

		Assert.assertEquals(
			_ancestorJournalFolder.getName(), breadcrumbEntry.getTitle());

		url = breadcrumbEntry.getURL();

		Assert.assertTrue(url.contains("param_groupType=site"));
		Assert.assertTrue(
			url.contains("param_scopeGroupType=" + scopeGroupType));
		Assert.assertTrue(
			url.contains(
				"param_folderId=" + _ancestorJournalFolder.getFolderId()));

		breadcrumbEntry = breadcrumbEntries.get(3);

		Assert.assertEquals(
			_journalFolder.getName(), breadcrumbEntry.getTitle());

		Assert.assertNull(breadcrumbEntry.getURL());
	}

	private void _testSearchContainerWithScope(String scope) throws Exception {
		Mockito.when(
			_httpServletRequest.getParameter("scope")
		).thenReturn(
			scope
		);

		JournalArticleItemSelectorViewDisplayContext
			journalArticleItemSelectorViewDisplayContext =
				new JournalArticleItemSelectorViewDisplayContext(
					_httpServletRequest,
					Mockito.mock(InfoItemItemSelectorCriterion.class),
					RandomTestUtil.randomString(),
					Mockito.mock(JournalArticleItemSelectorView.class),
					Mockito.mock(JournalWebConfiguration.class), _portal,
					new MockLiferayPortletURL(),
					Mockito.mock(ResourcePermissionLocalService.class),
					Mockito.mock(RoleLocalService.class), false,
					Mockito.mock(StagingGroupHelper.class));

		SearchContainer<?> searchContainer =
			journalArticleItemSelectorViewDisplayContext.getSearchContainer();

		Assert.assertNotNull(searchContainer);

		MockLiferayPortletURL mockLiferayPortletURL =
			(MockLiferayPortletURL)searchContainer.getIteratorURL();

		Assert.assertEquals(scope, mockLiferayPortletURL.getParameter("scope"));
	}

	private static MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic;
	private static MockedStatic<JournalFolderLocalServiceUtil>
		_journalFolderLocalServiceUtilMockedStatic;
	private static MockedStatic<JournalFolderServiceUtil>
		_journalFolderServiceUtilMockedStatic;
	private static MockedStatic<PortletURLBuilder>
		_portletURLBuilderMockedStatic;
	private static MockedStatic<PortletURLUtil> _portletURLUtilMockedStatic;
	private static MockedStatic<SearchDisplayStyleUtil>
		_searchDisplayStyleUtilMockedStatic;
	private static MockedStatic<SearchOrderByUtil>
		_searchOrderByUtilMockedStatic;

	private final JournalFolder _ancestorJournalFolder = Mockito.mock(
		JournalFolder.class);
	private final BeanProperties _beanProperties = Mockito.mock(
		BeanProperties.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final JournalFolder _journalFolder = Mockito.mock(
		JournalFolder.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}