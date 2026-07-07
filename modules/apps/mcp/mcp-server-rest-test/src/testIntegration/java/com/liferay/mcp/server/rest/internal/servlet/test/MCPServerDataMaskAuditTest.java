/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.mcp.server.rest.test.util.MCPServerDataMaskTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class MCPServerDataMaskAuditTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		MCPServerDataMaskTestUtil.updateMCPServerConfiguration(true);

		String dataMaskingPrefix =
			".com.liferay.headless.data.mask.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.headless.data.mask.impl",
			MCPServerDataMaskAuditTest.class,
			new String[] {
				dataMaskingPrefix + "01.list.type.definition",
				dataMaskingPrefix + "02.object.definition",
				dataMaskingPrefix + "03.object.entry"
			});

		String prefix = ".com.liferay.mcp.server.rest.internal.batch.";

		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.mcp.server.rest.impl",
			MCPServerDataMaskAuditTest.class,
			new String[] {
				prefix + "01.object.definition",
				prefix + "02.object.definition",
				prefix + "03.object.definition", prefix + "04.object.entry"
			});
	}

	@After
	public void tearDown() throws Exception {
		MCPServerDataMaskTestUtil.updateMCPServerConfiguration(false);
	}

	@Test
	public void testAuditLogRecordsDataMaskCreate() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry customMaskObjectEntry =
				MCPServerDataMaskTestUtil.addCustomMask(
					RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

			List<AuditEvent> auditEvents = _getAuditEvents(
				customMaskObjectEntry, EventTypes.ADD);

			Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					auditEvents.get(
						0
					).getAdditionalInfo());

			Assert.assertTrue(
				additionalInfoJSONObject.toString(),
				additionalInfoJSONObject.has("detectionRegex"));
			Assert.assertTrue(additionalInfoJSONObject.has("maskType"));
			Assert.assertTrue(additionalInfoJSONObject.has("name"));
			Assert.assertTrue(additionalInfoJSONObject.has("replacementValue"));
		}
	}

	@Test
	public void testAuditLogRecordsDataMaskDelete() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry customMaskObjectEntry =
				MCPServerDataMaskTestUtil.addCustomMask(
					RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

			_objectEntryLocalService.deleteObjectEntry(
				customMaskObjectEntry.getObjectEntryId());

			List<AuditEvent> auditEvents = _getAuditEvents(
				customMaskObjectEntry, EventTypes.DELETE);

			Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					auditEvents.get(
						0
					).getAdditionalInfo());

			Assert.assertTrue(
				additionalInfoJSONObject.toString(),
				additionalInfoJSONObject.has("maskType"));
			Assert.assertTrue(additionalInfoJSONObject.has("name"));
		}
	}

	@Test
	public void testAuditLogRecordsDataMaskUpdate() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry customMaskObjectEntry =
				MCPServerDataMaskTestUtil.addCustomMask(
					RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(),
				customMaskObjectEntry.getObjectEntryId(), 0,
				HashMapBuilder.<String, Serializable>putAll(
					customMaskObjectEntry.getValues()
				).put(
					"replacementValue", "[CUSTOM]"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			List<AuditEvent> auditEvents = _getAuditEvents(
				customMaskObjectEntry, EventTypes.UPDATE);

			Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					auditEvents.get(
						0
					).getAdditionalInfo());

			JSONObject attributeJSONObject = _getAttributeJSONObject(
				additionalInfoJSONObject.getJSONArray("attributes"),
				"replacementValue");

			Assert.assertNotNull(
				additionalInfoJSONObject.toString(), attributeJSONObject);
			Assert.assertEquals(
				"[CUSTOM]", attributeJSONObject.getString("newValue"));
			Assert.assertEquals(
				"[REDACTED]", attributeJSONObject.getString("oldValue"));
		}
	}

	@Test
	public void testAuditLogRecordsDeleteReasonWhenMaskIsDeleted()
		throws Exception {

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customMaskObjectEntry =
			MCPServerDataMaskTestUtil.addCustomMask(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry profileDataMaskObjectEntry =
				MCPServerDataMaskTestUtil.addProfileDataMask(
					profileObjectEntry.getExternalReferenceCode(),
					customMaskObjectEntry.getObjectEntryId(), 1);

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			_objectEntryLocalService.getObjectEntries(
				0, profileDataMaskObjectEntry.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				_objectEntryLocalService.deleteObjectEntry(
					customMaskObjectEntry.getObjectEntryId());
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			List<AuditEvent> deleteAuditEvents = _getAuditEvents(
				profileDataMaskObjectEntry, EventTypes.DELETE);

			Assert.assertEquals(
				deleteAuditEvents.toString(), 1, deleteAuditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					deleteAuditEvents.get(
						0
					).getAdditionalInfo());

			Assert.assertEquals(
				"Data mask deleted.",
				additionalInfoJSONObject.getString("deleteReason"));

			Assert.assertNull(
				_objectEntryLocalService.fetchObjectEntry(
					profileDataMaskObjectEntry.getObjectEntryId()));
		}
	}

	@Test
	public void testAuditLogRecordsDeleteReasonWhenProfileIsDeleted()
		throws Exception {

		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customMaskObjectEntry =
			MCPServerDataMaskTestUtil.addCustomMask(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry profileDataMaskObjectEntry =
				MCPServerDataMaskTestUtil.addProfileDataMask(
					profileObjectEntry.getExternalReferenceCode(),
					customMaskObjectEntry.getObjectEntryId(), 1);

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			_objectEntryLocalService.getObjectEntries(
				0, profileDataMaskObjectEntry.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				_objectEntryLocalService.deleteObjectEntry(
					profileObjectEntry.getObjectEntryId());
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);
			}

			List<AuditEvent> deleteAuditEvents = _getAuditEvents(
				profileDataMaskObjectEntry, EventTypes.DELETE);

			Assert.assertEquals(
				deleteAuditEvents.toString(), 1, deleteAuditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					deleteAuditEvents.get(
						0
					).getAdditionalInfo());

			Assert.assertEquals(
				"Profile deleted.",
				additionalInfoJSONObject.getString("deleteReason"));

			Assert.assertNull(
				_objectEntryLocalService.fetchObjectEntry(
					profileDataMaskObjectEntry.getObjectEntryId()));
		}
	}

	@Test
	public void testAuditLogRecordsProfileDataMaskCreate() throws Exception {
		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customMaskObjectEntry =
			MCPServerDataMaskTestUtil.addCustomMask(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry profileDataMaskObjectEntry =
				MCPServerDataMaskTestUtil.addProfileDataMask(
					profileObjectEntry.getExternalReferenceCode(),
					customMaskObjectEntry.getObjectEntryId(), 1);

			List<AuditEvent> auditEvents = _getAuditEvents(
				profileDataMaskObjectEntry, EventTypes.ADD);

			Assert.assertEquals(auditEvents.toString(), 1, auditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					auditEvents.get(
						0
					).getAdditionalInfo());

			Assert.assertTrue(
				additionalInfoJSONObject.toString(),
				additionalInfoJSONObject.has("executionOrder"));
			Assert.assertTrue(
				additionalInfoJSONObject.has(
					"mcpServerProfileExternalReferenceCode"));
		}
	}

	@Test
	public void testAuditLogRecordsProfileDataMaskDelete() throws Exception {
		ObjectEntry profileObjectEntry = MCPServerDataMaskTestUtil.addProfile(
			RandomTestUtil.randomString(), "no PII here",
			"mcp-server-profiles getMCPServerProfilesPage");

		ObjectEntry customMaskObjectEntry =
			MCPServerDataMaskTestUtil.addCustomMask(
				RandomTestUtil.randomString(), "\\d{4}", "[REDACTED]");

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_enableAuditPersistence()) {

			ObjectEntry profileDataMaskObjectEntry =
				MCPServerDataMaskTestUtil.addProfileDataMask(
					profileObjectEntry.getExternalReferenceCode(),
					customMaskObjectEntry.getObjectEntryId(), 1);

			MCPServerDataMaskTestUtil.removeProfileDataMask(
				profileDataMaskObjectEntry, _DELETE_REASON);

			List<AuditEvent> updateAuditEvents = _getAuditEvents(
				profileDataMaskObjectEntry, EventTypes.UPDATE);

			Assert.assertEquals(
				updateAuditEvents.toString(), 1, updateAuditEvents.size());

			JSONObject additionalInfoJSONObject =
				JSONFactoryUtil.createJSONObject(
					updateAuditEvents.get(
						0
					).getAdditionalInfo());

			JSONObject attributeJSONObject = _getAttributeJSONObject(
				additionalInfoJSONObject.getJSONArray("attributes"),
				"deleteReason");

			Assert.assertNotNull(
				additionalInfoJSONObject.toString(), attributeJSONObject);
			Assert.assertEquals(
				_DELETE_REASON, attributeJSONObject.getString("newValue"));

			List<AuditEvent> deleteAuditEvents = _getAuditEvents(
				profileDataMaskObjectEntry, EventTypes.DELETE);

			Assert.assertEquals(
				deleteAuditEvents.toString(), 1, deleteAuditEvents.size());
		}
	}

	private ConfigurationTemporarySwapper _enableAuditPersistence()
		throws Exception {

		return new ConfigurationTemporarySwapper(
			"com.liferay.portal.security.audit.router.configuration." +
				"PersistentAuditMessageProcessorConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", true
			).put(
				"flushInterval", 1
			).build());
	}

	private JSONObject _getAttributeJSONObject(
		JSONArray attributesJSONArray, String name) {

		for (int i = 0; i < attributesJSONArray.length(); i++) {
			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				i);

			if (name.equals(attributeJSONObject.getString("name"))) {
				return attributeJSONObject;
			}
		}

		return null;
	}

	private List<AuditEvent> _getAuditEvents(
			ObjectEntry objectEntry, String eventType)
		throws Exception {

		String classPK = String.valueOf(objectEntry.getObjectEntryId());

		for (int i = 0; i < 60; i++) {
			List<AuditEvent> auditEvents =
				_auditEventLocalService.getAuditEvents(
					0, 0, 0, null, null, null, null, null, classPK, null, null,
					null, eventType, null, 0, null, true, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			if (!auditEvents.isEmpty()) {
				return auditEvents;
			}

			Thread.sleep(100);
		}

		return Collections.emptyList();
	}

	private static final String _DELETE_REASON = "Removed by test.";

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}