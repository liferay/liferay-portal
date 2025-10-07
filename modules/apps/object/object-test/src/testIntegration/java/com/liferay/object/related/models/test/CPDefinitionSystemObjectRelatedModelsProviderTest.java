/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.related.models.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.related.models.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class CPDefinitionSystemObjectRelatedModelsProviderTest
	extends BaseSystemObjectRelatedModelsProviderTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cProductIds = addBaseModels(2);

		CPDefinition cpDefinition1 =
			CPDefinitionLocalServiceUtil.getCProductCPDefinition(
				_cProductIds[0], 1);

		_cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition1, cpDefinition1.getDefaultLanguageId(),
			"CP" + RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CPDefinition cpDefinition2 =
			CPDefinitionLocalServiceUtil.getCProductCPDefinition(
				_cProductIds[1], 1);

		_cpDefinitionLocalService.updateCPDefinitionLocalization(
			cpDefinition2, cpDefinition2.getDefaultLanguageId(),
			"CP" + RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();
		_systemObjectDefinition = getSystemObjectDefinition();
	}

	@Override
	@Test
	public void testSystemObjectEntry1toMObjectRelatedModels()
		throws Exception {

		super.testSystemObjectEntry1toMObjectRelatedModels();

		_testSystemObjectEntryObjectRelatedModels(
			0, ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		_testSystemObjectEntryObjectRelatedModels(
			TestPropsValues.getGroupId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Override
	@Test
	public void testSystemObjectEntryMtoMObjectRelatedModels()
		throws Exception {

		super.testSystemObjectEntryMtoMObjectRelatedModels();

		_testSystemObjectEntryObjectRelatedModels(
			0, ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		_testSystemObjectEntryObjectRelatedModels(
			TestPropsValues.getGroupId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
	}

	@Override
	protected long[] addBaseModels(int count) throws Exception {
		long[] cProductIds = new long[count];

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		for (int i = 0; i < count; i++) {
			CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
				commerceCatalog.getGroupId());

			cProductIds[i] = cpDefinition.getCProductId();
		}

		return cProductIds;
	}

	@Override
	protected void assertFailure(long primaryKey) {
		AssertUtils.assertFailure(
			NoSuchCPDefinitionException.class,
			"No CPDefinition exists with the primary key " + primaryKey,
			() -> _cpDefinitionLocalService.getCPDefinition(primaryKey));
	}

	@Override
	protected void deleteBaseModel(long primaryKey) {
		_cpDefinitionLocalService.deleteCPDefinitions(
			primaryKey, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected Object fetchBaseModel(long primaryKey) {
		return _cpDefinitionLocalService.fetchCPDefinitionByCProductId(
			primaryKey);
	}

	@Override
	protected String getName(long primaryKey) throws Exception {
		return String.valueOf(primaryKey);
	}

	@Override
	protected ObjectDefinition getSystemObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.fetchObjectDefinitionByClassName(
			TestPropsValues.getCompanyId(), CPDefinition.class.getName());
	}

	private void _testSystemObjectEntryObjectRelatedModels(
			long groupId, String objectRelationshipType)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			groupId, _objectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		ObjectRelationship objectRelationship = addObjectRelationship(
			_objectDefinition, _systemObjectDefinition,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			objectRelationshipType);

		if (objectRelationshipType.equals(
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			insertIntoOrUpdateExtensionTable(
				objectEntry.getObjectEntryId(), _cProductIds[0],
				_systemObjectDefinition.getObjectDefinitionId());
			insertIntoOrUpdateExtensionTable(
				objectEntry.getObjectEntryId(), _cProductIds[1],
				_systemObjectDefinition.getObjectDefinitionId());
		}
		else {
			ObjectRelationshipTestUtil.addObjectRelationshipMappingTableValues(
				objectRelationship.getObjectRelationshipId(),
				objectEntry.getPrimaryKey(), _cProductIds[0]);
			ObjectRelationshipTestUtil.addObjectRelationshipMappingTableValues(
				objectRelationship.getObjectRelationshipId(),
				objectEntry.getPrimaryKey(), _cProductIds[1]);
		}

		CPDefinition cpDefinition =
			CPDefinitionLocalServiceUtil.getCProductCPDefinition(
				_cProductIds[0], 1);

		ObjectRelationshipTestUtil.assertSearchRelatedModels(
			0, objectRelatedModelsProvider,
			objectRelationship.getObjectRelationshipId(),
			objectEntry.getPrimaryKey(), StringUtil.randomString());
		ObjectRelationshipTestUtil.assertSearchRelatedModels(
			1, objectRelatedModelsProvider,
			objectRelationship.getObjectRelationshipId(),
			objectEntry.getPrimaryKey(), cpDefinition.getName());
		ObjectRelationshipTestUtil.assertSearchRelatedModels(
			2, objectRelatedModelsProvider,
			objectRelationship.getObjectRelationshipId(),
			objectEntry.getPrimaryKey(), "CP");

		objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship.getObjectRelationshipId());
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private long[] _cProductIds;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectDefinition _systemObjectDefinition;

}