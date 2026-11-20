/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.details;

import com.liferay.launch.set.details.LaunchSetDetails;
import com.liferay.launch.set.details.provider.LaunchSetDetailsProvider;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LaunchSetDetailsProvider.class)
public class LaunchSetDetailsProviderImpl implements LaunchSetDetailsProvider {

	@Override
	public LaunchSetDetails fetchLaunchSetDetails(long launchSetId) {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			launchSetId);

		if (objectEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if ((objectDefinition == null) ||
			!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_LAUNCH_SET")) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"The object entry " + launchSetId + " is not a launch set");
			}

			return null;
		}

		return new LaunchSetDetails(
			launchSetId, MapUtil.getString(objectEntry.getValues(), "name"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetDetailsProviderImpl.class);

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}