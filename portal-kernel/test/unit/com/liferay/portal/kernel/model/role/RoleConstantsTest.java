/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model.role;

import com.liferay.portal.kernel.model.Role;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class RoleConstantsTest {

	@Test
	public void testIsUnmodifiable() {
		Assert.assertFalse(RoleConstants.isUnmodifiable(null));
		Assert.assertFalse(
			RoleConstants.isUnmodifiable(Mockito.mock(Role.class)));

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			RoleConstants.CMS_ADMINISTRATOR
		);

		Assert.assertTrue(RoleConstants.isUnmodifiable(role));

		role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			RoleConstants.CRYPTO_OFFICER
		);

		Assert.assertTrue(RoleConstants.isUnmodifiable(role));

		role = Mockito.mock(Role.class);

		Mockito.when(
			role.isSystem()
		).thenReturn(
			true
		);

		Assert.assertTrue(RoleConstants.isUnmodifiable(role));
	}

	@Test
	public void testToSystemRoleExternalReferenceCode() {
		Assert.assertEquals(
			"L_AA", RoleConstants.toSystemRoleExternalReferenceCode("AA"));
		Assert.assertEquals(
			"L_AA", RoleConstants.toSystemRoleExternalReferenceCode("aA"));
		Assert.assertEquals(
			"L_AA", RoleConstants.toSystemRoleExternalReferenceCode("aa"));
		Assert.assertEquals(
			"L_AA-BB",
			RoleConstants.toSystemRoleExternalReferenceCode("aa-bb"));
		Assert.assertEquals(
			"L_AA_BB",
			RoleConstants.toSystemRoleExternalReferenceCode("aa bb"));
		Assert.assertEquals(
			"L_AA_BB",
			RoleConstants.toSystemRoleExternalReferenceCode("aa_bb"));
		Assert.assertEquals(
			"L_AA_BB_CC",
			RoleConstants.toSystemRoleExternalReferenceCode("aa bb cc"));
	}

}