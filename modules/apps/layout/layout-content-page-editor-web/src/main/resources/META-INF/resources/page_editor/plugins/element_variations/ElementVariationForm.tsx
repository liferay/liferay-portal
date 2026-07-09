/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {LanguagePicker, Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import {Action, ElementVariation} from './elementVariationsReducer';
import {EditableElementOption} from './getEditableElementOptions';

type ElementVariationFormData = Pick<
	ElementVariation,
	'audienceEntryERCs' | 'hide' | 'html' | 'js' | 'name' | 'targetElement'
>;

interface Props {
	audiences: Array<{label: string; value: string}>;
	defaultLanguageId: string;
	dispatch: React.Dispatch<Action>;
	editableElementOptions: EditableElementOption[];
	elementVariation: ElementVariationFormData;
	languageId: string;
	locales: Array<{id: string; label: string; symbol: string}>;
	onCancel: () => void;
	onChange: (properties: Partial<ElementVariationFormData>) => void;
	onLanguageIdChange: (languageId: string) => void;
	onReloadPreview: () => void;
	onSave: () => void;
}

export default function ElementVariationForm({
	audiences,
	defaultLanguageId,
	dispatch,
	editableElementOptions,
	elementVariation,
	languageId,
	locales,
	onCancel,
	onChange,
	onLanguageIdChange,
	onReloadPreview,
	onSave,
}: Props) {
	const audienceId = useId();
	const htmlId = useId();
	const jsId = useId();
	const nameId = useId();
	const targetElementId = useId();

	const targetElementItems = editableElementOptions.map(
		(editableElementOption, index) => ({
			key: String(index),
			label: editableElementOption.label,
			value: editableElementOption.value,
		})
	);

	const selectedTargetElementItem = targetElementItems.find(
		(targetElementItem) =>
			targetElementItem.value === elementVariation.targetElement
	);

	return (
		<>
			<div className="align-items-center border-bottom d-flex flex-shrink-0 px-3 py-3">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('back')}
					borderless
					className="mr-2"
					displayType="secondary"
					onClick={onCancel}
					size="sm"
					symbol="angle-left"
				/>

				<span className="font-weight-bold">
					{Liferay.Language.get('element-variation')}
				</span>

				<div className="ml-auto">
					<LanguagePicker
						defaultLocaleId={defaultLanguageId}
						hideTriggerText
						locales={locales}
						messages={{
							default: Liferay.Language.get('default'),
							option: Liferay.Language.get('x-language-x'),
							translated: Liferay.Language.get('translated'),
							translating:
								Liferay.Language.get('translating-x-x'),
							trigger: Liferay.Language.get(
								'select-a-language.-current-language-x'
							),
							untranslated:
								Liferay.Language.get('not-translated'),
						}}
						onSelectedLocaleChange={(id) =>
							onLanguageIdChange(String(id))
						}
						selectedLocaleId={languageId}
						small
					/>
				</div>
			</div>

			<div className="flex-grow-1 overflow-auto p-3">
				<ClayForm.Group small>
					<label htmlFor={nameId}>
						{Liferay.Language.get('name')}

						<ClayIcon
							className="mr-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<ClayInput
						defaultValue={elementVariation.name}
						id={nameId}
						onBlur={(event) => onChange({name: event.target.value})}
						type="text"
					/>
				</ClayForm.Group>

				<ClayForm.Group small>
					<label htmlFor={targetElementId}>
						{Liferay.Language.get('page-element')}

						<ClayIcon
							className="mr-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<Picker
						aria-label={Liferay.Language.get('page-element')}
						className="form-control-sm"
						id={targetElementId}
						items={targetElementItems}
						onSelectionChange={(selection) => {
							const targetElementItem = targetElementItems.find(
								(currentTargetElementItem) =>
									currentTargetElementItem.key ===
									String(selection)
							);

							onChange({
								targetElement: targetElementItem?.value ?? '',
							});
						}}
						selectedKey={selectedTargetElementItem?.key}
					>
						{(item) => (
							<Option key={item.key} textValue={item.label}>
								<span
									className="d-block"
									onMouseEnter={() =>
										dispatch({
											highlightedTargetElement:
												item.value,
											type: 'SET_HIGHLIGHTED_TARGET_ELEMENT',
										})
									}
									onMouseLeave={() =>
										dispatch({
											highlightedTargetElement: null,
											type: 'SET_HIGHLIGHTED_TARGET_ELEMENT',
										})
									}
								>
									{item.label}
								</span>
							</Option>
						)}
					</Picker>
				</ClayForm.Group>

				{elementVariation.targetElement ? (
					<>
						<ClayForm.Group small>
							<label htmlFor={audienceId}>
								{Liferay.Language.get('audience')}

								<ClayIcon
									className="mr-1 reference-mark"
									symbol="asterisk"
								/>
							</label>

							<ClayMultiSelect
								id={audienceId}
								items={audiences.filter((audience) =>
									elementVariation.audienceEntryERCs.includes(
										audience.value
									)
								)}
								onItemsChange={(
									items: Array<{
										label: string;
										value: string;
									}>
								) => {
									const existingAudiences = items
										.map((item) =>
											audiences.find(
												(audience) =>
													audience.value ===
													item.value
											)
										)
										.filter(
											(
												audience
											): audience is {
												label: string;
												value: string;
											} => Boolean(audience)
										);

									onChange({
										audienceEntryERCs:
											existingAudiences.map(
												(audience) => audience.value
											),
									});
								}}
								sourceItems={audiences}
							/>
						</ClayForm.Group>

						<ClayForm.Group className="my-4">
							<ClayToggle
								label={Liferay.Language.get(
									'hide-page-element'
								)}
								onToggle={(hide) => {
									const properties: Partial<ElementVariationFormData> =
										{
											hide: {
												...elementVariation.hide,
												[languageId]: hide,
											},
										};

									if (hide) {
										properties.html = {
											...elementVariation.html,
											[languageId]: '',
										};
										properties.js = {
											...elementVariation.js,
											[languageId]: '',
										};
									}

									onChange(properties);
								}}
								toggled={Boolean(
									elementVariation.hide[languageId]
								)}
							/>
						</ClayForm.Group>

						{elementVariation.hide[languageId] ? null : (
							<>
								<ClayForm.Group small>
									<label htmlFor={htmlId}>
										{Liferay.Language.get('html')}
									</label>

									<ClayInput
										component="textarea"
										defaultValue={
											elementVariation.html[languageId] ??
											''
										}
										id={htmlId}
										key={languageId}
										onBlur={(event) =>
											onChange({
												html: {
													...elementVariation.html,
													[languageId]:
														event.target.value,
												},
											})
										}
									/>
								</ClayForm.Group>

								<ClayForm.Group small>
									<label htmlFor={jsId}>
										{Liferay.Language.get('javascript')}
									</label>

									<p className="mb-1 text-2 text-secondary">
										{Liferay.Language.get(
											'changes-persist-in-the-preview.-reload-to-update'
										)}
									</p>

									<ClayInput
										component="textarea"
										defaultValue={
											elementVariation.js[languageId] ??
											''
										}
										id={jsId}
										key={languageId}
										onBlur={(event) =>
											onChange({
												js: {
													...elementVariation.js,
													[languageId]:
														event.target.value,
												},
											})
										}
									/>

									<ClayButton
										className="mt-2"
										displayType="secondary"
										onClick={onReloadPreview}
										size="sm"
									>
										<ClayIcon
											className="mr-2"
											symbol="reload"
										/>

										{Liferay.Language.get('reload')}
									</ClayButton>
								</ClayForm.Group>
							</>
						)}
					</>
				) : null}
			</div>

			<div className="border-top d-flex flex-shrink-0 p-3">
				<ClayButton
					borderless
					displayType="secondary"
					onClick={onCancel}
					size="sm"
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>

				<ClayButton
					className="ml-2"
					displayType="primary"
					onClick={onSave}
					size="sm"
				>
					{Liferay.Language.get('save')}
				</ClayButton>
			</div>
		</>
	);
}
