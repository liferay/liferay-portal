/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.tuning.rankings.index.Ranking;

import java.util.List;

/**
 * @author Bryan Engler
 */
public class RankingEntryDisplayContextBuilder {

	public RankingEntryDisplayContextBuilder(Ranking ranking) {
		_ranking = ranking;
	}

	public RankingEntryDisplayContext build() {
		RankingEntryDisplayContext rankingEntryDisplayContext =
			new RankingEntryDisplayContext();

		_setAliases(rankingEntryDisplayContext);
		_setGroupExternalReferenceCode(rankingEntryDisplayContext);
		_setHiddenResultsCount(rankingEntryDisplayContext);
		_setIndex(rankingEntryDisplayContext);
		_setNameForDisplay(rankingEntryDisplayContext);
		_setPinnedResultsCount(rankingEntryDisplayContext);
		_setStatus(rankingEntryDisplayContext);
		_setSXPBlueprintExternalReferenceCode(rankingEntryDisplayContext);
		_setSXPBlueprintTitle(rankingEntryDisplayContext);
		_setUid(rankingEntryDisplayContext);

		return rankingEntryDisplayContext;
	}

	public RankingEntryDisplayContextBuilder sxpBlueprintTitle(
		String sxpBlueprintTitle) {

		_sxpBlueprintTitle = sxpBlueprintTitle;

		return this;
	}

	private String _getSizeString(List<?> list) {
		return String.valueOf(list.size());
	}

	private void _setAliases(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setAliases(
			StringUtil.merge(
				_ranking.getAliases(), StringPool.COMMA_AND_SPACE));
	}

	private void _setGroupExternalReferenceCode(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setGroupExternalReferenceCode(
			_ranking.getGroupExternalReferenceCode());
	}

	private void _setHiddenResultsCount(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setHiddenResultsCount(
			_getSizeString(_ranking.getHiddenDocumentIds()));
	}

	private void _setIndex(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setIndex(_ranking.getIndexName());
	}

	private void _setNameForDisplay(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setKeywords(_ranking.getNameForDisplay());
	}

	private void _setPinnedResultsCount(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setPinnedResultsCount(
			_getSizeString(_ranking.getPins()));
	}

	private void _setSXPBlueprintExternalReferenceCode(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setSXPBlueprintExternalReferenceCode(
			_ranking.getSXPBlueprintExternalReferenceCode());
	}

	private void _setSXPBlueprintTitle(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setSXPBlueprintTitle(_sxpBlueprintTitle);
	}

	private void _setStatus(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setStatus(_ranking.getStatus());
	}

	private void _setUid(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setUid(_ranking.getRankingDocumentId());
	}

	private final Ranking _ranking;
	private String _sxpBlueprintTitle;

}