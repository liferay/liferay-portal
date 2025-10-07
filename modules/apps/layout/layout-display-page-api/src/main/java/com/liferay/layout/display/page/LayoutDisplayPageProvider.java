/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.display.page;

import com.liferay.info.item.InfoItemReference;

/**
 * @author Jorge Ferrer
 */
public interface LayoutDisplayPageProvider<T> {

	public String getClassName();

	public default String getDefaultURLSeparator() {
		return null;
	}

	public LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(InfoItemReference infoItemReference);

	public default LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		return getLayoutDisplayPageObjectProvider(infoItemReference);
	}

	public LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle);

	public default LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(
			long groupId, String urlTitle, String version) {

		return getLayoutDisplayPageObjectProvider(groupId, urlTitle);
	}

	public default LayoutDisplayPageObjectProvider<T>
		getLayoutDisplayPageObjectProvider(T t) {

		return null;
	}

	public default LayoutDisplayPageObjectProvider<T>
		getParentLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		return null;
	}

	public String getURLSeparator();

	public default boolean inheritable() {
		return false;
	}

}