/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

const CustomAuthorTableCell = ({
	actions,
	itemData,
	itemId,
	loadData,
	openSidePanel,
	options,
	rootPropertyName,
	value,
	valuePath,
}: {
	actions: Array<{label: string}>;
	itemData: {color: string};
	itemId: string;
	loadData: Function;
	openSidePanel: Function;
	options: {label: string};
	rootPropertyName: string;
	value: string;
	valuePath: Array<string>;
}) => {
	const ModalBody = ({closeModal}: {closeModal: Function}) => {
		return (
			<>
				<div>First action label: {actions[0].label}</div>
				<div>Item ID: {itemId}</div>
				<div>Item color: {itemData.color}</div>
				<div>Field label: {options.label}</div>
				<div>First field name: {rootPropertyName}</div>
				<div>Second field name: {valuePath[1]}</div>
				<br />
				<div>
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => {
								closeModal();

								loadData();
							}}
						>
							Reload Data
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => {
								closeModal();

								openSidePanel({
									url: 'about:blank',
								});
							}}
						>
							Open Side Panel
						</ClayButton>
					</ClayButton.Group>
				</div>
			</>
		);
	};

	return (
		<>
			<ClayLink
				onClick={(event) => {
					event.stopPropagation();

					openModal({
						bodyComponent: ModalBody,
						title: Liferay.Language.get('details'),
					});
				}}
				style={{cursor: 'pointer'}}
			>
				{value}
			</ClayLink>
		</>
	);
};

export default CustomAuthorTableCell;
