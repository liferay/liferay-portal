/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.item.selector;

import com.liferay.item.selector.BaseItemSelectorCriterion;

/**
 * @author Sergio González
 */
public class LayoutItemSelectorCriterion extends BaseItemSelectorCriterion {

	public LayoutItemSelectorCriterion() {
		_showBreadcrumb = true;
		_showPrivatePages = true;
		_showPublicPages = true;
	}

	public boolean isCheckDisplayPage() {
		return _checkDisplayPage;
	}

	public boolean isEnableCurrentPage() {
		return _enableCurrentPage;
	}

	public boolean isMultiSelection() {
		return _multiSelection;
	}

	public boolean isShowBreadcrumb() {
		return _showBreadcrumb;
	}

	public boolean isShowPrivatePages() {
		return _showPrivatePages;
	}

	public boolean isShowPublicPages() {
		return _showPublicPages;
	}

	public void setCheckDisplayPage(boolean checkDisplayPage) {
		_checkDisplayPage = checkDisplayPage;
	}

	public void setEnableCurrentPage(boolean enableCurrentPage) {
		_enableCurrentPage = enableCurrentPage;
	}

	public void setMultiSelection(boolean multiSelection) {
		_multiSelection = multiSelection;
	}

	public void setShowBreadcrumb(boolean showBreadcrumb) {
		_showBreadcrumb = showBreadcrumb;
	}

	public void setShowPrivatePages(boolean showPrivatePages) {
		_showPrivatePages = showPrivatePages;
	}

	public void setShowPublicPages(boolean showPublicPages) {
		_showPublicPages = showPublicPages;
	}

	private boolean _checkDisplayPage;
	private boolean _enableCurrentPage;
	private boolean _multiSelection;
	private boolean _showBreadcrumb;
	private boolean _showPrivatePages;
	private boolean _showPublicPages;

}