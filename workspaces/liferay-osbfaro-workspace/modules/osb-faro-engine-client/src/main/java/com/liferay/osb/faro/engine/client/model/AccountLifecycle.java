/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import java.util.Date;
import java.util.List;

/**
 * @author Riccardo Ferrari
 */
public class AccountLifecycle {

	public String getDescription() {
		return _description;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public Date getProcessedDate() {
		if (_processedDate == null) {
			return null;
		}

		return new Date(_processedDate.getTime());
	}

	public String getSegmentId() {
		return _segmentId;
	}

	public List<AccountLifecycleStage> getStages() {
		return _stages;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setProcessedDate(Date processedDate) {
		if (processedDate != null) {
			_processedDate = new Date(processedDate.getTime());
		}
	}

	public void setSegmentId(String segmentId) {
		_segmentId = segmentId;
	}

	public void setStages(List<AccountLifecycleStage> stages) {
		_stages = stages;
	}

	private String _description;
	private String _id;
	private String _name;
	private Date _processedDate;
	private String _segmentId;
	private List<AccountLifecycleStage> _stages;

}