/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RootLayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
@Sync
public class ConvertLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();

		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_serviceContext = _getServiceContext(
			_group, TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_testPortletName = "TEST_PORTLET_" + RandomTestUtil.randomString();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testConvertWidgetLayoutToContentLayout() throws Exception {
		Layout originalLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString());

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(originalLayout.getPlid()),
			new MockLiferayPortletActionResponse());

		_validateLayoutConversion(originalLayout);
	}

	@Test
	public void testConvertWidgetLayoutToContentLayoutWithExistingStructure()
		throws Exception {

		Layout originalLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addDefaultSegmentsExperience(
				null, TestPropsValues.getUserId(), originalLayout.getPlid(),
				_serviceContext);

		_layoutPageTemplateStructureLocalService.addLayoutPageTemplateStructure(
			TestPropsValues.getUserId(), _group.getGroupId(),
			originalLayout.getPlid(),
			segmentsExperience.getSegmentsExperienceId(), StringPool.BLANK,
			_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(originalLayout.getPlid()),
			new MockLiferayPortletActionResponse());

		_validateLayoutConversion(originalLayout);
	}

	@Test
	@TestInfo("LPS-98589")
	public void testConvertWidgetLayoutToContentLayoutWithPortletDecorators()
		throws Exception {

		_registerTestPortlet(_testPortletName);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).buildString());

		LayoutTestUtil.addPortletToLayout(
			layout, _testPortletName,
			HashMapBuilder.put(
				"portletSetupPortletDecoratorId", new String[] {"borderless"}
			).build());

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(layout.getPlid()),
			new MockLiferayPortletActionResponse());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Layout convertedLayout = _layoutLocalService.getLayout(
			layout.getPlid());

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), convertedLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		PortletPreferences portletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				_portletPreferencesLocalService.getPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					convertedLayout.getPlid(),
					PortletIdCodec.encode(
						editableValuesJSONObject.getString("portletId"),
						editableValuesJSONObject.getString("instanceId"))));

		Assert.assertEquals(
			"borderless",
			portletPreferences.getValue(
				"portletSetupPortletDecoratorId", StringPool.BLANK));
	}

	@Test
	public void testConvertWidgetLayoutToContentLayoutWithTypeSettings()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(),
			UnicodePropertiesBuilder.put(
				LayoutConstants.CUSTOMIZABLE_LAYOUT, "true"
			).put(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
			).put(
				"column-1-customizable", "false"
			).put(
				"column-2-customizable", "false"
			).buildString());

		UnicodeProperties typeSettingsPropertiesUnicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertTrue(
			typeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutConstants.CUSTOMIZABLE_LAYOUT));
		Assert.assertTrue(
			typeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
		Assert.assertTrue(
			typeSettingsPropertiesUnicodeProperties.containsKey(
				"column-1-customizable"));
		Assert.assertTrue(
			typeSettingsPropertiesUnicodeProperties.containsKey(
				"column-2-customizable"));

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(layout.getPlid()),
			new MockLiferayPortletActionResponse());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Layout convertedLayout = _layoutLocalService.getLayout(
			layout.getPlid());

		UnicodeProperties convertedTypeSettingsPropertiesUnicodeProperties =
			convertedLayout.getTypeSettingsProperties();

		Assert.assertFalse(
			convertedTypeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutConstants.CUSTOMIZABLE_LAYOUT));
		Assert.assertFalse(
			convertedTypeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
		Assert.assertFalse(
			convertedTypeSettingsPropertiesUnicodeProperties.containsKey(
				"column-1-customizable"));
		Assert.assertFalse(
			convertedTypeSettingsPropertiesUnicodeProperties.containsKey(
				"column-2-customizable"));

		Layout darftLayout = convertedLayout.fetchDraftLayout();

		UnicodeProperties draftTypeSettingsPropertiesUnicodeProperties =
			darftLayout.getTypeSettingsProperties();

		Assert.assertFalse(
			draftTypeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutConstants.CUSTOMIZABLE_LAYOUT));
		Assert.assertFalse(
			draftTypeSettingsPropertiesUnicodeProperties.containsKey(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
		Assert.assertFalse(
			draftTypeSettingsPropertiesUnicodeProperties.containsKey(
				"column-1-customizable"));
		Assert.assertFalse(
			draftTypeSettingsPropertiesUnicodeProperties.containsKey(
				"column-2-customizable"));
	}

	private void _assertTypeSettingsProperties(
		Layout originalLayout, Layout persistedLayout) {

		UnicodeProperties originalLayoutTypeSettingsUnicodeProperties =
			originalLayout.getTypeSettingsProperties();
		UnicodeProperties persistedLayoutTypeSettingsUnicodeProperties =
			persistedLayout.getTypeSettingsProperties();

		for (Map.Entry<String, String> entry :
				originalLayoutTypeSettingsUnicodeProperties.entrySet()) {

			Assert.assertEquals(
				entry.getValue(),
				persistedLayoutTypeSettingsUnicodeProperties.getProperty(
					entry.getKey()));
		}
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long plid)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.addParameter(
			"rowIds", new String[] {String.valueOf(plid)});

		return mockLiferayPortletActionRequest;
	}

	private ServiceContext _getServiceContext(Group group, long userId)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group, userId);

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _registerTestPortlet(String portletId) {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"com.liferay.portlet.instanceable", "true"
				).put(
					"jakarta.portlet.name", portletId
				).build()));
	}

	private void _validateLayoutConversion(Layout originalLayout)
		throws Exception {

		Layout persistedDraftLayout = originalLayout.fetchDraftLayout();

		Assert.assertNotNull(persistedDraftLayout);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					originalLayout.getGroupId(), originalLayout.getPlid());

		Assert.assertNotNull(layoutPageTemplateStructure);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		Assert.assertNotNull(layoutStructure.getMainItemId());

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		Assert.assertNotNull(rootLayoutStructureItem);
		Assert.assertTrue(
			rootLayoutStructureItem instanceof RootLayoutStructureItem);
		Assert.assertEquals(
			rootLayoutStructureItem.getItemType(),
			LayoutDataItemTypeConstants.TYPE_ROOT);

		List<String> rootItemIds = rootLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(rootItemIds.toString(), 1, rootItemIds.size());

		LayoutStructureItem containerLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(rootItemIds.get(0));

		Assert.assertNotNull(containerLayoutStructureItem);
		Assert.assertTrue(
			containerLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem);
		Assert.assertEquals(
			containerLayoutStructureItem.getItemType(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER);

		List<String> containerItemIds =
			containerLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			containerItemIds.toString(), 1, containerItemIds.size());

		LayoutStructureItem rowLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(containerItemIds.get(0));

		Assert.assertNotNull(rowLayoutStructureItem);
		Assert.assertTrue(
			rowLayoutStructureItem instanceof RowStyledLayoutStructureItem);
		Assert.assertEquals(
			rowLayoutStructureItem.getItemType(),
			LayoutDataItemTypeConstants.TYPE_ROW);

		List<String> rowItemIds = rowLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(rowItemIds.toString(), 1, rowItemIds.size());

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(rowItemIds.get(0));

		Assert.assertNotNull(columnLayoutStructureItem);
		Assert.assertTrue(
			columnLayoutStructureItem instanceof ColumnLayoutStructureItem);
		Assert.assertEquals(
			columnLayoutStructureItem.getItemType(),
			LayoutDataItemTypeConstants.TYPE_COLUMN);

		List<String> columnItemIds =
			columnLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(columnItemIds.toString(), 0, columnItemIds.size());

		Layout persistedPublishedLayout = _layoutLocalService.getLayout(
			originalLayout.getPlid());

		Assert.assertNotNull(persistedPublishedLayout);

		Assert.assertEquals(
			originalLayout.getPlid(), persistedPublishedLayout.getPlid());
		Assert.assertEquals(
			originalLayout.getGroupId(), persistedPublishedLayout.getGroupId());
		Assert.assertEquals(
			originalLayout.getUserId(), persistedPublishedLayout.getUserId());
		Assert.assertEquals(
			originalLayout.getParentPlid(),
			persistedPublishedLayout.getParentPlid());
		Assert.assertEquals(
			originalLayout.isPrivateLayout(),
			persistedPublishedLayout.isPrivateLayout());
		Assert.assertEquals(
			originalLayout.getLayoutId(),
			persistedPublishedLayout.getLayoutId());
		Assert.assertEquals(
			originalLayout.getParentLayoutId(),
			persistedPublishedLayout.getParentLayoutId());
		Assert.assertEquals(
			originalLayout.getClassName(),
			persistedPublishedLayout.getClassName());
		Assert.assertEquals(
			originalLayout.getClassNameId(),
			persistedPublishedLayout.getClassNameId());
		Assert.assertEquals(
			originalLayout.getClassPK(), persistedPublishedLayout.getClassPK());
		Assert.assertEquals(
			originalLayout.getNameMap(), persistedPublishedLayout.getNameMap());
		Assert.assertEquals(
			originalLayout.getTitleMap(),
			persistedPublishedLayout.getTitleMap());
		Assert.assertEquals(
			originalLayout.getDescriptionMap(),
			persistedPublishedLayout.getDescriptionMap());
		Assert.assertEquals(
			originalLayout.getKeywordsMap(),
			persistedPublishedLayout.getKeywordsMap());
		Assert.assertEquals(
			originalLayout.getRobotsMap(),
			persistedPublishedLayout.getRobotsMap());
		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, persistedPublishedLayout.getType());

		_assertTypeSettingsProperties(originalLayout, persistedDraftLayout);

		Assert.assertEquals(
			originalLayout.isSystem(), persistedPublishedLayout.isSystem());
		Assert.assertEquals(
			originalLayout.getFriendlyURLMap(),
			persistedPublishedLayout.getFriendlyURLMap());
	}

	private BundleContext _bundleContext;
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/convert_layout")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();
	private String _testPortletName;

	private class TestPortlet extends GenericPortlet {

		@Override
		protected void doView(
				RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		}

	}

}