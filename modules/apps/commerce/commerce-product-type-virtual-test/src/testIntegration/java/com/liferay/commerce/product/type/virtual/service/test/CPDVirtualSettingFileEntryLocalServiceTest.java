/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingURLException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CPDVirtualSettingFileEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddCPDVirtualSettingFileEntry() throws Exception {
		Assert.assertThrows(
			CPDefinitionVirtualSettingURLException.class,
			() ->
				_cpdVirtualSettingFileEntryLocalService.
					addCPDVirtualSettingFileEntry(
						TestPropsValues.getUserId(),
						TestPropsValues.getGroupId(),
						RandomTestUtil.randomLong(), 0,
						"file:///" + RandomTestUtil.randomString(),
						RandomTestUtil.randomString()));
	}

	@Test
	public void testUpdateCPDVirtualSettingFileEntry() throws Exception {
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryLocalService.
				createCPDVirtualSettingFileEntry(RandomTestUtil.nextLong());

		cpdVirtualSettingFileEntry.setCompanyId(TestPropsValues.getCompanyId());

		_cpdVirtualSettingFileEntryLocalService.addCPDVirtualSettingFileEntry(
			cpdVirtualSettingFileEntry);

		Assert.assertThrows(
			CPDefinitionVirtualSettingURLException.class,
			() ->
				_cpdVirtualSettingFileEntryLocalService.
					updateCPDVirtualSettingFileEntry(
						cpdVirtualSettingFileEntry.
							getCPDefinitionVirtualSettingFileEntryId(),
						0, "file:///" + RandomTestUtil.randomString(),
						RandomTestUtil.randomString()));
	}

	@Inject
	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

}