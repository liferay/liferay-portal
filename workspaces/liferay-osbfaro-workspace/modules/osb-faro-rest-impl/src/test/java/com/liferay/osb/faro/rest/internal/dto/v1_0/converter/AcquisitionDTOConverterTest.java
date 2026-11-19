/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAcquisitionsPageResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Pinheiro
 */
public class AcquisitionDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_acquisitionDTOConverter.toDTO(null, null));

		GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition
			composition =
				new GetWorkspaceGroupChannelAcquisitionsPageResponse.
					Composition();

		composition.setCount(RandomTestUtil.randomLong());
		composition.setName(RandomTestUtil.randomString());

		Acquisition acquisition = _acquisitionDTOConverter.toDTO(
			null, composition);

		Assert.assertEquals(composition.getCount(), acquisition.getCount());
		Assert.assertEquals(composition.getName(), acquisition.getName());
	}

	private final AcquisitionDTOConverter _acquisitionDTOConverter =
		new AcquisitionDTOConverter();

}