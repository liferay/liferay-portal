/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.osb.faro.contacts.service.ContactsCardTemplateLocalService;
import com.liferay.osb.faro.web.internal.model.display.contacts.card.template.ContactsLayoutTemplateSettingDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.UserConstants;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = ContactsLayoutHelper.class)
public class ContactsLayoutHelper {

	public String addContactsCardTemplateIds(long groupId, int type)
		throws Exception {

		List<List<ContactsLayoutTemplateSettingDisplay>>
			contactsLayoutTemplateSettingDisplaysList = new ArrayList<>();

		for (int[] contactsCardTemplateTypes :
				_contactsCardTemplateManagerUtil.
					getDefaultContactsCardTemplateTypes(type)) {

			List<ContactsLayoutTemplateSettingDisplay>
				contactsLayoutTemplateSettingDisplays = new ArrayList<>();

			for (int contactsCardTemplateType : contactsCardTemplateTypes) {
				ContactsCardTemplate contactsCardTemplate =
					_contactsCardTemplateLocalService.addContactsCardTemplate(
						groupId, UserConstants.USER_ID_DEFAULT,
						_contactsCardTemplateManagerUtil.getDefaultName(
							contactsCardTemplateType),
						_contactsCardTemplateManagerUtil.getDefaultSettings(
							contactsCardTemplateType),
						contactsCardTemplateType);

				contactsLayoutTemplateSettingDisplays.add(
					new ContactsLayoutTemplateSettingDisplay(
						contactsCardTemplate.getContactsCardTemplateId(), 0));
			}

			contactsLayoutTemplateSettingDisplaysList.add(
				contactsLayoutTemplateSettingDisplays);
		}

		return JSONUtil.writeValueAsString(
			contactsLayoutTemplateSettingDisplaysList);
	}

	public String addHeaderContactsCardTemplateIds(long groupId, int type) {
		int[] contactsCardTemplateTypes =
			_contactsCardTemplateManagerUtil.
				getDefaultHeaderContactsCardTemplateTypes(type);

		if (contactsCardTemplateTypes.length == 0) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			contactsCardTemplateTypes.length * 2);

		for (int contactsCardTemplateType : contactsCardTemplateTypes) {
			ContactsCardTemplate contactsCardTemplate =
				_contactsCardTemplateLocalService.addContactsCardTemplate(
					groupId, UserConstants.USER_ID_DEFAULT,
					_contactsCardTemplateManagerUtil.getDefaultName(
						contactsCardTemplateType),
					_contactsCardTemplateManagerUtil.getDefaultSettings(
						contactsCardTemplateType),
					contactsCardTemplateType);

			sb.append(contactsCardTemplate.getContactsCardTemplateId());

			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	@Reference
	private ContactsCardTemplateLocalService _contactsCardTemplateLocalService;

	@Reference
	private ContactsCardTemplateManagerHelper _contactsCardTemplateManagerUtil;

}