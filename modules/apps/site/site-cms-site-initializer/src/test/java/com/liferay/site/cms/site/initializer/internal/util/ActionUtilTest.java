/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
public class ActionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			themeDisplay.getPathMain()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getPortalURL()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			StringPool.BLANK
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	@Test
	public void testGetStructureObjectFolderCustomDropdownItems() {
		List<DropdownItem> dropdownItems =
			ActionUtil.getStructureObjectFolderCustomDropdownItems(
				List.of(), _mockHttpServletRequest);

		Assert.assertTrue(dropdownItems.toString(), dropdownItems.isEmpty());

		CMSStructureObjectFolderContributor
			cmsStructureObjectFolderContributor = Mockito.mock(
				CMSStructureObjectFolderContributor.class);

		dropdownItems = ActionUtil.getStructureObjectFolderCustomDropdownItems(
			List.of(cmsStructureObjectFolderContributor),
			_mockHttpServletRequest);

		Assert.assertTrue(dropdownItems.toString(), dropdownItems.isEmpty());

		String objectFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			cmsStructureObjectFolderContributor.
				getObjectFolderExternalReferenceCode()
		).thenReturn(
			objectFolderExternalReferenceCode
		);

		dropdownItems = ActionUtil.getStructureObjectFolderCustomDropdownItems(
			List.of(cmsStructureObjectFolderContributor),
			_mockHttpServletRequest);

		Assert.assertTrue(dropdownItems.toString(), dropdownItems.isEmpty());

		String creationMenuIcon = RandomTestUtil.randomString();

		Mockito.when(
			cmsStructureObjectFolderContributor.getCreationMenuIcon()
		).thenReturn(
			creationMenuIcon
		);

		String objectEntryFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		Mockito.when(
			cmsStructureObjectFolderContributor.
				getObjectEntryFolderExternalReferenceCode()
		).thenReturn(
			objectEntryFolderExternalReferenceCode
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getLabel(Mockito.any(Locale.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		try (MockedStatic<ObjectDefinitionServiceUtil>
				objectDefinitionServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectDefinitionServiceUtil.class)) {

			objectDefinitionServiceUtilMockedStatic.when(
				() -> ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					Mockito.anyLong(),
					Mockito.eq(
						new String[] {objectFolderExternalReferenceCode}))
			).thenReturn(
				List.of(objectDefinition)
			);

			dropdownItems =
				ActionUtil.getStructureObjectFolderCustomDropdownItems(
					List.of(cmsStructureObjectFolderContributor),
					_mockHttpServletRequest);

			Assert.assertEquals(
				dropdownItems.toString(), 1, dropdownItems.size());

			DropdownItem dropdownItem = dropdownItems.get(0);

			Assert.assertEquals(creationMenuIcon, dropdownItem.get("icon"));
		}
	}

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

}