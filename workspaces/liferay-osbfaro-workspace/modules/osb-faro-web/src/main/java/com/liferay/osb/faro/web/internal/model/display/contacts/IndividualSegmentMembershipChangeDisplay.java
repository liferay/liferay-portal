/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class IndividualSegmentMembershipChangeDisplay {

	public IndividualSegmentMembershipChangeDisplay() {
	}

	@SuppressWarnings("unchecked")
	public IndividualSegmentMembershipChangeDisplay(
		IndividualSegmentMembershipChange individualSegmentMembershipChange) {

		Map<String, Object> embeddedResources =
			individualSegmentMembershipChange.getEmbeddedResources();

		if (MapUtil.isNotEmpty(embeddedResources)) {
			_accountNames = JSONUtil.convertValue(
				embeddedResources.get("account-names"),
				new TypeReference<List<String>>() {
				});
		}

		_dateChanged = individualSegmentMembershipChange.getDateChanged();
		_dateFirst = individualSegmentMembershipChange.getDateFirst();
		_id = individualSegmentMembershipChange.getId();
		_individualDeleted =
			individualSegmentMembershipChange.isIndividualDeleted();
		_individualEmail =
			individualSegmentMembershipChange.getIndividualEmail();
		_individualId = individualSegmentMembershipChange.getIndividualId();
		_individualName = individualSegmentMembershipChange.getIndividualName();
		_individualsCount =
			individualSegmentMembershipChange.getIndividualsCount();
		_individualSegmentId =
			individualSegmentMembershipChange.getIndividualSegmentId();
		_operation = individualSegmentMembershipChange.getOperation();
	}

	private List<String> _accountNames;
	private Date _dateChanged;
	private Date _dateFirst;
	private String _id;
	private boolean _individualDeleted;
	private String _individualEmail;
	private String _individualId;
	private String _individualName;
	private String _individualSegmentId;
	private Long _individualsCount;
	private String _operation;

}