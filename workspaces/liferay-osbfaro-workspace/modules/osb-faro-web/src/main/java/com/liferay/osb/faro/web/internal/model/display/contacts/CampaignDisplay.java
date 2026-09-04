/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.model.Campaign;

import java.util.Date;

/**
 * @author Riccardo Ferrari
 */
public class CampaignDisplay {

	public CampaignDisplay(Campaign campaign) {
		_accountsTouched = campaign.getAccountsTouched();
		_campaignName = campaign.getCampaignName();
		_campaignType = campaign.getCampaignType();
		_endDate = campaign.getEndDate();
		_id = campaign.getId();
		_individualsTouched = campaign.getIndividualsTouched();
		_origin = campaign.getOrigin();
		_startDate = campaign.getStartDate();
		_status = campaign.getStatus();
	}

	private final Long _accountsTouched;
	private final String _campaignName;
	private final String _campaignType;
	private final Date _endDate;
	private final String _id;
	private final Long _individualsTouched;
	private final String _origin;
	private final Date _startDate;
	private final String _status;

}