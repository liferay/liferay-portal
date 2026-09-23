/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import React, {useRef, useState} from 'react';

import buildLocalizedValue from '../../common/utils/buildLocalizedValue';
import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectPublishedChildren from '../selectors/selectPublishedChildren';
import selectStructure from '../selectors/selectStructure';
import selectSystemFieldNames from '../selectors/selectSystemFieldNames';
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
import handleAddGroup from '../utils/handleAddGroup';
import openReferencedStructureModal from '../utils/openReferencedStructureModal';

const MENU_SPACING = 16;

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
	const publishedChildren = useSelector(selectPublishedChildren);
	const structure = useSelector(selectStructure);
	const systemFieldNames = useSelector(selectSystemFieldNames);

	const {data: objectDefinitions, status} = useCache('object-definitions');

	const [maxHeight, setMaxHeight] = useState<number>();
	const [search, setSearch] = useState('');

	const triggerRef = useRef<HTMLButtonElement | null>(null);

	const nested =
		Boolean(Liferay.FeatureFlags['LPD-96666']) &&
		Boolean(parentUuid) &&
		parentUuid !== structure.uuid;

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
		<ClayDropDownWithItems
			items={[
				Liferay.FeatureFlags['LPD-96666']
					? [
							{
								className: 'dropdown-item-cms-success',
								label: Liferay.Language.get('group'),
								onClick: () =>
									handleAddGroup({
										dispatch,
										parent: parentUuid ?? structure.uuid,
										publishedChildren,
										structure,
										uuids: [],
									}),
								symbolLeft: 'fieldset',
							},
						]
					: [],
				FIELD_TYPES.map(
					(type): Item => ({
						label: FIELD_TYPE_LABEL[type],
						onClick: () => addField(type),
						symbolLeft: FIELD_TYPE_ICON[type],
					})
				),
				nested
					? []
					: [
							{
								className: 'dropdown-item-cms-warning',
								label: Liferay.Language.get(
									'select-related-content'
								),
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
										parentUuid:
											parentUuid ?? structure.uuid,
										status,
										structure,
										systemFieldNames,
									}),
								symbolLeft: 'edit-layout',
							},
						],
			]
				.map((section) =>
					section.filter(({label}) =>
						label.toLowerCase().includes(search.toLowerCase())
					)
				)
				.filter((section) => section.length)
				.flatMap((section, index) =>
					index ? [{type: 'divider' as const}, ...section] : section
				)}
			menuElementAttrs={{
				className:
					'dropdown-menu-cms structure-builder__add-child-menu',
				style: {maxHeight},
			}}
			onActiveChange={(active) => {
				if (!active) {
					setSearch('');

					return;
				}

				if (triggerRef.current) {
					setMaxHeight(
						window.innerHeight -
							triggerRef.current.getBoundingClientRect().bottom -
							MENU_SPACING
					);
				}
			}}
			onSearchValueChange={setSearch}
			searchProps={{
				className: 'pb-2',
				placeholder: Liferay.Language.get('search'),
			}}
			searchValue={search}
			searchable
			trigger={
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('add-field')}
					className={className}
					displayType={displayType}
					ref={(node: HTMLButtonElement) => {
						triggerRef.current = node;
					}}
					size="sm"
					symbol="plus"
					title={Liferay.Language.get('add-field')}
					{...triggerProps}
				/>
			}
		/>
	);
}
