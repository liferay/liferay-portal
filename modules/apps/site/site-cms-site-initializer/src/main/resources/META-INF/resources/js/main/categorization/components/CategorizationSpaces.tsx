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

import SpaceService from '../../../services/SpaceService';
import SpaceSticker, {LogoColor} from '../../components/SpaceSticker';

type Space = {
	displayType?: string;
	label: string;
	value: any;
};

const ALL_SPACES: Space[] = [
	{
		label: 'All Spaces',
		value: -1,
	},
];

export default function CategorizationSpaces({
	assetLibraries,
	checkboxText,
	selectedSpaces,
	setSelectedSpaces,
	setSpaceChange,
	setSpaceInputError,
	spaceInputError,
}: {
	assetLibraries?: any;
	checkboxText: string;
	selectedSpaces: number[];
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

				setSelectedItems(ALL_SPACES);
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
			if (selectedItems?.find((space) => space.value === -1)) {
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

		if (selectedItems.length) {
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
		initialSelectedSpaces,
		selectedItems,
		setSpaceChange,
		setSpaceInputError,
	]);

	const _handleChangeAllSpaces = (event: ChangeEvent<HTMLInputElement>) => {
		if (!event.target.checked) {
			setSelectedItems([]);
			setSelectedSpaces([]);
		}
		else {
			setSelectedItems(ALL_SPACES);
			setSelectedSpaces([-1]);
		}

		setCheckbox((checkbox) => !checkbox);
	};

	const _handleChangeSpaces = (items: Space[]) => {
		setSelectedItems(
			availableSpaces.filter((item) => items.includes(item))
		);

		console.log(items, '<<<<<<<<<<<');
		setSelectedSpaces(items.map((item) => item.value));
	};

	const isChecked = (itemValue: number) => {
		return selectedSpaces.includes(itemValue);
	};

	const _handleCheckboxChange = (itemValue: any) => {
		setSelectedSpaces((prevSelectedSpaces: number[]) => {
			if (isChecked(itemValue)) {
				return prevSelectedSpaces.filter((id) => id !== itemValue);
			}
			else {
				return [...prevSelectedSpaces, itemValue];
			}
		});
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
				>
					{(item) => (
						<ClayMultiSelect.Item
							key={item.value}
							textValue={item.label}
						>
							<div className="autofit-row autofit-row-center">
								<div className="autofit-col">
									<ClayCheckbox
										aria-label={item.label}
										checked={isChecked(item.value)}
										onChange={() => {
											_handleCheckboxChange(item.value);
										}}
									/>
								</div>

								<span className="align-items-center d-flex space-renderer-sticker">
									<SpaceSticker
										displayType={
											item.displayType as LogoColor
										}
										name={item.label}
										size="sm"
									/>
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
