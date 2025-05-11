/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {
	API,
	FormError,
	Input,
	SingleSelect,
	constantsUtils,
	objectDefinitionUtils,
	openToast,
	useForm,
} from '@liferay/object-js-components-web';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';

import './ModalAddObjectDefinition.scss';

interface ModalAddObjectDefinitionProps {
	handleOnClose: () => void;
	learnResourceContext: any;
	objectDefinitionsStorageTypes: LabelValueObject[];
	objectFolderExternalReferenceCode?: string;
	onAfterSubmit?: (value: ObjectDefinition) => void;
	reload?: boolean;
}

type TInitialValues = {
	label: string;
	name?: string;
	pluralLabel: string;
	storageType: string;
};

export function ModalAddObjectDefinition({
	handleOnClose,
	learnResourceContext,
	objectDefinitionsStorageTypes,
	objectFolderExternalReferenceCode,
	onAfterSubmit,
}: ModalAddObjectDefinitionProps) {
	const [error, setError] = useState<string>('');

	const {observer, onClose} = useModal({
		onClose: () => handleOnClose(),
	});

	const [showProxyWarning, setShowProxyWarning] = useState<boolean>(false);

	const objectDefinitionStorageTypesSortedByLabel = [
		...objectDefinitionsStorageTypes,
	].sort((firstStorage, secondStorage) => {
		const firstLabel = firstStorage.label.toLowerCase();
		const secondLabel = secondStorage.label.toLowerCase();

		if (firstLabel < secondLabel) {
			return -1;
		}
		else if (firstLabel > secondLabel) {
			return 1;
		}
		else {
			return 0;
		}
	});

	const initialValues: TInitialValues = {
		label: '',
		name: undefined,
		pluralLabel: '',
		storageType: 'default',
	};

	const onSubmit = async ({
		label,
		name,
		pluralLabel,
		storageType,
	}: TInitialValues) => {
		const objectDefinition: Partial<ObjectDefinition> = {
			label: {
				[defaultLanguageId]: label,
			},
			name: name || objectDefinitionUtils.normalizeName(label),
			objectFields: [],
			pluralLabel: {
				[defaultLanguageId]: pluralLabel,
			},
			scope: 'company',
		};

		if (objectFolderExternalReferenceCode) {
			objectDefinition.objectFolderExternalReferenceCode =
				objectFolderExternalReferenceCode;
		}

		if (Liferay.FeatureFlags['LPS-135430']) {
			objectDefinition.storageType = storageType;
		}
		try {
			const newObjectDefinition = (await API.postObjectDefinition(
				objectDefinition
			)) as ObjectDefinition;

			onClose();

			openToast({
				message: sub(
					Liferay.Language.get('x-was-created-successfully'),
					`<strong>${Liferay.Util.escapeHTML(label)}</strong>`
				),
				type: 'success',
			});

			if (onAfterSubmit) {
				onAfterSubmit(newObjectDefinition);
			}
		}
		catch (error) {
			setError((error as Error).message);
		}
	};

	const validate = (values: TInitialValues) => {
		const errors: FormError<TInitialValues> = {};

		if (!values.label) {
			errors.label = constantsUtils.REQUIRED_MSG;
		}
		if (!(values.name ?? values.label)) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}
		if (!values.pluralLabel) {
			errors.pluralLabel = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const {errors, handleChange, handleSubmit, setValues, values} = useForm({
		initialValues,
		onSubmit,
		validate,
	});

	return (
		<ClayModalProvider>
			<ClayModal center observer={observer}>
				<ClayForm onSubmit={handleSubmit}>
					<ClayModal.Header>
						{Liferay.Language.get('new-custom-object')}
					</ClayModal.Header>

					<ClayModal.Body>
						{error && (
							<ClayAlert displayType="danger">{error}</ClayAlert>
						)}

						<Input
							error={errors.label}
							id="objectDefinitionLabel"
							label={Liferay.Language.get('label')}
							name="label"
							onChange={handleChange}
							required
							value={values.label}
						/>

						<Input
							error={errors.pluralLabel}
							id="objectDefinitionPluralLabel"
							label={Liferay.Language.get('plural-label')}
							name="pluralLabel"
							onChange={handleChange}
							required
							value={values.pluralLabel}
						/>

						<Input
							error={errors.name}
							id="objectDefinitionName"
							label={Liferay.Language.get('object-name')}
							name="name"
							onChange={handleChange}
							required
							value={
								values.name ??
								objectDefinitionUtils.normalizeName(
									values.label
								)
							}
						/>

						{Liferay.FeatureFlags['LPS-135430'] && (
							<>
								<div className="lfr__object-web-modal-add-object-definition-storage-type">
									<SingleSelect<LabelValueObject>
										items={
											objectDefinitionStorageTypesSortedByLabel
										}
										label={Liferay.Language.get(
											'storage-type'
										)}
										onSelectionChange={(value) => {
											setValues({
												...values,
												storageType: value as string,
											});

											if (value !== 'default') {
												setShowProxyWarning(true);
											}
										}}
										selectedKey={values.storageType}
										tooltip={Liferay.Language.get(
											'object-definition-storage-type-tooltip'
										)}
									/>
								</div>

								{showProxyWarning && (
									<ClayAlert
										displayType="info"
										onClose={() =>
											setShowProxyWarning(false)
										}
										title={`${Liferay.Language.get('info')}:`}
									>
										{Liferay.Language.get(
											'proxy-objects-have-some-known-limitations'
										)}
										&nbsp;
										<LearnResourcesContext.Provider
											value={learnResourceContext}
										>
											<LearnMessage
												className="alert-link"
												resource="object-web"
												resourceKey="managing-data-from-external-systems"
											/>
										</LearnResourcesContext.Provider>
									</ClayAlert>
								)}
							</>
						)}
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group key={1} spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onClose()}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton displayType="primary" type="submit">
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayForm>
			</ClayModal>
		</ClayModalProvider>
	);
}
