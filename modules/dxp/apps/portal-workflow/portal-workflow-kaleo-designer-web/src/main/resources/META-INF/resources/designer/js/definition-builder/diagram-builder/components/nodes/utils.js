/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {defaultLanguageId} from '../../../constants';
import {insertNodeAt} from '../../util/insertNodeAt';
import AIDecisionNode from './AIDecisionNode';
import AIHubAgentNode from './AIHubAgentNode';
import ConditionNode from './ConditionNode';
import ForkNode from './ForkNode';
import HTTPRequestNode from './HTTPRequestNode';
import JoinNode from './JoinNode';
import JoinXorNode from './JoinXorNode';
import LLMNode from './LLMNode';
import ServiceNode from './ServiceNode';
import TaskNode from './TaskNode';
import EndNode from './state/EndNode';
import StartNode from './state/StartNode';
import StateNode from './state/StateNode';

const defaultNodes = [
	{
		data: {
			description: Liferay.Language.get('begin-a-workflow'),
			label: {[defaultLanguageId]: Liferay.Language.get('start')},
		},
		id: uuidv4(),
		position: {x: 300, y: 100},
		type: 'start',
	},
	{
		data: {
			description: Liferay.Language.get('conclude-the-workflow'),
			label: {[defaultLanguageId]: Liferay.Language.get('end')},
		},
		id: uuidv4(),
		position: {x: 300, y: 400},
		type: 'end',
	},
];

const nodeDescription = {
	'ai-decision': Liferay.Language.get('make-a-decision-using-llm-models'),
	'ai-hub-agent': Liferay.Language.get('invoke-an-ai-hub-agent'),
	'condition': Liferay.Language.get('execute-conditional-logic'),
	'end': Liferay.Language.get('conclude-the-workflow'),
	'fork': Liferay.Language.get('split-the-workflow-into-multiple-paths'),
	'http-request': Liferay.Language.get(
		'make-outbound-calls-to-rest-apis-or-client-extensions'
	),
	'join': Liferay.Language.get('all-interactions-need-to-be-closed'),
	'join-xor': Liferay.Language.get('only-one-interaction-needs-to-be-closed'),
	'llm': Liferay.Language.get(
		'generate-content-summarize-and-classify-data-using-llm-models'
	),
	'service': Liferay.Language.get(
		'execute-custom-business-logic-using-a-java-delegate'
	),
	'start': Liferay.Language.get('begin-a-workflow'),
	'state': Liferay.Language.get('execute-actions-in-the-workflow'),
	'task': Liferay.Language.get('ask-a-user-to-work-on-the-item'),
};

let nodeTypes = {
	'condition': ConditionNode,
	'end': EndNode,
	'fork': ForkNode,
	'join': JoinNode,
	'join-xor': JoinXorNode,
	'start': StartNode,
	'state': StateNode,
	'task': TaskNode,
};

if (Liferay.FeatureFlags['LPD-62272']) {
	nodeTypes = insertNodeAt(nodeTypes, 'ai-hub-agent', AIHubAgentNode, 1);
	nodeTypes = insertNodeAt(nodeTypes, 'ai-decision', AIDecisionNode, 2);
	nodeTypes = insertNodeAt(nodeTypes, 'llm', LLMNode, 7);
	nodeTypes = insertNodeAt(nodeTypes, 'http-request', HTTPRequestNode, 8);
	nodeTypes = insertNodeAt(nodeTypes, 'service', ServiceNode, 9);
}

export {defaultNodes, nodeDescription, nodeTypes};
