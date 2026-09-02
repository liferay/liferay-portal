/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.entry.processor.analytics.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.entry.processor.analytics.AnalyticsAttributesContributor;
import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcos Martins
 */
@FeatureFlag("LPD-58677")
@RunWith(Arquillian.class)
public class CMPAnalyticsAttributesContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(CMPAnalyticsAttributesContributorTest.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetAnalyticsAttributesWhenObjectIsNotObjectEntry()
		throws Exception {

		Assert.assertEquals(
			Collections.emptyMap(),
			_analyticsAttributesContributor.getAnalyticsAttributes(
				new InfoItemFieldMapped(
					RandomTestUtil.randomString(),
					new InfoItemReference(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomLong()),
					RandomTestUtil.randomString()),
				LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testGetAnalyticsAttributesWithCMPProjectLink()
		throws Exception {

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry objectEntry = _addCMSBasicWebContentObjectEntry();

		CMPTestUtil.addCMPProjectLinkObjectEntry(
			cmpProjectObjectEntry, objectEntry);

		_assertAnalyticsCMPProjects(cmpProjectObjectEntry, objectEntry);
	}

	@Test
	public void testGetAnalyticsAttributesWithCMPTaskLink() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry objectEntry = _addCMSBasicWebContentObjectEntry();

		CMPTestUtil.addCMPTaskLinkObjectEntry(
			CMPTestUtil.addCMPTaskObjectEntry(cmpProjectObjectEntry),
			objectEntry);

		_assertAnalyticsCMPProjects(cmpProjectObjectEntry, objectEntry);
	}

	@Test
	public void testGetAnalyticsAttributesWithoutCMPProjectLink()
		throws Exception {

		Assert.assertEquals(
			Collections.emptyMap(),
			_getAnalyticsAttributes(_addCMSBasicWebContentObjectEntry()));
	}

	private ObjectEntry _addCMSBasicWebContentObjectEntry() throws Exception {
		return CMPTestUtil.addCMSBasicWebContentObjectEntry(
			_depotEntry, RandomTestUtil.randomString());
	}

	private void _assertAnalyticsCMPProjects(
			ObjectEntry cmpProjectObjectEntry, ObjectEntry objectEntry)
		throws Exception {

		Map<String, Object> analyticsAttributes = _getAnalyticsAttributes(
			objectEntry);

		Assert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", cmpProjectObjectEntry.getObjectEntryId()
				).put(
					"name", cmpProjectObjectEntry.getTitleValue()
				)
			).toString(),
			analyticsAttributes.get("analytics-cmp-projects"));
	}

	private Map<String, Object> _getAnalyticsAttributes(ObjectEntry objectEntry)
		throws Exception {

		return _analyticsAttributesContributor.getAnalyticsAttributes(
			new InfoItemFieldMapped(
				RandomTestUtil.randomString(),
				new InfoItemReference(
					objectEntry.getModelClassName(),
					objectEntry.getObjectEntryId()),
				objectEntry),
			LocaleUtil.getSiteDefault());
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.entry.processor.analytics.CMPAnalyticsAttributesContributor"
	)
	private AnalyticsAttributesContributor _analyticsAttributesContributor;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}