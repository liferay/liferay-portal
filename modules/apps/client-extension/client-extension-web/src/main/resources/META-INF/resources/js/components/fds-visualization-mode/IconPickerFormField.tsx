/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

interface IProps {
	disabled?: boolean;
	icon?: string;
	portletNamespace: string;
	spritemap: string;
}

function IconSelectionModalBody({
	closeModal,
	iconSymbols,
	onSelect,
	spritemap,
}: {
	closeModal: () => void;
	iconSymbols: string[];
	onSelect: (iconSymbol: string) => void;
	spritemap: string;
}) {
	const [query, setQuery] = useState('');

	const filteredIconSymbols = query
		? iconSymbols.filter((iconSymbol) =>
				iconSymbol.toLowerCase().includes(query.toLowerCase())
			)
		: iconSymbols;

	return (
		<>
			<ClayInput
				aria-label={Liferay.Language.get('search')}
				onChange={({target: {value}}) => setQuery(value)}
				placeholder={Liferay.Language.get('search')}
				type="search"
				value={query}
			/>

			<ClayLayout.SheetSection>
				<ul className="list-unstyled mt-4 row">
					{filteredIconSymbols.map((iconSymbol) => (
						<li className="col-md-4" key={iconSymbol}>
							<ClayButton
								borderless
								displayType="secondary"
								onClick={() => {
									onSelect(iconSymbol);

									closeModal();
								}}
								size="sm"
							>
								<ClayIcon
									className="mr-2"
									spritemap={spritemap}
									symbol={iconSymbol}
								/>

								{iconSymbol}
							</ClayButton>
						</li>
					))}
				</ul>
			</ClayLayout.SheetSection>
		</>
	);
}

export default function IconPickerFormField({
	disabled,
	icon: initialIcon = '',
	portletNamespace,
	spritemap,
}: IProps) {
	const [icon, setIcon] = useState(initialIcon);
	const [iconSymbols, setIconSymbols] = useState<string[]>([]);

	useEffect(() => {
		const getIconSymbols = async () => {
			const response = await fetch(spritemap);

			const responseText = await response.text();

			if (!responseText.length) {
				return;
			}

			const spritemapDocument = new DOMParser().parseFromString(
				responseText,
				'text/xml'
			);

			setIconSymbols(
				Array.from(spritemapDocument.querySelectorAll('symbol')).map(
					(element) => element.id
				)
			);
		};

		getIconSymbols();
	}, [spritemap]);

	const iconFormElementId = `${portletNamespace}icon`;

	return (
		<>
			<label htmlFor={iconFormElementId}>
				{Liferay.Language.get('icon')}

				<span className="reference-mark text-warning">
					<ClayIcon spritemap={spritemap} symbol="asterisk" />
				</span>

				<span className="hide-accessible sr-only">
					{Liferay.Language.get('required')}
				</span>
			</label>

			<input
				disabled={disabled}
				name={`${portletNamespace}icon`}
				type="hidden"
				value={icon}
			/>

			<ClayInput.Group>
				<ClayInput.GroupItem prepend shrink>
					<ClayInput.GroupText>
						<ClayIcon spritemap={spritemap} symbol={icon} />
					</ClayInput.GroupText>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append>
					<ClayInput
						placeholder={Liferay.Language.get('no-icon-selected')}
						readOnly
						type="text"
						value={icon}
					/>
				</ClayInput.GroupItem>

				<ClayButtonWithIcon
					aria-label={
						icon
							? Liferay.Language.get('change-icon')
							: Liferay.Language.get('add-icon')
					}
					className="ml-2"
					disabled={disabled}
					displayType="secondary"
					id={iconFormElementId}
					onClick={() =>
						openModal({
							bodyComponent: ({
								closeModal,
							}: {
								closeModal: () => void;
							}) => (
								<IconSelectionModalBody
									closeModal={closeModal}
									iconSymbols={iconSymbols}
									onSelect={setIcon}
									spritemap={spritemap}
								/>
							),
							size: 'lg',
							title: Liferay.Language.get('select-an-icon'),
						})
					}
					symbol={icon ? 'change' : 'plus'}
				/>

				{icon && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('remove-icon')}
						className="ml-2"
						disabled={disabled}
						displayType="secondary"
						onClick={() => setIcon('')}
						symbol="trash"
					/>
				)}
			</ClayInput.Group>

			<div className="form-text">
				{Liferay.Language.get(
					'select-the-icon-that-represents-this-frontend-data-set-visualization-mode'
				)}
			</div>
		</>
	);
}
