/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferenceValueTable;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ServiceWrapper.class)
public class AuditPortalPreferencesLocalServiceWrapper
	extends PortalPreferencesLocalServiceWrapper {

	@Override
	public PortalPreferences updatePreferences(
		long ownerId, int ownerType,
		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences) {

		if (ownerType != PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			return super.updatePreferences(
				ownerId, ownerType, portalPreferences);
		}

		Map<String, List<String>> oldValuesMap = _getValuesMap(
			fetchPortalPreferences(ownerId, ownerType));

		PortalPreferences modelPortalPreferences = super.updatePreferences(
			ownerId, ownerType, portalPreferences);

		Map<String, List<String>> newValuesMap = _getValuesMap(
			modelPortalPreferences);

		Set<String> keys = new TreeSet<>(newValuesMap.keySet());

		keys.addAll(oldValuesMap.keySet());

		List<Attribute> attributes = TransformUtil.transform(
			keys,
			key -> {
				List<String> defaultValues = Collections.singletonList(
					PropsUtil.get(key));

				if (Objects.equals(
						newValuesMap.getOrDefault(key, defaultValues),
						oldValuesMap.getOrDefault(key, defaultValues))) {

					return null;
				}

				return new Attribute(key);
			});

		if (attributes.isEmpty()) {
			return modelPortalPreferences;
		}

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				PortalPreferences.class.getName(),
				modelPortalPreferences.getPortalPreferencesId(),
				EventTypes.UPDATE, attributes);

			auditMessage.setCompanyId(ownerId);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		return modelPortalPreferences;
	}

	private Map<String, List<String>> _getValuesMap(
		PortalPreferences portalPreferences) {

		if (portalPreferences == null) {
			return Collections.emptyMap();
		}

		List<PortalPreferenceValue> portalPreferenceValues =
			_portalPreferenceValueLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					PortalPreferenceValueTable.INSTANCE
				).from(
					PortalPreferenceValueTable.INSTANCE
				).where(
					PortalPreferenceValueTable.INSTANCE.portalPreferencesId.eq(
						portalPreferences.getPortalPreferencesId())
				).orderBy(
					PortalPreferenceValueTable.INSTANCE.index.ascending()
				));

		Map<String, List<String>> valuesMap = new HashMap<>();

		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValues) {

			String key = portalPreferenceValue.getKey();

			if (Validator.isNotNull(portalPreferenceValue.getNamespace())) {
				key =
					portalPreferenceValue.getNamespace() + StringPool.POUND +
						key;
			}

			List<String> values = valuesMap.computeIfAbsent(
				key, portalPreferenceKey -> new ArrayList<>());

			values.add(portalPreferenceValue.getValue());
		}

		return valuesMap;
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

}