/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import {useFormik} from 'formik';
import {openModal, openToast, useId} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useState} from 'react';

import {FieldText} from '../../common/components/forms';
import FieldWrapper from '../../common/components/forms/FieldWrapper';
import {
	Errors,
	invalidCharacters,
	maxLength,
	minValue,
	nonNumeric,
	notNull,
	required,
	validNumber,
	validate,
} from '../../common/components/forms/validations';
import SpaceService from '../../common/services/SpaceService';
import {LogoColor, Space} from '../../common/types/Space';
import {ERC_MAX_LENGTH} from '../../common/utils/constants';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import SpaceBaseFields from './SpaceBaseFields';
import SpacePanel from './SpacePanel';

const MINUTES_PER_DAY = 1440;

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
	const [initialERC, setInitialERC] = useState(space.externalReferenceCode);
	const [initialFriendlyURL, setInitialFriendlyURL] = useState(
		space.friendlyURL ?? ''
	);

	const id = useId();

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		setFieldError,
		setFieldValue,
		submitForm,
		touched,
		values,
	} = useFormik({
		initialValues: {
			description: space.description,
			erc: initialERC,
			friendlyURL: initialFriendlyURL,
			logoColor: space.settings?.logoColor as LogoColor,
			name: space.name,
			sharingEnabled: space.settings?.sharingEnabled ?? false,
			trashEnabled: space.settings?.trashEnabled ?? true,
			trashEntriesMaxAge: String(
				Math.round(
					(space.settings?.trashEntriesMaxAge ?? 0) / MINUTES_PER_DAY
				)
			),
		},
		onSubmit: async (values) => {
			const {
				description,
				erc,
				friendlyURL,
				logoColor = 'outline-0',
				name,
				sharingEnabled,
				trashEnabled,
				trashEntriesMaxAge,
			} = values;

			const {data, error, type} = await SpaceService.updateSpace(
				initialERC,
				{
					description,
					externalReferenceCode: erc,
					friendlyURL,
					name,
					settings: {
						logoColor,
						sharingEnabled,
						trashEnabled,
						trashEntriesMaxAge:
							Number(trashEntriesMaxAge) * MINUTES_PER_DAY,
					},
				}
			);

			if (error) {
				const message =
					typeof error === 'string'
						? error
						: Liferay.Language.get(
								'an-unexpected-error-occurred-while-saving-the-space'
							);

				if (type?.startsWith('FRIENDLY_URL_')) {
					setFieldError('friendlyURL', message);
				}
				else {
					openToast({
						message,
						type: 'danger',
					});
				}
			}
			else if (data) {
				openToast({
					message: Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						Liferay.Util.escapeHTML(name)
					),
					type: 'success',
				});

				const updatedSpace = data as Space;

				setFieldValue('friendlyURL', updatedSpace.friendlyURL ?? '');

				if (setSpace) {
					setSpace(updatedSpace);
					setInitialERC(updatedSpace.externalReferenceCode);
					setInitialFriendlyURL(updatedSpace.friendlyURL ?? '');
				}
			}
		},
		validate: (values): Errors =>
			validate(
				{
					erc: [maxLength(ERC_MAX_LENGTH), required],
					friendlyURL: [
						(value) =>
							!value
								? Liferay.Language.get(
										'please-enter-a-friendly-url'
									)
								: undefined,
					],
					name: [
						required,
						nonNumeric,
						notNull,
						invalidCharacters(['*']),
						maxLength(150),
					],
					trashEntriesMaxAge: values.trashEnabled
						? [minValue(1), required, validNumber]
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

		if (values.friendlyURL !== initialFriendlyURL) {
			openModal({
				bodyHTML: Liferay.Language.get(
					'changing-the-friendly-url-will-break-existing-inbound-links-bookmarks-and-redirects-pointing-to-this-space.-make-sure-to-set-up-redirects-and-update-any-references-before-saving'
				),
				buttons: [
					{
						displayType: 'secondary',
						label: Liferay.Language.get('cancel'),
						onClick: ({processClose}) => {
							processClose();
						},
						type: 'cancel',
					},
					{
						displayType: 'warning',
						label: Liferay.Language.get('save'),
						onClick: ({processClose}) => {
							processClose();
							submitForm();
						},
					},
				],
				center: true,
				role: 'alertdialog',
				status: 'warning',
				title: Liferay.Language.get('save-custom-friendly-url'),
			});

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

						<FieldWrapper
							errorMessage={
								touched.friendlyURL
									? (errors?.friendlyURL as string)
									: undefined
							}
							feedbackId={`feedback-${id}friendlyURL`}
							fieldId={`${id}friendlyURL`}
							helpIcon={Liferay.Language.get(
								'this-value-determines-display-pages-urls-for-this-space.-affects-seo-and-cross-environment-portability'
							)}
							label={Liferay.Language.get('friendly-url')}
							required
						>
							<ClayInput
								aria-describedby={
									errors?.friendlyURL
										? `feedback-${id}friendlyURL`
										: undefined
								}
								id={`${id}friendlyURL`}
								name="friendlyURL"
								onBlur={(event) => {
									const value = event.target.value.trim();

									if (value && !value.startsWith('/')) {
										setFieldValue(
											'friendlyURL',
											'/' + value
										);
									}

									handleBlur(event);
								}}
								onChange={handleChange}
								required
								value={values.friendlyURL}
							/>
						</FieldWrapper>

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
