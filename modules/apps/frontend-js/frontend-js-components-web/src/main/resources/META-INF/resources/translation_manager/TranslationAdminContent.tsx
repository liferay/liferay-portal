/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayModal from '@clayui/modal';
import ClayTable from '@clayui/table';
import React, {useMemo, useState} from 'react';

export interface Locale {
	displayName: string;
	id: Liferay.Language.Locale;
	label: Liferay.Language.Locale;
	symbol: string;
}

export interface Translations {
	activeLanguageIds?: Liferay.Language.Locale[];
	ariaLabels?: {
		default?: string;
		manageTranslations?: string;
		managementToolbar?: string;
		notTranslated?: string;
		translated?: string;
	};
	availableLocales: Locale[];
	defaultLanguageId: Liferay.Language.Locale;
	translations?: Record<Liferay.Language.Locale, string> | null;
}

interface IProps extends Translations {
	onAddLocale?: (localeId: Liferay.Language.Locale) => void;
	onCancel?: React.MouseEventHandler<HTMLButtonElement>;
	onDone?: React.MouseEventHandler<HTMLButtonElement>;
	onRemoveLocale?: (localeId: Liferay.Language.Locale) => void;
}

const noop = () => {};

export default function TranslationAdminContent({
	ariaLabels = {
		default: Liferay.Language.get('default'),
		manageTranslations: Liferay.Language.get('manage-translations'),
		managementToolbar: Liferay.Language.get('management-toolbar'),
		notTranslated: Liferay.Language.get('not-translated'),
		translated: Liferay.Language.get('translated'),
	},
	activeLanguageIds: initialActiveLanguageIds = [],
	availableLocales: initialAvailableLocales = [],
	defaultLanguageId,
	onAddLocale = noop,
	onCancel = noop,
	onDone = noop,
	onRemoveLocale = noop,
	translations = null,
}: IProps) {
	const [creationMenuActive, setCreationMenuActive] = useState(false);
	const [searchValue, setSearchValue] = useState('');

	const activeLanguageIds = useMemo(() => {
		return initialAvailableLocales.filter((availableLocale) => {
			const regExp = new RegExp(
				searchValue.replace(/[-[\]/{}()*+?.\\^$|]/g, '\\$&'),
				'i'
			);

			return (
				initialActiveLanguageIds.includes(availableLocale.id) &&
				(availableLocale.label.match(regExp) ||
					availableLocale.displayName.match(regExp))
			);
		});
	}, [initialAvailableLocales, initialActiveLanguageIds, searchValue]);

	const availableLocales = useMemo(() => {
		return initialAvailableLocales.filter(
			(availableLocale) =>
				!initialActiveLanguageIds.includes(availableLocale.id)
		);
	}, [initialAvailableLocales, initialActiveLanguageIds]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('manage-translations')}
			</ClayModal.Header>

			<ClayModal.Header withTitle={false}>
				<ClayInput.Group className="align-items-center">
					<ClayInput.GroupItem>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							insetAfter={true}
							onChange={(event) =>
								setSearchValue(event.target.value)
							}
							placeholder={Liferay.Language.get('search')}
							value={searchValue}
						/>

						<ClayInput.GroupInsetItem after tag="span">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('search')}
								displayType="unstyled"
								onClick={() => {
									setSearchValue('');
								}}
								symbol={searchValue ? 'times' : 'search'}
							/>
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<ClayDropDown
							active={
								creationMenuActive && !!availableLocales.length
							}
							hasLeftSymbols
							menuElementAttrs={{
								className: 'dropdown-menu-width-shrink',
							}}
							onActiveChange={setCreationMenuActive}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('add')}
									className="lfr-portal-tooltip"
									disabled={!availableLocales.length}
									small
									symbol="plus"
									title={Liferay.Language.get('add')}
								/>
							}
						>
							<ClayDropDown.ItemList>
								{availableLocales.map((availableLocale) => {
									return (
										<ClayDropDown.Item
											key={availableLocale.label}
											onClick={() => {
												onAddLocale(availableLocale.id);
												setCreationMenuActive(false);
											}}
											symbolLeft={availableLocale.symbol}
										>
											{availableLocale.label}
										</ClayDropDown.Item>
									);
								})}
							</ClayDropDown.ItemList>
						</ClayDropDown>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayModal.Header>

			<ClayModal.Body className="pb-0 pt-3" scrollable>
				<ClayTable>
					<ClayTable.Head>
						<ClayTable.Row>
							<ClayTable.Cell headingCell>
								{Liferay.Language.get('code')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('language')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('status')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell />
						</ClayTable.Row>
					</ClayTable.Head>

					<ClayTable.Body>
						{activeLanguageIds.map((activeLocale) => {
							const label = activeLocale.label;

							const isDefaultLocale =
								activeLocale.id === defaultLanguageId;

							const localeValue = translations
								? translations[label]
								: null;

							return (
								<ClayTable.Row key={label}>
									<ClayTable.Cell>
										<>
											<ClayIcon
												className="inline-item inline-item-before"
												symbol={activeLocale.symbol}
											/>
											<strong>{label}</strong>
										</>
									</ClayTable.Cell>

									<ClayTable.Cell expanded>
										{activeLocale.displayName}
									</ClayTable.Cell>

									<ClayTable.Cell>
										<ClayLabel
											displayType={
												isDefaultLocale
													? 'info'
													: localeValue
														? 'success'
														: 'warning'
											}
										>
											{isDefaultLocale
												? ariaLabels.default
												: localeValue
													? ariaLabels.translated
													: ariaLabels.notTranslated}
										</ClayLabel>
									</ClayTable.Cell>

									<ClayTable.Cell>
										{!isDefaultLocale && (
											<ClayButtonWithIcon
												aria-label={Liferay.Language.get(
													'delete'
												)}
												className="lfr-portal-tooltip"
												displayType="unstyled"
												monospaced={false}
												onClick={() =>
													onRemoveLocale(
														activeLocale.id
													)
												}
												symbol="trash"
												title={Liferay.Language.get(
													'delete'
												)}
											/>
										)}
									</ClayTable.Cell>
								</ClayTable.Row>
							);
						})}
					</ClayTable.Body>
				</ClayTable>
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onCancel}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" onClick={onDone}>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
