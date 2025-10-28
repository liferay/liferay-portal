/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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
	constantsUtils,
	objectDefinitionUtils,
	openToast,
	useForm,
} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';

interface ModalAddObjectFolderProps {
	handleOnClose: () => void;
	setObjectFoldersRequestInfo: React.Dispatch<
		React.SetStateAction<ObjectFoldersRequestInfo>
	>;
	setSelectedObjectFolder: (values: Partial<ObjectFolder>) => void;
}

type TInitialValues = {
	label: string;
	name?: string;
};

export function ModalAddObjectFolder({
	handleOnClose,
	setObjectFoldersRequestInfo,
	setSelectedObjectFolder,
}: ModalAddObjectFolderProps) {
	const [error, setError] = useState<string>('');

	const {observer, onClose} = useModal({
		onClose: () => handleOnClose(),
	});

	const initialValues: TInitialValues = {
		label: '',
		name: undefined,
	};

	const onSubmit = async ({label, name}: TInitialValues) => {
		const objectFolder: Partial<ObjectFolder> = {
			label: {
				[defaultLanguageId]: label,
			},
			name: name || objectDefinitionUtils.normalizeName(label),
		};

		try {
			const newObjectFolder = (await API.save<ObjectFolder>({
				item: objectFolder,
				method: 'POST',
				returnValue: true,
				url: '/o/object-admin/v1.0/object-folders',
			})) as ObjectFolder;

			onClose();

			openToast({
				message: sub(
					Liferay.Language.get('x-was-created-successfully'),
					`<strong>${Liferay.Util.escapeHTML(label)}</strong>`
				),
				type: 'success',
			});

			setObjectFoldersRequestInfo(
				(prevValues: ObjectFoldersRequestInfo) => {
					return {
						actions: prevValues.actions,
						items: [...prevValues.items, newObjectFolder],
					};
				}
			);

			setSelectedObjectFolder(newObjectFolder);

			const currentURL = new URL(window.location.href);

			currentURL.searchParams.set(
				'objectFolderName',
				newObjectFolder.name
			);

			window.history.replaceState(null, '', currentURL.href);
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

		return errors;
	};

	const {errors, handleChange, handleSubmit, values} = useForm({
		initialValues,
		onSubmit,
		validate,
	});

	return (
		<ClayModalProvider>
			<ClayModal center observer={observer}>
				<ClayForm onSubmit={handleSubmit}>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('new-object-folder')}
					</ClayModal.Header>

					<ClayModal.Body>
						{error && (
							<ClayAlert displayType="danger">{error}</ClayAlert>
						)}

						<Input
							error={errors.label}
							label={Liferay.Language.get('label')}
							name="label"
							onChange={handleChange}
							required
							value={values.label}
						/>

						<Input
							error={errors.name}
							label={Liferay.Language.get('name')}
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
									{Liferay.Language.get('create-folder')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayForm>
			</ClayModal>
		</ClayModalProvider>
	);
}
