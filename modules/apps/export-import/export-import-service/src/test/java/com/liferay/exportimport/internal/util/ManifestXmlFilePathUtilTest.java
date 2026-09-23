/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class ManifestXmlFilePathUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetExportManifestXmlFilePathWhenGroupScoped() {
		Assert.assertEquals(
			"/group/" + _SCOPE_GROUP_ID + "/manifest.xml",
			ManifestXmlFilePathUtil.getExportManifestXmlFilePath(
				_mockPortletDataContext(_getGroupParameterMap())));
	}

	@Test
	public void testGetExportManifestXmlFilePathWhenNotGroupScoped() {
		Assert.assertEquals(
			"/manifest.xml",
			ManifestXmlFilePathUtil.getExportManifestXmlFilePath(
				_mockPortletDataContext(new HashMap<>())));
	}

	@Test
	public void testGetImportManifestXmlFilePathWhenGroupScoped() {
		Assert.assertEquals(
			"/group/" + _SOURCE_GROUP_ID + "/manifest.xml",
			ManifestXmlFilePathUtil.getImportManifestXmlFilePath(
				_mockPortletDataContext(_getGroupParameterMap())));
	}

	@Test
	public void testGetImportManifestXmlFilePathWhenNotGroupScoped() {
		Assert.assertEquals(
			"/manifest.xml",
			ManifestXmlFilePathUtil.getImportManifestXmlFilePath(
				_mockPortletDataContext(new HashMap<>())));
	}

	@Test
	public void testGetImportManifestXmlFilePathWhenSourceGroupIdIsGiven() {
		Assert.assertEquals(
			"/group/" + _SOURCE_GROUP_ID + "/manifest.xml",
			ManifestXmlFilePathUtil.getImportManifestXmlFilePath(
				_SOURCE_GROUP_ID));
	}

	private Map<String, String[]> _getGroupParameterMap() {
		return GroupExportImportParameterUtil.getGroupExportParameterMap(
			RandomTestUtil.randomString(), new HashMap<>());
	}

	private PortletDataContext _mockPortletDataContext(
		Map<String, String[]> parameterMap) {

		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		Mockito.when(
			portletDataContext.getParameterMap()
		).thenReturn(
			parameterMap
		);

		Mockito.when(
			portletDataContext.getScopeGroupId()
		).thenReturn(
			_SCOPE_GROUP_ID
		);

		Mockito.when(
			portletDataContext.getSourceGroupId()
		).thenReturn(
			_SOURCE_GROUP_ID
		);

		return portletDataContext;
	}

	private static final long _SCOPE_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SOURCE_GROUP_ID = RandomTestUtil.randomLong();

}