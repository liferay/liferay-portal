/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.index.contributor;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;

/**
 * @author Luan Maoski
 */
public class UserModelIndexerWriterContributor
	extends ModelIndexerWriterContributor<User> {

	public UserModelIndexerWriterContributor(
		IndexerDocumentBuilder indexerDocumentBuilder,
		IndexWriterHelper indexWriterHelper,
		UserLocalService userLocalService) {

		super(userLocalService::getIndexableActionableDynamicQuery);

		_indexerDocumentBuilder = indexerDocumentBuilder;
		_indexWriterHelper = indexWriterHelper;
	}

	@Override
	public void customize(
		IndexableActionableDynamicQuery indexableActionableDynamicQuery,
		IndexerDocumentBuilder indexerDocumentBuilder) {

		indexableActionableDynamicQuery.setPerformActionMethod(
			(User user) -> {
				if (!user.isGuestUser()) {
					return indexerDocumentBuilder.getDocument(user);
				}

				return null;
			});
	}

	@Override
	public void modelIndexed(User user) {
		Contact contact = user.fetchContact();

		if (contact == null) {
			return;
		}

		try {
			_indexWriterHelper.updateDocument(
				user.getCompanyId(),
				_indexerDocumentBuilder.getDocument(contact));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private final IndexWriterHelper _indexWriterHelper;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;

}