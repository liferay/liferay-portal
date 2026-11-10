/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceTest extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-53245")
	public void testPatchCustomObjectEntry() throws Exception {
		assertPatchCustomObjectEntry(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			getObjectDefinition1(), getObjectDefinition2(), testGroupId);
		assertPatchCustomObjectEntry(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutCustomObjectEntry() throws Exception {
		assertPutCustomObjectEntry(
			jsonObject ->
				getObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			getObjectDefinition1(), getObjectDefinition2(), testGroupId);
		assertPutCustomObjectEntry(
			jsonObject ->
				getSiteScopedObjectDefinition1().getRESTContextPath() + "/" +
					jsonObject.getLong("id"),
			getSiteScopedObjectDefinition1(), getSiteScopedObjectDefinition2(),
			testGroupId);
	}

}