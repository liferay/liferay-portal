/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.util;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
public class LayoutPageTemplatePortletUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-104842")
	public void testFetchLayoutPageTemplateCollection() {
		_testFetchLayoutPageTemplateCollection();
		_testFetchLayoutPageTemplateCollectionWithExternalReferenceCode();
		_testFetchLayoutPageTemplateCollectionWithOtherGroupId();
		_testFetchLayoutPageTemplateCollectionWithoutParameters();
	}

	private void _testFetchLayoutPageTemplateCollection() {
		long groupId = RandomTestUtil.randomLong();
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"layoutPageTemplateCollectionId",
			String.valueOf(layoutPageTemplateCollectionId));

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		Mockito.when(
			layoutPageTemplateCollection.getGroupId()
		).thenReturn(
			groupId
		);

		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionLocalServiceUtil.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId)
		).thenReturn(
			layoutPageTemplateCollection
		);

		Assert.assertSame(
			layoutPageTemplateCollection,
			LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
				mockHttpServletRequest, groupId));
	}

	private void _testFetchLayoutPageTemplateCollectionWithExternalReferenceCode() {
		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = RandomTestUtil.randomLong();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"layoutPageTemplateCollectionExternalReferenceCode",
			externalReferenceCode);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionLocalServiceUtil.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						externalReferenceCode, groupId)
		).thenReturn(
			layoutPageTemplateCollection
		);

		Assert.assertSame(
			layoutPageTemplateCollection,
			LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
				mockHttpServletRequest, groupId));
	}

	private void _testFetchLayoutPageTemplateCollectionWithOtherGroupId() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"layoutPageTemplateCollectionId",
			String.valueOf(layoutPageTemplateCollectionId));

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		Mockito.when(
			layoutPageTemplateCollection.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionLocalServiceUtil.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId)
		).thenReturn(
			layoutPageTemplateCollection
		);

		Assert.assertNull(
			LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
				mockHttpServletRequest, RandomTestUtil.randomLong()));
	}

	private void _testFetchLayoutPageTemplateCollectionWithoutParameters() {
		Assert.assertNull(
			LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
				new MockHttpServletRequest(), RandomTestUtil.randomLong()));
	}

	private final MockedStatic<LayoutPageTemplateCollectionLocalServiceUtil>
		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic =
			Mockito.mockStatic(
				LayoutPageTemplateCollectionLocalServiceUtil.class);

}