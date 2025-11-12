/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Action} from '../../plugins/page_rules/components/Action';
import {Condition} from '../../plugins/page_rules/components/Condition';
import {ConditionType} from '../../plugins/page_rules/components/RuleBuilderSection';
import {State} from '../../types/State';
import addRuleAction from '../actions/addRule';
import updateNetwork from '../actions/updateNetwork';
import RulesService from '../services/RulesService';

type Props = {
	actions: Action[];
	conditionType: ConditionType;
	conditions: Condition[];
	name: string;
};

export default function addRule({
	actions,
	conditionType,
	conditions,
	name,
	script,
}: Props) {
	return (
		dispatch: (
			action: ReturnType<typeof updateNetwork | typeof addRuleAction>
		) => void,
		getState: () => State
	) => {
		const {segmentsExperienceId} = getState();

		return RulesService.addRule({
			actions,
			conditionType,
			conditions,
			name,
			onNetworkStatus: dispatch,
			segmentsExperienceId,
			script,
		}).then(({addedRuleId, layoutData}) => {
			dispatch(addRuleAction({layoutData, ruleId: addedRuleId}));
		});
	};
}
