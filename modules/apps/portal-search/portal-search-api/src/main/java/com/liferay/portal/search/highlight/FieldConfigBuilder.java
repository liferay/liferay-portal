/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.highlight;

import com.liferay.portal.search.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@ProviderType
public interface FieldConfigBuilder {

	public FieldConfigBuilder boundaryChars(char... boundaryChars);

	public FieldConfigBuilder boundaryMaxScan(Integer boundaryMaxScan);

	public FieldConfigBuilder boundaryScannerLocale(
		String boundaryScannerLocale);

	public FieldConfigBuilder boundaryScannerType(String boundaryScannerType);

	public FieldConfig build();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             Highlights#fieldConfigBuilder(String)}
	 */
	@Deprecated
	public FieldConfigBuilder field(String field);

	public FieldConfigBuilder forceSource(Boolean forceSource);

	public FieldConfigBuilder fragmentOffset(Integer fragmentOffset);

	public FieldConfigBuilder fragmentSize(Integer fragmentSize);

	public FieldConfigBuilder fragmenter(String fragmenter);

	public FieldConfigBuilder highlightFilter(Boolean highlightFilter);

	public FieldConfigBuilder highlightQuery(Query highlightQuery);

	public FieldConfigBuilder highlighterType(String highlighterType);

	public FieldConfigBuilder matchedFields(String... matchedFields);

	public FieldConfigBuilder noMatchSize(Integer noMatchSize);

	public FieldConfigBuilder numFragments(Integer numFragments);

	public FieldConfigBuilder order(String order);

	public FieldConfigBuilder phraseLimit(Integer phraseLimit);

	public FieldConfigBuilder postTags(String... postTags);

	public FieldConfigBuilder preTags(String... preTags);

	public FieldConfigBuilder requireFieldMatch(Boolean requireFieldMatch);

}