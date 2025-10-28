/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {openModal} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import openInUseModal from '../commands/openInUseModal';
import {ModalType} from '../constants/modalTypes';
import {MappingType} from '../types/MappingTypes';
import {ValidationError} from '../types/ValidationError';
import ContentTypeModalForm from './ContentTypeModalForm';

interface Props {
	description?: string;
	disableWarning?: boolean;
	displayPageName: string;
	formSubmitURL: string;
	mappingTypes: MappingType[];
	namespace: string;
	onClose: () => void;
	selectedSubtype?: string;
	selectedType?: string;
	title: string;
	type: ModalType;
	warningMessage: string;
}

interface ModalProps {
	children: React.ReactNode;
	disableWarning: boolean;
	error: ValidationError;
	onCloseAlert: () => void;
	onCloseWarning: () => void;
	warningMessage: string;
	warningVisible: boolean;
}

export function ModalContent({
	children,
	disableWarning,
	error,
	onCloseAlert,
	onCloseWarning,
	warningMessage,
	warningVisible,
}: ModalProps) {
	return (
		<>
			{warningMessage && warningVisible && !disableWarning ? (
				<ClayAlert
					displayType="warning"
					onClose={onCloseWarning}
					title={Liferay.Language.get('warning')}
					variant="stripe"
				>
					{warningMessage}
				</ClayAlert>
			) : null}

			{error && error.other ? (
				<ClayAlert
					displayType="danger"
					onClose={onCloseAlert}
					title={Liferay.Language.get('error')}
					variant="stripe"
				>
					{error.other}
				</ClayAlert>
			) : null}

			{children}
		</>
	);
}

export default function ContentTypeModal({
	description,
	disableWarning = false,
	displayPageName,
	formSubmitURL,
	mappingTypes,
	namespace,
	onClose,
	selectedSubtype,
	selectedType,
	title,
	type,
	warningMessage,
}: Props) {
	const [error, setError] = useState<ValidationError>({});
	const [loading, setLoading] = useState(false);
	const [warningVisible, setWarningVisible] = useState(true);

	const {observer} = useModal({onClose});

	const formRef = useRef<HTMLFormElement>(null);

	const validateForm = useCallback(
		(form: any) => {
			const {elements} = form;
			const error: ValidationError = {};

			const errorMessage = Liferay.Language.get('this-field-is-required');

			const nameField = elements[`${namespace}name`];

			if (nameField && !nameField.value) {
				error.name = errorMessage;
			}

			const classNameIdField = elements[`${namespace}classNameId`];

			if (classNameIdField.selectedIndex === 0) {
				error.classNameId = errorMessage;
			}

			const classTypeIdField = elements[`${namespace}classTypeId`];

			if (classTypeIdField && classTypeIdField.selectedIndex === 0) {
				error.classTypeId = errorMessage;
			}

			return error;
		},
		[namespace]
	);

	const handleSubmit = useCallback(
		(event: any) => {
			event.preventDefault();

			const form = formRef.current;

			const error = validateForm(form);

			if (Object.keys(error).length !== 0) {
				setError(error);

				return;
			}

			setLoading(true);

			fetch(formSubmitURL, {
				body: new FormData(form!),
				method: 'POST',
			})
				.then((response) => response.json())
				.then(({error, redirectURL}) => {
					if (error?.hasUsages) {
						onClose();

						openInUseModal({
							assetType: error.assetType,
							status: 'warning',
							viewUsagesURL: error.viewUsagesURL,
						});
					}
					else if (error?.isLocked) {
						onClose();

						openModal({
							bodyHTML: `
							<p class="text-secondary">
								${Liferay.Language.get(
									'the-content-type-cannot-be-changed-because-this-display-page-template-is-being-edited-by-another-user'
								)}
							</p>`,
							buttons: [
								{
									autoFocus: true,
									displayType: 'warning',
									label: Liferay.Language.get('ok'),
									type: 'cancel',
								},
							],
							status: 'warning',
							title: Liferay.Language.get(
								'display-page-in-edition'
							),
						});
					}
					else if (error) {
						setLoading(false);
						setError(error);
						setWarningVisible(false);
					}
					else if (redirectURL) {
						navigate(redirectURL, {
							beforeScreenFlip: onClose,
						});
					}
				})
				.catch(() =>
					setError({
						other: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
					})
				);
		},
		[formSubmitURL, onClose, validateForm]
	);

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ModalContent
				disableWarning={disableWarning}
				error={error}
				onCloseAlert={() => setError({})}
				onCloseWarning={() => setWarningVisible(false)}
				warningMessage={warningMessage}
				warningVisible={warningVisible}
			>
				<ClayModal.Body>
					{description ? (
						<p className="text-secondary">{description}</p>
					) : null}

					<ContentTypeModalForm
						displayPageName={displayPageName}
						error={error}
						formRef={formRef}
						mappingTypes={mappingTypes}
						namespace={namespace}
						onSubmit={handleSubmit}
						selectedSubtype={selectedSubtype}
						selectedType={selectedType}
						type={type}
					/>
				</ClayModal.Body>
			</ModalContent>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
