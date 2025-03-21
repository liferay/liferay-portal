/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.manager;

import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Danny Situ
 */
public class PhoneContactInfoManager extends BaseContactInfoManager<Phone> {

	public PhoneContactInfoManager(
		String className, long classPK, PhoneLocalService phoneLocalService,
		PhoneService phoneService) {

		_className = className;
		_classPK = classPK;
		_phoneLocalService = phoneLocalService;
		_phoneService = phoneService;
	}

	@Override
	public Phone construct(ActionRequest actionRequest) {
		long phoneId = ParamUtil.getLong(actionRequest, "primaryKey");

		String number = ParamUtil.getString(actionRequest, "phoneNumber");
		String extension = ParamUtil.getString(actionRequest, "phoneExtension");
		long listTypeId = ParamUtil.getLong(actionRequest, "phoneListTypeId");
		boolean primary = ParamUtil.getBoolean(actionRequest, "phonePrimary");

		Phone phone = _phoneLocalService.createPhone(phoneId);

		phone.setNumber(number);
		phone.setExtension(extension);
		phone.setListTypeId(listTypeId);
		phone.setPrimary(primary);

		return phone;
	}

	@Override
	public Phone doAdd(Phone phone) throws Exception {
		return _phoneService.addPhone(
			phone.getExternalReferenceCode(), _className, _classPK,
			phone.getNumber(), phone.getExtension(), phone.getListTypeId(),
			phone.isPrimary(), new ServiceContext());
	}

	@Override
	public void doDelete(long phoneId) throws Exception {
		_phoneService.deletePhone(phoneId);
	}

	@Override
	public void doUpdate(Phone phone) throws Exception {
		_phoneService.updatePhone(
			phone.getExternalReferenceCode(), phone.getPhoneId(),
			phone.getNumber(), phone.getExtension(), phone.getListTypeId(),
			phone.isPrimary());
	}

	@Override
	public Phone get(long phoneId) throws Exception {
		return _phoneService.getPhone(phoneId);
	}

	@Override
	public List<Phone> getAll() throws Exception {
		return _phoneService.getPhones(_className, _classPK);
	}

	@Override
	public long getPrimaryKey(Phone phone) {
		return phone.getPhoneId();
	}

	@Override
	public boolean isPrimary(Phone phone) {
		return phone.isPrimary();
	}

	@Override
	public void setPrimary(Phone phone, boolean primary) {
		phone.setPrimary(primary);
	}

	private final String _className;
	private final long _classPK;
	private final PhoneLocalService _phoneLocalService;
	private final PhoneService _phoneService;

}