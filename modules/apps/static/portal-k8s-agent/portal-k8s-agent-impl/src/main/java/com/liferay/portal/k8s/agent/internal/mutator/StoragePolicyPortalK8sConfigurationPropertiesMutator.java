/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.mutator;

import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.k8s.agent.mutator.PortalK8sConfigurationPropertiesMutator;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

/**
 * @author Gregory Amerson
 */
@Component(service = PortalK8sConfigurationPropertiesMutator.class)
@ServiceRanking(1900)
public class StoragePolicyPortalK8sConfigurationPropertiesMutator
	implements PortalK8sConfigurationPropertiesMutator {

	@Override
	public void mutateConfigurationProperties(
		Map<String, String> annotations, Map<String, String> labels,
		Dictionary<String, Object> properties) {

		properties.put(
			ReloadablePersistenceManager.STORAGE_POLICY_KEY,
			ReloadablePersistenceManager.STORAGE_POLICY_VALUE_EPHEMERAL);
	}

}