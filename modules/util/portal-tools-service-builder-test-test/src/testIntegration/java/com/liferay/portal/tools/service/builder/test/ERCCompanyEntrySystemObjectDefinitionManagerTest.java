/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.ERCCompanyEntry;
import com.liferay.portal.tools.service.builder.test.service.ERCCompanyEntryLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ERCCompanyEntrySystemObjectDefinitionManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("ERCCompanyEntry");
	}

	@Test
	public void testCRUDBaseModel() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		int randomInt = RandomTestUtil.randomInt();

		_systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"column1", randomInt
			).put(
				"externalReferenceCode", externalReferenceCode
			).build());

		ERCCompanyEntry ercCompanyEntry1 =
			_ercCompanyEntryLocalService.
				getERCCompanyEntryByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertEquals(randomInt, ercCompanyEntry1.getColumn1());

		randomInt = RandomTestUtil.randomInt();

		_systemObjectDefinitionManager.updateBaseModel(
			ercCompanyEntry1.getErcCompanyEntryId(), TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"column1", randomInt
			).put(
				"externalReferenceCode", externalReferenceCode
			).build());

		ercCompanyEntry1 =
			(ERCCompanyEntry)
				_systemObjectDefinitionManager.
					fetchBaseModelByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertEquals(randomInt, ercCompanyEntry1.getColumn1());

		ERCCompanyEntry ercCompanyEntry2 =
			(ERCCompanyEntry)
				_systemObjectDefinitionManager.
					getBaseModelByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

		Assert.assertEquals(ercCompanyEntry1, ercCompanyEntry2);

		_systemObjectDefinitionManager.deleteBaseModel(ercCompanyEntry1);

		Assert.assertNull(
			_ercCompanyEntryLocalService.fetchERCCompanyEntry(
				ercCompanyEntry1.getErcCompanyEntryId()));
	}

	@Test
	public void testGetObjectFields() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(),
				_systemObjectDefinitionManager.getName());

		Assert.assertNotNull(
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "column1"));
	}

	private static SystemObjectDefinitionManager _systemObjectDefinitionManager;

	@Inject
	private ERCCompanyEntryLocalService _ercCompanyEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}