/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {Form, FormikProvider, useFormik} from 'formik';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {FormField} from '../../../components/forms/FormField';
import {FormSection} from '../../../components/forms/FormSection';
import {FormToggle} from '../../../components/forms/FormToggle';
import useFocusFirstInvalidField from '../../../hooks/useFocusFirstInvalidField';
import useRequest from '../../../hooks/useRequest';
import ProfileService from '../../../services/ProfileService';
import {
	Profile,
	ProfileFormValues,
	ProfilePayload,
} from '../../../types/Profile';
import copyProfileURL from '../../../utils/copyProfileURL';
import required from '../../../utils/required';
import toIdentifier from '../../../utils/toIdentifier';
import {openErrorToast, openSuccessToast} from '../../../utils/toasts';

interface EditProfileInfoProps {
	backURL: string;
	editProfileURL: string;
	portletNamespace: string;
	profileERC: string;
}

export default function EditProfileInfo({
	backURL,
	editProfileURL,
	portletNamespace,
	profileERC,
}: EditProfileInfoProps) {
	const {
		data: profile,
		error,
		loading,
	} = useRequest(ProfileService.getProfile, profileERC || null);

	useEffect(() => {
		if (error) {
			openErrorToast(error);

			navigate(backURL);
		}
	}, [backURL, error]);

	if (loading || error) {
		return (
			<div className="align-items-center d-flex justify-content-center mt-4">
				<ClayLoadingIndicator />
			</div>
		);
	}

	return (
		<ProfileForm
			backURL={backURL}
			editProfileURL={editProfileURL}
			portletNamespace={portletNamespace}
			profile={profile}
		/>
	);
}

function toProfilePath(value: string): string {
	return value
		.toLowerCase()
		.replace(/\s+/g, '-')
		.replace(/[^a-z0-9-]/g, '');
}

function validateProfilePath(value: string): string | undefined {
	const requiredError = required(value);

	if (requiredError) {
		return requiredError;
	}

	return /^[a-z0-9]+(-[a-z0-9]+)*$/.test(value)
		? undefined
		: Liferay.Language.get(
				'please-enter-a-valid-profile-path-lowercase-letters-and-numbers-separated-by-single-hyphens'
			);
}

interface ProfileFormProps {
	backURL: string;
	editProfileURL: string;
	portletNamespace: string;
	profile: Profile | null;
}

function ProfileForm({
	backURL,
	editProfileURL,
	portletNamespace,
	profile,
}: ProfileFormProps) {
	const isDefaultProfile =
		profile?.externalReferenceCode === 'L_MCP_SERVER_DEFAULT_PROFILE';
	const isNew = !profile?.externalReferenceCode;

	const [profilePathChanged, setProfilePathChanged] = useState(false);

	const formRef = useRef<HTMLFormElement>(null);

	const formik = useFormik<ProfileFormValues>({
		initialValues: {
			active: profile?.profileStatus?.key === 'active',
			description: profile?.description ?? '',
			name: profile?.name ?? '',
			profilePath: profile?.profilePath ?? '',
		},
		onSubmit: async (values) => {
			const payload: ProfilePayload = {
				description: values.description,
				name: values.name,
				profilePath: values.profilePath,
				profileStatus: {key: values.active ? 'active' : 'inactive'},
			};

			const {data: saved, error} = profile?.externalReferenceCode
				? await ProfileService.patchProfile(
						profile.externalReferenceCode,
						payload
					)
				: await ProfileService.postProfile(payload);

			if (error) {
				openErrorToast(error);

				return;
			}

			if (saved) {
				openSuccessToast(
					sub(
						Liferay.Language.get('x-was-saved-successfully'),
						saved.name
					)
				);

				if (!isNew) {
					navigate(backURL);
				}
				else {
					const url = new URL(editProfileURL, window.location.origin);

					url.searchParams.set(
						`${portletNamespace}profileERC`,
						saved.externalReferenceCode ?? ''
					);

					navigate(url.toString());
				}
			}
		},
	});

	useFocusFirstInvalidField(formRef, formik);

	const {setFieldValue, values} = formik;

	useEffect(() => {
		if (isNew && !profilePathChanged) {
			setFieldValue('profilePath', toIdentifier(values.name));
		}
	}, [isNew, profilePathChanged, setFieldValue, values.name]);

	return (
		<FormikProvider value={formik}>
			<Form className="profile-form" noValidate ref={formRef}>
				{isNew && (
					<ClayAlert
						displayType="info"
						title={`${Liferay.Language.get('info')}:`}
					>
						{Liferay.Language.get(
							'tools,-data-masks,-and-status-become-available-once-the-profile-is-saved'
						)}
					</ClayAlert>
				)}

				<FormSection title={Liferay.Language.get('profile-status')}>
					<div className="align-items-center d-flex justify-content-between">
						<span className="text-secondary">
							{Liferay.Language.get(
								'activate-to-make-this-profiles-tools-available-to-mcp-clients'
							)}
						</span>

						<FormToggle
							ariaLabel={Liferay.Language.get('profile-status')}
							disabled={isNew}
							name="active"
						/>
					</div>
				</FormSection>

				<FormSection
					className="mt-4"
					title={Liferay.Language.get('profile-information')}
				>
					<FormField
						id="profileName"
						label={Liferay.Language.get('title')}
						name="name"
						required
					/>

					<FormField
						disabled={isDefaultProfile}
						helpMessage={
							isDefaultProfile
								? Liferay.Language.get(
										'the-default-profile-has-no-profile-path'
									)
								: Liferay.Language.get(
										'the-url-can-be-modified'
									)
						}
						id="profilePath"
						label={Liferay.Language.get('url')}
						name="profilePath"
						onChange={({target: {value}}) => {
							setProfilePathChanged(true);

							setFieldValue('profilePath', toProfilePath(value));
						}}
						prefix={
							<Text as="p" size={2} weight="semi-bold">
								{isDefaultProfile
									? `${window.location.origin}/o/mcp`
									: `${window.location.origin}/o/mcp/`}
							</Text>
						}
						required={!isDefaultProfile}
						suffix={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('copy-url')}
								disabled={
									!isDefaultProfile && !values.profilePath
								}
								displayType="secondary"
								onClick={() =>
									copyProfileURL(values.profilePath)
								}
								symbol="copy"
								title={Liferay.Language.get('copy-url')}
							/>
						}
						validate={
							isDefaultProfile ? undefined : validateProfilePath
						}
					/>

					<FormField
						component="textarea"
						id="profileDescription"
						label={Liferay.Language.get('description')}
						name="description"
						required
					/>
				</FormSection>

				<ClayLayout.SheetFooter>
					<ClayButton
						disabled={formik.isSubmitting}
						displayType="primary"
						type="submit"
					>
						{Liferay.Language.get('save')}
					</ClayButton>

					<ClayButton
						displayType="secondary"
						onClick={() => navigate(backURL)}
						type="button"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				</ClayLayout.SheetFooter>
			</Form>
		</FormikProvider>
	);
}
