/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import React, {useState} from 'react';

import {useStateDispatch} from '../../structure_builder/contexts/StateContext';
import {
	FIELD_TYPES,
	FIELD_TYPE_LABEL,
	Field,
	getDefaultField,
} from '../../structure_builder/utils/field';
import {Structure} from '../types/Structure';
import ReferencedStructureModal from './ReferencedStructureModal';

export default function AddFieldDropdown({
	triggerType = 'text',
}: {
	triggerType?: 'text' | 'icon';
}) {
	const dispatch = useStateDispatch();

	const [showStructuresModal, setShowStructuresModal] = useState(false);

	const addField = (type: Field['type']) =>
		dispatch({
			field: getDefaultField({type}),
			type: 'add-field',
		});

	const addReferencedStructures = (ercs: Array<Structure['erc']>) =>
		dispatch({
			ercs,
			type: 'add-referenced-structures',
		});

	return (
		<>
			{showStructuresModal ? (
				<ReferencedStructureModal
					onAdd={addReferencedStructures}
					onCloseModal={() => setShowStructuresModal(false)}
				/>
			) : null}

			<ClayDropDownWithItems
				items={[
					...FIELD_TYPES.map((type) => ({
						label: FIELD_TYPE_LABEL[type],
						onClick: () => addField(type),
					})),
					{
						label: Liferay.Language.get('referenced-structure'),
						onClick: () => setShowStructuresModal(true),
					},
				]}
				trigger={
					triggerType === 'text' ? (
						<ClayButton displayType="secondary" size="sm">
							{Liferay.Language.get('add-field')}
						</ClayButton>
					) : (
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('add-field')}
							displayType="secondary"
							size="sm"
							symbol="plus"
						/>
					)
				}
			/>
		</>
	);
}
