/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.initializer.SiteInitializer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brian I. Kim
 */
public class SiteInitializerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInitializeDefersWhenContentTypeObjectDefinitionMissing()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		SiteInitializer siteInitializer = Mockito.mock(SiteInitializer.class);

		try (MockedStatic<ObjectDefinitionLocalServiceUtil>
				objectDefinitionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(ObjectDefinitionLocalServiceUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class)) {

			objectDefinitionLocalServiceUtilMockedStatic.when(
				() ->
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							Mockito.anyString(), Mockito.eq(companyId))
			).thenReturn(
				null
			);

			SiteInitializerUtil.initialize(companyId, siteInitializer);

			groupLocalServiceUtilMockedStatic.verify(
				() -> GroupLocalServiceUtil.getGroup(
					Mockito.anyLong(), Mockito.anyString()),
				Mockito.never());

			Mockito.verifyNoInteractions(siteInitializer);
		}
	}

	@Test
	public void testInitializeProceedsWhenContentTypeObjectDefinitionsExist()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		SiteInitializer siteInitializer = Mockito.mock(SiteInitializer.class);

		try (MockedStatic<ObjectDefinitionLocalServiceUtil>
				objectDefinitionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(ObjectDefinitionLocalServiceUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class);
			MockedStatic<FriendlyURLNormalizerUtil>
				friendlyURLNormalizerUtilMockedStatic = Mockito.mockStatic(
					FriendlyURLNormalizerUtil.class);
			MockedStatic<LayoutLocalServiceUtil>
				layoutLocalServiceUtilMockedStatic = Mockito.mockStatic(
					LayoutLocalServiceUtil.class)) {

			objectDefinitionLocalServiceUtilMockedStatic.when(
				() ->
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							Mockito.anyString(), Mockito.eq(companyId))
			).thenReturn(
				Mockito.mock(ObjectDefinition.class)
			);

			Group group = Mockito.mock(Group.class);

			groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(
					companyId, GroupConstants.CMS)
			).thenReturn(
				group
			);

			friendlyURLNormalizerUtilMockedStatic.when(
				() -> FriendlyURLNormalizerUtil.normalizeWithEncoding(
					"/dashboard")
			).thenReturn(
				"/dashboard"
			);

			layoutLocalServiceUtilMockedStatic.when(
				() -> LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
					Mockito.anyLong(), Mockito.anyBoolean(),
					Mockito.anyString())
			).thenReturn(
				Mockito.mock(Layout.class)
			);

			SiteInitializerUtil.initialize(companyId, siteInitializer);

			groupLocalServiceUtilMockedStatic.verify(
				() -> GroupLocalServiceUtil.getGroup(
					companyId, GroupConstants.CMS));
		}
	}

}