/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.model.adapter.StagedAssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Bowerman
 */
@RunWith(Arquillian.class)
public class AssetEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testDeleteEntry() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = GroupTestUtil.addGroup();

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		group = group.getStagingGroup();

		AssetEntry assetEntry1 = _addAssetEntryWithClassUUID(
			group.getGroupId());
		AssetEntry assetEntry2 = _addAssetEntryWithClassUUID(
			group.getGroupId());

		AssetLink assetLink = _assetLinkLocalService.addLink(
			TestPropsValues.getUserId(), assetEntry1.getEntryId(),
			assetEntry2.getEntryId(), 0, 0);

		_assetEntryLocalService.deleteEntry(assetEntry2);

		SystemEvent systemEvent = _systemEventLocalService.fetchSystemEvent(
			group.getGroupId(),
			_portal.getClassNameId(StagedAssetLink.class.getName()),
			assetLink.getLinkId(), SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(
			assetEntry1.getClassUuid() + StringPool.POUND +
				assetEntry2.getClassUuid(),
			systemEvent.getClassUuid());
	}

	@Test
	public void testUpdateEntry() throws Exception {
		AssetEntry assetEntry1 = _addAssetEntryWithClassUUID(
			TestPropsValues.getGroupId());

		Assert.assertNull(
			_assetTagLocalService.fetchTag(assetEntry1.getGroupId(), "tag"));

		_assetEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), assetEntry1.getGroupId(), null, null,
			assetEntry1.getClassName(), assetEntry1.getClassPK(),
			assetEntry1.getClassUuid(), assetEntry1.getClassTypeId(), null,
			new String[] {"tag"}, false, false, null, null, null, null,
			assetEntry1.getMimeType(), assetEntry1.getTitle(),
			assetEntry1.getDescription(), assetEntry1.getSummary(),
			assetEntry1.getUrl(), assetEntry1.getLayoutUuid(),
			assetEntry1.getHeight(), assetEntry1.getWidth(),
			assetEntry1.getPriority(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNotNull(
			_assetTagLocalService.fetchTag(assetEntry1.getGroupId(), "tag"));

		Group group = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		AssetEntry assetEntry2 = _addAssetEntryWithClassUUID(
			TestPropsValues.getGroupId());

		Assert.assertNull(
			_assetTagLocalService.fetchTag(group.getGroupId(), "tag"));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute("assetTagScopeGroupId", group.getGroupId());

		_assetEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), assetEntry2.getGroupId(), null, null,
			assetEntry2.getClassName(), assetEntry2.getClassPK(),
			assetEntry2.getClassUuid(), assetEntry2.getClassTypeId(), null,
			new String[] {"tag"}, false, false, null, null, null, null,
			assetEntry2.getMimeType(), assetEntry2.getTitle(),
			assetEntry2.getDescription(), assetEntry2.getSummary(),
			assetEntry2.getUrl(), assetEntry2.getLayoutUuid(),
			assetEntry2.getHeight(), assetEntry2.getWidth(),
			assetEntry2.getPriority(), serviceContext);

		Assert.assertNotNull(
			_assetTagLocalService.fetchTag(group.getGroupId(), "tag"));
	}

	private AssetEntry _addAssetEntryWithClassUUID(long groupId) {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(groupId);

		assetEntry.setClassUuid(PortalUUIDUtil.generate());

		return _assetEntryLocalService.updateAssetEntry(assetEntry);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetLinkLocalService _assetLinkLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}