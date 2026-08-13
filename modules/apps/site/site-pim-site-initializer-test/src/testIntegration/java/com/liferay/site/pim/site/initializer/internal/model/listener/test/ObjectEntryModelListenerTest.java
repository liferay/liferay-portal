/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectEntryFolderConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMBaseSKUTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PIMTestUtil.getOrAddGroup();
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_addPIMLinkObjectEntry(
			depotEntry.getGroupId(), objectEntry1.getModelClassName(),
			objectEntry1.getExternalReferenceCode());

		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_addPIMLinkObjectEntry(
			depotEntry.getGroupId(), objectEntry2.getModelClassName(),
			objectEntry2.getExternalReferenceCode());

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry1.getObjectEntryId());

		Assert.assertEquals(
			0,
			_getValuesListCount(
				objectEntry1.getExternalReferenceCode(),
				objectEntry1.getModelClassName(), depotEntry.getGroupId()));
		Assert.assertEquals(
			1,
			_getValuesListCount(
				objectEntry2.getExternalReferenceCode(),
				objectEntry2.getModelClassName(), depotEntry.getGroupId()));
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		String externalReferenceCode = objectEntry.getExternalReferenceCode();

		_addPIMLinkObjectEntry(
			depotEntry.getGroupId(), objectEntry.getModelClassName(),
			externalReferenceCode);

		String newExternalReferenceCode = RandomTestUtil.randomString();

		objectEntry.setExternalReferenceCode(newExternalReferenceCode);

		objectEntry = _objectEntryLocalService.updateObjectEntry(objectEntry);

		Assert.assertEquals(
			0,
			_getValuesListCount(
				externalReferenceCode, objectEntry.getModelClassName(),
				depotEntry.getGroupId()));
		Assert.assertEquals(
			1,
			_getValuesListCount(
				newExternalReferenceCode, objectEntry.getModelClassName(),
				depotEntry.getGroupId()));
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		_testOnBeforeCreateMovesEntryToProductsFolder();
		_testOnBeforeCreateRecreatesMissingProductsFolder();
		_testOnBeforeCreateWithDuplicatePIMLinkException();
		_testOnBeforeCreateWithInvalidExternalReferenceCode();
	}

	private void _addPIMLinkObjectEntry(
			long groupId, String className, String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition = _getPIMLinkObjectDefinition();

		_objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"clusterKey", PortalUUIDUtil.generate()
			).put(
				"sourceClassExternalReferenceCode", externalReferenceCode
			).put(
				"sourceClassName", className
			).put(
				"type", "variant"
			).build(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private ObjectDefinition _getPIMLinkObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.
			fetchObjectDefinitionByExternalReferenceCode(
				PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
				TestPropsValues.getCompanyId());
	}

	private int _getValuesListCount(
			String classExternalReferenceCode, String className, long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _getPIMLinkObjectDefinition();

		return _objectEntryLocalService.getValuesListCount(
			TestPropsValues.getCompanyId(), new Long[] {groupId},
			new Long[] {objectDefinition.getObjectDefinitionId()},
			_filterFactory.create(
				StringBundler.concat(
					"(sourceClassExternalReferenceCode eq '",
					classExternalReferenceCode, "' and sourceClassName eq '",
					className, "') or (",
					"targetClassExternalReferenceCode eq '",
					classExternalReferenceCode, "' and targetClassName eq '",
					className, "')"),
				objectDefinition));
	}

	private void _testOnBeforeCreateMovesEntryToProductsFolder()
		throws Exception {

		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(),
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getObjectEntryFolderId());
	}

	private void _testOnBeforeCreateRecreatesMissingProductsFolder()
		throws Exception {

		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId());
		}

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId()));

		ObjectEntry objectEntry = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(),
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getObjectEntryFolderId());
	}

	private void _testOnBeforeCreateWithDuplicatePIMLinkException()
		throws Exception {

		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_addPIMLinkObjectEntry(
			depotEntry.getGroupId(), objectEntry.getModelClassName(),
			objectEntry.getExternalReferenceCode());

		Assert.assertThrows(
			ModelListenerException.class,
			() -> _addPIMLinkObjectEntry(
				depotEntry.getGroupId(), objectEntry.getModelClassName(),
				objectEntry.getExternalReferenceCode()));
	}

	private void _testOnBeforeCreateWithInvalidExternalReferenceCode()
		throws Exception {

		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					TestPropsValues.getCompanyId());

		Assert.assertThrows(
			ModelListenerException.class,
			() -> _addPIMLinkObjectEntry(
				depotEntry.getGroupId(), objectDefinition.getClassName(),
				RandomTestUtil.randomString()));
	}

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}