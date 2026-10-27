/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.osb.faro.rest.dto.v1_0.Individual;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class FaroEnumUtilTest {

	@Test
	public void testCreate() {
		Assert.assertEquals(
			Individual.ProfileType.KNOWN,
			FaroEnumUtil.create(Individual.ProfileType::create, "KNOWN"));
		Assert.assertNull(
			FaroEnumUtil.create(Individual.ProfileType::create, "UNKNOWN"));
		Assert.assertNull(
			FaroEnumUtil.create(Individual.ProfileType::create, null));
	}

}