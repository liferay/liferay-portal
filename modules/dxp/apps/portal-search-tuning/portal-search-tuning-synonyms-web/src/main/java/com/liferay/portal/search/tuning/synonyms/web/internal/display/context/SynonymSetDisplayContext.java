/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;

import java.util.List;

/**
 * @author Filipe Oshiro
 */
public class SynonymSetDisplayContext {

	public String getDisplayedSynonymSet() {
		return _displayedSynonymSet;
	}

	public List<DropdownItem> getDropdownItems() {
		return _dropDownItems;
	}

	public String getEditRenderURL() {
		return _editRenderURL;
	}

	public String getSynonymSet() {
		return _synonymSet;
	}

	public String getSynonymSetId() {
		return _synonymSetId;
	}

	public void setDisplayedSynonymSet(String displayedSynonymSet) {
		_displayedSynonymSet = displayedSynonymSet;
	}

	public void setDropDownItems(List<DropdownItem> dropDownItems) {
		_dropDownItems = dropDownItems;
	}

	public void setEditRenderURL(String editRenderURL) {
		_editRenderURL = editRenderURL;
	}

	public void setSynonymSetId(String synonymSetId) {
		_synonymSetId = synonymSetId;
	}

	public void setSynonyms(String synonyms) {
		_synonymSet = synonyms;
	}

	private String _displayedSynonymSet;
	private List<DropdownItem> _dropDownItems;
	private String _editRenderURL;
	private String _synonymSet;
	private String _synonymSetId;

}