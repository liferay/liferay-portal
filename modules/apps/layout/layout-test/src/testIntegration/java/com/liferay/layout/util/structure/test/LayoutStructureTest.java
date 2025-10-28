/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class LayoutStructureTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_fragmentEntry = _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			StringUtil.randomString(), StringUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, "{fieldSets: []}", null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		_fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				_fragmentEntry.getExternalReferenceCode(),
				_fragmentEntry.getScopeERC(), defaultSegmentsExperienceId,
				_layout.getPlid(), _fragmentEntry.getCss(),
				_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
				_fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				null, _fragmentEntry.getType(), serviceContext);
	}

	@Test
	public void testCopyCollectionStyledLayoutStructureItemWithSameCollectionStyledLayoutStructureItemAsParent() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem collectionStyledLayoutStructureItem =
			layoutStructure.addCollectionStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					collectionStyledLayoutStructureItem.getItemId()),
				collectionStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			2, 2, 1, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyColumnLayoutStructureItem() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem1 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem1.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem1.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(columnLayoutStructureItem.getItemId()),
			rootLayoutStructureItem.getItemId());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyDropZoneLayoutStructureItem() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem dropZoneLayoutStructureItem =
			layoutStructure.addDropZoneLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(dropZoneLayoutStructureItem.getItemId()),
			rootLayoutStructureItem.getItemId());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyFormStepLayoutStructureItem() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepLayoutStructureItem =
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(formStepLayoutStructureItem.getItemId()),
			rootLayoutStructureItem.getItemId());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyFragmentDropZoneLayoutStructureItem() throws Exception {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem fragmentDropZoneLayoutStructureItem =
			layoutStructure.addFragmentDropZoneLayoutStructureItem(
				fragmentStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(
				fragmentDropZoneLayoutStructureItem.getItemId()),
			rootLayoutStructureItem.getItemId());
	}

	@Test
	public void testCopyFragmentStyledLayoutStructureItemWithSameFragmentStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				fragmentStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 1, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test
	public void testCopyInsideFormStyledLayoutStructureItemWithMultistep()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.updateItemConfig(
			_jsonFactory.createJSONObject("{formType : multistep}"));

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepLayoutStructureItem =
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				formStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 1, 0, copiedLayoutStructureItems, formStepLayoutStructureItem);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyLayoutStructureItemsWithCollectionItemAsParent() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem collectionStyledLayoutStructureItem =
			layoutStructure.addCollectionStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(rootLayoutStructureItem.getItemId()),
			collectionStyledLayoutStructureItem.getChildrenItemId(0));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyLayoutStructureItemsWithParentItemAsChildrenOfItemId()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			columnLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(
				containerStyledLayoutStructureItem.getItemId()),
			columnLayoutStructureItem.getItemId());
	}

	@Test
	public void testCopyLayoutStructureItemWithCollectionStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem collectionStyledLayoutStructureItem =
			layoutStructure.addCollectionStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				collectionStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 1, 0, copiedLayoutStructureItems,
			layoutStructure.getLayoutStructureItem(
				collectionStyledLayoutStructureItem.getChildrenItemId(0)));
	}

	@Test
	public void testCopyLayoutStructureItemWithColumnLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				columnLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				columnLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems, columnLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithContainerStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				columnLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				containerStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems,
			containerStyledLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithDropZoneLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem dropZoneLayoutStructureItem =
			layoutStructure.addDropZoneLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				dropZoneLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				dropZoneLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems, dropZoneLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithFormStepLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepLayoutStructureItem =
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				formStepLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				formStepLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems, formStepLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithFormStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				formStyledLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				formStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems, formStyledLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithFragmentDropZoneLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem1 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink1.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem fragmentDropZoneLayoutStructureItem =
			layoutStructure.addFragmentDropZoneLayoutStructureItem(
				fragmentStyledLayoutStructureItem1.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem2 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink2.getFragmentEntryLinkId(),
				fragmentDropZoneLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem2.getItemId()),
				fragmentDropZoneLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems,
			fragmentDropZoneLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithFragmentStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem1 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink1.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem2 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink2.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem2.getItemId()),
				fragmentStyledLayoutStructureItem1.getItemId());

		_assertParentLayoutStructureItem(
			1, 3, 2, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithRootLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				rootLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 0, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithRowStyledLayoutStructureItemAsParent()
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				columnLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					fragmentStyledLayoutStructureItem.getItemId()),
				rowStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 1, copiedLayoutStructureItems,
			containerStyledLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithRowStyleLayoutStructureItemAsParent() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0, 3);

		layoutStructure.addContainerStyledLayoutStructureItem(
			rootLayoutStructureItem.getItemId(), 1);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					rowStyledLayoutStructureItem.getItemId()),
				rowStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 3, 1, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test
	public void testCopyLayoutStructureItemWithSameItemIdAndParentItemId() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					containerStyledLayoutStructureItem.getItemId()),
				containerStyledLayoutStructureItem.getItemId());

		_assertParentLayoutStructureItem(
			1, 2, 1, copiedLayoutStructureItems, rootLayoutStructureItem);
	}

	@Test
	public void testCopyMultipleLayoutStructureItems() throws Exception {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 2);

		LayoutStructureItem columnLayoutStructureItem1 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem1 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink1.getFragmentEntryLinkId(),
				columnLayoutStructureItem1.getItemId(), 0);

		LayoutStructureItem columnLayoutStructureItem2 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 1);

		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem2 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink2.getFragmentEntryLinkId(),
				columnLayoutStructureItem2.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Arrays.asList(
					fragmentStyledLayoutStructureItem1.getItemId(),
					fragmentStyledLayoutStructureItem2.getItemId()),
				containerStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			copiedLayoutStructureItems.toString(), 2,
			copiedLayoutStructureItems.size());

		List<String> childrenItemIds =
			containerStyledLayoutStructureItem.getChildrenItemIds();

		LayoutStructureItem copiedLayoutStructureItem1 =
			copiedLayoutStructureItems.get(0);

		_assertFragmentStyledLayoutStructureItem(
			(FragmentStyledLayoutStructureItem)copiedLayoutStructureItem1,
			(FragmentStyledLayoutStructureItem)
				fragmentStyledLayoutStructureItem1);

		Assert.assertEquals(
			0, childrenItemIds.indexOf(copiedLayoutStructureItem1.getItemId()));

		LayoutStructureItem copiedLayoutStructureItem2 =
			copiedLayoutStructureItems.get(1);

		_assertFragmentStyledLayoutStructureItem(
			(FragmentStyledLayoutStructureItem)copiedLayoutStructureItem2,
			(FragmentStyledLayoutStructureItem)
				fragmentStyledLayoutStructureItem2);

		Assert.assertEquals(
			1, childrenItemIds.indexOf(copiedLayoutStructureItem2.getItemId()));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCopyRootLayoutStructureItem() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.copyLayoutStructureItems(
			Collections.singletonList(rootLayoutStructureItem.getItemId()),
			containerStyledLayoutStructureItem.getItemId());
	}

	@Test
	public void testCopySingleLayoutStructureItem() throws Exception {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 2);

		LayoutStructureItem columnLayoutStructureItem1 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem1 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink1.getFragmentEntryLinkId(),
				columnLayoutStructureItem1.getItemId(), 0);

		LayoutStructureItem columnLayoutStructureItem2 =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 1);

		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem2 =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink2.getFragmentEntryLinkId(),
				columnLayoutStructureItem2.getItemId(), 0);

		List<LayoutStructureItem> copiedLayoutStructureItems =
			layoutStructure.copyLayoutStructureItems(
				Collections.singletonList(
					rowStyledLayoutStructureItem.getItemId()),
				containerStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			copiedLayoutStructureItems.toString(), 5,
			copiedLayoutStructureItems.size());

		Assert.assertTrue(
			copiedLayoutStructureItems.get(0) instanceof
				RowStyledLayoutStructureItem);
		Assert.assertTrue(
			copiedLayoutStructureItems.get(1) instanceof
				ColumnLayoutStructureItem);

		_assertFragmentStyledLayoutStructureItem(
			(FragmentStyledLayoutStructureItem)copiedLayoutStructureItems.get(
				2),
			(FragmentStyledLayoutStructureItem)
				fragmentStyledLayoutStructureItem1);

		Assert.assertTrue(
			copiedLayoutStructureItems.get(3) instanceof
				ColumnLayoutStructureItem);

		_assertFragmentStyledLayoutStructureItem(
			(FragmentStyledLayoutStructureItem)copiedLayoutStructureItems.get(
				4),
			(FragmentStyledLayoutStructureItem)
				fragmentStyledLayoutStructureItem2);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testDuplicateDropZoneLayoutStructureItem() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		layoutStructure.addDropZoneLayoutStructureItem(
			containerStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.duplicateLayoutStructureItem(
			Collections.singletonList(
				containerStyledLayoutStructureItem.getItemId()));
	}

	@Test
	public void testMarkLayoutStructureItemForDeletion1() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				_fragmentEntryLink.getFragmentEntryLinkId(),
				columnLayoutStructureItem.getItemId(), 0);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(
				fragmentStyledLayoutStructureItem.getItemId()),
			Collections.emptyList());

		Assert.assertEquals(
			0,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));

		layoutStructure.unmarkLayoutStructureItemForDeletion(
			fragmentStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));
	}

	@Test
	public void testMarkLayoutStructureItemForDeletion2() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			_fragmentEntryLink.getFragmentEntryLinkId(),
			columnLayoutStructureItem.getItemId(), 0);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(columnLayoutStructureItem.getItemId()),
			Collections.emptyList());

		Assert.assertEquals(
			0,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));

		layoutStructure.unmarkLayoutStructureItemForDeletion(
			columnLayoutStructureItem.getItemId());

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));
	}

	@Test
	public void testMarkLayoutStructureItemForDeletion3() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			_fragmentEntryLink.getFragmentEntryLinkId(),
			columnLayoutStructureItem.getItemId(), 0);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(rowStyledLayoutStructureItem.getItemId()),
			Collections.emptyList());

		Assert.assertEquals(
			0,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));

		layoutStructure.unmarkLayoutStructureItemForDeletion(
			rowStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));
	}

	@Test
	public void testMarkLayoutStructureItemForDeletion4() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem rowStyledLayoutStructureItem =
			layoutStructure.addRowStyledLayoutStructureItem(
				containerStyledLayoutStructureItem.getItemId(), 0, 1);

		LayoutStructureItem columnLayoutStructureItem =
			layoutStructure.addColumnLayoutStructureItem(
				rowStyledLayoutStructureItem.getItemId(), 0);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			_fragmentEntryLink.getFragmentEntryLinkId(),
			columnLayoutStructureItem.getItemId(), 0);

		layoutStructure.markLayoutStructureItemForDeletion(
			Collections.singletonList(
				containerStyledLayoutStructureItem.getItemId()),
			Collections.emptyList());

		Assert.assertEquals(
			0,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));

		layoutStructure.unmarkLayoutStructureItemForDeletion(
			containerStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			1,
			_fragmentEntryLinkLocalService.
				getAllFragmentEntryLinksCountByFragmentEntryERC(
					_group.getGroupId(),
					_fragmentEntry.getExternalReferenceCode(),
					_fragmentEntry.getScopeERC()));
	}

	private FragmentEntryLink _addFragmentEntryLink() throws Exception {
		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), null,
			_fragmentEntry.getExternalReferenceCode(),
			_fragmentEntry.getScopeERC(), defaultSegmentsExperienceId,
			_layout.getPlid(), _fragmentEntry.getCss(),
			_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
			_fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0, null,
			_fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertFragmentStyledLayoutStructureItem(
		FragmentStyledLayoutStructureItem
			copiedFragmentStyledLayoutStructureItem,
		FragmentStyledLayoutStructureItem
			originalFragmentStyledLayoutStructureItem) {

		Assert.assertEquals(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				copiedFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId()),
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				originalFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId()));
	}

	private void _assertParentLayoutStructureItem(
		int expectedCopiedItemsIds, int expectedChildrenItemIds, int childIndex,
		List<LayoutStructureItem> copiedLayoutStructureItems,
		LayoutStructureItem parentLayoutStructureItem) {

		Assert.assertEquals(
			copiedLayoutStructureItems.toString(), expectedCopiedItemsIds,
			copiedLayoutStructureItems.size());

		LayoutStructureItem layoutStructureItem =
			copiedLayoutStructureItems.get(0);

		Assert.assertEquals(
			layoutStructureItem.getParentItemId(),
			parentLayoutStructureItem.getItemId());

		List<String> childrenItemIds =
			parentLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), expectedChildrenItemIds,
			childrenItemIds.size());
		Assert.assertEquals(
			childrenItemIds.get(childIndex), layoutStructureItem.getItemId());
	}

	private FragmentEntry _fragmentEntry;
	private FragmentEntryLink _fragmentEntryLink;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}