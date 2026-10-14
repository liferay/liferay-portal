/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceFetcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFetch() throws Exception {
		_assertFetch("file://example.com/etc/passwd", "file");
		_assertFetch("ftp://example.com/a.png", "ftp");
		_assertFetch("intranet://example.com/a.png", "intranet");
	}

	private void _assertFetch(String url, String scheme) throws Exception {
		try {
			_staticSiteExportResourceFetcher.fetch(url);

			Assert.fail(url);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Scheme " + scheme + " is not supported",
				illegalArgumentException.getMessage());
		}
	}

	private final StaticSiteExportResourceFetcher
		_staticSiteExportResourceFetcher = new StaticSiteExportResourceFetcher(
			null, null, null, null, null);

}