/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import {defaultLanguageId} from '../../../constants';
import BaseNode from './BaseNode';

export default function JoinNode({
	data: {actions, description, label, newNode, notifications} = {},
	descriptionSidebar,
	id,
	...otherProps
}) {
	if (!label || !label[defaultLanguageId]) {
		label = {
			[defaultLanguageId]: Liferay.Language.get('join[workflow-node]'),
		};
	}

	return (
		<BaseNode
			actions={actions}
			description={description}
			descriptionSidebar={descriptionSidebar}
			icon="radio-button"
			id={id}
			label={label}
			newNode={newNode}
			nodeTypeClassName="join-node"
			notifications={notifications}
			type="join"
			{...otherProps}
		/>
	);
}

JoinNode.propTypes = {
	data: PropTypes.object,
	descriptionSidebar: PropTypes.string,
	id: PropTypes.string,
};
