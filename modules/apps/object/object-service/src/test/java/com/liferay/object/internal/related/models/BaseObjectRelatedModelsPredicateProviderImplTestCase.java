/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.petra.string.StringBundler;

import org.junit.Assert;

import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public abstract class BaseObjectRelatedModelsPredicateProviderImplTestCase {

	protected void assertPredicateString(
		String operator, String predicateString) {

		Assert.assertTrue(
			predicateString,
			predicateString.contains("ObjectEntry.companyId = ?"));
		Assert.assertTrue(
			predicateString,
			predicateString.contains("ObjectEntry.externalReferenceCode = ?"));
		Assert.assertTrue(
			predicateString,
			predicateString.contains("ObjectEntry.groupId " + operator));
		Assert.assertTrue(
			predicateString,
			predicateString.contains("ObjectEntry.objectDefinitionId = ?"));
	}

	protected ObjectDefinition mockObjectDefinition(
		long companyId, long objectDefinitionId, String objectDefinitionName,
		String scope) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		String dbTableName = StringBundler.concat(
			"O_", companyId, "_", objectDefinitionName);

		Mockito.when(
			objectDefinition.getDBTableName()
		).thenReturn(
			dbTableName
		);

		Mockito.when(
			objectDefinition.getExtensionDBTableName()
		).thenReturn(
			dbTableName + "x"
		);

		Mockito.when(
			objectDefinition.getLocalizationDBTableName()
		).thenReturn(
			dbTableName + "_l"
		);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		String pkObjectFieldName = "c_" + objectDefinitionName + "Id";

		Mockito.when(
			objectDefinition.getPKObjectFieldDBColumnName()
		).thenReturn(
			pkObjectFieldName + "_"
		);

		Mockito.when(
			objectDefinition.getPKObjectFieldName()
		).thenReturn(
			pkObjectFieldName
		);

		Mockito.when(
			objectDefinition.getScope()
		).thenReturn(
			scope
		);

		return objectDefinition;
	}

	protected ObjectRelationship mockObjectRelationship(
		long objectDefinitionId1, long objectDefinitionId2,
		String objectRelationshipName) {

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.getDBTableName()
		).thenReturn(
			"R_" + objectRelationshipName
		);

		Mockito.when(
			objectRelationship.getName()
		).thenReturn(
			objectRelationshipName
		);

		Mockito.when(
			objectRelationship.getObjectDefinitionId1()
		).thenReturn(
			objectDefinitionId1
		);

		Mockito.when(
			objectRelationship.getObjectDefinitionId2()
		).thenReturn(
			objectDefinitionId2
		);

		Mockito.when(
			objectRelationship.isReverse()
		).thenReturn(
			false
		);

		return objectRelationship;
	}

}