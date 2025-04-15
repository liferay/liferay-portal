/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.portlet.action;

import com.liferay.batch.planner.batch.engine.broker.BatchEngineBroker;
import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.web.internal.helper.BatchPlannerPlanHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.nio.file.Files;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER,
		"mvc.command.name=/batch_planner/submit_batch_planner_plan"
	},
	service = MVCResourceCommand.class
)
public class SubmitBatchPlannerPlanMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

		if (cmd.equals(Constants.EXPORT)) {
			_submitExportBatchPlannerPlan(resourceRequest, resourceResponse);
		}
		else if (cmd.equals(Constants.IMPORT)) {
			_submitImportBatchPlannerPlan(resourceRequest, resourceResponse);
		}
	}

	private void _submitExportBatchPlannerPlan(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanHelper.addExportBatchPlannerPlan(
				resourceRequest, null);

		if (batchPlannerPlan.isTemplate()) {
			return;
		}

		_batchEngineBroker.submit(batchPlannerPlan.getBatchPlannerPlanId());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"externalReferenceCode",
				batchPlannerPlan.getBatchPlannerPlanId()));
	}

	private void _submitImportBatchPlannerPlan(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(resourceRequest);

		File importFile = _toBatchPlannerFile(
			uploadPortletRequest.getFileName("importFile"),
			uploadPortletRequest.getFileAsStream("importFile"));

		try {
			URI importFileURI = importFile.toURI();

			BatchPlannerPlan batchPlannerPlan =
				_batchPlannerPlanHelper.addImportBatchPlannerPlan(
					resourceRequest,
					ParamUtil.getString(resourceRequest, "name"),
					importFileURI.toString());

			_batchEngineBroker.submit(batchPlannerPlan.getBatchPlannerPlanId());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"externalReferenceCode",
					batchPlannerPlan.getBatchPlannerPlanId()));
		}
		finally {
			_file.delete(importFile);
		}
	}

	private File _toBatchPlannerFile(String fileName, InputStream inputStream)
		throws Exception {

		File file = _file.createTempFile(
			_file.stripExtension(fileName) + StringPool.DASH,
			_file.getExtension(fileName));

		try {
			Files.copy(inputStream, file.toPath());

			return file;
		}
		catch (IOException ioException) {
			if (file.exists()) {
				file.delete();
			}

			throw ioException;
		}
	}

	@Reference
	private BatchEngineBroker _batchEngineBroker;

	@Reference
	private BatchPlannerPlanHelper _batchPlannerPlanHelper;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private Portal _portal;

}