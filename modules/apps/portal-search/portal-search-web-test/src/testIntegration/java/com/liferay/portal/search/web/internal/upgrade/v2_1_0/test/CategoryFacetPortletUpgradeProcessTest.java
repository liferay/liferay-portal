/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.upgrade.v2_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class CategoryFacetPortletUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String portletId =
			"com_liferay_portal_search_web_category_facet_portlet_" +
				"CategoryFacetPortlet";

		_assetVocabulary1 = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_assetVocabulary2 = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String defaultPreferences = StringBundler.concat(
			"<portlet-preferences><preference><name>vocabularyIds",
			"</name><value>",
			_assetVocabulary1.getVocabularyId() + "," +
				_assetVocabulary2.getVocabularyId(),
			"</value></preference></portlet-preferences>");

		_portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				_group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId,
				_portletLocalService.fetchPortletById(
					_group.getCompanyId(), portletId),
				defaultPreferences);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.portal.search.web.internal.upgrade.v2_1_0." +
				"CategoryFacetPortletUpgradeProcess");

		upgradeProcess.upgrade();

		_multiVMPool.clear();

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				_portletPreferencesLocalService.getPortletPreferences(
					_portletPreferences.getPortletPreferencesId()));

		Assert.assertEquals(
			StringBundler.concat(
				_group.getExternalReferenceCode(), "&&",
				_assetVocabulary1.getExternalReferenceCode(), ",",
				_group.getExternalReferenceCode(), "&&",
				_assetVocabulary2.getExternalReferenceCode()),
			GetterUtil.getString(
				jxPortletPreferences.getValue(
					"groupVocabularyExternalReferenceCodes", null)));
		Assert.assertNull(jxPortletPreferences.getValue("vocabularyIds", null));
	}

	@Inject
	private static PortletLocalService _portletLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary1;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary2;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@DeleteAfterTestRun
	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.search.web.internal.upgrade.registry.SearchWebUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}