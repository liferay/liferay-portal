/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.exportimport.test;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class AnnouncementsExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-98716")
	public void testExportImportScopeGroupExternalReferenceCodesFromStagedGroups()
		throws Exception {

		_setUpLocalStaging();

		Layout stagingLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		Group liveGroup1 = _addStagedGroup();
		Group liveGroup2 = _addStagedGroup();

		String portletId = _addAnnouncementsPortletWithScopeGroups(
			stagingLayout, liveGroup1.getStagingGroup(),
			liveGroup2.getStagingGroup());

		_publishLayouts(_stagingGroup);

		Layout liveLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			stagingLayout.getUuid(), _liveGroup.getGroupId(),
			stagingLayout.isPrivateLayout());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.getPreferences(
				_liveGroup.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, liveLayout.getPlid(),
				portletId);

		_assertScopeGroupExternalReferenceCodesTranslated(
			portletPreferences.getValue(
				"selectedScopeGroupExternalReferenceCodes", StringPool.BLANK),
			liveGroup1, liveGroup2);

		GroupTestUtil.deleteGroup(liveGroup1);
		GroupTestUtil.deleteGroup(liveGroup2);
	}

	@Test
	@TestInfo("LPD-98716")
	public void testGetPortletConfigurationWithLocalStaging() throws Exception {
		_setUpLocalStaging();

		Layout stagingLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		Group liveGroup1 = _addStagedGroup();
		Group liveGroup2 = _addStagedGroup();

		String portletId = _addAnnouncementsPortletWithScopeGroups(
			stagingLayout, liveGroup1.getStagingGroup(),
			liveGroup2.getStagingGroup());

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		Map<String, Object> portletConfiguration =
			_portletPreferencesPortletConfigurationExporter.
				getPortletConfiguration(stagingLayout.getPlid(), portletId);

		_assertScopeGroupExternalReferenceCodesTranslated(
			(String)portletConfiguration.get(
				"selectedScopeGroupExternalReferenceCodes"),
			liveGroup1, liveGroup2);

		ExportImportThreadLocal.setPortletStagingInProcess(false);

		GroupTestUtil.deleteGroup(liveGroup1);
		GroupTestUtil.deleteGroup(liveGroup2);
	}

	private String _addAnnouncementsPortletWithScopeGroups(
			Layout layout, Group... scopeGroups)
		throws Exception {

		return LayoutTestUtil.addPortletToLayout(
			layout, AnnouncementsPortletKeys.ANNOUNCEMENTS,
			HashMapBuilder.put(
				"selectedScopeGroupExternalReferenceCodes",
				new String[] {
					String.valueOf(
						JSONUtil.putAll(
							(Object[])TransformUtil.transform(
								scopeGroups, Group::getExternalReferenceCode,
								String.class)))
				}
			).build());
	}

	private Group _addStagedGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group, TestPropsValues.getUserId());

		return group;
	}

	private void _assertScopeGroupExternalReferenceCodesTranslated(
			String selectedScopeGroupExternalReferenceCodes,
			Group... expectedScopeGroups)
		throws Exception {

		List<String> externalReferenceCodes = JSONUtil.toStringList(
			JSONFactoryUtil.createJSONArray(
				selectedScopeGroupExternalReferenceCodes));

		Assert.assertEquals(
			externalReferenceCodes.toString(), expectedScopeGroups.length,
			externalReferenceCodes.size());

		for (Group expectedScopeGroup : expectedScopeGroups) {
			Assert.assertTrue(
				selectedScopeGroupExternalReferenceCodes,
				externalReferenceCodes.contains(
					expectedScopeGroup.getExternalReferenceCode()));
		}
	}

	private void _publishLayouts(Group stagingGroup) throws Exception {
		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, parameterMap);
	}

	private void _setUpLocalStaging() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(
			_liveGroup, TestPropsValues.getUserId());

		_stagingGroup = _liveGroup.getStagingGroup();
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

	private Group _stagingGroup;

}