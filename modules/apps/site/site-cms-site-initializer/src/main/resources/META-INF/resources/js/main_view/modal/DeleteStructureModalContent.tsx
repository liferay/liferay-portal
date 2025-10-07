/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {FieldText} from '../../common/components/forms';

export default function DeleteStructureModalContent({
	closeModal,
	name,
	onDelete,
	usesCount,
}: {
	closeModal: () => void;
	name: string;
	onDelete: () => Promise<void>;
	usesCount: number;
}) {
	const [value, setValue] = useState<string>('');
	const hasError = name?.toLocaleLowerCase() !== value?.toLocaleLowerCase();

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		try {
			await onDelete();
		}
		finally {
			closeModal();
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('delete-x'),
					Liferay.Util.escapeHTML(name)
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{Liferay.Language.get(
						'deleting-a-content-structure-will-also-remove-all-of-its-associated-entries'
					)}
				</p>

				<p
					dangerouslySetInnerHTML={{
						__html: sub(
							Liferay.Language.get(
								'x-is-currently-used-by-x-entries'
							),
							`<strong>${Liferay.Util.escapeHTML(name)}</strong>`,
							`<strong>${Liferay.Util.escapeHTML(usesCount.toString())}</strong>`
						),
					}}
				/>

				<p
					dangerouslySetInnerHTML={{
						__html: sub(
							Liferay.Language.get(
								'to-confirm-the-deletion-please-type-x-below'
							),
							`<strong>${Liferay.Util.escapeHTML(name)}</strong>`
						),
					}}
				/>

				<FieldText
					errorMessage={
						value !== '' && hasError
							? sub(
									Liferay.Language.get(
										'input-does-not-match-x'
									),
									name
								)
							: undefined
					}
					label=""
					name="deleConfirmation"
					onChange={({target: {value}}) => {
						setValue(value);
					}}
					placeholder={Liferay.Language.get(
						'confirm-content-structure-name'
					)}
					value={value}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={hasError}
							displayType="danger"
							type="submit"
						>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			></ClayModal.Footer>
		</form>
	);
}
