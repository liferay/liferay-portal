/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferenceValueTable;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ServiceWrapper.class)
public class AuditPortletPreferencesLocalServiceWrapper
	extends PortletPreferencesLocalServiceWrapper {

	@Override
	public void deletePortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException {

		if (ownerType != PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			super.deletePortletPreferences(ownerId, ownerType, plid, portletId);

			return;
		}

		PortletPreferences portletPreferences = fetchPortletPreferences(
			ownerId, ownerType, plid, portletId);

		Map<String, List<String>> oldValuesMap = _getValuesMap(
			portletPreferences);

		super.deletePortletPreferences(ownerId, ownerType, plid, portletId);

		_route(
			Collections.emptyMap(), oldValuesMap, ownerId, portletId,
			portletPreferences);
	}

	@Override
	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId,
		jakarta.portlet.PortletPreferences portletPreferences) {

		return _updatePreferences(
			ownerId, ownerType, plid, portletId,
			() -> super.updatePreferences(
				ownerId, ownerType, plid, portletId, portletPreferences));
	}

	@Override
	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId, String xml) {

		return _updatePreferences(
			ownerId, ownerType, plid, portletId,
			() -> super.updatePreferences(
				ownerId, ownerType, plid, portletId, xml));
	}

	private Map<String, List<String>> _getValuesMap(
		PortletPreferences portletPreferences) {

		if (portletPreferences == null) {
			return Collections.emptyMap();
		}

		List<PortletPreferenceValue> portletPreferenceValues =
			_portletPreferenceValueLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					PortletPreferenceValueTable.INSTANCE
				).from(
					PortletPreferenceValueTable.INSTANCE
				).where(
					PortletPreferenceValueTable.INSTANCE.portletPreferencesId.
						eq(portletPreferences.getPortletPreferencesId())
				).orderBy(
					PortletPreferenceValueTable.INSTANCE.index.ascending()
				));

		Map<String, List<String>> valuesMap = new HashMap<>();

		for (PortletPreferenceValue portletPreferenceValue :
				portletPreferenceValues) {

			List<String> values = valuesMap.computeIfAbsent(
				portletPreferenceValue.getName(), name -> new ArrayList<>());

			values.add(portletPreferenceValue.getValue());
		}

		return valuesMap;
	}

	private void _route(
		Map<String, List<String>> newValuesMap,
		Map<String, List<String>> oldValuesMap, long ownerId, String portletId,
		PortletPreferences portletPreferences) {

		Set<String> keys = new TreeSet<>(newValuesMap.keySet());

		keys.addAll(oldValuesMap.keySet());

		List<Attribute> attributes = TransformUtil.transform(
			keys,
			key -> {
				if (Objects.equals(
						newValuesMap.get(key), oldValuesMap.get(key))) {

					return null;
				}

				return new Attribute(key);
			});

		if (attributes.isEmpty()) {
			return;
		}

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				PortletPreferences.class.getName(),
				portletPreferences.getPortletPreferencesId(), EventTypes.UPDATE,
				attributes);

			auditMessage.setCompanyId(ownerId);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put("portletId", portletId);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private PortletPreferences _updatePreferences(
		long ownerId, int ownerType, long plid, String portletId,
		Supplier<PortletPreferences> portletPreferencesSupplier) {

		if (ownerType != PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			return portletPreferencesSupplier.get();
		}

		Map<String, List<String>> oldValuesMap = _getValuesMap(
			fetchPortletPreferences(ownerId, ownerType, plid, portletId));

		PortletPreferences portletPreferences =
			portletPreferencesSupplier.get();

		Map<String, List<String>> newValuesMap = _getValuesMap(
			portletPreferences);

		_route(
			newValuesMap, oldValuesMap, ownerId, portletId, portletPreferences);

		return portletPreferences;
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}