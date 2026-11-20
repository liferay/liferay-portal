/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.details;

import com.liferay.launch.internal.set.util.LaunchSetUtil;
import com.liferay.launch.set.details.LaunchSetDetails;
import com.liferay.launch.set.details.provider.LaunchSetDetailsProvider;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.util.MapUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LaunchSetDetailsProvider.class)
public class LaunchSetDetailsProviderImpl implements LaunchSetDetailsProvider {

	@Override
	public LaunchSetDetails fetchLaunchSetDetails(long launchSetId) {
		ObjectEntry objectEntry = LaunchSetUtil.fetchLaunchSetObjectEntry(
			launchSetId);

		if (objectEntry == null) {
			return null;
		}

		return new LaunchSetDetails(
			launchSetId, MapUtil.getString(objectEntry.getValues(), "name"));
	}

}