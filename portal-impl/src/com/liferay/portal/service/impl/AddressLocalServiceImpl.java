/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManagerUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.account.configuration.manager.AccountEntryAddressSubtypeConfigurationManagerUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.AddressCityException;
import com.liferay.portal.kernel.exception.AddressStreetException;
import com.liferay.portal.kernel.exception.AddressSubtypeException;
import com.liferay.portal.kernel.exception.AddressZipException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.list.type.manager.ListTypeEntryManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.AddressLocalServiceBaseImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class AddressLocalServiceImpl extends AddressLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Address addAddress(
			String externalReferenceCode, long userId, String className,
			long classPK, long countryId, long listTypeId, long regionId,
			String city, String description, boolean mailing, String name,
			boolean primary, String street1, String street2, String street3,
			String subtype, String zip, String phoneNumber,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		if (!IncompleteModelManagerUtil.isIncompleteModel()) {
			validate(
				0, city, classNameId, classPK, user.getCompanyId(), countryId,
				listTypeId, mailing, primary, regionId, street1, subtype, zip);
		}

		long addressId = counterLocalService.increment();

		Address address = addressPersistence.create(addressId);

		address.setUuid(serviceContext.getUuid());
		address.setExternalReferenceCode(externalReferenceCode);
		address.setCompanyId(user.getCompanyId());
		address.setUserId(user.getUserId());
		address.setUserName(user.getFullName());
		address.setClassNameId(classNameId);
		address.setClassPK(classPK);
		address.setCountryId(countryId);
		address.setListTypeId(listTypeId);
		address.setRegionId(regionId);
		address.setCity(city);
		address.setDescription(description);
		address.setMailing(mailing);
		address.setName(name);
		address.setPrimary(primary);
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setStreet3(street3);
		address.setSubtype(subtype);
		address.setZip(zip);

		if (IncompleteModelManagerUtil.isIncompleteModel()) {
			address.setStatus(WorkflowConstants.STATUS_INCOMPLETE);
		}
		else {
			address.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		address = addressPersistence.update(address);

		if (Validator.isNotNull(phoneNumber)) {
			_addAddressPhone(addressId, address.getCompanyId(), phoneNumber);
		}

		_reindexUser(address.getClassName(), address.getClassPK());

		return address;
	}

	@Override
	public Address copyAddress(
			long sourceAddressId, String className, long classPK,
			ServiceContext serviceContext)
		throws PortalException {

		Address sourceAddress = addressPersistence.findByPrimaryKey(
			sourceAddressId);

		return addressLocalService.addAddress(
			null, serviceContext.getUserId(), className, classPK,
			sourceAddress.getCountryId(), sourceAddress.getListTypeId(),
			sourceAddress.getRegionId(), sourceAddress.getCity(),
			sourceAddress.getDescription(), sourceAddress.isMailing(),
			sourceAddress.getName(), sourceAddress.isPrimary(),
			sourceAddress.getStreet1(), sourceAddress.getStreet2(),
			sourceAddress.getStreet3(), sourceAddress.getSubtype(),
			sourceAddress.getZip(), sourceAddress.getPhoneNumber(),
			serviceContext);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Address deleteAddress(Address address) {
		addressPersistence.remove(address);

		_phoneLocalService.deletePhones(
			address.getCompanyId(), address.getClassName(),
			address.getAddressId());

		_reindexUser(address.getClassName(), address.getClassPK());

		return address;
	}

	@Override
	public Address deleteAddress(long addressId) throws PortalException {
		Address address = addressPersistence.findByPrimaryKey(addressId);

		return addressLocalService.deleteAddress(address);
	}

	@Override
	public void deleteAddresses(
		long companyId, String className, long classPK) {

		List<Address> addresses = addressPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);

		for (Address address : addresses) {
			addressLocalService.deleteAddress(address);
		}
	}

	@Override
	public void deleteCountryAddresses(long countryId) {
		List<Address> addresses = addressPersistence.findByCountryId(countryId);

		for (Address address : addresses) {
			addressLocalService.deleteAddress(address);
		}
	}

	@Override
	public void deleteRegionAddresses(long regionId) {
		List<Address> addresses = addressPersistence.findByRegionId(regionId);

		for (Address address : addresses) {
			addressLocalService.deleteAddress(address);
		}
	}

	@Override
	public List<Address> getAddresses() {
		return addressPersistence.findAll();
	}

	@Override
	public List<Address> getAddresses(
		long companyId, String className, long classPK) {

		return addressPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public List<Address> getAddresses(
		long companyId, String className, long classPK, int start, int end,
		OrderByComparator<Address> orderByComparator) {

		return addressPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK, start, end, orderByComparator);
	}

	@Override
	public int getAddressesCount(
		long companyId, String className, long classPK) {

		return addressPersistence.countByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public List<Address> getListTypeAddresses(
		long companyId, String className, long classPK, long[] listTypeIds) {

		return addressPersistence.findByC_C_C_L(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK, listTypeIds);
	}

	@Override
	public List<Address> getListTypeAddresses(
		long companyId, String className, long classPK, long[] listTypeIds,
		int start, int end, OrderByComparator<Address> orderByComparator) {

		return addressPersistence.findByC_C_C_L(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK, listTypeIds, start, end, orderByComparator);
	}

	@Override
	public Address getOrAddIncompleteAddress(
			String externalReferenceCode, long companyId, long userId,
			String className, long classPK)
		throws Exception {

		return IncompleteModelManagerUtil.getOrAddIncompleteModel(
			Address.class, companyId, externalReferenceCode,
			this::fetchAddressByExternalReferenceCode,
			this::getAddressByExternalReferenceCode,
			() -> addressLocalService.addAddress(
				externalReferenceCode, userId, className, classPK, 0, 0, 0,
				StringPool.BLANK, StringPool.BLANK, false, null, false,
				StringPool.BLANK, null, null, null, null, null,
				new ServiceContext()));
	}

	@Override
	public BaseModelSearchResult<Address> searchAddresses(
			long companyId, String className, long classPK, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = buildSearchContext(
			companyId, className, classPK, keywords, params, start, end, sort);

		return searchAddresses(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Address updateAddress(
			String externalReferenceCode, long addressId, long countryId,
			long listTypeId, long regionId, String city, String description,
			boolean mailing, String name, boolean primary, String street1,
			String street2, String street3, String subtype, String zip,
			String phoneNumber)
		throws PortalException {

		validate(
			addressId, city, 0, 0, 0, countryId, listTypeId, mailing, primary,
			regionId, street1, subtype, zip);

		Address address = addressPersistence.findByPrimaryKey(addressId);

		address.setExternalReferenceCode(externalReferenceCode);
		address.setCountryId(countryId);
		address.setListTypeId(listTypeId);
		address.setRegionId(regionId);
		address.setCity(city);
		address.setDescription(description);
		address.setMailing(mailing);
		address.setName(name);
		address.setPrimary(primary);
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setStreet3(street3);
		address.setSubtype(subtype);
		address.setZip(zip);

		if (address.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			address.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		address = addressPersistence.update(address);

		if (Validator.isNotNull(phoneNumber)) {
			List<Phone> phones = _phoneLocalService.getPhones(
				address.getCompanyId(), Address.class.getName(), addressId);

			if (ListUtil.isEmpty(phones)) {
				_addAddressPhone(
					addressId, address.getCompanyId(), phoneNumber);
			}
			else {
				Phone phone = phones.get(0);

				phone.setNumber(phoneNumber);

				_phoneLocalService.updatePhone(phone);
			}
		}

		_reindexUser(address.getClassName(), address.getClassPK());

		return address;
	}

	@Override
	public Address updateExternalReferenceCode(
			Address address, String externalReferenceCode)
		throws PortalException {

		if (Objects.equals(
				address.getExternalReferenceCode(), externalReferenceCode)) {

			return address;
		}

		address.setExternalReferenceCode(externalReferenceCode);

		return addressPersistence.update(address);
	}

	@Override
	public Address updateExternalReferenceCode(
			long addressId, String externalReferenceCode)
		throws PortalException {

		return updateExternalReferenceCode(
			addressPersistence.findByPrimaryKey(addressId),
			externalReferenceCode);
	}

	protected SearchContext buildSearchContext(
		long companyId, String className, long classPK, String keywords,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.CLASS_NAME_ID,
				_classNameLocalService.getClassNameId(className)
			).put(
				Field.CLASS_PK, classPK
			).put(
				Field.NAME, keywords
			).put(
				"city", keywords
			).put(
				"countryName", keywords
			).put(
				"params", params
			).put(
				"regionName", keywords
			).put(
				"zip", keywords
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected List<Address> getAddresses(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<Address> addresses = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long addressId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			Address address = fetchAddress(addressId);

			if (address == null) {
				addresses = null;

				Indexer<Address> indexer = IndexerRegistryUtil.getIndexer(
					Address.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (addresses != null) {
				addresses.add(address);
			}
		}

		return addresses;
	}

	protected BaseModelSearchResult<Address> searchAddresses(
			SearchContext searchContext)
		throws PortalException {

		Indexer<Address> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			Address.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<Address> addresses = getAddresses(hits);

			if (addresses != null) {
				return new BaseModelSearchResult<>(addresses, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected void validate(
		long addressId, long companyId, long classNameId, long classPK,
		boolean mailing, boolean primary) {

		// Check to make sure there isn't another address with the same company
		// id, class name, and class pk that also has mailing set to true

		if (mailing) {
			List<Address> addresses = addressPersistence.findByC_C_C_M(
				companyId, classNameId, classPK, mailing);

			for (Address address : addresses) {
				if ((addressId <= 0) || (address.getAddressId() != addressId)) {
					address.setMailing(false);

					addressPersistence.update(address);
				}
			}
		}

		// Check to make sure there isn't another address with the same company
		// id, class name, and class pk that also has primary set to true

		if (primary) {
			List<Address> addresses = addressPersistence.findByC_C_C_P(
				companyId, classNameId, classPK, primary);

			for (Address address : addresses) {
				if ((addressId <= 0) || (address.getAddressId() != addressId)) {
					address.setPrimary(false);

					addressPersistence.update(address);
				}
			}
		}
	}

	protected void validate(
			long addressId, String city, long classNameId, long classPK,
			long companyId, long countryId, long listTypeId, boolean mailing,
			boolean primary, long regionId, String street1, String subtype,
			String zip)
		throws PortalException {

		if (Validator.isNull(street1)) {
			throw new AddressStreetException();
		}
		else if (Validator.isNull(city)) {
			throw new AddressCityException();
		}
		else if (Validator.isNull(zip)) {
			Country country = _countryPersistence.fetchByPrimaryKey(countryId);

			if ((country != null) && country.isZipRequired()) {
				throw new AddressZipException();
			}
		}

		if (addressId > 0) {
			Address address = addressPersistence.findByPrimaryKey(addressId);

			companyId = address.getCompanyId();
			classNameId = address.getClassNameId();
			classPK = address.getClassPK();
		}

		if ((classNameId == _classNameLocalService.getClassNameId(
				Company.class)) ||
			(classNameId == _classNameLocalService.getClassNameId(
				Contact.class)) ||
			(classNameId == _classNameLocalService.getClassNameId(
				Organization.class))) {

			_listTypeLocalService.validate(
				listTypeId, classNameId, ListTypeConstants.ADDRESS);
		}

		if (Validator.isNotNull(subtype) &&
			((classNameId == _classNameLocalService.getClassNameId(
				"com.liferay.account.model.AccountEntry")) ||
			 (classNameId == _classNameLocalService.getClassNameId(
				 "com.liferay.commerce.model.CommerceOrder")))) {

			ListType listType = _listTypeLocalService.getListType(listTypeId);

			String externalReferenceCode =
				AccountEntryAddressSubtypeConfigurationManagerUtil.
					getAddressSubtypeListTypeDefinitionExternalReferenceCode(
						companyId, listType.getName());

			if (Validator.isNull(externalReferenceCode)) {
				throw new AddressSubtypeException();
			}

			long listTypeEntryId =
				ListTypeEntryManagerUtil.
					getListTypeEntryIdByListTypeDefinitionExternalReferenceCode(
						externalReferenceCode, companyId, subtype);

			if (listTypeEntryId == 0) {
				throw new AddressSubtypeException();
			}
		}

		validate(addressId, companyId, classNameId, classPK, mailing, primary);
	}

	private void _addAddressPhone(
			long addressId, long companyId, String phoneNumber)
		throws PortalException {

		ListType listType = _listTypeLocalService.getListType(
			companyId, "phone-number", ListTypeConstants.ADDRESS_PHONE);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		_phoneLocalService.addPhone(
			null, serviceContext.getUserId(), Address.class.getName(),
			addressId, phoneNumber, null, listType.getListTypeId(), false,
			serviceContext);
	}

	private void _reindexUser(String className, long classPK) {
		if (!Objects.equals(className, Contact.class.getName())) {
			return;
		}

		Contact contact = _contactLocalService.fetchContact(classPK);

		if ((contact == null) ||
			!Objects.equals(contact.getClassName(), User.class.getName())) {

			return;
		}

		try {
			Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(contact.getClassName(), contact.getClassPK());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddressLocalServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ContactLocalService.class)
	private ContactLocalService _contactLocalService;

	@BeanReference(type = CountryPersistence.class)
	private CountryPersistence _countryPersistence;

	@BeanReference(type = ListTypeLocalService.class)
	private ListTypeLocalService _listTypeLocalService;

	@BeanReference(type = PhoneLocalService.class)
	private PhoneLocalService _phoneLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}