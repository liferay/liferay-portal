/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.HashMap;
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
 * @author Vilmos Papp
 */
@RunWith(Arquillian.class)
public class PortalImplActualURLTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_userGroup = _userGroupLocalService.addUserGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			TestPropsValues.getCompanyId(), "Test " + RandomTestUtil.nextInt(),
			StringPool.BLANK, _serviceContext);

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testChildLayoutFriendlyURL() throws Exception {
		Group group = _userGroup.getGroup();

		Layout homeLayout = _layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Home", StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK, _serviceContext);

		_layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), true,
			homeLayout.getLayoutId(), "Child Layout", StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK, _serviceContext);

		Assert.assertNotNull(
			_portal.getActualURL(
				group.getGroupId(), true, Portal.PATH_MAIN,
				"/~/" + _userGroup.getUserGroupId() + "/child-layout",
				new HashMap<>(), _getRequestContext()));

		try {
			_portal.getActualURL(
				group.getGroupId(), true, Portal.PATH_MAIN,
				"/~/" + _userGroup.getUserGroupId() +
					"/nonexistent-child-layout",
				new HashMap<>(), _getRequestContext());

			Assert.fail();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}
	}

	@Test
	@TestInfo("LPD-86119")
	public void testGetActualURLWithDisplayPageTemplateAndVersion()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle firstVersionJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			_portal.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), locale, true, true,
			_serviceContext);

		Assert.assertEquals(
			0,
			Double.compare(
				Double.valueOf("1.0"),
				firstVersionJournalArticle.getVersion()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			firstVersionJournalArticle.getStatus());

		JournalArticle secondVersionJournalArticle =
			JournalTestUtil.updateArticle(
				firstVersionJournalArticle,
				RandomTestUtil.randomLocaleStringMap(),
				firstVersionJournalArticle.getContent(), true, true,
				_serviceContext);

		Assert.assertEquals(
			0,
			Double.compare(
				Double.valueOf("1.1"),
				secondVersionJournalArticle.getVersion()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			secondVersionJournalArticle.getStatus());

		String firstVersionTitle = firstVersionJournalArticle.getTitle(locale);
		String secondVersionTitle = secondVersionJournalArticle.getTitle(
			locale);

		Assert.assertNotEquals(firstVersionTitle, secondVersionTitle);

		JournalArticle draftVersionJournalArticle =
			JournalTestUtil.updateArticle(
				secondVersionJournalArticle,
				RandomTestUtil.randomLocaleStringMap(),
				secondVersionJournalArticle.getContent(), true, false,
				_serviceContext);

		Assert.assertEquals(
			0,
			Double.compare(
				Double.valueOf("1.2"),
				draftVersionJournalArticle.getVersion()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			draftVersionJournalArticle.getStatus());

		String draftVersionTitle = draftVersionJournalArticle.getTitle(locale);

		Assert.assertNotEquals(draftVersionTitle, secondVersionTitle);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				firstVersionJournalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);
		String friendlyURL =
			_layoutDisplayPageProvider.getURLSeparator() +
				secondVersionJournalArticle.getUrlTitle(locale);

		_testGetActualURLWithDisplayPageTemplateAndVersion(
			layoutPageTemplateEntry.getPlid(), firstVersionTitle, friendlyURL,
			"1.0");
		_testGetActualURLWithDisplayPageTemplateAndVersion(
			layoutPageTemplateEntry.getPlid(), secondVersionTitle, friendlyURL,
			"1.1");
		_testGetActualURLWithDisplayPageTemplateAndVersion(
			layoutPageTemplateEntry.getPlid(), draftVersionTitle, friendlyURL,
			"1.2");
		_testGetActualURLWithDisplayPageTemplateAndVersion(
			layoutPageTemplateEntry.getPlid(), secondVersionTitle, friendlyURL,
			null);
	}

	@Test
	public void testNodeLayoutActualURL() throws Exception {
		Group group = _userGroup.getGroup();

		Layout homeLayout = _layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Home", StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK, _serviceContext);

		Layout nodeLayout = _layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), true,
			homeLayout.getLayoutId(), "Node", StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_NODE, false,
			StringPool.BLANK, _serviceContext);

		Layout childLayout = _layoutLocalService.addLayout(
			null, _serviceContext.getUserId(), group.getGroupId(), true,
			nodeLayout.getLayoutId(), "Child Layout", StringPool.BLANK,
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK, _serviceContext);

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			HttpComponentsUtil.getQueryString(
				_portal.getActualURL(
					group.getGroupId(), true, Portal.PATH_MAIN,
					"/~/" + _userGroup.getUserGroupId() + "/node",
					new HashMap<>(), _getRequestContext())));

		Assert.assertNull(parameterMap.get("p_l_id"));
		Assert.assertEquals(
			childLayout.getGroupId(), MapUtil.getLong(parameterMap, "groupId"));
		Assert.assertEquals(
			childLayout.isPrivateLayout(),
			MapUtil.getBoolean(parameterMap, "privateLayout"));
		Assert.assertEquals(
			childLayout.getLayoutId(),
			MapUtil.getLong(parameterMap, "layoutId"));
	}

	@Test
	public void testNullFriendlyURLFirstLayoutPublished() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(
			layout, LayoutTestUtil.addTypeContentLayout(_group),
			LayoutTestUtil.addTypeContentLayout(_group));

		_assertGetActualURLAsGuestUser(layout);
	}

	@Test
	public void testNullFriendlyURLFirstLayoutPublishedWithoutPermission()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(
			layout1, layout2, LayoutTestUtil.addTypeContentLayout(_group));

		_removeResourcePermission(layout1);

		_assertGetActualURLAsGuestUser(layout2);
	}

	@Test
	public void testNullFriendlyURLFirstLayoutUnpublished() throws Exception {
		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout, LayoutTestUtil.addTypeContentLayout(_group));

		_assertGetActualURLAsGuestUser(layout);
	}

	@Test
	public void testNullFriendlyURLNoLayoutPublished() throws Exception {
		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		try {
			_getActualURLAsGuestUserParameterMap();

			Assert.fail();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			Assert.assertEquals(
				noSuchLayoutException.getMessage(),
				StringBundler.concat(
					"{groupId=", _group.getGroupId(), ", privateLayout=false}"),
				noSuchLayoutException.getMessage());
		}
	}

	@Test
	public void testNullFriendlyURLNoLayoutWithPermission() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);
		Layout layout3 = LayoutTestUtil.addTypeContentLayout(_group);

		_publishLayouts(layout1, layout2, layout3);

		_removeResourcePermission(layout1, layout2, layout3);

		try {
			_getActualURLAsGuestUserParameterMap();

			Assert.fail();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			Assert.assertEquals(
				noSuchLayoutException.getMessage(),
				StringBundler.concat(
					"{groupId=", _group.getGroupId(), ", privateLayout=false}"),
				noSuchLayoutException.getMessage());
		}
	}

	private void _assertGetActualURLAsGuestUser(Layout layout)
		throws Exception {

		Map<String, String[]> parameterMap =
			_getActualURLAsGuestUserParameterMap();

		Assert.assertEquals(
			MapUtil.toString(parameterMap), layout.getPlid(),
			MapUtil.getLong(parameterMap, "p_l_id"));
	}

	private Map<String, String[]> _getActualURLAsGuestUserParameterMap()
		throws Exception {

		User user = _userLocalService.fetchGuestUser(_group.getCompanyId());

		try {
			UserTestUtil.setUser(user);

			return HttpComponentsUtil.getParameterMap(
				HttpComponentsUtil.getQueryString(
					_portal.getActualURL(
						_group.getGroupId(), false, Portal.PATH_MAIN, null,
						Collections.emptyMap(),
						HashMapBuilder.<String, Object>put(
							"request",
							() -> {
								MockHttpServletRequest mockHttpServletRequest =
									new MockHttpServletRequest();

								mockHttpServletRequest.setAttribute(
									WebKeys.USER_ID, user.getUserId());

								return mockHttpServletRequest;
							}
						).build())));
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	private Map<String, Object> _getRequestContext() {
		return HashMapBuilder.<String, Object>put(
			"request", new MockHttpServletRequest(Method.GET, "/")
		).build();
	}

	private void _publishLayouts(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);
		}
	}

	private void _removeResourcePermission(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			RoleTestUtil.removeResourcePermission(
				RoleConstants.GUEST, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()), ActionKeys.VIEW);
		}
	}

	private void _testGetActualURLWithDisplayPageTemplateAndVersion(
			long expectedPlid, String expectedTitle, String friendlyURL,
			String version)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/");

		mockHttpServletRequest.setAttribute(
			WebKeys.USER_ID, TestPropsValues.getUserId());

		Assert.assertEquals(
			expectedPlid,
			MapUtil.getLong(
				HttpComponentsUtil.getParameterMap(
					HttpComponentsUtil.getQueryString(
						_portal.getActualURL(
							_group.getGroupId(), false, Portal.PATH_MAIN,
							friendlyURL,
							HashMapBuilder.put(
								"version",
								() -> {
									if (Validator.isNull(version)) {
										return null;
									}

									return new String[] {version};
								}
							).build(),
							HashMapBuilder.<String, Object>put(
								"request", mockHttpServletRequest
							).build()))),
				"p_l_id"));

		ListMergeable<String> titleListMergeable =
			(ListMergeable<String>)mockHttpServletRequest.getAttribute(
				WebKeys.PAGE_TITLE);

		Assert.assertEquals(
			expectedTitle, titleListMergeable.mergeToString(StringPool.COMMA));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalImplActualURLTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_layoutDisplayPageProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}