/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * @author Brian Wing Shun Chan
 */
public class NullServletOutputStream extends ServletOutputStream {

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
	}

	@Override
	public void write(int b) {
	}

}