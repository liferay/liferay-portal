/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.definition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;

import jakarta.portlet.PortletPreferences;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class PortletRatingsDefinitionUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_blogsEntryPropertyKey = RatingsDataTransformerUtil.getPropertyKey(
			BlogsEntry.class.getName());

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			TestPropsValues.getCompanyId());

		_originalBlogsEntryRatingsType = portletPreferences.getValue(
			_blogsEntryPropertyKey, null);
	}

	@After
	public void tearDown() throws Exception {
		if (_originalBlogsEntryRatingsType == null) {
			_companyLocalService.removePreferences(
				TestPropsValues.getCompanyId(),
				new String[] {_blogsEntryPropertyKey});
		}
		else {
			_companyLocalService.updatePreferences(
				TestPropsValues.getCompanyId(),
				UnicodePropertiesBuilder.put(
					_blogsEntryPropertyKey, _originalBlogsEntryRatingsType
				).build());
		}
	}

	@Test
	@TestInfo("LPD-115333")
	public void testGetRatingsType() throws Exception {
		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				_blogsEntryPropertyKey, RatingsType.STARS.getValue()
			).build());

		_group1 = GroupTestUtil.addGroup();

		_group2 = GroupTestUtil.addGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			_group2.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			_blogsEntryPropertyKey, RatingsType.STACKED_STARS.getValue());
		typeSettingsUnicodeProperties.setProperty(
			RatingsDataTransformerUtil.getPropertyKey(
				DLFileEntry.class.getName()),
			RatingsType.LIKE.getValue());

		_groupLocalService.updateGroup(
			_group2.getGroupId(), typeSettingsUnicodeProperties.toString());

		Assert.assertEquals(
			RatingsType.LIKE,
			PortletRatingsDefinitionUtil.getRatingsType(
				TestPropsValues.getCompanyId(), _group2.getGroupId(),
				DLFileEntry.class.getName()));
		Assert.assertEquals(
			RatingsType.STACKED_STARS,
			PortletRatingsDefinitionUtil.getRatingsType(
				TestPropsValues.getCompanyId(), _group2.getGroupId(),
				BlogsEntry.class.getName()));
		Assert.assertEquals(
			RatingsType.STARS,
			PortletRatingsDefinitionUtil.getRatingsType(
				TestPropsValues.getCompanyId(), _group1.getGroupId(),
				BlogsEntry.class.getName()));
		Assert.assertEquals(
			RatingsType.STARS,
			PortletRatingsDefinitionUtil.getRatingsType(
				TestPropsValues.getCompanyId(), _group1.getGroupId(),
				DLFileEntry.class.getName()));
	}

	private String _blogsEntryPropertyKey;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private GroupLocalService _groupLocalService;

	private String _originalBlogsEntryRatingsType;

}