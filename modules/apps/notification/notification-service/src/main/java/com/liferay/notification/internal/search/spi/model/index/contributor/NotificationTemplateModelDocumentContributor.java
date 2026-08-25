/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.search.spi.model.index.contributor;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gustavo Lima
 */
@Component(
	property = "indexer.class.name=com.liferay.notification.model.NotificationTemplate",
	service = ModelDocumentContributor.class
)
public class NotificationTemplateModelDocumentContributor
	implements ModelDocumentContributor<NotificationTemplate> {

	@Override
	public void contribute(
		Document document, NotificationTemplate notificationTemplate) {

		document.addLocalizedText(
			Field.NAME, notificationTemplate.getNameMap());
		document.addLocalizedKeyword(
			Field.getSortableFieldName(Field.NAME),
			notificationTemplate.getNameMap(), true);
		document.addKeyword(
			"objectDefinitionId", notificationTemplate.getObjectDefinitionId());
		document.addKeyword("system", notificationTemplate.isSystem());
		document.remove(Field.USER_NAME);
	}

}