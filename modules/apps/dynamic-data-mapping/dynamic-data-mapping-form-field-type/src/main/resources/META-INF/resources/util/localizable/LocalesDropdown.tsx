/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {
	PagesVisitor,
	useConfig,
	useForm,
	useFormState,
} from 'data-engine-js-components-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import AvailableLocaleLabel from './AvailableLocaleLabel';

export interface AvailableLocale {
	displayName: string;
	icon: string;
	localeId: Liferay.Language.Locale;
}

export interface EditingLocale extends AvailableLocale {
	isDefault: boolean;
	isTranslated: boolean;
}
interface LocalesDropdownProps {
	availableLocales: EditingLocale[];
	editingLocale: EditingLocale;
	fieldName: string;
	onLanguageClicked: (localeId: Liferay.Language.Locale) => void;
}

const LocalesDropdown = ({
	availableLocales,
	editingLocale,
	fieldName,
	onLanguageClicked,
}: LocalesDropdownProps) => {
	const alignElementRef = useRef(null);
	const dispatch = useForm();
	const dropdownMenuRef = useRef(null);
	const {pages} = useFormState();
	const {portletNamespace} = useConfig();

	const [dropdownActive, setDropdownActive] = useState(false);

	const localeChangeHandler = useCallback(
		(event: any) => {
			const localeId = event.item.getAttribute('data-value');
			document.getElementsByName(fieldName + localeId)[0].click();
		},
		[fieldName]
	);
	useEffect(() => {
		Liferay.on('inputLocalized:localeChanged', localeChangeHandler);

		return () =>
			Liferay.detach(
				'inputLocalized:localeChanged',
				localeChangeHandler as () => void
			);
	}, [localeChangeHandler]);

	return (
		<div>
			<ClayButton
				aria-expanded="false"
				aria-haspopup="true"
				className="dropdown-toggle"
				data-testid="triggerButton"
				displayType="secondary"
				monospaced
				onClick={() => setDropdownActive(!dropdownActive)}
				ref={alignElementRef}
			>
				<span className="inline-item">
					<ClayIcon symbol={editingLocale.icon} />
				</span>

				<span className="btn-section" data-testid="triggerText">
					{editingLocale.icon}
				</span>
			</ClayButton>

			<ClayDropDown.Menu
				active={dropdownActive}
				alignElementRef={alignElementRef}
				onActiveChange={setDropdownActive}
				ref={dropdownMenuRef}
			>
				<ClayDropDown.ItemList>
					{availableLocales.map(
						({
							displayName,
							icon,
							isDefault,
							isTranslated,
							localeId,
						}) => (
							<ClayDropDown.Item
								className="custom-dropdown-item-row"
								data-testid={`availableLocalesDropdown${localeId}`}
								key={localeId}

								// @ts-ignore

								name={fieldName + localeId}
								onClick={(event) => {
									onLanguageClicked(localeId);
									setDropdownActive(false);

									if (event.isTrusted) {
										const visitor = new PagesVisitor(pages);

										visitor.mapFields(
											(field) => {
												if (
													(field.localizedObjectField ||
														field.localizable) &&
													fieldName !==
														field.fieldName
												) {
													document
														.getElementsByName(
															field.fieldName +
																localeId
														)[0]
														.click();
												}
											},
											true,
											true
										);

										dispatch({
											payload: {
												editingLanguageId: localeId,
											},
											type: 'language_locales_dropdown_change',
										});

										const friendlyURLInputComponent =
											Liferay.component(
												`${portletNamespace}friendlyURL`
											);

										if (friendlyURLInputComponent) {
											Liferay.fire(
												'inputLocalized:localeChanged',
												{
													item: document.querySelector(
														`[data-languageid="${localeId}"][data-value="${localeId}"]`
													),
												}
											);
										}
									}
								}}
							>
								<ClayLayout.ContentRow containerElement="span">
									<ClayLayout.ContentCol
										containerElement="span"
										expand
									>
										<ClayLayout.ContentSection containerElement="span">
											<span className="inline-item inline-item-before">
												<ClayIcon symbol={icon} />
											</span>

											{displayName}
										</ClayLayout.ContentSection>
									</ClayLayout.ContentCol>

									<ClayLayout.ContentCol containerElement="span">
										<AvailableLocaleLabel
											isDefault={isDefault}
											isSubmitLabel={
												fieldName === 'submitLabel'
											}
											isTranslated={isTranslated}
										/>
									</ClayLayout.ContentCol>
								</ClayLayout.ContentRow>
							</ClayDropDown.Item>
						)
					)}
				</ClayDropDown.ItemList>
			</ClayDropDown.Menu>
		</div>
	);
};

export default LocalesDropdown;
