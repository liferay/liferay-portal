/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.index.configuration.contributor;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jürgen Kappler
 */
public class CMSOutboundLinksCompanyIndexMappingsContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContributeMappings() {
		CMSOutboundLinksCompanyIndexMappingsContributor
			cmsOutboundLinksCompanyIndexMappingsContributor =
				new CMSOutboundLinksCompanyIndexMappingsContributor();

		Assert.assertEquals(
			"{\"properties\": {\"outboundLinks\": {\"type\": \"keyword\"}}}",
			StringUtil.removeChars(
				cmsOutboundLinksCompanyIndexMappingsContributor.
					contributeMappings(),
				CharPool.NEW_LINE, CharPool.RETURN, CharPool.TAB));
	}

}