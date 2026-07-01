/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import React, {useContext} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import {getUpdatedDataItem} from '../utils';

const AIHubAgentDefinition = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('ai-hub-agent')}>
			<ClayForm.Group>
				<label htmlFor="agentDefinitionExternalReferenceCode">
					{Liferay.Language.get(
						'agent-definition-external-reference-code'
					)}
				</label>

				<ClayInput
					id="agentDefinitionExternalReferenceCode"
					onChange={({target}) =>
						setSelectedItem(
							getUpdatedDataItem(
								'agentDefinitionExternalReferenceCode',
								selectedItem,
								target
							)
						)
					}
					required={true}
					type="text"
					value={
						selectedItem?.data
							.agentDefinitionExternalReferenceCode ?? ''
					}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

export default AIHubAgentDefinition;
