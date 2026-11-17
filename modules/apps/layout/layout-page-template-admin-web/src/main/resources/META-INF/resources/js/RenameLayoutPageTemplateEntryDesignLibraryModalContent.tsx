/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {useState} from 'react';

import {FormField} from './components/ContentTypeModalForm';

interface Props {
	closeModal: () => void;
	itemData: {
		actions: {update: {href: string; method: string}};
		embedded: {name: string};
	};
	loadData: () => void;
	title: string;
}

export default function RenameLayoutPageTemplateEntryDesignLibraryModalContent({
	closeModal,
	itemData,
	loadData,
	title,
}: Props) {
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(false);
	const [name, setName] = useState(itemData.embedded.name);

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault();

		setLoading(true);

		try {
			const {href, method} = itemData.actions.update;

			const response = await fetch(href, {
				body: JSON.stringify({name: name.trim()}),
				headers: {
					'Accept': 'application/json',
					'Accept-Language':
						Liferay.ThemeDisplay.getBCP47LanguageId(),
					'Content-Type': 'application/json',
				},
				method,
			});

			if (!response.ok) {
				const problem = await response.json().catch(() => null);

				setError(
					problem?.title ||
						Liferay.Language.get('an-unexpected-error-occurred')
				);
				setLoading(false);

				return;
			}

			closeModal();

			loadData();
		}
		catch {
			setError(Liferay.Language.get('an-unexpected-error-occurred'));
			setLoading(false);
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				<FormField
					error={error}
					id="name"
					name={Liferay.Language.get('name')}
				>
					<ClayInput
						id="name"
						onChange={(event) => {
							setError('');
							setName(event.target.value);
						}}
						required
						value={name}
					/>
				</FormField>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading || !name.trim()}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
