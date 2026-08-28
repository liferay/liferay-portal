/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

/**
 * @author Mario Leandro
 */
public class PanelAppNavigationItem {

	public PanelAppNavigationItem(
		String canonicalName, String href, String label) {

		this(canonicalName, href, label, null);
	}

	public PanelAppNavigationItem(
		String canonicalName, String href, String label, String parentLabel) {

		_canonicalName = canonicalName;
		_href = href;
		_label = label;
		_parentLabel = parentLabel;
	}

	public String getCanonicalName() {
		return _canonicalName;
	}

	public String getHref() {
		return _href;
	}

	public String getLabel() {
		return _label;
	}

	public String getParentLabel() {
		return _parentLabel;
	}

	private final String _canonicalName;
	private final String _href;
	private final String _label;
	private final String _parentLabel;

}