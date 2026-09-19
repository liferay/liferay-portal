/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.highlight;

import com.liferay.portal.search.query.Query;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public interface FieldConfig {

	public char[] getBoundaryChars();

	public Integer getBoundaryMaxScan();

	public String getBoundaryScannerLocale();

	public String getBoundaryScannerType();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getFieldName()}
	 */
	@Deprecated
	public String getField();

	public String getFieldName();

	public Boolean getForceSource();

	public Integer getFragmentOffset();

	public Integer getFragmentSize();

	public String getFragmenter();

	public Boolean getHighlightFilter();

	public Query getHighlightQuery();

	public String getHighlighterType();

	public String[] getMatchedFields();

	public Integer getNoMatchSize();

	public Integer getNumFragments();

	public String getOrder();

	public Integer getPhraseLimit();

	public String[] getPostTags();

	public String[] getPreTags();

	public Boolean getRequireFieldMatch();

}