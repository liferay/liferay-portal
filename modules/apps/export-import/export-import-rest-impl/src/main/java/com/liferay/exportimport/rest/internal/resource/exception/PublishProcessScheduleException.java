/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Magdalena Jedraszak
 */
public class PublishProcessScheduleException extends PortalException {

	public static class MustBeValid extends PublishProcessScheduleException {

		public MustBeValid() {
			super("The publication schedule is invalid");
		}

	}

	public static class MustRunAtLeastOnce
		extends PublishProcessScheduleException {

		public MustRunAtLeastOnce() {
			super("The publication schedule never runs");
		}

	}

	private PublishProcessScheduleException(String msg) {
		super(msg);
	}

}