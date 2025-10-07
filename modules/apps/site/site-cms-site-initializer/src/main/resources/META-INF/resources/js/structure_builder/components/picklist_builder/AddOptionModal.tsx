/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import classNames from 'classnames';
import {InputLocalized} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {Option} from '../../../common/types/Picklist';
import getLocalizedValue from '../../../common/utils/getLocalizedValue';
import {useAddOption} from '../../contexts/PicklistBuilderContext';
import getRandomId from '../../utils/getRandomId';
import ERCInput from '../ERCInput';
import Input from '../Input';

export default function AddOptionModal({
	onCloseModal,
	option = null,
}: {
	onCloseModal: () => void;
	option: Option | null;
}) {
	const [erc, setErc] = useState<string>(option?.erc || getRandomId());
	const [key, setKey] = useState<string>(option?.key || getRandomKey());
	const [name, setName] = useState<Liferay.Language.LocalizedValue<string>>(
		option?.name || {
			[Liferay.ThemeDisplay.getDefaultLanguageId()]:
				Liferay.Language.get('option'),
		}
	);
	const addOption = useAddOption();

	const {observer, onClose} = useModal({
		onClose: () => onCloseModal(),
	});

	const onSave = () => {
		if (!erc || !key || !name) {
			return;
		}

		addOption({erc, key, name});

		onClose();
	};

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header>
				{Liferay.Language.get('add-option')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group className={classNames({'has-error': !name})}>
					<InputLocalized
						aria-label={Liferay.Language.get('picklist-name')}
						error={
							getLocalizedValue(name)
								? ''
								: Liferay.Language.get('this-field-is-required')
						}
						label={Liferay.Language.get('name')}
						onBlur={() => setName(name)}
						onChange={(name) => setName(name)}
						required
						translations={
							name as Liferay.Language.LocalizedValue<string>
						}
					/>

					<Input
						disabled={Boolean(option?.key)}
						label={Liferay.Language.get('key')}
						onValueChange={(key) => setKey(key)}
						required
						value={key}
					/>

					<ERCInput
						helpText={sub(
							Liferay.Language.get(
								'unique-key-for-referencing-the-x'
							),
							Liferay.Language.get('option')
						)}
						onValueChange={(erc) => setErc(erc)}
						value={erc}
					/>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={onSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

function getRandomKey() {
	const digits = Math.floor(
		Math.pow(10, 5) + Math.random() * 9 * Math.pow(10, 5)
	);

	return `${Liferay.Language.get('option')}${digits}`;
}
