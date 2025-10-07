/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.saml.persistence.exception.DuplicateSamlIdpSpSessionException;
import com.liferay.saml.persistence.exception.NoSuchIdpSpSessionException;
import com.liferay.saml.persistence.model.SamlIdpSpSession;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;
import com.liferay.saml.persistence.service.base.SamlIdpSpSessionLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 * @author Mika Koivisto
 */
@Component(
	property = "model.class.name=com.liferay.saml.persistence.model.SamlIdpSpSession",
	service = AopService.class
)
public class SamlIdpSpSessionLocalServiceImpl
	extends SamlIdpSpSessionLocalServiceBaseImpl {

	@Override
	public SamlIdpSpSession addSamlIdpSpSession(
			long samlIdpSsoSessionId, String samlSpEntityId,
			String nameIdFormat, String nameIdValue,
			ServiceContext serviceContext)
		throws PortalException {

		SamlIdpSpSession samlIdpSpSession = _fetchSamlIdpSpSession(
			samlIdpSsoSessionId, samlSpEntityId);

		if (samlIdpSpSession != null) {
			throw new DuplicateSamlIdpSpSessionException(
				StringBundler.concat(
					"Duplicate SAML IDP SP session ", samlIdpSsoSessionId,
					" for ", samlSpEntityId));
		}

		User user = _userLocalService.getUserById(serviceContext.getUserId());

		SamlPeerBinding samlPeerBinding =
			_samlPeerBindingLocalService.fetchSamlPeerBinding(
				user.getCompanyId(), samlSpEntityId, false, nameIdFormat, null,
				nameIdValue);

		if (samlPeerBinding == null) {
			samlPeerBinding = _samlPeerBindingLocalService.addSamlPeerBinding(
				user.getUserId(), samlSpEntityId, nameIdFormat, null, null,
				null, nameIdValue);
		}

		long samlIdpSpSessionId = counterLocalService.increment(
			SamlIdpSpSession.class.getName());

		samlIdpSpSession = samlIdpSpSessionPersistence.create(
			samlIdpSpSessionId);

		samlIdpSpSession.setCompanyId(user.getCompanyId());
		samlIdpSpSession.setUserId(user.getUserId());
		samlIdpSpSession.setUserName(user.getFullName());
		samlIdpSpSession.setSamlIdpSsoSessionId(samlIdpSsoSessionId);
		samlIdpSpSession.setSamlPeerBindingId(
			samlPeerBinding.getSamlPeerBindingId());

		return samlIdpSpSessionPersistence.update(samlIdpSpSession);
	}

	@Override
	public SamlIdpSpSession getSamlIdpSpSession(
			long samlIdpSsoSessionId, String samlSpEntityId)
		throws PortalException {

		SamlIdpSpSession samlIdpSpSession = _fetchSamlIdpSpSession(
			samlIdpSsoSessionId, samlSpEntityId);

		if (samlIdpSpSession == null) {
			throw new NoSuchIdpSpSessionException();
		}

		return samlIdpSpSession;
	}

	@Override
	public List<SamlIdpSpSession> getSamlIdpSpSessions(
		long samlIdpSsoSessionId) {

		return samlIdpSpSessionPersistence.findBySamlIdpSsoSessionId(
			samlIdpSsoSessionId);
	}

	@Override
	public SamlIdpSpSession updateModifiedDate(
			long samlIdpSsoSessionId, String samlSpEntityId)
		throws PortalException {

		SamlIdpSpSession samlIdpSpSession = getSamlIdpSpSession(
			samlIdpSsoSessionId, samlSpEntityId);

		samlIdpSpSession.setModifiedDate(new Date());

		return samlIdpSpSessionPersistence.update(samlIdpSpSession);
	}

	private SamlIdpSpSession _fetchSamlIdpSpSession(
		long samlIdpSsoSessionId, String samlSpEntityId) {

		List<SamlIdpSpSession> samlIdpSsoSessions =
			samlIdpSpSessionPersistence.findBySamlIdpSsoSessionId(
				samlIdpSsoSessionId);

		if (samlIdpSsoSessions.isEmpty()) {
			return null;
		}

		for (SamlIdpSpSession samlIdpSsoSession : samlIdpSsoSessions) {
			SamlPeerBinding samlPeerBinding =
				_samlPeerBindingLocalService.fetchSamlPeerBinding(
					samlIdpSsoSession.getSamlPeerBindingId());

			if (Objects.equals(
					samlSpEntityId, samlPeerBinding.getSamlPeerEntityId())) {

				return samlIdpSsoSession;
			}
		}

		return null;
	}

	@Reference
	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

	@Reference
	private UserLocalService _userLocalService;

}