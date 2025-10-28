/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {Input} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import React, {FormEvent, useState} from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {TYPES, useViewContext} from '../objectViewContext';

interface IProps {
	editingObjectFieldName: string;
	observer: Observer;
	onClose: () => void;
}

export function ModalEditViewColumn({
	editingObjectFieldName,
	observer,
	onClose,
}: IProps) {
	const [
		{
			objectView: {objectViewColumns},
		},
		dispatch,
	] = useViewContext();

	const [editingColumn] = objectViewColumns.filter(
		(viewColumn) => viewColumn.objectFieldName === editingObjectFieldName
	);

	const {label} = editingColumn;

	const [translations, setTranslations] = useState(label);

	const onSubmit = (event: FormEvent) => {
		event.preventDefault();

		Object.entries(translations).forEach(([key, value]) => {
			if (value === '' && key !== defaultLanguageId) {
				delete translations[key as Liferay.Language.Locale];
			}
		});

		dispatch({
			payload: {editingObjectFieldName, translations},
			type: TYPES.EDIT_OBJECT_VIEW_COLUMN_LABEL,
		});

		onClose();
	};

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={(event) => onSubmit(event)}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('rename-column-label')}
				</ClayModal.Header>

				<ClayModal.Body>
					<Input
						disabled
						label={Liferay.Language.get('field-label')}
						name={editingObjectFieldName}
						value={editingColumn.fieldLabel}
					/>

					<InputLocalized
						id="locale"
						label={Liferay.Language.get('column-label')}
						onChange={setTranslations}
						required
						translations={translations}
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
								{Liferay.Language.get('edit')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}
