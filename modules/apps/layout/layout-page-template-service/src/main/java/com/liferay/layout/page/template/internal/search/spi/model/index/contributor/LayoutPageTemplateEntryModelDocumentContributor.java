/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search.spi.model.index.contributor;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Juan Pablo Montero
 */
@Component(
	property = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry",
	service = ModelDocumentContributor.class
)
public class LayoutPageTemplateEntryModelDocumentContributor
	implements ModelDocumentContributor<LayoutPageTemplateEntry> {

	@Override
	public void contribute(
		Document document, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		document.addText(Field.NAME, layoutPageTemplateEntry.getName());
		document.addNumber(Field.STATUS, layoutPageTemplateEntry.getStatus());
		document.addKeyword(Field.TYPE, layoutPageTemplateEntry.getType());
	}

}