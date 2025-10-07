/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.welcome.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureServiceUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.util.LayoutUtilityPageEntryLayoutProviderUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nikoletta Buza
 * @author Istvan Sajtos
 */
@RunWith(Arquillian.class)
public class WelcomeSiteInitializerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPS-177408")
	public void test() throws Exception {
		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		Assert.assertNotNull(
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.
						TYPE_SC_INTERNAL_SERVER_ERROR));
	}

	@Test
	@TestInfo("LPS-188909")
	public void testCannotViewPortalVersionInfoOnHomePage() throws Exception {
		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		Layout layout = _layoutLocalService.fetchDefaultLayout(
			_group.getGroupId(), false);

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layoutLocalService.getLayout(layout.getPlid()),
			_layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		Assert.assertTrue(html.contains("Enjoy using the best DXP on Earth!"));
	}

	@FeatureFlag("LPD-6378")
	@Test
	public void testCreateAccountLayoutUtilityPageEntryPageDefinition()
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, _serviceContext.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT, 0,
				_serviceContext);

		Layout layout1 = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		_addPortletToLayoutUtilityPageEntryLayout(
			layout1, LoginPortletKeys.CREATE_ACCOUNT);

		PageDefinition pageDefinition1 = _getPageDefinition(
			layout1, _getLayoutStructure(layout1));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		Layout layout2 =
			LayoutUtilityPageEntryLayoutProviderUtil.
				getDefaultLayoutUtilityPageEntryLayout(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT);

		PageDefinition pageDefinition2 = _getPageDefinition(
			layout2, _getLayoutStructure(layout2));

		Assert.assertEquals(
			_removeUUIDs(pageDefinition1.toString()),
			_removeUUIDs(pageDefinition2.toString()));
	}

	@FeatureFlag("LPD-6378")
	@Test
	public void testForgotPasswordLayoutUtilityPageEntryPageDefinition()
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, _serviceContext.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD, 0,
				_serviceContext);

		Layout layout1 = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		_addPortletToLayoutUtilityPageEntryLayout(
			layout1, LoginPortletKeys.FORGOT_PASSWORD);

		PageDefinition pageDefinition1 = _getPageDefinition(
			layout1, _getLayoutStructure(layout1));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		Layout layout2 =
			LayoutUtilityPageEntryLayoutProviderUtil.
				getDefaultLayoutUtilityPageEntryLayout(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD);

		PageDefinition pageDefinition2 = _getPageDefinition(
			layout2, _getLayoutStructure(layout2));

		Assert.assertEquals(
			_removeUUIDs(pageDefinition1.toString()),
			_removeUUIDs(pageDefinition2.toString()));
	}

	@FeatureFlag("LPD-6378")
	@Test
	public void testInitialize() throws PortalException {
		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		_assertLayoutUtilityPageEntry(
			"Create Account",
			"com_liferay_login_web_portlet_CreateAccountPortlet",
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT));
		_assertLayoutUtilityPageEntry(
			"Forgot Password",
			"com_liferay_login_web_portlet_ForgotPasswordPortlet",
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD));
		_assertLayoutUtilityPageEntry(
			"Sign In", "com_liferay_login_web_portlet_LoginPortlet",
			_layoutUtilityPageEntryLocalService.
				fetchDefaultLayoutUtilityPageEntry(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_LOGIN));
	}

	@FeatureFlag("LPD-6378")
	@Test
	public void testLoginLayoutUtilityPageEntryPageDefinition()
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, _serviceContext.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_LOGIN, 0, _serviceContext);

		Layout layout1 = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		_addPortletToLayoutUtilityPageEntryLayout(
			layout1, LoginPortletKeys.LOGIN);

		PageDefinition pageDefinition1 = _getPageDefinition(
			layout1, _getLayoutStructure(layout1));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.site.initializer.welcome");

		siteInitializer.initialize(_group.getGroupId());

		Layout layout2 =
			LayoutUtilityPageEntryLayoutProviderUtil.
				getDefaultLayoutUtilityPageEntryLayout(
					_group.getGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_LOGIN);

		PageDefinition pageDefinition2 = _getPageDefinition(
			layout2, _getLayoutStructure(layout2));

		Assert.assertEquals(
			_removeUUIDs(pageDefinition1.toString()),
			_removeUUIDs(pageDefinition2.toString()));
	}

	private void _addPortletToLayoutUtilityPageEntryLayout(
			Layout layout, String portletId)
		throws Exception {

		JSONObject editableValueJSONObject =
			_fragmentEntryProcessorRegistry.getDefaultEditableValuesJSONObject(
				StringPool.BLANK, null);

		Portlet portlet = _portletLocalService.getPortletById(portletId);

		editableValueJSONObject.put(
			"instanceId",
			portlet.isInstanceable() ? RandomTestUtil.randomString() :
				StringPool.BLANK
		).put(
			"portletId", portletId
		);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, _serviceContext.getUserId(),
				_serviceContext.getScopeGroupId(), 0, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				editableValueJSONObject.toString(), StringPool.BLANK, 0, null,
				FragmentConstants.TYPE_PORTLET, _serviceContext);

		LayoutStructure layoutStructure = _getLayoutStructure(layout);

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)
				layoutStructure.addContainerStyledLayoutStructureItem(
					layoutStructure.getMainItemId(), 0);

		containerStyledLayoutStructureItem.setWidthType("fixed");

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			containerStyledLayoutStructureItem.getItemId(), 0);

		JSONObject dataJSONObject = layoutStructure.toJSONObject();

		LayoutPageTemplateStructureServiceUtil.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				dataJSONObject.toString());
	}

	private void _assertLayoutUtilityPageEntry(
		String expectedName, String expectedPortletId,
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		Assert.assertNotNull(layoutUtilityPageEntry);
		Assert.assertEquals(expectedName, layoutUtilityPageEntry.getName());

		long layoutUtilityPageEntryPlid = layoutUtilityPageEntry.getPlid();

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layoutUtilityPageEntryPlid);

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(
			expectedPortletId, portletPreferences.getPortletId());
	}

	private LayoutStructure _getLayoutStructure(Layout layout) {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private PageDefinition _getPageDefinition(
			Layout layout, LayoutStructure layoutStructure)
		throws Exception {

		DTOConverter<LayoutStructure, PageDefinition> dtoConverter =
			(DTOConverter<LayoutStructure, PageDefinition>)
				_dtoConverterRegistry.getDTOConverter(
					LayoutStructure.class.getName());

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, layoutStructure.getMainItemId(), null,
				null, null);

		dtoConverterContext.setAttribute("layout", layout);

		return dtoConverter.toDTO(dtoConverterContext, layoutStructure);
	}

	private String _removeUUIDs(String s) {
		Matcher matcher = _uuidPattern.matcher(s);

		return matcher.replaceAll("\"\"");
	}

	private static final Pattern _uuidPattern = Pattern.compile(
		"\"[a-f0-9\\-]{36}\"");

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}