/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search.spi.model.index.contributor;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roselaine Marques
 */
@Component(
	property = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection",
	service = ModelDocumentContributor.class
)
public class LayoutPageTemplateCollectionModelDocumentContributor
	implements ModelDocumentContributor<LayoutPageTemplateCollection> {

	@Override
	public void contribute(
		Document document,
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		document.addText(
			Field.DESCRIPTION, layoutPageTemplateCollection.getDescription());
		document.addText(Field.NAME, layoutPageTemplateCollection.getName());
		document.addKeyword(Field.TYPE, layoutPageTemplateCollection.getType());
	}

}