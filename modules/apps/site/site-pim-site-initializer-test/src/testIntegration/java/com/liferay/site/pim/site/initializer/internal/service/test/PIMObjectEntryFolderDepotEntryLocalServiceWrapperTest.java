/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectEntryFolderConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-96666"))
@RunWith(Arquillian.class)
public class PIMObjectEntryFolderDepotEntryLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PIMTestUtil.getOrAddGroup();
	}

	@Test
	public void testAddDepotEntry() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		Assert.assertNotNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId()));
		Assert.assertNotNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId()));
		Assert.assertNotNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId()));
	}

	@Test
	public void testDeleteDepotEntry() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		_depotEntryLocalService.deleteDepotEntry(depotEntry);

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), TestPropsValues.getCompanyId()));
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}