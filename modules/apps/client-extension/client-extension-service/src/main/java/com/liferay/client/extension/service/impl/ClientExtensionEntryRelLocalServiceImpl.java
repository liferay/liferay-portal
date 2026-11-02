/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.impl;

import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.base.ClientExtensionEntryRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.client.extension.model.ClientExtensionEntryRel",
	service = AopService.class
)
public class ClientExtensionEntryRelLocalServiceImpl
	extends ClientExtensionEntryRelLocalServiceBaseImpl {

	@Override
	public ClientExtensionEntryRel addClientExtensionEntryRel(
			long userId, long groupId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		ClientExtensionEntryRel clientExtensionEntryRel =
			clientExtensionEntryRelPersistence.create(
				counterLocalService.increment());

		clientExtensionEntryRel.setUuid(serviceContext.getUuid());
		clientExtensionEntryRel.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		clientExtensionEntryRel.setCompanyId(user.getCompanyId());
		clientExtensionEntryRel.setUserId(user.getUserId());
		clientExtensionEntryRel.setUserName(user.getFullName());

		clientExtensionEntryRel.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		clientExtensionEntryRel.setModifiedDate(
			serviceContext.getCreateDate(new Date()));
		clientExtensionEntryRel.setClassNameId(classNameId);
		clientExtensionEntryRel.setClassPK(classPK);
		clientExtensionEntryRel.setCETExternalReferenceCode(
			cetExternalReferenceCode);
		clientExtensionEntryRel.setType(type);
		clientExtensionEntryRel.setTypeSettings(typeSettings);

		return clientExtensionEntryRelPersistence.update(
			clientExtensionEntryRel);
	}

	@Override
	public void deleteClientExtensionEntryRels(long classNameId, long classPK) {
		clientExtensionEntryRelPersistence.removeByC_C(classNameId, classPK);
	}

	@Override
	public void deleteClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		clientExtensionEntryRelPersistence.removeByC_C_T(
			classNameId, classPK, type);
	}

	@Override
	public void deleteClientExtensionEntryRels(
		long companyId, String cetExternalReferenceCode) {

		clientExtensionEntryRelPersistence.removeByC_CETERC(
			companyId, cetExternalReferenceCode);
	}

	@Override
	public ClientExtensionEntryRel fetchClientExtensionEntryRel(
		long classNameId, long classPK, String type) {

		return clientExtensionEntryRelPersistence.fetchByC_C_T_First(
			classNameId, classPK, type, null);
	}

	@Override
	public List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK) {

		return clientExtensionEntryRelPersistence.findByC_C(
			classNameId, classPK);
	}

	@Override
	public List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		return clientExtensionEntryRelPersistence.findByC_C_T(
			classNameId, classPK, type);
	}

	@Override
	public List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		String type) {

		return clientExtensionEntryRelPersistence.findByType(type);
	}

	@Override
	public int getClientExtensionEntryRelsCount(
		long classNameId, long classPK, String type) {

		return clientExtensionEntryRelPersistence.countByC_C_T(
			classNameId, classPK, type);
	}

	@Override
	public ClientExtensionEntryRel updateClientExtensionEntryRel(
			long clientExtensionEntryRelId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings)
		throws PortalException {

		ClientExtensionEntryRel clientExtensionEntryRel =
			clientExtensionEntryRelPersistence.findByPrimaryKey(
				clientExtensionEntryRelId);

		clientExtensionEntryRel.setClassNameId(classNameId);
		clientExtensionEntryRel.setClassPK(classPK);
		clientExtensionEntryRel.setCETExternalReferenceCode(
			cetExternalReferenceCode);
		clientExtensionEntryRel.setType(type);
		clientExtensionEntryRel.setTypeSettings(typeSettings);

		return clientExtensionEntryRelPersistence.update(
			clientExtensionEntryRel);
	}

	@Reference
	private UserLocalService _userLocalService;

}