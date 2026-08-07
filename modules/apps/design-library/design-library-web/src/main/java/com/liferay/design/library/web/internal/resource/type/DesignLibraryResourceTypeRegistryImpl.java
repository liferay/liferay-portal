/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.resource.type;

import com.liferay.design.library.resource.type.DesignLibraryResourceType;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Thiago Buarque
 */
@Component(service = DesignLibraryResourceTypeRegistry.class)
public class DesignLibraryResourceTypeRegistryImpl
	implements DesignLibraryResourceTypeRegistry {

	@Override
	public DesignLibraryResourceType getDesignLibraryResourceType(
		String entryClassName) {

		for (DesignLibraryResourceType designLibraryResourceType :
				_designLibraryResourceTypes) {

			if (Objects.equals(
					designLibraryResourceType.getEntryClassName(),
					entryClassName)) {

				return designLibraryResourceType;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"No design library resource type is registered for " +
					entryClassName);
		}

		return null;
	}

	@Override
	public List<DesignLibraryResourceType> getDesignLibraryResourceTypes() {
		return Collections.unmodifiableList(_designLibraryResourceTypes);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DesignLibraryResourceTypeRegistryImpl.class);

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<DesignLibraryResourceType>
		_designLibraryResourceTypes;

}