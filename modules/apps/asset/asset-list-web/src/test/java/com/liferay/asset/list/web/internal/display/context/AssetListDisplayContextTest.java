/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class AssetListDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpPortalGetHttpServletRequest();
		_setUpPortalUtil();
		_setUpPortletPreferencesFactoryUtil();
	}

	@Test
	public void testGetClassName() throws Exception {
		_setUpAssetRendererFactoryClassProvider();

		AssetListDisplayContext assetListDisplayContext =
			new AssetListDisplayContext(
				_assetRendererFactoryClassProvider,
				Mockito.mock(RenderRequest.class),
				Mockito.mock(RenderResponse.class));

		Class<? extends AssetRendererFactory> clazz =
			_assetRendererFactory.getClass();

		Assert.assertEquals(
			clazz.getSimpleName(),
			assetListDisplayContext.getClassName(_assetRendererFactory));
	}

	private void _setUpAssetRendererFactoryClassProvider() {
		Mockito.doReturn(
			_assetRendererFactory.getClass()
		).when(
			_assetRendererFactoryClassProvider
		).getClass(
			_assetRendererFactory
		);
	}

	private HttpServletRequest _setUpPortalGetHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			httpServletRequest
		).when(
			_portal
		).getHttpServletRequest(
			Mockito.any(PortletRequest.class)
		);

		return httpServletRequest;
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private void _setUpPortletPreferencesFactoryUtil() {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			Mockito.mock(PortletPreferencesFactory.class));
	}

	private final AssetRendererFactory<?> _assetRendererFactory = Mockito.mock(
		AssetRendererFactory.class);
	private final AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider = Mockito.mock(
			AssetRendererFactoryClassProvider.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}