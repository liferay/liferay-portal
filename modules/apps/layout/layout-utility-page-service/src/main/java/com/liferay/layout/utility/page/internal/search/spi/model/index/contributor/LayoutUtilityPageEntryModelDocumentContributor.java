/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.search.spi.model.index.contributor;

import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Juan Pablo Montero
 */
@Component(
	property = "indexer.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry",
	service = ModelDocumentContributor.class
)
public class LayoutUtilityPageEntryModelDocumentContributor
	implements ModelDocumentContributor<LayoutUtilityPageEntry> {

	@Override
	public void contribute(
		Document document, LayoutUtilityPageEntry layoutUtilityPageEntry) {

		document.addText(Field.NAME, layoutUtilityPageEntry.getName());
		document.addKeyword(Field.TYPE, layoutUtilityPageEntry.getType());
	}

}