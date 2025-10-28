/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.test.util.MockInfoServiceRegistrationHolder;
import com.liferay.info.test.util.model.MockObject;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _portal.getClassNameId(JournalArticle.class.getName());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetAssetDisplayPageLayoutPageTemplateEntryWithInfoItemIdentifier()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, false, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			_serviceContext);

		InfoItemReference infoItemReference = new InfoItemReference(
			objectDefinition.getClassName(),
			new ERCInfoItemIdentifier(objectEntry.getExternalReferenceCode()));

		Assert.assertNull(
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), infoItemReference));

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			defaultLayoutPageTemplateEntry,
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), infoItemReference));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, false,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			objectEntry.getObjectEntryId(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, _serviceContext);

		Assert.assertEquals(
			layoutPageTemplateEntry,
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), infoItemReference));
	}

	@Test
	public void testGetAssetDisplayPageLayoutPageTemplateEntryWithUnsupportedInfoItemIdentifier()
		throws Exception {

		MockObject mockObject = new MockObject(
			RandomTestUtil.randomLong(), false, true);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(
								InfoField.builder(
								).infoFieldType(
									TextInfoFieldType.INSTANCE
								).namespace(
									RandomTestUtil.randomString()
								).name(
									RandomTestUtil.randomString()
								).build())
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability)) {

			InfoItemReference infoItemReference = new InfoItemReference(
				MockObject.class.getName(),
				new ERCInfoItemIdentifier(RandomTestUtil.randomString()));

			Assert.assertNull(
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					_group.getGroupId(), infoItemReference));

			LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(MockObject.class.getName()), 0, true,
					WorkflowConstants.STATUS_APPROVED);

			Assert.assertNull(
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					_group.getGroupId(), infoItemReference));

			infoItemReference = new InfoItemReference(
				MockObject.class.getName(),
				new ClassPKInfoItemIdentifier(RandomTestUtil.randomLong()));

			Assert.assertEquals(
				defaultLayoutPageTemplateEntry,
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					_group.getGroupId(), infoItemReference));
		}
	}

	@Test
	public void testHasAssetDisplayPageWithInfoItemIdentifier()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, false, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			_serviceContext);

		InfoItemReference infoItemReference = new InfoItemReference(
			objectDefinition.getClassName(),
			new ERCInfoItemIdentifier(objectEntry.getExternalReferenceCode()));

		Assert.assertFalse(
			AssetDisplayPageUtil.hasAssetDisplayPage(
				_group.getGroupId(), infoItemReference));

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertTrue(
			AssetDisplayPageUtil.hasAssetDisplayPage(
				_group.getGroupId(), infoItemReference));

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry);

		Assert.assertFalse(
			AssetDisplayPageUtil.hasAssetDisplayPage(
				_group.getGroupId(), infoItemReference));

		layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, false,
				WorkflowConstants.STATUS_APPROVED);

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
				objectEntry.getObjectEntryId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC, _serviceContext);

		Assert.assertTrue(
			AssetDisplayPageUtil.hasAssetDisplayPage(
				_group.getGroupId(), infoItemReference));

		_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
			assetDisplayPageEntry);

		Assert.assertFalse(
			AssetDisplayPageUtil.hasAssetDisplayPage(
				_group.getGroupId(), infoItemReference));
	}

	@Test
	public void testHasAssetDisplayPageWithUnsupportedInfoItemIdentifier()
		throws Exception {

		MockObject mockObject = new MockObject(
			RandomTestUtil.randomLong(), false, true);

		try (MockInfoServiceRegistrationHolder
				mockInfoServiceRegistrationHolder =
					new MockInfoServiceRegistrationHolder(
						InfoFieldSet.builder(
						).infoFieldSetEntries(
							ListUtil.fromArray(
								InfoField.builder(
								).infoFieldType(
									TextInfoFieldType.INSTANCE
								).namespace(
									RandomTestUtil.randomString()
								).name(
									RandomTestUtil.randomString()
								).build())
						).build(),
						mockObject, _portal, _displayPageInfoItemCapability)) {

			InfoItemReference infoItemReference = new InfoItemReference(
				MockObject.class.getName(),
				new ERCInfoItemIdentifier(RandomTestUtil.randomString()));

			Assert.assertFalse(
				AssetDisplayPageUtil.hasAssetDisplayPage(
					_group.getGroupId(), infoItemReference));

			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(MockObject.class.getName()), 0, true,
				WorkflowConstants.STATUS_APPROVED);

			Assert.assertFalse(
				AssetDisplayPageUtil.hasAssetDisplayPage(
					_group.getGroupId(), infoItemReference));

			infoItemReference = new InfoItemReference(
				MockObject.class.getName(),
				new ClassPKInfoItemIdentifier(RandomTestUtil.randomLong()));

			Assert.assertTrue(
				AssetDisplayPageUtil.hasAssetDisplayPage(
					_group.getGroupId(), infoItemReference));
		}
	}

	@Test
	public void testViewNondefaultAssetDisplayPageEntry() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), _classNameId,
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			defaultLayoutPageTemplateEntry,
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), _classNameId,
				journalArticle.getResourcePrimKey(),
				journalArticle.getDDMStructureId()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), _classNameId,
				journalArticle.getDDMStructureId(), false,
				WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			defaultLayoutPageTemplateEntry,
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), _classNameId,
				journalArticle.getResourcePrimKey(),
				journalArticle.getDDMStructureId()));

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), _classNameId,
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, _serviceContext);

		Assert.assertEquals(
			layoutPageTemplateEntry,
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				_group.getGroupId(), _classNameId,
				journalArticle.getResourcePrimKey(),
				journalArticle.getDDMStructureId()));
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	private long _classNameId;

	@Inject(
		filter = "info.item.capability.key=" + DisplayPageInfoItemCapability.KEY
	)
	private InfoItemCapability _displayPageInfoItemCapability;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}