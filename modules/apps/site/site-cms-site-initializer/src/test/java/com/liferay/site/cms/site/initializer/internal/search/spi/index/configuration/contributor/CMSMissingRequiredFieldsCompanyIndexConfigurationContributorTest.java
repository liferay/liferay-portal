/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.index.configuration.contributor;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.spi.index.configuration.contributor.helper.MappingsHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class CMSMissingRequiredFieldsCompanyIndexConfigurationContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContributeMappings() throws Exception {
		CMSMissingRequiredFieldsCompanyIndexConfigurationContributor
			cmsMissingRequiredFieldsCompanyIndexConfigurationContributor =
				new CMSMissingRequiredFieldsCompanyIndexConfigurationContributor();

		MappingsHelper mappingsHelper = Mockito.mock(MappingsHelper.class);

		cmsMissingRequiredFieldsCompanyIndexConfigurationContributor.
			contributeMappings(RandomTestUtil.randomLong(), mappingsHelper);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			mappingsHelper
		).putMappings(
			argumentCaptor.capture()
		);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			argumentCaptor.getValue());

		Assert.assertEquals(
			"integer",
			jsonObject.getJSONObject(
				"properties"
			).getJSONObject(
				"missingRequiredFieldsCount"
			).getString(
				"type"
			));
	}

}