/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.result.display.context.builder;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultContentDisplayContext;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.internal.MutableRenderParametersImpl;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SearchResultContentDisplayContextBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAssetEntry();
		setUpAssetRenderer();
		_setUpAssetRendererFactory();
		_setUpPortal();
		_setUpRenderResponse();
		_setUpThemeDisplay();
	}

	@Test
	public void testEditPermission() throws Exception {
		String title = RandomTestUtil.randomString();

		Mockito.doReturn(
			title
		).when(
			_assetRenderer
		).getTitle(
			Mockito.any()
		);

		String editPortletURLString = RandomTestUtil.randomString();

		Mockito.doReturn(
			editPortletURLString
		).when(
			_editPortletURL
		).toString();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			_buildDisplayContext();

		Assert.assertTrue(
			searchResultContentDisplayContext.hasEditPermission());

		_assertIcon(
			title, editPortletURLString, searchResultContentDisplayContext);
	}

	@Test
	public void testEditPermissionFalse() throws Exception {
		Mockito.doReturn(
			false
		).when(
			_assetRenderer
		).hasEditPermission(
			Mockito.any()
		);

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			_buildDisplayContext();

		Assert.assertFalse(
			searchResultContentDisplayContext.hasEditPermission());

		_assertIconMissing(searchResultContentDisplayContext);

		_assertAssetDisplay(searchResultContentDisplayContext);
	}

	@Test
	public void testVisible() throws Exception {
		SearchResultContentDisplayContext searchResultContentDisplayContext =
			_buildDisplayContext();

		Assert.assertTrue(searchResultContentDisplayContext.isVisible());

		_assertAssetDisplay(searchResultContentDisplayContext);
	}

	@Test
	public void testVisibleFalseFromEntry() throws Exception {
		Mockito.doReturn(
			false
		).when(
			_assetEntry
		).isVisible();

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			_buildDisplayContext();

		Assert.assertFalse(searchResultContentDisplayContext.isVisible());
	}

	@Test
	public void testVisibleFalseFromViewPermission() throws Exception {
		Mockito.doReturn(
			false
		).when(
			_assetRenderer
		).hasViewPermission(
			Mockito.any()
		);

		SearchResultContentDisplayContext searchResultContentDisplayContext =
			_buildDisplayContext();

		Assert.assertFalse(searchResultContentDisplayContext.isVisible());
	}

	protected void setUpAssetRenderer() throws Exception, PortalException {
		Mockito.doReturn(
			_editPortletURL
		).when(
			_assetRenderer
		).getURLEdit(
			Mockito.any(), Mockito.any()
		);

		Mockito.doReturn(
			true
		).when(
			_assetRenderer
		).hasEditPermission(
			Mockito.any()
		);

		Mockito.doReturn(
			true
		).when(
			_assetRenderer
		).hasViewPermission(
			Mockito.any()
		);
	}

	private void _assertAssetDisplay(
		SearchResultContentDisplayContext searchResultContentDisplayContext) {

		Assert.assertSame(
			_assetEntry, searchResultContentDisplayContext.getAssetEntry());

		Assert.assertSame(
			_assetRenderer,
			searchResultContentDisplayContext.getAssetRenderer());

		Assert.assertSame(
			_assetRendererFactory,
			searchResultContentDisplayContext.getAssetRendererFactory());
	}

	private void _assertIcon(
		String editTarget, String urlString,
		SearchResultContentDisplayContext searchResultContentDisplayContext) {

		Assert.assertEquals(
			editTarget, searchResultContentDisplayContext.getIconEditTarget());

		Assert.assertEquals(
			urlString, searchResultContentDisplayContext.getIconURLString());
	}

	private void _assertIconMissing(
		SearchResultContentDisplayContext searchResultContentDisplayContext) {

		Assert.assertNull(
			searchResultContentDisplayContext.getIconEditTarget());

		Assert.assertNull(searchResultContentDisplayContext.getIconURLString());
	}

	private SearchResultContentDisplayContext _buildDisplayContext()
		throws Exception {

		SearchResultContentDisplayContextBuilder
			searchResultContentDisplayContextBuilder = Mockito.spy(
				new SearchResultContentDisplayContextBuilder());

		Mockito.doReturn(
			_assetEntry
		).when(
			searchResultContentDisplayContextBuilder
		).getAssetEntry();

		searchResultContentDisplayContextBuilder.setAssetEntryId(
			RandomTestUtil.randomLong());
		searchResultContentDisplayContextBuilder.setAssetRendererFactoryLookup(
			_assetRendererFactoryLookup);
		searchResultContentDisplayContextBuilder.setLocale(LocaleUtil.US);
		searchResultContentDisplayContextBuilder.setPermissionChecker(
			_permissionChecker);
		searchResultContentDisplayContextBuilder.setPortal(_portal);
		searchResultContentDisplayContextBuilder.setRenderRequest(
			_renderRequest);
		searchResultContentDisplayContextBuilder.setRenderResponse(
			_renderResponse);
		searchResultContentDisplayContextBuilder.setType(
			RandomTestUtil.randomString());

		return searchResultContentDisplayContextBuilder.build();
	}

	private void _setUpAssetEntry() {
		Mockito.doReturn(
			_assetRenderer
		).when(
			_assetEntry
		).getAssetRenderer();

		Mockito.doReturn(
			true
		).when(
			_assetEntry
		).isVisible();
	}

	private void _setUpAssetRendererFactory() throws Exception {
		Mockito.doReturn(
			_assetEntry
		).when(
			_assetRendererFactory
		).getAssetEntry(
			Mockito.anyLong()
		);

		Mockito.doReturn(
			_assetRendererFactory
		).when(
			_assetRendererFactoryLookup
		).getAssetRendererFactoryByType(
			Mockito.anyString()
		);
	}

	private void _setUpPortal() {
		Mockito.doReturn(
			Mockito.mock(LiferayPortletRequest.class)
		).when(
			_portal
		).getLiferayPortletRequest(
			Mockito.any()
		);

		Mockito.doReturn(
			Mockito.mock(LiferayPortletResponse.class)
		).when(
			_portal
		).getLiferayPortletResponse(
			Mockito.any()
		);
	}

	private void _setUpRenderResponse() {
		Mockito.doReturn(
			_renderPortletURL
		).when(
			_renderResponse
		).createRenderURL();

		Mockito.doReturn(
			new MutableRenderParametersImpl(new HashMap<>(), new HashSet<>())
		).when(
			_renderPortletURL
		).getRenderParameters();
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setURLCurrent("http://example.com");

		Mockito.when(
			(ThemeDisplay)_renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	private final AssetEntry _assetEntry = Mockito.mock(AssetEntry.class);
	private final AssetRenderer<?> _assetRenderer = Mockito.mock(
		AssetRenderer.class);

	@SuppressWarnings("rawtypes")
	private AssetRendererFactory _assetRendererFactory = Mockito.mock(
		AssetRendererFactory.class);

	private final AssetRendererFactoryLookup _assetRendererFactoryLookup =
		Mockito.mock(AssetRendererFactoryLookup.class);
	private final PortletURL _editPortletURL = Mockito.mock(PortletURL.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final PortletURL _renderPortletURL = Mockito.mock(PortletURL.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}