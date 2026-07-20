/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.exception.CommerceAddressCityException;
import com.liferay.commerce.exception.CommerceAddressCountryException;
import com.liferay.commerce.exception.CommerceAddressNameException;
import com.liferay.commerce.exception.CommerceAddressStreetException;
import com.liferay.commerce.exception.CommerceAddressTypeException;
import com.liferay.commerce.exception.CommerceAddressZipException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceGeocoder;
import com.liferay.commerce.model.impl.CommerceAddressImpl;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.model.CommerceChannelRelTable;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.service.base.CommerceAddressLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alec Sloan
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"liferay.service=false",
		"model.class.name=com.liferay.commerce.model.CommerceAddress"
	},
	service = AopService.class
)
@Deprecated
public class CommerceAddressLocalServiceImpl
	extends CommerceAddressLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAddress addCommerceAddress(
			String externalReferenceCode, String className, long classPK,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(city, countryId, name, street1, type, zip);

		User user = _userLocalService.getUser(serviceContext.getUserId());

		return CommerceAddressImpl.fromAddress(
			_addressLocalService.addAddress(
				externalReferenceCode, user.getUserId(), className, classPK,
				countryId, CommerceAddressImpl.toAddressTypeId(type), regionId,
				city, description, false, name, false, street1, street2,
				street3, subtype, zip, phoneNumber, serviceContext));
	}

	@Override
	public CommerceAddress copyCommerceAddress(
			long sourceCommerceAddressId, String className, long classPK,
			ServiceContext serviceContext)
		throws PortalException {

		CommerceAddress sourceCommerceAddress = getCommerceAddress(
			sourceCommerceAddressId);

		CommerceAddress targetCommerceAddress =
			commerceAddressLocalService.addCommerceAddress(
				StringPool.BLANK, className, classPK,
				sourceCommerceAddress.getCountryId(),
				sourceCommerceAddress.getRegionId(),
				sourceCommerceAddress.getCity(),
				sourceCommerceAddress.getDescription(),
				sourceCommerceAddress.getName(),
				sourceCommerceAddress.getPhoneNumber(),
				sourceCommerceAddress.getStreet1(),
				sourceCommerceAddress.getStreet2(),
				sourceCommerceAddress.getStreet3(),
				sourceCommerceAddress.getSubtype(),
				sourceCommerceAddress.getType(), sourceCommerceAddress.getZip(),
				serviceContext);

		return CommerceAddressImpl.fromAddress(
			_addressLocalService.getAddress(
				targetCommerceAddress.getCommerceAddressId()));
	}

	@Override
	public CommerceAddress createCommerceAddress(long commerceAddressId) {
		CommerceAddress commerceAddress = new CommerceAddressImpl();

		commerceAddress.setNew(true);
		commerceAddress.setPrimaryKey(commerceAddressId);
		commerceAddress.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceAddress;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CommerceAddress deleteCommerceAddress(
			CommerceAddress commerceAddress)
		throws PortalException {

		// Commerce address

		_addressLocalService.deleteAddress(
			commerceAddress.getCommerceAddressId());

		return commerceAddress;
	}

	@Override
	public void deleteCommerceAddresses(String className, long classPK)
		throws PortalException {

		_addressLocalService.deleteAddresses(
			CompanyThreadLocal.getCompanyId(), className, classPK);
	}

	@Override
	public void deleteCountryCommerceAddresses(long countryId)
		throws PortalException {

		_addressLocalService.deleteCountryAddresses(countryId);
	}

	@Override
	public void deleteRegionCommerceAddresses(long regionId)
		throws PortalException {

		_addressLocalService.deleteRegionAddresses(regionId);
	}

	@Override
	public CommerceAddress fetchCommerceAddress(long commerceAddressId) {
		return CommerceAddressImpl.fromAddress(
			_addressLocalService.fetchAddress(commerceAddressId));
	}

	@Override
	public CommerceAddress fetchCommerceAddressByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return CommerceAddressImpl.fromAddress(
			_addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, companyId));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAddress geolocateCommerceAddress(long commerceAddressId)
		throws PortalException {

		Address address = _addressLocalService.getAddress(commerceAddressId);

		double[] coordinates = _commerceGeocoder.getCoordinates(
			address.getStreet1(), address.getCity(), address.getZip(),
			address.getRegion(), address.getCountry());

		address.setLatitude(coordinates[0]);
		address.setLongitude(coordinates[1]);

		return CommerceAddressImpl.fromAddress(
			_addressLocalService.updateAddress(address));
	}

	@Override
	public List<CommerceAddress> getBillingAndShippingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return TransformUtil.transform(
			_addressLocalService.getListTypeAddresses(
				companyId, className, classPK,
				new long[] {
					CommerceAddressImpl.toAddressTypeId(
						CommerceAddressConstants.
							ADDRESS_TYPE_BILLING_AND_SHIPPING)
				}),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return commerceAddressLocalService.getBillingCommerceAddresses(
			companyId, className, classPK, 0, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	@Override
	public List<CommerceAddress> getBillingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			_addressLocalService.dslQuery(
				_getGroupByStep(
					DSLQueryFactoryUtil.selectDistinct(AddressTable.INSTANCE),
					AddressTable.INSTANCE.listTypeId.eq(
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_BILLING)
					).or(
						AddressTable.INSTANCE.listTypeId.eq(
							CommerceAddressImpl.toAddressTypeId(
								CommerceAddressConstants.
									ADDRESS_TYPE_BILLING_AND_SHIPPING))
					),
					channelId, className, classPK, true, false
				).limit(
					start, end
				)),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_BILLING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING)
					}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, sort);

		return TransformUtil.transform(
			_filterByCommerceChannel(
				addressBaseModelSearchResult.getBaseModels(), commerceChannelId,
				start, end),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public int getBillingCommerceAddressesCount(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return _addressLocalService.dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(AddressTable.INSTANCE),
				AddressTable.INSTANCE.listTypeId.eq(
					CommerceAddressImpl.toAddressTypeId(
						CommerceAddressConstants.ADDRESS_TYPE_BILLING)
				).or(
					AddressTable.INSTANCE.listTypeId.eq(
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING))
				),
				channelId, className, classPK, true, false
			).limit(
				start, end
			));
	}

	@Override
	public int getBillingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_BILLING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING)
					}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return _filterByCommerceChannel(
			addressBaseModelSearchResult.getBaseModels(), commerceChannelId, 0,
			addressBaseModelSearchResult.getLength() - 1
		).size();
	}

	@Override
	public CommerceAddress getCommerceAddress(long commerceAddressId)
		throws PortalException {

		return CommerceAddressImpl.fromAddress(
			_addressLocalService.getAddress(commerceAddressId));
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Override
	public List<CommerceAddress> getCommerceAddresses(
		long groupId, String className, long classPK) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return new ArrayList<>();
		}

		return getCommerceAddressesByCompanyId(
			group.getCompanyId(), className, classPK);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Override
	public List<CommerceAddress> getCommerceAddresses(
		long groupId, String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return new ArrayList<>();
		}

		return getCommerceAddressesByCompanyId(
			group.getCompanyId(), className, classPK, start, end,
			orderByComparator);
	}

	@Override
	public List<CommerceAddress> getCommerceAddresses(
		String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		return getCommerceAddressesByCompanyId(
			CompanyThreadLocal.getCompanyId(), className, classPK, start, end,
			orderByComparator);
	}

	@Override
	public List<CommerceAddress> getCommerceAddressesByCompanyId(
		long companyId, String className, long classPK) {

		return TransformUtil.transform(
			_addressLocalService.getAddresses(companyId, className, classPK),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public List<CommerceAddress> getCommerceAddressesByCompanyId(
		long companyId, String className, long classPK, int start, int end,
		OrderByComparator<CommerceAddress> orderByComparator) {

		return TransformUtil.transform(
			_addressLocalService.getAddresses(
				companyId, className, classPK, start, end,
				_getAddressOrderByComparator(orderByComparator)),
			CommerceAddressImpl::fromAddress);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Override
	public int getCommerceAddressesCount(
		long groupId, String className, long classPK) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return 0;
		}

		return getCommerceAddressesCountByCompanyId(
			group.getCompanyId(), className, classPK);
	}

	@Override
	public int getCommerceAddressesCount(String className, long classPK) {
		return getCommerceAddressesCountByCompanyId(
			CompanyThreadLocal.getCompanyId(), className, classPK);
	}

	@Override
	public int getCommerceAddressesCountByCompanyId(
		long companyId, String className, long classPK) {

		return _addressLocalService.getAddressesCount(
			companyId, className, classPK);
	}

	@Override
	public List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException {

		return commerceAddressLocalService.getShippingCommerceAddresses(
			companyId, className, classPK, 0, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	@Override
	public List<CommerceAddress> getShippingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			_addressLocalService.dslQuery(
				_getGroupByStep(
					DSLQueryFactoryUtil.selectDistinct(AddressTable.INSTANCE),
					AddressTable.INSTANCE.listTypeId.eq(
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_SHIPPING)
					).or(
						AddressTable.INSTANCE.listTypeId.eq(
							CommerceAddressImpl.toAddressTypeId(
								CommerceAddressConstants.
									ADDRESS_TYPE_BILLING_AND_SHIPPING))
					),
					channelId, className, classPK, false, true
				).limit(
					start, end
				)),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			Sort sort)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_SHIPPING)
					}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, sort);

		return TransformUtil.transform(
			_filterByCommerceChannel(
				addressBaseModelSearchResult.getBaseModels(), commerceChannelId,
				start, end),
			CommerceAddressImpl::fromAddress);
	}

	@Override
	public int getShippingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_SHIPPING)
					}
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return _filterByCommerceChannel(
			addressBaseModelSearchResult.getBaseModels(), commerceChannelId, 0,
			addressBaseModelSearchResult.getLength() - 1
		).size();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company. Don't need to pass groupId
	 */
	@Deprecated
	@Override
	public BaseModelSearchResult<CommerceAddress> searchCommerceAddresses(
			long companyId, long groupId, String className, long classPK,
			String keywords, int start, int end, Sort sort)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_BILLING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_SHIPPING)
					}
				).build(),
				start, end, sort);

		return new BaseModelSearchResult<>(
			TransformUtil.transform(
				addressBaseModelSearchResult.getBaseModels(),
				CommerceAddressImpl::fromAddress),
			addressBaseModelSearchResult.getLength());
	}

	@Override
	public BaseModelSearchResult<CommerceAddress> searchCommerceAddresses(
			long companyId, String className, long classPK, String keywords,
			int start, int end, Sort sort)
		throws PortalException {

		BaseModelSearchResult<Address> addressBaseModelSearchResult =
			_addressLocalService.searchAddresses(
				companyId, className, classPK, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"listTypeIds",
					new long[] {
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_BILLING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.
								ADDRESS_TYPE_BILLING_AND_SHIPPING),
						CommerceAddressImpl.toAddressTypeId(
							CommerceAddressConstants.ADDRESS_TYPE_SHIPPING)
					}
				).build(),
				start, end, sort);

		return new BaseModelSearchResult<>(
			TransformUtil.transform(
				addressBaseModelSearchResult.getBaseModels(),
				CommerceAddressImpl::fromAddress),
			addressBaseModelSearchResult.getLength());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommerceAddress updateCommerceAddress(
			String externalReferenceCode, long commerceAddressId,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			ServiceContext serviceContext)
		throws PortalException {

		// Commerce address

		Address address = _addressLocalService.getAddress(commerceAddressId);

		_validate(city, countryId, name, street1, type, zip);

		address = _addressLocalService.updateAddress(
			externalReferenceCode, commerceAddressId, countryId,
			CommerceAddressImpl.toAddressTypeId(type), regionId, city,
			description, address.isMailing(), name, address.isPrimary(),
			street1, street2, street3, subtype, zip, phoneNumber);

		return CommerceAddressImpl.fromAddress(address);
	}

	private List<Address> _filterByCommerceChannel(
		List<Address> addresses, long commerceChannelId, int start, int end) {

		List<Address> filteredAddresses = new ArrayList<>();

		long commerceChannelRelsCount =
			_commerceChannelRelLocalService.getCommerceChannelRelsCount(
				commerceChannelId);

		if (commerceChannelRelsCount > 0) {
			for (Address address : addresses) {
				List<CommerceChannelRel> commerceChannelRels =
					_commerceChannelRelLocalService.getCommerceChannelRels(
						Address.class.getName(), address.getAddressId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

				if (!commerceChannelRels.isEmpty()) {
					for (CommerceChannelRel commerceChannelRel :
							commerceChannelRels) {

						if (commerceChannelRel.getCommerceChannelId() ==
								commerceChannelId) {

							filteredAddresses.add(address);

							break;
						}
					}
				}
				else {
					filteredAddresses.add(address);
				}
			}
		}
		else {
			filteredAddresses = addresses;
		}

		if (end > filteredAddresses.size()) {
			end = filteredAddresses.size();
		}

		if (end < 0) {
			return filteredAddresses;
		}

		return filteredAddresses.subList(start, end);
	}

	private OrderByComparator<Address> _getAddressOrderByComparator(
		OrderByComparator<CommerceAddress> orderByComparator) {

		if (orderByComparator == null) {
			return null;
		}

		return new OrderByComparator<Address>() {

			@Override
			public int compare(Address address1, Address address2) {
				return orderByComparator.compare(
					CommerceAddressImpl.fromAddress(address1),
					CommerceAddressImpl.fromAddress(address2));
			}

		};
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Predicate listTypeFilterPredicate,
		long commerceChannelId, String className, long classPK,
		boolean billingAllowed, boolean shippingAllowed) {

		JoinStep joinStep = fromStep.from(
			AddressTable.INSTANCE
		).leftJoinOn(
			CountryTable.INSTANCE,
			AddressTable.INSTANCE.countryId.eq(CountryTable.INSTANCE.countryId)
		).leftJoinOn(
			CommerceChannelRelTable.INSTANCE,
			CommerceChannelRelTable.INSTANCE.classPK.eq(
				AddressTable.INSTANCE.addressId
			).or(
				CommerceChannelRelTable.INSTANCE.classPK.eq(
					CountryTable.INSTANCE.countryId)
			)
		);

		return joinStep.where(
			() -> {
				Predicate predicate = CountryTable.INSTANCE.active.eq(true);

				predicate = predicate.and(
					AddressTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(className)
					).and(
						AddressTable.INSTANCE.classPK.eq(classPK)
					));

				predicate = predicate.and(
					listTypeFilterPredicate.withParentheses());

				Predicate groupFilterPredicate =
					CountryTable.INSTANCE.groupFilterEnabled.eq(false);

				Predicate channelFilterPredicate =
					CountryTable.INSTANCE.groupFilterEnabled.eq(true);

				channelFilterPredicate = channelFilterPredicate.and(
					CommerceChannelRelTable.INSTANCE.classNameId.eq(
						_classNameLocalService.getClassNameId(Country.class)));
				channelFilterPredicate = channelFilterPredicate.and(
					CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
						commerceChannelId));

				groupFilterPredicate = groupFilterPredicate.or(
					channelFilterPredicate.withParentheses());

				predicate = predicate.and(
					groupFilterPredicate.withParentheses());

				if (billingAllowed) {
					predicate = predicate.and(
						CountryTable.INSTANCE.billingAllowed.eq(true));
				}

				if (shippingAllowed) {
					predicate = predicate.and(
						CountryTable.INSTANCE.shippingAllowed.eq(true));
				}

				Predicate addressFilterPredicate =
					CommerceChannelRelTable.INSTANCE.commerceChannelId.eq(
						commerceChannelId);

				addressFilterPredicate = addressFilterPredicate.or(
					CommerceChannelRelTable.INSTANCE.commerceChannelId.
						isNull());

				return predicate.and(addressFilterPredicate.withParentheses());
			});
	}

	private void _validate(
			String city, long countryId, String name, String street1, int type,
			String zip)
		throws PortalException {

		if (Validator.isNull(city)) {
			throw new CommerceAddressCityException();
		}

		if (countryId <= 0) {
			throw new CommerceAddressCountryException();
		}

		if (Validator.isNull(name)) {
			throw new CommerceAddressNameException();
		}

		if (Validator.isNull(street1)) {
			throw new CommerceAddressStreetException();
		}

		if (!ArrayUtil.contains(CommerceAddressConstants.ADDRESS_TYPES, type)) {
			throw new CommerceAddressTypeException();
		}

		if (Validator.isNull(zip)) {
			throw new CommerceAddressZipException();
		}
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CommerceGeocoder _commerceGeocoder;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}