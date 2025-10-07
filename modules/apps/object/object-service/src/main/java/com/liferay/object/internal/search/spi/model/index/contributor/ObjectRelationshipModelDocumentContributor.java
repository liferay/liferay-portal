/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gleice Lisbino
 */
@Component(
	property = "indexer.class.name=com.liferay.object.model.ObjectRelationship",
	service = ModelDocumentContributor.class
)
public class ObjectRelationshipModelDocumentContributor
	implements ModelDocumentContributor<ObjectRelationship> {

	@Override
	public void contribute(
		Document document, ObjectRelationship objectRelationship) {

		document.addText(Field.NAME, objectRelationship.getName());
		document.addKeyword("edge", objectRelationship.isEdge());
		document.addLocalizedKeyword(
			"localized_label", objectRelationship.getLabelMap(), true, true);
		document.addKeyword(
			"objectDefinitionId", objectRelationship.getObjectDefinitionId1());
		document.addKeyword(
			"objectRelationshipId",
			objectRelationship.getObjectRelationshipId());
		document.addKeyword("system", objectRelationship.isSystem());

		document.remove(Field.USER_NAME);
	}

}