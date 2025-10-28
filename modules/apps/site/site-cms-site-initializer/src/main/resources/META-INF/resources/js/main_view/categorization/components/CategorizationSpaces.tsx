/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {sub} from 'frontend-js-web';
import React, {ChangeEvent, useEffect, useState} from 'react';

import SpaceSticker from '../../../common/components/SpaceSticker';
import SpaceService from '../../../common/services/SpaceService';

type Space = {
	displayType?: string;
	label: string;
	value: any;
};

export default function CategorizationSpaces({
	assetLibraries,
	checkboxText,
	setSelectedSpaces,
	setSpaceChange,
	setSpaceInputError,
	spaceInputError,
}: {
	assetLibraries?: any;
	checkboxText: string;
	setSelectedSpaces: (value: any) => void;
	setSpaceChange?: (value: boolean) => void;
	setSpaceInputError: (value: string) => void;
	spaceInputError: string;
}) {
	const [availableSpaces, setAvailableSpaces] = useState<Space[]>([]);
	const [checkbox, setCheckbox] = useState(true);
	const [selectedItems, setSelectedItems] = useState<Space[]>([]);
	const [initialSelectedSpaces, setInitialSelectedSpaces] = useState<
		number[]
	>([]);

	useEffect(() => {
		SpaceService.getSpaces().then((response) => {
			const spaces = response.map((item) => ({
				displayType: item.settings?.logoColor,
				label: item.name,
				value: item.id,
			}));

			setAvailableSpaces(spaces);

			const initialSpaces = assetLibraries?.map(
				(item: {name: string}) =>
					spaces.find((space) => space.label === item.name)?.value
			);

			setInitialSelectedSpaces(initialSpaces);

			if (
				!assetLibraries ||
				!assetLibraries.length ||
				assetLibraries?.some((item: {id: number}) => item.id === -1)
			) {
				setCheckbox(true);

				setSelectedItems([]);
			}
			else if (initialSpaces) {
				setCheckbox(false);

				setSelectedItems(
					spaces.filter((item) => initialSpaces.includes(item.value))
				);
			}
		});
	}, [assetLibraries]);

	useEffect(() => {
		if (setSpaceChange) {
			if (checkbox) {
				setSpaceChange(false);
			}
			else if (
				initialSelectedSpaces?.some(
					(item: number) =>
						!selectedItems.find((space) => space.value === item)
				)
			) {
				setSpaceChange(true);
			}
			else {
				setSpaceChange(false);
			}
		}

		if (checkbox || selectedItems.length) {
			setSpaceInputError('');
		}
		else {
			setSpaceInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('space')
				)
			);
		}
	}, [
		checkbox,
		initialSelectedSpaces,
		selectedItems,
		setSpaceChange,
		setSpaceInputError,
	]);

	const _handleChangeAllSpaces = (event: ChangeEvent<HTMLInputElement>) => {
		setSelectedItems([]);

		if (!event.target.checked) {
			setSelectedSpaces([]);
		}
		else {
			setSelectedSpaces([-1]);
		}

		setCheckbox((checkbox) => !checkbox);
	};

	const _handleChangeSpaces = (items: Space[]) => {
		setSelectedItems(
			availableSpaces.filter((item) => items.includes(item))
		);

		setSelectedSpaces(items.map((item) => item.value));
	};

	return (
		<div className="categorization-spaces">
			<label htmlFor="multiSelect">
				{Liferay.Language.get('space')}

				<span className="ml-1 reference-mark">
					<ClayIcon symbol="asterisk" />
				</span>
			</label>

			<div className={spaceInputError ? 'has-error' : ''}>
				<ClayMultiSelect
					aria-label={Liferay.Language.get('space-selector')}
					disabled={checkbox}
					id="multiSelect"
					items={selectedItems}
					loadingState={3}
					onItemsChange={(items: Space[]) => {
						_handleChangeSpaces(items);
					}}
					sourceItems={availableSpaces}
					value={checkbox ? Liferay.Language.get('all-spaces') : ''}
				>
					{(item) => (
						<ClayMultiSelect.Item
							key={item.value}
							textValue={item.label}
						>
							<div className="autofit-row autofit-row-center">
								<span className="align-items-center d-flex space-renderer-sticker">
									<SpaceSticker name={item.label} />
								</span>
							</div>
						</ClayMultiSelect.Item>
					)}
				</ClayMultiSelect>

				{spaceInputError && (
					<ClayAlert displayType="danger" variant="feedback">
						<strong>{Liferay.Language.get('error')}: </strong>

						{spaceInputError}
					</ClayAlert>
				)}
			</div>

			<div className="mt-2">
				<ClayCheckbox
					checked={checkbox}
					label={
						checkboxText === 'tag'
							? Liferay.Language.get(
									'make-this-tag-available-in-all-spaces'
								)
							: Liferay.Language.get(
									'make-this-vocabulary-available-in-all-spaces'
								)
					}
					onChange={_handleChangeAllSpaces}
				/>
			</div>
		</div>
	);
}
