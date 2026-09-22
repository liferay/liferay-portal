/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.preferences;

import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Caio Pinheiro
 */
public class LifecycleNotificationPreferences {

	public LifecycleNotificationPreferences() {
	}

	public LifecycleNotificationPreferences(
		Boolean accountStageChanges, String emailFrequency,
		Boolean netNewPipelineAccounts, Boolean newAccounts,
		Boolean newAtRiskAccounts, Boolean newStalledAccounts) {

		_accountStageChanges = accountStageChanges;
		_emailFrequency = emailFrequency;
		_netNewPipelineAccounts = netNewPipelineAccounts;
		_newAccounts = newAccounts;
		_newAtRiskAccounts = newAtRiskAccounts;
		_newStalledAccounts = newStalledAccounts;
	}

	public Boolean getAccountStageChanges() {
		return GetterUtil.getBoolean(_accountStageChanges);
	}

	public String getEmailFrequency() {
		return _emailFrequency;
	}

	public Boolean getNetNewPipelineAccounts() {
		return GetterUtil.getBoolean(_netNewPipelineAccounts);
	}

	public Boolean getNewAccounts() {
		return GetterUtil.getBoolean(_newAccounts);
	}

	public Boolean getNewAtRiskAccounts() {
		return GetterUtil.getBoolean(_newAtRiskAccounts);
	}

	public Boolean getNewStalledAccounts() {
		return GetterUtil.getBoolean(_newStalledAccounts);
	}

	public void setAccountStageChanges(Boolean accountStageChanges) {
		_accountStageChanges = accountStageChanges;
	}

	public void setEmailFrequency(String emailFrequency) {
		_emailFrequency = emailFrequency;
	}

	public void setNetNewPipelineAccounts(Boolean netNewPipelineAccounts) {
		_netNewPipelineAccounts = netNewPipelineAccounts;
	}

	public void setNewAccounts(Boolean newAccounts) {
		_newAccounts = newAccounts;
	}

	public void setNewAtRiskAccounts(Boolean newAtRiskAccounts) {
		_newAtRiskAccounts = newAtRiskAccounts;
	}

	public void setNewStalledAccounts(Boolean newStalledAccounts) {
		_newStalledAccounts = newStalledAccounts;
	}

	private Boolean _accountStageChanges;
	private String _emailFrequency;
	private Boolean _netNewPipelineAccounts;
	private Boolean _newAccounts;
	private Boolean _newAtRiskAccounts;
	private Boolean _newStalledAccounts;

}