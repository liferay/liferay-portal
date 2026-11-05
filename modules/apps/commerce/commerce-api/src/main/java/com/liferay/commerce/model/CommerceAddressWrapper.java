/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CommerceAddress}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddress
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceAddressWrapper
	extends BaseModelWrapper<CommerceAddress>
	implements CommerceAddress, ModelWrapper<CommerceAddress> {

	public CommerceAddressWrapper(CommerceAddress commerceAddress) {
		super(commerceAddress);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("commerceAddressId", getCommerceAddressId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("countryId", getCountryId());
		attributes.put("regionId", getRegionId());
		attributes.put("city", getCity());
		attributes.put("defaultBilling", isDefaultBilling());
		attributes.put("defaultShipping", isDefaultShipping());
		attributes.put("description", getDescription());
		attributes.put("latitude", getLatitude());
		attributes.put("longitude", getLongitude());
		attributes.put("name", getName());
		attributes.put("phoneNumber", getPhoneNumber());
		attributes.put("street1", getStreet1());
		attributes.put("street2", getStreet2());
		attributes.put("street3", getStreet3());
		attributes.put("subtype", getSubtype());
		attributes.put("type", getType());
		attributes.put("zip", getZip());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long commerceAddressId = (Long)attributes.get("commerceAddressId");

		if (commerceAddressId != null) {
			setCommerceAddressId(commerceAddressId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		Long countryId = (Long)attributes.get("countryId");

		if (countryId != null) {
			setCountryId(countryId);
		}

		Long regionId = (Long)attributes.get("regionId");

		if (regionId != null) {
			setRegionId(regionId);
		}

		String city = (String)attributes.get("city");

		if (city != null) {
			setCity(city);
		}

		Boolean defaultBilling = (Boolean)attributes.get("defaultBilling");

		if (defaultBilling != null) {
			setDefaultBilling(defaultBilling);
		}

		Boolean defaultShipping = (Boolean)attributes.get("defaultShipping");

		if (defaultShipping != null) {
			setDefaultShipping(defaultShipping);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Double latitude = (Double)attributes.get("latitude");

		if (latitude != null) {
			setLatitude(latitude);
		}

		Double longitude = (Double)attributes.get("longitude");

		if (longitude != null) {
			setLongitude(longitude);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String phoneNumber = (String)attributes.get("phoneNumber");

		if (phoneNumber != null) {
			setPhoneNumber(phoneNumber);
		}

		String street1 = (String)attributes.get("street1");

		if (street1 != null) {
			setStreet1(street1);
		}

		String street2 = (String)attributes.get("street2");

		if (street2 != null) {
			setStreet2(street2);
		}

		String street3 = (String)attributes.get("street3");

		if (street3 != null) {
			setStreet3(street3);
		}

		String subtype = (String)attributes.get("subtype");

		if (subtype != null) {
			setSubtype(subtype);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String zip = (String)attributes.get("zip");

		if (zip != null) {
			setZip(zip);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public CommerceAddress cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public com.liferay.portal.kernel.model.Country fetchCountry() {
		return model.fetchCountry();
	}

	/**
	 * Returns the city of this commerce address.
	 *
	 * @return the city of this commerce address
	 */
	@Override
	public String getCity() {
		return model.getCity();
	}

	/**
	 * Returns the fully qualified class name of this commerce address.
	 *
	 * @return the fully qualified class name of this commerce address
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this commerce address.
	 *
	 * @return the class name ID of this commerce address
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this commerce address.
	 *
	 * @return the class pk of this commerce address
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the commerce address ID of this commerce address.
	 *
	 * @return the commerce address ID of this commerce address
	 */
	@Override
	public long getCommerceAddressId() {
		return model.getCommerceAddressId();
	}

	/**
	 * Returns the company ID of this commerce address.
	 *
	 * @return the company ID of this commerce address
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public com.liferay.portal.kernel.model.Country getCountry()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getCountry();
	}

	/**
	 * Returns the country ID of this commerce address.
	 *
	 * @return the country ID of this commerce address
	 */
	@Override
	public long getCountryId() {
		return model.getCountryId();
	}

	/**
	 * Returns the create date of this commerce address.
	 *
	 * @return the create date of this commerce address
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the default billing of this commerce address.
	 *
	 * @return the default billing of this commerce address
	 */
	@Override
	public boolean getDefaultBilling() {
		return model.getDefaultBilling();
	}

	/**
	 * Returns the default shipping of this commerce address.
	 *
	 * @return the default shipping of this commerce address
	 */
	@Override
	public boolean getDefaultShipping() {
		return model.getDefaultShipping();
	}

	/**
	 * Returns the description of this commerce address.
	 *
	 * @return the description of this commerce address
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return model.getExpandoBridge();
	}

	/**
	 * Returns the external reference code of this commerce address.
	 *
	 * @return the external reference code of this commerce address
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this commerce address.
	 *
	 * @return the group ID of this commerce address
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the latitude of this commerce address.
	 *
	 * @return the latitude of this commerce address
	 */
	@Override
	public double getLatitude() {
		return model.getLatitude();
	}

	/**
	 * Returns the longitude of this commerce address.
	 *
	 * @return the longitude of this commerce address
	 */
	@Override
	public double getLongitude() {
		return model.getLongitude();
	}

	/**
	 * Returns the modified date of this commerce address.
	 *
	 * @return the modified date of this commerce address
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this commerce address.
	 *
	 * @return the mvcc version of this commerce address
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this commerce address.
	 *
	 * @return the name of this commerce address
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the phone number of this commerce address.
	 *
	 * @return the phone number of this commerce address
	 */
	@Override
	public String getPhoneNumber() {
		return model.getPhoneNumber();
	}

	/**
	 * Returns the primary key of this commerce address.
	 *
	 * @return the primary key of this commerce address
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public com.liferay.portal.kernel.model.Region getRegion()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getRegion();
	}

	/**
	 * Returns the region ID of this commerce address.
	 *
	 * @return the region ID of this commerce address
	 */
	@Override
	public long getRegionId() {
		return model.getRegionId();
	}

	/**
	 * Returns the status of this commerce address.
	 *
	 * @return the status of this commerce address
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the street1 of this commerce address.
	 *
	 * @return the street1 of this commerce address
	 */
	@Override
	public String getStreet1() {
		return model.getStreet1();
	}

	/**
	 * Returns the street2 of this commerce address.
	 *
	 * @return the street2 of this commerce address
	 */
	@Override
	public String getStreet2() {
		return model.getStreet2();
	}

	/**
	 * Returns the street3 of this commerce address.
	 *
	 * @return the street3 of this commerce address
	 */
	@Override
	public String getStreet3() {
		return model.getStreet3();
	}

	/**
	 * Returns the subtype of this commerce address.
	 *
	 * @return the subtype of this commerce address
	 */
	@Override
	public String getSubtype() {
		return model.getSubtype();
	}

	@Override
	public String getSubtype(java.util.Locale locale) {
		return model.getSubtype(locale);
	}

	/**
	 * Returns the type of this commerce address.
	 *
	 * @return the type of this commerce address
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this commerce address.
	 *
	 * @return the user ID of this commerce address
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this commerce address.
	 *
	 * @return the user name of this commerce address
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this commerce address.
	 *
	 * @return the user uuid of this commerce address
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the zip of this commerce address.
	 *
	 * @return the zip of this commerce address
	 */
	@Override
	public String getZip() {
		return model.getZip();
	}

	/**
	 * Returns <code>true</code> if this commerce address is default billing.
	 *
	 * @return <code>true</code> if this commerce address is default billing; <code>false</code> otherwise
	 */
	@Override
	public boolean isDefaultBilling() {
		return model.isDefaultBilling();
	}

	/**
	 * Returns <code>true</code> if this commerce address is default shipping.
	 *
	 * @return <code>true</code> if this commerce address is default shipping; <code>false</code> otherwise
	 */
	@Override
	public boolean isDefaultShipping() {
		return model.isDefaultShipping();
	}

	@Override
	public boolean isGeolocated() {
		return model.isGeolocated();
	}

	@Override
	public boolean isSameAddress(CommerceAddress commerceAddress) {
		return model.isSameAddress(commerceAddress);
	}

	/**
	 * Sets the city of this commerce address.
	 *
	 * @param city the city of this commerce address
	 */
	@Override
	public void setCity(String city) {
		model.setCity(city);
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this commerce address.
	 *
	 * @param classNameId the class name ID of this commerce address
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this commerce address.
	 *
	 * @param classPK the class pk of this commerce address
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the commerce address ID of this commerce address.
	 *
	 * @param commerceAddressId the commerce address ID of this commerce address
	 */
	@Override
	public void setCommerceAddressId(long commerceAddressId) {
		model.setCommerceAddressId(commerceAddressId);
	}

	/**
	 * Sets the company ID of this commerce address.
	 *
	 * @param companyId the company ID of this commerce address
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the country ID of this commerce address.
	 *
	 * @param countryId the country ID of this commerce address
	 */
	@Override
	public void setCountryId(long countryId) {
		model.setCountryId(countryId);
	}

	/**
	 * Sets the create date of this commerce address.
	 *
	 * @param createDate the create date of this commerce address
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets whether this commerce address is default billing.
	 *
	 * @param defaultBilling the default billing of this commerce address
	 */
	@Override
	public void setDefaultBilling(boolean defaultBilling) {
		model.setDefaultBilling(defaultBilling);
	}

	/**
	 * Sets whether this commerce address is default shipping.
	 *
	 * @param defaultShipping the default shipping of this commerce address
	 */
	@Override
	public void setDefaultShipping(boolean defaultShipping) {
		model.setDefaultShipping(defaultShipping);
	}

	/**
	 * Sets the description of this commerce address.
	 *
	 * @param description the description of this commerce address
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the external reference code of this commerce address.
	 *
	 * @param externalReferenceCode the external reference code of this commerce address
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this commerce address.
	 *
	 * @param groupId the group ID of this commerce address
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the latitude of this commerce address.
	 *
	 * @param latitude the latitude of this commerce address
	 */
	@Override
	public void setLatitude(double latitude) {
		model.setLatitude(latitude);
	}

	/**
	 * Sets the longitude of this commerce address.
	 *
	 * @param longitude the longitude of this commerce address
	 */
	@Override
	public void setLongitude(double longitude) {
		model.setLongitude(longitude);
	}

	/**
	 * Sets the modified date of this commerce address.
	 *
	 * @param modifiedDate the modified date of this commerce address
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this commerce address.
	 *
	 * @param mvccVersion the mvcc version of this commerce address
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this commerce address.
	 *
	 * @param name the name of this commerce address
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the phone number of this commerce address.
	 *
	 * @param phoneNumber the phone number of this commerce address
	 */
	@Override
	public void setPhoneNumber(String phoneNumber) {
		model.setPhoneNumber(phoneNumber);
	}

	/**
	 * Sets the primary key of this commerce address.
	 *
	 * @param primaryKey the primary key of this commerce address
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the region ID of this commerce address.
	 *
	 * @param regionId the region ID of this commerce address
	 */
	@Override
	public void setRegionId(long regionId) {
		model.setRegionId(regionId);
	}

	/**
	 * Sets the status of this commerce address.
	 *
	 * @param status the status of this commerce address
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the street1 of this commerce address.
	 *
	 * @param street1 the street1 of this commerce address
	 */
	@Override
	public void setStreet1(String street1) {
		model.setStreet1(street1);
	}

	/**
	 * Sets the street2 of this commerce address.
	 *
	 * @param street2 the street2 of this commerce address
	 */
	@Override
	public void setStreet2(String street2) {
		model.setStreet2(street2);
	}

	/**
	 * Sets the street3 of this commerce address.
	 *
	 * @param street3 the street3 of this commerce address
	 */
	@Override
	public void setStreet3(String street3) {
		model.setStreet3(street3);
	}

	/**
	 * Sets the subtype of this commerce address.
	 *
	 * @param subtype the subtype of this commerce address
	 */
	@Override
	public void setSubtype(String subtype) {
		model.setSubtype(subtype);
	}

	/**
	 * Sets the type of this commerce address.
	 *
	 * @param type the type of this commerce address
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this commerce address.
	 *
	 * @param userId the user ID of this commerce address
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this commerce address.
	 *
	 * @param userName the user name of this commerce address
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this commerce address.
	 *
	 * @param userUuid the user uuid of this commerce address
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the zip of this commerce address.
	 *
	 * @param zip the zip of this commerce address
	 */
	@Override
	public void setZip(String zip) {
		model.setZip(zip);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CommerceAddressWrapper wrap(CommerceAddress commerceAddress) {
		return new CommerceAddressWrapper(commerceAddress);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-814573995