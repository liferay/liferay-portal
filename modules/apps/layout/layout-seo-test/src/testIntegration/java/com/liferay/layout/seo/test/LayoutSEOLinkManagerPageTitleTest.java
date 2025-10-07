/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class LayoutSEOLinkManagerPageTitleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_layout = _addLayout();

		_group = _addGroup();

		if (FeatureFlagManagerUtil.isEnabled(
				_group.getCompanyId(), "LPD-17564")) {

			_cmsGroup = _groupLocalService.fetchGroup(
				_group.getCompanyId(), GroupConstants.CMS);

			if (_cmsGroup == null) {
				_cmsGroup = GroupTestUtil.addGroup(
					_group.getCompanyId(), TestPropsValues.getUserId(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID, GroupConstants.CMS);
			}
		}

		_layout.setGroupId(_group.getGroupId());

		_mockHttpServletRequest = _getMockHttpServletRequest();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setRequest(_mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetFullPageTitleUsesLayoutPrototypeTitle()
		throws Exception {

		String layoutPrototypeTitle = RandomTestUtil.randomString();

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			layoutPrototypeTitle);

		_prototypeGroup = layoutPrototype.getGroup();

		_layout.setGroupId(_prototypeGroup.getGroupId());

		ListMergeable<String> subtitleListMergeable = new ListMergeable<>();

		subtitleListMergeable.add(RandomTestUtil.randomString());
		subtitleListMergeable.add(RandomTestUtil.randomString());

		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				subtitleListMergeable.mergeToString(" - "), " - ",
				layoutPrototypeTitle, " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, null, subtitleListMergeable, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesLayoutTitle() throws Exception {
		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				_layout.getTitle(), " - ", _group.getName(), " - ",
				companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, null, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesLayoutTitleAndSubtitleList()
		throws Exception {

		ListMergeable<String> subtitleListMergeable = new ListMergeable<>();

		subtitleListMergeable.add(RandomTestUtil.randomString());
		subtitleListMergeable.add(RandomTestUtil.randomString());

		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				subtitleListMergeable.mergeToString(" - "), " - ",
				_layout.getTitle(), " - ", _group.getName(), " - ",
				companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, null, subtitleListMergeable, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesLayoutTitleAndTitleList()
		throws Exception {

		ListMergeable<String> titleListMergeable = new ListMergeable<>();

		titleListMergeable.add(RandomTestUtil.randomString());
		titleListMergeable.add(RandomTestUtil.randomString());

		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				titleListMergeable.mergeToString(" - "), " - ",
				_group.getName(), " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, titleListMergeable, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesLayoutTitleAndTitleListAndSubtitleList()
		throws Exception {

		ListMergeable<String> titleListMergeable = new ListMergeable<>();

		titleListMergeable.add(RandomTestUtil.randomString());
		titleListMergeable.add(RandomTestUtil.randomString());

		ListMergeable<String> subtitleListMergeable = new ListMergeable<>();

		subtitleListMergeable.add(RandomTestUtil.randomString());
		subtitleListMergeable.add(RandomTestUtil.randomString());

		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				subtitleListMergeable.mergeToString(" - "), " - ",
				titleListMergeable.mergeToString(" - "), " - ",
				_group.getName(), " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, titleListMergeable, subtitleListMergeable,
				companyName, LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesPortletTitle() throws Exception {
		_modifyLayoutToSystemManage();

		Portlet portlet = _portletLocalService.getPortletById(
			TestPropsValues.getCompanyId(), PortletKeys.BLOGS_ADMIN);

		String companyName = RandomTestUtil.randomString();

		ListMergeable<String> titleListMergeable = new ListMergeable<>();

		titleListMergeable.add(RandomTestUtil.randomString());
		titleListMergeable.add(RandomTestUtil.randomString());

		ListMergeable<String> subtitleListMergeable = new ListMergeable<>();

		subtitleListMergeable.add(RandomTestUtil.randomString());
		subtitleListMergeable.add(RandomTestUtil.randomString());

		Assert.assertEquals(
			StringBundler.concat(
				_portal.getPortletTitle(
					portlet.getRootPortletId(), LocaleUtil.getDefault()),
				" - ", _group.getName(), " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, portlet.getRootPortletId(), null, titleListMergeable,
				subtitleListMergeable, companyName, LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesStatusTitle() throws Exception {
		SessionErrors.add(_mockHttpServletRequest, NoSuchLayoutException.class);

		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				_language.get(LocaleUtil.getDefault(), "status"), " - ",
				_group.getName(), " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, null, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesTilesTitle() throws Exception {
		String tilesTitle = RandomTestUtil.randomString();
		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				tilesTitle, " - ", _group.getName(), " - ", companyName),
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, tilesTitle, null, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleUsesTilesTitleAndCompanyName()
		throws Exception {

		String tilesTitle = RandomTestUtil.randomString();
		String companyName = _group.getName();

		Assert.assertEquals(
			tilesTitle + " - " + companyName,
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, tilesTitle, null, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetFullPageTitleWithIncludeInstanceNameWithIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", true
						).build())) {

			String companyName = RandomTestUtil.randomString();

			Assert.assertEquals(
				StringBundler.concat(
					_layout.getTitle(), " - ", _group.getName(), " - ",
					companyName),
				_layoutSEOLinkManager.getFullPageTitle(
					_layout, null, null, null, null, companyName,
					LocaleUtil.getDefault()));

			Assert.assertEquals(
				StringBundler.concat(
					_layout.getTitle(), " - ", _group.getName()),
				_layoutSEOLinkManager.getFullPageTitle(
					_layout, null, null, null, null, _group.getName(),
					LocaleUtil.getDefault()));
		}
	}

	@Test
	public void testGetFullPageTitleWithIncludeInstanceNameWithoutIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", false
						).build())) {

			String companyName = RandomTestUtil.randomString();

			Assert.assertEquals(
				StringBundler.concat(_layout.getTitle(), " - ", companyName),
				_layoutSEOLinkManager.getFullPageTitle(
					_layout, null, null, null, null, companyName,
					LocaleUtil.getDefault()));
		}
	}

	@Test
	public void testGetFullPageTitleWithoutIncludeInstanceNameWithIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", true
						).build())) {

			Assert.assertEquals(
				StringBundler.concat(
					_layout.getTitle(), " - ", _group.getName()),
				_layoutSEOLinkManager.getFullPageTitle(
					_layout, null, null, null, null,
					RandomTestUtil.randomString(), LocaleUtil.getDefault()));
		}
	}

	@Test
	public void testGetFullPageTitleWithoutIncludeInstanceNameWithoutIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", false
						).build())) {

			String companyName = RandomTestUtil.randomString();

			Assert.assertEquals(
				_layout.getTitle(),
				_layoutSEOLinkManager.getFullPageTitle(
					_layout, null, null, null, null, companyName,
					LocaleUtil.getDefault()));
		}
	}

	@Test
	public void testGetFullPageUsesLayoutTitleAndCompanyName()
		throws Exception {

		String companyName = _group.getName();

		Assert.assertEquals(
			_layout.getTitle() + " - " + companyName,
			_layoutSEOLinkManager.getFullPageTitle(
				_layout, null, null, null, null, companyName,
				LocaleUtil.getDefault()));
	}

	@Test
	public void testGetPageTitleSuffixCompanyName() throws Exception {
		String companyName = _group.getName();

		Assert.assertEquals(
			companyName,
			_layoutSEOLinkManager.getPageTitleSuffix(_layout, companyName));
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixCompanyNameInCMSGroup() throws Exception {
		_layout.setGroupId(_cmsGroup.getGroupId());

		Assert.assertEquals(
			StringPool.BLANK,
			_layoutSEOLinkManager.getPageTitleSuffix(
				_layout, _cmsGroup.getDescriptiveName()));
	}

	@Test
	public void testGetPageTitleSuffixGroupNameCompanyName() throws Exception {
		String companyName = RandomTestUtil.randomString();

		Assert.assertEquals(
			_group.getName() + " - " + companyName,
			_layoutSEOLinkManager.getPageTitleSuffix(_layout, companyName));
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixGroupNameCompanyNameInCMSGroup()
		throws Exception {

		_layout.setGroupId(_cmsGroup.getGroupId());

		Assert.assertEquals(
			_cmsGroup.getDescriptiveName(),
			_layoutSEOLinkManager.getPageTitleSuffix(
				_layout, RandomTestUtil.randomString()));
	}

	@Test
	public void testGetPageTitleSuffixWithIncludeInstanceNameWithIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", true
						).build())) {

			String companyName = RandomTestUtil.randomString();

			Assert.assertEquals(
				StringBundler.concat(_group.getName(), " - ", companyName),
				_layoutSEOLinkManager.getPageTitleSuffix(_layout, companyName));

			Assert.assertEquals(
				_group.getName(),
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, _group.getName()));
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixWithIncludeInstanceNameWithIncludeSiteNameInCMSGroup()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_cmsGroup.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", true
						).build())) {

			_layout.setGroupId(_cmsGroup.getGroupId());

			Assert.assertEquals(
				_cmsGroup.getDescriptiveName(),
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));

			Assert.assertEquals(
				StringPool.BLANK,
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, _cmsGroup.getDescriptiveName()));
		}
	}

	@Test
	public void testGetPageTitleSuffixWithIncludeInstanceNameWithoutIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", false
						).build())) {

			String companyName = RandomTestUtil.randomString();

			Assert.assertEquals(
				companyName,
				_layoutSEOLinkManager.getPageTitleSuffix(_layout, companyName));
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixWithIncludeInstanceNameWithoutIncludeSiteNameInCMSGroup()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_cmsGroup.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", true
						).put(
							"includeSiteName", false
						).build())) {

			_layout.setGroupId(_cmsGroup.getGroupId());

			Assert.assertEquals(
				StringPool.BLANK,
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));
		}
	}

	@Test
	public void testGetPageTitleSuffixWithoutIncludeInstanceNameWithIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", true
						).build())) {

			Assert.assertEquals(
				_group.getName(),
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixWithoutIncludeInstanceNameWithIncludeSiteNameInCMSGroup()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_cmsGroup.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", true
						).build())) {

			_layout.setGroupId(_cmsGroup.getGroupId());

			Assert.assertEquals(
				_cmsGroup.getDescriptiveName(),
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));
		}
	}

	@Test
	public void testGetPageTitleSuffixWithoutIncludeInstanceNameWithoutIncludeSiteName()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", false
						).build())) {

			Assert.assertEquals(
				StringPool.BLANK,
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	@TestInfo("LPD-66633")
	public void testGetPageTitleSuffixWithoutIncludeInstanceNameWithoutIncludeSiteNameInCMSGroup()
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_cmsGroup.getGroupId(), _PID,
						HashMapDictionaryBuilder.<String, Object>put(
							"includeInstanceName", false
						).put(
							"includeSiteName", false
						).build())) {

			_layout.setGroupId(_cmsGroup.getGroupId());

			Assert.assertEquals(
				StringPool.BLANK,
				_layoutSEOLinkManager.getPageTitleSuffix(
					_layout, RandomTestUtil.randomString()));
		}
	}

	@Test
	public void testGetPageTitleUsesLayoutPrototypeTitle() throws Exception {
		String layoutPrototypeTitle = RandomTestUtil.randomString();

		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			layoutPrototypeTitle);

		_prototypeGroup = layoutPrototype.getGroup();

		_layout.setGroupId(_prototypeGroup.getGroupId());

		String actual = _layoutSEOLinkManager.getPageTitle(
			_layout, null, null, null, null, LocaleUtil.getDefault());

		Assert.assertEquals(layoutPrototypeTitle, actual);
	}

	@Test
	public void testGetPageTitleUsesPortletTitle() throws Exception {
		_modifyLayoutToSystemManage();

		Portlet portlet = _portletLocalService.getPortletById(
			TestPropsValues.getCompanyId(), PortletKeys.BLOGS_ADMIN);

		Assert.assertEquals(
			_portal.getPortletTitle(
				portlet.getRootPortletId(), LocaleUtil.getDefault()),
			_layoutSEOLinkManager.getPageTitle(
				_layout, portlet.getRootPortletId(), null, null, null,
				LocaleUtil.getDefault()));
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		group.setName(RandomTestUtil.randomString());

		return _groupLocalService.updateGroup(group);
	}

	private Layout _addLayout() throws Exception {
		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		layout.setTitle(RandomTestUtil.randomString());

		return layout;
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _modifyLayoutToSystemManage() {
		_layout.setSystem(true);
		_layout.setFriendlyURL("/manage");
	}

	private static final String _PID =
		"com.liferay.layout.seo.internal.configuration." +
			"LayoutSEOGeneralGroupConfiguration";

	private Group _cmsGroup;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	private MockHttpServletRequest _mockHttpServletRequest;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private Group _prototypeGroup;

	private ServiceContext _serviceContext;

}