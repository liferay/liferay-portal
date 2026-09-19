/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.highlight;

import com.liferay.portal.search.query.Query;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@ProviderType
public interface HighlightBuilder {

	public HighlightBuilder addFieldConfig(FieldConfig fieldConfig);

	public HighlightBuilder boundaryChars(char... boundaryChars);

	public HighlightBuilder boundaryMaxScan(Integer boundaryMaxScan);

	public HighlightBuilder boundaryScannerLocale(String boundaryScannerLocale);

	public HighlightBuilder boundaryScannerType(String boundaryScannerType);

	public Highlight build();

	public HighlightBuilder encoder(String encoder);

	public HighlightBuilder fieldConfigs(Collection<FieldConfig> fieldConfigs);

	public HighlightBuilder forceSource(Boolean forceSource);

	public HighlightBuilder fragmentSize(Integer fragmentSize);

	public HighlightBuilder fragmenter(String fragmenter);

	public HighlightBuilder highlightFilter(Boolean highlightFilter);

	public HighlightBuilder highlightQuery(Query highlightQuery);

	public HighlightBuilder highlighterType(String highlighterType);

	public HighlightBuilder noMatchSize(Integer noMatchSize);

	public HighlightBuilder numOfFragments(Integer numOfFragments);

	public HighlightBuilder order(String order);

	public HighlightBuilder phraseLimit(Integer phraseLimit);

	public HighlightBuilder postTags(String... postTags);

	public HighlightBuilder preTags(String... preTags);

	public HighlightBuilder requireFieldMatch(Boolean requireFieldMatch);

	public HighlightBuilder tagsSchema(String tagsSchema);

	public HighlightBuilder useExplicitFieldOrder(
		Boolean useExplicitFieldOrder);

}