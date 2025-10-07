/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.provider;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.provider.BaseInfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.journal.model.JournalArticle;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=10",
		"item.class.name=com.liferay.journal.model.JournalArticle"
	},
	service = InfoItemDetailsProvider.class
)
public class JournalArticleInfoItemDetailsProvider
	extends BaseInfoItemDetailsProvider<JournalArticle> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(JournalArticle.class.getName());
	}

	@Override
	protected InfoItemIdentifierFactory<JournalArticle>
		getInfoItemIdentifierFactory() {

		return new InfoItemIdentifierFactory<>() {

			@Override
			public ClassPKInfoItemIdentifier createClassPKInfoItemIdentifier(
				JournalArticle journalArticle) {

				return new ClassPKInfoItemIdentifier(
					journalArticle.getResourcePrimKey());
			}

			@Override
			public ERCInfoItemIdentifier createERCInfoItemIdentifier(
				String externalReferenceCode,
				String scopeExternalReferenceCode) {

				return new ERCInfoItemIdentifier(
					externalReferenceCode, scopeExternalReferenceCode);
			}

			@Override
			public GroupKeyInfoItemIdentifier createGroupKeyInfoItemIdentifier(
				long groupId, JournalArticle journalArticle) {

				return new GroupKeyInfoItemIdentifier(
					journalArticle.getGroupId(), journalArticle.getArticleId());
			}

			@Override
			public GroupUrlTitleInfoItemIdentifier
				createGroupUrlTitleInfoItemIdentifier(
					long groupId, JournalArticle journalArticle) {

				return new GroupUrlTitleInfoItemIdentifier(
					journalArticle.getGroupId(), journalArticle.getUrlTitle());
			}

		};
	}

}