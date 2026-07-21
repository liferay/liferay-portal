/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display;

import com.liferay.account.model.AccountEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeLocalServiceUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Pei-Jung Lan
 */
public class AddressDisplay {

	public static AddressDisplay of(Address address) {
		if (address != null) {
			return new AddressDisplay(address);
		}

		return _EMPTY_INSTANCE;
	}

	public static AddressDisplay of(long addressId) {
		return of(AddressLocalServiceUtil.fetchAddress(addressId));
	}

	public long getAddressId() {
		return _addressId;
	}

	public String getCity() {
		return _city;
	}

	public long getListTypeId() {
		return _listTypeId;
	}

	public String getListTypeName() {
		return _listTypeName;
	}

	public String getName() {
		return _name;
	}

	public String getRegionName() {
		return HtmlUtil.escape(_region.getName());
	}

	public int getStatus() {
		return _status;
	}

	public String getStreet() {
		return _street;
	}

	public String getSubtype() {
		return _subtype;
	}

	public String getType(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			new AggregateResourceBundle(
				resourceBundle, PortalUtil.getResourceBundle(locale)),
			_listTypeName);
	}

	public String getZip() {
		return _zip;
	}

	private AddressDisplay() {
		_addressId = 0;
		_city = StringPool.BLANK;

		ListType listType = ListTypeLocalServiceUtil.fetchListType(
			CompanyThreadLocal.getCompanyId(), "billing-and-shipping",
			AccountEntry.class.getName() + ListTypeConstants.ADDRESS);

		if (listType == null) {
			_listTypeId = 0;
		}
		else {
			_listTypeId = listType.getListTypeId();
		}

		_listTypeName = StringPool.BLANK;
		_name = StringPool.BLANK;
		_region = null;
		_status = 0;
		_street = StringPool.BLANK;
		_subtype = StringPool.BLANK;
		_zip = StringPool.BLANK;
	}

	private AddressDisplay(Address address) {
		_addressId = address.getAddressId();
		_city = HtmlUtil.escape(address.getCity());
		_listTypeId = address.getListTypeId();
		_listTypeName = _getListTypeName(address);
		_name = HtmlUtil.escape(address.getName());
		_region = address.getRegion();
		_status = address.getStatus();
		_street = HtmlUtil.escape(address.getStreet1());
		_subtype = address.getSubtype();
		_zip = HtmlUtil.escape(address.getZip());
	}

	private String _getListTypeName(Address address) {
		ListType listType = address.getListType();

		return listType.getName();
	}

	private static final AddressDisplay _EMPTY_INSTANCE = new AddressDisplay();

	private final long _addressId;
	private final String _city;
	private final long _listTypeId;
	private final String _listTypeName;
	private final String _name;
	private final Region _region;
	private final int _status;
	private final String _street;
	private final String _subtype;
	private final String _zip;

}