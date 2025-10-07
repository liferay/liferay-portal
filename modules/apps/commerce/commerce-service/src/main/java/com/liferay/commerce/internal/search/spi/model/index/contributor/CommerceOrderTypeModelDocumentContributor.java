/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.index.contributor;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.model.CommerceOrderType",
	service = ModelDocumentContributor.class
)
public class CommerceOrderTypeModelDocumentContributor
	implements ModelDocumentContributor<CommerceOrderType> {

	@Override
	public void contribute(
		Document document, CommerceOrderType commerceOrderType) {

		document.addText(Field.NAME, commerceOrderType.getName());
		document.addDate("displayDate", commerceOrderType.getDisplayDate());
		document.addDateSortable(
			"displayDate", commerceOrderType.getDisplayDate());
		document.addDate(
			"expirationDate", commerceOrderType.getExpirationDate());
		document.addDateSortable(
			"expirationDate", commerceOrderType.getExpirationDate());
	}

}