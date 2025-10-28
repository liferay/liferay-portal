/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {
	API,
	FormError,
	Input,
	constantsUtils,
	useForm,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import {defaultLanguageId} from '../utils/constants';

interface IProps extends React.HTMLAttributes<HTMLElement> {
	apiURL: string;
	inputId: string;
	label: string;
	observer: Observer;
	onClose: () => void;
}

type TInitialValues = {
	name: LocalizedValue<string>;
};

export function ModalBasicWithFieldName({
	apiURL,
	inputId,
	label,
	observer,
	onClose,
}: IProps) {
	const initialValues: TInitialValues = {
		name: {[defaultLanguageId]: ''},
	};
	const [error, setError] = useState<string>('');

	const onSubmit = async ({name}: TInitialValues) => {
		try {
			await API.save({
				item: {name: {[defaultLanguageId]: name}},
				method: 'POST',
				url: apiURL,
			});

			onClose();
			window.location.reload();
		}
		catch (error) {
			setError((error as Error).message);
		}
	};

	const validate = ({name}: TInitialValues) => {
		const errors: FormError<TInitialValues> = {};

		if (name[defaultLanguageId] === '') {
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
		<>
			<ClayModal observer={observer}>
				<ClayForm onSubmit={handleSubmit}>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{label}
					</ClayModal.Header>

					<ClayModal.Body>
						{error && (
							<ClayAlert displayType="danger">{error}</ClayAlert>
						)}

						<Input
							error={errors.name}
							id={inputId}
							label={Liferay.Language.get('name')}
							name="name"
							onChange={handleChange}
							required
							value={values.name[defaultLanguageId]}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group key={1} spaced>
								<ClayButton
									displayType="secondary"
									onClick={onClose}
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
		</>
	);
}

export default ModalBasicWithFieldName;
