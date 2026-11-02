/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.saml.persistence.internal.util.SamlConfigurationUtil;
import com.liferay.saml.persistence.model.SamlSpAuthRequest;
import com.liferay.saml.persistence.service.base.SamlSpAuthRequestLocalServiceBaseImpl;
import com.liferay.saml.runtime.configuration.SamlConfiguration;

import java.util.Date;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "model.class.name=com.liferay.saml.persistence.model.SamlSpAuthRequest",
	service = AopService.class
)
public class SamlSpAuthRequestLocalServiceImpl
	extends SamlSpAuthRequestLocalServiceBaseImpl {

	@Override
	public SamlSpAuthRequest addSamlSpAuthRequest(
		String samlIdpEntityId, String samlRelayState,
		String samlSpAuthRequestKey, ServiceContext serviceContext) {

		long samlSpAuthRequestId = counterLocalService.increment(
			SamlSpAuthRequest.class.getName());

		SamlSpAuthRequest samlSpAuthRequest =
			samlSpAuthRequestPersistence.create(samlSpAuthRequestId);

		samlSpAuthRequest.setCompanyId(serviceContext.getCompanyId());
		samlSpAuthRequest.setCreateDate(new Date());
		samlSpAuthRequest.setSamlIdpEntityId(samlIdpEntityId);
		samlSpAuthRequest.setSamlRelayState(samlRelayState);
		samlSpAuthRequest.setSamlSpAuthRequestKey(samlSpAuthRequestKey);

		return samlSpAuthRequestPersistence.update(samlSpAuthRequest);
	}

	@Override
	public void deleteExpiredSamlSpAuthRequests() {
		Date createDate = new Date();

		createDate.setTime(
			createDate.getTime() - _samlConfiguration.getSpAuthRequestMaxAge());

		samlSpAuthRequestPersistence.removeByLtCreateDate(createDate);
	}

	@Override
	public SamlSpAuthRequest fetchSamlSpAuthRequest(
		String samlIdpEntityId, String samlSpAuthRequestKey) {

		return samlSpAuthRequestPersistence.fetchBySIEI_SSARK(
			samlIdpEntityId, samlSpAuthRequestKey);
	}

	@Activate
	protected void activate() {
		_samlConfiguration = SamlConfigurationUtil.getSamlConfiguration(
			_configurationAdmin);
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private SamlConfiguration _samlConfiguration;

}