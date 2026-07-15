/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {
	FieldText,
	required,
	validate,
} from '@liferay/site-cms-site-initializer';
import {useFormik} from 'formik';
import {openToast, useId} from 'frontend-js-components-web';
import {navigate, sessionStorage} from 'frontend-js-web';
import React from 'react';

import RoomService from '../../common/services/RoomService';
import {IRoomObjectEntry} from '../../common/utils/types';

const SUCCESS_MESSAGE_SESSION_KEY =
	'com.liferay.site.dsr.site.initializer.roomSettingsSuccessMessage';

export default function RoomGeneralSettings({
	backURL,
	room,
}: {
	backURL: string;
	room: IRoomObjectEntry;
}) {
	const id = useId();

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		submitForm,
		touched,
		values,
	} = useFormik({
		initialValues: {
			externalReferenceCode: room.externalReferenceCode,
			friendlyURL: room.friendlyURL ?? '',
			name: room.name,
		},
		onSubmit: async (values) => {
			try {
				await RoomService.updateRoomSettings(room.id, {
					externalReferenceCode: values.externalReferenceCode,
					friendlyURL: values.friendlyURL,
					name: values.name,
				});

				sessionStorage.setItem(
					SUCCESS_MESSAGE_SESSION_KEY,
					Liferay.Language.get('your-request-completed-successfully'),
					sessionStorage.TYPES.NECESSARY
				);

				navigate(backURL);
			}
			catch (error) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			}
		},
		validate: (values) =>
			validate(
				{
					externalReferenceCode: [required],
					friendlyURL: [required],
					name: [required],
				},
				values
			),
	});

	return (
		<form
			className="container-fluid container-fluid-max-md p-0 p-md-4"
			onSubmit={handleSubmit}
		>
			<ClayPanel
				aria-label={Liferay.Language.get('general')}
				className="mb-4"
				collapsable
				defaultExpanded
				displayTitle={
					<ClayPanel.Title>
						<h2 className="mb-0 py-2 text-6 text-dark">
							{Liferay.Language.get('general')}
						</h2>
					</ClayPanel.Title>
				}
				displayType="secondary"
				role="group"
				showCollapseIcon
			>
				<div className="pt-4 px-4">
					<FieldText
						errorMessage={
							touched.name ? (errors?.name as string) : undefined
						}
						label={Liferay.Language.get('name')}
						name="name"
						onBlur={handleBlur}
						onChange={handleChange}
						required
						value={values.name}
					/>

					<FieldText
						errorMessage={
							touched.externalReferenceCode
								? (errors?.externalReferenceCode as string)
								: undefined
						}
						helpIcon={Liferay.Language.get(
							'unique-key-for-referencing-the-room'
						)}
						label={Liferay.Language.get('external-reference-code')}
						name="externalReferenceCode"
						onBlur={handleBlur}
						onChange={handleChange}
						required
						value={values.externalReferenceCode}
					/>

					<FieldText
						errorMessage={
							touched.friendlyURL
								? (errors?.friendlyURL as string)
								: undefined
						}
						helpIcon={Liferay.Language.get(
							'this-value-determines-the-url-for-this-room'
						)}
						label={Liferay.Language.get('friendly-url')}
						name="friendlyURL"
						onBlur={handleBlur}
						onChange={handleChange}
						required
						value={values.friendlyURL}
					/>

					<ClayForm.Group>
						<label htmlFor={`${id}siteId`}>
							{Liferay.Language.get('site-id')}
						</label>

						<ClayInput
							id={`${id}siteId`}
							name="siteId"
							readOnly
							value={String(room.siteId)}
						/>
					</ClayForm.Group>
				</div>
			</ClayPanel>

			<ClayButton.Group className="mt-2" spaced>
				<ClayButton onClick={submitForm}>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() => navigate(backURL)}
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayButton.Group>
		</form>
	);
}
