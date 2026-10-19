/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.upgrade.v2_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Larissa Ribeiro
 */
@RunWith(Arquillian.class)
public class CMPDescriptionObjectFieldUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			CMPDescriptionObjectFieldUpgradeProcessTest.class);

		_cmpProjectObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());
		_cmpTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectEntry cmpProjectObjectEntry1 = _addCMPProjectObjectEntry(
			"<p>Already <strong>rich</strong></p>");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.content.marketing.platform.web.internal.upgrade." +
				"v2_0_0.CMPDescriptionObjectFieldUpgradeProcess");

		upgradeProcess.upgrade();

		Assert.assertEquals(
			"<p>Already <strong>rich</strong></p>",
			_getDescription(cmpProjectObjectEntry1));

		_updateObjectField(_cmpProjectObjectDefinition);
		_updateObjectField(_cmpTaskObjectDefinition);

		ObjectEntry cmpProjectObjectEntry2 = _addCMPProjectObjectEntry(
			"First line\nSecond line\n\nSecond paragraph with <b> & " +
				"\"quotes\"");

		ObjectEntry cmpTaskObjectEntry1 = _addCMPTaskObjectEntry(
			cmpProjectObjectEntry2, "Task text");
		ObjectEntry cmpTaskObjectEntry2 = _addCMPTaskObjectEntry(
			cmpProjectObjectEntry2, null);

		upgradeProcess.upgrade();

		_assertObjectField(_cmpProjectObjectDefinition);
		_assertObjectField(_cmpTaskObjectDefinition);

		Assert.assertEquals(
			"<p>First line<br>Second line</p><p>Second paragraph with " +
				"&lt;b&gt; &amp; &#34;quotes&#34;</p>",
			_getDescription(cmpProjectObjectEntry2));
		Assert.assertEquals(
			"<p>Task text</p>", _getDescription(cmpTaskObjectEntry1));
		Assert.assertTrue(
			Validator.isBlank(_getDescription(cmpTaskObjectEntry2)));
	}

	private ObjectEntry _addCMPProjectObjectEntry(String description)
		throws Exception {

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"description", description
				).build());

		_depotEntries.add(
			_depotEntryLocalService.fetchGroupDepotEntry(
				cmpProjectObjectEntry.getGroupId()));

		return cmpProjectObjectEntry;
	}

	private ObjectEntry _addCMPTaskObjectEntry(
			ObjectEntry cmpProjectObjectEntry, String description)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			cmpProjectObjectEntry.getGroupId(),
			cmpProjectObjectEntry.getUserId(),
			_cmpTaskObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"description", description
			).put(
				"r_cmpProjectToCMPTasks_c_cmpProjectId",
				cmpProjectObjectEntry.getObjectEntryId()
			).put(
				"title", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertObjectField(ObjectDefinition objectDefinition) {
		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "description");

		Assert.assertEquals(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			objectField.getBusinessType());
	}

	private String _getDescription(ObjectEntry objectEntry) throws Exception {
		return MapUtil.getString(
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()),
			"description", null);
	}

	private void _updateObjectField(ObjectDefinition objectDefinition) {
		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "description");

		objectField.setBusinessType(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT);

		_objectFieldLocalService.updateObjectField(objectField);
	}

	private ObjectDefinition _cmpProjectObjectDefinition;
	private ObjectDefinition _cmpTaskObjectDefinition;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.marketing.platform.web.internal.upgrade.registry.CMPSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}