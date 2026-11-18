/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.instance.lifecycle;

import com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope.SAPEntryScopeDescriptorFinderRegistrator;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.kernel.model.Company;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2JSONWSPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements EveryNodeEveryStartup {

	public OAuth2JSONWSPortalInstanceLifecycleListener(
		SAPEntryScopeDescriptorFinderRegistrator
			sapEntryScopeDescriptorFinderRegistrator) {

		_sapEntryScopeDescriptorFinderRegistrator =
			sapEntryScopeDescriptorFinderRegistrator;
	}

	@Override
	public void portalInstanceRegistered(Company company) {
		_sapEntryScopeDescriptorFinderRegistrator.register(
			company.getCompanyId());
	}

	private final SAPEntryScopeDescriptorFinderRegistrator
		_sapEntryScopeDescriptorFinderRegistrator;

}