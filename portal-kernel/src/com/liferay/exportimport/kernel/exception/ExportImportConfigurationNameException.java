/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class ExportImportConfigurationNameException extends PortalException {

	public static class MustNotExceedMaximumLength
		extends ExportImportConfigurationNameException {

		public MustNotExceedMaximumLength(String name, int nameMaxLength) {
			super(
				StringBundler.concat(
					"Name ", name, " must have fewer than ", nameMaxLength,
					" characters"));

			this.name = name;
			this.nameMaxLength = nameMaxLength;
		}

		public final String name;
		public final int nameMaxLength;

	}

	private ExportImportConfigurationNameException() {
	}

	private ExportImportConfigurationNameException(String msg) {
		super(msg);
	}

	private ExportImportConfigurationNameException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	private ExportImportConfigurationNameException(Throwable throwable) {
		super(throwable);
	}

}