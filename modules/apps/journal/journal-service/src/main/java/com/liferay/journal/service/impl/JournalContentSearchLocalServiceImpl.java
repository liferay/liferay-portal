/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.service.base.JournalContentSearchLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalContentSearch",
	service = AopService.class
)
public class JournalContentSearchLocalServiceImpl
	extends JournalContentSearchLocalServiceBaseImpl {

	@Override
	public void deleteArticleContentSearch(
		long groupId, boolean privateLayout, long layoutId, String portletId,
		String articleId) {

		JournalContentSearch contentSearch =
			journalContentSearchPersistence.fetchByG_P_L_P_A(
				groupId, privateLayout, layoutId, portletId, articleId);

		if (contentSearch != null) {
			deleteJournalContentSearch(contentSearch);
		}
	}

	@Override
	public List<JournalContentSearch> getArticleContentSearches() {
		return journalContentSearchPersistence.findAll();
	}

	@Override
	public List<Long> getLayoutIds(
		long groupId, boolean privateLayout, String articleId) {

		return TransformUtil.transform(
			journalContentSearchPersistence.findByG_P_A(
				groupId, privateLayout, articleId),
			contentSearch -> contentSearch.getLayoutId());
	}

	@Override
	public int getLayoutIdsCount(
		long groupId, boolean privateLayout, String articleId) {

		return journalContentSearchPersistence.countByG_P_A(
			groupId, privateLayout, articleId);
	}

}