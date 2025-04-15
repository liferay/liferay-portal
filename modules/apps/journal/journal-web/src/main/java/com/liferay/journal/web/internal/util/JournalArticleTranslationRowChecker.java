/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.PortletResponse;

/**
 * @author Pavel Savinov
 */
public class JournalArticleTranslationRowChecker
	extends EmptyOnClickRowChecker {

	public JournalArticleTranslationRowChecker(
		PortletResponse portletResponse) {

		super(portletResponse);
	}

	@Override
	public boolean isChecked(Object object) {
		if (object instanceof JournalArticleTranslation) {
			JournalArticleTranslation articleTranslation =
				(JournalArticleTranslation)object;

			if (articleTranslation.isDefault()) {
				return false;
			}
		}

		return super.isDisabled(object);
	}

	@Override
	public boolean isDisabled(Object object) {
		if (object instanceof JournalArticleTranslation) {
			JournalArticleTranslation articleTranslation =
				(JournalArticleTranslation)object;

			return articleTranslation.isDefault();
		}

		return super.isDisabled(object);
	}

}