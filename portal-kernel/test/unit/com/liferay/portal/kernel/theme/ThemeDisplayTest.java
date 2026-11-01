/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.theme;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistryUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class ThemeDisplayTest {

	@Before
	public void setUp() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			group.getParentGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Mockito.when(
			group.isSite()
		).thenReturn(
			true
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroup(_GROUP_ID)
		).thenReturn(
			group
		);

		_hashedFilesRegistryUtilMockedStatic.when(
			() -> HashedFilesRegistryUtil.getHashedFileURI(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> HashedFilesUtil.addHash(
				invocationOnMock.getArgument(0), _HASH)
		);

		ReflectionTestUtil.setFieldValue(_themeDisplay, "_theme", _mockTheme());
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
		_hashedFilesRegistryUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testGetClayCSSURL() {
		_mockPortalUtil(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithCDN() {
		_mockPortalUtil(
			"http://cdn.com", StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithCDNAndContext() {
		_mockPortalUtil("http://cdn.com", "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/dxp/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithCDNContextAndProxy() {
		_mockPortalUtil("http://cdn.com", "/dxp", "/proxy", false);

		Assert.assertEquals(
			"http://cdn.com/proxy/dxp/o/classic-theme/css/clay.(" + _HASH +
				").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithContext() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"/dxp/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithContextAndProxy() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", "/liferay", false);

		Assert.assertEquals(
			"/liferay/dxp/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetClayCSSURLWithProxy() {
		_mockPortalUtil(StringPool.BLANK, StringPool.BLANK, "/liferay", false);

		Assert.assertEquals(
			"/liferay/o/classic-theme/css/clay.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testGetLayoutGroup() throws Exception {
		_themeDisplay.setSiteGroupId(_GROUP_ID);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Assert.assertSame(
			_themeDisplay.getSiteGroup(), _themeDisplay.getLayoutGroup(layout));

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			layout.getGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Assert.assertSame(group, _themeDisplay.getLayoutGroup(layout));
	}

	@Test
	public void testGetMainCSSURL() {
		_mockPortalUtil(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithCDN() {
		_mockPortalUtil(
			"http://cdn.com", StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithCDNAndContext() {
		_mockPortalUtil("http://cdn.com", "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/dxp/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithCDNContextAndProxy() {
		_mockPortalUtil("http://cdn.com", "/dxp", "/proxy", false);

		Assert.assertEquals(
			"http://cdn.com/proxy/dxp/o/classic-theme/css/main.(" + _HASH +
				").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithContext() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"/dxp/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithContextAndProxy() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", "/liferay", false);

		Assert.assertEquals(
			"/liferay/dxp/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainCSSURLWithProxy() {
		_mockPortalUtil(StringPool.BLANK, StringPool.BLANK, "/liferay", false);

		Assert.assertEquals(
			"/liferay/o/classic-theme/css/main.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	@Test
	public void testGetMainJSURL() {
		_mockPortalUtil(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithCDN() {
		_mockPortalUtil(
			"http://cdn.com", StringPool.BLANK, StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithCDNAndContext() {
		_mockPortalUtil("http://cdn.com", "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"http://cdn.com/dxp/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithCDNContextAndProxy() {
		_mockPortalUtil("http://cdn.com", "/dxp", "/proxy", false);

		Assert.assertEquals(
			"http://cdn.com/proxy/dxp/o/classic-theme/js/main.(" + _HASH +
				").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithContext() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", StringPool.BLANK, false);

		Assert.assertEquals(
			"/dxp/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithContextAndProxy() {
		_mockPortalUtil(StringPool.BLANK, "/dxp", "/liferay", false);

		Assert.assertEquals(
			"/liferay/dxp/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetMainJSURLWithProxy() {
		_mockPortalUtil(StringPool.BLANK, StringPool.BLANK, "/liferay", false);

		Assert.assertEquals(
			"/liferay/o/classic-theme/js/main.(" + _HASH + ").js",
			_themeDisplay.getMainJSURL());
	}

	@Test
	public void testGetParentSiteGroupId() {
		_themeDisplay.setSiteGroupId(_GROUP_ID);

		Assert.assertEquals(
			_PARENT_GROUP_ID, _themeDisplay.getParentSiteGroupId());
	}

	@Test
	public void testRTLGetClayCSSURL() {
		_mockPortalUtil(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, true);

		Assert.assertEquals(
			"/o/classic-theme/css/clay_rtl.(" + _HASH + ").css",
			_themeDisplay.getClayCSSURL());
	}

	@Test
	public void testRTLGetMainCSSURL() {
		_mockPortalUtil(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, true);

		Assert.assertEquals(
			"/o/classic-theme/css/main_rtl.(" + _HASH + ").css",
			_themeDisplay.getMainCSSURL());
	}

	private void _mockPortalUtil(
		String cdnPath, String contextPath, String proxyPath, boolean rtl) {

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getCDNHost(Mockito.any())
		).thenReturn(
			cdnPath
		);

		_portalUtilMockedStatic.when(
			PortalUtil::getPathContext
		).thenReturn(
			proxyPath + contextPath
		);

		_portalUtilMockedStatic.when(
			PortalUtil::getPathModule
		).thenReturn(
			proxyPath + contextPath + "/o"
		);

		_portalUtilMockedStatic.when(
			PortalUtil::getPathProxy
		).thenReturn(
			proxyPath
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.isRightToLeft(Mockito.any())
		).thenReturn(
			rtl
		);
	}

	private Theme _mockTheme() {
		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getCssPath()
		).thenReturn(
			"/css"
		);

		Mockito.when(
			theme.getJavaScriptPath()
		).thenReturn(
			"/js"
		);

		Mockito.when(
			theme.getServletContextName()
		).thenReturn(
			"classic-theme"
		);

		return theme;
	}

	private static final long _GROUP_ID = 1;

	private static final String _HASH = RandomTestUtil.randomString(8);

	private static final long _PARENT_GROUP_ID = 2;

	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final MockedStatic<HashedFilesRegistryUtil>
		_hashedFilesRegistryUtilMockedStatic = Mockito.mockStatic(
			HashedFilesRegistryUtil.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}