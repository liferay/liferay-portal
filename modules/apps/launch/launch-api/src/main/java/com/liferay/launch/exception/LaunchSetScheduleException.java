/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Jose Luis Navarro
 */
public class LaunchSetScheduleException extends PortalException {

	public static class MustBeOpen extends LaunchSetScheduleException {

		public MustBeOpen(long launchSetId) {
			super(
				String.format(
					"Launch set %s must be open to be scheduled", launchSetId));

			this.launchSetId = launchSetId;
		}

		public long launchSetId;

	}

	public static class MustHaveFutureDate extends LaunchSetScheduleException {

		public MustHaveFutureDate(long launchSetId) {
			super(
				String.format(
					"Launch set %s must be scheduled for a future date",
					launchSetId));

			this.launchSetId = launchSetId;
		}

		public long launchSetId;

	}

	private LaunchSetScheduleException(String msg) {
		super(msg);
	}

}