/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import React from 'react';

import buildLocalizedValue from '../../common/utils/buildLocalizedValue';
import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectStructure from '../selectors/selectStructure';
import {Group} from '../types/Structure';
import {
	FIELD_TYPES,
	FIELD_TYPE_ICON,
	FIELD_TYPE_LABEL,
	Field,
	getDefaultField,
} from '../utils/field';
import getRandomId from '../utils/getRandomId';
import getRandomName from '../utils/getRandomName';
import getUuid from '../utils/getUuid';
import openReferencedStructureModal from '../utils/openReferencedStructureModal';

type Item = {
	className?: string;
	label: string;
	onClick: () => void;
	symbolLeft: string;
};

export default function AddChildDropdown({
	className,
	displayType = 'secondary',
	parentUuid,
	triggerProps,
}: {
	className?: string;
	displayType?: 'secondary' | 'unstyled';
	parentUuid?: Group['uuid'];
	triggerProps?: React.HTMLAttributes<HTMLButtonElement> & {
		'data-canonical-name'?: string;
	};
}) {
	const dispatch = useStateDispatch();
	const structure = useSelector(selectStructure);

	const {data: objectDefinitions, status} = useCache('object-definitions');

	const addField = (type: Field['type']) =>
		dispatch({
			field: getDefaultField({
				parent: parentUuid ?? structure.uuid,
				type,
			}),
			type: 'add-field',
		});

	const addRelatedContent = () =>
		dispatch({
			relatedContent: {
				erc: getRandomId(),
				label: buildLocalizedValue('select-related-content'),
				multiselection: false,
				name: getRandomName(),
				parent: parentUuid ?? structure.uuid,
				relatedStructureERC: '',
				type: 'related-content',
				uuid: getUuid(),
			},
			type: 'add-related-content',
		});

	return (
		<>
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
						label: Liferay.Language.get('select-related-content'),
						onClick: () => addRelatedContent(),
						symbolLeft: 'select-from-list',
					},
					{
						className: 'dropdown-item-cms-warning',
						label: Liferay.Language.get(
							'referenced-content-structure'
						),
						onClick: () =>
							openReferencedStructureModal({
								dispatch,
								objectDefinitions,
								parentUuid: parentUuid ?? structure.uuid,
								status,
								structure,
							}),
						symbolLeft: 'edit-layout',
					},
				]}
				menuElementAttrs={{className: 'dropdown-menu-cms'}}
				menuHeight="auto"
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('add-field')}
						className={className}
						displayType={displayType}
						size="sm"
						symbol="plus"
						title={Liferay.Language.get('add-field')}
						{...triggerProps}
					/>
				}
			/>
		</>
	);
}
