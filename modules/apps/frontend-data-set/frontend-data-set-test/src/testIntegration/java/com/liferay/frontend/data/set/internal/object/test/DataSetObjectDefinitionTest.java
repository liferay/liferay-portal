/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.object.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Antonio Ortega
 */
@RunWith(Arquillian.class)
public class DataSetObjectDefinitionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(DataSetObjectDefinitionTest.class);
	}

	@After
	public void tearDown() throws Exception {
		FrontendDataSetTestUtil.tearDown();
	}

	@Test
	public void testShowPaginationDefaultsToTrue() throws Exception {
		ObjectDefinition dataSetObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", TestPropsValues.getCompanyId());

		_dataSetObjectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			dataSetObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"defaultItemsPerPage", RandomTestUtil.randomInt(1, 100)
			).put(
				"label", RandomTestUtil.randomString()
			).put(
				"listOfItemsPerPage", RandomTestUtil.randomString()
			).put(
				"restApplication", RandomTestUtil.randomString()
			).put(
				"restEndpoint", RandomTestUtil.randomString()
			).put(
				"restSchema", RandomTestUtil.randomString()
			).build(),
			new ServiceContext());

		Map<String, Serializable> values = _dataSetObjectEntry.getValues();

		Assert.assertEquals(Boolean.TRUE, values.get("showPagination"));
	}

	@DeleteAfterTestRun
	private ObjectEntry _dataSetObjectEntry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}