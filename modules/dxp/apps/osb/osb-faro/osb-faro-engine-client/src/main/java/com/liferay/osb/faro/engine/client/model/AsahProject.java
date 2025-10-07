/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.osb.faro.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author André Miranda
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsahProject {

	public AsahProject() {
	}

	public AsahProject(List<AsahProject> asahProjects) {
		_asahProjects = new HashSet<>(asahProjects);
	}

	public AsahProject(String id, Date startDate) {
		_id = id;
		_startDate = startDate;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || !(object instanceof AsahProject)) {
			return false;
		}

		AsahProject project = (AsahProject)object;

		if (Objects.equals(_asahProjects, project._asahProjects) &&
			Objects.equals(_id, project._id) &&
			Objects.equals(_startDate, project._startDate)) {

			return true;
		}

		return false;
	}

	@JsonProperty("projects")
	public Set<AsahProject> getAsahProjects() {
		return _asahProjects;
	}

	public String getId() {
		return _id;
	}

	@JsonFormat(
		pattern = DateUtil.PATTERN_DATE, shape = JsonFormat.Shape.STRING,
		timezone = "UTC"
	)
	public Date getStartDate() {
		if (_startDate == null) {
			return null;
		}

		return new Date(_startDate.getTime());
	}

	@Override
	public int hashCode() {
		return Objects.hash(_asahProjects, _id, _startDate);
	}

	public void setAsahProjects(Set<AsahProject> asahProjects) {
		_asahProjects = asahProjects;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setStartDate(Date startDate) {
		if (startDate != null) {
			_startDate = new Date(startDate.getTime());
		}
	}

	private Set<AsahProject> _asahProjects;
	private String _id;
	private Date _startDate;

}