/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.seo.studio.constants.SEOStudioScanConstants;
import com.liferay.seo.studio.detector.BaseDetector;
import com.liferay.seo.studio.model.CrawlHit;
import com.liferay.seo.studio.model.DetectorResult;
import com.liferay.seo.studio.model.Domain;

import java.net.URI;

import java.util.List;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Brooke Dalton
 */
@Service
public class DetectorService {

	public DetectorResult detect(
			long seoStudioScanId, JSONObject seoStudioScanJSONObject)
		throws Exception {

		long seoStudioDomainId = seoStudioScanJSONObject.getLong(
			"r_seoStudioDomainToSEOStudioScans_seoStudioDomainId");

		Domain domain = _seoStudioService.getSEOStudioDomain(seoStudioDomainId);

		if (domain == null) {
			return new DetectorResult(
				"Unable to get a domain for SEO Studio domain ID " +
					seoStudioDomainId,
				SEOStudioScanConstants.STATE_FAILED);
		}

		List<CrawlHit> crawlHits = _seoStudioService.getCrawlHits(
			seoStudioDomainId);

		if (ListUtil.isEmpty(crawlHits)) {
			return new DetectorResult(
				"Unable to get crawl hits for SEO Studio domain ID " +
					seoStudioDomainId,
				SEOStudioScanConstants.STATE_FAILED);
		}

		URI crawlURI = _seoStudioService.toCrawlURI(domain.getHostname());

		for (BaseDetector detector : _detectors) {
			detector.detect(
				seoStudioScanJSONObject.getLong(
					"r_accountToSEOStudioScans_accountEntryId"),
				crawlHits, crawlURI, seoStudioScanId);
		}

		return new DetectorResult(null, SEOStudioScanConstants.STATE_COMPLETED);
	}

	@Autowired
	private List<BaseDetector> _detectors;

	@Autowired
	private SEOStudioService _seoStudioService;

}