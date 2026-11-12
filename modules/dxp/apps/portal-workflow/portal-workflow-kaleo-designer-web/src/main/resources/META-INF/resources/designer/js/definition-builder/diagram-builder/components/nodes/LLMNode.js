/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {defaultLanguageId} from '../../../constants';
import BaseNode from './BaseNode';

export default function LLMNode({
	data: {
		description,
		inputVariables,
		label,
		modelName,
		modelProvider,
		newNode,
		outputVariables,
		prompt,
		rag,
		tools,
		userMessage,
	} = {},
	descriptionSidebar,
	id,
	...otherProps
}) {
	if (!label || !label[defaultLanguageId]) {
		label = {
			[defaultLanguageId]: Liferay.Language.get('llm-node'),
		};
	}

	return (
		<BaseNode
			description={description}
			descriptionSidebar={descriptionSidebar}
			icon="stars"
			id={id}
			inputVariables={inputVariables}
			label={label}
			modelName={modelName}
			modelProvider={modelProvider}
			newNode={newNode}
			nodeTypeClassName="llm-node"
			outputVariables={outputVariables}
			prompt={prompt}
			rag={rag}
			tools={tools}
			type="llm"
			userMessage={userMessage}
			{...otherProps}
		/>
	);
}

LLMNode.propTypes = {
	data: PropTypes.object,
	descriptionSidebar: PropTypes.string,
	id: PropTypes.string,
};
