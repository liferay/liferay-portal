/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v3_3_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class StorageLinksUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, StorageLinksUpgradeProcessTest.class.getSimpleName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		DDMStructure productionDDMStructure = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			productionDDMStructure = _ddmStructureLocalService.addStructure(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				_classNameLocalService.getClassNameId(JournalArticle.class),
				StringUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, StringUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.US, StringUtil.randomString()
				).build(),
				_getDDMForm(), _ddm.getDefaultDDMFormLayout(_getDDMForm()),
				StorageType.DEFAULT.toString(),
				DDMStructureConstants.TYPE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		DDMStructure ctCollectionDDMStructure = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			ctCollectionDDMStructure =
				_ddmStructureLocalService.updateStructure(
					TestPropsValues.getUserId(),
					productionDDMStructure.getStructureId(), _getDDMForm(),
					_ddm.getDefaultDDMFormLayout(_getDDMForm()),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			List<DDMStorageLink> ddmStorageLinks = _getDDMStorageLinks(
				ctCollectionDDMStructure.getStructureId());

			for (DDMStorageLink ddmStorageLink : ddmStorageLinks) {
				ddmStorageLink.setStructureVersionId(0);

				_ddmStorageLinkLocalService.updateDDMStorageLink(
					ddmStorageLink);
			}
		}

		_runUpgrade();

		_multiVMPool.clear();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			ctCollectionDDMStructure =
				_ddmStructureLocalService.fetchDDMStructure(
					productionDDMStructure.getStructureId());

			List<DDMStorageLink> ctCollectionDDMStorageLinks =
				_getDDMStorageLinks(ctCollectionDDMStructure.getStructureId());
			DDMStructureVersion ctCollectionDDMStructureVersion =
				ctCollectionDDMStructure.getStructureVersion();

			List<Long> ddmStructureVersionIds = ListUtil.toList(
				ctCollectionDDMStorageLinks,
				DDMStorageLink::getStructureVersionId);

			Assert.assertFalse(
				ddmStructureVersionIds.toString(),
				ListUtil.exists(
					ddmStructureVersionIds,
					ddmStructureVersionId -> ddmStructureVersionId == 0));
			Assert.assertEquals(
				ddmStructureVersionIds.toString(),
				ddmStructureVersionIds.size(),
				ListUtil.count(
					ddmStructureVersionIds,
					ddmStructureVersionId ->
						ddmStructureVersionId ==
							ctCollectionDDMStructureVersion.
								getStructureVersionId()));
		}
	}

	private DDMForm _getDDMForm() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addAvailableLocale(LocaleUtil.US);
		ddmForm.addDDMFormField(
			new DDMFormField("field", DDMFormFieldTypeConstants.TEXT));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		return ddmForm;
	}

	private List<DDMStorageLink> _getDDMStorageLinks(long structureId) {
		return TransformUtil.transform(
			_ddmStorageLinkLocalService.getDDMStorageLinks(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			ddmStorageLink -> {
				if (ddmStorageLink.getStructureId() == structureId) {
					return ddmStorageLink;
				}

				return null;
			});
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.journal.internal.upgrade.v3_3_0." +
				"StorageLinksUpgradeProcess");

		upgradeProcess.upgrade();
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DDM _ddm;

	@Inject
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}