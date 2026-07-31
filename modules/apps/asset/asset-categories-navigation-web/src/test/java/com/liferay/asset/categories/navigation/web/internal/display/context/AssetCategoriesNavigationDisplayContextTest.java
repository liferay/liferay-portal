/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.display.context;

import com.liferay.asset.categories.navigation.web.internal.configuration.AssetCategoriesNavigationPortletInstanceConfiguration;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Akhash Ramprakash
 */
public class AssetCategoriesNavigationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpConfigurationProviderUtil();
		_setUpThemeDisplay();

		_companyGroup = _mockGroup();
		_parentGroup = _mockGroup();
	}

	@After
	public void tearDown() {
		_assetVocabularyLocalServiceUtilMockedStatic.close();
		_assetVocabularyServiceUtilMockedStatic.close();
		_configurationProviderUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testGetAssetVocabularyIds() throws Exception {
		_testGetAssetVocabularyIdsWithLegacyPortletPreferences();
		_testGetAssetVocabularyIdsWithMisalignedPortletPreferences();
		_testGetAssetVocabularyIdsWithOrderedPortletPreferences();
		_testGetAssetVocabularyIdsWithUnresolvableAssetVocabulary();
	}

	private AssetCategoriesNavigationDisplayContext _createDisplayContext(
			PortletPreferences portletPreferences)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getPreferences()
		).thenReturn(
			portletPreferences
		);

		return new AssetCategoriesNavigationDisplayContext(
			mockHttpServletRequest, renderRequest);
	}

	private AssetVocabulary _mockAssetVocabulary(long groupId) {
		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		String externalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			assetVocabulary.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		long vocabularyId = RandomTestUtil.randomLong();

		Mockito.when(
			assetVocabulary.getVocabularyId()
		).thenReturn(
			vocabularyId
		);

		_assetVocabularyLocalServiceUtilMockedStatic.when(
			() ->
				AssetVocabularyLocalServiceUtil.
					fetchAssetVocabularyByExternalReferenceCode(
						externalReferenceCode, groupId)
		).thenReturn(
			assetVocabulary
		);

		_assetVocabularyServiceUtilMockedStatic.when(
			() -> AssetVocabularyServiceUtil.fetchVocabulary(vocabularyId)
		).thenReturn(
			assetVocabulary
		);

		return assetVocabulary;
	}

	private Group _mockGroup() {
		Group group = Mockito.mock(Group.class);

		String externalReferenceCode = RandomTestUtil.randomString();

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);

		return group;
	}

	private void _setUpConfigurationProviderUtil() {
		AssetCategoriesNavigationPortletInstanceConfiguration
			assetCategoriesNavigationPortletInstanceConfiguration =
				Mockito.mock(
					AssetCategoriesNavigationPortletInstanceConfiguration.
						class);

		Mockito.when(
			assetCategoriesNavigationPortletInstanceConfiguration.
				allAssetVocabularies()
		).thenReturn(
			false
		);

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.eq(
					AssetCategoriesNavigationPortletInstanceConfiguration.
						class),
				Mockito.any(ThemeDisplay.class))
		).thenReturn(
			assetCategoriesNavigationPortletInstanceConfiguration
		);
	}

	private void _setUpThemeDisplay() {
		_themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);
	}

	private void _testGetAssetVocabularyIdsWithLegacyPortletPreferences()
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		AssetVocabulary scopeAssetVocabulary = _mockAssetVocabulary(
			_SCOPE_GROUP_ID);

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			scopeAssetVocabulary.getExternalReferenceCode());

		AssetVocabulary companyAssetVocabulary1 = _mockAssetVocabulary(
			_companyGroup.getGroupId());
		AssetVocabulary companyAssetVocabulary2 = _mockAssetVocabulary(
			_companyGroup.getGroupId());

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				_companyGroup.getExternalReferenceCode(),
			companyAssetVocabulary1.getExternalReferenceCode(),
			companyAssetVocabulary2.getExternalReferenceCode());

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			_companyGroup.getExternalReferenceCode());

		AssetCategoriesNavigationDisplayContext
			assetCategoriesNavigationDisplayContext = _createDisplayContext(
				portletPreferences);

		Assert.assertArrayEquals(
			new long[] {
				companyAssetVocabulary1.getVocabularyId(),
				companyAssetVocabulary2.getVocabularyId(),
				scopeAssetVocabulary.getVocabularyId()
			},
			assetCategoriesNavigationDisplayContext.getAssetVocabularyIds());
	}

	private void _testGetAssetVocabularyIdsWithMisalignedPortletPreferences()
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		AssetVocabulary scopeAssetVocabulary = _mockAssetVocabulary(
			_SCOPE_GROUP_ID);

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			scopeAssetVocabulary.getExternalReferenceCode());

		AssetVocabulary companyAssetVocabulary = _mockAssetVocabulary(
			_companyGroup.getGroupId());

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				_companyGroup.getExternalReferenceCode(),
			companyAssetVocabulary.getExternalReferenceCode());

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			_companyGroup.getExternalReferenceCode(),
			_companyGroup.getExternalReferenceCode(), StringPool.BLANK);

		AssetCategoriesNavigationDisplayContext
			assetCategoriesNavigationDisplayContext = _createDisplayContext(
				portletPreferences);

		Assert.assertArrayEquals(
			new long[] {
				companyAssetVocabulary.getVocabularyId(),
				scopeAssetVocabulary.getVocabularyId()
			},
			assetCategoriesNavigationDisplayContext.getAssetVocabularyIds());
	}

	private void _testGetAssetVocabularyIdsWithOrderedPortletPreferences()
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		AssetVocabulary scopeAssetVocabulary = _mockAssetVocabulary(
			_SCOPE_GROUP_ID);

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			scopeAssetVocabulary.getExternalReferenceCode());

		AssetVocabulary companyAssetVocabulary1 = _mockAssetVocabulary(
			_companyGroup.getGroupId());
		AssetVocabulary companyAssetVocabulary2 = _mockAssetVocabulary(
			_companyGroup.getGroupId());

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				_companyGroup.getExternalReferenceCode(),
			companyAssetVocabulary1.getExternalReferenceCode(),
			companyAssetVocabulary2.getExternalReferenceCode());

		AssetVocabulary parentAssetVocabulary = _mockAssetVocabulary(
			_parentGroup.getGroupId());

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				_parentGroup.getExternalReferenceCode(),
			parentAssetVocabulary.getExternalReferenceCode());

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			_companyGroup.getExternalReferenceCode(), StringPool.BLANK,
			_parentGroup.getExternalReferenceCode(),
			_companyGroup.getExternalReferenceCode());

		AssetCategoriesNavigationDisplayContext
			assetCategoriesNavigationDisplayContext = _createDisplayContext(
				portletPreferences);

		Assert.assertArrayEquals(
			new long[] {
				companyAssetVocabulary1.getVocabularyId(),
				scopeAssetVocabulary.getVocabularyId(),
				parentAssetVocabulary.getVocabularyId(),
				companyAssetVocabulary2.getVocabularyId()
			},
			assetCategoriesNavigationDisplayContext.getAssetVocabularyIds());
	}

	private void _testGetAssetVocabularyIdsWithUnresolvableAssetVocabulary()
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		AssetVocabulary scopeAssetVocabulary = _mockAssetVocabulary(
			_SCOPE_GROUP_ID);

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			scopeAssetVocabulary.getExternalReferenceCode());

		AssetVocabulary companyAssetVocabulary = _mockAssetVocabulary(
			_companyGroup.getGroupId());

		portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				_companyGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString(),
			companyAssetVocabulary.getExternalReferenceCode());

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			_companyGroup.getExternalReferenceCode(), StringPool.BLANK,
			_companyGroup.getExternalReferenceCode());

		AssetCategoriesNavigationDisplayContext
			assetCategoriesNavigationDisplayContext = _createDisplayContext(
				portletPreferences);

		Assert.assertArrayEquals(
			new long[] {
				scopeAssetVocabulary.getVocabularyId(),
				companyAssetVocabulary.getVocabularyId()
			},
			assetCategoriesNavigationDisplayContext.getAssetVocabularyIds());
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private final MockedStatic<AssetVocabularyLocalServiceUtil>
		_assetVocabularyLocalServiceUtilMockedStatic = Mockito.mockStatic(
			AssetVocabularyLocalServiceUtil.class);
	private final MockedStatic<AssetVocabularyServiceUtil>
		_assetVocabularyServiceUtilMockedStatic = Mockito.mockStatic(
			AssetVocabularyServiceUtil.class);
	private Group _companyGroup;
	private final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private Group _parentGroup;
	private ThemeDisplay _themeDisplay;

}