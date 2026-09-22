/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

/**
 * @author Caio Pinheiro
 */
public enum LifecycleTriggerType {

	ACCOUNT_STAGE_CHANGES("accountStageChanges", "account-stage-changes", null),
	NET_NEW_PIPELINE_ACCOUNTS(
		"netNewPipelineAccounts", "net-new-pipeline-accounts", "PIPELINE"),
	NEW_ACCOUNTS("newAccounts", "new-accounts", null),
	NEW_AT_RISK_ACCOUNTS(
		"newAtRiskAccounts", "new-at-risk-accounts", "AT_RISK"),
	NEW_STALLED_ACCOUNTS("newStalledAccounts", "new-stalled-accounts", null);

	public String getKey() {
		return _key;
	}

	public String getLanguageKey() {
		return _languageKey;
	}

	public String getToLifecycleStage() {
		return _toLifecycleStage;
	}

	private LifecycleTriggerType(
		String key, String languageKey, String toLifecycleStage) {

		_key = key;
		_languageKey = languageKey;
		_toLifecycleStage = toLifecycleStage;
	}

	private final String _key;
	private final String _languageKey;
	private final String _toLifecycleStage;

}