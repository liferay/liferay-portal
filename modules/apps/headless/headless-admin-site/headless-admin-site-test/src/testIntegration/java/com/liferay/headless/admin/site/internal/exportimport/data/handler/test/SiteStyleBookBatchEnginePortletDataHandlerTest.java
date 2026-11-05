/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.constants.StyleBookPortletKeys;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class SiteStyleBookBatchEnginePortletDataHandlerTest
	extends BaseStyleBookBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testEnableLocalStaging() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		Group group = getGroup();

		addEntry(externalReferenceCode, group.getGroupId());

		GroupTestUtil.enableLocalStaging(group);

		Assert.assertFalse(
			group.isStagedPortlet(StyleBookPortletKeys.STYLE_BOOK));

		Group stagingGroup = group.getStagingGroup();

		Assert.assertFalse(
			hasEntry(externalReferenceCode, stagingGroup.getGroupId()));
	}

	@Test
	public void testGetExportablePortlets() throws Exception {
		Group group = getGroup();

		Assert.assertTrue(hasStyleBookPortlet(group.getGroupId()));
	}

	@Test
	public void testPublishLayouts() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		Group group = getGroup();

		addEntry(externalReferenceCode1, group.getGroupId());

		GroupTestUtil.enableLocalStaging(group);

		String externalReferenceCode2 = RandomTestUtil.randomString();
		Group stagingGroup = group.getStagingGroup();

		addEntry(externalReferenceCode2, stagingGroup.getGroupId());

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			group.getGroupId(), false,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());

		Assert.assertTrue(hasEntry(externalReferenceCode1, group.getGroupId()));
		Assert.assertFalse(
			hasEntry(externalReferenceCode2, group.getGroupId()));
	}

}