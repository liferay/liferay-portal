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

	public int getType() {
		return _type;
	}

	public static class MustNotBeNull
		extends LayoutPageTemplateCollectionNameException {

		public MustNotBeNull(int type) {
			super("Name must not be null", type);
		}

	}

	public static class MustNotExceedMaximumSize
		extends LayoutPageTemplateCollectionNameException {

		public MustNotExceedMaximumSize(int nameMaxLength, int type) {
			super("Maximum length of name exceeded " + nameMaxLength, type);

			_nameMaxLength = nameMaxLength;
		}

		public int getNameMaxLength() {
			return _nameMaxLength;
		}

		private final int _nameMaxLength;

	}

	private LayoutPageTemplateCollectionNameException(String msg, int type) {
		super(msg);

		_type = type;
	}

	private final int _type;

}