/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.resource.type;

import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributorRegistry;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
@Component(service = DesignLibraryResourceTypeContributorRegistry.class)
public class DesignLibraryResourceTypeContributorRegistryImpl
	implements DesignLibraryResourceTypeContributorRegistry {

	@Override
	public List<DesignLibraryResourceTypeContributor>
		getDesignLibraryResourceTypeContributors() {

		return Collections.unmodifiableList(
			_designLibraryResourceTypeContributors);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<DesignLibraryResourceTypeContributor>
		_designLibraryResourceTypeContributors;

}