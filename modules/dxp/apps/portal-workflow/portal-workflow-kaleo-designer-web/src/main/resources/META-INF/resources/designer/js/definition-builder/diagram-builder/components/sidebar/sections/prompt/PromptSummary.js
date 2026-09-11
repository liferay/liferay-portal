/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useContext} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import SidebarPanel from '../../SidebarPanel';
import InputOutputVariables from '../shared-components/InputOutputVariables';

const PromptSummary = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('prompt')}>
			<ClayForm.Group>
				<ClayInput
					component="textarea"
					onChange={({target}) =>
						setSelectedItem({
							...selectedItem,
							data: {
								...selectedItem.data,
								prompt: target.value,
							},
						})
					}
					required={true}
					type="text"
					value={selectedItem?.data.prompt ?? ''}
				/>
			</ClayForm.Group>

			<InputOutputVariables />

			<ClayForm.Group>
				<label htmlFor="userMessage">
					{Liferay.Language.get('user-message')}
				</label>

				<ClayInput
					component="textarea"
					id="userMessage"
					onChange={({target}) =>
						setSelectedItem({
							...selectedItem,
							data: {
								...selectedItem.data,
								userMessage: target.value,
							},
						})
					}
					required={true}
					type="text"
					value={selectedItem?.data.userMessage ?? ''}
				/>
			</ClayForm.Group>
		</SidebarPanel>
	);
};

PromptSummary.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default PromptSummary;
