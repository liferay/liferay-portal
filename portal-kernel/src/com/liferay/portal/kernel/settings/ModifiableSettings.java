/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Collection;

/**
 * @author Iván Zaera
 */
public interface ModifiableSettings extends Settings {

	public Collection<String> getModifiedKeys();

	public void reset();

	public void reset(String key);

	public ModifiableSettings setValue(String key, String value);

	public ModifiableSettings setValues(ModifiableSettings modifiableSettings);

	public ModifiableSettings setValues(String key, String[] values);

	public void store() throws IOException, ValidatorException;

}