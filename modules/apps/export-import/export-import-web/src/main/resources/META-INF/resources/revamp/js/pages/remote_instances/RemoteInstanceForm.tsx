/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import {Form, Formik} from 'formik';
import {escapeHTML} from 'frontend-js-web';
import React from 'react';

import {
	FormikFieldPassword,
	FormikFieldText,
} from '../../components/forms/formik';
import {
	addRemoteInstance,
	updateRemoteInstance,
} from '../../services/remoteInstances';
import {
	RemoteInstance,
	RemoteInstanceFormValues,
} from '../../types/remoteInstance';
import validateRemoteInstance from '../../utils/validateRemoteInstance';

interface Props {
	onBack: () => void;
	remoteInstance: RemoteInstance | null;
}

function getInitialValues(
	remoteInstance: RemoteInstance | null
): RemoteInstanceFormValues {
	return {
		clientId: remoteInstance?.clientId ?? '',
		clientSecret: '',
		hostname: remoteInstance?.hostname ?? '',
		name: remoteInstance?.name ?? '',
		port: remoteInstance?.port ? String(remoteInstance.port) : '',
		secureConnection: remoteInstance?.secureConnection ?? true,
	};
}

export default function RemoteInstanceForm({onBack, remoteInstance}: Props) {
	const clientSecretStored = remoteInstance !== null;

	return (
		<Formik
			initialValues={getInitialValues(remoteInstance)}
			onSubmit={async (values) => {
				const remoteInstanceRequest = {
					clientId: values.clientId.trim(),
					hostname: values.hostname.trim(),
					name: values.name.trim(),
					port: values.port.trim() ? Number(values.port) : null,
					secureConnection: values.secureConnection,
					...(values.clientSecret && {
						clientSecret: values.clientSecret,
					}),
				};

				const {error} = remoteInstance
					? await updateRemoteInstance(
							remoteInstance.id,
							remoteInstanceRequest
						)
					: await addRemoteInstance(remoteInstanceRequest);

				if (error !== null) {
					Liferay.Util.openToast({
						message: escapeHTML(error),
						type: 'danger',
					});

					return;
				}

				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});

				onBack();
			}}
			validate={(values) =>
				validateRemoteInstance(values, clientSecretStored)
			}
		>
			{(formik) => (
				<Form className="sheet sheet-lg" noValidate>
					<div className="sheet-header">
						<h2 className="sheet-title">
							{remoteInstance
								? Liferay.Language.get(
										'edit-remote-environment'
									)
								: Liferay.Language.get(
										'add-remote-environment'
									)}
						</h2>
					</div>

					<FormikFieldText
						label={Liferay.Language.get('name')}
						name="name"
						required
					/>

					<FormikFieldText
						label={Liferay.Language.get('hostname')}
						name="hostname"
						required
					/>

					<FormikFieldText
						inputMode="numeric"
						label={Liferay.Language.get('port')}
						name="port"
					/>

					<ClayForm.Group>
						<ClayCheckbox
							checked={formik.values.secureConnection}
							label={Liferay.Language.get(
								'use-a-secure-network-connection'
							)}
							onChange={(event) =>
								formik.setFieldValue(
									'secureConnection',
									event.target.checked
								)
							}
						/>
					</ClayForm.Group>

					<FormikFieldText
						label={Liferay.Language.get('client-id')}
						name="clientId"
						required
					/>

					<FormikFieldPassword
						autoComplete="new-password"
						helpMessage={
							clientSecretStored
								? Liferay.Language.get(
										'leave-blank-to-keep-the-current-value'
									)
								: undefined
						}
						label={Liferay.Language.get('client-secret')}
						name="clientSecret"
						required={!clientSecretStored}
					/>

					<div className="sheet-footer">
						<div className="btn-group-item">
							<ClayButton
								disabled={formik.isSubmitting}
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</div>

						<div className="btn-group-item">
							<ClayButton
								displayType="secondary"
								onClick={onBack}
								type="button"
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>
						</div>
					</div>
				</Form>
			)}
		</Formik>
	);
}
