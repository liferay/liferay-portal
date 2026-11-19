/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.document.library.repository.external.MinorVersionCapability;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.util.RepositoryUtil;

/**
 * @author Akhash Ramprakash
 */
public class DLRepositoryUtil {

	public static boolean isMinorVersionEnabled(long repositoryId) {
		if (!RepositoryUtil.isExternalRepository(repositoryId)) {
			return true;
		}

		try {
			Repository repository = RepositoryProviderUtil.getRepository(
				repositoryId);

			if (!repository.isCapabilityProvided(
					MinorVersionCapability.class)) {

				return true;
			}

			MinorVersionCapability minorVersionCapability =
				repository.getCapability(MinorVersionCapability.class);

			return minorVersionCapability.isMinorVersionEnabled();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return true;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLRepositoryUtil.class);

}