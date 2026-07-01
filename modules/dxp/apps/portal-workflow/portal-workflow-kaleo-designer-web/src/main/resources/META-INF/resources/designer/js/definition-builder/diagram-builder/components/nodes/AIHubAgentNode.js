/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {defaultLanguageId} from '../../../constants';
import BaseNode from './BaseNode';

export default function AIHubAgentNode({
	data: {
		agentDefinitionExternalReferenceCode,
		description,
		label,
		newNode,
		timeout,
	} = {},
	descriptionSidebar,
	id,
	...otherProps
}) {
	if (!label || !label[defaultLanguageId]) {
		label = {
			[defaultLanguageId]: Liferay.Language.get('ai-hub-agent'),
		};
	}

	return (
		<BaseNode
			agentDefinitionExternalReferenceCode={
				agentDefinitionExternalReferenceCode
			}
			description={description}
			descriptionSidebar={descriptionSidebar}
			icon="chip"
			id={id}
			label={label}
			newNode={newNode}
			nodeTypeClassName="ai-hub-agent-node"
			timeout={timeout}
			type="ai-hub-agent"
			{...otherProps}
		/>
	);
}

AIHubAgentNode.propTypes = {
	data: PropTypes.object,
	descriptionSidebar: PropTypes.string,
	id: PropTypes.string,
};
