/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.math.BigDecimal;

/**
 * @author Marcos Martins
 */
public class BigQueryPrice {

	public BigDecimal getActiveLogicalStoragePricePerGiBMonth() {
		return _activeLogicalStoragePricePerGiBMonth;
	}

	public String getLocation() {
		return _location;
	}

	public BigDecimal getLongTermLogicalStoragePricePerGiBMonth() {
		return _longTermLogicalStoragePricePerGiBMonth;
	}

	public BigDecimal getOnDemandPricePerTiB() {
		return _onDemandPricePerTiB;
	}

	public BigDecimal getReservationPricePerSlotHour() {
		return _reservationPricePerSlotHour;
	}

	public void setActiveLogicalStoragePricePerGiBMonth(
		BigDecimal activeLogicalStoragePricePerGiBMonth) {

		_activeLogicalStoragePricePerGiBMonth =
			activeLogicalStoragePricePerGiBMonth;
	}

	public void setLocation(String location) {
		_location = location;
	}

	public void setLongTermLogicalStoragePricePerGiBMonth(
		BigDecimal longTermLogicalStoragePricePerGiBMonth) {

		_longTermLogicalStoragePricePerGiBMonth =
			longTermLogicalStoragePricePerGiBMonth;
	}

	public void setOnDemandPricePerTiB(BigDecimal onDemandPricePerTiB) {
		_onDemandPricePerTiB = onDemandPricePerTiB;
	}

	public void setReservationPricePerSlotHour(
		BigDecimal reservationPricePerSlotHour) {

		_reservationPricePerSlotHour = reservationPricePerSlotHour;
	}

	private BigDecimal _activeLogicalStoragePricePerGiBMonth;
	private String _location;
	private BigDecimal _longTermLogicalStoragePricePerGiBMonth;
	private BigDecimal _onDemandPricePerTiB;
	private BigDecimal _reservationPricePerSlotHour;

}