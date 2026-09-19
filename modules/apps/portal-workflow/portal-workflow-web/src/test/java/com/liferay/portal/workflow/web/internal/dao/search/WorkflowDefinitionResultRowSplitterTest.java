/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.dao.search;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.ResultRowSplitterEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.workflow.DefaultWorkflowDefinition;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Inácio Nery
 */
public class WorkflowDefinitionResultRowSplitterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSplitDefinitions() {
		_addWorkflowDefinition(true);
		_addWorkflowDefinition(false);

		List<ResultRowSplitterEntry> resultRowSplitterEntryList =
			_resultRowSplitter.split(_resultRows);

		Assert.assertEquals(
			resultRowSplitterEntryList.toString(), 2,
			resultRowSplitterEntryList.size());

		ResultRowSplitterEntry resultRowSplitterEntry =
			resultRowSplitterEntryList.get(0);

		Assert.assertEquals("published", resultRowSplitterEntry.getTitle());

		List<ResultRow> resultRows = resultRowSplitterEntry.getResultRows();

		Assert.assertEquals(resultRows.toString(), 1, resultRows.size());

		resultRowSplitterEntry = resultRowSplitterEntryList.get(1);

		Assert.assertEquals("not-published", resultRowSplitterEntry.getTitle());

		resultRows = resultRowSplitterEntry.getResultRows();

		Assert.assertEquals(resultRows.toString(), 1, resultRows.size());
	}

	@Test
	public void testSplitNoDefinitions() {
		List<ResultRowSplitterEntry> resultRowSplitterEntryList =
			_resultRowSplitter.split(_resultRows);

		Assert.assertEquals(
			resultRowSplitterEntryList.toString(), 0,
			resultRowSplitterEntryList.size());
	}

	@Test
	public void testSplitNotPublishedDefinitions() {
		_addWorkflowDefinition(false);

		List<ResultRowSplitterEntry> resultRowSplitterEntryList =
			_resultRowSplitter.split(_resultRows);

		Assert.assertEquals(
			resultRowSplitterEntryList.toString(), 1,
			resultRowSplitterEntryList.size());

		ResultRowSplitterEntry resultRowSplitterEntry =
			resultRowSplitterEntryList.get(0);

		Assert.assertEquals("not-published", resultRowSplitterEntry.getTitle());

		List<ResultRow> resultRows = resultRowSplitterEntry.getResultRows();

		Assert.assertEquals(resultRows.toString(), 1, resultRows.size());
	}

	@Test
	public void testSplitPublishedDefinitions() {
		_addWorkflowDefinition(true);

		List<ResultRowSplitterEntry> resultRowSplitterEntryList =
			_resultRowSplitter.split(_resultRows);

		Assert.assertEquals(
			resultRowSplitterEntryList.toString(), 1,
			resultRowSplitterEntryList.size());

		ResultRowSplitterEntry resultRowSplitterEntry =
			resultRowSplitterEntryList.get(0);

		Assert.assertEquals("published", resultRowSplitterEntry.getTitle());

		List<ResultRow> resultRows = resultRowSplitterEntry.getResultRows();

		Assert.assertEquals(resultRows.toString(), 1, resultRows.size());
	}

	private void _addWorkflowDefinition(boolean active) {
		DefaultWorkflowDefinition defaultWorkflowDefinition =
			new DefaultWorkflowDefinition();

		defaultWorkflowDefinition.setActive(active);

		ResultRow resultRow = new com.liferay.taglib.search.ResultRow(
			defaultWorkflowDefinition, RandomTestUtil.randomLong(),
			RandomTestUtil.randomInt());

		_resultRows.add(resultRow);
	}

	private final ResultRowSplitter _resultRowSplitter =
		new WorkflowDefinitionResultRowSplitter();
	private final List<ResultRow> _resultRows = new ArrayList<>();

}