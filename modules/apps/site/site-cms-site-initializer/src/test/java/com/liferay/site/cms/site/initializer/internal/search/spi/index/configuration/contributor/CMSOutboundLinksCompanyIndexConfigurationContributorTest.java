/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.index.configuration.contributor;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.MappingsHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class CMSOutboundLinksCompanyIndexConfigurationContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContributeMappings() {
		CMSOutboundLinksCompanyIndexConfigurationContributor
			cmsOutboundLinksCompanyIndexConfigurationContributor =
				new CMSOutboundLinksCompanyIndexConfigurationContributor();

		MappingsHelper mappingsHelper = Mockito.mock(MappingsHelper.class);

		cmsOutboundLinksCompanyIndexConfigurationContributor.contributeMappings(
			RandomTestUtil.randomLong(), mappingsHelper);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			mappingsHelper
		).putMappings(
			argumentCaptor.capture()
		);

		Assert.assertEquals(
			"{\"properties\": {\"outboundLinks\": {\"type\": \"keyword\"}}}",
			StringUtil.removeChars(
				argumentCaptor.getValue(), CharPool.NEW_LINE, CharPool.RETURN,
				CharPool.TAB));
	}

}