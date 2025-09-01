/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {FormikHelpers, FormikTouched, useFormik} from 'formik';
import {openToast, useId} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React from 'react';

import {FieldText} from '../../common/components/forms';
import {
	Errors,
	invalidCharacters,
	maxLength,
	nonNumeric,
	notNull,
	required,
	validNumber,
	validate,
} from '../../common/components/forms/validations';
import SpaceService from '../../common/services/SpaceService';
import {LogoColor, MimeTypeLimit, Space} from '../../common/types/Space';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import getRandomId from '../../structure_builder/utils/getRandomId';
import SpaceBaseFields from './SpaceBaseFields';
import SpacePanel from './SpacePanel';

type Touched = FormikTouched<Record<string, any>>;

const getInitialMimeTypeLimit = () =>
	({
		id: getRandomId(),
		maximumSize: '',
		mimeType: '',
	}) as MimeTypeLimit;

export default function SpaceGeneralSettings({
	backURL,
	groupId,
	setSpace,
	space,
}: {
	backURL?: string;
	groupId: string;
	setSpace?: React.Dispatch<React.SetStateAction<any>>;
	space: Space;
}) {
	const id = useId();
	const mimeTypeLimits = space.settings?.mimeTypeLimits;
	const mimeTypeLimitsWithIds = mimeTypeLimits?.length
		? mimeTypeLimits.map((limit) => ({
				...limit,
				id: limit.id ?? getRandomId(),
			}))
		: [getInitialMimeTypeLimit()];

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		setErrors,
		setFieldValue,
		setTouched,
		submitForm,
		touched,
		values,
	} = useFormik({
		initialValues: {
			description: space.description,
			erc: space.externalReferenceCode,
			logoColor: space.settings?.logoColor as LogoColor,
			mimeTypeLimits: mimeTypeLimitsWithIds,
			name: space.name,
			sharingEnabled: space.settings?.sharingEnabled ?? false,
			trashEnabled: space.settings?.trashEnabled ?? true,
			trashEntriesMaxAge: String(
				space.settings?.trashEntriesMaxAge ?? ''
			),
		},
		onSubmit: async (values) => {
			const {
				description,
				erc,
				logoColor = 'outline-0',
				mimeTypeLimits,
				name,
				sharingEnabled,
				trashEnabled,
				trashEntriesMaxAge,
			} = values;

			const {data, error} = await SpaceService.updateSpace(erc, {
				description,
				externalReferenceCode: erc,
				name,
				settings: {
					logoColor,
					mimeTypeLimits: mimeTypeLimits
						?.map(({id: _id, ...rest}) => rest)
						.filter((mimeTypeLimit) =>
							Object.values(mimeTypeLimit).some((value) => value)
						),
					sharingEnabled,
					trashEnabled,
					trashEntriesMaxAge,
				},
			});

			if (error) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred-while-saving-the-space'
					),
					type: 'danger',
				});
			}
			else if (data) {
				openToast({
					message: Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						name
					),
					type: 'success',
				});

				if (setSpace) {
					setSpace(data);
				}
			}
		},
		validate: (values): Errors =>
			validate(
				{
					erc: [required],
					name: [
						required,
						nonNumeric,
						notNull,
						invalidCharacters(['*']),
						maxLength(150),
					],
					trashEntriesMaxAge: values.trashEnabled
						? [required, validNumber]
						: [],
				},
				values,
				errors
			),
	});

	const onSave = () => {
		if (Object.keys(errors).length) {
			focusInvalidElement();

			return;
		}

		submitForm();
	};

	const onCancel = () => {
		if (backURL) {
			navigate(backURL);
		}
		else {
			window.history.back();
		}
	};

	return (
		<form
			className="container-fluid container-fluid-max-md p-0 p-md-4"
			onSubmit={handleSubmit}
		>
			<SpacePanel title={Liferay.Language.get('general')}>
				<SpaceBaseFields
					errors={errors}
					onBlurName={handleBlur}
					onChangeDescription={(value) =>
						setFieldValue('description', value)
					}
					onChangeLogoColor={(value) =>
						setFieldValue('logoColor', value)
					}
					onChangeName={handleChange}
					touched={touched}
					values={values}
				>
					<>
						<ClayForm.Group>
							<label htmlFor={`${id}groupId`}>
								{Liferay.Language.get('group-id')}
							</label>

							<ClayInput
								id={`${id}groupId`}
								readOnly
								value={groupId}
							/>
						</ClayForm.Group>

						<FieldText
							errorMessage={touched.erc ? errors?.erc : undefined}
							helpIcon={Liferay.Language.get(
								'unique-key-for-referencing-the-space-definition'
							)}
							label={Liferay.Language.get('erc')}
							name="erc"
							onBlur={handleBlur}
							onChange={handleChange}
							required
							value={values.erc}
						/>
					</>
				</SpaceBaseFields>
			</SpacePanel>

			<SpacePanel title={Liferay.Language.get('sharing')}>
				<>
					<p className="mb-4">
						{Liferay.Language.get(
							'enable-this-option-to-allow-users-to-share-items-with-other-users'
						)}
					</p>
					<ClayForm.Group>
						<ClayCheckbox
							checked={values.sharingEnabled}
							label={Liferay.Language.get('enable-sharing')}
							onChange={({target: {checked}}) =>
								setFieldValue('sharingEnabled', checked)
							}
						/>
					</ClayForm.Group>
				</>
			</SpacePanel>

			<SpacePanel title={Liferay.Language.get('mime-type-limit')}>
				<p>{Liferay.Language.get('file-size-mime-type-description')}</p>

				<MimeTypeLimitFields
					errors={errors}
					mimeTypeLimits={values.mimeTypeLimits}
					setErrors={setErrors}
					setFieldValue={setFieldValue}
					setTouched={setTouched}
					touched={touched}
				/>
			</SpacePanel>

			<SpacePanel title={Liferay.Language.get('recycle-bin')}>
				<>
					<p className="mb-4">
						{Liferay.Language.get(
							'enable-this-option-to-allow-users-to-move-assets-into-recycle-bin'
						)}
					</p>

					<ClayForm.Group>
						<ClayCheckbox
							checked={values.trashEnabled}
							label={Liferay.Language.get('enable-recycle-bin')}
							onChange={({target: {checked}}) =>
								setFieldValue('trashEnabled', checked)
							}
						/>
					</ClayForm.Group>

					{values.trashEnabled && (
						<FieldText
							errorMessage={
								touched.trashEntriesMaxAge
									? (errors?.trashEntriesMaxAge as string)
									: undefined
							}
							formGroupProps={{className: 'col-12 col-sm-6 p-0'}}
							helpIcon={Liferay.Language.get(
								'trash-entries-max-age-in-days-help'
							)}
							label={Liferay.Language.get(
								'trash-entries-max-age'
							)}
							name="trashEntriesMaxAge"
							onBlur={handleBlur}
							onChange={handleChange}
							type="number"
							value={String(values.trashEntriesMaxAge ?? '')}
						/>
					)}
				</>
			</SpacePanel>

			<ClayButton.Group className="mt-2" spaced>
				<ClayButton onClick={onSave}>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton displayType="secondary" onClick={onCancel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayButton.Group>
		</form>
	);
}

function MimeTypeLimitFields({
	errors,
	mimeTypeLimits,
	setErrors,
	setFieldValue,
	setTouched,
	touched,
}: {
	errors: Errors;
	mimeTypeLimits: MimeTypeLimit[];
	setErrors: (errors: Errors) => void;
	setFieldValue: FormikHelpers<Record<string, any>>['setFieldValue'];
	setTouched: (touched: Touched) => void;
	touched: Touched;
}) {
	const addError = (fieldName: string, errorMessage: string) => {
		setErrors({...errors, [fieldName]: errorMessage});
	};

	const addRow = () => {
		setFieldValue('mimeTypeLimits', [
			...mimeTypeLimits,
			getInitialMimeTypeLimit(),
		]);
	};

	const removeError = (fieldName: string) => {
		const nextErrors = {...errors};

		delete nextErrors[fieldName];

		setErrors(nextErrors);
	};

	const removeRow = (
		{currentTarget}: {currentTarget: HTMLElement},
		fieldName: string
	) => {
		removeError(fieldName);

		setFieldValue(
			'mimeTypeLimits',
			mimeTypeLimits.filter(({id}) => currentTarget.id !== `${id}button`),
			false
		);
	};

	return (
		<>
			{mimeTypeLimits.map(({id, maximumSize, mimeType}, index) => {
				const fieldName = `${id}maximumSize`;

				return (
					<div
						className={classNames('position-relative pt-3', {
							'mb-0': index === mimeTypeLimits.length - 1,
						})}
						key={id}
					>
						<div className="row">
							<FieldText
								formGroupProps={{
									className: 'col-12 col-sm-6',
								}}
								helpIcon={Liferay.Language.get(
									'mime-type-help-message'
								)}
								id={`${id}text`}
								label={Liferay.Language.get('mime-type')}
								name={`${id}mimeType`}
								onChange={({target: {value}}) => {
									setFieldValue(
										`mimeTypeLimits[${index}].mimeType`,
										value
									);
								}}
								value={mimeType}
							/>

							<FieldText
								errorMessage={
									touched[fieldName]
										? (errors?.[fieldName] as string)
										: undefined
								}
								formGroupProps={{
									className: 'col-12 col-sm-6',
								}}
								helpIcon={Liferay.Language.get(
									'maximum-file-size-help-message'
								)}
								label={Liferay.Language.get(
									'maximum-file-size'
								)}
								name={fieldName}
								onBlur={() =>
									setTouched({
										...touched,
										[fieldName]: true,
									})
								}
								onChange={({target}) => {
									const errorMessage = validNumber(
										target.value
									);

									if (errorMessage) {
										addError(fieldName, errorMessage);
									}
									else {
										removeError(fieldName);
									}

									setFieldValue(
										`mimeTypeLimits[${index}].maximumSize`,
										target.value,
										false
									);
								}}
								type="number"
								value={maximumSize.toString()}
							/>
						</div>

						<div
							className="position-absolute"
							style={{right: 0, top: 0}}
						>
							{mimeTypeLimits.length > 1 ? (
								<ClayButtonWithIcon
									aria-label={sub(
										Liferay.Language.get('remove-x'),
										Liferay.Language.get('mime-type-limit')
									)}
									className="mr-1 rounded-circle"
									id={`${id}button`}
									onClick={(event) =>
										removeRow(event, fieldName)
									}
									size="xs"
									symbol="hr"
									title={Liferay.Language.get('remove')}
								/>
							) : null}

							<ClayButtonWithIcon
								aria-label={sub(
									Liferay.Language.get('add-x'),
									Liferay.Language.get('mime-type-limit')
								)}
								className="rounded-circle"
								onClick={addRow}
								size="xs"
								symbol="plus"
								title={Liferay.Language.get('add')}
							/>
						</div>
					</div>
				);
			})}
		</>
	);
}
