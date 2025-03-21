/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_3_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import java.util.List;
import java.util.Map;

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
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureRelUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		_segmentsExperienceId1 = segmentsExperience1.getSegmentsExperienceId();

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		_segmentsExperienceId2 = segmentsExperience2.getSegmentsExperienceId();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUpgrade() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			_addFragmentStyledLayoutStructureItems(_layout);

		String defaultSegmentsExperienceValue = RandomTestUtil.randomString();
		String segmentsExperience1Value = RandomTestUtil.randomString();
		String segmentsExperience2Value = RandomTestUtil.randomString();

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						_SEGMENTS_EXPERIENCE_ID_PREFIX +
							_SEGMENTS_EXPERIENCE_ID_DEFAULT,
						JSONUtil.put(
							_languageId, defaultSegmentsExperienceValue)
					).put(
						_SEGMENTS_EXPERIENCE_ID_PREFIX + _segmentsExperienceId1,
						JSONUtil.put(_languageId, segmentsExperience1Value)
					).put(
						_SEGMENTS_EXPERIENCE_ID_PREFIX + _segmentsExperienceId2,
						JSONUtil.put(_languageId, segmentsExperience2Value)
					).put(
						"defaultValue", RandomTestUtil.randomString()
					))
			).toString());

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			fragmentEntryLink);

		_assertGetFragmentEntryLinksBySegmentsExperienceId(_layout.getPlid());

		_runUpgrade();

		_assertGetFragmentEntryLinksByPlid(3, _layout.getPlid());

		_assertFragmentEntryLink(
			defaultSegmentsExperienceValue, _layout.getPlid(),
			_SEGMENTS_EXPERIENCE_ID_DEFAULT);
		_assertFragmentEntryLink(
			segmentsExperience1Value, _layout.getPlid(),
			_segmentsExperienceId1);
		_assertFragmentEntryLink(
			segmentsExperience2Value, _layout.getPlid(),
			_segmentsExperienceId2);
	}

	@Test
	public void testUpgradeWithFragmentEntryLinkTypePortlet() throws Exception {
		_assertUpgradeWithFragmentEntryLinkTypePortlet(_layout);
	}

	@Test
	public void testUpgradeWithFragmentEntryLinkTypePortletAndDraftLayout()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.configuration.module.configuration." +
					"internal.model.listener.PortletPreferencesModelListener",
				LoggerTestUtil.ERROR)) {

			_assertUpgradeWithFragmentEntryLinkTypePortlet(
				_layout.fetchDraftLayout());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	private void _addFragmentStyledLayoutStructureItem(
			long fragmentEntryLinkId, long plid, long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(_group.getGroupId(), plid);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), 0,
			fragmentLayoutStructureItems.size());

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId, layoutStructure.getMainItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), plid, segmentsExperienceId,
				layoutStructure.toString());

		_assertFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId, plid, segmentsExperienceId);
	}

	private FragmentEntryLink _addFragmentStyledLayoutStructureItems(
			Layout layout)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), layout,
				fragmentEntry.getFragmentEntryKey(),
				_SEGMENTS_EXPERIENCE_ID_DEFAULT, fragmentEntry.getType());

		_assertFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_SEGMENTS_EXPERIENCE_ID_DEFAULT);

		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_segmentsExperienceId1);
		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_segmentsExperienceId2);

		return fragmentEntryLink;
	}

	private void _addPortletPreferenceValue(
			long plid, com.liferay.portal.kernel.model.Portlet portlet,
			String portletId, String name, String value)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId, portlet,
				null);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		jxPortletPreferences.setValue(name, value);

		_portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId,
			jxPortletPreferences);
	}

	private FragmentEntryLink _addTypePortletFragmentStyledLayoutStructureItems(
			Layout layout, String portletId)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, StringPool.BLANK, StringPool.BLANK, 0, StringPool.BLANK,
				StringPool.BLANK, layout, StringPool.BLANK,
				_SEGMENTS_EXPERIENCE_ID_DEFAULT,
				FragmentConstants.TYPE_PORTLET);

		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				"instanceId", fragmentEntryLink.getNamespace()
			).put(
				"portletId", portletId
			).toString());

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				fragmentEntryLink);

		_assertFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_SEGMENTS_EXPERIENCE_ID_DEFAULT);

		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_segmentsExperienceId1);
		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), layout.getPlid(),
			_segmentsExperienceId2);

		return fragmentEntryLink;
	}

	private void _assertFragmentEntryLink(
			String expectedValue, long plid, long segmentsExperienceId)
		throws Exception {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(), segmentsExperienceId, plid);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		Assert.assertTrue(
			Validator.isNotNull(fragmentEntryLink.getEditableValues()));

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableJSONObject = editableValuesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject elementTextJSONObject = editableJSONObject.getJSONObject(
			"element-text");

		Assert.assertTrue(
			Validator.isNotNull(
				elementTextJSONObject.getString("defaultValue")));

		Assert.assertEquals(
			expectedValue, elementTextJSONObject.getString(_languageId));
	}

	private void _assertFragmentEntryLink(
			String expected, String name, long plid, String portletId,
			long segmentsExperienceId)
		throws Exception {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(), segmentsExperienceId, plid);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		Assert.assertEquals(
			FragmentConstants.TYPE_PORTLET, fragmentEntryLink.getType());

		Assert.assertTrue(
			Validator.isNotNull(fragmentEntryLink.getEditableValues()));

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		Assert.assertEquals(
			portletId, editableValuesJSONObject.getString("portletId"));

		String instanceId = editableValuesJSONObject.getString("instanceId");

		Assert.assertEquals(fragmentEntryLink.getNamespace(), instanceId);

		_assertPortletPreferenceValue(
			expected, name, plid,
			StringBundler.concat(portletId, _INSTANCE_SEPARATOR, instanceId));
	}

	private void _assertFragmentStyledLayoutStructureItem(
		long fragmentEntryLinkId, long plid, long segmentsExperienceId) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(_group.getGroupId(), plid);

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			MapUtil.toString(fragmentLayoutStructureItems), 1,
			fragmentLayoutStructureItems.size());

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)fragmentLayoutStructureItems.get(
				fragmentEntryLinkId);

		Assert.assertEquals(
			fragmentEntryLinkId,
			fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());
	}

	private void _assertGetFragmentEntryLinksByPlid(int count, long plid) {
		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), plid);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), count, fragmentEntryLinks.size());
	}

	private void _assertGetFragmentEntryLinksBySegmentsExperienceId(
		int count, long plid, long segmentsExperienceId) {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(), segmentsExperienceId, plid);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), count, fragmentEntryLinks.size());
	}

	private void _assertGetFragmentEntryLinksBySegmentsExperienceId(long plid) {
		_assertGetFragmentEntryLinksByPlid(1, plid);

		_assertGetFragmentEntryLinksBySegmentsExperienceId(
			1, plid, _SEGMENTS_EXPERIENCE_ID_DEFAULT);
		_assertGetFragmentEntryLinksBySegmentsExperienceId(
			0, plid, _segmentsExperienceId1);
		_assertGetFragmentEntryLinksBySegmentsExperienceId(
			0, plid, _segmentsExperienceId2);
	}

	private void _assertPortletPreferenceValue(
			String expected, String name, long plid, String portletId)
		throws Exception {

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				_portletPreferencesLocalService.getPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId));

		Assert.assertEquals(
			expected, jxPortletPreferences.getValue(name, null));
	}

	private void _assertUpgradeWithFragmentEntryLinkTypePortlet(Layout layout)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		String portletId = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"com.liferay.portlet.instanceable", "true"
				).put(
					"jakarta.portlet.name", portletId
				).build());

		try {
			com.liferay.portal.kernel.model.Portlet portlet =
				_portletLocalService.fetchPortletById(
					TestPropsValues.getCompanyId(), portletId);

			FragmentEntryLink fragmentEntryLink =
				_addTypePortletFragmentStyledLayoutStructureItems(
					layout, portletId);

			String name = RandomTestUtil.randomString();

			String defaultSegmentsExperienceValue =
				RandomTestUtil.randomString();

			_addPortletPreferenceValue(
				layout.getPlid(), portlet,
				StringBundler.concat(
					portletId, _INSTANCE_SEPARATOR,
					fragmentEntryLink.getNamespace()),
				name, defaultSegmentsExperienceValue);

			String segmentsExperience1Value = RandomTestUtil.randomString();

			_addPortletPreferenceValue(
				layout.getPlid(), portlet,
				StringBundler.concat(
					portletId, _INSTANCE_SEPARATOR,
					fragmentEntryLink.getNamespace(),
					_SEGMENTS_EXPERIENCE_SEPARATOR_1, _segmentsExperienceId1),
				name, segmentsExperience1Value);

			String segmentsExperience2Value = RandomTestUtil.randomString();

			_addPortletPreferenceValue(
				layout.getPlid(), portlet,
				StringBundler.concat(
					portletId, _INSTANCE_SEPARATOR,
					fragmentEntryLink.getNamespace(),
					_SEGMENTS_EXPERIENCE_SEPARATOR_2, _segmentsExperienceId2),
				name, segmentsExperience2Value);

			_assertGetFragmentEntryLinksBySegmentsExperienceId(
				layout.getPlid());

			ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

			try {
				_runUpgrade();
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}

			_assertGetFragmentEntryLinksByPlid(3, layout.getPlid());

			_assertFragmentEntryLink(
				defaultSegmentsExperienceValue, name, layout.getPlid(),
				portletId, _SEGMENTS_EXPERIENCE_ID_DEFAULT);
			_assertFragmentEntryLink(
				segmentsExperience1Value, name, layout.getPlid(), portletId,
				_segmentsExperienceId1);
			_assertFragmentEntryLink(
				segmentsExperience2Value, name, layout.getPlid(), portletId,
				_segmentsExperienceId2);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(3, 3, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_multiVMPool.clear();
	}

	private static final String _INSTANCE_SEPARATOR = "_INSTANCE_";

	private static final long _SEGMENTS_EXPERIENCE_ID_DEFAULT = 0;

	private static final String _SEGMENTS_EXPERIENCE_ID_PREFIX =
		"segments-experience-id-";

	private static final String _SEGMENTS_EXPERIENCE_SEPARATOR_1 =
		"_SEGMENTS_EXPERIENCE_";

	private static final String _SEGMENTS_EXPERIENCE_SEPARATOR_2 =
		"SEGMENTSEXPERIENCE";

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private String _languageId;
	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	private long _segmentsExperienceId1;
	private long _segmentsExperienceId2;

	private class TestPortlet extends GenericPortlet {
	}

}