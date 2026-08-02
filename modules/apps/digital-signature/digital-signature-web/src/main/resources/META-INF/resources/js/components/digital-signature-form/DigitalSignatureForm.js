/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {useFormik} from 'formik';
import {openToast} from 'frontend-js-components-web';
import {
	createResourceURL,
	fetch,
	navigate as liferayNavigate,
	objectToFormData,
} from 'frontend-js-web';
import React, {useContext} from 'react';

import {AppContext} from '../../AppContext';
import {
	isEmail,
	maxLength,
	required,
	validate,
	withInvalidExtensions,
} from '../form/FormValidation';
import DigitalSignatureFormBase from './DigitalSignatureFormBase';

const defaultRecipient = {
	email: '',
	fullName: '',
};

const DigitalSignatureForm = ({fileEntries = [], navigate}) => {
	const {
		allowedFileExtensions,
		baseResourceURL,
		enableEmbeddedView,
		portletNamespace,
	} = useContext(AppContext);
	const urlParams = new URLSearchParams(window.location.href);
	const backURL = urlParams.get(`${portletNamespace}backURL`);

	const onGoBack = () => {
		if (navigate) {
			return navigate(-1);
		}

		return liferayNavigate(backURL);
	};

	const onSubmit = async (values) => {
		const fileEntryIds = values.fileEntries.map(
			({fileEntryId}) => fileEntryId
		);

		const formDataValues = {
			[`${portletNamespace}emailMessage`]: values.emailMessage,
			[`${portletNamespace}emailSubject`]: values.emailSubject,
			[`${portletNamespace}envelopeName`]: values.envelopeName,
			[`${portletNamespace}expireAfter`]: values.expireAfter,
			[`${portletNamespace}expireWarn`]: values.expireWarn,
			[`${portletNamespace}fileEntryIds`]: fileEntryIds,
			[`${portletNamespace}recipients`]: JSON.stringify(
				values.recipients
			),
		};

		try {
			const response = await fetch(
				createResourceURL(baseResourceURL, {
					p_p_resource_id: '/digital_signature/add_ds_envelope',
				}),
				{
					body: objectToFormData(formDataValues),
					method: 'POST',
				}
			);

			const {dsEnvelopeId} = await response.json();

			if (dsEnvelopeId) {
				openToast({
					message: Liferay.Language.get(
						'your-envelope-was-created-successfully'
					),
					title: Liferay.Language.get('success'),
					type: 'success',
				});
			}
		}
		catch (error) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});
		}

		onGoBack();
	};

	const getInitialErrors = () => {
		if (fileEntries.length) {
			return {
				fileEntries: withInvalidExtensions(
					fileEntries,
					allowedFileExtensions
				),
			};
		}
	};

	const onValidate = (values) => {
		let errorList = {};

		errorList = {
			...validate(
				{
					emailMessage: [(v) => maxLength(v, 10000)],
					emailSubject: [(v) => maxLength(v, 100), required],
					envelopeName: [(v) => maxLength(v, 100), required],
					fileEntries: [
						(v) => withInvalidExtensions(v, allowedFileExtensions),
					],
				},
				values
			),
		};

		if (values.expireAfter > 0 && values.expireWarn >= values.expireAfter) {
			errorList.expireWarn = Liferay.Language.get(
				'days-to-warn-signers-must-be-fewer-than-days-until-expiration'
			);
		}

		const recipientErrors = values.recipients.map((recipient) =>
			validate(
				{
					email: [(v) => maxLength(v, 100), isEmail, required],
					fullName: [required],
				},
				recipient
			)
		);

		const withRecipientError = recipientErrors.some(
			(recipient) => recipient?.email || recipient?.fullName
		);

		if (withRecipientError) {
			errorList.recipients = recipientErrors;
		}

		return errorList;
	};

	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		isValid,
		setFieldValue,
		values,
	} = useFormik({
		initialErrors: getInitialErrors(),
		initialValues: {
			emailMessage: '',
			emailSubject: '',
			envelopeName: '',
			expireAfter: 120,
			expireWarn: 3,
			fileEntries,
			recipients: [defaultRecipient],
		},
		onSubmit,
		validate: onValidate,
	});

	return (
		<ClayLayout.Container className="collect-digital-signature">
			<ClayCard>
				<div className="header">
					<h1>
						{Liferay.Language.get('new-digital-signature-envelope')}
					</h1>
				</div>

				<hr />

				<ClayCard.Body className="m-2">
					<ClayForm onSubmit={handleSubmit}>
						<DigitalSignatureFormBase
							defaultRecipient={defaultRecipient}
							enableEmbeddedView={enableEmbeddedView}
							errors={errors}
							handleChange={handleChange}
							setFieldValue={setFieldValue}
							values={values}
						/>

						<ClayButton
							disabled={
								!isValid ||
								isSubmitting ||
								!values.fileEntries.length ||
								values.fileEntries.length > 10
							}
							type="submit"
						>
							{Liferay.Language.get('send')}
						</ClayButton>

						<ClayButton
							className="ml-2"
							displayType="secondary"
							onClick={onGoBack}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayForm>
				</ClayCard.Body>
			</ClayCard>
		</ClayLayout.Container>
	);
};

export default DigitalSignatureForm;
