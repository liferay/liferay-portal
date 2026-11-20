/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Objects;

/**
 * @author Carlos Correa
 */
public class LaunchSetUtil {

	public static ObjectEntry fetchLaunchSetObjectEntry(long launchSetId) {
		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			launchSetId);

		if (objectEntry == null) {
			return null;
		}

		if (!_isLaunchSet(objectEntry)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Object entry " + launchSetId + " is not a launch set");
			}

			return null;
		}

		return objectEntry;
	}

	public static ObjectEntry getLaunchSetObjectEntry(long launchSetId)
		throws PortalException {

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(
			launchSetId);

		if (!_isLaunchSet(objectEntry)) {
			throw new PortalException(
				"Object entry " + launchSetId + " is not a launch set");
		}

		return objectEntry;
	}

	private static boolean _isLaunchSet(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (objectDefinition == null) {
			return false;
		}

		return Objects.equals(
			objectDefinition.getExternalReferenceCode(), "L_LAUNCH_SET");
	}

	private static final Log _log = LogFactoryUtil.getLog(LaunchSetUtil.class);

}