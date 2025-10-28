/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {stringUtils} from '@liferay/object-js-components-web';
import React from 'react';

import {deleteObjectField} from './deleteObjectFieldUtil';

interface ModalDeleteObjectFieldProps {
	handleOnClose: () => void;
	objectField: ObjectField;
	onAfterSubmit: () => void;
	setObjectField?: (values: ObjectField | null) => void;
}

export function ModalDeleteObjectField({
	handleOnClose,
	objectField,
	onAfterSubmit,
	setObjectField,
}: ModalDeleteObjectFieldProps) {
	const {observer, onClose, open} = useModal({
		onClose: () => handleOnClose(),
	});

	return (
		<ClayModalProvider>
			{objectField && (
				<ClayModal center observer={observer} status="danger">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('delete-object-field')}
					</ClayModal.Header>

					<ClayModal.Body>
						<Text as="p">
							{Liferay.Language.get(
								"this-action-cannot-be-undone-and-will-permanently-delete-this-field's-data"
							)}
						</Text>

						<Text as="p">
							{Liferay.Language.get('it-may-affect-many-records')}
						</Text>

						<Text as="p">
							{Liferay.Language.get('do-you-want-to-proceed')}
						</Text>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onClose()}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="danger"
									onClick={() => {
										deleteObjectField(
											objectField.id,
											stringUtils.getLocalizableLabel({
												fallbackLabel: objectField.name,
												labels: objectField.label,
											})
										);

										open
											? onClose()
											: setObjectField &&
												setObjectField(null);

										onAfterSubmit();
									}}
								>
									{Liferay.Language.get('delete')}
								</ClayButton>
							</ClayButton.Group>
						}
					></ClayModal.Footer>
				</ClayModal>
			)}
		</ClayModalProvider>
	);
}
