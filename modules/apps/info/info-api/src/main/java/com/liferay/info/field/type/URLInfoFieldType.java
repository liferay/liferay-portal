/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.type;

/**
 * @author Jorge Ferrer
 */
public class URLInfoFieldType implements InfoFieldType {

	public static final Attribute<URLInfoFieldType, Boolean> DOWNLOAD =
		new Attribute<>();

	public static final URLInfoFieldType INSTANCE = new URLInfoFieldType();

	public static final Attribute<URLInfoFieldType, Boolean> NOFOLLOW =
		new Attribute<>();

	@Override
	public String getName() {
		return "url";
	}

	private URLInfoFieldType() {
	}

}