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
@FeatureFlag("LPD-58677")
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

		_cmpProjectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", TestPropsValues.getCompanyId());
		_cmpTaskLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK_LINK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testDeleteCMPProjectObjectEntry() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpProjectLinkObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				cmpProjectObjectEntry.getGroupId(),
				cmpProjectObjectEntry.getUserId(),
				_cmpProjectLinkObjectDefinition.getObjectDefinitionId(), 0,
				null,
				HashMapBuilder.<String, Serializable>put(
					"classExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"className", RandomTestUtil.randomString()
				).put(
					"groupExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"r_cmpProjectToCMPProjectLinks_c_cmpProjectId",
					cmpProjectObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpProjectLinkObjectEntry.getObjectEntryId()));
	}

	@Test
	public void testDeleteCMPTaskObjectEntry() throws Exception {
		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry();

		ObjectEntry cmpTaskLinkObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				cmpTaskObjectEntry.getGroupId(), cmpTaskObjectEntry.getUserId(),
				_cmpTaskLinkObjectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"classExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"className", RandomTestUtil.randomString()
				).put(
					"groupExternalReferenceCode", RandomTestUtil.randomString()
				).put(
					"r_cmpTaskToCMPTaskLinks_c_cmpTaskId",
					cmpTaskObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(
			cmpTaskObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry.getObjectEntryId()));
	}

	private ObjectDefinition _cmpProjectLinkObjectDefinition;
	private ObjectDefinition _cmpTaskLinkObjectDefinition;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}