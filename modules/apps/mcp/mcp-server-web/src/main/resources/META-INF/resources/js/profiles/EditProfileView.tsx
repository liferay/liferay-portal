/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {Form, FormikProvider, useFormik} from 'formik';
import {navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {FormField} from '../forms/FormField';
import {FormSection} from '../forms/FormSection';
import {getProfile} from '../services/getProfile';
import {patchProfile} from '../services/patchProfile';
import {postProfile} from '../services/postProfile';
import {Profile, ProfileFormValues, ProfilePayload} from '../types';
import {openErrorToast, openSuccessToast} from '../utils';

interface EditProfileViewProps {
	backURL: string;
	editProfileURL: string;
	portletNamespace: string;
	profileERC: string;
}

export default function EditProfileView({
	backURL,
	editProfileURL,
	portletNamespace,
	profileERC,
}: EditProfileViewProps) {
	const [loading, setLoading] = useState(Boolean(profileERC));
	const [profile, setProfile] = useState<Profile | null>(null);

	useEffect(() => {
		if (!profileERC) {
			return;
		}

		let isMounted = true;

		getProfile(profileERC).then(({data, error}) => {
			if (!isMounted) {
				return;
			}

			if (error || !data) {
				openErrorToast(
					error ||
						Liferay.Language.get('an-unexpected-error-occurred')
				);

				navigate(backURL);

				return;
			}

			setProfile(data);
			setLoading(false);
		});

		return () => {
			isMounted = false;
		};
	}, [backURL, profileERC]);

	if (loading) {
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
	const formik = useFormik<ProfileFormValues>({
		initialValues: {
			description: profile?.description ?? '',
			name: profile?.name ?? '',
		},
		onSubmit: async (values) => {
			const payload: ProfilePayload = {
				description: values.description,
				name: values.name,
			};

			const {data: saved, error} = profile?.externalReferenceCode
				? await patchProfile(profile.externalReferenceCode, payload)
				: await postProfile(payload);

			if (error) {
				openErrorToast(error);

				return;
			}

			if (saved) {
				openSuccessToast(
					Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						saved.name
					)
				);

				if (profile?.externalReferenceCode) {
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

	useEffect(() => {
		if (!formik.submitCount) {
			return;
		}

		document
			.querySelector<HTMLElement>(
				'.profile-form .form-group.has-error input:not([type="hidden"]), .profile-form .form-group.has-error textarea'
			)
			?.focus();
	}, [formik.submitCount]);

	return (
		<FormikProvider value={formik}>
			<Form className="profile-form" noValidate>
				<FormSection
					title={Liferay.Language.get('profile-information')}
				>
					<FormField
						id="profileName"
						label={Liferay.Language.get('title')}
						name="name"
						required
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
