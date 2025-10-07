/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v2_13_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	public void testUpgrade() throws Exception {
		String editableValues = StringUtil.replace(
			_read("editable_values.json"), "${", "}",
			HashMapBuilder.put(
				"CLASS_NAME_ID",
				String.valueOf(
					_classNameLocalService.getClassNameId(
						DLFileEntry.class.getName()))
			).put(
				"CLASS_PK1",
				() -> {
					DLFileEntry dlFileEntry1 = _addDLFileEntry();

					return String.valueOf(dlFileEntry1.getPrimaryKey());
				}
			).put(
				"CLASS_PK2",
				() -> {
					DLFileEntry dlFileEntry2 = _addDLFileEntry();

					return String.valueOf(dlFileEntry2.getPrimaryKey());
				}
			).build());

		FragmentEntryLink draftLayoutFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValues, _draftLayout, _segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		List<FragmentEntryLink> publishedLayoutFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_layout.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			publishedLayoutFragmentEntryLinks.toString(), 1,
			publishedLayoutFragmentEntryLinks.size());

		FragmentEntryLink publishedLayoutFragmentEntryLink =
			publishedLayoutFragmentEntryLinks.get(0);

		_assertFragmentEntryLinksFileEntryClassNameId(
			_classNameLocalService.getClassNameId(DLFileEntry.class.getName()),
			draftLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			publishedLayoutFragmentEntryLink.getFragmentEntryLinkId());

		runUpgrade();

		_assertFragmentEntryLinksFileEntryClassNameId(
			_classNameLocalService.getClassNameId(FileEntry.class.getName()),
			draftLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			publishedLayoutFragmentEntryLink.getFragmentEntryLinkId());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", _draftLayout, _segmentsExperienceId);
	}

	@Override
	protected CTService<?> getCTService() {
		return _fragmentEntryLinkLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(2, 13, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		FragmentEntryLink fragmentEntryLink = (FragmentEntryLink)ctModel;

		return _fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			).toString());
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		return _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, StringUtil.randomId(),
			ContentTypes.IMAGE_PNG, StringUtil.randomString(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new UnsyncByteArrayInputStream(new byte[0]), 0, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private void _assertFragmentEntryLinksFileEntryClassNameId(
			long expectedClassNameId, long... fragmentEntryLinkIds)
		throws Exception {

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkId);

			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			JSONObject editableFragmentEntryProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

			Set<String> editableElementIds =
				editableFragmentEntryProcessorJSONObject.keySet();

			for (String editableElementId : editableElementIds) {
				JSONObject editableElementJSONObject =
					editableFragmentEntryProcessorJSONObject.getJSONObject(
						editableElementId);

				JSONObject configJSONObject =
					editableElementJSONObject.getJSONObject("config");

				String classNameId = configJSONObject.getString("classNameId");

				Assert.assertEquals(
					String.valueOf(expectedClassNameId), classNameId);
			}

			JSONObject backgroundFragmentEntryProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(
					FragmentEntryProcessorConstants.
						KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR);

			JSONObject backgroundImageJSONObject =
				backgroundFragmentEntryProcessorJSONObject.getJSONObject(
					"backgroundImage");

			JSONObject jsonObject = backgroundImageJSONObject.getJSONObject(
				"en_US");

			Assert.assertEquals(
				String.valueOf(expectedClassNameId),
				jsonObject.getString("classNameId"));
		}
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	private Layout _draftLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}