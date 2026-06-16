/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.SEOStudioScanConstants;
import com.liferay.seo.studio.model.DetectorResult;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobCondition;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;

import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Brooke Dalton
 */
@Service
public class CrawlerJobStatusService {

	@Scheduled(fixedDelay = 60000)
	public void scheduledUpdateStates() {
		JSONArray itemsJSONArray = new JSONObject(
			_seoStudioService.getActiveScans()
		).optJSONArray(
			"items"
		);

		if (itemsJSONArray == null) {
			return;
		}

		for (Object object : itemsJSONArray) {
			JSONObject scanJSONObject = (JSONObject)object;

			long seoStudioScanId = scanJSONObject.getLong("id");

			try {
				String executionId = scanJSONObject.optString("executionId");

				if (Validator.isNull(executionId)) {
					continue;
				}

				Job job = _kubernetesJobService.getJob(executionId);

				String state = _getScanState(job);

				if (Validator.isNull(state) ||
					state.equals(scanJSONObject.optString("state"))) {

					continue;
				}

				if (state.equals(SEOStudioScanConstants.STATE_COMPLETED)) {
					DetectorResult detectorResult = _detectorService.detect(
						scanJSONObject, seoStudioScanId);

					_seoStudioService.patchScan(
						detectorResult.getErrorMessage(), seoStudioScanId,
						detectorResult.getState());
				}
				else if (state.equals(SEOStudioScanConstants.STATE_FAILED)) {
					_seoStudioService.patchScan(
						_getErrorMessage(job), seoStudioScanId,
						SEOStudioScanConstants.STATE_FAILED);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to update status of scan " + seoStudioScanId,
					exception);
			}
		}
	}

	private String _getErrorMessage(Job job) {
		if (job == null) {
			return "Kubernetes job does not exist";
		}

		JobStatus jobStatus = job.getStatus();

		List<JobCondition> jobConditions = jobStatus.getConditions();

		if (ListUtil.isEmpty(jobConditions)) {
			return "Kubernetes job failed";
		}

		for (JobCondition jobCondition : jobConditions) {
			if (!Objects.equals(jobCondition.getType(), "Failed") ||
				!Objects.equals(jobCondition.getStatus(), "True")) {

				continue;
			}

			String errorMessage = jobCondition.getMessage();

			if (Validator.isNotNull(errorMessage)) {
				return errorMessage;
			}
		}

		return "Kubernetes job failed";
	}

	private String _getScanState(Job job) {
		if (job == null) {
			return SEOStudioScanConstants.STATE_FAILED;
		}

		JobStatus jobStatus = job.getStatus();

		if (jobStatus == null) {
			return null;
		}

		if (GetterUtil.getInteger(jobStatus.getActive()) > 0) {
			return SEOStudioScanConstants.STATE_RUNNING;
		}

		if (GetterUtil.getInteger(jobStatus.getFailed()) > 0) {
			return SEOStudioScanConstants.STATE_FAILED;
		}

		if (GetterUtil.getInteger(jobStatus.getSucceeded()) > 0) {
			return SEOStudioScanConstants.STATE_COMPLETED;
		}

		return null;
	}

	private static final Log _log = LogFactory.getLog(
		CrawlerJobStatusService.class);

	@Autowired
	private DetectorService _detectorService;

	@Autowired
	private KubernetesJobService _kubernetesJobService;

	@Autowired
	private SEOStudioService _seoStudioService;

}