/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.manager;

import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Danny Situ
 */
public class EmailAddressContactInfoManager
	extends BaseContactInfoManager<EmailAddress> {

	public EmailAddressContactInfoManager(
		String className, long classPK,
		EmailAddressLocalService emailAddressLocalService,
		EmailAddressService emailAddressService) {

		_className = className;
		_classPK = classPK;
		_emailAddressLocalService = emailAddressLocalService;
		_emailAddressService = emailAddressService;
	}

	@Override
	protected EmailAddress construct(ActionRequest actionRequest)
		throws Exception {

		long emailAddressId = ParamUtil.getLong(actionRequest, "primaryKey");

		String address = ParamUtil.getString(
			actionRequest, "emailAddressAddress");
		long listTypeId = ParamUtil.getLong(
			actionRequest, "emailAddressTypeId");
		boolean primary = ParamUtil.getBoolean(
			actionRequest, "emailAddressPrimary");

		EmailAddress emailAddress =
			_emailAddressLocalService.createEmailAddress(emailAddressId);

		emailAddress.setAddress(address);
		emailAddress.setListTypeId(listTypeId);
		emailAddress.setPrimary(primary);

		return emailAddress;
	}

	@Override
	protected EmailAddress doAdd(EmailAddress emailAddress) throws Exception {
		return _emailAddressService.addEmailAddress(
			emailAddress.getExternalReferenceCode(), _className, _classPK,
			emailAddress.getAddress(), emailAddress.getListTypeId(),
			emailAddress.isPrimary(), new ServiceContext());
	}

	@Override
	protected void doDelete(long emailAddressId) throws Exception {
		_emailAddressService.deleteEmailAddress(emailAddressId);
	}

	@Override
	protected void doUpdate(EmailAddress emailAddress) throws Exception {
		_emailAddressService.updateEmailAddress(
			emailAddress.getExternalReferenceCode(),
			emailAddress.getEmailAddressId(), emailAddress.getAddress(),
			emailAddress.getListTypeId(), emailAddress.isPrimary());
	}

	@Override
	protected EmailAddress get(long emailAddressId) throws Exception {
		return _emailAddressService.getEmailAddress(emailAddressId);
	}

	@Override
	protected List<EmailAddress> getAll() throws Exception {
		return _emailAddressService.getEmailAddresses(_className, _classPK);
	}

	@Override
	protected long getPrimaryKey(EmailAddress emailAddress) {
		return emailAddress.getEmailAddressId();
	}

	@Override
	protected boolean isPrimary(EmailAddress emailAddress) {
		return emailAddress.isPrimary();
	}

	@Override
	protected void setPrimary(EmailAddress emailAddress, boolean primary) {
		emailAddress.setPrimary(primary);
	}

	private final String _className;
	private final long _classPK;
	private final EmailAddressLocalService _emailAddressLocalService;
	private final EmailAddressService _emailAddressService;

}