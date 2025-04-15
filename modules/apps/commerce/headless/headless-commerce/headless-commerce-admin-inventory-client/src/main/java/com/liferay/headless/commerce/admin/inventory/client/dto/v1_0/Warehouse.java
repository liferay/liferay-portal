/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.dto.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class Warehouse implements Cloneable, Serializable {

	public static Warehouse toDTO(String json) {
		return WarehouseSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCity(UnsafeSupplier<String, Exception> cityUnsafeSupplier) {
		try {
			city = cityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String city;

	public String getCountryISOCode() {
		return countryISOCode;
	}

	public void setCountryISOCode(String countryISOCode) {
		this.countryISOCode = countryISOCode;
	}

	public void setCountryISOCode(
		UnsafeSupplier<String, Exception> countryISOCodeUnsafeSupplier) {

		try {
			countryISOCode = countryISOCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String countryISOCode;

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> description;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(
		UnsafeSupplier<Double, Exception> latitudeUnsafeSupplier) {

		try {
			latitude = latitudeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double latitude;

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(
		UnsafeSupplier<Double, Exception> longitudeUnsafeSupplier) {

		try {
			longitude = longitudeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double longitude;

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public void setName(
		UnsafeSupplier<Map<String, String>, Exception> nameUnsafeSupplier) {

		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> name;

	public String getRegionISOCode() {
		return regionISOCode;
	}

	public void setRegionISOCode(String regionISOCode) {
		this.regionISOCode = regionISOCode;
	}

	public void setRegionISOCode(
		UnsafeSupplier<String, Exception> regionISOCodeUnsafeSupplier) {

		try {
			regionISOCode = regionISOCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String regionISOCode;

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public void setStreet1(
		UnsafeSupplier<String, Exception> street1UnsafeSupplier) {

		try {
			street1 = street1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String street1;

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public void setStreet2(
		UnsafeSupplier<String, Exception> street2UnsafeSupplier) {

		try {
			street2 = street2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String street2;

	public String getStreet3() {
		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	public void setStreet3(
		UnsafeSupplier<String, Exception> street3UnsafeSupplier) {

		try {
			street3 = street3UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String street3;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	public WarehouseItem[] getWarehouseItems() {
		return warehouseItems;
	}

	public void setWarehouseItems(WarehouseItem[] warehouseItems) {
		this.warehouseItems = warehouseItems;
	}

	public void setWarehouseItems(
		UnsafeSupplier<WarehouseItem[], Exception>
			warehouseItemsUnsafeSupplier) {

		try {
			warehouseItems = warehouseItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WarehouseItem[] warehouseItems;

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setZip(UnsafeSupplier<String, Exception> zipUnsafeSupplier) {
		try {
			zip = zipUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String zip;

	@Override
	public Warehouse clone() throws CloneNotSupportedException {
		return (Warehouse)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Warehouse)) {
			return false;
		}

		Warehouse warehouse = (Warehouse)object;

		return Objects.equals(toString(), warehouse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WarehouseSerDes.toJSON(this);
	}

}