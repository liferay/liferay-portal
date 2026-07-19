/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectEntryMtoMObjectRelatedModelsPredicateProviderImplTest
	extends BaseObjectRelatedModelsPredicateProviderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPredicate() throws Exception {

		// Company scope

		long companyId = RandomTestUtil.randomLong();
		long objectDefinitionId1 = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = mockObjectDefinition(
			companyId, objectDefinitionId1, RandomTestUtil.randomString(),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectFieldLocalService objectFieldLocalService =
			_mockObjectFieldLocalService();

		long objectDefinitionId2 = RandomTestUtil.randomLong();

		ObjectRelationship objectRelationship = mockObjectRelationship(
			objectDefinitionId1, objectDefinitionId2,
			RandomTestUtil.randomString());

		String objectDefinitionName2 = RandomTestUtil.randomString();

		assertPredicateString(
			"= ?",
			_getPredicateString(
				new Long[] {RandomTestUtil.randomLong()}, objectDefinition,
				objectFieldLocalService, objectRelationship,
				mockObjectDefinition(
					companyId, objectDefinitionId2, objectDefinitionName2,
					ObjectDefinitionConstants.SCOPE_COMPANY)));

		// Depot scope with connected depot entries

		assertPredicateString(
			"in (?, ?, ?)",
			_getPredicateString(
				new Long[] {
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomLong()
				},
				objectDefinition, objectFieldLocalService, objectRelationship,
				mockObjectDefinition(
					companyId, objectDefinitionId2, objectDefinitionName2,
					ObjectDefinitionConstants.SCOPE_DEPOT)));

		// Site scope

		assertPredicateString(
			"in (?)",
			_getPredicateString(
				new Long[] {RandomTestUtil.randomLong()}, objectDefinition,
				objectFieldLocalService, objectRelationship,
				mockObjectDefinition(
					companyId, objectDefinitionId2, objectDefinitionName2,
					ObjectDefinitionConstants.SCOPE_SITE)));
	}

	@Test
	public void testGetPredicateWithRelatedLocalizedObjectField()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		long objectDefinitionId1 = RandomTestUtil.randomLong();

		ObjectDefinition objectDefinition = mockObjectDefinition(
			companyId, objectDefinitionId1, RandomTestUtil.randomString(),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		long objectDefinitionId2 = RandomTestUtil.randomLong();

		ObjectDefinition relatedObjectDefinition = mockObjectDefinition(
			companyId, objectDefinitionId2, RandomTestUtil.randomString(),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Locale themeDisplayLocale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

		String predicateString = _getPredicateString(
			new Long[] {RandomTestUtil.randomLong()}, objectDefinition,
			_mockObjectFieldLocalService(objectDefinitionId2),
			mockObjectRelationship(
				objectDefinitionId1, objectDefinitionId2,
				RandomTestUtil.randomString()),
			relatedObjectDefinition);

		LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);

		Assert.assertTrue(
			predicateString,
			predicateString.contains(
				relatedObjectDefinition.getLocalizationDBTableName()));
	}

	private String _getPredicateString(
			Long[] groupIds, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationship objectRelationship,
			ObjectDefinition relatedObjectDefinition)
		throws Exception {

		ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl
			objectEntryMtoMObjectRelatedModelsPredicateProviderImpl =
				new ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl(
					objectDefinition, objectFieldLocalService);

		return String.valueOf(
			objectEntryMtoMObjectRelatedModelsPredicateProviderImpl.
				getPredicate(
					groupIds, objectRelationship,
					ObjectEntryTable.INSTANCE.externalReferenceCode.eq(
						RandomTestUtil.randomString()),
					relatedObjectDefinition));
	}

	private ObjectFieldLocalService _mockObjectFieldLocalService() {
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

		return objectFieldLocalService;
	}

	private ObjectFieldLocalService _mockObjectFieldLocalService(
		long objectDefinitionId) {

		ObjectFieldLocalService objectFieldLocalService =
			_mockObjectFieldLocalService();

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getDBColumnName()
		).thenReturn(
			"name_"
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			ObjectFieldConstants.DB_TYPE_STRING
		);

		Mockito.when(
			objectField.isLocalized()
		).thenReturn(
			true
		);

		Mockito.when(
			objectFieldLocalService.getLocalizedObjectFields(objectDefinitionId)
		).thenReturn(
			Collections.singletonList(objectField)
		);

		return objectFieldLocalService;
	}

}