/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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
			mockObjectFieldLocalService(
				objectDefinitionId2,
				mockObjectField(
					"name_", ObjectFieldConstants.DB_TYPE_STRING, true)),
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

	@Override
	protected String getPredicateString(
			Long[] groupIds, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationship objectRelationship)
		throws Exception {

		return _getPredicateString(
			groupIds, objectDefinition, objectFieldLocalService,
			objectRelationship, objectDefinition);
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

}