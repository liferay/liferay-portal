/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {Form, Formik} from 'formik';
import React, {useEffect, useState} from 'react';

import FieldSelectWithOption from '../../components/forms/FieldSelectWithOption';
import {FormikWrapper} from '../../components/forms/formik/FormikWrapper';
import {addRemoteImportProcess} from '../../services/remoteImportProcesses';
import {getRemoteInstances} from '../../services/remoteInstances';
import {RemoteInstance} from '../../types/remoteInstance';

export default function RemoteImportModal({
	closeModal,
	exportProcessId,
}: {
	closeModal: () => void;
	exportProcessId: number;
}) {
	const [error, setError] = useState<string | null>(null);
	const [loading, setLoading] = useState(true);
	const [remoteInstances, setRemoteInstances] = useState<
		Pick<RemoteInstance, 'id' | 'name'>[]
	>([]);

	useEffect(() => {
		getRemoteInstances().then((result) => {
			if (result.error !== null) {
				setError(result.error);
			}
			else {
				setRemoteInstances(result.data);
			}

			setLoading(false);
		});
	}, []);

	return (
		<Formik
			enableReinitialize
			initialValues={{
				remoteInstanceId: remoteInstances.length
					? String(remoteInstances[0].id)
					: '',
			}}
			onSubmit={async (values) => {
				const {error} = await addRemoteImportProcess(
					exportProcessId,
					Number(values.remoteInstanceId)
				);

				if (error !== null) {
					setError(error);

					return;
				}

				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'the-remote-import-was-requested.-you-will-be-notified-when-it-starts-and-when-it-finishes'
					),
					type: 'success',
				});

				closeModal();
			}}
		>
			{(formik) => (
				<Form noValidate>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('execute-remote-import')}
					</ClayModal.Header>

					<ClayModal.Body>
						{loading && <ClayLoadingIndicator />}

						{error && (
							<ClayAlert
								displayType="danger"
								title={Liferay.Language.get('error')}
							>
								{error}
							</ClayAlert>
						)}

						{!loading && !remoteInstances.length && !error && (
							<p className="text-secondary">
								{Liferay.Language.get(
									'no-remote-environments-have-been-added'
								)}
							</p>
						)}

						{!!remoteInstances.length && (
							<FormikWrapper name="remoteInstanceId" required>
								{({errorMessage, field}) => (
									<FieldSelectWithOption
										{...field}
										errorMessage={errorMessage}
										label={Liferay.Language.get(
											'remote-environment'
										)}
										options={remoteInstances.map(
											({id, name}) => ({
												label: name,
												value: String(id),
											})
										)}
										required
									/>
								)}
							</FormikWrapper>
						)}
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={closeModal}
									type="button"
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									disabled={
										loading ||
										formik.isSubmitting ||
										!remoteInstances.length
									}
									type="submit"
								>
									{formik.isSubmitting && (
										<span className="inline-item inline-item-before">
											<ClayLoadingIndicator size="xs" />
										</span>
									)}

									{Liferay.Language.get('execute')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</Form>
			)}
		</Formik>
	);
}
