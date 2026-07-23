/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-58677")}
)
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(ObjectEntryLocalServiceTest.class);

		_projectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", TestPropsValues.getCompanyId());
		_taskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testDeleteProjectObjectEntry() throws Exception {
		ObjectEntry projectObjectEntry = CMPTestUtil.addProjectObjectEntry();

		ObjectEntry projectLinkObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				projectObjectEntry.getGroupId(), projectObjectEntry.getUserId(),
				_projectLinkObjectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"classExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"className", RandomTestUtil.randomString()
				).put(
					"groupExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
					projectObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(
			projectObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				projectLinkObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testDeleteTaskObjectEntry() throws Exception {
		ObjectEntry taskObjectEntry = CMPTestUtil.addTaskObjectEntry();

		ObjectEntry taskLinkObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				taskObjectEntry.getGroupId(), taskObjectEntry.getUserId(),
				_taskLinkObjectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"classExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"className", RandomTestUtil.randomString()
				).put(
					"groupExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
					taskObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(
			taskObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				taskLinkObjectEntry.getObjectEntryId()));
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectDefinition _projectLinkObjectDefinition;
	private ObjectDefinition _taskLinkObjectDefinition;

}