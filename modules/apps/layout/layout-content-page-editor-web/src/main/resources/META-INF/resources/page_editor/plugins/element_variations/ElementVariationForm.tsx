/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {LanguagePicker, Option, Picker} from '@clayui/core';
import ClayForm, {ClayCheckbox, ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import CodeEditorField from './CodeEditorField';
import {Action, ElementVariation} from './elementVariationsReducer';
import {EditableElementOption} from './getEditableElementOptions';

type ElementVariationFormData = Pick<
	ElementVariation,
	| 'active'
	| 'audienceEntryERCs'
	| 'hide'
	| 'html'
	| 'js'
	| 'name'
	| 'targetElement'
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

	const translating = languageId !== defaultLanguageId;

	const notLocalizableHint = translating ? (
		<span className="element-variations__not-localizable-label font-weight-lighter ml-1">
			({Liferay.Language.get('not-localizable')})
		</span>
	) : null;

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

						{notLocalizableHint}
					</label>

					<ClayInput
						defaultValue={elementVariation.name}
						id={nameId}
						onBlur={(event) => onChange({name: event.target.value})}
						readOnly={translating}
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

						{notLocalizableHint}
					</label>

					<Picker
						aria-label={Liferay.Language.get('page-element')}
						className="form-control-sm"
						disabled={translating}
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

								{notLocalizableHint}
							</label>

							<ClayMultiSelect
								disabled={translating}
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

						<ClayForm.Group className="align-items-center d-flex my-4">
							<ClayToggle
								disabled={translating}
								label={Liferay.Language.get(
									'hide-page-element'
								)}
								onToggle={(hide) => {
									const properties: Partial<ElementVariationFormData> =
										{hide};

									if (hide) {
										properties.html = {};
										properties.js = {};
									}

									onChange(properties);
								}}
								toggled={elementVariation.hide}
							/>

							{translating ? (
								<span className="element-variations__not-localizable-label font-weight-lighter mb-1 ml-2 text-2">
									({Liferay.Language.get('not-localizable')})
								</span>
							) : null}
						</ClayForm.Group>

						{elementVariation.hide ? null : (
							<>
								<CodeEditorField
									defaultLanguageValue={
										translating
											? elementVariation.html[
													defaultLanguageId
												] ?? ''
											: undefined
									}
									initialValue={
										elementVariation.html[languageId] ?? ''
									}
									key={`html-${languageId}`}
									label={Liferay.Language.get('html')}
									mode="text/html"
									onChange={(value) =>
										onChange({
											html: {
												...elementVariation.html,
												[languageId]: value,
											},
										})
									}
								/>

								<CodeEditorField
									defaultLanguageValue={
										translating
											? elementVariation.js[
													defaultLanguageId
												] ?? ''
											: undefined
									}
									description={Liferay.Language.get(
										'changes-persist-in-the-preview.-reload-to-update'
									)}
									initialValue={
										elementVariation.js[languageId] ?? ''
									}
									key={`js-${languageId}`}
									label={Liferay.Language.get('javascript')}
									mode="text/javascript"
									onChange={(value) =>
										onChange({
											js: {
												...elementVariation.js,
												[languageId]: value,
											},
										})
									}
								/>
							</>
						)}

						<div className="mb-4">
							<ClayButton
								displayType="secondary"
								onClick={onReloadPreview}
								size="xs"
							>
								<ClayIcon className="mr-2" symbol="reload" />

								{Liferay.Language.get('reload')}
							</ClayButton>
						</div>

						<ClayForm.Group className="my-4" small>
							<ClayCheckbox
								checked={!elementVariation.active}
								disabled={translating}
								label={Liferay.Language.get(
									'disable-element-variation'
								)}
								onChange={(event) =>
									onChange({active: !event.target.checked})
								}
							/>
						</ClayForm.Group>
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
