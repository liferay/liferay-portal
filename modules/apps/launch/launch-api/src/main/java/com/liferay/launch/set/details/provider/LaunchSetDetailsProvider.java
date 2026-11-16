/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.details.provider;

import com.liferay.launch.set.details.LaunchSetDetails;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 */
@ProviderType
public interface LaunchSetDetailsProvider {

	public LaunchSetDetails fetchLaunchSetDetails(long launchSetId);

}