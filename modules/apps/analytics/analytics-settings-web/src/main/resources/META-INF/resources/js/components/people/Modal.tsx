/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {useState} from 'react';

import {updateAttributesConfiguration} from '../../utils/api';
import {TEmptyState} from '../table/StateRenderer';
import Table from '../table/Table';
import {TColumn, TFormattedItems, TTableRequestParams} from '../table/types';
import {getIds} from '../table/utils';
import {EPeople} from './People';

type TRawItem = {
	id: number;
	name: string;
	selected: boolean;
};

export interface ICommonModalProps {
	observer: any;
	onCloseModal: () => void;
	syncAllAccounts: boolean;
	syncAllContacts: boolean;
	syncedIds: {
		[key in EPeople]: string[];
	};
}

interface IModalProps {
	columns: TColumn[];
	emptyState: TEmptyState;
	name: EPeople;
	observer: any;
	onCloseModal: () => void;
	requestFn: (params: TTableRequestParams) => Promise<any>;
	syncAllAccounts: boolean;
	syncAllContacts: boolean;
	syncedIds: {
		[key in EPeople]: string[];
	};
	title: string;
}

const Modal: React.FC<
	{children?: React.ReactNode | undefined} & IModalProps
> = ({
	columns,
	emptyState,
	name,
	observer,
	onCloseModal,
	requestFn,
	syncAllAccounts,
	syncAllContacts,
	syncedIds,
	title,
}) => {
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
					emptyState={emptyState}
					mapperItems={(items) => {
						return items.map(({id, name, selected}) => ({
							checked: selected,
							columns: [
								{
									id: 'name',
									value: name,
								},
							],
							disabled: false,
							id: String(id),
						}));
					}}
					onItemsChange={setItems}
					requestFn={requestFn}
					type="people"
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onCloseModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={async () => {
								const {ok} =
									await updateAttributesConfiguration({
										...syncedIds,
										[name]: getIds(
											items,
											syncedIds[name].map((id) =>
												Number(id)
											)
										),
										syncAllAccounts,
										syncAllContacts,
									});

								if (ok) {
									Liferay.Util.openToast({
										message: Liferay.Language.get(
											'people-data-have-been-saved'
										),
									});

									onCloseModal();
								}
							}}
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default Modal;
