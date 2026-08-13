/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import {useModal} from '@clayui/modal';
import React, {useEffect, useMemo, useState} from 'react';

import {
	fetchAccountsFields,
	fetchPeopleFields,
	fetchSelectedFields,
	updateAccountsFields,
	updatePeopleFields,
} from '../../utils/api';
import Loading from '../Loading';
import {TFormattedItems} from '../table/types';
import Modal, {TRawItem} from './Modal';

enum EFields {
	Account = 'account',
	People = 'people',
}

const Attributes: React.FC = () => {
	const {
		observer: observerAccountsAttributes,
		onOpenChange: onOpenChangeAccountsAttributes,
		open: openAccountsAttributes,
	} = useModal();
	const {
		observer: observerPeopleAttributes,
		onOpenChange: onOpenChangePeopleAttributes,
		open: openPeopleAttributes,
	} = useModal();

	const [selectedFields, setSelectedFields] = useState<{
		[key in EFields]: number | React.ReactNode;
	}>({
		[EFields.Account]: <Loading inline />,
		[EFields.People]: <Loading inline />,
	});

	const syncData = async () => {
		const selectedFields = await fetchSelectedFields();

		setSelectedFields(selectedFields);
	};

	const handleSubmit = async ({
		closeFn,
		items,
		key,
		updateFn,
	}: {
		closeFn: (value: boolean) => void;
		items: TFormattedItems;
		key: EFields;
		updateFn: (items: TRawItem[]) => Promise<any>;
	}) => {
		const fields: TRawItem[] = getFields(items);
		const {ok} = await updateFn(fields);

		if (ok) {
			closeFn(false);

			setSelectedFields({
				...selectedFields,
				[key]: <Loading inline />,
			});

			setTimeout(syncData, 1000);

			Liferay.Util.openToast({
				message: Liferay.Language.get('attributes-have-been-saved'),
			});
		}
	};

	useEffect(() => {
		syncData();
	}, []);

	const attributesList = useMemo(() => {
		const {account, people} = selectedFields;

		return [
			{
				count: people,
				icon: 'users',
				onOpenModal: () => onOpenChangePeopleAttributes(true),
				title: Liferay.Language.get('people'),
			},
			{
				count: account,
				icon: 'briefcase',
				onOpenModal: () => onOpenChangeAccountsAttributes(true),
				title: Liferay.Language.get('account'),
			},
		];
	}, [
		onOpenChangeAccountsAttributes,
		onOpenChangePeopleAttributes,
		selectedFields,
	]);

	return (
		<>
			<ClayList className="mb-0">
				{attributesList.map(({count, icon, onOpenModal, title}) => (
					<ClayList.Item
						className="align-items-center"
						flex
						key={title}
						role={title}
					>
						<ClayList.ItemField className="mr-2">
							<ClayIcon symbol={icon} />
						</ClayList.ItemField>

						<ClayList.ItemField expand>
							<ClayList.ItemTitle>{title}</ClayList.ItemTitle>

							<ClayList.ItemText className="mr-1 text-secondary">
								{count} {Liferay.Language.get('selected')}
							</ClayList.ItemText>
						</ClayList.ItemField>

						<ClayList.ItemField>
							<ClayButton
								displayType="secondary"
								onClick={onOpenModal}
							>
								{Liferay.Language.get('select-attributes')}
							</ClayButton>
						</ClayList.ItemField>
					</ClayList.Item>
				))}
			</ClayList>

			{openAccountsAttributes && (
				<Modal
					observer={observerAccountsAttributes}
					onCancel={() => onOpenChangeAccountsAttributes(false)}
					onSubmit={(items) =>
						handleSubmit({
							closeFn: onOpenChangeAccountsAttributes,
							items,
							key: EFields.Account,
							updateFn: updateAccountsFields,
						})
					}
					requestFn={fetchAccountsFields}
					title={Liferay.Language.get('sync-account-attributes')}
				/>
			)}

			{openPeopleAttributes && (
				<Modal
					observer={observerPeopleAttributes}
					onCancel={() => onOpenChangePeopleAttributes(false)}
					onSubmit={(items) =>
						handleSubmit({
							closeFn: onOpenChangePeopleAttributes,
							items,
							key: EFields.People,
							updateFn: updatePeopleFields,
						})
					}
					requestFn={fetchPeopleFields}
					title={Liferay.Language.get('sync-people-attributes')}
				/>
			)}
		</>
	);
};

function getFields(items: TFormattedItems): TRawItem[] {
	return Object.values(items).map(
		({
			checked,
			columns: [
				{value: name},
				{value: type},
				{value: example},
				{value: source},
			],
			disabled,
		}) => {
			return {
				example: example as string,
				name: name as string,
				required: !!disabled,
				selected: !!checked,
				source: source as string,
				type: type as string,
			};
		}
	);
}

export default Attributes;
