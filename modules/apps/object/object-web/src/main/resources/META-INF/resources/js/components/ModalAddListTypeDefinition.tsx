/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {
	API,
	FormError,
	Input,
	constantsUtils,
	useForm,
} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../utils/constants';

const ModalAddListTypeDefinition: React.FC<
	{children?: React.ReactNode | undefined} & IProps
> = ({apiURL, observer, onClose}) => {
	const initialValues: TInitialValues = {
		name_i18n: {[defaultLanguageId]: ''},
	};
	const [error, setError] = useState<string>('');

	const onSubmit = async (values: TInitialValues) => {
		try {
			await API.save({item: values, method: 'POST', url: apiURL});

			onClose();
			window.location.reload();
		}
		catch (error) {
			setError((error as Error).message);
		}
	};

	const validate = (values: TInitialValues) => {
		const errors: FormError<TInitialValues> = {};

		if (!values.name_i18n[defaultLanguageId]) {
			errors.name_i18n = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const {errors, handleSubmit, setValues, values} = useForm({
		initialValues,
		onSubmit,
		validate,
	});

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('new-picklist')}
				</ClayModal.Header>

				<ClayModal.Body>
					{error && (
						<ClayAlert displayType="danger">{error}</ClayAlert>
					)}

					<Input
						error={errors.name_i18n}
						id="listTypeDefinitionName"
						label={Liferay.Language.get('name')}
						name="name_i18n"
						onChange={({target: {value}}) =>
							setValues({
								name_i18n: {
									[defaultLanguageId]: value,
								},
							})
						}
						required
						value={values.name_i18n[defaultLanguageId]}
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
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

interface IProps extends React.HTMLAttributes<HTMLElement> {
	apiURL: string;
	observer: Observer;
	onClose: () => void;
}

type TInitialValues = {
	name_i18n: LocalizedValue<string>;
};

const ModalWithProvider: React.FC<
	{children?: React.ReactNode | undefined} & IProps
> = ({apiURL}) => {
	const [visibleModal, setVisibleModal] = useState<boolean>(false);
	const {observer, onClose} = useModal({
		onClose: () => setVisibleModal(false),
	});

	useEffect(() => {
		Liferay.on('addListTypeDefinition', () => setVisibleModal(true));

		return () => {
			Liferay.detach('addListTypeDefinition', () =>
				setVisibleModal(true)
			);
		};
	}, []);

	return (
		<ClayModalProvider>
			{visibleModal && (
				<ModalAddListTypeDefinition
					apiURL={apiURL}
					observer={observer}
					onClose={onClose}
				/>
			)}
		</ClayModalProvider>
	);
};

export default ModalWithProvider;
