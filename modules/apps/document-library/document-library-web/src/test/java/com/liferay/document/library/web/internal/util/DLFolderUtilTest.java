/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alicia García
 */
public class DLFolderUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidateDepotFolder() throws PortalException {
		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		long depotGroupId = RandomTestUtil.randomLong();

		Group depotGroup = _getDepotGroup(depotGroupId);

		ReflectionTestUtil.setFieldValue(
			GroupLocalServiceUtil.class, "_service", groupLocalService);

		Mockito.when(
			groupLocalService.getGroup(Mockito.anyLong())
		).thenReturn(
			depotGroup
		);

		DepotEntryLocalService depotEntryLocalService = Mockito.mock(
			DepotEntryLocalService.class);

		List<DepotEntry> depotEntries = _getGroupConnectedDepotEntries(
			depotGroupId);

		ReflectionTestUtil.setFieldValue(
			DepotEntryLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<DepotEntryLocalService>(
				DepotEntryLocalServiceUtil.class,
				DepotEntryLocalService.class) {

				@Override
				public DepotEntryLocalService get() {
					return depotEntryLocalService;
				}

			});

		Mockito.when(
			depotEntryLocalService.getGroupConnectedDepotEntries(
				Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt())
		).thenReturn(
			depotEntries
		);

		DLFolderUtil.validateDepotFolder(
			RandomTestUtil.randomLong(), depotGroup.getGroupId(),
			RandomTestUtil.randomLong());
	}

	@Test(expected = NoSuchFolderException.class)
	public void testValidateDepotFolderNotConnected() throws PortalException {
		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		long depotGroupId = RandomTestUtil.randomLong();

		Group depotGroup = _getDepotGroup(depotGroupId);

		Mockito.when(
			groupLocalService.getGroup(Mockito.anyLong())
		).thenReturn(
			depotGroup
		);

		ReflectionTestUtil.setFieldValue(
			GroupLocalServiceUtil.class, "_service", groupLocalService);

		DepotEntryLocalService depotEntryLocalService = Mockito.mock(
			DepotEntryLocalService.class);

		List<DepotEntry> depotEntries = _getGroupConnectedDepotEntries(
			RandomTestUtil.randomLong());

		Mockito.when(
			depotEntryLocalService.getGroupConnectedDepotEntries(
				Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt())
		).thenReturn(
			depotEntries
		);

		ReflectionTestUtil.setFieldValue(
			DepotEntryLocalServiceUtil.class, "_serviceSnapshot",
			new Snapshot<DepotEntryLocalService>(
				DepotEntryLocalServiceUtil.class,
				DepotEntryLocalService.class) {

				@Override
				public DepotEntryLocalService get() {
					return depotEntryLocalService;
				}

			});

		DLFolderUtil.validateDepotFolder(
			RandomTestUtil.randomLong(), depotGroup.getGroupId(),
			RandomTestUtil.randomLong());
	}

	private DepotEntry _addDepotEntry(long depotGroupId) {
		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.doReturn(
			depotGroupId
		).when(
			depotEntry
		).getGroupId();

		return depotEntry;
	}

	private Group _getDepotGroup(long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.doReturn(
			groupId
		).when(
			group
		).getGroupId();

		Mockito.doReturn(
			true
		).when(
			group
		).isDepot();

		return group;
	}

	private List<DepotEntry> _getGroupConnectedDepotEntries(long depotGroupId) {
		return ListUtil.fromArray(_addDepotEntry(depotGroupId));
	}

}