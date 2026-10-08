/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.display.context;

import com.liferay.document.library.item.selector.web.internal.info.item.DLInfoItemItemSelectorView;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Tejesh Boggavarapu
 */
public class DLItemSelectorViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_mimeTypesUtilMockedStatic.close();
		_portletPreferencesFactoryUtilMockedStatic.close();
	}

	@Test
	public void testGetExtensions() {
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		Assert.assertArrayEquals(
			new String[0], _getExtensions(infoItemItemSelectorCriterion));

		String extension1 = RandomTestUtil.randomString();
		String mimeType1 = RandomTestUtil.randomString();

		Mockito.when(
			MimeTypesUtil.getExtensions(mimeType1)
		).thenReturn(
			SetUtil.fromArray(extension1)
		);

		String extension2 = RandomTestUtil.randomString();
		String mimeType2 = RandomTestUtil.randomString();

		Mockito.when(
			MimeTypesUtil.getExtensions(mimeType2)
		).thenReturn(
			SetUtil.fromArray(extension1, extension2)
		);

		String mimeType3 = RandomTestUtil.randomString();

		Mockito.when(
			MimeTypesUtil.getExtensions(mimeType3)
		).thenReturn(
			Collections.emptySet()
		);

		infoItemItemSelectorCriterion.setMimeTypes(
			new String[] {
				mimeType1, StringPool.BLANK, mimeType2, null, StringPool.SPACE,
				mimeType3
			});

		Assert.assertArrayEquals(
			new String[] {extension1, extension2, mimeType3},
			_getExtensions(infoItemItemSelectorCriterion));
	}

	private String[] _getExtensions(
		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion) {

		DLItemSelectorViewDisplayContext<InfoItemItemSelectorCriterion>
			dlItemSelectorViewDisplayContext =
				new DLItemSelectorViewDisplayContext<>(
					null, null, null, new DLInfoItemItemSelectorView(), null,
					Mockito.mock(HttpServletRequest.class),
					infoItemItemSelectorCriterion, null, null, null, false,
					null);

		return dlItemSelectorViewDisplayContext.getExtensions();
	}

	private static final MockedStatic<MimeTypesUtil>
		_mimeTypesUtilMockedStatic = Mockito.mockStatic(MimeTypesUtil.class);
	private static final MockedStatic<PortletPreferencesFactoryUtil>
		_portletPreferencesFactoryUtilMockedStatic = Mockito.mockStatic(
			PortletPreferencesFactoryUtil.class);

}