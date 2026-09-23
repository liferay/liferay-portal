/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.model;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMConnectorChannelFieldDisplayTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);
	}

	@Test
	public void testGetStatus() {
		PIMConnectorChannelFieldDisplay pimConnectorChannelFieldDisplay =
			new PIMConnectorChannelFieldDisplay(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US, false, Collections.emptyList());

		PIMConnectorChannelFieldDisplay.Status status =
			pimConnectorChannelFieldDisplay.getStatus();

		Assert.assertEquals("secondary", status.getDisplayStyle());
		Assert.assertEquals("not-mapped", status.getLabel());

		pimConnectorChannelFieldDisplay = new PIMConnectorChannelFieldDisplay(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US, true, Collections.emptyList());

		status = pimConnectorChannelFieldDisplay.getStatus();

		Assert.assertEquals("danger", status.getDisplayStyle());
		Assert.assertEquals("required-not-mapped", status.getLabel());

		pimConnectorChannelFieldDisplay = new PIMConnectorChannelFieldDisplay(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US, false, Collections.singletonList("Name"));

		status = pimConnectorChannelFieldDisplay.getStatus();

		Assert.assertEquals("success", status.getDisplayStyle());
		Assert.assertEquals("mapped", status.getLabel());

		pimConnectorChannelFieldDisplay = new PIMConnectorChannelFieldDisplay(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US, true, Collections.singletonList("Name"));

		status = pimConnectorChannelFieldDisplay.getStatus();

		Assert.assertEquals("success", status.getDisplayStyle());
		Assert.assertEquals("mapped", status.getLabel());
	}

	@Test
	public void testIsMapped() {
		PIMConnectorChannelFieldDisplay pimConnectorChannelFieldDisplay =
			new PIMConnectorChannelFieldDisplay(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				LocaleUtil.US, false, Collections.emptyList());

		Assert.assertFalse(pimConnectorChannelFieldDisplay.isMapped());

		pimConnectorChannelFieldDisplay = new PIMConnectorChannelFieldDisplay(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US, false, Collections.singletonList("Name"));

		Assert.assertTrue(pimConnectorChannelFieldDisplay.isMapped());
	}

}