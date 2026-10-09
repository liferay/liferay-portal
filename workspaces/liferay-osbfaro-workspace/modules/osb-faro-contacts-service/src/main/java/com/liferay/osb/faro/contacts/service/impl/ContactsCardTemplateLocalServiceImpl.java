/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.service.impl;

import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.osb.faro.contacts.service.base.ContactsCardTemplateLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(
	property = "model.class.name=com.liferay.osb.faro.contacts.model.ContactsCardTemplate",
	service = AopService.class
)
public class ContactsCardTemplateLocalServiceImpl
	extends ContactsCardTemplateLocalServiceBaseImpl {

	@Override
	public ContactsCardTemplate addContactsCardTemplate(
		long groupId, long userId, String name, String settings, int type) {

		long contactsCardTemplateId = counterLocalService.increment();

		ContactsCardTemplate contactsCardTemplate =
			contactsCardTemplatePersistence.create(contactsCardTemplateId);

		contactsCardTemplate.setGroupId(groupId);
		contactsCardTemplate.setUserId(userId);
		contactsCardTemplate.setName(name);
		contactsCardTemplate.setSettings(settings);
		contactsCardTemplate.setType(type);

		return contactsCardTemplatePersistence.update(contactsCardTemplate);
	}

	@Override
	public void deleteContactsCardTemplates(long groupId) {
		contactsCardTemplatePersistence.removeByGroupId(groupId);
	}

	@Override
	public List<ContactsCardTemplate> getContactsCardTemplates(
		String contactsCardTemplateIds) {

		List<ContactsCardTemplate> contactsCardTemplates = new ArrayList<>();

		for (String contactsCardTemplateId :
				StringUtil.split(contactsCardTemplateIds)) {

			ContactsCardTemplate contactsCardTemplate =
				contactsCardTemplatePersistence.fetchByPrimaryKey(
					Long.valueOf(contactsCardTemplateId));

			if (contactsCardTemplate == null) {
				continue;
			}

			contactsCardTemplates.add(contactsCardTemplate);
		}

		return contactsCardTemplates;
	}

	@Override
	public ContactsCardTemplate updateContactsCardTemplate(
			long contactsCardTemplateId, String name, String settings, int type)
		throws PortalException {

		ContactsCardTemplate contactsCardTemplate =
			contactsCardTemplatePersistence.findByPrimaryKey(
				contactsCardTemplateId);

		contactsCardTemplate.setName(name);
		contactsCardTemplate.setSettings(settings);
		contactsCardTemplate.setType(type);

		return contactsCardTemplatePersistence.update(contactsCardTemplate);
	}

}