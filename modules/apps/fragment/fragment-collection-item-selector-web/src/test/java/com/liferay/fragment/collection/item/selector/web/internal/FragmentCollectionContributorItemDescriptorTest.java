/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Mario Leandro
 */
public class FragmentCollectionContributorItemDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_fragmentCollectionContributor.getFragmentCollectionKey()
		).thenReturn(
			_FRAGMENT_COLLECTION_KEY
		);

		Mockito.when(
			_fragmentCollectionContributor.getName(LocaleUtil.SPAIN)
		).thenReturn(
			_NAME_SPANISH
		);

		Mockito.when(
			_fragmentCollectionContributor.getName(LocaleUtil.US)
		).thenReturn(
			_NAME_ENGLISH
		);
	}

	@Test
	public void testGetPayload() throws Exception {
		_testGetPayload(LocaleUtil.SPAIN, _NAME_SPANISH);
		_testGetPayload(LocaleUtil.US, _NAME_ENGLISH);
	}

	@Test
	public void testGetTitle() {
		FragmentCollectionContributorItemDescriptor
			fragmentCollectionContributorItemDescriptor =
				new FragmentCollectionContributorItemDescriptor(
					_fragmentCollectionContributor, LocaleUtil.US);

		Assert.assertEquals(
			_NAME_SPANISH,
			fragmentCollectionContributorItemDescriptor.getTitle(
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			_NAME_ENGLISH,
			fragmentCollectionContributorItemDescriptor.getTitle(
				LocaleUtil.US));
	}

	private void _testGetPayload(Locale locale, String name) throws Exception {
		FragmentCollectionContributorItemDescriptor
			fragmentCollectionContributorItemDescriptor =
				new FragmentCollectionContributorItemDescriptor(
					_fragmentCollectionContributor, locale);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"fragmentCollectionKey", _FRAGMENT_COLLECTION_KEY
			).put(
				"groupId", CompanyConstants.SYSTEM
			).put(
				"name", name
			).toString(),
			fragmentCollectionContributorItemDescriptor.getPayload(), true);
	}

	private static final String _FRAGMENT_COLLECTION_KEY =
		RandomTestUtil.randomString();

	private static final String _NAME_ENGLISH = "Account Selector";

	private static final String _NAME_SPANISH = "Selector de cuenta";

	private final FragmentCollectionContributor _fragmentCollectionContributor =
		Mockito.mock(FragmentCollectionContributor.class);

}