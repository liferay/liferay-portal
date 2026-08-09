/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;

/**
 * @author Alexandre Oliveira
 */
public class SegmentActivation {

	public String getCronExpression() {
		return _cronExpression;
	}

	public FrequencyType getFrequencyType() {
		return _frequencyType;
	}

	public String getId() {
		return _id;
	}

	public Date getLastRunDate() {
		if (_lastRunDate == null) {
			return null;
		}

		return new Date(_lastRunDate.getTime());
	}

	public Date getScheduleEndDate() {
		if (_scheduleEndDate == null) {
			return null;
		}

		return new Date(_scheduleEndDate.getTime());
	}

	public Date getScheduleStartDate() {
		if (_scheduleStartDate == null) {
			return null;
		}

		return new Date(_scheduleStartDate.getTime());
	}

	public ScheduleType getScheduleType() {
		return _scheduleType;
	}

	public String getSegmentId() {
		return _segmentId;
	}

	public void setCronExpression(String cronExpression) {
		_cronExpression = cronExpression;
	}

	public void setFrequencyType(FrequencyType frequencyType) {
		_frequencyType = frequencyType;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLastRunDate(Date lastRunDate) {
		if (lastRunDate != null) {
			_lastRunDate = new Date(lastRunDate.getTime());
		}
	}

	public void setScheduleEndDate(Date scheduleEndDate) {
		if (scheduleEndDate != null) {
			_scheduleEndDate = new Date(scheduleEndDate.getTime());
		}
	}

	public void setScheduleStartDate(Date scheduleStartDate) {
		if (scheduleStartDate != null) {
			_scheduleStartDate = new Date(scheduleStartDate.getTime());
		}
	}

	public void setScheduleType(ScheduleType scheduleType) {
		_scheduleType = scheduleType;
	}

	public void setSegmentId(String segmentId) {
		_segmentId = segmentId;
	}

	public enum FrequencyType {

		BETWEEN, INDEFINITELY

	}

	public enum ScheduleType {

		BATCH

	}

	private String _cronExpression;
	private FrequencyType _frequencyType;
	private String _id;
	private Date _lastRunDate;
	private Date _scheduleEndDate;
	private Date _scheduleStartDate;
	private ScheduleType _scheduleType;
	private String _segmentId;

}