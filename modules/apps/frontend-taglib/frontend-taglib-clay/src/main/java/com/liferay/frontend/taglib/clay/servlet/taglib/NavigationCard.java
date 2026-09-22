/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

/**
 * @author Marko Cikos
 */
public interface NavigationCard extends BaseClayCard {

	public default String getCardType() {
		return null;
	}

	public default String getDescription() {
		return null;
	}

	public default String getIcon() {
		return null;
	}

	public default String getImageAlt() {
		return null;
	}

	public default String getImageSrc() {
		return null;
	}

	public String getTitle();

	public default Boolean isSmall() {
		return null;
	}

}