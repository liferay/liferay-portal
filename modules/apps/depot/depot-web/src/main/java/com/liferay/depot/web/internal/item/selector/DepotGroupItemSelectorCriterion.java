/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;

/**
 * @author Cristina González
 * @author Roberto Díaz
 */
public class DepotGroupItemSelectorCriterion
	extends GroupItemSelectorCriterion {

	public int getDepotEntryType() {
		return _depotEntryType;
	}

	public void setDepotEntryType(int depotEntryType) {
		_depotEntryType = depotEntryType;
	}

	private int _depotEntryType = DepotConstants.TYPE_ANY;

}