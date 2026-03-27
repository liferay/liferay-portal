/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector;

import com.liferay.item.selector.BaseItemSelectorCriterion;

/**
 * @author Alessio Antonio Rendina
 */
public class OrganizationItemSelectorCriterion
	extends BaseItemSelectorCriterion {

	public String getSelectedOrganizationIds() {
		return _selectedOrganizationIds;
	}

	public boolean isMultiSelection() {
		return _multiSelection;
	}

	public void setMultiSelection(boolean multiSelection) {
		_multiSelection = multiSelection;
	}

	public void setSelectedOrganizationIds(String selectedOrganizationIds) {
		_selectedOrganizationIds = selectedOrganizationIds;
	}

	private boolean _multiSelection;
	private String _selectedOrganizationIds;

}