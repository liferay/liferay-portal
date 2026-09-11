/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import React, {useContext, useMemo} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import {
	formatVariablesForTextarea,
	parseVariablesInput,
} from '../../../../util/parseVariables';

const PLACEHOLDER = '[{"name":"tone", "type":"string"}]';

const InputOutputVariables = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const inputVariablesValue = useMemo(
		() => formatVariablesForTextarea(selectedItem?.data?.inputVariables),
		[selectedItem]
	);

	const outputVariablesValue = useMemo(
		() => formatVariablesForTextarea(selectedItem?.data?.outputVariables),
		[selectedItem]
	);

	const handleVariablesChange =
		(field) =>
		({target}) => {
			if (!selectedItem) {
				return;
			}

			setSelectedItem({
				...selectedItem,
				data: {
					...selectedItem.data,
					[field]: parseVariablesInput(target.value),
				},
			});
		};

	return (
		<>
			<ClayForm.Group>
				<label htmlFor="inputVariables">
					{Liferay.Language.get('input-variables')}
				</label>

				<ClayInput
					component="textarea"
					id="inputVariables"
					onChange={handleVariablesChange('inputVariables')}
					placeholder={PLACEHOLDER}
					type="text"
					value={inputVariablesValue}
				/>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="outputVariables">
					{Liferay.Language.get('output-variables')}
				</label>

				<ClayInput
					component="textarea"
					id="outputVariables"
					onChange={handleVariablesChange('outputVariables')}
					placeholder={PLACEHOLDER}
					type="text"
					value={outputVariablesValue}
				/>
			</ClayForm.Group>
		</>
	);
};

export default InputOutputVariables;
