/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Adolfo Pérez
 */
public class SystemCategoryException extends PortalException {

	public static class MustNotDelete extends SystemCategoryException {

		public MustNotDelete(long categoryId) {
			super(String.format("Category %s cannot be deleted", categoryId));

			this.categoryId = categoryId;
		}

		public long categoryId;

	}

	public static class MustNotModify extends SystemCategoryException {

		public MustNotModify(long categoryId) {
			super(String.format("Category %s cannot be modified", categoryId));

			this.categoryId = categoryId;
		}

		public long categoryId;

	}

	public static class MustNotRename extends SystemCategoryException {

		public MustNotRename(long categoryId) {
			super(String.format("Category %s cannot be renamed", categoryId));

			this.categoryId = categoryId;
		}

		public long categoryId;

	}

	private SystemCategoryException(String msg) {
		super(msg);
	}

}