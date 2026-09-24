/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {useField} from 'formik';
import {FieldBase, openModal, useId} from 'frontend-js-components-web';
import React from 'react';

import NewTokenSetModalContent, {
	FrontendTokenSetOption,
} from '../NewTokenSetModalContent';

interface TokenSetFieldProps {
	onCreate: (tokenSet: FrontendTokenSetOption) => void;
	tokenSets: FrontendTokenSetOption[];
}

export default function TokenSetField({
	onCreate,
	tokenSets,
}: TokenSetFieldProps) {
	const [field, meta, helpers] = useField<string>('tokenSetName');

	const id = useId();

	const openNewTokenSetModal = () => {
		openModal({
			contentComponent: ({closeModal}) => (
				<NewTokenSetModalContent
					closeModal={closeModal}
					existingTokenSets={tokenSets}
					onSuccess={(tokenSet) => {
						onCreate(tokenSet);
						helpers.setValue(tokenSet.name);
					}}
				/>
			),
		});
	};

	return (
		<FieldBase
			errorMessage={meta.touched ? meta.error : undefined}
			id={id}
			label={Liferay.Language.get('token-set')}
			required
		>
			<div className="align-items-center d-flex">
				<div className="flex-grow-1 mr-2">
					<Picker
						id={id}
						items={tokenSets}
						messages={{
							itemDescribedby: Liferay.Language.get(
								'you-are-currently-on-a-text-element,-inside-of-a-list-box'
							),
							itemSelected: Liferay.Language.get('x-selected'),
							scrollToBottomAriaLabel:
								Liferay.Language.get('scroll-to-bottom'),
							scrollToTopAriaLabel:
								Liferay.Language.get('scroll-to-top'),
						}}
						onBlur={() => helpers.setTouched(true)}
						onSelectionChange={(tokenSetName: React.Key) =>
							helpers.setValue(String(tokenSetName))
						}
						selectedKey={field.value}
					>
						{(item) => (
							<Option key={item.name} textValue={item.label}>
								{item.label}
							</Option>
						)}
					</Picker>
				</div>

				<ClayButton
					displayType="secondary"
					onClick={openNewTokenSetModal}
				>
					<ClayIcon
						className="inline-item inline-item-before"
						symbol="plus"
					/>

					{Liferay.Language.get('new-token-set')}
				</ClayButton>
			</div>
		</FieldBase>
	);
}
