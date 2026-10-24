/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Marco Leo
 */
@Component(
	configurationPid = "com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration",
	service = OrganizationTypeConfigurationWrapper.class
)
public class OrganizationTypeConfigurationWrapper {

	public String[] getChildrenTypes() {
		return ArrayUtil.filter(
			_organizationTypeConfiguration.childrenTypes(),
			Validator::isNotNull);
	}

	public boolean isCountryEnabled() {
		return _organizationTypeConfiguration.countryEnabled();
	}

	public boolean isCountryRequired() {
		return _organizationTypeConfiguration.countryRequired();
	}

	public boolean isRootable() {
		return _organizationTypeConfiguration.rootable();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_organizationTypeConfiguration = ConfigurableUtil.createConfigurable(
			OrganizationTypeConfiguration.class, properties);
	}

	private volatile OrganizationTypeConfiguration
		_organizationTypeConfiguration;

}