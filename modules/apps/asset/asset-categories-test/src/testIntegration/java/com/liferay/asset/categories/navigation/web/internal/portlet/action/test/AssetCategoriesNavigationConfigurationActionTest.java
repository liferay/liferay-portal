/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class AssetCategoriesNavigationConfigurationActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_parentGroup = GroupTestUtil.addGroup();

		_group = GroupTestUtil.addGroup(_parentGroup.getGroupId());
	}

	@Test
	public void testPostProcess() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetVocabulary companyAssetVocabulary1 = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());
		AssetVocabulary companyAssetVocabulary2 = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		AssetVocabulary parentAssetVocabulary = AssetTestUtil.addVocabulary(
			_parentGroup.getGroupId());
		AssetVocabulary scopeAssetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		portletPreferences.setValue(
			"assetVocabularyIds",
			StringUtil.merge(
				new Long[] {
					companyAssetVocabulary1.getVocabularyId(),
					scopeAssetVocabulary.getVocabularyId(),
					parentAssetVocabulary.getVocabularyId(),
					companyAssetVocabulary2.getVocabularyId()
				}));

		_postProcess(portletPreferences);

		Assert.assertArrayEquals(
			new String[] {
				companyGroup.getExternalReferenceCode(), StringPool.BLANK,
				_parentGroup.getExternalReferenceCode(),
				companyGroup.getExternalReferenceCode()
			},
			portletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {scopeAssetVocabulary.getExternalReferenceCode()},
			portletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {
				companyAssetVocabulary1.getExternalReferenceCode(),
				companyAssetVocabulary2.getExternalReferenceCode()
			},
			portletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				null));
		Assert.assertArrayEquals(
			new String[] {parentAssetVocabulary.getExternalReferenceCode()},
			portletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes_" +
					_parentGroup.getExternalReferenceCode(),
				null));
		Assert.assertNull(
			portletPreferences.getValue("assetVocabularyIds", null));
	}

	@Test
	public void testPostProcessWithoutAssetVocabularies() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		portletPreferences.setValue(
			"assetVocabularyIds",
			String.valueOf(assetVocabulary.getVocabularyId()));

		_postProcess(portletPreferences);

		Assert.assertArrayEquals(
			new String[] {assetVocabulary.getExternalReferenceCode()},
			portletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null));

		portletPreferences.setValue("assetVocabularyIds", StringPool.BLANK);

		_postProcess(portletPreferences);

		Assert.assertNull(
			portletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null));
		Assert.assertNull(
			portletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _postProcess(PortletPreferences portletPreferences)
		throws Exception {

		MockPortletRequest mockPortletRequest = new MockPortletRequest();

		mockPortletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		ReflectionTestUtil.invoke(
			_configurationAction, "postProcess",
			new Class<?>[] {
				long.class, PortletRequest.class, PortletPreferences.class
			},
			TestPropsValues.getCompanyId(), mockPortletRequest,
			portletPreferences);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "jakarta.portlet.name=" + AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION
	)
	private ConfigurationAction _configurationAction;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _parentGroup;

}