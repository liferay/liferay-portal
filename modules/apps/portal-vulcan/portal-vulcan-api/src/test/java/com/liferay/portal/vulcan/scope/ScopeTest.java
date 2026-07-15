/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.scope;

import com.liferay.portal.kernel.group.capability.GroupCapabilityUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class ScopeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_group = Mockito.mock(Group.class);

		Mockito.when(
			_group.getExternalReferenceCode()
		).thenReturn(
			_EXTERNAL_REFERENCE_CODE
		);
	}

	@Test
	public void testScopeOfExportImportCompanyGroup() {
		MockedStatic<GroupCapabilityUtil> groupCapabilityUtilMockedStatic =
			Mockito.mockStatic(GroupCapabilityUtil.class);

		groupCapabilityUtilMockedStatic.when(
			() -> GroupCapabilityUtil.isSupportsScope(_group)
		).thenReturn(
			false
		);

		Assert.assertNull(Scope.of(_group, null));

		groupCapabilityUtilMockedStatic.close();
	}

	@Test
	public void testScopeOfNull() {
		Assert.assertNull(Scope.of(null, null));
	}

	@Test
	public void testScopeOfTypeAssetLibrary() {
		Mockito.when(
			_group.isDepot()
		).thenReturn(
			true
		);

		_assertScope(Scope.of(_group, null), Scope.Type.ASSET_LIBRARY);
	}

	@Test
	public void testScopeOfTypeNull() {
		_assertScope(Scope.of(_group, null), null);
	}

	@Test
	public void testScopeOfTypeSite() {
		Mockito.when(
			_group.isSite()
		).thenReturn(
			true
		);

		_assertScope(Scope.of(_group, null), Scope.Type.SITE);
	}

	@Test
	public void testScopeOfTypeSiteCompany() {
		Mockito.when(
			_group.isSite()
		).thenReturn(
			true
		);

		Mockito.when(
			_group.isCompany()
		).thenReturn(
			true
		);

		_assertScope(Scope.of(_group, null), Scope.Type.SITE);
	}

	@Test
	public void testScopeOfTypeSpace() {
		Mockito.when(
			_group.isCMS()
		).thenReturn(
			true
		);

		_assertScope(Scope.of(_group, null), Scope.Type.SPACE);
	}

	private void _assertScope(Scope scope, Scope.Type scopeType) {
		Assert.assertEquals(
			_EXTERNAL_REFERENCE_CODE, scope.getExternalReferenceCode());
		Assert.assertEquals(scopeType, scope.getType());
	}

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private Group _group;

}