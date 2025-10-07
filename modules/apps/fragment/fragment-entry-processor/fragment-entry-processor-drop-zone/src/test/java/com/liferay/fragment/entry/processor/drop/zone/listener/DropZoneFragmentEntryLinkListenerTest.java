/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.drop.zone.listener;

import com.liferay.fragment.entry.processor.drop.zone.FragmentEntryProcessorDropZoneTestUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class DropZoneFragmentEntryLinkListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpDropZoneFragmentEntryLinkListener();
	}

	@Test
	@TestInfo("LPS-121223")
	public void testProcessFragmentEntryLinkHTMLInEditAddingDropZone()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						dropZoneId2);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];
		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[1];

		String newDropZoneId = RandomTestUtil.randomString();

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(
				dropZoneId1, newDropZoneId, dropZoneId2));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			fragmentEntryLink,
			new KeyValuePair(
				dropZoneId1, fragmentDropZoneLayoutStructureItem1.getItemId()),
			new KeyValuePair(newDropZoneId, StringPool.BLANK),
			new KeyValuePair(
				dropZoneId2, fragmentDropZoneLayoutStructureItem2.getItemId()));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditMode() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentEntryProcessorDropZoneTestUtil.
			addFragmentDropZoneLayoutStructureItem(
				fragmentEntryLink, layoutStructure, StringPool.BLANK);

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(StringPool.BLANK));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			true, new String[0], fragmentEntryLink);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditModeDifferentIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentEntryProcessorDropZoneTestUtil.
			addFragmentDropZoneLayoutStructureItem(
				fragmentEntryLink, layoutStructure,
				RandomTestUtil.randomString());

		String elementDropZoneId = RandomTestUtil.randomString();

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(elementDropZoneId));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			elementDropZoneId, fragmentEntryLink, StringPool.BLANK);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditModeSameIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId = RandomTestUtil.randomString();

		FragmentEntryProcessorDropZoneTestUtil.
			addFragmentDropZoneLayoutStructureItem(
				fragmentEntryLink, layoutStructure, dropZoneId);

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(dropZoneId));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			true, new String[0], fragmentEntryLink);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditMultipleDropZonesWithoutIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentEntryProcessorDropZoneTestUtil.
			addFragmentDropZoneLayoutStructureItem(
				fragmentEntryLink, layoutStructure, StringPool.BLANK);

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(StringPool.BLANK));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			true, new String[0], fragmentEntryLink);
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditRemovingDropZones()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						RandomTestUtil.randomString(), dropZoneId2,
						RandomTestUtil.randomString());

		FragmentDropZoneLayoutStructureItem
			deletedFragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[1];
		FragmentDropZoneLayoutStructureItem
			deletedFragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[3];
		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];
		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[2];

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(
				dropZoneId1, dropZoneId2));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			false,
			new String[] {
				deletedFragmentDropZoneLayoutStructureItem1.getItemId(),
				deletedFragmentDropZoneLayoutStructureItem2.getItemId()
			},
			fragmentEntryLink,
			new KeyValuePair(
				dropZoneId1, fragmentDropZoneLayoutStructureItem1.getItemId()),
			new KeyValuePair(
				dropZoneId2, fragmentDropZoneLayoutStructureItem2.getItemId()));
	}

	@Test
	@TestInfo("LPD-39780")
	public void testProcessFragmentEntryLinkHTMLInEditRemovingDropZonesWithoutIds()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, StringPool.BLANK,
						StringPool.BLANK, StringPool.BLANK);

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(StringPool.BLANK));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				fragmentDropZoneLayoutStructureItems[0];

		_assertUpdateLayoutPageTemplateStructureData(
			false,
			TransformUtil.transform(
				ArrayUtil.subset(fragmentDropZoneLayoutStructureItems, 1, 2),
				curFragmentDropZoneLayoutStructureItem ->
					curFragmentDropZoneLayoutStructureItem.getItemId(),
				String.class),
			fragmentEntryLink,
			new KeyValuePair(
				StringPool.BLANK,
				fragmentDropZoneLayoutStructureItem.getItemId()));
	}

	@Test
	public void testProcessFragmentEntryLinkHTMLInEditSwappingDropZones()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryProcessorDropZoneTestUtil.getMockFragmentEntryLink();

		LayoutStructure layoutStructure = new LayoutStructure();

		String dropZoneId1 = RandomTestUtil.randomString();
		String dropZoneId2 = RandomTestUtil.randomString();
		String dropZoneId3 = RandomTestUtil.randomString();

		FragmentDropZoneLayoutStructureItem[]
			fragmentDropZoneLayoutStructureItems =
				FragmentEntryProcessorDropZoneTestUtil.
					addFragmentDropZoneLayoutStructureItems(
						fragmentEntryLink, layoutStructure, dropZoneId1,
						dropZoneId2, dropZoneId3);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem1 =
				fragmentDropZoneLayoutStructureItems[0];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem2 =
				fragmentDropZoneLayoutStructureItems[1];

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem3 =
				fragmentDropZoneLayoutStructureItems[2];

		_setUpFragmentEntryProcessorRegistry(
			fragmentEntryLink,
			FragmentEntryProcessorDropZoneTestUtil.getHTML(
				dropZoneId2, dropZoneId3, dropZoneId1));

		_setUpLayoutPageTemplateStructure(layoutStructure.toString());

		_assertUpdateLayoutPageTemplateStructureData(
			fragmentEntryLink,
			new KeyValuePair(
				dropZoneId2, fragmentDropZoneLayoutStructureItem2.getItemId()),
			new KeyValuePair(
				dropZoneId3, fragmentDropZoneLayoutStructureItem3.getItemId()),
			new KeyValuePair(
				dropZoneId1, fragmentDropZoneLayoutStructureItem1.getItemId()));
	}

	private void _assertUpdateLayoutPageTemplateStructureData(
			boolean never, String[] deletedItemIds,
			FragmentEntryLink fragmentEntryLink,
			KeyValuePair... dropZoneIdItemIdKeyValuePairs)
		throws Exception {

		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			serviceContext.getThemeDisplay()
		).thenReturn(
			themeDisplay
		);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_dropZoneFragmentEntryLinkListener.
				updateLayoutPageTemplateStructure(fragmentEntryLink, null);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		Mockito.verify(
			serviceContext
		).getRequest();

		Mockito.verify(
			serviceContext
		).getResponse();

		Mockito.verify(
			themeDisplay
		).getRequest();

		Mockito.verify(
			themeDisplay
		).getResponse();

		if (never) {
			Mockito.verify(
				_layoutPageTemplateStructureLocalService, Mockito.never()
			).updateLayoutPageTemplateStructureData(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyString()
			);

			return;
		}

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			_layoutPageTemplateStructureLocalService
		).updateLayoutPageTemplateStructureData(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyLong(), argumentCaptor.capture()
		);

		LayoutStructure layoutStructure = LayoutStructure.of(
			argumentCaptor.getValue());

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			_getFragmentStyledLayoutStructureItem(layoutStructure);

		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryLinkId(),
			fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		List<String> childrenItemIds =
			fragmentStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), dropZoneIdItemIdKeyValuePairs.length,
			childrenItemIds.size());

		for (int i = 0; i < dropZoneIdItemIdKeyValuePairs.length; i++) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemIds.get(i));

			Assert.assertTrue(
				layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem);

			String dropZoneId = dropZoneIdItemIdKeyValuePairs[i].getKey();

			if (!Validator.isBlank(dropZoneId)) {
				FragmentDropZoneLayoutStructureItem
					fragmentDropZoneLayoutStructureItem =
						(FragmentDropZoneLayoutStructureItem)
							layoutStructureItem;

				Assert.assertEquals(
					dropZoneId,
					fragmentDropZoneLayoutStructureItem.
						getFragmentDropZoneId());
			}

			String itemId = dropZoneIdItemIdKeyValuePairs[i].getValue();

			if (!Validator.isBlank(itemId)) {
				Assert.assertEquals(itemId, layoutStructureItem.getItemId());
			}
		}

		for (String deletedItemId : deletedItemIds) {
			Assert.assertTrue(
				layoutStructure.isItemMarkedForDeletion(deletedItemId));
		}
	}

	private void _assertUpdateLayoutPageTemplateStructureData(
			FragmentEntryLink fragmentEntryLink,
			KeyValuePair... dropZoneIdItemIdKeyValuePairs)
		throws Exception {

		_assertUpdateLayoutPageTemplateStructureData(
			false, new String[0], fragmentEntryLink,
			dropZoneIdItemIdKeyValuePairs);
	}

	private void _assertUpdateLayoutPageTemplateStructureData(
			String dropZoneId, FragmentEntryLink fragmentEntryLink,
			String itemId)
		throws Exception {

		_assertUpdateLayoutPageTemplateStructureData(
			fragmentEntryLink, new KeyValuePair(dropZoneId, itemId));
	}

	private FragmentStyledLayoutStructureItem
		_getFragmentStyledLayoutStructureItem(LayoutStructure layoutStructure) {

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemIds.get(0));

		List<String> containerChildrenItemIds =
			containerStyledLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			containerChildrenItemIds.toString(), 1,
			containerChildrenItemIds.size());

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				containerChildrenItemIds.get(0));

		Assert.assertTrue(
			fragmentStyledLayoutStructureItem instanceof
				FragmentStyledLayoutStructureItem);

		return (FragmentStyledLayoutStructureItem)
			fragmentStyledLayoutStructureItem;
	}

	private void _setUpDropZoneFragmentEntryLinkListener() {
		_dropZoneFragmentEntryLinkListener =
			new DropZoneFragmentEntryLinkListener();

		_layoutPageTemplateStructureLocalService = Mockito.mock(
			LayoutPageTemplateStructureLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_dropZoneFragmentEntryLinkListener,
			"_layoutPageTemplateStructureLocalService",
			_layoutPageTemplateStructureLocalService);

		_fragmentEntryProcessorRegistry = Mockito.mock(
			FragmentEntryProcessorRegistry.class);

		ReflectionTestUtil.setFieldValue(
			_dropZoneFragmentEntryLinkListener,
			"_fragmentEntryProcessorRegistry", _fragmentEntryProcessorRegistry);
	}

	private void _setUpFragmentEntryProcessorRegistry(
			FragmentEntryLink fragmentEntryLink, String processedHTML)
		throws Exception {

		Mockito.when(
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				Mockito.eq(fragmentEntryLink),
				Mockito.any(FragmentEntryProcessorContext.class))
		).thenReturn(
			processedHTML
		);
	}

	private void _setUpLayoutPageTemplateStructure(String data) {
		LayoutPageTemplateStructure layoutPageTemplateStructure = Mockito.mock(
			LayoutPageTemplateStructure.class);

		Mockito.when(
			layoutPageTemplateStructure.getData(Mockito.anyLong())
		).thenReturn(
			data
		);

		Mockito.when(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			layoutPageTemplateStructure
		);
	}

	private DropZoneFragmentEntryLinkListener
		_dropZoneFragmentEntryLinkListener;
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

}