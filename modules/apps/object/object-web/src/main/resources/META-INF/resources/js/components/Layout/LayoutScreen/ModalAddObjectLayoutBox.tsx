/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {
	FormError,
	Input,
	constantsUtils,
	useForm,
} from '@liferay/object-js-components-web';
import React from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {TYPES, useLayoutContext} from '../objectLayoutContext';

type TInitialValues = {
	name: string;
};

interface IModalAddObjectLayoutBoxProps
	extends React.HTMLAttributes<HTMLElement> {
	observer: Observer;
	onClose: () => void;
}

export function ModalAddObjectLayoutBox({
	observer,
	onClose,
	tabIndex,
}: IModalAddObjectLayoutBoxProps) {
	const [, dispatch] = useLayoutContext();

	const initialValues: TInitialValues = {
		name: '',
	};

	const onSubmit = (values: TInitialValues) => {
		dispatch({
			payload: {
				name: {
					[defaultLanguageId]: values.name,
				},
				tabIndex,
				type: 'regular',
			},
			type: TYPES.ADD_OBJECT_LAYOUT_BOX,
		});

		onClose();
	};

	const onValidate = (values: TInitialValues) => {
		const errors: FormError<TInitialValues> = {};

		if (!values.name) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const {errors, handleChange, handleSubmit, values} = useForm({
		initialValues,
		onSubmit,
		validate: onValidate,
	});

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('add-block')}
				</ClayModal.Header>

				<ClayModal.Body>
					<Input
						error={errors.name}
						id="inputName"
						label={Liferay.Language.get('label')}
						name="name"
						onChange={handleChange}
						required
						value={values.name}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton type="submit">
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}
