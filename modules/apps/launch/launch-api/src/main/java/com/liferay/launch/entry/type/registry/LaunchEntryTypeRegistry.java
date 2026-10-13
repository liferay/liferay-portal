/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.entry.type.registry;

import com.liferay.launch.entry.type.LaunchEntryType;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alejandro Tardín
 */
@ProviderType
public interface LaunchEntryTypeRegistry {

	public LaunchEntryType getLaunchEntryType(String className);

	public List<LaunchEntryType> getLaunchEntryTypes();

}