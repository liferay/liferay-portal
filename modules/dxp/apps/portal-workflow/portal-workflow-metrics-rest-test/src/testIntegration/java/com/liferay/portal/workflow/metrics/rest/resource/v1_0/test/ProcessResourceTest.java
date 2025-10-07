/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ProcessResourceTest extends BaseProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_documents = _workflowMetricsRESTTestHelper.getDocuments(
			testGroup.getCompanyId());

		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				document.getLong("companyId"), document.getLong("processId"));
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.restoreProcess(document);
		}

		_deleteProcesses();
	}

	@Override
	@Test
	public void testGetProcess() throws Exception {
		super.testGetProcess();
	}

	@Override
	@Test
	public void testGetProcessTitle() throws Exception {
		Process process = randomProcess();

		_addProcess(process);

		String title = processResource.getProcessTitle(process.getId());

		Assert.assertEquals(process.getTitle(), title);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected Process randomProcess() throws Exception {
		Process process = super.randomProcess();

		process.setTitle(RandomTestUtil.randomString());
		process.setTitle_i18n(
			HashMapBuilder.put(
				LocaleUtil.US.toString(), process.getTitle()
			).build());

		return process;
	}

	@Override
	protected Process testDeleteProcess_addProcess() throws Exception {
		return testGetProcess_addProcess();
	}

	@Override
	protected Process testGetProcess_addProcess() throws Exception {
		return _addProcess(randomProcess());
	}

	@Override
	protected Process testGraphQLProcess_addProcess() throws Exception {
		return testGetProcess_addProcess();
	}

	@Override
	protected Process testPostProcess_addProcess(Process process)
		throws Exception {

		return _addProcess(process);
	}

	@Override
	protected Process testPutProcess_addProcess() throws Exception {
		return testGetProcess_addProcess();
	}

	private Process _addProcess(Process process) throws Exception {
		process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId(), process);

		_processes.add(process);

		return process;
	}

	private void _deleteProcesses() throws Exception {
		for (Process process : _processes) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), process);
		}
	}

	private Document[] _documents;
	private final List<Process> _processes = new ArrayList<>();

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}