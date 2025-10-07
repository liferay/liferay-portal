/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DepotEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddDepotEntry() throws Exception {
		_assertObjectEntryFolders(
			_depotEntryLocalService.addDepotEntry(
				HashMapBuilder.put(
					LocaleUtil.getDefault(), StringUtil.randomString()
				).build(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), StringUtil.randomString()
				).build(),
				DepotConstants.TYPE_ASSET_LIBRARY,
				ServiceContextTestUtil.getServiceContext()));

		Group group = GroupTestUtil.addGroup();

		group.setType(GroupConstants.TYPE_DEPOT);

		group = _groupLocalService.updateGroup(group);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute("staging", Boolean.TRUE);

		_assertObjectEntryFolders(
			_depotEntryLocalService.addDepotEntry(group, serviceContext));
	}

	private void _assertObjectEntryFolders(DepotEntry depotEntry) {
		Assert.assertEquals(
			2,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				depotEntry.getGroupId(), depotEntry.getCompanyId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));

		AssertUtils.assertEquals(
			Arrays.asList("Contents", "Files"),
			ListUtil.sort(
				ListUtil.toList(
					_objectEntryFolderLocalService.getObjectEntryFolders(
						depotEntry.getGroupId(), depotEntry.getCompanyId(),
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					ObjectEntryFolder::getName)));
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}