/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Víctor Galán
 */
public class FragmentCollectionManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPS-162848")
	public void testGetLayoutElementMapsListMap() {
		ReflectionTestUtil.setFieldValue(
			_fragmentCollectionManager, "_infoItemServiceRegistry",
			_infoItemServiceRegistry);

		_testGetLayoutElementMapsListMapWithoutEditPageInfoItemCapability();
		_testGetLayoutElementMapsListMapWithoutInfoPermissionProvider();
		_testGetLayoutElementMapsListMapWithoutViewPermission();
		_testGetLayoutElementMapsListMapWithViewPermission();
	}

	private void _setUpInfoItemClassDetails() {
		InfoItemClassDetails infoItemClassDetails = Mockito.mock(
			InfoItemClassDetails.class);

		Mockito.when(
			infoItemClassDetails.getClassName()
		).thenReturn(
			_CLASS_NAME
		);

		Mockito.when(
			_infoItemServiceRegistry.getInfoItemClassDetails(
				EditPageInfoItemCapability.KEY)
		).thenReturn(
			ListUtil.fromArray(infoItemClassDetails)
		);
	}

	private void _testGetLayoutElementMapsListMapWithoutEditPageInfoItemCapability() {
		Mockito.when(
			_infoItemServiceRegistry.getInfoItemClassDetails(
				EditPageInfoItemCapability.KEY)
		).thenReturn(
			Collections.emptyList()
		);

		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			_fragmentCollectionManager.getLayoutElementMapsListMap(
				_permissionChecker);

		Assert.assertFalse(layoutElementMapsListMap.containsKey("INPUTS"));
	}

	private void _testGetLayoutElementMapsListMapWithoutInfoPermissionProvider() {
		_setUpInfoItemClassDetails();

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, _CLASS_NAME)
		).thenReturn(
			null
		);

		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			_fragmentCollectionManager.getLayoutElementMapsListMap(
				_permissionChecker);

		Assert.assertTrue(layoutElementMapsListMap.containsKey("INPUTS"));
	}

	private void _testGetLayoutElementMapsListMapWithoutViewPermission() {
		_setUpInfoItemClassDetails();

		InfoPermissionProvider<?> infoPermissionProvider = Mockito.mock(
			InfoPermissionProvider.class);

		Mockito.when(
			infoPermissionProvider.hasViewPermission(_permissionChecker)
		).thenReturn(
			false
		);

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, _CLASS_NAME)
		).thenReturn(
			infoPermissionProvider
		);

		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			_fragmentCollectionManager.getLayoutElementMapsListMap(
				_permissionChecker);

		Assert.assertFalse(layoutElementMapsListMap.containsKey("INPUTS"));
	}

	private void _testGetLayoutElementMapsListMapWithViewPermission() {
		_setUpInfoItemClassDetails();

		InfoPermissionProvider<?> infoPermissionProvider = Mockito.mock(
			InfoPermissionProvider.class);

		Mockito.when(
			infoPermissionProvider.hasViewPermission(_permissionChecker)
		).thenReturn(
			true
		);

		Mockito.when(
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, _CLASS_NAME)
		).thenReturn(
			infoPermissionProvider
		);

		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			_fragmentCollectionManager.getLayoutElementMapsListMap(
				_permissionChecker);

		Assert.assertTrue(layoutElementMapsListMap.containsKey("INPUTS"));
	}

	private static final String _CLASS_NAME =
		"com.liferay.object.model.ObjectEntry";

	private final FragmentCollectionManager _fragmentCollectionManager =
		new FragmentCollectionManager();
	private final InfoItemServiceRegistry _infoItemServiceRegistry =
		Mockito.mock(InfoItemServiceRegistry.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}