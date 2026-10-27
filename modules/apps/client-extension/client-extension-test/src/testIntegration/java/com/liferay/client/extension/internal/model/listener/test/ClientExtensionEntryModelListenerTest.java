/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class ClientExtensionEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class, _auditMessages::add,
			HashMapDictionaryBuilder.<String, Object>put(
				"eventTypes", "*"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();

		for (ClientExtensionEntry clientExtensionEntry :
				_clientExtensionEntries) {

			_clientExtensionEntryLocalService.deleteClientExtensionEntry(
				clientExtensionEntry.getClientExtensionEntryId());
		}
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeCreate() throws Exception {
		PrintStream printStream = System.out;

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).put(
						"outputToConsole", true
					).build())) {

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			System.setOut(new PrintStream(byteArrayOutputStream));

			String url = _getRandomURL();

			ClientExtensionEntry clientExtensionEntry =
				_addClientExtensionEntry(url);

			AuditMessage auditMessage = _getAuditMessage(
				clientExtensionEntry, EventTypes.ADD);

			_assertAdditionalInfo(auditMessage, clientExtensionEntry);
			_assertNotContains(auditMessage, url);

			Assert.assertEquals(
				"instance.clientextensionentry.add",
				auditMessage.getResourceAction());

			String logMessage = null;

			for (String line :
					StringUtil.splitLines(byteArrayOutputStream.toString())) {

				if (!line.startsWith("LoggingAuditMessageProcessor: ")) {
					continue;
				}

				logMessage = StringUtil.removeSubstring(
					line, "LoggingAuditMessageProcessor: ");

				break;
			}

			JSONAssert.assertEquals(
				String.valueOf(auditMessage.toJSONObject()), logMessage,
				JSONCompareMode.STRICT);
		}
		finally {
			System.setOut(printStream);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		String url = _getRandomURL();

		ClientExtensionEntry clientExtensionEntry = _addClientExtensionEntry(
			url);

		_clientExtensionEntries.remove(clientExtensionEntry);

		_clientExtensionEntryLocalService.deleteClientExtensionEntry(
			clientExtensionEntry.getClientExtensionEntryId());

		AuditMessage auditMessage = _getAuditMessage(
			clientExtensionEntry, EventTypes.DELETE);

		_assertAdditionalInfo(auditMessage, clientExtensionEntry);
		_assertNotContains(auditMessage, url);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		String url = _getRandomURL();

		ClientExtensionEntry clientExtensionEntry = _addClientExtensionEntry(
			url);

		_clientExtensionEntryLocalService.updateClientExtensionEntry(
			TestPropsValues.getUserId(),
			clientExtensionEntry.getClientExtensionEntryId(),
			clientExtensionEntry.getDescription(),
			clientExtensionEntry.getNameMap(),
			clientExtensionEntry.getProperties(),
			clientExtensionEntry.getSourceCodeURL(),
			clientExtensionEntry.getTypeSettings());

		Assert.assertNull(
			_fetchAuditMessage(clientExtensionEntry, EventTypes.UPDATE));

		String description = RandomTestUtil.randomString();
		String newURL = _getRandomURL();

		_clientExtensionEntryLocalService.updateClientExtensionEntry(
			TestPropsValues.getUserId(),
			clientExtensionEntry.getClientExtensionEntryId(), description,
			clientExtensionEntry.getNameMap(),
			clientExtensionEntry.getProperties(),
			clientExtensionEntry.getSourceCodeURL(), _getTypeSettings(newURL));

		AuditMessage auditMessage = _getAuditMessage(
			clientExtensionEntry, EventTypes.UPDATE);

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		JSONArray attributesJSONArray = additionalInfoJSONObject.getJSONArray(
			"attributes");

		JSONObject descriptionJSONObject = _getAttributeJSONObject(
			attributesJSONArray, "description");

		Assert.assertEquals(
			description, descriptionJSONObject.getString("newValue"));
		Assert.assertEquals(
			StringPool.BLANK, descriptionJSONObject.getString("oldValue"));

		JSONObject typeSettingsJSONObject = _getAttributeJSONObject(
			attributesJSONArray, "typeSettings");

		Assert.assertEquals(
			StringPool.BLANK, typeSettingsJSONObject.getString("newValue"));
		Assert.assertEquals(
			StringPool.BLANK, typeSettingsJSONObject.getString("oldValue"));

		_assertNotContains(auditMessage, newURL);
		_assertNotContains(auditMessage, url);
	}

	private ClientExtensionEntry _addClientExtensionEntry(String url)
		throws Exception {

		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				_getTypeSettings(url));

		_clientExtensionEntries.add(clientExtensionEntry);

		return clientExtensionEntry;
	}

	private void _assertAdditionalInfo(
			AuditMessage auditMessage,
			ClientExtensionEntry clientExtensionEntry)
		throws Exception {

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode",
				clientExtensionEntry.getExternalReferenceCode()
			).put(
				"nameMap",
				JSONUtil.put(
					LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
					clientExtensionEntry.getName(LocaleUtil.getDefault()))
			).put(
				"type", ClientExtensionEntryConstants.TYPE_GLOBAL_JS
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT);
	}

	private void _assertNotContains(AuditMessage auditMessage, String value) {
		String json = String.valueOf(auditMessage.toJSONObject());

		Assert.assertFalse(json.contains(value));
	}

	private AuditMessage _fetchAuditMessage(
		ClientExtensionEntry clientExtensionEntry, String eventType) {

		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(
					auditMessage.getClassName(),
					ClientExtensionEntry.class.getName()) &&
				Objects.equals(
					auditMessage.getClassPK(),
					String.valueOf(
						clientExtensionEntry.getClientExtensionEntryId())) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	private JSONObject _getAttributeJSONObject(
		JSONArray attributesJSONArray, String name) {

		for (int i = 0; i < attributesJSONArray.length(); i++) {
			JSONObject attributeJSONObject = attributesJSONArray.getJSONObject(
				i);

			if (Objects.equals(attributeJSONObject.getString("name"), name)) {
				return attributeJSONObject;
			}
		}

		throw new AssertionError("Attribute " + name + " was not found");
	}

	private AuditMessage _getAuditMessage(
		ClientExtensionEntry clientExtensionEntry, String eventType) {

		AuditMessage auditMessage = _fetchAuditMessage(
			clientExtensionEntry, eventType);

		Assert.assertNotNull(auditMessage);

		return auditMessage;
	}

	private String _getRandomURL() {
		return "http://" + RandomTestUtil.randomString() + ".com";
	}

	private String _getTypeSettings(String url) {
		return UnicodePropertiesBuilder.create(
			true
		).put(
			"url", url
		).buildString();
	}

	private final List<AuditMessage> _auditMessages =
		new CopyOnWriteArrayList<>();
	private final List<ClientExtensionEntry> _clientExtensionEntries =
		new ArrayList<>();

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

}