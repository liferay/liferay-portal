/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class
	LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException
		extends PortalException {

	public String getLayoutPageTemplateCollectionKey() {
		return _layoutPageTemplateCollectionKey;
	}

	public int getType() {
		return _type;
	}

	public static class MustNotBeDuplicate
		extends LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException {

		public MustNotBeDuplicate(
			long groupId, String layoutPageTemplateCollectionKey, int type) {

			super(
				StringBundler.concat(
					"Duplicate layout page template collection for group ",
					groupId, " with layout page template collection key ",
					layoutPageTemplateCollectionKey),
				layoutPageTemplateCollectionKey, type);
		}

	}

	public static class MustNotContainInvalidCharacters
		extends LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException {

		public MustNotContainInvalidCharacters(
			String layoutPageTemplateCollectionKey, int type) {

			super(
				StringBundler.concat(
					"Layout page template collection key ",
					layoutPageTemplateCollectionKey,
					" must contain only alphanumeric characters, dashes, and ",
					"underscores"),
				layoutPageTemplateCollectionKey, type);
		}

	}

	public static class MustNotExceedMaximumSize
		extends LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException {

		public MustNotExceedMaximumSize(
			String layoutPageTemplateCollectionKey,
			int layoutPageTemplateCollectionKeyMaxSize, int type) {

			super(
				StringBundler.concat(
					"Layout page template collection key ",
					layoutPageTemplateCollectionKey, " must have fewer than ",
					layoutPageTemplateCollectionKeyMaxSize, " characters"),
				layoutPageTemplateCollectionKey, type);

			_layoutPageTemplateCollectionKeyMaxSize =
				layoutPageTemplateCollectionKeyMaxSize;
		}

		public int getLayoutPageTemplateCollectionKeyMaxSize() {
			return _layoutPageTemplateCollectionKeyMaxSize;
		}

		private final int _layoutPageTemplateCollectionKeyMaxSize;

	}

	private LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException(
		String msg, String layoutPageTemplateCollectionKey, int type) {

		super(msg);

		_layoutPageTemplateCollectionKey = layoutPageTemplateCollectionKey;
		_type = type;
	}

	private final String _layoutPageTemplateCollectionKey;
	private final int _type;

}