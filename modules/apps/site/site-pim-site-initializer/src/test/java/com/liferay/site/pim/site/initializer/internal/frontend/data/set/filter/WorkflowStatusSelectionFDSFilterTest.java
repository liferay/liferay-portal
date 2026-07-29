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
			WorkflowConstants.LABEL_APPROVED, WorkflowConstants.STATUS_APPROVED,
			selectionFDSFilterItems.get(0));
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_DRAFT, WorkflowConstants.STATUS_DRAFT,
			selectionFDSFilterItems.get(1));
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_EXPIRED, WorkflowConstants.STATUS_EXPIRED,
			selectionFDSFilterItems.get(2));
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_PENDING, WorkflowConstants.STATUS_PENDING,
			selectionFDSFilterItems.get(3));
		_assertSelectionFDSFilterItem(
			WorkflowConstants.LABEL_SCHEDULED,
			WorkflowConstants.STATUS_SCHEDULED, selectionFDSFilterItems.get(4));
	}

	private void _assertSelectionFDSFilterItem(
		String label, int value,
		SelectionFDSFilterItem selectionFDSFilterItem) {

		Assert.assertEquals(label, selectionFDSFilterItem.getLabel());
		Assert.assertEquals(
			String.valueOf(value),
			String.valueOf(selectionFDSFilterItem.getValue()));
	}

}