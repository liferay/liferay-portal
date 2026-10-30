/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.configuration.internal.configuration.persistence.listener;

import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = {"model.class.name=*", "service.ranking:Integer=100"},
	service = ConfigurationModelListener.class
)
public class AuditConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterDelete(String pid) {
		_route(pid);
	}

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		_route(pid);
	}

	@Override
	public void onBeforeDelete(String pid) {
		Map<String, AuditMessage> auditMessages = _auditMessages.get();

		auditMessages.remove(pid);

		try {
			Dictionary<?, ?> dictionary = _reloadablePersistenceManager.load(
				pid);

			if (dictionary == null) {
				return;
			}

			Set<String> keys = _getKeys(dictionary);

			if (keys.isEmpty()) {
				return;
			}

			auditMessages.put(
				pid,
				_createAuditMessage(
					"delete", TransformUtil.transform(keys, Attribute::new),
					dictionary, EventTypes.DELETE, pid));
		}
		catch (Exception exception) {
			_log.error("Unable to audit deletion of " + pid, exception);
		}
	}

	@Override
	public void onBeforeSave(
		String pid, Dictionary<String, Object> properties) {

		Map<String, AuditMessage> auditMessages = _auditMessages.get();

		auditMessages.remove(pid);

		try {
			AuditMessage auditMessage = _getAuditMessage(pid, properties);

			if (auditMessage != null) {
				auditMessages.put(pid, auditMessage);
			}
		}
		catch (Exception exception) {
			_log.error("Unable to audit update of " + pid, exception);
		}
	}

	private AuditMessage _createAuditMessage(
			String action, List<Attribute> attributes,
			Dictionary<?, ?> dictionary, String eventType, String pid)
		throws Exception {

		String rawPid = ConfigurationPidUtil.getRawPid(
			GetterUtil.getString(
				dictionary.get(ConfigurationAdmin.SERVICE_FACTORYPID), pid));

		if (rawPid.startsWith("com.liferay.portal.security.audit.")) {
			eventType = EventTypes.AUDIT_CONFIG_CHANGE;
		}

		long groupId = GetterUtil.getLong(
			dictionary.get(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()));

		AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			groupId, 0, action, rawPid, pid, null, eventType, "configuration",
			attributes);

		long companyId = GetterUtil.getLong(
			dictionary.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()));

		if (companyId > 0) {
			auditMessage.setCompanyId(companyId);
		}

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		if (Objects.equals(eventType, EventTypes.AUDIT_CONFIG_CHANGE)) {
			additionalInfoJSONObject.put("action", action);
		}

		String fileName = GetterUtil.getString(
			dictionary.get(FileInstallConstants.FELIX_FILE_INSTALL_FILENAME));

		if (Validator.isNotNull(fileName)) {
			additionalInfoJSONObject.put("fileName", fileName);
		}

		String scope = ExtendedObjectClassDefinition.Scope.SYSTEM.getValue();

		if (Validator.isNotNull(
				dictionary.get(
					ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
						getPropertyKey()))) {

			scope =
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.getValue();
		}
		else if (groupId > 0) {
			scope = ExtendedObjectClassDefinition.Scope.GROUP.getValue();
		}
		else if (companyId > 0) {
			scope = ExtendedObjectClassDefinition.Scope.COMPANY.getValue();
		}

		additionalInfoJSONObject.put("scope", scope);

		return auditMessage;
	}

	private AuditMessage _getAuditMessage(
			String pid, Dictionary<String, Object> properties)
		throws Exception {

		if (pid.endsWith("factory") ||
			(properties.get("_felix_.cm.newConfiguration") != null)) {

			return null;
		}

		Dictionary<Object, Object> dictionary = new HashMapDictionary<>();
		Enumeration<String> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			dictionary.put(key, properties.get(key));
		}

		Map<String, Object> overrideProperties =
			ConfigurationOverridePropertiesUtil.getOverrideProperties(pid);

		if (overrideProperties != null) {
			for (Map.Entry<String, Object> entry :
					overrideProperties.entrySet()) {

				dictionary.put(entry.getKey(), entry.getValue());
			}
		}

		Dictionary<?, ?> originalDictionary =
			_reloadablePersistenceManager.load(pid);
		Set<String> originalKeys = Collections.emptySet();

		if (originalDictionary != null) {
			originalKeys = _getKeys(originalDictionary);
		}

		Set<String> keys = new TreeSet<>(originalKeys);

		keys.addAll(_getKeys(dictionary));

		List<Attribute> attributes = TransformUtil.transform(
			keys,
			key -> {
				Object originalValue = null;

				if (originalDictionary != null) {
					originalValue = originalDictionary.get(key);
				}

				if (Objects.deepEquals(originalValue, dictionary.get(key))) {
					return null;
				}

				return new Attribute(key);
			});

		if (attributes.isEmpty()) {
			return null;
		}

		if (originalKeys.isEmpty()) {
			return _createAuditMessage(
				"add", attributes, dictionary, EventTypes.ADD, pid);
		}

		return _createAuditMessage(
			"update", attributes, dictionary, EventTypes.UPDATE, pid);
	}

	private Set<String> _getKeys(Dictionary<?, ?> dictionary) {
		Set<String> keys = new TreeSet<>();

		Enumeration<?> enumeration = dictionary.keys();

		while (enumeration.hasMoreElements()) {
			String key = String.valueOf(enumeration.nextElement());

			if (_internalKeys.contains(key)) {
				continue;
			}

			keys.add(key);
		}

		return keys;
	}

	private void _route(String pid) {
		Map<String, AuditMessage> auditMessages = _auditMessages.get();

		AuditMessage auditMessage = auditMessages.remove(pid);

		if (auditMessage == null) {
			return;
		}

		try {
			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			_log.error("Unable to route audit message for " + pid, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditConfigurationModelListener.class);

	private static final ThreadLocal<Map<String, AuditMessage>> _auditMessages =
		new CentralizedThreadLocal<>(
			AuditConfigurationModelListener.class + "._auditMessages",
			HashMap::new);
	private static final Set<String> _internalKeys = Set.of(
		":org.apache.felix.configadmin.locked:",
		":org.apache.felix.configadmin.revision:",
		"_felix_.cm.newConfiguration",
		ConfigurationAdmin.SERVICE_BUNDLELOCATION,
		ConfigurationAdmin.SERVICE_FACTORYPID, Constants.SERVICE_PID,
		FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}