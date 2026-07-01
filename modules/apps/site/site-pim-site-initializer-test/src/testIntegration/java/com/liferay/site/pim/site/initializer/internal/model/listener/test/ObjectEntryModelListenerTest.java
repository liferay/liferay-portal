/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-96666")}
)
@RunWith(Arquillian.class)
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PIMTestUtil.getOrAddGroup(ObjectEntryModelListenerTest.class);
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry objectEntry = PIMTestUtil.addCatalogObjectEntry();

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		Assert.assertEquals(
			MapUtil.getString(objectEntry.getValues(), "name"),
			group.getName(LocaleUtil.getDefault()));
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectEntry objectEntry = PIMTestUtil.addCatalogObjectEntry();

		String name = RandomTestUtil.randomString();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"name", name
			).build(),
			ServiceContextTestUtil.getServiceContext(objectEntry.getGroupId()));

		Group group = _groupLocalService.fetchGroup(objectEntry.getGroupId());

		Assert.assertEquals(name, group.getName(LocaleUtil.getDefault()));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}