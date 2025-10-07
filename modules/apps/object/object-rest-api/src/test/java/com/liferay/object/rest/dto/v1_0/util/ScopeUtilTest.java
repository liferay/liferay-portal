/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0.util;

import com.liferay.object.rest.dto.v1_0.Scope;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class ScopeUtilTest {

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
	public void testToScopeNull() throws Exception {
		Assert.assertNull(ScopeUtil.toScope(null));
	}

	@Test
	public void testToScopeTypeAssetLibrary() throws Exception {
		Mockito.when(
			_group.isDepot()
		).thenReturn(
			true
		);

		_assertScope(ScopeUtil.toScope(_group), Scope.Type.ASSET_LIBRARY);
	}

	@Test
	public void testToScopeTypeCMS() throws Exception {
		Mockito.when(
			_group.isCMS()
		).thenReturn(
			true
		);

		_assertScope(ScopeUtil.toScope(_group), Scope.Type.CMS);
	}

	@Test
	public void testToScopeTypeNull() throws Exception {
		_assertScope(ScopeUtil.toScope(_group), null);
	}

	@Test
	public void testToScopeTypeSite() throws Exception {
		Mockito.when(
			_group.isSite()
		).thenReturn(
			true
		);

		_assertScope(ScopeUtil.toScope(_group), Scope.Type.SITE);
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