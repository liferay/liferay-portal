/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.constants.FragmentTypeConstants;
import com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.BasicFragmentEntryVerticalCard;
import com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.FragmentEntryVerticalCardFactory;
import com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib.InheritedFragmentEntryVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class FragmentEntryVerticalCardFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpPortalUtil();
	}

	@Test
	public void testGetBasicFragmentTypeVerticalCard() {
		FragmentEntryVerticalCardFactory fragmentEntryVerticalCardFactory =
			FragmentEntryVerticalCardFactory.getInstance();

		VerticalCard verticalCard =
			fragmentEntryVerticalCardFactory.getVerticalCard(
				Mockito.mock(FragmentEntry.class),
				Mockito.mock(RenderRequest.class), null, null,
				FragmentTypeConstants.BASIC_FRAGMENT_TYPE);

		Assert.assertTrue(
			verticalCard instanceof BasicFragmentEntryVerticalCard);
	}

	@Test
	public void testGetInheritedFragmentTypeVerticalCard() {
		FragmentEntryVerticalCardFactory fragmentEntryVerticalCardFactory =
			FragmentEntryVerticalCardFactory.getInstance();

		VerticalCard verticalCard =
			fragmentEntryVerticalCardFactory.getVerticalCard(
				Mockito.mock(FragmentEntry.class),
				Mockito.mock(RenderRequest.class), null, null,
				FragmentTypeConstants.INHERITED_FRAGMENT_TYPE);

		Assert.assertTrue(
			verticalCard instanceof InheritedFragmentEntryVerticalCard);
	}

	@Test
	@TestInfo("LPS-122082")
	public void testIsSelectableFragmentEntryTypeReact() {
		FragmentEntryVerticalCardFactory fragmentEntryVerticalCardFactory =
			FragmentEntryVerticalCardFactory.getInstance();

		FragmentEntry fragmentEntry = Mockito.mock(FragmentEntry.class);

		Mockito.when(
			fragmentEntry.isTypeReact()
		).thenReturn(
			true
		);

		BasicFragmentEntryVerticalCard basicFragmentEntryVerticalCard =
			(BasicFragmentEntryVerticalCard)
				fragmentEntryVerticalCardFactory.getVerticalCard(
					fragmentEntry, Mockito.mock(RenderRequest.class), null,
					null, FragmentTypeConstants.BASIC_FRAGMENT_TYPE);

		Assert.assertFalse(basicFragmentEntryVerticalCard.isSelectable());
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private final Portal _portal = Mockito.mock(Portal.class);

}