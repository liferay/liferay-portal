/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureServiceUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateStructureRelExportImportTest
	extends BaseExportImportTestCase {

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

		layout = LayoutTestUtil.addTypeContentLayout(group);

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-72839")
	public void testCollectionDisplay() throws Exception {
		AssetListEntry assetListEntry1 = _addAssetListEntry(group);

		String itemId = ContentLayoutTestUtil.addCollectionDisplayToLayout(
			_getCollectionJSONObject(
				assetListEntry1.getAssetListEntryId(), null, null),
			layout, _layoutStructureProvider, null, null, 0,
			_segmentsExperienceId);

		String externalReferenceCode = RandomTestUtil.randomString();

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> itemConfigJSONObject.put(
				"collection",
				_getCollectionJSONObject(0, externalReferenceCode, null)));

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		importedLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
			layout.getExternalReferenceCode(), importedGroup.getGroupId());

		_assertCollectionConfig(
			externalReferenceCode,
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		AssetListEntry assetListEntry3 = _addAssetListEntry(group);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> itemConfigJSONObject.put(
				"collection",
				_getCollectionJSONObject(
					0, assetListEntry3.getExternalReferenceCode(), null)));

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		AssetListEntry importedAssetListEntry =
			_assetListEntryLocalService.getAssetListEntryByUuidAndGroupId(
				assetListEntry3.getUuid(), importedGroup.getGroupId());

		_assertCollectionConfig(
			importedAssetListEntry.getExternalReferenceCode(),
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getGroupId());

		AssetListEntry assetListEntry4 = _addAssetListEntry(guestGroup);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> itemConfigJSONObject.put(
				"collection",
				_getCollectionJSONObject(
					0, assetListEntry4.getExternalReferenceCode(),
					guestGroup.getExternalReferenceCode())));

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		_assertCollectionConfig(
			assetListEntry4.getExternalReferenceCode(),
			_getLayoutStructureItem(itemId, importedLayout.getPlid()),
			guestGroup.getExternalReferenceCode());
	}

	@Test
	@TestInfo("LPD-72839")
	public void testContainer() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject = ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"link",
				_getFileEntryJSONObject(
					externalReferenceCode, "FileEntry_title", null)
			).put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					_getFileEntryJSONObject(externalReferenceCode, null, null))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER, layout,
			_layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		String itemId = jsonObject.getString("addedItemId");

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		importedLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
			layout.getExternalReferenceCode(), importedGroup.getGroupId());

		_assertContainerConfig(
			externalReferenceCode,
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		FileEntry fileEntry1 = _addFileEntry(_serviceContext);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> {
				itemConfigJSONObject.put(
					"link",
					_getFileEntryJSONObject(
						fileEntry1.getExternalReferenceCode(),
						"FileEntry_title", null));

				JSONObject stylesJSONObject =
					itemConfigJSONObject.getJSONObject("styles");

				stylesJSONObject.put(
					"backgroundImage",
					_getFileEntryJSONObject(
						fileEntry1.getExternalReferenceCode(), null, null));

				return itemConfigJSONObject;
			});

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		FileEntry importedFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry1.getUuid(), importedGroup.getGroupId());

		_assertContainerConfig(
			importedFileEntry.getExternalReferenceCode(),
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getGroupId());

		FileEntry fileEntry2 = _addFileEntry(
			ServiceContextTestUtil.getServiceContext(guestGroup.getGroupId()));

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> {
				itemConfigJSONObject.put(
					"link",
					_getFileEntryJSONObject(
						fileEntry2.getExternalReferenceCode(),
						"FileEntry_title",
						guestGroup.getExternalReferenceCode()));

				JSONObject stylesJSONObject =
					itemConfigJSONObject.getJSONObject("styles");

				stylesJSONObject.put(
					"backgroundImage",
					_getFileEntryJSONObject(
						fileEntry2.getExternalReferenceCode(), null,
						guestGroup.getExternalReferenceCode()));

				return itemConfigJSONObject;
			});

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		_assertContainerConfig(
			fileEntry2.getExternalReferenceCode(),
			_getLayoutStructureItem(itemId, importedLayout.getPlid()),
			guestGroup.getExternalReferenceCode());

		Layout mappedLayout = LayoutTestUtil.addTypeContentLayout(group);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> itemConfigJSONObject.put(
				"link",
				JSONUtil.put(
					"layout",
					_getLayoutJSONObject(
						mappedLayout.getExternalReferenceCode(), null))));

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		_assertLayoutJSONObject(
			mappedLayout.getExternalReferenceCode(), "link",
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);
	}

	@Test
	@TestInfo({"LPD-67912", "LPD-72839"})
	public void testFormContainer() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		JSONObject jsonObject = ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"classNameId",
				String.valueOf(_portal.getClassNameId(JournalArticle.class))
			).put(
				"classTypeId", "0"
			).put(
				"formConfig",
				FormStyledLayoutStructureItem.FORM_CONFIG_OTHER_ITEM_TYPE
			).put(
				"successMessage",
				JSONUtil.put(
					"layout", _getLayoutJSONObject(externalReferenceCode, null)
				).put(
					"type", "page"
				)
			).toString(),
			LayoutDataItemTypeConstants.TYPE_FORM, layout,
			_layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		String itemId = jsonObject.getString("addedItemId");

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		importedLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
			layout.getExternalReferenceCode(), importedGroup.getGroupId());

		_assertLayoutJSONObject(
			externalReferenceCode, "successMessage",
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		Layout mappedLayout = LayoutTestUtil.addTypeContentLayout(group);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> {
				JSONObject successMessageJSONObject =
					itemConfigJSONObject.getJSONObject("successMessage");

				successMessageJSONObject.put(
					"layout",
					_getLayoutJSONObject(
						mappedLayout.getExternalReferenceCode(), null));

				return itemConfigJSONObject;
			});

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		_assertLayoutJSONObject(
			mappedLayout.getExternalReferenceCode(), "successMessage",
			_getLayoutStructureItem(itemId, importedLayout.getPlid()), null);

		Group guestGroup = _groupLocalService.getGroup(
			TestPropsValues.getGroupId());

		Layout guestGroupLayout = LayoutTestUtil.addTypeContentLayout(
			guestGroup);

		_updateLayoutStructureItem(
			itemId,
			itemConfigJSONObject -> {
				JSONObject successMessageJSONObject =
					itemConfigJSONObject.getJSONObject("successMessage");

				successMessageJSONObject.put(
					"layout",
					_getLayoutJSONObject(
						guestGroupLayout.getExternalReferenceCode(),
						guestGroup.getExternalReferenceCode()));

				return itemConfigJSONObject;
			});

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		_assertLayoutJSONObject(
			guestGroupLayout.getExternalReferenceCode(), "successMessage",
			_getLayoutStructureItem(itemId, importedLayout.getPlid()),
			guestGroup.getExternalReferenceCode());
	}

	@Test
	@TestInfo("LPD-97443")
	public void testImportedLayoutPageTemplateStructureRelHasDefaultSegmentsExperience()
		throws Exception {

		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		importedLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
			layout.getExternalReferenceCode(), importedGroup.getGroupId());

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				importedLayout.getPlid());

		Assert.assertNotEquals(
			SegmentsExperienceConstants.ID_DEFAULT,
			defaultSegmentsExperienceId);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					importedGroup.getGroupId(), importedLayout.getPlid());

		Assert.assertNull(
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					SegmentsExperienceConstants.ID_DEFAULT));

		Assert.assertNotNull(
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					defaultSegmentsExperienceId));
	}

	private AssetListEntry _addAssetListEntry(Group group) throws Exception {
		return _assetListEntryLocalService.addAssetListEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"anyAssetType",
				String.valueOf(_portal.getClassNameId(JournalArticle.class))
			).buildString(),
			_serviceContext);
	}

	private FileEntry _addFileEntry(ServiceContext serviceContext)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			_read("dependencies/sample.png"), null, null, null, serviceContext);
	}

	private void _assertCollectionConfig(
		String externalReferenceCode, LayoutStructureItem layoutStructureItem,
		String scopeExternalReferenceCode) {

		JSONObject itemConfigJSONObject =
			layoutStructureItem.getItemConfigJSONObject();

		JSONObject collectionJSONObject = itemConfigJSONObject.getJSONObject(
			"collection");

		_assertMappedField(
			externalReferenceCode, collectionJSONObject,
			scopeExternalReferenceCode);
	}

	private void _assertContainerConfig(
		String externalReferenceCode, LayoutStructureItem layoutStructureItem,
		String scopeExternalReferenceCode) {

		JSONObject itemConfigJSONObject =
			layoutStructureItem.getItemConfigJSONObject();

		JSONObject linkJSONObject = itemConfigJSONObject.getJSONObject("link");

		_assertMappedField(
			externalReferenceCode, linkJSONObject, scopeExternalReferenceCode);

		JSONObject stylesJSONObject = itemConfigJSONObject.getJSONObject(
			"styles");

		JSONObject backgroundImageJSONObject = stylesJSONObject.getJSONObject(
			"backgroundImage");

		_assertMappedField(
			externalReferenceCode, backgroundImageJSONObject,
			scopeExternalReferenceCode);
	}

	private void _assertLayoutJSONObject(
		String externalReferenceCode, String key,
		LayoutStructureItem layoutStructureItem,
		String scopeExternalReferenceCode) {

		JSONObject itemConfigJSONObject =
			layoutStructureItem.getItemConfigJSONObject();

		JSONObject jsonObject = itemConfigJSONObject.getJSONObject(key);

		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (Validator.isNotNull(externalReferenceCode)) {
			Assert.assertEquals(
				externalReferenceCode,
				layoutJSONObject.getString("externalReferenceCode"));
		}
		else {
			Assert.assertFalse(layoutJSONObject.has("externalReferenceCode"));
		}

		if (Validator.isNotNull(scopeExternalReferenceCode)) {
			Assert.assertEquals(
				scopeExternalReferenceCode,
				layoutJSONObject.getString("scopeExternalReferenceCode"));
		}
		else {
			Assert.assertFalse(
				layoutJSONObject.has("scopeExternalReferenceCode"));
		}
	}

	private void _assertMappedField(
		String externalReferenceCode, JSONObject jsonObject,
		String scopeExternalReferenceCode) {

		if (Validator.isNotNull(externalReferenceCode)) {
			Assert.assertEquals(
				externalReferenceCode,
				jsonObject.getString("externalReferenceCode"));
		}
		else {
			Assert.assertFalse(jsonObject.has("externalReferenceCode"));
		}

		if (Validator.isNotNull(scopeExternalReferenceCode)) {
			Assert.assertEquals(
				scopeExternalReferenceCode,
				jsonObject.getString("scopeExternalReferenceCode"));
		}
		else {
			Assert.assertFalse(jsonObject.has("scopeExternalReferenceCode"));
		}
	}

	private JSONObject _getCollectionJSONObject(
		long classPK, String externalReferenceCode,
		String scopeExternalReferenceCode) {

		return JSONUtil.put(
			"classPK",
			() -> {
				if (classPK <= 0) {
					return null;
				}

				return classPK;
			}
		).put(
			"externalReferenceCode",
			() -> {
				if (Validator.isNull(externalReferenceCode)) {
					return null;
				}

				return externalReferenceCode;
			}
		).put(
			"scopeExternalReferenceCode",
			() -> {
				if (Validator.isNull(scopeExternalReferenceCode)) {
					return null;
				}

				return scopeExternalReferenceCode;
			}
		).put(
			"type",
			"com.liferay.item.selector.criteria.InfoListItemSelectorReturnType"
		);
	}

	private JSONObject _getFileEntryJSONObject(
		String externalReferenceCode, String fieldId,
		String scopeExternalReferenceCode) {

		return JSONUtil.put(
			"className", FileEntry.class.getName()
		).put(
			"classNameId", _portal.getClassNameId(FileEntry.class)
		).put(
			"classTypeId",
			String.valueOf(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)
		).put(
			"externalReferenceCode",
			() -> {
				if (Validator.isNull(externalReferenceCode)) {
					return null;
				}

				return externalReferenceCode;
			}
		).put(
			"fieldId", fieldId
		).put(
			"itemSubtype",
			_language.get(
				LocaleUtil.ENGLISH,
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT)
		).put(
			"itemType", "Document"
		).put(
			"scopeExternalReferenceCode",
			() -> {
				if (Validator.isNull(scopeExternalReferenceCode)) {
					return null;
				}

				return scopeExternalReferenceCode;
			}
		).put(
			"type",
			"com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType"
		);
	}

	private JSONObject _getLayoutJSONObject(
		String externalReferenceCode, String scopeExternalReferenceCode) {

		return JSONUtil.put(
			"externalReferenceCode",
			() -> {
				if (Validator.isNull(externalReferenceCode)) {
					return null;
				}

				return externalReferenceCode;
			}
		).put(
			"scopeExternalReferenceCode",
			() -> {
				if (Validator.isNull(scopeExternalReferenceCode)) {
					return null;
				}

				return scopeExternalReferenceCode;
			}
		);
	}

	private LayoutStructureItem _getLayoutStructureItem(
		String itemId, long plid) {

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid);

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				plid, segmentsExperienceId);

		return layoutStructure.getLayoutStructureItem(itemId);
	}

	private byte[] _read(String fileName) throws Exception {
		return FileUtil.getBytes(
			LayoutPageTemplateStructureRelExportImportTest.class, fileName);
	}

	private void _updateLayoutStructureItem(
			String itemId,
			UnsafeFunction<JSONObject, JSONObject, Exception> unsafeFunction)
		throws Exception {

		LayoutStructure layoutStructure =
			_layoutStructureProvider.getLayoutStructure(
				layout.getPlid(), _segmentsExperienceId);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		layoutStructureItem.updateItemConfig(
			unsafeFunction.apply(
				layoutStructureItem.getItemConfigJSONObject()));

		LayoutPageTemplateStructureServiceUtil.
			updateLayoutPageTemplateStructureData(
				layout.getGroupId(), layout.getPlid(), _segmentsExperienceId,
				layoutStructure.toString());
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}