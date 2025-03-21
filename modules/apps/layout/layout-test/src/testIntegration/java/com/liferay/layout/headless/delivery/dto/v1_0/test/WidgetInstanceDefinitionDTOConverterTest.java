/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.headless.delivery.dto.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.WidgetInstance;
import com.liferay.headless.delivery.dto.v1_0.WidgetPermission;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class WidgetInstanceDefinitionDTOConverterTest {

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

		String symbolicName = "com.liferay.headless.delivery.impl";

		_bundle = BundleUtil.getBundle(bundle.getBundleContext(), symbolicName);

		Assert.assertNotNull(
			"Unable to find bundle with symbolic name: " + symbolicName,
			_bundle);

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
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
	public void testToWidgetInstanceDefinition() throws Exception {
		_registerTestPortlet(_testPortletName);

		String namespace = StringUtil.randomId();

		String instanceId = StringUtil.randomId();

		JSONObject editableValueJSONObject =
			_fragmentEntryProcessorRegistry.getDefaultEditableValuesJSONObject(
				StringPool.BLANK, StringPool.BLANK);

		editableValueJSONObject.put(
			"instanceId", instanceId
		).put(
			"portletId", _testPortletName
		);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, _serviceContext.getUserId(),
				_serviceContext.getScopeGroupId(), 0, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				editableValueJSONObject.toString(), namespace, 0, null,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		String testPortletId = PortletIdCodec.encode(
			_testPortletName, instanceId);

		String configProperty1Value = RandomTestUtil.randomString();
		String configProperty2Value = RandomTestUtil.randomString();

		_portletPreferencesPortletConfigurationImporter.
			importPortletConfiguration(
				layout.getPlid(), testPortletId,
				HashMapBuilder.<String, Object>put(
					"config-property-1", configProperty1Value
				).put(
					"config-property-2", configProperty2Value
				).build());

		ResourceAction resourceAction =
			_resourceActionLocalService.addResourceAction(
				TestPortlet.class.getName(), "VIEW",
				RandomTestUtil.randomLong());

		Role role = _roleLocalService.getDefaultGroupRole(_group.getGroupId());

		Map<Long, String[]> roleIdsToActionIds = HashMapBuilder.put(
			role.getRoleId(),
			() -> {
				List<String> actionIds = new ArrayList<>();

				actionIds.add(resourceAction.getActionId());

				return actionIds.toArray(new String[0]);
			}
		).build();

		String resourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			layout.getPlid(), testPortletId);

		_resourcePermissionService.setIndividualResourcePermissions(
			layout.getGroupId(), layout.getCompanyId(), _testPortletName,
			resourcePrimKey, roleIdsToActionIds);

		WidgetInstance widgetInstance = ReflectionTestUtil.invoke(
			_getWidgetInstanceMapper(), "getWidgetInstance",
			new Class<?>[] {FragmentEntryLink.class, String.class},
			fragmentEntryLink, testPortletId);

		Assert.assertNotNull(widgetInstance);

		Map<String, Object> widgetConfig = widgetInstance.getWidgetConfig();

		Assert.assertEquals(
			configProperty1Value, widgetConfig.get("config-property-1"));
		Assert.assertEquals(
			configProperty2Value, widgetConfig.get("config-property-2"));

		Assert.assertEquals(instanceId, widgetInstance.getWidgetInstanceId());

		Assert.assertEquals(_testPortletName, widgetInstance.getWidgetName());

		WidgetPermission[] widgetPermissions =
			widgetInstance.getWidgetPermissions();

		Assert.assertEquals(
			Arrays.toString(widgetPermissions), 1, widgetPermissions.length);

		WidgetPermission widgetPermission = widgetPermissions[0];

		Assert.assertEquals(role.getName(), widgetPermission.getRoleKey());

		String[] actionKeys = widgetPermission.getActionKeys();

		Assert.assertEquals(Arrays.toString(actionKeys), 1, actionKeys.length);

		Assert.assertEquals("VIEW", actionKeys[0]);

		_layoutLocalService.deleteLayout(layout.getPlid());
		_resourceActionLocalService.deleteResourceAction(
			resourceAction.getResourceActionId());
	}

	private Object _getWidgetInstanceMapper() throws Exception {
		Class<?> clazz = _bundle.loadClass(
			"com.liferay.headless.delivery.internal.dto.v1_0.mapper." +
				"WidgetInstanceMapper");

		Constructor<?> constructor = clazz.getDeclaredConstructor(
			LayoutLocalService.class, Portal.class, PortletLocalService.class,
			PortletPreferencesPortletConfigurationExporter.class,
			ResourceActionLocalService.class,
			ResourcePermissionLocalService.class, RoleLocalService.class,
			TeamLocalService.class);

		return constructor.newInstance(
			_layoutLocalService, _portal, _portletLocalService,
			_portletPreferencesPortletConfigurationExporter,
			_resourceActionLocalService, _resourcePermissionLocalService,
			_roleLocalService, _teamLocalService);
	}

	private void _registerTestPortlet(String portletId) throws Exception {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"com.liferay.portlet.instanceable", "true"
				).put(
					"jakarta.portlet.name", portletId
				).build()));
	}

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

	@Inject
	private PortletPreferencesPortletConfigurationImporter
		_portletPreferencesPortletConfigurationImporter;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private ResourcePermissionService _resourcePermissionService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();

	@Inject
	private TeamLocalService _teamLocalService;

	private String _testPortletName;

	private class TestPortlet extends GenericPortlet {
	}

}