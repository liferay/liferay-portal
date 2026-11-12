/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import jakarta.portlet.Portlet;
import jakarta.portlet.ResourceRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class ActionUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetTrashEntries() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group1 = GroupTestUtil.addGroup();

		KBFolder kbFolder = KBTestUtil.addKBFolder(group1.getGroupId());

		_kbFolderLocalService.moveKBFolderToTrash(
			TestPropsValues.getUserId(), kbFolder.getKbFolderId());

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			KBFolder.class.getName(), kbFolder.getKbFolderId());

		Assert.assertEquals(
			Collections.singletonList(trashEntry),
			_getTrashEntries(group1.getGroupId(), trashEntry.getEntryId()));

		Group group2 = GroupTestUtil.addGroup();

		Assert.assertEquals(
			Collections.emptyList(),
			_getTrashEntries(group2.getGroupId(), trashEntry.getEntryId()));
	}

	private List<TrashEntry> _getTrashEntries(
			long scopeGroupId, long trashEntryId)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(scopeGroupId);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"rowIds", String.valueOf(trashEntryId));

		Class<?> clazz = _portlet.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return ReflectionTestUtil.invoke(
			classLoader.loadClass(
				"com.liferay.trash.web.internal.portlet.action.ActionUtil"),
			"getTrashEntries", new Class<?>[] {ResourceRequest.class},
			mockLiferayResourceRequest);
	}

	@Inject
	private KBFolderLocalService _kbFolderLocalService;

	@Inject(
		filter = "component.name=com.liferay.trash.web.internal.portlet.TrashPortlet"
	)
	private Portlet _portlet;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

}