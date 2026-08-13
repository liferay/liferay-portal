/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class DesignLibraryResourceTypeContributorRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDesignLibraryResourceTypeContributors() {
		_testGetDesignLibraryResourceTypeContributors();
		_testGetDesignLibraryResourceTypeContributorsWithoutContributors();
		_testGetDesignLibraryResourceTypeContributorsWithoutViewPermission();
	}

	private DesignLibraryResourceTypeContributor
		_createDesignLibraryResourceTypeContributor(boolean hasViewPermission) {

		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor = Mockito.mock(
				DesignLibraryResourceTypeContributor.class);

		Mockito.when(
			designLibraryResourceTypeContributor.hasViewPermission(
				_permissionChecker, _depotEntry)
		).thenReturn(
			hasViewPermission
		);

		return designLibraryResourceTypeContributor;
	}

	private List<DesignLibraryResourceTypeContributor>
		_getDesignLibraryResourceTypeContributors(
			DesignLibraryResourceTypeContributor...
				designLibraryResourceTypeContributors) {

		DesignLibraryResourceTypeContributorRegistryImpl
			designLibraryResourceTypeContributorRegistryImpl =
				new DesignLibraryResourceTypeContributorRegistryImpl();

		ReflectionTestUtil.setFieldValue(
			designLibraryResourceTypeContributorRegistryImpl,
			"_designLibraryResourceTypeContributors",
			Arrays.asList(designLibraryResourceTypeContributors));

		return designLibraryResourceTypeContributorRegistryImpl.
			getDesignLibraryResourceTypeContributors(
				_permissionChecker, _depotEntry);
	}

	private void _testGetDesignLibraryResourceTypeContributors() {
		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor1 =
				_createDesignLibraryResourceTypeContributor(true);
		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor2 =
				_createDesignLibraryResourceTypeContributor(false);
		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor3 =
				_createDesignLibraryResourceTypeContributor(true);

		Assert.assertEquals(
			Arrays.asList(
				designLibraryResourceTypeContributor1,
				designLibraryResourceTypeContributor3),
			_getDesignLibraryResourceTypeContributors(
				designLibraryResourceTypeContributor1,
				designLibraryResourceTypeContributor2,
				designLibraryResourceTypeContributor3));
	}

	private void _testGetDesignLibraryResourceTypeContributorsWithoutContributors() {
		Assert.assertEquals(
			Collections.emptyList(),
			_getDesignLibraryResourceTypeContributors());
	}

	private void _testGetDesignLibraryResourceTypeContributorsWithoutViewPermission() {
		Assert.assertEquals(
			Collections.emptyList(),
			_getDesignLibraryResourceTypeContributors(
				_createDesignLibraryResourceTypeContributor(false)));
	}

	private final DepotEntry _depotEntry = Mockito.mock(DepotEntry.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}