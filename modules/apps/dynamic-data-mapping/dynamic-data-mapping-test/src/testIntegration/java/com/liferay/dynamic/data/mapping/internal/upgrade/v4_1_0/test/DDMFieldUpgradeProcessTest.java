/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DDMFieldUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_ddmFieldLocalService.deleteDDMFormValues(_contentId);
		_ddmStorageLinkLocalService.deleteClassStorageLink(_contentId);
	}

	@Test
	public void testUpgradeProcessWithInstanceIdsDifferingInCase()
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(DDLRecordSet.class), _group);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field1", "field2");

		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			ddmForm, "json");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		DDMContent ddmContent = _ddmContentLocalService.createDDMContent(
			RandomTestUtil.nextLong());

		ddmContent.setGroupId(_group.getGroupId());
		ddmContent.setCompanyId(_group.getCompanyId());
		ddmContent.setUserId(_group.getCreatorUserId());
		ddmContent.setName(RandomTestUtil.randomString());
		ddmContent.setData(
			JSONUtil.put(
				"availableLanguageIds", JSONUtil.putAll("en_US")
			).put(
				"defaultLanguageId", "en_US"
			).put(
				"fieldValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"instanceId", "abcd"
					).put(
						"name", "field1"
					).put(
						"value", JSONUtil.put("en_US", "value1")
					),
					JSONUtil.put(
						"instanceId", "ABCD"
					).put(
						"name", "field2"
					).put(
						"value", JSONUtil.put("en_US", "value2")
					))
			).toString());

		ddmContent = _ddmContentLocalService.addDDMContent(ddmContent);

		_contentId = ddmContent.getContentId();

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getStructureVersion();

		_ddmStorageLinkLocalService.addStorageLink(
			PortalUtil.getClassNameId(DDMContent.class), _contentId,
			ddmStructureVersion.getStructureVersionId(), serviceContext);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0." +
				"DDMFieldUpgradeProcess");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0." +
					"DDMFieldUpgradeProcess",
				LoggerTestUtil.WARN)) {

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith(
					"Replaced duplicate instance ID \"ABCD\" of field " +
						"\"field2\" in storage " + _contentId));
		}

		_entityCache.clearCache();
		_multiVMPool.clear();

		DDMFormValues ddmFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmForm, _contentId);

		DDMFormFieldValue ddmFormFieldValue1 =
			ddmFormValues.getDDMFormFieldValue("field1", false);

		Assert.assertEquals("abcd", ddmFormFieldValue1.getInstanceId());

		Value value1 = ddmFormFieldValue1.getValue();

		Assert.assertEquals("value1", value1.getString(LocaleUtil.US));

		DDMFormFieldValue ddmFormFieldValue2 =
			ddmFormValues.getDDMFormFieldValue("field2", false);

		Assert.assertFalse(
			StringUtil.equalsIgnoreCase(
				"abcd", ddmFormFieldValue2.getInstanceId()));

		Value value2 = ddmFormFieldValue2.getValue();

		Assert.assertEquals("value2", value2.getString(LocaleUtil.US));
	}

	private long _contentId;

	@Inject
	private DDMContentLocalService _ddmContentLocalService;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}