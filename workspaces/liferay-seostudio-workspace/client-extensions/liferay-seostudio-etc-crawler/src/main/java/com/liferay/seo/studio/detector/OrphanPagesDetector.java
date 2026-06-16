/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.detector;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.model.CrawlHit;

import java.net.URI;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author Brooke Dalton
 */
@Component
public class OrphanPagesDetector extends BaseDetector {

	@Override
	public void detect(
			long accountEntryId, List<CrawlHit> crawlHits, URI crawlURI,
			long seoStudioScanId)
		throws Exception {

		Set<String> canonicalURLs = new LinkedHashSet<>();
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

		String domainURL = seoStudioService.toDomainURL(crawlURI);

		List<String> orphanPageURLs = TransformUtil.transform(
			canonicalURLs,
			canonicalURL -> {
				if (canonicalURL.equals(domainURL) ||
					linkedURLs.contains(canonicalURL)) {

					return null;
				}

				return canonicalURL;
			});

		if (ListUtil.isEmpty(orphanPageURLs)) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"No orphan pages were detected for scan " +
						seoStudioScanId);
			}

			return;
		}

		JSONObject definitionJSONObject = new JSONObject();

		definitionJSONObject.put(
			"category", "linksAndURLs"
		).put(
			"classification", "problem"
		).put(
			"description",
			StringBundler.concat(
				"This page is published and indexable but has zero internal ",
				"links pointing to it. Orphan pages are nearly invisible to ",
				"both users browsing the site and crawlers building the link ",
				"graph. Even when they are listed in a sitemap, they collect ",
				"very little ranking authority.")
		).put(
			"fixHint",
			StringBundler.concat(
				"Identify 2-5 topically related pages and add contextual ",
				"internal links pointing to the orphan, with descriptive ",
				"anchor text. If no relevant linking context exists anywhere ",
				"on the site, that is a signal the page may not belong in the ",
				"public site at all.")
		).put(
			"name", "orphanPages"
		).put(
			"severity", "2"
		);

		postInsights(
			accountEntryId, definitionJSONObject, orphanPageURLs,
			resolvePageIds(accountEntryId, orphanPageURLs, seoStudioScanId),
			seoStudioScanId);
	}

	private static final Log _log = LogFactory.getLog(
		OrphanPagesDetector.class);

}