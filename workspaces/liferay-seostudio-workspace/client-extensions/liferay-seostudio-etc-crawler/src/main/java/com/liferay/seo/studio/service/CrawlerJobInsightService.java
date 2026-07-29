/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.SEOStudioScanConstants;
import com.liferay.seo.studio.model.CrawlHit;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobCondition;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
public class CrawlerJobInsightService {

	@Scheduled(fixedDelay = 60000)
	public void scheduledPatchSEOStudioScans() throws Exception {
		JSONArray itemsJSONArray = new JSONObject(
			_seoStudioService.getActiveSEOStudioScans()
		).optJSONArray(
			"items"
		);

		if (itemsJSONArray == null) {
			return;
		}

		for (Object object : itemsJSONArray) {
			JSONObject seoStudioScanJSONObject = (JSONObject)object;

			_patchSEOStudioScan(seoStudioScanJSONObject);
		}
	}

	private void _patchSEOStudioScan(JSONObject seoStudioScanJSONObject)
		throws Exception {

		String executionId = seoStudioScanJSONObject.optString("executionId");

		if (Validator.isNull(executionId)) {
			return;
		}

		String state = null;

		Job job = _kubernetesJobService.getJob(executionId);

		if (job == null) {
			state = SEOStudioScanConstants.STATE_FAILED;
		}
		else {
			JobStatus jobStatus = job.getStatus();

			if (jobStatus != null) {
				if (GetterUtil.getInteger(jobStatus.getActive()) > 0) {
					state = SEOStudioScanConstants.STATE_RUNNING;
				}

				if (GetterUtil.getInteger(jobStatus.getFailed()) > 0) {
					state = SEOStudioScanConstants.STATE_FAILED;
				}

				if (GetterUtil.getInteger(jobStatus.getSucceeded()) > 0) {
					state = SEOStudioScanConstants.STATE_COMPLETED;
				}
			}
		}

		if (Validator.isNull(state) ||
			state.equals(seoStudioScanJSONObject.optString("state"))) {

			return;
		}

		long seoStudioScanId = seoStudioScanJSONObject.getLong("id");

		if (state.equals(SEOStudioScanConstants.STATE_COMPLETED)) {
			long seoStudioDomainId = _seoStudioService.getSEOStudioDomainId(
				seoStudioScanJSONObject);

			JSONObject seoStudioDomainJSONObject =
				_seoStudioService.fetchSEOStudioDomainJSONObject(
					seoStudioDomainId);

			if (seoStudioDomainJSONObject == null) {
				_seoStudioService.patchSEOStudioScan(
					"Unable to get a domain for SEO Studio domain ID " +
						seoStudioDomainId,
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED);

				return;
			}

			List<CrawlHit> crawlHits = _seoStudioService.getCrawlHits(
				seoStudioDomainId);

			if (ListUtil.isEmpty(crawlHits)) {
				_seoStudioService.patchSEOStudioScan(
					"Unable to get crawl hits for SEO Studio domain ID " +
						seoStudioDomainId,
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED);

				return;
			}

			Set<String> canonicalURLs = new LinkedHashSet<>();
			String domainURL = _seoStudioService.toDomainURL(
				_seoStudioService.toCrawlURI(
					seoStudioDomainJSONObject.getString("hostname")));
			Set<String> linkedURLs = new HashSet<>();

			for (CrawlHit crawlHit : crawlHits) {
				String canonicalURL = crawlHit.getCanonicalURL();

				if (Validator.isNull(canonicalURL)) {
					continue;
				}

				canonicalURLs.add(canonicalURL);

				for (String linkedURL : crawlHit.getLinks()) {
					if (Validator.isNotNull(linkedURL) &&
						!linkedURL.equals(canonicalURL)) {

						linkedURLs.add(linkedURL);
					}
				}
			}

			List<String> pageURLs = TransformUtil.transform(
				canonicalURLs,
				canonicalURL -> {
					if (canonicalURL.equals(domainURL) ||
						linkedURLs.contains(canonicalURL)) {

						return null;
					}

					return canonicalURL;
				});

			if (ListUtil.isEmpty(pageURLs)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"No orphan pages were found for SEO Studio scan ID " +
							seoStudioScanId);
				}
			}
			else {
				JSONObject orphanPagesInsightJSONObject = new JSONObject(
				).put(
					"category", "linksAndURLs"
				).put(
					"classification", "problem"
				).put(
					"description",
					StringBundler.concat(
						"This page is published and indexable but has zero ",
						"internal links pointing to it. Orphan pages are ",
						"nearly invisible to both users browsing the site and ",
						"crawlers building the link graph. Even when they are ",
						"listed in a sitemap, they collect very little ",
						"ranking authority.")
				).put(
					"fixHint",
					StringBundler.concat(
						"Identify 2-5 topically related pages and add ",
						"contextual internal links pointing to the orphan, ",
						"with descriptive anchor text. If no relevant linking ",
						"context exists anywhere on the site, that is a ",
						"signal the page may not belong in the public site at ",
						"all.")
				).put(
					"name", "orphanPages"
				).put(
					"pageURLs", pageURLs
				).put(
					"severity", "2"
				);

				long accountEntryId = seoStudioScanJSONObject.getLong(
					"r_accountToSEOStudioScans_accountEntryId");

				_seoStudioService.postSEOStudioScanInsightsBatch(
					accountEntryId, orphanPagesInsightJSONObject,
					seoStudioScanId);
			}

			_seoStudioService.patchSEOStudioScan(
				null, seoStudioScanId, SEOStudioScanConstants.STATE_COMPLETED);
		}
		else if (state.equals(SEOStudioScanConstants.STATE_FAILED)) {
			String errorMessage = null;

			if (job == null) {
				errorMessage = "Kubernetes job does not exist";
			}
			else {
				JobStatus jobStatus = job.getStatus();

				for (JobCondition jobCondition : jobStatus.getConditions()) {
					if (!Objects.equals(jobCondition.getType(), "Failed") ||
						!Objects.equals(jobCondition.getStatus(), "True")) {

						continue;
					}

					if (Validator.isNotNull(jobCondition.getMessage())) {
						errorMessage = jobCondition.getMessage();

						break;
					}
				}

				if (errorMessage == null) {
					errorMessage = "Kubernetes job failed";
				}
			}

			_seoStudioService.patchSEOStudioScan(
				errorMessage, seoStudioScanId,
				SEOStudioScanConstants.STATE_FAILED);
		}
	}

	private static final Log _log = LogFactory.getLog(
		CrawlerJobInsightService.class);

	@Autowired
	private KubernetesJobService _kubernetesJobService;

	@Autowired
	private SEOStudioService _seoStudioService;

}