/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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