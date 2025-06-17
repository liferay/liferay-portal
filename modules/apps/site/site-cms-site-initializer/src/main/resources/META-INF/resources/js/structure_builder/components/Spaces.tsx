/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import React from 'react';

import {Space} from '../../types/Space';
import {useCache} from '../contexts/CacheContext';
import {State, useStateDispatch} from '../contexts/StateContext';
import selectStructureSpaces from '../selectors/selectStructureSpaces';
import selectStructureUuid from '../selectors/selectStructureUuid';
import selectValidationErrors from '../selectors/selectValidationErrors';
import {Structure} from '../types/Structure';

type Item = {
	label: string;
	value: string;
};

export default function Spaces({
	disabled,
	structure,
}: {
	disabled?: boolean;
	structure: Structure;
}) {
	const dispatch = useStateDispatch();
	const structureSpaces = selectStructureSpaces(structure);
	const structureUuid = selectStructureUuid(structure);
	const validationErrors = selectValidationErrors(structureUuid)(structure);

	const id = useId();

	const {data: spaces, status} = useCache('spaces');

	const hasError = validationErrors.has('no-space');

	return (
		<div className="mt-5">
			<span className="border-bottom d-block mb-3 panel-title text-secondary">
				{Liferay.Language.get('space-availability')}
			</span>

			<p>
				{Liferay.Language.get(
					'select-the-spaces-where-this-structure-will-be-available-for-use'
				)}
			</p>

			<ClayForm.Group className={classNames({'has-error': hasError})}>
				<label htmlFor={id}>
					{Liferay.Language.get('spaces')}

					<ClayIcon
						className="ml-1 reference-mark"
						focusable="false"
						role="presentation"
						symbol="asterisk"
					/>
				</label>

				<ClayMultiSelect
					disabled={disabled || structureSpaces === 'all'}
					id={id}
					items={getSelection(structureSpaces, spaces)}
					loadingState={status === 'saving' ? 1 : 0}
					onBlur={() => {
						if (!structureSpaces.length) {
							dispatch({
								error: 'no-space',
								type: 'add-validation-error',
								uuid: structureUuid,
							});
						}
					}}
					onItemsChange={(items: Item[]) => {
						const ercs = items
							.filter((item) =>
								spaces.some(({name}) => name === item.label)
							)
							.map(({value}) => value);

						dispatch({
							spaces: ercs,
							type: 'update-structure',
						});
					}}
					sourceItems={spaces.map(toItem)}
					value={
						structureSpaces === 'all'
							? Liferay.Language.get('all-spaces')
							: ''
					}
				/>

				{hasError ? (
					<FieldFeedback
						errorMessage={Liferay.Language.get(
							'spaces-must-be-selected'
						)}
					/>
				) : null}
			</ClayForm.Group>

			<ClayForm.Group>
				<ClayCheckbox
					checked={structureSpaces === 'all'}
					disabled={disabled}
					label={Liferay.Language.get(
						'make-this-structure-available-in-all-spaces'
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

function getSelection(structureSpaces: State['spaces'], spaces: Space[]) {
	if (structureSpaces === 'all') {
		return [];
	}

	return spaces
		.filter(({externalReferenceCode}) =>
			structureSpaces.includes(externalReferenceCode)
		)
		.map(toItem);
}

function toItem(space: Space): Item {
	return {
		label: space.name,
		value: space.externalReferenceCode,
	};
}
