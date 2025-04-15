/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.admin.web.internal.display.context.builder.IndexActionsDisplayContextBuilder;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.cluster.StatsInformation;
import com.liferay.portal.search.cluster.StatsInformationFactory;
import com.liferay.portal.search.configuration.ReindexConfiguration;
import com.liferay.portal.search.index.IndexInformation;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Felipe Lorenz
 */
public class IndexActionsDisplayContextTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalInstancePool.enableCache();

		_setUpHttpServletRequest();
		_setUpIndexInformation();
		_setUpLanguage();
		_setUpPortalUtil();
		_setUpThemeDisplay();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			IndexerRegistry.class, Mockito.mock(IndexerRegistry.class), null);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetStatsInformation() {
		IndexActionsDisplayContextBuilder indexActionsDisplayContextBuilder =
			new IndexActionsDisplayContextBuilder(
				_language, _portal, _reindexConfiguration,
				new MockRenderRequest(), _searchCapabilities);

		indexActionsDisplayContextBuilder.setStatsInformationFactory(
			getStatsInformationFactory(16.0, 10.0, 20.0));

		IndexActionsDisplayContext indexActionsDisplayContext =
			indexActionsDisplayContextBuilder.build();

		Map<String, Object> data = indexActionsDisplayContext.getData();

		Map<String, Object> searchEngineDiskSpace =
			(Map<String, Object>)data.get("searchEngineDiskSpace");

		Assert.assertEquals(
			16.0, (double)searchEngineDiskSpace.get("availableDiskSpace"), 0);
		Assert.assertEquals(
			20.0, (double)searchEngineDiskSpace.get("usedDiskSpace"), 0);
		Assert.assertFalse(
			(boolean)searchEngineDiskSpace.get("isLowOnDiskSpace"));

		indexActionsDisplayContextBuilder.setStatsInformationFactory(
			getStatsInformationFactory(14.0, 10.0, 20.0));

		indexActionsDisplayContext = indexActionsDisplayContextBuilder.build();

		data = indexActionsDisplayContext.getData();

		searchEngineDiskSpace = (Map<String, Object>)data.get(
			"searchEngineDiskSpace");

		Assert.assertTrue(
			(boolean)searchEngineDiskSpace.get("isLowOnDiskSpace"));
	}

	protected StatsInformationFactory getStatsInformationFactory(
		double available, double largest, double used) {

		StatsInformationFactory statsInformationFactory = Mockito.mock(
			StatsInformationFactory.class);

		StatsInformation statsInformation = Mockito.mock(
			StatsInformation.class);

		Mockito.when(
			statsInformation.getAvailableDiskSpace()
		).thenReturn(
			available
		);

		Mockito.when(
			statsInformation.getSizeOfLargestIndex()
		).thenReturn(
			largest
		);

		Mockito.when(
			statsInformation.getUsedDiskSpace()
		).thenReturn(
			used
		);

		Mockito.when(
			statsInformationFactory.getStatsInformation()
		).thenReturn(
			statsInformation
		);

		return statsInformationFactory;
	}

	private void _setUpHttpServletRequest() {
		Mockito.doReturn(
			_themeDisplay
		).when(
			_httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);
	}

	private void _setUpIndexInformation() {
		Mockito.when(
			_indexInformation.getIndexNames()
		).thenReturn(
			new String[] {"index1", "index2"}
		);

		Mockito.when(
			_indexInformation.getCompanyIndexName(Mockito.anyLong())
		).thenAnswer(
			invocation -> "index" + invocation.getArguments()[0]
		);
	}

	private void _setUpLanguage() {
		Mockito.doReturn(
			"name"
		).when(
			_language
		).get(
			Mockito.any(HttpServletRequest.class), Mockito.anyString()
		);
	}

	private void _setUpPortalUtil() {
		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			_portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doReturn(
			_httpServletRequest
		).when(
			_portal
		).getHttpServletRequest(
			Mockito.any()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private void _setUpThemeDisplay() {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.doReturn(
			permissionChecker
		).when(
			_themeDisplay
		).getPermissionChecker();

		Mockito.doReturn(
			true
		).when(
			permissionChecker
		).isOmniadmin();
	}

	private static final ThemeDisplay _themeDisplay = Mockito.mock(
		ThemeDisplay.class);

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final IndexInformation _indexInformation = Mockito.mock(
		IndexInformation.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ReindexConfiguration _reindexConfiguration = Mockito.mock(
		ReindexConfiguration.class);
	private final SearchCapabilities _searchCapabilities = Mockito.mock(
		SearchCapabilities.class);
	private ServiceRegistration<IndexerRegistry> _serviceRegistration;

}