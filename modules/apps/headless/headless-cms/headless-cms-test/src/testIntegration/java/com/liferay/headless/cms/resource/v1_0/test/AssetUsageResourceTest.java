/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.headless.cms.client.dto.v1_0.AssetUsage;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Thiago Buarque
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-21926"),
		@FeatureFlag("LPD-31149"), @FeatureFlag("LPD-34594"),
		@FeatureFlag("LPS-179669")
	}
)
@RunWith(Arquillian.class)
public class AssetUsageResourceTest extends BaseAssetUsageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		if (!_isCMSSiteInitialized()) {
			Bundle testBundle = FrameworkUtil.getBundle(
				BulkActionResourceTest.class);

			BundleContext bundleContext = testBundle.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.site.initializer.cms")) {

					_deleteFile(bundle, "00.list.type.definition");
					_deleteFile(bundle, "01.object.folder");
					_deleteFile(bundle, "02.object.definition");

					CompletableFuture<Void> completableFuture =
						_batchEngineUnitProcessor.processBatchEngineUnits(
							_batchEngineUnitReader.getBatchEngineUnits(bundle));

					completableFuture.join();

					break;
				}
			}
		}

		_basicDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_DOCUMENT", testCompany.getCompanyId());
		_basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", testCompany.getCompanyId());

		_serviceContext = new ServiceContext() {
			{
				setAttribute("friendlyUrlMap", new HashMap<String, String>());
				setCompanyId(testGroup.getCompanyId());
				setUserId(TestPropsValues.getUserId());
			}
		};

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY, _serviceContext);

		_objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_FILES", _depotEntry.getGroupId(),
					testCompany.getCompanyId());

		_themeDisplay = _getThemeDisplay();
	}

	@Override
	@Test
	public void testGetAssetUsagesAssetPage() throws Exception {
		_addObjectRelationship(
			_basicDocumentObjectDefinition, _basicWebContentObjectDefinition);

		long assetId = testGetAssetUsagesAssetPage_getAssetId();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).put(
				_relationshipObjectField.getName(), assetId
			).build(),
			_serviceContext);

		AssetUsage assetUsage1 = new AssetUsage();

		assetUsage1.setName(() -> objectEntry.getTitleValue(_LANGUAGE_ID));
		assetUsage1.setType(
			() -> _basicWebContentObjectDefinition.getLabel(_LANGUAGE_ID));
		assetUsage1.setUrl(
			() -> StringBundler.concat(
				_themeDisplay.getPortalURL(), _portal.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?&p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&objectEntryId=",
				objectEntry.getObjectEntryId()));

		AssetUsage assetUsage2 = _addLayoutAssetUsage(
			randomAssetUsage(), assetId,
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);

		Page<AssetUsage> page = assetUsageResource.getAssetUsagesAssetPage(
			assetId, null, Pagination.of(1, 10), "name:asc");

		List<AssetUsage> items = ListUtil.fromCollection(page.getItems());

		Assert.assertEquals(items.toString(), 2, items.size());

		String name = assetUsage1.getName();

		if (name.compareTo(assetUsage2.getName()) < 0) {
			equals(assetUsage1, items.get(0));
			equals(assetUsage2, items.get(1));
		}
		else {
			equals(assetUsage1, items.get(1));
			equals(assetUsage2, items.get(0));
		}

		page = assetUsageResource.getAssetUsagesAssetPage(
			assetId, null, Pagination.of(1, 10), "name:desc");

		items = ListUtil.fromCollection(page.getItems());

		Assert.assertEquals(items.toString(), 2, items.size());

		if (name.compareTo(assetUsage2.getName()) < 0) {
			Assert.assertTrue(equals(assetUsage1, items.get(1)));
			Assert.assertTrue(equals(assetUsage2, items.get(0)));
		}
		else {
			Assert.assertTrue(equals(assetUsage1, items.get(0)));
			Assert.assertTrue(equals(assetUsage2, items.get(1)));
		}
	}

	@Override
	protected boolean equals(AssetUsage assetUsage1, AssetUsage assetUsage2) {
		if (!Objects.deepEquals(
				HttpComponentsUtil.removeParameter(
					assetUsage1.getUrl(), "p_p_auth"),
				HttpComponentsUtil.removeParameter(
					assetUsage2.getUrl(), "p_p_auth"))) {

			return false;
		}

		return super.equals(assetUsage1, assetUsage2);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "type"};
	}

	@Override
	protected AssetUsage randomAssetUsage() throws Exception {
		return new AssetUsage() {
			{
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected AssetUsage testGetAssetUsagesAssetPage_addAssetUsage(
			Long assetId, AssetUsage assetUsage)
		throws Exception {

		return _addLayoutAssetUsage(
			assetUsage, assetId,
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
	}

	@Override
	protected Long testGetAssetUsagesAssetPage_getAssetId() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_depotEntry.getGroupId());

		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), dlFolder.getGroupId(),
			dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(bytes), bytes.length, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _depotEntry.getUserId(),
			_basicDocumentObjectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(), _LANGUAGE_ID,
			HashMapBuilder.<String, Serializable>put(
				"file", String.valueOf(dlFileEntry.getFileEntryId())
			).build(),
			_serviceContext);

		return objectEntry.getObjectEntryId();
	}

	private AssetUsage _addLayoutAssetUsage(
			AssetUsage assetUsage, Long assetId, int type)
		throws Exception {

		String name = RandomTestUtil.randomString();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(), 0,
				null, 0, 0, name, type, 0, true, 0, 0, 0,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			testGroup.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(
				_basicDocumentObjectDefinition.getClassName()),
			assetId, RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
			layoutPageTemplateEntry.getPlid(), _serviceContext);

		assetUsage.setName(name);

		if (type == LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {
			assetUsage.setType("Display Page Template");
		}
		else if (type == LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {
			assetUsage.setType("Page Template");
			assetUsage.setUrl(
				() -> HttpComponentsUtil.addParameter(
					PortalUtil.getLayoutFullURL(
						_layoutLocalService.getLayout(
							layoutPageTemplateEntry.getPlid()),
						_themeDisplay),
					"p_l_mode", Constants.PREVIEW));
		}

		return assetUsage;
	}

	private void _addObjectRelationship(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2)
		throws Exception {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_relationshipObjectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(testCompany);
		themeDisplay.setLanguageId(testGroup.getDefaultLanguageId());
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(testGroup.getDefaultLanguageId()));
		themeDisplay.setPortalDomain("localhost");
		themeDisplay.setPortalURL(
			testCompany.getPortalURL(testGroup.getGroupId()));
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(testGroup.getGroupId());
		themeDisplay.setUser(testCompany.getGuestUser());

		return themeDisplay;
	}

	private boolean _isCMSSiteInitialized() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				TestPropsValues.getCompanyId());

		if (objectFolder != null) {
			return true;
		}

		return false;
	}

	private static final String _LANGUAGE_ID = "en_US";

	private ObjectDefinition _basicDocumentObjectDefinition;
	private ObjectDefinition _basicWebContentObjectDefinition;

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	private ObjectField _relationshipObjectField;
	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}