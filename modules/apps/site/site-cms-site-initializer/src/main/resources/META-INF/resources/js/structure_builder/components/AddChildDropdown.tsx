/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import React, {useState} from 'react';

import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {ReferencedStructure} from '../types/Structure';
import {
	FIELD_TYPES,
	FIELD_TYPE_ICON,
	FIELD_TYPE_LABEL,
	Field,
	getDefaultField,
} from '../utils/field';
import ReferencedStructureModal from './ReferencedStructureModal';

type Item = {
	className?: string;
	label: string;
	onClick: () => void;
	symbolLeft: string;
};

export default function AddChildDropdown({
	triggerType = 'text',
}: {
	triggerType?: 'text' | 'icon';
}) {
	const dispatch = useStateDispatch();
	const structureUuid = useSelector(selectStructureUuid);

	const [showStructuresModal, setShowStructuresModal] = useState(false);

	const addField = (type: Field['type']) =>
		dispatch({
			field: getDefaultField({parent: structureUuid, type}),
			type: 'add-field',
		});

	const addReferencedStructures = (
		referencedStructures: ReferencedStructure[]
	) =>
		dispatch({
			referencedStructures,
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
					...FIELD_TYPES.map(
						(type): Item => ({
							label: FIELD_TYPE_LABEL[type],
							onClick: () => addField(type),
							symbolLeft: FIELD_TYPE_ICON[type],
						})
					),
					{type: 'divider'},
					{
						className: 'dropdown-item-cms-warning',
						label: Liferay.Language.get(
							'referenced-content-structure'
						),
						onClick: () => setShowStructuresModal(true),
						symbolLeft: 'edit-layout',
					},
				]}
				menuElementAttrs={{className: 'dropdown-menu-cms'}}
				menuHeight="auto"
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
							title={Liferay.Language.get('add-field')}
						/>
					)
				}
			/>
		</>
	);
}
