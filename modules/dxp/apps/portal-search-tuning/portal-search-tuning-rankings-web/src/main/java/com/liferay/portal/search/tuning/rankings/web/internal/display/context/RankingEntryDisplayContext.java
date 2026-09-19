/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import java.util.Date;

/**
 * @author Wade Cao
 */
public class RankingEntryDisplayContext {

	public String getAliases() {
		return _aliases;
	}

	public Date getDisplayDate() {
		return null;
	}

	public String getGroupExternalReferenceCode() {
		return _groupExternalReferenceCode;
	}

	public String getHiddenResultsCount() {
		return _hiddenResultsCount;
	}

	public String getIndex() {
		return _index;
	}

	public String getKeywords() {
		return _keywords;
	}

	public Date getModifiedDate() {
		return null;
	}

	public String getPinnedResultsCount() {
		return _pinnedResultsCount;
	}

	public String getSXPBlueprintExternalReferenceCode() {
		return _sxpBlueprintExternalReferenceCode;
	}

	public String getSXPBlueprintTitle() {
		return _sxpBlueprintTitle;
	}

	public String getStatus() {
		return _status;
	}

	public String getUid() {
		return _uid;
	}

	public void setAliases(String aliases) {
		_aliases = aliases;
	}

	public void setGroupExternalReferenceCode(
		String groupExternalReferenceCode) {

		_groupExternalReferenceCode = groupExternalReferenceCode;
	}

	public void setHiddenResultsCount(String hiddenResultsCount) {
		_hiddenResultsCount = hiddenResultsCount;
	}

	public void setIndex(String index) {
		_index = index;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setPinnedResultsCount(String pinnedResultsCount) {
		_pinnedResultsCount = pinnedResultsCount;
	}

	public void setSXPBlueprintExternalReferenceCode(
		String sxpBlueprintExternalReferenceCode) {

		_sxpBlueprintExternalReferenceCode = sxpBlueprintExternalReferenceCode;
	}

	public void setSXPBlueprintTitle(String sxpBlueprintTitle) {
		_sxpBlueprintTitle = sxpBlueprintTitle;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public void setUid(String uid) {
		_uid = uid;
	}

	private String _aliases;
	private String _groupExternalReferenceCode;
	private String _hiddenResultsCount;
	private String _index;
	private String _keywords;
	private String _pinnedResultsCount;
	private String _status;
	private String _sxpBlueprintExternalReferenceCode;
	private String _sxpBlueprintTitle;
	private String _uid;

}