/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {defaultLanguageId} from '../../../constants';
import BaseNode from './BaseNode';

export default function AskAHumanNode({
	data: {
		description,
		inputVariables,
		label,
		newNode,
		outputVariables,
		question,
	} = {},
	descriptionSidebar,
	id,
	...otherProps
}) {
	if (!label || !label[defaultLanguageId]) {
		label = {
			[defaultLanguageId]: Liferay.Language.get('ask-a-human'),
		};
	}

	return (
		<BaseNode
			description={description}
			descriptionSidebar={descriptionSidebar}
			icon="user"
			id={id}
			inputVariables={inputVariables}
			label={label}
			newNode={newNode}
			nodeTypeClassName="ask-a-human-node"
			outputVariables={outputVariables}
			question={question}
			type="ask-a-human"
			{...otherProps}
		/>
	);
}

AskAHumanNode.propTypes = {
	data: PropTypes.object,
	descriptionSidebar: PropTypes.string,
	id: PropTypes.string,
};
