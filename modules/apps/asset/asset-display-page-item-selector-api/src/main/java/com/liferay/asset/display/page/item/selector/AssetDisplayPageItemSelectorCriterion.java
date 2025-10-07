/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector;

import com.liferay.item.selector.BaseItemSelectorCriterion;

/**
 * @author Jürgen Kappler
 */
public class AssetDisplayPageItemSelectorCriterion
	extends BaseItemSelectorCriterion {

	public AssetDisplayPageItemSelectorCriterion() {
		_classNameId = 0;
		_classTypeId = 0;
	}

	public long getClassNameId() {
		return _classNameId;
	}

	public long getClassTypeId() {
		return _classTypeId;
	}

	public void setClassNameId(long classNameId) {
		_classNameId = classNameId;
	}

	public void setClassTypeId(long classTypeId) {
		_classTypeId = classTypeId;
	}

	private long _classNameId;
	private long _classTypeId;

}