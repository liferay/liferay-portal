/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

/**
 * @author Riccardo Ferrari
 */
public class AccountLifecycleStageMetric {

	public long getAccountsCount() {
		return _accountsCount;
	}

	public double getAverageStageDuration() {
		return _averageStageDuration;
	}

	public Double getConversionRateToNextStage() {
		return _conversionRateToNextStage;
	}

	public String getDescription() {
		return _description;
	}

	public double getPercentage() {
		return _percentage;
	}

	public String getStageType() {
		return _stageType;
	}

	public void setAccountsCount(long accountsCount) {
		_accountsCount = accountsCount;
	}

	public void setAverageStageDuration(double averageStageDuration) {
		_averageStageDuration = averageStageDuration;
	}

	public void setConversionRateToNextStage(Double conversionRateToNextStage) {
		_conversionRateToNextStage = conversionRateToNextStage;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setPercentage(double percentage) {
		_percentage = percentage;
	}

	public void setStageType(String stageType) {
		_stageType = stageType;
	}

	private long _accountsCount;
	private double _averageStageDuration;
	private Double _conversionRateToNextStage;
	private String _description;
	private double _percentage;
	private String _stageType;

}