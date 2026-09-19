/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.highlight;

import com.liferay.portal.search.query.Query;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 * @author André de Oliveira
 */
@ProviderType
public interface Highlight {

	public char[] getBoundaryChars();

	public Integer getBoundaryMaxScan();

	public String getBoundaryScannerLocale();

	public String getBoundaryScannerType();

	public String getEncoder();

	public List<FieldConfig> getFieldConfigs();

	public Boolean getForceSource();

	public Integer getFragmentSize();

	public String getFragmenter();

	public Boolean getHighlightFilter();

	public Query getHighlightQuery();

	public String getHighlighterType();

	public Integer getNoMatchSize();

	public Integer getNumOfFragments();

	public String getOrder();

	public Integer getPhraseLimit();

	public String[] getPostTags();

	public String[] getPreTags();

	public Boolean getRequireFieldMatch();

	public String getTagsSchema();

	public Boolean getUseExplicitFieldOrder();

}