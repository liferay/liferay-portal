/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface Props {
	onClose: () => void;
	onSearch: (searchTerm: string) => void;
	searchTerm: string;
}

export default function ElementVariationSearch({
	onClose,
	onSearch,
	searchTerm,
}: Props) {
	return (
		<>
			<ClayInput.Group className="flex-grow-1" small>
				<ClayInput.GroupItem>
					<ClayInput
						autoFocus
						insetAfter
						onChange={({target: {value}}) => onSearch(value)}
						placeholder={`${Liferay.Language.get('search')}...`}
						sizing="sm"
						spellCheck={false}
						value={searchTerm}
					/>

					<ClayInput.GroupInsetItem after tag="span">
						{searchTerm ? (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('clear')}
								borderless
								displayType="secondary"
								monospaced
								onClick={() => onSearch('')}
								size="sm"
								symbol="times-small"
								title={Liferay.Language.get('clear')}
							/>
						) : null}

						<ClayIcon className="mr-2 mt-0" symbol="search" />
					</ClayInput.GroupInsetItem>
				</ClayInput.GroupItem>
			</ClayInput.Group>

			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('close')}
				borderless
				className="flex-shrink-0 ml-2"
				displayType="secondary"
				onClick={onClose}
				size="sm"
				symbol="times"
				title={Liferay.Language.get('close')}
			/>
		</>
	);
}
