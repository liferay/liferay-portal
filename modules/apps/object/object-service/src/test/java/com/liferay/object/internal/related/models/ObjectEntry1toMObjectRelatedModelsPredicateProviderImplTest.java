/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectEntry1toMObjectRelatedModelsPredicateProviderImplTest
	extends BaseObjectRelatedModelsPredicateProviderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	protected String getPredicateString(
			Long[] groupIds, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl
			objectEntry1toMObjectRelatedModelsPredicateProviderImpl =
				new ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl(
					objectDefinition, objectFieldLocalService);

		return String.valueOf(
			objectEntry1toMObjectRelatedModelsPredicateProviderImpl.
				getPredicate(
					groupIds, objectRelationship,
					ObjectEntryTable.INSTANCE.externalReferenceCode.eq(
						RandomTestUtil.randomString()),
					objectDefinition));
	}

}