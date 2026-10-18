/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.servlet.jsp.PageContext;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Thiago Buarque
 */
public class InputAssetLinksDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpJSONFactoryUtil();
		_setUpPortalUtil();
		_setUpSiteConnectedGroupGroupProviderUtil();
		_setUpStagingGroupHelperUtil();

		_assetRendererFactory1 = _addAssetRendererFactory(
			_CLASS_NAME_1, _PORTLET_ID_1);
		_assetRendererFactory2 = _addAssetRendererFactory(
			_CLASS_NAME_2, _PORTLET_ID_2);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() -> AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				Mockito.anyLong())
		).thenReturn(
			Arrays.asList(_assetRendererFactory1, _assetRendererFactory2)
		);
	}

	@After
	public void tearDown() {
		_assetRendererFactoryRegistryUtilMockedStatic.close();
		_frameworkUtilMockedStatic.close();
		_siteConnectedGroupGroupProviderUtilMockedStatic.close();
		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Test
	public void testGetAssetEntryTypesJSONArray() throws Exception {
		long liveGroupId = RandomTestUtil.randomLong();

		Mockito.when(
			_stagingGroupHelper.getStagedPortletGroupId(
				_SCOPE_GROUP_ID, _PORTLET_ID_2)
		).thenReturn(
			liveGroupId
		);

		long scopeDepotGroupId = RandomTestUtil.randomLong();

		_setUpCurrentAndAncestorSiteAndDepotGroupIds(
			_SCOPE_GROUP_ID, _SCOPE_GROUP_ID, scopeDepotGroupId);

		long liveDepotGroupId = RandomTestUtil.randomLong();

		_setUpCurrentAndAncestorSiteAndDepotGroupIds(
			liveGroupId, liveGroupId, liveDepotGroupId);

		_setStagedPortlet(_PORTLET_ID_1, true);
		_setStagedPortlet(_PORTLET_ID_2, false);

		JSONArray assetEntryTypesJSONArray = _getAssetEntryTypesJSONArray();

		Assert.assertEquals(
			assetEntryTypesJSONArray.toString(), 2,
			assetEntryTypesJSONArray.length());

		Assert.assertArrayEquals(
			new long[] {_SCOPE_GROUP_ID, scopeDepotGroupId},
			_getGroupIds(_CLASS_NAME_ID_1, assetEntryTypesJSONArray));
		Assert.assertArrayEquals(
			new long[] {liveGroupId, liveDepotGroupId},
			_getGroupIds(_CLASS_NAME_ID_2, assetEntryTypesJSONArray));

		_setStagedPortlet(_PORTLET_ID_1, false);
		_setStagedPortlet(_PORTLET_ID_2, true);

		assetEntryTypesJSONArray = _getAssetEntryTypesJSONArray();

		Assert.assertEquals(
			assetEntryTypesJSONArray.toString(), 1,
			assetEntryTypesJSONArray.length());

		Assert.assertArrayEquals(
			new long[] {_SCOPE_GROUP_ID, scopeDepotGroupId},
			_getGroupIds(_CLASS_NAME_ID_1, assetEntryTypesJSONArray));
	}

	private AssetRendererFactory<?> _addAssetRendererFactory(
		String className, String portletId) {

		AssetRendererFactory<?> assetRendererFactory = Mockito.mock(
			AssetRendererFactory.class);

		Mockito.when(
			assetRendererFactory.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			assetRendererFactory.getPortletId()
		).thenReturn(
			portletId
		);

		Mockito.when(
			assetRendererFactory.getTypeName(LocaleUtil.US)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetRendererFactory.isLinkable()
		).thenReturn(
			true
		);

		Mockito.when(
			assetRendererFactory.isSelectable()
		).thenReturn(
			true
		);

		return assetRendererFactory;
	}

	private JSONArray _getAssetEntryTypesJSONArray() throws Exception {
		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(_CLASS_NAME_1)
		).thenReturn(
			_assetRendererFactory1
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockHttpServletRequest.setAttribute(
			"liferay-asset:input-asset-links:className", _CLASS_NAME_1);

		PageContext pageContext = Mockito.mock(PageContext.class);

		Mockito.when(
			pageContext.getRequest()
		).thenReturn(
			mockHttpServletRequest
		);

		InputAssetLinksDisplayContext inputAssetLinksDisplayContext =
			new InputAssetLinksDisplayContext(pageContext);

		return inputAssetLinksDisplayContext.getAssetEntryTypesJSONArray();
	}

	private long[] _getGroupIds(long classNameId, JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject.getLong("classNameId") == classNameId) {
				return JSONUtil.toLongArray(
					jsonObject.getJSONArray("groupIds"));
			}
		}

		return null;
	}

	private void _setStagedPortlet(String portletId, boolean staged) {
		Mockito.when(
			_stagingGroupHelper.isStagedPortlet(_SCOPE_GROUP_ID, portletId)
		).thenReturn(
			staged
		);
	}

	private void _setUpCurrentAndAncestorSiteAndDepotGroupIds(
		long groupId, long... currentAndAncestorSiteAndDepotGroupIds) {

		_siteConnectedGroupGroupProviderUtilMockedStatic.when(
			() ->
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						AdditionalMatchers.aryEq(new long[] {groupId}))
		).thenReturn(
			currentAndAncestorSiteAndDepotGroupIds
		);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpPortalUtil() {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getClassNameId(_CLASS_NAME_1)
		).thenReturn(
			_CLASS_NAME_ID_1
		);

		Mockito.when(
			portal.getClassNameId(_CLASS_NAME_2)
		).thenReturn(
			_CLASS_NAME_ID_2
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	private void _setUpSiteConnectedGroupGroupProviderUtil() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(
				SiteConnectedGroupGroupProviderUtil.class)
		).thenReturn(
			bundleContext.getBundle()
		);

		_siteConnectedGroupGroupProviderUtilMockedStatic = Mockito.mockStatic(
			SiteConnectedGroupGroupProviderUtil.class);

		_siteConnectedGroupGroupProviderUtilMockedStatic.when(
			() ->
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						Mockito.any(long[].class))
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0)
		);
	}

	private void _setUpStagingGroupHelperUtil() {
		_stagingGroupHelper = Mockito.mock(StagingGroupHelper.class);

		Mockito.when(
			_stagingGroupHelper.getStagedPortletGroupId(
				Mockito.anyLong(), Mockito.any())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0)
		);

		_stagingGroupHelperUtilMockedStatic.when(
			StagingGroupHelperUtil::getStagingGroupHelper
		).thenReturn(
			_stagingGroupHelper
		);
	}

	private static final String _CLASS_NAME_1 = RandomTestUtil.randomString();

	private static final String _CLASS_NAME_2 = RandomTestUtil.randomString();

	private static final long _CLASS_NAME_ID_1 = RandomTestUtil.randomLong();

	private static final long _CLASS_NAME_ID_2 = RandomTestUtil.randomLong();

	private static final String _PORTLET_ID_1 = RandomTestUtil.randomString();

	private static final String _PORTLET_ID_2 = RandomTestUtil.randomString();

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private AssetRendererFactory<?> _assetRendererFactory1;
	private AssetRendererFactory<?> _assetRendererFactory2;
	private final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);
	private final MockedStatic<FrameworkUtil> _frameworkUtilMockedStatic =
		Mockito.mockStatic(FrameworkUtil.class);
	private MockedStatic<SiteConnectedGroupGroupProviderUtil>
		_siteConnectedGroupGroupProviderUtilMockedStatic;
	private StagingGroupHelper _stagingGroupHelper;
	private final MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);

}