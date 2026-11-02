/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.base.JournalArticleResourceLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticleResource",
	service = AopService.class
)
public class JournalArticleResourceLocalServiceImpl
	extends JournalArticleResourceLocalServiceBaseImpl {

	@Override
	public JournalArticleResource fetchArticleResource(
		long groupId, String articleId) {

		return journalArticleResourcePersistence.fetchByG_A(groupId, articleId);
	}

	@Override
	public JournalArticleResource fetchArticleResource(
		String uuid, long groupId) {

		return journalArticleResourcePersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public JournalArticleResource getArticleResource(
			long articleResourcePrimKey)
		throws PortalException {

		return journalArticleResourcePersistence.findByPrimaryKey(
			articleResourcePrimKey);
	}

	@Override
	public long getArticleResourcePrimKey(long groupId, String articleId) {
		return getArticleResourcePrimKey(null, groupId, articleId);
	}

	@Override
	public long getArticleResourcePrimKey(
		String uuid, long groupId, String articleId) {

		JournalArticleResource articleResource = null;

		if (Validator.isNotNull(uuid)) {
			articleResource = journalArticleResourcePersistence.fetchByUUID_G(
				uuid, groupId);
		}

		if (articleResource == null) {
			articleResource = journalArticleResourcePersistence.fetchByG_A(
				groupId, articleId);
		}

		if (articleResource == null) {
			long articleResourcePrimKey = counterLocalService.increment();

			articleResource = journalArticleResourcePersistence.create(
				articleResourcePrimKey);

			if (Validator.isNotNull(uuid)) {
				articleResource.setUuid(uuid);
			}

			articleResource.setGroupId(groupId);
			articleResource.setArticleId(articleId);

			articleResource = journalArticleResourcePersistence.update(
				articleResource);
		}

		return articleResource.getResourcePrimKey();
	}

	@Override
	public List<JournalArticleResource> getArticleResources(long groupId) {
		return journalArticleResourcePersistence.findByGroupId(groupId);
	}

}