/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.helper;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.web.internal.model.TestClassType;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetPublisherWebHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddSelection() throws Exception {
		_setUpSAXReaderUtil();

		AssetEntryLocalService assetEntryLocalService = Mockito.mock(
			AssetEntryLocalService.class);

		AssetEntry assetEntry1 = _addAssetEntry(assetEntryLocalService);
		AssetEntry assetEntry2 = _addAssetEntry(assetEntryLocalService);

		AssetPublisherWebHelper assetPublisherWebHelper =
			new AssetPublisherWebHelper();

		ReflectionTestUtil.setFieldValue(
			assetPublisherWebHelper, "_assetEntryLocalService",
			assetEntryLocalService);

		MockPortletPreferences portletPreferences =
			new MockPortletPreferences();

		assetPublisherWebHelper.addSelection(
			portletPreferences, assetEntry1.getEntryId(), -1);
		assetPublisherWebHelper.addSelection(
			portletPreferences, assetEntry2.getEntryId(), -1);

		String[] assetEntryXmls = portletPreferences.getValues(
			"assetEntryXml", new String[0]);

		Assert.assertEquals(
			assetEntry1.getClassName(), _getAssetEntryType(assetEntryXmls[0]));
		Assert.assertEquals(
			assetEntry2.getClassName(), _getAssetEntryType(assetEntryXmls[1]));
	}

	@Test
	public void testGetClassTypeIds() {
		AssetPublisherWebHelper assetPublisherWebHelper =
			new AssetPublisherWebHelper();

		MockPortletPreferences portletPreferences =
			new MockPortletPreferences();

		String className = RandomTestUtil.randomString();

		portletPreferences.setValue(
			"anyClassType" + className, Boolean.FALSE.toString());

		Long[] classTypeIds = {
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong()
		};

		portletPreferences.setValue(
			"classTypeIds" + className,
			StringUtil.merge(classTypeIds, StringPool.COMMA));

		ArrayUtil.reverse(classTypeIds);

		Assert.assertArrayEquals(
			classTypeIds,
			assetPublisherWebHelper.getClassTypeIds(
				portletPreferences, className,
				ListUtil.fromArray(
					new TestClassType(
						classTypeIds[0], RandomTestUtil.randomString()),
					new TestClassType(
						RandomTestUtil.randomLong(),
						RandomTestUtil.randomString()),
					new TestClassType(
						classTypeIds[1], RandomTestUtil.randomString()))));
	}

	private AssetEntry _addAssetEntry(
		AssetEntryLocalService assetEntryLocalService) {

		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.when(
			assetEntry.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			assetEntry.getClassUuid()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		long entryId = RandomTestUtil.randomLong();

		Mockito.when(
			assetEntry.getEntryId()
		).thenReturn(
			entryId
		);

		Mockito.when(
			assetEntryLocalService.fetchEntry(entryId)
		).thenReturn(
			assetEntry
		);

		return assetEntry;
	}

	private String _getAssetEntryType(String assetEntryXml) throws Exception {
		Document document = SAXReaderUtil.read(assetEntryXml);

		Element rootElement = document.getRootElement();

		return rootElement.elementText("asset-entry-type");
	}

	private void _setUpSAXReaderUtil() {
		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();

		secureSAXReaderImpl.setSecure(true);

		saxReaderUtil.setSAXReader(secureSAXReaderImpl);

		UnsecureSAXReaderUtil unsecureSAXReaderUtil =
			new UnsecureSAXReaderUtil();

		unsecureSAXReaderUtil.setSAXReader(new SAXReaderImpl());
	}

}