/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.related.models.test.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;

/**
 * @author Pedro Leite
 */
public class ObjectRelationshipTestUtil {

	public static void addObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1, long primaryKey2)
		throws Exception {

		ObjectRelationshipLocalServiceUtil.
			addObjectRelationshipMappingTableValues(
				TestPropsValues.getUserId(), objectRelationshipId, primaryKey1,
				primaryKey2, ServiceContextTestUtil.getServiceContext());
	}

	public static void assertGetRelatedModels(
			int expectedSize,
			ObjectRelatedModelsProvider objectRelatedModelsProvider,
			long objectRelationshipId, long primaryKey)
		throws Exception {

		List<ObjectEntry> objectEntries =
			objectRelatedModelsProvider.getRelatedModels(
				0, objectRelationshipId, null, false, primaryKey, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			objectEntries.toString(), expectedSize, objectEntries.size());
	}

	public static void assertSearchRelatedModels(
			int expectedSize, long groupId,
			ObjectRelatedModelsProvider objectRelatedModelsProvider,
			long objectRelationshipId, long primaryKey, String search)
		throws Exception {

		List<ObjectEntry> objectEntries =
			objectRelatedModelsProvider.getRelatedModels(
				groupId, objectRelationshipId, null, false, primaryKey, search,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			objectEntries.toString(), expectedSize, objectEntries.size());
	}

	public static void assertSearchRelatedModels(
			int expectedSize,
			ObjectRelatedModelsProvider objectRelatedModelsProvider,
			long objectRelationshipId, long primaryKey, String search)
		throws Exception {

		assertSearchRelatedModels(
			expectedSize, 0, objectRelatedModelsProvider, objectRelationshipId,
			primaryKey, search);
	}

	public static ObjectRelationship updateObjectRelationship(
			String externalReferenceCode, long objectRelationshipId,
			String deletionType, Map<Locale, String> labelMap)
		throws Exception {

		return ObjectRelationshipLocalServiceUtil.updateObjectRelationship(
			externalReferenceCode, objectRelationshipId, 0, deletionType, false,
			labelMap, null);
	}

}