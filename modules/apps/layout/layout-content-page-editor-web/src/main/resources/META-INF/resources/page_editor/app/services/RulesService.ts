/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Action} from '../../plugins/page_rules/components/Action';
import {Condition} from '../../plugins/page_rules/components/Condition';
import {ConditionType} from '../../plugins/page_rules/components/RuleBuilderSection';
import {LayoutData} from '../../types/layout_data/LayoutData';
import {config} from '../config/index';
import draftServiceFetch, {OnNetworkStatus} from './draftServiceFetch';
import serviceFetch from './serviceFetch';

/**
 * Add a rule
 */
type AddRuleProps = {
	actions: Action[];
	conditionType: ConditionType;
	conditions: Condition[];
	name: string;
	onNetworkStatus: OnNetworkStatus;
	segmentsExperienceId: string;
};

function addRule({
	actions,
	conditionType,
	conditions,
	name,
	onNetworkStatus,
	script,
	segmentsExperienceId,
}: AddRuleProps): Promise<{addedRuleId: string; layoutData: LayoutData}> {
	return draftServiceFetch(
		config.addRuleURL,
		{
			body: {
				actions: JSON.stringify(actions),
				conditionType,
				conditions: JSON.stringify(conditions),
				name,
				script,
				segmentsExperienceId,
			},
		},
		onNetworkStatus
	);
}

/**
 * Delete a rule
 */
type DeleteRuleProps = {
	onNetworkStatus: OnNetworkStatus;
	ruleId: string;
	segmentsExperienceId: string;
};

function deleteRule({
	onNetworkStatus,
	ruleId,
	segmentsExperienceId,
}: DeleteRuleProps): Promise<{layoutData: LayoutData}> {
	return draftServiceFetch(
		config.deleteRuleURL,
		{
			body: {
				ruleId,
				segmentsExperienceId,
			},
		},
		onNetworkStatus
	);
}

/**
 * Get roles
 */
function getRoles(): Promise<Array<{name: string; roleId: string}>> {
	return serviceFetch(config.getRolesURL, {});
}

/**
 * Get users
 */
function getUsers(): Promise<Array<{screenName: string; userId: string}>> {
	return serviceFetch(config.getUsersURL, {});
}

/**
 * Update a rule with new name, actions and conditions
 */
type UpdateRuleProps = {
	actions: Action[];
	conditionType: ConditionType;
	conditions: Condition[];
	name: string;
	onNetworkStatus: OnNetworkStatus;
	ruleId: string;
	segmentsExperienceId: string;
};

function updateRule({
	actions,
	conditionType,
	conditions,
	name,
	onNetworkStatus,
	ruleId,
	script,
	segmentsExperienceId,
}: UpdateRuleProps): Promise<{layoutData: LayoutData}> {
	return draftServiceFetch(
		config.updateRuleURL,
		{
			body: {
				actions: JSON.stringify(actions),
				conditionType,
				conditions: JSON.stringify(conditions),
				name,
				ruleId,
				script,
				segmentsExperienceId,
			},
		},
		onNetworkStatus
	);
}

export default {addRule, deleteRule, getRoles, getUsers, updateRule};
