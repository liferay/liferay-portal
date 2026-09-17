/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.util.function.LongFunction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class FolderTitleLookupImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Bundle bundle = FrameworkUtil.getBundle(
			FolderTitleLookupImplTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.portal.search.web");

		_folderSearcherConstructor = bundle.loadClass(
			_PACKAGE_NAME + ".FolderSearcher"
		).getConstructor(
			Long.TYPE
		);

		_folderTitleLookupImplConstructor = bundle.loadClass(
			_PACKAGE_NAME + ".FolderTitleLookupImpl"
		).getConstructor(
			LongFunction.class, HttpServletRequest.class
		);
	}

	@Test
	public void testGetFolderTitle() throws Exception {
		Assert.assertNull(_getFolderTitle(RandomTestUtil.randomLong()));

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_group.getGroupId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		ObjectEntryFolder childObjectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_group.getGroupId(),
				objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(
			objectEntryFolder.getName(),
			_getFolderTitle(objectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			childObjectEntryFolder.getName(),
			_getFolderTitle(childObjectEntryFolder.getObjectEntryFolderId()));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private String _getFolderTitle(long folderId) throws Exception {
		LongFunction<Object> folderSearcherFunction = this::_newFolderSearcher;

		return ReflectionTestUtil.invoke(
			_folderTitleLookupImplConstructor.newInstance(
				folderSearcherFunction, _getMockHttpServletRequest()),
			"getFolderTitle", new Class<?>[] {Long.TYPE}, folderId);
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private Object _newFolderSearcher(long folderId) {
		try {
			return _folderSearcherConstructor.newInstance(folderId);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	private static final String _PACKAGE_NAME =
		"com.liferay.portal.search.web.internal.facet.display.context";

	@Inject
	private CompanyLocalService _companyLocalService;

	private Constructor<?> _folderSearcherConstructor;
	private Constructor<?> _folderTitleLookupImplConstructor;

	@DeleteAfterTestRun
	private Group _group;

}