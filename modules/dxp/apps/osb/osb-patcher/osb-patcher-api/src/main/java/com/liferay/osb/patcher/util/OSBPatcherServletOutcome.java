/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

/**
 * @author Zsolt Balogh
 */
public class OSBPatcherServletOutcome {

	public static final int STATUS_CONFLICT = -1;

	public static final int STATUS_EXCEPTION = -2;

	public static final int STATUS_SUCCESS = 0;

	public String getResult() {
		return result;
	}

	public int getStatus() {
		return status;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	protected String result;
	protected int status;

}