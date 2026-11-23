/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public abstract class BaseObjectRelatedModelsPredicateProviderImplTestCase {

	@Test
	public void testGetPredicate() throws Exception {

		// Company scope

		long companyId = RandomTestUtil.randomLong();
		long objectDefinitionId = RandomTestUtil.randomLong();
		String objectDefinitionName = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition = mockObjectDefinition(
			companyId, objectDefinitionId, objectDefinitionName,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		String objectRelationshipName = RandomTestUtil.randomString();

		ObjectFieldLocalService objectFieldLocalService =
			mockObjectFieldLocalService(
				objectDefinitionId,
				mockObjectField(
					ObjectRelationshipUtil.getObjectRelationshipFieldName(
						objectDefinition, objectRelationshipName),
					ObjectFieldConstants.DB_TYPE_LONG, false));

		ObjectRelationship objectRelationship = mockObjectRelationship(
			objectDefinitionId, objectDefinitionId, objectRelationshipName);

		assertPredicateString(
			"= ?",
			getPredicateString(
				new Long[] {RandomTestUtil.randomLong()}, objectDefinition,
				objectFieldLocalService, objectRelationship));

		// Depot scope with connected depot entries

		assertPredicateString(
			"in (?, ?, ?)",
			getPredicateString(
				new Long[] {
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomLong()
				},
				mockObjectDefinition(
					companyId, objectDefinitionId, objectDefinitionName,
					ObjectDefinitionConstants.SCOPE_DEPOT),
				objectFieldLocalService, objectRelationship));

		// Site scope

		assertPredicateString(
			"in (?)",
			getPredicateString(
				new Long[] {RandomTestUtil.randomLong()},
				mockObjectDefinition(
					companyId, objectDefinitionId, objectDefinitionName,
					ObjectDefinitionConstants.SCOPE_SITE),
				objectFieldLocalService, objectRelationship));
	}

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

	protected abstract String getPredicateString(
			Long[] groupIds, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationship objectRelationship)
		throws Exception;

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

	protected ObjectField mockObjectField(
		String dbColumnName, String dbType, boolean localized) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)
		).thenReturn(
			false
		);

		Mockito.when(
			objectField.getDBColumnNames()
		).thenReturn(
			new String[] {dbColumnName}
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			objectField.hasInsertValues()
		).thenReturn(
			true
		);

		Mockito.when(
			objectField.isLocalized()
		).thenReturn(
			localized
		);

		return objectField;
	}

	protected ObjectFieldLocalService mockObjectFieldLocalService(
		long objectDefinitionId, ObjectField objectField) {

		ObjectFieldLocalService objectFieldLocalService = Mockito.mock(
			ObjectFieldLocalService.class);

		Mockito.when(
			objectFieldLocalService.getLocalizedObjectFields(Mockito.anyLong())
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			objectFieldLocalService.getObjectFields(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Collections.emptyList()
		);

		if (objectField.isLocalized()) {
			Mockito.when(
				objectFieldLocalService.getLocalizedObjectFields(
					objectDefinitionId)
			).thenReturn(
				Collections.singletonList(objectField)
			);
		}
		else {
			Mockito.when(
				objectFieldLocalService.getObjectFields(
					Mockito.eq(objectDefinitionId), Mockito.anyString())
			).thenReturn(
				Collections.singletonList(objectField)
			);
		}

		return objectFieldLocalService;
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