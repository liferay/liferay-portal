/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.preferences;

import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Kong
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class WorkspacePreferences {

	public void addDistributionCardTabPreferences(
		String id, String individualSegmentId,
		DistributionCardTabPreferences distributionCardTabPreferences) {

		DistributionCardTabsPreferences distributionCardTabsPreferences =
			getDistributionCardTabsPreferences(individualSegmentId);

		distributionCardTabsPreferences.addDistributionTab(
			id, distributionCardTabPreferences);
	}

	public Map<String, EmailReportPreferences> addEmailReportPreference(
		String channelId, Boolean enabled, String frequency) {

		if (enabled == null) {
			enabled = false;
		}

		_emailReportPreferences.put(
			channelId, new EmailReportPreferences(enabled, frequency));

		return _emailReportPreferences;
	}

	public String getDefaultChannelId() {
		return _defaultChannelId;
	}

	public DistributionCardTabsPreferences getDistributionCardTabsPreferences(
		String individualSegmentId) {

		if (Validator.isNull(individualSegmentId)) {
			return _individualDashboardPreferences.
				getDistributionCardTabsPreferences();
		}

		IndividualSegmentPreferences individualSegmentPreferences =
			_individualSegmentPreferences.get(individualSegmentId);

		if (individualSegmentPreferences == null) {
			individualSegmentPreferences = new IndividualSegmentPreferences();

			_individualSegmentPreferences.put(
				individualSegmentId, individualSegmentPreferences);
		}

		return individualSegmentPreferences.
			getDistributionCardTabsPreferences();
	}

	public Map<String, EmailReportPreferences> getEmailReportPreferences(
		String channelId) {

		if (Validator.isNull(channelId)) {
			return _emailReportPreferences;
		}

		return Collections.singletonMap(
			channelId,
			_emailReportPreferences.getOrDefault(
				channelId, new EmailReportPreferences(false, "monthly")));
	}

	public IndividualDashboardPreferences getIndividualDashboardPreferences() {
		return _individualDashboardPreferences;
	}

	public Map<String, IndividualSegmentPreferences>
		getIndividualSegmentPreferences() {

		return _individualSegmentPreferences;
	}

	public boolean isUpgradeModalSeen() {
		return _upgradeModalSeen;
	}

	public void removeDistributionCardTabPreferences(
		String id, String individualSegmentId) {

		DistributionCardTabsPreferences distributionCardTabsPreferences =
			getDistributionCardTabsPreferences(individualSegmentId);

		distributionCardTabsPreferences.removeDistributionTab(id);
	}

	public boolean removeEmailReportPreferences(String channelId) {
		EmailReportPreferences emailReportPreferences =
			_emailReportPreferences.remove(channelId);

		if (emailReportPreferences != null) {
			return true;
		}

		return false;
	}

	public void removeIndividualSegmentPreference(String individualSegmentId) {
		_individualSegmentPreferences.remove(individualSegmentId);
	}

	public void removeIndividualSegmentsPreferences(
		List<String> individualSegmentIds) {

		for (String individualSegmentId : individualSegmentIds) {
			_individualSegmentPreferences.remove(individualSegmentId);
		}
	}

	public void setDefaultChannelId(String defaultChannelId) {
		_defaultChannelId = defaultChannelId;
	}

	public void setEmailReportPreferences(
		Map<String, EmailReportPreferences> emailReportPreferences) {

		_emailReportPreferences = emailReportPreferences;
	}

	public void setIndividualDashboardPreferences(
		IndividualDashboardPreferences individualDashboardPreferences) {

		_individualDashboardPreferences = individualDashboardPreferences;
	}

	public void setIndividualSegmentPreferences(
		Map<String, IndividualSegmentPreferences>
			individualSegmentPreferences) {

		_individualSegmentPreferences = individualSegmentPreferences;
	}

	public void setUpgradeModalSeen(boolean upgradeModalSeen) {
		_upgradeModalSeen = upgradeModalSeen;
	}

	private String _defaultChannelId;
	private Map<String, EmailReportPreferences> _emailReportPreferences =
		new HashMap<>();
	private IndividualDashboardPreferences _individualDashboardPreferences =
		new IndividualDashboardPreferences();
	private Map<String, IndividualSegmentPreferences>
		_individualSegmentPreferences = new HashMap<>();
	private boolean _upgradeModalSeen;

}