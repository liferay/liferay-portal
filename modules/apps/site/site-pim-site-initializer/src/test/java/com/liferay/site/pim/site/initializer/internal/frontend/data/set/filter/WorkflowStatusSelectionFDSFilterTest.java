/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Balazs Breier
 */
public class WorkflowStatusSelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSelectionFDSFilterItems() {
		WorkflowStatusSelectionFDSFilter workflowStatusSelectionFDSFilter =
			new WorkflowStatusSelectionFDSFilter();

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			workflowStatusSelectionFDSFilter.getSelectionFDSFilterItems(
				LocaleUtil.US);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 5,
			selectionFDSFilterItems.size());

		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_APPROVED, selectionFDSFilterItems.get(0),
			WorkflowConstants.STATUS_APPROVED);
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_DRAFT, selectionFDSFilterItems.get(1),
			WorkflowConstants.STATUS_DRAFT);
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_EXPIRED, selectionFDSFilterItems.get(2),
			WorkflowConstants.STATUS_EXPIRED);
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_PENDING, selectionFDSFilterItems.get(3),
			WorkflowConstants.STATUS_PENDING);
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_SCHEDULED, selectionFDSFilterItems.get(4),
			WorkflowConstants.STATUS_SCHEDULED);
	}

	private void _assertSelectionFDSFilterItem(
		String label, SelectionFDSFilterItem selectionFDSFilterItem,
		int value) {

		Assert.assertEquals(label, selectionFDSFilterItem.getLabel());
		Assert.assertEquals(
			String.valueOf(value),
			String.valueOf(selectionFDSFilterItem.getValue()));
	}

}