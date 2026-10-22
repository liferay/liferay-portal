/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {
	ClayCheckbox,
	ClayInput,
	ClayRadio,
	ClayRadioGroup,
} from '@clayui/form';
import {setNestedObjectValues, useFormik} from 'formik';
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
import {LogoColor, MapProviderKey, Space} from '../../common/types/Space';
import {ERC_MAX_LENGTH} from '../../common/utils/constants';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import SpaceBaseFields from './SpaceBaseFields';
import SpacePanel from './SpacePanel';

const MINUTES_PER_DAY = 1440;

export default function SpaceGeneralSettings({
	backURL,
	companyGoogleMapsAPIKey,
	companyMapProviderKey,
	groupId,
	setSpace,
	space,
}: {
	backURL?: string;
	companyGoogleMapsAPIKey?: string;
	companyMapProviderKey?: MapProviderKey;
	groupId: string;
	setSpace?: React.Dispatch<React.SetStateAction<any>>;
	space: Space;
}) {
	const [initialERC, setInitialERC] = useState(space.externalReferenceCode);
	const [initialFriendlyURL, setInitialFriendlyURL] = useState(
		space.friendlyURL ?? ''
	);
	const [showGoogleMapsAPIKey, setShowGoogleMapsAPIKey] = useState(false);

	const id = useId();

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		setFieldError,
		setFieldValue,
		setTouched,
		submitForm,
		touched,
		values,
	} = useFormik({
		initialValues: {
			description: space.description,
			erc: initialERC,
			friendlyURL: initialFriendlyURL,
			googleMapsAPIKey:
				space.settings?.googleMapsAPIKey ||
				companyGoogleMapsAPIKey ||
				'',
			logoColor: space.settings?.logoColor as LogoColor,
			mapProviderKey:
				space.settings?.mapProviderKey ??
				companyMapProviderKey ??
				'OpenStreetMap',
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
				googleMapsAPIKey,
				logoColor = 'outline-0',
				mapProviderKey,
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
						...(Liferay.FeatureFlags['LPD-11388']
							? {
									googleMapsAPIKey:
										mapProviderKey === 'GoogleMaps'
											? googleMapsAPIKey
											: '',
									mapProviderKey,
								}
							: {}),
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
				setFieldValue(
					'googleMapsAPIKey',
					updatedSpace.settings?.googleMapsAPIKey ||
						companyGoogleMapsAPIKey ||
						''
				);
				setShowGoogleMapsAPIKey(false);

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
					googleMapsAPIKey:
						values.mapProviderKey === 'GoogleMaps'
							? [required]
							: [],
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

	const onSave = async () => {
		if (Object.keys(errors).length) {
			await setTouched(setNestedObjectValues(errors, true));

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

			{Liferay.FeatureFlags['LPD-11388'] && (
				<SpacePanel title={Liferay.Language.get('location-provider')}>
					<>
						<p className="mb-4">
							{Liferay.Language.get(
								'choose-the-map-provider-used-by-location-fields-on-assets-in-this-space'
							)}
						</p>

						<ClayForm.Group>
							<label id={`${id}mapProviderKey`}>
								{Liferay.Language.get('provider')}
							</label>

							<ClayRadioGroup
								aria-labelledby={`${id}mapProviderKey`}
								name="mapProviderKey"
								onChange={(value) =>
									setFieldValue('mapProviderKey', value)
								}
								role="radiogroup"
								value={values.mapProviderKey}
							>
								<ClayRadio
									label={Liferay.Language.get(
										'openstreetmap'
									)}
									value="OpenStreetMap"
								>
									<div className="font-weight-normal form-text mb-1 mt-0">
										{Liferay.Language.get(
											'no-api-key-is-required.-this-option-uses-the-public-openstreetmap-tile-service'
										)}
									</div>
								</ClayRadio>

								<ClayRadio
									label={Liferay.Language.get('google-maps')}
									value="GoogleMaps"
								>
									<div className="font-weight-normal form-text mb-1 mt-0">
										{Liferay.Language.get(
											'a-google-maps-api-key-with-billing-enabled-is-required'
										)}
									</div>
								</ClayRadio>
							</ClayRadioGroup>

							{values.mapProviderKey === 'GoogleMaps' && (
								<FieldWrapper
									className="ml-4"
									errorMessage={
										touched.googleMapsAPIKey
											? (errors?.googleMapsAPIKey as string)
											: undefined
									}
									feedbackId={`feedback-${id}googleMapsAPIKey`}
									fieldId={`${id}googleMapsAPIKey`}
									helpMessage={Liferay.Language.get(
										'enable-the-geocoding-api,-maps-javascript-api,-and-places-api-for-this-key-in-google-cloud'
									)}
									label={Liferay.Language.get('api-key')}
									required
								>
									<ClayInput.Group>
										<ClayInput.GroupItem>
											<ClayInput
												aria-describedby={`feedback-${id}googleMapsAPIKey`}
												autoComplete="new-password"
												data-1p-ignore
												id={`${id}googleMapsAPIKey`}
												insetAfter
												name="googleMapsAPIKey"
												onBlur={handleBlur}
												onChange={handleChange}
												placeholder={Liferay.Language.get(
													'enter-your-google-maps-api-key'
												)}
												required
												type={
													showGoogleMapsAPIKey
														? 'text'
														: 'password'
												}
												value={values.googleMapsAPIKey}
											/>

											<ClayInput.GroupInsetItem
												after
												tag="span"
											>
												<ClayButtonWithIcon
													aria-label={Liferay.Util.sub(
														showGoogleMapsAPIKey
															? Liferay.Language.get(
																	'hide-x'
																)
															: Liferay.Language.get(
																	'show-x'
																),
														Liferay.Language.get(
															'google-maps-api-key'
														)
													)}
													displayType="unstyled"
													onClick={() =>
														setShowGoogleMapsAPIKey(
															!showGoogleMapsAPIKey
														)
													}
													symbol={
														showGoogleMapsAPIKey
															? 'hidden'
															: 'view'
													}
												/>
											</ClayInput.GroupInsetItem>
										</ClayInput.GroupItem>
									</ClayInput.Group>
								</FieldWrapper>
							)}
						</ClayForm.Group>
					</>
				</SpacePanel>
			)}

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
