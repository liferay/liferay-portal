/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Gomes
 */
@RunWith(Arquillian.class)
public class CMPProjectSelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFDSFilters() {
		String[] fdsNames = {
			CMSSiteInitializerFDSNames.ALL_SECTION,
			CMSSiteInitializerFDSNames.CONTENTS_SECTION,
			CMSSiteInitializerFDSNames.FILES_SECTION,
			CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION,
			CMSSiteInitializerFDSNames.VIEW_CONTENTS_FOLDER,
			CMSSiteInitializerFDSNames.VIEW_FILES_FOLDER
		};

		for (String fdsName : fdsNames) {
			List<String> fdsFilterIds = TransformUtil.transform(
				_fdsFilterRegistry.getFDSFilters(fdsName), FDSFilter::getId);

			Assert.assertTrue(
				fdsName, fdsFilterIds.contains("cmpProjectObjectEntryIds"));
		}
	}

	@Inject
	private FDSFilterRegistry _fdsFilterRegistry;

}