/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.constants;

/**
 * @author Danny Situ
 */
public class DSRequestConstants {

	public static final String STATUS_COMPLETED = "completed";

	public static final String STATUS_CREATED = "created";

	public static final String STATUS_DECLINED = "declined";

	public static final String STATUS_SENT = "sent";

	public static final String STATUS_VOIDED = "voided";

	public static final String[] STATUSES = {
		STATUS_COMPLETED, STATUS_CREATED, STATUS_DECLINED, STATUS_SENT,
		STATUS_VOIDED
	};

}