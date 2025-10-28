/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class PortletPreferencesUpgradeProcessTest {

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
	@TestInfo("LPD-45005")
	public void testUpgrade() throws Exception {
		Assert.assertNotNull(
			ReflectionUtil.getDeclaredField(
				_upgradeStepRegistrator.getClass(), "viewCountManager"));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		PortletPreferences layoutPortletPreferences = _getPortletPreferences(
			layout, _addPortletFragmentEntryLink(layout));

		Layout controlPanelLayout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutLocalService.updateType(
			controlPanelLayout.getPlid(), LayoutConstants.TYPE_CONTROL_PANEL);

		PortletPreferences controlPanelPortletPreferences =
			_clonePortletPreferences(
				controlPanelLayout.getPlid(), layoutPortletPreferences);

		Assert.assertEquals(
			controlPanelLayout.getPlid(),
			controlPanelPortletPreferences.getPlid());

		_assertUpgrade();

		Assert.assertNull(
			_layoutLocalService.fetchLayout(controlPanelLayout.getPlid()));

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				layoutPortletPreferences.getPortletPreferencesId()));

		controlPanelPortletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				controlPanelPortletPreferences.getPortletPreferencesId());

		Assert.assertEquals(
			layout.getPlid(), controlPanelPortletPreferences.getPlid());
	}

	@Test
	@TestInfo("LPD-34944")
	public void testUpgradeFragmentEntryLinkWithMultiplePortlets()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_addFragmentEntryLink(layout);

		Layout groupControlPanelLayout = LayoutTestUtil.addTypeContentLayout(
			_group);

		_layoutLocalService.updateType(
			groupControlPanelLayout.getPlid(),
			LayoutConstants.TYPE_CONTROL_PANEL);

		List<PortletPreferences> portletPreferencesList = new ArrayList<>(
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layout.getPlid()));

		portletPreferencesList.addAll(
			TransformUtil.transform(
				portletPreferencesList,
				portletPreferences -> _clonePortletPreferences(
					groupControlPanelLayout.getPlid(), portletPreferences)));

		_assertUpgrade();

		Assert.assertNull(
			_layoutLocalService.fetchLayout(groupControlPanelLayout.getPlid()));

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			PortletPreferences curPortletPreferences =
				_portletPreferencesLocalService.fetchPortletPreferences(
					portletPreferences.getPortletPreferencesId());

			if (portletPreferences.getPlid() == layout.getPlid()) {
				Assert.assertNull(curPortletPreferences);

				continue;
			}

			Assert.assertEquals(
				groupControlPanelLayout.getPlid(),
				portletPreferences.getPlid());
			Assert.assertEquals(
				layout.getPlid(), curPortletPreferences.getPlid());
		}
	}

	@Test
	@TestInfo("LPD-34944")
	public void testUpgradeFragmentEntryLinkWithMultiplePortletsAndCompanyControlPanelLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_addFragmentEntryLink(layout);

		Layout groupControlPanelLayout = LayoutTestUtil.addTypeContentLayout(
			_group);

		_layoutLocalService.updateType(
			groupControlPanelLayout.getPlid(),
			LayoutConstants.TYPE_CONTROL_PANEL);

		List<PortletPreferences> layoutPortletPreferencesList = new ArrayList<>(
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layout.getPlid()));

		List<PortletPreferences> portletPreferencesList = new ArrayList<>(
			layoutPortletPreferencesList);

		portletPreferencesList.addAll(
			TransformUtil.transform(
				layoutPortletPreferencesList,
				portletPreferences -> _clonePortletPreferences(
					groupControlPanelLayout.getPlid(), portletPreferences)));

		Layout companyControlPanelLayout = _getCompanyControlPanelLayout();

		portletPreferencesList.addAll(
			TransformUtil.transform(
				layoutPortletPreferencesList,
				portletPreferences -> _clonePortletPreferences(
					companyControlPanelLayout.getPlid(), portletPreferences)));

		_assertUpgrade();

		Assert.assertNull(
			_layoutLocalService.fetchLayout(groupControlPanelLayout.getPlid()));

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			PortletPreferences curPortletPreferences =
				_portletPreferencesLocalService.fetchPortletPreferences(
					portletPreferences.getPortletPreferencesId());

			if ((portletPreferences.getPlid() == layout.getPlid()) ||
				(portletPreferences.getPlid() ==
					companyControlPanelLayout.getPlid())) {

				Assert.assertNull(curPortletPreferences);

				continue;
			}

			Assert.assertEquals(
				groupControlPanelLayout.getPlid(),
				portletPreferences.getPlid());
			Assert.assertEquals(
				layout.getPlid(), curPortletPreferences.getPlid());
		}
	}

	@Test
	public void testUpgradeWithCompanyControlPanelLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		PortletPreferences layoutPortletPreferences = _getPortletPreferences(
			layout, _addPortletFragmentEntryLink(layout));

		Layout groupControlPanelLayout = LayoutTestUtil.addTypeContentLayout(
			_group);

		_layoutLocalService.updateType(
			groupControlPanelLayout.getPlid(),
			LayoutConstants.TYPE_CONTROL_PANEL);

		PortletPreferences groupControlPanelPortletPreferences =
			_clonePortletPreferences(
				groupControlPanelLayout.getPlid(), layoutPortletPreferences);

		Assert.assertEquals(
			groupControlPanelLayout.getPlid(),
			groupControlPanelPortletPreferences.getPlid());

		Layout companyControlPanelLayout = _getCompanyControlPanelLayout();

		PortletPreferences companyControlPanelPortletPreferences =
			_clonePortletPreferences(
				companyControlPanelLayout.getPlid(), layoutPortletPreferences);

		Assert.assertEquals(
			companyControlPanelLayout.getPlid(),
			companyControlPanelPortletPreferences.getPlid());

		_assertUpgrade();

		Assert.assertNull(
			_layoutLocalService.fetchLayout(groupControlPanelLayout.getPlid()));

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				layoutPortletPreferences.getPortletPreferencesId()));

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				companyControlPanelPortletPreferences.
					getPortletPreferencesId()));

		groupControlPanelPortletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				groupControlPanelPortletPreferences.getPortletPreferencesId());

		Assert.assertEquals(
			layout.getPlid(), groupControlPanelPortletPreferences.getPlid());
	}

	private void _addFragmentEntryLink(Layout layout) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(
				serviceContext.getScopeGroupId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				"<div><lfr-widget-breadcrumb></lfr-widget-breadcrumb>" +
					RandomTestUtil.randomString() +
						"<lfr-widget-nav></lfr-widget-nav></div>",
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), layout.getGroupId(), null,
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getScopeERC(), segmentsExperienceId,
				layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), StringPool.BLANK,
				StringPool.BLANK, 0, StringPool.BLANK, fragmentEntry.getType(),
				serviceContext),
			layout, null, 0, segmentsExperienceId);

		ContentLayoutTestUtil.getRenderLayoutHTML(
			_layoutLocalService.getLayout(layout.getPlid()),
			_layoutServiceContextHelper, _layoutStructureProvider,
			segmentsExperienceId);
	}

	private String _addPortletFragmentEntryLink(Layout layout)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private void _assertUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.fragment.internal.upgrade.v1_1_0." +
					"PortletPreferencesUpgradeProcess",
				LoggerTestUtil.ALL)) {

			_runUpgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
	}

	private PortletPreferences _clonePortletPreferences(
		long plid, PortletPreferences portletPreferences) {

		int initialCount = _getPortletPreferencesCount(
			plid, portletPreferences.getPortletId());

		PortletPreferences clonedPortletPreferences =
			(PortletPreferences)portletPreferences.clone();

		clonedPortletPreferences.setPortletPreferencesId(
			RandomTestUtil.nextLong());
		clonedPortletPreferences.setPlid(plid);
		clonedPortletPreferences.setNew(true);

		clonedPortletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				clonedPortletPreferences);

		Assert.assertEquals(
			initialCount + 1,
			_getPortletPreferencesCount(
				plid, portletPreferences.getPortletId()));

		return clonedPortletPreferences;
	}

	private Layout _getCompanyControlPanelLayout() throws Exception {
		Group controlPanelGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);

		List<Layout> layouts = _layoutLocalService.getLayouts(
			controlPanelGroup.getGroupId(), true,
			LayoutConstants.TYPE_CONTROL_PANEL);

		return layouts.get(0);
	}

	private PortletPreferences _getPortletPreferences(
		Layout layout, String portletId) {

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				layout.getPlid(), portletId);

		Assert.assertEquals(
			portletPreferences.toString(), 1, portletPreferences.size());

		return portletPreferences.get(0);
	}

	private int _getPortletPreferencesCount(long plid, String portletId) {
		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				plid, portletId);

		return portletPreferences.size();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 1, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}