/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
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
		PIMTestUtil.getOrAddGroup(ObjectEntryModelListenerTest.class);
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		_testOnBeforeCreateMovesEntryToProductsFolder();
		_testOnBeforeCreateRecreatesMissingProductsFolder();
	}

	private ObjectEntry _addPIMBaseSkuObjectEntry(
			long groupId, long objectEntryFolderId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_PIM_BASE_SKU", TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), objectEntryFolderId, null,
			HashMapBuilder.<String, Serializable>put(
				"code", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private long _addSpaceDepotEntryGroupId() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		return depotEntry.getGroupId();
	}

	private long _getObjectEntryFolderId(
			long groupId, String externalReferenceCode)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, groupId,
					TestPropsValues.getCompanyId());

		return objectEntryFolder.getObjectEntryFolderId();
	}

	private void _testOnBeforeCreateMovesEntryToProductsFolder()
		throws Exception {

		long groupId = _addSpaceDepotEntryGroupId();

		long objectEntryFolderId = _getObjectEntryFolderId(
			groupId,
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		ObjectEntry objectEntry = _addPIMBaseSkuObjectEntry(
			groupId, objectEntryFolderId);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					"L_PRODUCTS", groupId, TestPropsValues.getCompanyId());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getObjectEntryFolderId());
	}

	private void _testOnBeforeCreateRecreatesMissingProductsFolder()
		throws Exception {

		long groupId = _addSpaceDepotEntryGroupId();

		long objectEntryFolderId = _getObjectEntryFolderId(
			groupId,
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					"L_PRODUCTS", groupId, TestPropsValues.getCompanyId());
		}

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					"L_PRODUCTS", groupId, TestPropsValues.getCompanyId()));

		ObjectEntry objectEntry = _addPIMBaseSkuObjectEntry(
			groupId, objectEntryFolderId);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					"L_PRODUCTS", groupId, TestPropsValues.getCompanyId());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getObjectEntryFolderId());
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}