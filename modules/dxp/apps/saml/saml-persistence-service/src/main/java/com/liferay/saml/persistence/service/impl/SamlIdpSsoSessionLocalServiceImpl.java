/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.saml.persistence.exception.DuplicateSamlIdpSsoSessionException;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.service.base.SamlIdpSsoSessionLocalServiceBaseImpl;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpSessionPersistence;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "model.class.name=com.liferay.saml.persistence.model.SamlIdpSsoSession",
	service = AopService.class
)
public class SamlIdpSsoSessionLocalServiceImpl
	extends SamlIdpSsoSessionLocalServiceBaseImpl {

	@Override
	public SamlIdpSsoSession addSamlIdpSsoSession(
			String samlIdpSsoSessionKey, ServiceContext serviceContext)
		throws PortalException {

		SamlIdpSsoSession samlIdpSsoSession =
			samlIdpSsoSessionPersistence.fetchBySamlIdpSsoSessionKey(
				samlIdpSsoSessionKey);

		if (samlIdpSsoSession != null) {
			throw new DuplicateSamlIdpSsoSessionException(
				"Duplicate SAML IDP SSO session for " + samlIdpSsoSessionKey);
		}

		User user = _userLocalService.getUserById(serviceContext.getUserId());
		Date date = new Date();

		long samlIdpSsoSessionId = counterLocalService.increment(
			SamlIdpSsoSession.class.getName());

		samlIdpSsoSession = samlIdpSsoSessionPersistence.create(
			samlIdpSsoSessionId);

		samlIdpSsoSession.setCompanyId(serviceContext.getCompanyId());
		samlIdpSsoSession.setUserId(user.getUserId());
		samlIdpSsoSession.setUserName(user.getFullName());
		samlIdpSsoSession.setCreateDate(date);
		samlIdpSsoSession.setModifiedDate(date);
		samlIdpSsoSession.setSamlIdpSsoSessionKey(samlIdpSsoSessionKey);

		return samlIdpSsoSessionPersistence.update(samlIdpSsoSession);
	}

	@Override
	public void deleteExpiredSamlIdpSsoSessions() {
		Date createDate = new Date();

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		createDate.setTime(
			createDate.getTime() -
				samlProviderConfiguration.sessionMaximumAge());

		samlIdpSsoSessionPersistence.removeByLtCreateDate(createDate);
		_samlIdpSpSessionPersistence.removeByLtCreateDate(createDate);
	}

	@Override
	public SamlIdpSsoSession fetchSamlIdpSso(String samlIdpSsoSessionKey) {
		return samlIdpSsoSessionPersistence.fetchBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);
	}

	@Override
	public SamlIdpSsoSession fetchSamlIdpSsoByUserId(long userId) {
		return samlIdpSsoSessionPersistence.fetchByUserId(userId);
	}

	@Override
	public SamlIdpSsoSession getSamlIdpSso(String samlIdpSsoSessionKey)
		throws PortalException {

		return samlIdpSsoSessionPersistence.findBySamlIdpSsoSessionKey(
			samlIdpSsoSessionKey);
	}

	@Override
	public SamlIdpSsoSession updateModifiedDate(String samlIdpSsoSessionKey)
		throws PortalException {

		SamlIdpSsoSession samlIdpSsoSession =
			samlIdpSsoSessionPersistence.findBySamlIdpSsoSessionKey(
				samlIdpSsoSessionKey);

		samlIdpSsoSession.setModifiedDate(new Date());

		return samlIdpSsoSessionPersistence.update(samlIdpSsoSession);
	}

	@Reference
	private SamlIdpSpSessionPersistence _samlIdpSpSessionPersistence;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private UserLocalService _userLocalService;

}