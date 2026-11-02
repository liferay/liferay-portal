/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.service.impl;

import com.liferay.contacts.exception.DuplicateEntryEmailAddressException;
import com.liferay.contacts.exception.EntryEmailAddressException;
import com.liferay.contacts.exception.RequiredEntryEmailAddressException;
import com.liferay.contacts.model.Entry;
import com.liferay.contacts.service.base.EntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.FullNameValidatorFactory;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.contacts.model.Entry",
	service = AopService.class
)
public class EntryLocalServiceImpl extends EntryLocalServiceBaseImpl {

	@Override
	public Entry addEntry(
			long userId, String fullName, String emailAddress, String comments)
		throws PortalException {

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		_validate(user.getCompanyId(), 0, userId, fullName, emailAddress);

		long contactId = counterLocalService.increment();

		Entry entry = entryPersistence.create(contactId);

		entry.setCompanyId(user.getCompanyId());
		entry.setUserId(user.getUserId());
		entry.setUserName(user.getFullName());
		entry.setCreateDate(date);
		entry.setModifiedDate(date);
		entry.setFullName(fullName);
		entry.setEmailAddress(emailAddress);
		entry.setComments(comments);

		entry = entryPersistence.update(entry);

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, user.getUserId(), Entry.class.getName(),
			contactId, false, false, false);

		return entry;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Entry deleteEntry(Entry entry) throws PortalException {
		entry = super.deleteEntry(entry);

		_resourceLocalService.deleteResource(
			entry.getCompanyId(), Entry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, entry.getEntryId());

		return entry;
	}

	@Override
	public int getEntriesCount(long userId) {
		return entryPersistence.countByUserId(userId);
	}

	@Override
	public List<Entry> search(
		long userId, String keywords, int start, int end) {

		return entryFinder.findByKeywords(userId, keywords, start, end);
	}

	@Override
	public int searchCount(long userId, String keywords) {
		return entryFinder.countByKeywords(userId, keywords);
	}

	@Override
	public List<BaseModel<?>> searchUsersAndContacts(
		long companyId, long userId, String keywords, int start, int end) {

		return entryFinder.findByKeywords(
			companyId, userId, keywords, start, end);
	}

	@Override
	public int searchUsersAndContactsCount(
		long companyId, long userId, String keywords) {

		return entryFinder.countByKeywords(companyId, userId, keywords);
	}

	@Override
	public Entry updateEntry(
			long entryId, String fullName, String emailAddress, String comments)
		throws PortalException {

		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		_validate(
			entry.getCompanyId(), entryId, entry.getUserId(), fullName,
			emailAddress);

		entry.setModifiedDate(new Date());
		entry.setFullName(fullName);
		entry.setEmailAddress(emailAddress);
		entry.setComments(comments);

		return entryPersistence.update(entry);
	}

	private void _validate(
			long companyId, long entryId, long userId, String fullName,
			String emailAddress)
		throws PortalException {

		if (Validator.isNull(fullName)) {
			throw new ContactNameException.MustHaveValidFullName(
				FullNameValidatorFactory.getInstance());
		}

		if (Validator.isNull(emailAddress)) {
			throw new RequiredEntryEmailAddressException();
		}

		if (!Validator.isEmailAddress(emailAddress)) {
			throw new EntryEmailAddressException();
		}

		if (entryId > 0) {
			Entry entry = entryPersistence.findByPrimaryKey(entryId);

			if (!StringUtil.equalsIgnoreCase(
					emailAddress, entry.getEmailAddress())) {

				_validateEmailAddress(companyId, userId, emailAddress);
			}
		}
		else {
			_validateEmailAddress(companyId, userId, emailAddress);
		}
	}

	private void _validateEmailAddress(
			long companyId, long userId, String emailAddress)
		throws PortalException {

		Entry entry = entryPersistence.fetchByU_EA(userId, emailAddress);

		if (entry != null) {
			throw new DuplicateEntryEmailAddressException();
		}

		User user = _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (user != null) {
			throw new DuplicateEntryEmailAddressException();
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}