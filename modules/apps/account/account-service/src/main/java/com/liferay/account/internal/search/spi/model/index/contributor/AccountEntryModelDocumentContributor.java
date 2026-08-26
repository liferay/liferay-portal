/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.search.spi.model.index.contributor;

import com.liferay.account.model.AccountEntry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	property = "indexer.class.name=com.liferay.account.model.AccountEntry",
	service = ModelDocumentContributor.class
)
public class AccountEntryModelDocumentContributor
	implements ModelDocumentContributor<AccountEntry> {

	@Override
	public void contribute(Document document, AccountEntry accountEntry) {
		document.setSortableTextFields(
			ArrayUtil.append(
				PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS),
				"externalReferenceCode"));

		AccountGroupAccountEntryDocumentContributorUtil.contribute(
			document, accountEntry);
		OrganizationAccountEntryDocumentContributorUtil.contribute(
			document, accountEntry);
		UserAccountEntryDocumentContributorUtil.contribute(
			document, accountEntry);

		document.addText(Field.DESCRIPTION, accountEntry.getDescription());
		document.addText(Field.NAME, accountEntry.getName());
		document.addKeyword(Field.STATUS, accountEntry.getStatus());
		document.addKeyword("accountEntryId", accountEntry.getAccountEntryId());
		document.addKeyword("domains", _getDomains(accountEntry));
		document.addKeyword(
			"externalReferenceCode", accountEntry.getExternalReferenceCode());
		document.addKeyword(
			"parentAccountEntryId", accountEntry.getParentAccountEntryId());
		document.addText("taxIdNumber", accountEntry.getTaxIdNumber());
		document.remove(Field.USER_NAME);
	}

	private String[] _getDomains(AccountEntry accountEntry) {
		return ArrayUtil.toStringArray(
			StringUtil.split(accountEntry.getDomains(), CharPool.COMMA));
	}

}