/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentStagingTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Map;

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
public class FragmentCollectionStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_liveGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testFragmentResourcesWithFoldersCopiedWhenLocalStagingActivated()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_liveGroup.getGroupId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		_addPortletFileEntry(
			fragmentCollection.getResourcesFolderId(), fragmentCollection,
			"Image1");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_liveGroup.getGroupId(), TestPropsValues.getUserId());

		Folder folder1 = _addPortletFolder(
			fragmentCollection.getResourcesFolderId(), _liveGroup.getGroupId(),
			"Folder1", serviceContext);

		_addPortletFileEntry(
			folder1.getFolderId(), fragmentCollection, "Image2");

		Folder folder2 = _addPortletFolder(
			folder1.getFolderId(), _liveGroup.getGroupId(), "Folder2",
			serviceContext);

		_addPortletFileEntry(
			folder2.getFolderId(), fragmentCollection, "Image3");

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FragmentCollection stagingFragmentCollection =
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByUuidAndGroupId(
					fragmentCollection.getUuid(), _stagingGroup.getGroupId());

		Assert.assertNotNull(stagingFragmentCollection);

		Map<String, FileEntry> resourcesMap =
			stagingFragmentCollection.getResourcesMap();

		Assert.assertTrue(resourcesMap.containsKey("Image1"));
		Assert.assertTrue(resourcesMap.containsKey("Folder1/Image2"));
		Assert.assertTrue(resourcesMap.containsKey("Folder1/Folder2/Image3"));
	}

	@Test
	@TestInfo("LPD-57728")
	public void testMarketplaceFragmentEntriesNotCopiedWhenLocalStagingActivated()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_liveGroup.getGroupId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		FragmentEntry marketplaceFragmentEntry =
			FragmentEntryTestUtil.addFragmentEntry(
				fragmentCollection.getFragmentCollectionId());

		marketplaceFragmentEntry.setMarketplace(true);

		_fragmentEntryLocalService.updateFragmentEntry(
			marketplaceFragmentEntry);

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FragmentCollection stagingFragmentCollection =
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByUuidAndGroupId(
					fragmentCollection.getUuid(), _stagingGroup.getGroupId());

		Assert.assertNotNull(stagingFragmentCollection);

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				stagingFragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			fragmentEntries.toString(), 2, fragmentEntries.size());
	}

	@Test
	public void testSingleFragmentResourceCopiedWhenLocalStagingActivated()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_liveGroup.getGroupId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		_addPortletFileEntry(
			fragmentCollection.getResourcesFolderId(), fragmentCollection,
			"Image");

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FragmentCollection stagingFragmentCollection =
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByUuidAndGroupId(
					fragmentCollection.getUuid(), _stagingGroup.getGroupId());

		Assert.assertNotNull(stagingFragmentCollection);

		Map<String, FileEntry> resourcesMap =
			stagingFragmentCollection.getResourcesMap();

		Assert.assertTrue(resourcesMap.containsKey("Image"));
	}

	private FileEntry _addPortletFileEntry(
			long folderId, FragmentCollection fragmentCollection, String name)
		throws Exception {

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, fragmentCollection.getGroupId(), TestPropsValues.getUserId(),
			FragmentCollection.class.getName(),
			fragmentCollection.getFragmentCollectionId(),
			FragmentPortletKeys.FRAGMENT, folderId,
			new UnsyncByteArrayInputStream(new byte[0]), name,
			ContentTypes.IMAGE_PNG, false);
	}

	private Folder _addPortletFolder(
			long folderId, long groupId, String name,
			ServiceContext serviceContext)
		throws Exception {

		return PortletFileRepositoryUtil.addPortletFolder(
			groupId, TestPropsValues.getUserId(), FragmentPortletKeys.FRAGMENT,
			folderId, name, serviceContext);
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private Group _stagingGroup;

}