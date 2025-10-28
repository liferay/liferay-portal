/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {Input} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

export default function DangerModal({
	children,
	errorMessage,
	observer,
	onClose,
	onDelete,
	placeholder,
	title,
	token,
}: IProps) {
	const [value, setValue] = useState<string>();

	return (
		<ClayModal center observer={observer} status="danger">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				{children}

				<Input
					error={
						value !== undefined &&
						value.toLowerCase() !== token.toLowerCase()
							? errorMessage
							: ''
					}
					onChange={({target: {value}}) => {
						setValue(value);
					}}
					placeholder={placeholder}
					value={value}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={
								token?.toLocaleLowerCase() !==
								value?.toLocaleLowerCase()
							}
							displayType="danger"
							onClick={onDelete}
						>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</ClayModal>
	);
}

interface IProps {
	children?: React.ReactNode;
	errorMessage: string;
	observer: Observer;
	onClose: () => void;
	onDelete: (event: React.MouseEvent<HTMLButtonElement>) => void;
	placeholder?: string;
	title: string;
	token: string;
}
