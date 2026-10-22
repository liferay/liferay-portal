/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

/**
 * @author Iván Zaera
 */
public abstract class BaseModifiableSettings
	extends BaseSettings implements ModifiableSettings {

	public BaseModifiableSettings() {
	}

	public BaseModifiableSettings(Settings parentSettings) {
		super(parentSettings);
	}

}