/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.Map;

import org.junit.Assert;

/**
 * @author Daniel Sanz
 */
public class FrontendDataSetTestUtil {

	public static void assertFDSActionDropdownItem(
		String expectedIcon, String expectedId, String expectedLabel,
		String expectedMethod, FDSActionDropdownItem fdsActionDropdownItem) {

		Map<String, String> data =
			(Map<String, String>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(expectedId, data.get("id"));
		Assert.assertEquals(expectedMethod, data.get("method"));

		Assert.assertEquals(expectedIcon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(expectedLabel, fdsActionDropdownItem.get("label"));
	}

	public static void assertFDSActionDropdownItem(
		String expectedIcon, String expectedId, String expectedLabel,
		String expectedMethod, Map<String, Object> expectedVisibilityFilters,
		FDSActionDropdownItem fdsActionDropdownItem) {

		assertFDSActionDropdownItem(
			expectedIcon, expectedId, expectedLabel, expectedMethod,
			fdsActionDropdownItem);

		Map<String, Object> data =
			(Map<String, Object>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(
			expectedVisibilityFilters, data.get("visibilityFilters"));
	}

	public static void assertFDSActionDropdownItem(
		String expectedIcon, String expectedId, String expectedLabel,
		String expectedMethod, String expectedType,
		Map<String, Object> expectedVisibilityFilters,
		FDSActionDropdownItem fdsActionDropdownItem) {

		assertFDSActionDropdownItem(
			expectedIcon, expectedId, expectedLabel, expectedMethod,
			expectedVisibilityFilters, fdsActionDropdownItem);

		Assert.assertEquals(expectedType, fdsActionDropdownItem.get("type"));
	}

	public static void initialize(Class<?> clazz) throws Exception {

		// Remember whether this call is the one that creates the object
		// definitions, so that tearDown deletes only what it created

		boolean create = false;

		if (_fetchObjectDefinition(_EXTERNAL_REFERENCE_CODE_ROOT) == null) {
			create = true;
		}

		BatchEngineTestUtil.processBatchEngineUnits(
			_BUNDLE_SYMBOLIC_NAME + ".impl", clazz,
			new String[] {
				"." + _BUNDLE_SYMBOLIC_NAME +
					".internal.batch.01.object.definition"
			});

		if (create) {
			_createdObjectDefinitions = true;
		}
	}

	public static void tearDown() throws Exception {
		if (!_createdObjectDefinitions) {
			return;
		}

		_createdObjectDefinitions = false;

		// The object definitions form a root context. Disable inheritance
		// first so that deleting them drops their dynamic tables instead of
		// leaving the data guard to remove the rows one by one.

		ObjectDefinition rootObjectDefinition = _fetchObjectDefinition(
			_EXTERNAL_REFERENCE_CODE_ROOT);

		if (rootObjectDefinition != null) {
			for (ObjectRelationship objectRelationship :
					ObjectRelationshipLocalServiceUtil.getObjectRelationships(
						rootObjectDefinition.getObjectDefinitionId(), true)) {

				ObjectRelationshipLocalServiceUtil.updateObjectRelationship(
					objectRelationship.getExternalReferenceCode(),
					objectRelationship.getObjectRelationshipId(),
					objectRelationship.getParameterObjectFieldId(),
					objectRelationship.getDeletionType(),
					objectRelationship.getDescriptionMap(), false,
					objectRelationship.getLabelMap(), null);
			}
		}

		for (String externalReferenceCode : _EXTERNAL_REFERENCE_CODES) {
			ObjectDefinition objectDefinition = _fetchObjectDefinition(
				externalReferenceCode);

			if (objectDefinition != null) {
				ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private static ObjectDefinition _fetchObjectDefinition(
			String externalReferenceCode)
		throws Exception {

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, TestPropsValues.getCompanyId());
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.frontend.data.set";

	private static final String _EXTERNAL_REFERENCE_CODE_ROOT = "L_DATA_SET";

	private static final String[] _EXTERNAL_REFERENCE_CODES = {
		"L_DATA_SET_ACTION", "L_DATA_SET_CARDS_SECTION",
		"L_DATA_SET_CLIENT_EXTENSION_FILTER", "L_DATA_SET_DATE_FILTER",
		"L_DATA_SET_LIST_SECTION", "L_DATA_SET_SELECTION_FILTER",
		"L_DATA_SET_SNAPSHOT", "L_DATA_SET_SORT", "L_DATA_SET_TABLE_SECTION",
		_EXTERNAL_REFERENCE_CODE_ROOT
	};

	private static boolean _createdObjectDefinitions;

}