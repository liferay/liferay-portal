/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.rest.client.dto.v1_0.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CTProcessResourceTest extends BaseCTProcessResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteCTProcessBatch() throws Exception {
		super.testDeleteCTProcessBatch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"description", "ownerName", "status"};
	}

	@Override
	protected CTProcess testDeleteCTProcess_addCTProcess() throws Exception {
		return _addCTProcess();
	}

	@Override
	protected CTProcess testGetCTProcess_addCTProcess() throws Exception {
		return _addCTProcess();
	}

	@Override
	protected CTProcess testGetCTProcessesPage_addCTProcess(CTProcess ctProcess)
		throws Exception {

		CTProcess postCTProcess = _addCTProcess(
			ctProcess.getName(), ctProcess.getDescription());

		com.liferay.change.tracking.model.CTProcess serviceBuilderCTProcess =
			_ctProcessLocalService.getCTProcess(postCTProcess.getId());

		serviceBuilderCTProcess.setCreateDate(ctProcess.getDatePublished());

		_ctProcessLocalService.updateCTProcess(serviceBuilderCTProcess);

		return ctProcessResource.getCTProcess(
			serviceBuilderCTProcess.getCtProcessId());
	}

	@Override
	protected CTProcess testGraphQLCTProcess_addCTProcess() throws Exception {
		return _addCTProcess();
	}

	@Override
	protected CTProcess testPostCTProcessRevert_addCTProcess()
		throws Exception {

		CTProcess ctProcess = _addCTProcess();

		com.liferay.change.tracking.model.CTProcess serviceBuilderCTProcess =
			_ctProcessLocalService.getCTProcess(ctProcess.getId());

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			serviceBuilderCTProcess.getCtCollectionId());

		ctCollection.setStatus(WorkflowConstants.STATUS_APPROVED);

		_ctCollectionLocalService.updateCTCollection(ctCollection);

		return ctProcess;
	}

	private CTProcess _addCTProcess() throws Exception {
		return _addCTProcess(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private CTProcess _addCTProcess(String name, String description)
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, name, description);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		com.liferay.change.tracking.model.CTProcess serviceBuilderCTProcess =
			_ctProcessLocalService.addCTProcess(
				TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

		return ctProcessResource.getCTProcess(
			serviceBuilderCTProcess.getCtProcessId());
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

}