/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {defaultLanguageId} from '../../../constants';
import BaseNode from './BaseNode';

export default function ServiceNode({
	data: {
		description,
		inputVariables,
		javaDelegate,
		label,
		newNode,
		outputVariables,
	} = {},
	descriptionSidebar,
	id,
	...otherProps
}) {
	if (!label || !label[defaultLanguageId]) {
		label = {
			[defaultLanguageId]: Liferay.Language.get('service'),
		};
	}

	return (
		<BaseNode
			description={description}
			descriptionSidebar={descriptionSidebar}
			icon="cog"
			id={id}
			inputVariables={inputVariables}
			javaDelegate={javaDelegate}
			label={label}
			newNode={newNode}
			nodeTypeClassName="service-node"
			outputVariables={outputVariables}
			type="service"
			{...otherProps}
		/>
	);
}

ServiceNode.propTypes = {
	data: PropTypes.object,
	descriptionSidebar: PropTypes.string,
	id: PropTypes.string,
};
