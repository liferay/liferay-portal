/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayPopover from '@clayui/popover';
import classNames from 'classnames';
import {
	EVENT_TYPES as CORE_EVENT_TYPES,
	FieldFeedback,
	Layout,
	PagesVisitor,
	useConfig,
	useForm,
	useFormState,
} from 'data-engine-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import './FieldBase.scss';

export function updateFieldNameLocale(editingLanguageId, locale, name) {
	return name.replace(new RegExp(`${editingLanguageId}$`), locale);
}

export function normalizeInputValue(fieldType, value) {
	if (!value) {
		return '';
	}

	if (
		fieldType === 'document_library' ||
		fieldType === 'geolocation' ||
		fieldType === 'grid' ||
		fieldType === 'image' ||
		fieldType === 'select'
	) {
		return !Object.keys(value).length ? '' : JSON.stringify(value);
	}

	return value;
}

const getFieldDetails = ({
	errorMessage,
	hasError,
	label,
	required,
	text,
	tip,
	warningMessage,
}) => {
	const fieldDetails = [];

	if (label) {
		fieldDetails.push(Liferay.Util.escape(label));
	}

	if (tip) {
		fieldDetails.push(Liferay.Util.escape(tip));
	}

	if (text) {
		fieldDetails.push(Liferay.Util.escape(text));
	}

	if (hasError) {
		fieldDetails.push(Liferay.Util.escape(errorMessage));
	}
	else {
		if (warningMessage) {
			fieldDetails.push(Liferay.Util.escape(warningMessage));
		}
		if (required) {
			fieldDetails.push(Liferay.Language.get('required'));
		}
	}

	return fieldDetails.length ? fieldDetails.join('<br>') : false;
};

const HideFieldProperty = () => {
	return (
		<ClayLabel className="ml-1" displayType="secondary">
			{Liferay.Language.get('hidden')}
		</ClayLabel>
	);
};

const LabelProperty = ({hideField, label}) => {
	return hideField ? <span className="text-secondary">{label}</span> : label;
};

const RequiredProperty = () => {
	return (
		<span className="ddm-label-required reference-mark">
			<ClayIcon symbol="asterisk" />
		</span>
	);
};

const FieldInformation = ({popover, tooltip}) => {
	return popover ? (
		<Popover {...popover} />
	) : (
		<span
			className="c-ml-2 text-4 text-secondary"
			data-testid="tooltip"
			tabIndex={0}
			title={tooltip}
		>
			<ClayIcon symbol="question-circle-full" />
		</span>
	);
};

const Popover = ({alignPosition, content, header, image}) => {
	const [isPopoverVisible, setIsPopoverVisible] = useState(false);

	const POPOVER_MAX_WIDTH = 256;

	return (
		<ClayPopover
			alignPosition={alignPosition}
			closeOnClickOutside
			data-testid="clayPopover"
			disableScroll
			header={header}
			onShowChange={setIsPopoverVisible}
			show={isPopoverVisible}
			style={{maxWidth: POPOVER_MAX_WIDTH}}
			trigger={
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('more-information')}
					className="c-ml-2 text-secondary"
					data-testid="tooltip"
					displayType="unstyled"
					monospaced={false}
					size="sm"
					symbol="question-circle-full"
				/>
			}
		>
			<p
				className="mb-4"
				dangerouslySetInnerHTML={{
					__html: content,
				}}
			/>

			{image && (
				<img
					alt={image.alt}
					height={image.height}
					src={image.src}
					width={image.width}
				/>
			)}
		</ClayPopover>
	);
};

const FIELDSET_REGEX = /Fieldset\d+/g;
const FIELDSET_REPEAT_INDEX_REGEX = /\$(\d+)(?:#|\$|$)/g;

export default function FieldBase({
	accessible = true,
	children,
	displayErrors,
	editOnlyInDefaultLanguage,
	errorMessage,
	fieldName,
	fieldReference,
	hideField,
	hideEditedFlag,
	id,
	instanceId,
	itemPath,
	label,
	localizedValue = {},
	localizedValueEdited,
	name,
	nestedFields,
	onClick,
	overMaximumRepetitionsLimit,
	popover,
	readOnly,
	repeatable,
	required,
	showLabel = true,
	style,
	text,
	tip,
	tooltip,
	type,
	valid,
	visible,
	warningMessage,
}) {
	const {disableFieldRepetition} = useConfig();
	const {editingLanguageId, pages} = useFormState();
	const [disabledRepeatableButton, setDisabledRepeatableButton] =
		useState(false);
	const dispatch = useForm();

	const hasError = displayErrors && errorMessage && !valid;

	const fieldDetails = getFieldDetails({
		errorMessage,
		hasError,
		label,
		required,
		text,
		tip,
		warningMessage,
	});

	const fieldDetailsId = `${id ?? name}_fieldDetails`;
	const fieldLabelId = `${id ?? name}_fieldLabel`;

	const hiddenTranslations = useMemo(() => {
		if (!localizedValue) {
			return;
		}

		return Object.entries(localizedValue).map(([locale, value]) => {
			return (
				<input
					data-field-name={`${fieldName}${instanceId}`}
					data-languageid={locale}
					data-translated={
						!!localizedValueEdited?.[editingLanguageId]
					}
					key={locale}
					type="hidden"
					value={normalizeInputValue(type, value)}
					{...(locale !== editingLanguageId && {
						name: updateFieldNameLocale(
							editingLanguageId,
							locale,
							name
						),
					})}
				/>
			);
		});
	}, [
		localizedValue,
		localizedValueEdited,
		editingLanguageId,
		fieldName,
		instanceId,
		name,
		type,
	]);

	const renderLabel =
		(label && showLabel) || hideField || repeatable || required || tooltip;
	const showDisabledFieldIcon =
		editOnlyInDefaultLanguage && showLabel && readOnly;
	const showGroup =
		type === 'checkbox_multiple' ||
		type === 'grid' ||
		type === 'paragraph' ||
		type === 'radio';
	const popoverOrTooltip = !!popover || !!tooltip;
	const showFor =
		type === 'date' ||
		type === 'date_time' ||
		type === 'document_library' ||
		type === 'image' ||
		type === 'numeric' ||
		type === 'rich_text' ||
		type === 'search_location' ||
		type === 'text';
	const readFieldDetails = !showFor;
	const hasFieldDetails =
		accessible && fieldDetails && readFieldDetails && type !== 'select';

	const accessiblePropsGroup = {
		...(!renderLabel && {'aria-labelledby': fieldDetailsId}),
		...(type === 'fieldset' && {role: 'group'}),
	};

	const accessiblePropsFields = {
		...(hasFieldDetails && {'aria-labelledby': fieldDetailsId}),
		...(showFor && {htmlFor: id ?? name}),
		...readFieldDetails,
	};

	const defaultRows = nestedFields?.map((field) => ({
		columns: [{fields: [field], size: 12}],
	}));

	const checkRepetitions = useMemo(() => {
		if (repeatable && name) {
			const currentFieldFieldsets = name.match(FIELDSET_REGEX);
			const currentFieldsetRepeatIndexes = name.match(
				FIELDSET_REPEAT_INDEX_REGEX
			);

			if (currentFieldsetRepeatIndexes) {
				currentFieldsetRepeatIndexes.pop();
			}

			const visitor = new PagesVisitor(pages);

			const repeatableFields = [];

			visitor.visitFields((field) => {
				const fieldName = field.name ?? field.fieldName;

				const fieldFieldsets = fieldName.match(FIELDSET_REGEX);
				const fieldsetRepeatIndexes = fieldName.match(
					FIELDSET_REPEAT_INDEX_REGEX
				);

				if (fieldsetRepeatIndexes) {
					fieldsetRepeatIndexes.pop();
				}

				const isSameFieldset =
					currentFieldFieldsets &&
					fieldFieldsets &&
					currentFieldsetRepeatIndexes &&
					fieldsetRepeatIndexes &&
					currentFieldFieldsets.every(
						(fieldFieldset, index) =>
							fieldFieldset === fieldFieldsets[index]
					) &&
					currentFieldsetRepeatIndexes.every(
						(fieldFieldset, index) =>
							fieldFieldset === fieldsetRepeatIndexes[index]
					);

				if (fieldReference === field.fieldReference && isSameFieldset) {
					repeatableFields.push(field);
				}

				if (
					!currentFieldFieldsets &&
					fieldReference === field.fieldReference
				) {
					repeatableFields.push(field);
				}
			});

			return repeatableFields.length;
		}
	}, [fieldReference, name, pages, repeatable]);

	const disableRepeatableButton = () => {
		setDisabledRepeatableButton(true);

		setTimeout(() => {
			setDisabledRepeatableButton(false);
		}, 1000);
	};

	const translationFilterChange = useCallback(
		(event) => {
			const pagesVisitor = new PagesVisitor(pages);
			switch (event.option) {
				case 'translated':
					dispatch({
						payload: pagesVisitor.mapFields(
							(field) => {
								if (!field.localizable) {
									return {
										...field,
										disabled: true,
										hidden: true,
										visible: false,
									};
								}
								if (
									field.localizedValueEdited?.[
										editingLanguageId
									]
								) {
									return {
										...field,
										disabled: false,
										hidden: false,
										visible: true,
									};
								}
								else {
									return {
										...field,
										disabled: true,
										hidden: true,
										visible: false,
									};
								}
							},
							false,
							true
						),
						type: CORE_EVENT_TYPES.PAGE.UPDATE,
					});

					break;
				case 'untranslated':
					dispatch({
						payload: pagesVisitor.mapFields(
							(field) => {
								if (!field.localizable) {
									return {
										...field,
										disabled: true,
										hidden: true,
										visible: false,
									};
								}
								if (
									field.localizedValueEdited?.[
										editingLanguageId
									]
								) {
									return {
										...field,
										disabled: true,
										hidden: true,
										visible: false,
									};
								}
								else {
									return {
										...field,
										disabled: false,
										hidden: false,
										visible: true,
									};
								}
							},
							false,
							true
						),
						type: CORE_EVENT_TYPES.PAGE.UPDATE,
					});
					break;
				default:
					dispatch({
						payload: pagesVisitor.mapFields(
							(field) => {
								return {
									...field,
									disabled: false,
									hidden: false,
									visible: true,
								};
							},
							false,
							true
						),
						type: CORE_EVENT_TYPES.PAGE.UPDATE,
					});
					break;
			}
		},
		[dispatch, editingLanguageId, pages]
	);

	useEffect(() => {
		if (disableFieldRepetition) {
			setDisabledRepeatableButton(true);
		}
		else {
			Liferay.on('disableRepeatableButton', disableRepeatableButton);

			return () => {
				Liferay.detach(
					'disableRepeatableButton',
					disableRepeatableButton
				);
			};
		}
	}, [disableFieldRepetition]);

	const markAsTranslated = useCallback(() => {
		const pagesVisitor = new PagesVisitor(pages);

		dispatch({
			payload: pagesVisitor.mapFields(
				(field) => {
					if (!field.localizedValue) {
						return;
					}

					return {
						...field,
						localizedValue: {
							...field.localizedValue,
							[editingLanguageId]: field.value,
						},
						localizedValueEdited: {
							...field.localizedValueEdited,
							[editingLanguageId]: true,
						},
					};
				},
				false,
				true
			),
			type: CORE_EVENT_TYPES.PAGE.UPDATE,
		});
	}, [dispatch, editingLanguageId, pages]);

	const resetTranslations = useCallback(
		({defaultLanguageId}) => {
			const pagesVisitor = new PagesVisitor(pages);

			dispatch({
				payload: pagesVisitor.mapFields(
					(field) => {
						const defaultValue =
							field.localizedValue[defaultLanguageId];
						if (field.localizedValue?.[editingLanguageId]) {
							delete field.localizedValue[editingLanguageId];
						}
						if (field.localizedValueEdited?.[editingLanguageId]) {
							delete field.localizedValueEdited[
								editingLanguageId
							];
						}

						return {
							...field,
							value: defaultValue,
						};
					},
					false,
					true
				),
				type: CORE_EVENT_TYPES.PAGE.UPDATE,
			});
		},
		[dispatch, editingLanguageId, pages]
	);

	useEffect(() => {
		Liferay.on('inputLocalized:resetTranslations', resetTranslations);
		Liferay.on('inputLocalized:markAsTranslated', markAsTranslated);
		Liferay.on(
			'inputLocalized:translationFilterChange',
			translationFilterChange
		);

		return () => {
			Liferay.detach(
				'inputLocalized:resetTranslations',
				resetTranslations
			);
			Liferay.detach('inputLocalized:markAsTranslated', markAsTranslated);
			Liferay.on('translationFilterChange', translationFilterChange);
		};
	}, [resetTranslations, markAsTranslated, translationFilterChange]);

	return (
		<ClayForm.Group
			{...accessiblePropsGroup}
			className={classNames({
				'has-error': hasError,
				'has-warning': warningMessage && !hasError,
				'hide': !visible,
			})}
			data-field-name={name}
			data-field-reference={fieldReference}
			onClick={onClick}
			style={style}
		>
			{repeatable && (
				<div className="lfr-ddm-form-field-repeatable-toolbar">
					{checkRepetitions > 1 && (
						<ClayButton
							aria-label={sub(
								Liferay.Language.get('remove-duplicate-field'),
								label ? label : type
							)}
							className={classNames(
								'ddm-form-field-repeatable-delete-button p-0',
								{
									'ddm-form-field-repeatable-button-disabled':
										disabledRepeatableButton,
								}
							)}
							disabled={readOnly || disabledRepeatableButton}
							onClick={() => {
								dispatch({
									payload: name,
									type: CORE_EVENT_TYPES.FIELD.REMOVED,
								});

								Liferay.fire('journal:storeState', {
									fieldName: Liferay.Language.get(
										'remove-repeatable-field'
									),
								});
							}}
							small
							title={Liferay.Language.get('remove')}
							type="button"
						>
							<ClayIcon symbol="hr" />
						</ClayButton>
					)}

					<ClayButton
						aria-label={sub(
							Liferay.Language.get('add-duplicate-field'),
							label ? label : type
						)}
						className={classNames(
							'ddm-form-field-repeatable-add-button p-0',
							{
								'ddm-form-field-repeatable-button-disabled':
									disabledRepeatableButton,
								'hide': overMaximumRepetitionsLimit,
							}
						)}
						disabled={readOnly || disabledRepeatableButton}
						onClick={() => {
							dispatch({
								payload: name,
								type: CORE_EVENT_TYPES.FIELD.REPEATED,
							});

							Liferay.fire('journal:storeState', {
								fieldName: Liferay.Language.get(
									'add-repeatable-field'
								),
							});
						}}
						small
						title={Liferay.Language.get('duplicate')}
						type="button"
					>
						<ClayIcon symbol="plus" />
					</ClayButton>
				</div>
			)}

			{renderLabel && (
				<>
					{showGroup ? (
						<div aria-labelledby={fieldLabelId} role="group">
							<label
								{...accessiblePropsFields}
								className={classNames('lfr-ddm-legend', {
									'text-muted': showDisabledFieldIcon,
								})}
								id={fieldLabelId}
							>
								{showLabel && label}

								{required && <RequiredProperty />}
							</label>

							{popoverOrTooltip && (
								<FieldInformation
									popover={popover}
									tooltip={tooltip}
								/>
							)}

							{showDisabledFieldIcon && (
								<FieldInformation
									tooltip={Liferay.Language.get(
										'this-field-cannot-be-localized'
									)}
								/>
							)}

							{children}
						</div>
					) : (
						<>
							<label
								{...accessiblePropsFields}
								className={classNames({
									'ddm-empty': !showLabel && !required,
									'ddm-label': showLabel || required,
									'ddm-repeatable': repeatable,
									'text-muted': showDisabledFieldIcon,
								})}
								{...(type === 'select' && {id: id ?? name})}
							>
								{showLabel && label && (
									<LabelProperty
										hideField={hideField}
										label={label}
									/>
								)}

								{required && <RequiredProperty />}

								{hideField && <HideFieldProperty />}
							</label>

							{showLabel && popoverOrTooltip && (
								<FieldInformation
									popover={popover}
									tooltip={tooltip}
								/>
							)}

							{showDisabledFieldIcon && (
								<FieldInformation
									tooltip={Liferay.Language.get(
										'this-field-cannot-be-localized'
									)}
								/>
							)}

							{children}

							{!showLabel && popoverOrTooltip && (
								<FieldInformation
									popover={popover}
									tooltip={tooltip}
								/>
							)}
						</>
					)}
				</>
			)}

			{!renderLabel && children}

			{hiddenTranslations}

			{!hideEditedFlag && (
				<input
					name={`${name}_edited`}
					type="hidden"
					value={localizedValue[editingLanguageId] !== undefined}
				/>
			)}

			<FieldFeedback
				aria-hidden={readFieldDetails}
				errorMessage={hasError ? errorMessage : undefined}
				helpMessage={typeof tip === 'string' ? tip : undefined}
				name={id ?? name}
				warningMessage={warningMessage}
			/>

			{hasFieldDetails && (
				<span
					className="sr-only"
					dangerouslySetInnerHTML={{
						__html: fieldDetails,
					}}
					id={fieldDetailsId}
				/>
			)}

			{defaultRows && <Layout itemPath={itemPath} rows={defaultRows} />}
		</ClayForm.Group>
	);
}
