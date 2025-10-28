/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {useState} from 'react';

import Table from '../table/Table';
import {TColumn, TFormattedItems, TTableRequestParams} from '../table/types';

export type TRawItem = {
	example: string;
	name: string;
	required: boolean;
	selected: boolean;
	source: string;
	type: string;
};
interface IModalProps {
	observer: any;
	onCancel: () => void;
	onSubmit: (items: TFormattedItems) => void;
	requestFn: (params: TTableRequestParams) => Promise<any>;
	title: string;
}

enum EColumn {
	Name = 'name',
	Type = 'type',
	Example = 'example',
	Source = 'source',
}

const columns: TColumn[] = [
	{
		expanded: true,
		id: EColumn.Name,
		label: Liferay.Language.get('attribute[noun]'),
	},
	{
		expanded: true,
		id: EColumn.Type,
		label: Liferay.Language.get('data-type'),
	},
	{
		id: EColumn.Example,
		label: Liferay.Language.get('sample-data'),
		sortable: false,
	},
	{
		id: EColumn.Source,
		label: Liferay.Language.get('source'),
		sortable: false,
	},
];

const Modal: React.FC<
	{children?: React.ReactNode | undefined} & IModalProps
> = ({observer, onCancel, onSubmit, requestFn, title}) => {
	const [items, setItems] = useState<TFormattedItems>({});

	return (
		<ClayModal center observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				<Table<TRawItem>
					columns={columns}
					emptyState={{
						noResultsTitle: Liferay.Language.get(
							'no-attributes-were-found'
						),
						title: Liferay.Language.get('there-are-no-attributes'),
					}}
					mapperItems={(items) =>
						items.map(
							({
								example,
								name,
								required,
								selected,
								source,
								type,
							}) => ({
								checked: selected,
								columns: [
									{id: EColumn.Name, value: name},
									{id: EColumn.Type, value: type},
									{
										id: EColumn.Example,
										value: example,
									},
									{id: EColumn.Source, value: source},
								],
								disabled: required,
								id: name + source,
							})
						)
					}
					onItemsChange={setItems}
					requestFn={requestFn}
					type="attribute"
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onCancel}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={() => onSubmit(items)}>
							{Liferay.Language.get('sync')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default Modal;
