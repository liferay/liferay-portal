/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useContext, useMemo} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import {
	formatVariablesForTextarea,
	parseVariablesInput,
} from '../../../../util/parseVariables';
import SidebarPanel from '../../SidebarPanel';

const ToolsSummary = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const tools = useMemo(
		() => formatVariablesForTextarea(selectedItem?.data?.tools),
		[selectedItem]
	);

	const onToolsChanges =
		() =>
		({target}) => {
			if (!selectedItem) {
				return;
			}

			const text = target.value;
			const parsed = parseVariablesInput(text);

			const updatedItem = {
				...selectedItem,
				data: {
					...selectedItem.data,
					['tools']: parsed,
				},
			};

			setSelectedItem(updatedItem);
		};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('tools')}>
			<ClayForm.Group>
				<ClayInput
					component="textarea"
					onChange={onToolsChanges()}
					placeholder='[{"externalReferenceCode":"L_LIFERAY_AI_HUB_MCP_SERVER"}]'
					type="text"
					value={tools}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

ToolsSummary.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default ToolsSummary;
