/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.saml.persistence.model.SamlSpMessage;
import com.liferay.saml.persistence.service.base.SamlSpMessageLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mika Koivisto
 */
@Component(
	property = "model.class.name=com.liferay.saml.persistence.model.SamlSpMessage",
	service = AopService.class
)
public class SamlSpMessageLocalServiceImpl
	extends SamlSpMessageLocalServiceBaseImpl {

	@Override
	public SamlSpMessage addSamlSpMessage(
		String samlIdpEntityId, Date expirationDate, String samlIdpResponseKey,
		ServiceContext serviceContext) {

		long samlSpMessageId = counterLocalService.increment(
			SamlSpMessage.class.getName());

		SamlSpMessage samlSpMessage = samlSpMessagePersistence.create(
			samlSpMessageId);

		samlSpMessage.setCompanyId(serviceContext.getCompanyId());
		samlSpMessage.setCreateDate(new Date());
		samlSpMessage.setSamlIdpEntityId(samlIdpEntityId);
		samlSpMessage.setExpirationDate(expirationDate);
		samlSpMessage.setSamlIdpResponseKey(samlIdpResponseKey);

		return samlSpMessagePersistence.update(samlSpMessage);
	}

	@Override
	public void deleteExpiredSamlSpMessages() {
		samlSpMessagePersistence.removeByLtExpirationDate(new Date());
	}

	@Override
	public SamlSpMessage fetchSamlSpMessage(
		String samlIdpEntityId, String samlIdpResponseKey) {

		return samlSpMessagePersistence.fetchBySIEI_SIRK(
			samlIdpEntityId, samlIdpResponseKey);
	}

	@Override
	public SamlSpMessage getSamlSpMessage(
			String samlIdpEntityId, String samlIdpResponseKey)
		throws PortalException {

		return samlSpMessagePersistence.findBySIEI_SIRK(
			samlIdpEntityId, samlIdpResponseKey);
	}

}