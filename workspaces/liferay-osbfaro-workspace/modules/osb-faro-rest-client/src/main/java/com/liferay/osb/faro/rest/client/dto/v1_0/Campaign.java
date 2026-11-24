/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.CampaignSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Campaign implements Cloneable, Serializable {

	public static Campaign toDTO(String json) {
		return CampaignSerDes.toDTO(json);
	}

	public Long getAccountsTouched() {
		return accountsTouched;
	}

	public void setAccountsTouched(Long accountsTouched) {
		this.accountsTouched = accountsTouched;
	}

	public void setAccountsTouched(
		UnsafeSupplier<Long, Exception> accountsTouchedUnsafeSupplier) {

		try {
			accountsTouched = accountsTouchedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long accountsTouched;

	public String getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}

	public void setCampaignType(
		UnsafeSupplier<String, Exception> campaignTypeUnsafeSupplier) {

		try {
			campaignType = campaignTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String campaignType;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		try {
			endDate = endDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date endDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public Long getIndividualsTouched() {
		return individualsTouched;
	}

	public void setIndividualsTouched(Long individualsTouched) {
		this.individualsTouched = individualsTouched;
	}

	public void setIndividualsTouched(
		UnsafeSupplier<Long, Exception> individualsTouchedUnsafeSupplier) {

		try {
			individualsTouched = individualsTouchedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long individualsTouched;

	public Date getLastTouchDate() {
		return lastTouchDate;
	}

	public void setLastTouchDate(Date lastTouchDate) {
		this.lastTouchDate = lastTouchDate;
	}

	public void setLastTouchDate(
		UnsafeSupplier<Date, Exception> lastTouchDateUnsafeSupplier) {

		try {
			lastTouchDate = lastTouchDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date lastTouchDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public void setOrigin(
		UnsafeSupplier<String, Exception> originUnsafeSupplier) {

		try {
			origin = originUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String origin;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		try {
			startDate = startDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date startDate;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String status;

	@Override
	public Campaign clone() throws CloneNotSupportedException {
		return (Campaign)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Campaign)) {
			return false;
		}

		Campaign campaign = (Campaign)object;

		return Objects.equals(toString(), campaign.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CampaignSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1581701646