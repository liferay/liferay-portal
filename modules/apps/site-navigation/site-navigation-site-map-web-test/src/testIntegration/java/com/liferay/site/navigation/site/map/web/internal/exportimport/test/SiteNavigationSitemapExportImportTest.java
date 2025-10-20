/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.site.map.web.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portlet.display.template.test.util.BaseExportImportTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class SiteNavigationSitemapExportImportTest
	extends BaseExportImportTestCase {

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			"com_liferay_site_navigation_site_map_web_portlet_" +
				"SiteNavigationSiteMapPortlet",
			RandomTestUtil.randomString());
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

}