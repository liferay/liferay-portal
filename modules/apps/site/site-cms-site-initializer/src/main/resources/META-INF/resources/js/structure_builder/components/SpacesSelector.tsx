/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import classNames from 'classnames';
import {FieldFeedback, openToast, useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import SpaceSticker from '../../common/components/SpaceSticker';
import SpaceService from '../../common/services/SpaceService';
import {Space} from '../../common/types/Space';
import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectErrors from '../selectors/selectErrors';
import {ReferencedStructure, Structure} from '../types/Structure';

export default function SpacesSelector({
	disabled,
	structure,
}: {
	disabled?: boolean;
	structure: Structure | ReferencedStructure;
}) {
	const [removing, setRemoving] = useState(false);

	const {spaces: structureSpaces, uuid: structureUuid} = structure;

	const dispatch = useStateDispatch();

	const errors = useSelector(selectErrors(structureUuid));

	const id = useId();

	const {data: spaces} = useCache('spaces');

	const selectedSpaces = getSelection(structureSpaces, spaces);

	const isDisabled = disabled || structureSpaces === 'all';

	const error = errors.get('spaces');

	const handleRemove = async (nextSpaces: Space[]) => {
		setRemoving(true);

		const space = findRemovedSpace({
			allSpaces: spaces,
			nextSpaces,
			structureSpaces,
		})!;

		const {data, error} = await SpaceService.getSpaceContents({
			path: (structure as Structure).path,
			siteId: space?.siteId,
		});

		if (!data || error) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}

		const contents = data.totalCount;

		if (contents > 0) {
			openToast({
				message: sub(
					Liferay.Language.get(
						'the-space-x-cannot-be-removed-because-it-has-content-created-from-this-structure'
					),
					Liferay.Util.escapeHTML(space.name)
				),
				type: 'danger',
			});

			setRemoving(false);

			return;
		}

		dispatch({
			spaces: nextSpaces.map((space) => space.externalReferenceCode),
			type: 'update-structure',
		});

		setRemoving(false);
	};

	return (
		<div className="mt-5">
			<span className="border-bottom d-block mb-3 panel-title text-secondary">
				{Liferay.Language.get('space-availability')}
			</span>

			<p>
				{Liferay.Language.get(
					'select-the-spaces-where-this-content-structure-will-be-available-for-use'
				)}
			</p>

			<ClayForm.Group className={classNames({'has-error': error})}>
				<label htmlFor={id}>
					{Liferay.Language.get('spaces')}

					<ClayIcon
						className="ml-1 reference-mark"
						focusable="false"
						role="presentation"
						symbol="asterisk"
					/>
				</label>

				{isDisabled ? (
					<ClayMultiSelect
						disabled
						id={id}
						items={selectedSpaces.map((space) => ({
							label: space.name,
							value: space.id,
						}))}
						value={
							structureSpaces === 'all'
								? Liferay.Language.get('all-spaces')
								: ''
						}
					/>
				) : (
					<ItemSelector<Space>
						apiURL={`${location.origin}/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'`}
						as={ClayInput}
						disabled={removing}
						id={id}
						items={selectedSpaces}
						loadingState={removing ? 1 : undefined}
						locator={{
							id: 'id',
							label: 'name',
							value: 'externalReferenceCode',
						}}
						multiSelect
						onBlur={() => {
							if (!structureSpaces.length) {
								dispatch({
									error: 'empty',
									property: 'spaces',
									type: 'add-error',
									uuid: structureUuid,
								});
							}
						}}
						onItemsChange={async (items: Array<Space>) => {
							if (items.length < structureSpaces.length) {
								await handleRemove(items);

								return;
							}

							dispatch({
								spaces: items.map(
									(item) => item.externalReferenceCode
								),
								type: 'update-structure',
							});
						}}
					>
						{(item: Space) => (
							<ItemSelector.Item
								key={item.externalReferenceCode}
								textValue={item.name}
							>
								<SpaceSticker
									displayType={item.settings?.logoColor}
									name={item.name}
									size="sm"
								/>
							</ItemSelector.Item>
						)}
					</ItemSelector>
				)}

				{error ? <FieldFeedback errorMessage={error} /> : null}
			</ClayForm.Group>

			<ClayForm.Group>
				<ClayCheckbox
					checked={structureSpaces === 'all'}
					disabled={disabled}
					label={Liferay.Language.get(
						'make-this-content-structure-available-in-all-spaces'
					)}
					onChange={(event) => {
						dispatch({
							spaces: event.target.checked ? 'all' : [],
							type: 'update-structure',
						});
					}}
				/>
			</ClayForm.Group>
		</div>
	);
}

function getSelection(
	structureSpaces: Structure['spaces'],
	spaces: Space[]
): Space[] {
	if (structureSpaces === 'all') {
		return [];
	}

	return spaces.filter(({externalReferenceCode}) =>
		structureSpaces.includes(externalReferenceCode)
	);
}

function findRemovedSpace({
	allSpaces,
	nextSpaces,
	structureSpaces,
}: {
	allSpaces: Space[];
	nextSpaces: Space[];
	structureSpaces: Structure['spaces'];
}): Space | undefined {
	const spaces = structureSpaces as Array<Space['externalReferenceCode']>;

	const erc = spaces.find(
		(erc) =>
			!nextSpaces.some((space) => space.externalReferenceCode === erc)
	);

	return allSpaces.find(
		({externalReferenceCode}) => erc === externalReferenceCode
	);
}
