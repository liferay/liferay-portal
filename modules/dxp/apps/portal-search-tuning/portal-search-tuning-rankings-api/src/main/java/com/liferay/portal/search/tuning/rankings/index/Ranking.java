/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.index;

import java.util.Collection;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Bryan Engler
 */
@ProviderType
public interface Ranking {

	public List<String> getAliases();

	public String getGroupExternalReferenceCode();

	public List<String> getHiddenDocumentIds();

	public String getIndexName();

	public String getName();

	public String getNameForDisplay();

	public List<Ranking.Pin> getPins();

	public String getQueryString();

	public Collection<String> getQueryStrings();

	public String getRankingDocumentId();

	public String getSXPBlueprintExternalReferenceCode();

	public String getStatus();

	public boolean isPinned(String documentId);

	public interface Builder {

		public Builder aliases(List<String> aliases);

		public Ranking build();

		public Builder groupExternalReferenceCode(
			String groupExternalReferenceCode);

		public Builder hiddenDocumentIds(List<String> hiddenDocumentIds);

		public Builder indexName(String indexName);

		public Builder name(String name);

		public Builder pins(List<Ranking.Pin> pins);

		public Builder queryString(String queryString);

		public Builder rankingDocumentId(String rankingDocumentId);

		public Builder status(String status);

		public Builder sxpBlueprintExternalReferenceCode(
			String sxpBlueprintExternalReferenceCode);

	}

	public interface Pin {

		public String getDocumentId();

		public int getPosition();

		public interface Builder {

			public Pin build();

			public Builder documentId(String documentId);

			public Builder position(int position);

		}

	}

}