/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.saml.persistence.exception.NoSuchIbSloMessageException;
import com.liferay.saml.persistence.model.SamlIbSloMessage;
import com.liferay.saml.persistence.service.base.SamlIbSloMessageLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mika Koivisto
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "model.class.name=com.liferay.saml.persistence.model.SamlIbSloMessage",
	service = AopService.class
)
public class SamlIbSloMessageLocalServiceImpl
	extends SamlIbSloMessageLocalServiceBaseImpl {

	@Override
	public SamlIbSloMessage addSamlIbSloMessage(
		long companyId, String samlIdpEntityId, String logoutRequestXml,
		String samlIdpSessionIndex) {

		long samlIbSloMessageId = counterLocalService.increment(
			SamlIbSloMessage.class.getName());

		SamlIbSloMessage samlIbSloMessage = samlIbSloMessagePersistence.create(
			samlIbSloMessageId);

		samlIbSloMessage.setCompanyId(companyId);
		samlIbSloMessage.setCreateDate(new Date());
		samlIbSloMessage.setSamlIdpEntityId(samlIdpEntityId);
		samlIbSloMessage.setLogoutRequestXml(logoutRequestXml);
		samlIbSloMessage.setSamlIdpSessionIndex(samlIdpSessionIndex);

		return samlIbSloMessagePersistence.update(samlIbSloMessage);
	}

	@Override
	public SamlIbSloMessage getSamlIbSloMessageBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws NoSuchIbSloMessageException {

		return samlIbSloMessagePersistence.findBySamlIdpSessionIndex(
			samlIdpSessionIndex);
	}

}