/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutPageTemplateCollectionNameException extends PortalException {

	public static class MustNotBeNull
		extends LayoutPageTemplateCollectionNameException {

		public MustNotBeNull() {
			super("Name must not be null");
		}

	}

	public static class MustNotExceedMaximumSize
		extends LayoutPageTemplateCollectionNameException {

		public MustNotExceedMaximumSize(int nameMaxLength) {
			super("Maximum length of name exceeded " + nameMaxLength);
		}

	}

	private LayoutPageTemplateCollectionNameException(String msg) {
		super(msg);
	}

}