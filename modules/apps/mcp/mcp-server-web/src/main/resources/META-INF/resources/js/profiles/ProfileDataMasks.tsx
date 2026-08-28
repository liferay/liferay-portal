/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useCallback, useEffect, useState} from 'react';

import OrderableTable from '../components/OrderableTable';
import {getDataMasks} from '../services/getDataMasks';
import {getProfileDataMasks} from '../services/getProfileDataMasks';
import {patchProfileDataMask} from '../services/patchProfileDataMask';
import {DataMask, ProfileDataMaskRow} from '../types';
import {openErrorToast} from '../utils';
import AddDataMasksModal from './AddDataMasksModal';
import RemoveDataMaskModal from './RemoveDataMaskModal';

interface ProfileDataMasksProps {
	profileERC: string;
}

export default function ProfileDataMasks({profileERC}: ProfileDataMasksProps) {
	const [dataMasks, setDataMasks] = useState<DataMask[]>([]);
	const [loading, setLoading] = useState(true);
	const [rows, setRows] = useState<ProfileDataMaskRow[]>([]);
	const [rowToRemove, setRowToRemove] = useState<ProfileDataMaskRow | null>(
		null
	);
	const [showAddModal, setShowAddModal] = useState(false);

	const loadRows = useCallback(async () => {
		const [associationsResult, dataMasksResult] = await Promise.all([
			getProfileDataMasks({
				mcpServerProfileExternalReferenceCode: profileERC,
			}),
			getDataMasks(),
		]);

		const error = associationsResult.error || dataMasksResult.error;

		if (error) {
			openErrorToast(error);

			setLoading(false);

			return;
		}

		const masks = dataMasksResult.data?.items ?? [];

		const masksByExternalReferenceCode = new Map(
			masks.map((mask) => [mask.externalReferenceCode, mask])
		);

		setDataMasks(masks);
		setRows(
			(associationsResult.data?.items ?? []).flatMap(
				(association, index) => {
					const mask = masksByExternalReferenceCode.get(
						association.dataMaskExternalReferenceCode
					);

					if (
						!mask ||
						!association.externalReferenceCode ||
						!association.id
					) {
						return [];
					}

					return [
						{
							dataMaskExternalReferenceCode:
								association.dataMaskExternalReferenceCode,
							description: mask.description ?? '',
							executionOrder: association.executionOrder ?? index,
							externalReferenceCode:
								association.externalReferenceCode,
							id: association.id,
							name: mask.name,
							type: mask.maskType?.name ?? '',
						},
					];
				}
			)
		);
		setLoading(false);
	}, [profileERC]);

	useEffect(() => {
		loadRows();
	}, [loadRows]);

	const onOrderChange = async ({order}: {order: string}) => {
		const rowsByExternalReferenceCode = new Map(
			rows.map((row) => [row.externalReferenceCode, row])
		);

		const orderedRows = order
			.split(',')
			.flatMap((externalReferenceCode, index) => {
				const row = rowsByExternalReferenceCode.get(
					externalReferenceCode
				);

				return row ? [{...row, executionOrder: index}] : [];
			});

		const results = await Promise.all(
			orderedRows.map((row) => {
				const previousRow = rowsByExternalReferenceCode.get(
					row.externalReferenceCode
				);

				if (previousRow?.executionOrder === row.executionOrder) {
					return Promise.resolve({error: undefined});
				}

				return patchProfileDataMask(row.id, {
					executionOrder: row.executionOrder,
				});
			})
		);

		const failed = results.find((result) => result.error);

		if (failed?.error) {
			openErrorToast(failed.error);

			loadRows();

			return;
		}

		setRows(orderedRows);
	};

	if (loading) {
		return (
			<div className="align-items-center d-flex justify-content-center mt-4">
				<ClayLoadingIndicator />
			</div>
		);
	}

	return (
		<div className="cadmin fds-admin">
			<OrderableTable
				actions={[
					{
						icon: 'trash',
						label: Liferay.Language.get('remove'),
						onClick: ({item}: {item: ProfileDataMaskRow}) =>
							setRowToRemove(item),
					},
				]}
				creationMenuItems={[
					{
						label: Liferay.Language.get('add-masks'),
						onClick: () => setShowAddModal(true),
					},
				]}
				creationMenuLabel={Liferay.Language.get('add-masks')}
				fields={[
					{label: Liferay.Language.get('name'), name: 'name'},
					{label: Liferay.Language.get('type'), name: 'type'},
					{
						label: Liferay.Language.get('description'),
						name: 'description',
					},
				]}
				items={rows}
				noItemsButtonLabel={Liferay.Language.get('add-masks')}
				noItemsDescription={Liferay.Language.get(
					'this-profile-has-no-data-masks-yet'
				)}
				noItemsTitle={Liferay.Language.get('no-data-masks')}
				onOrderChange={onOrderChange}
			/>

			{rowToRemove && (
				<RemoveDataMaskModal
					onClose={() => setRowToRemove(null)}
					onRemoved={loadRows}
					row={rowToRemove}
				/>
			)}

			{showAddModal && (
				<AddDataMasksModal
					dataMasks={dataMasks.filter(
						(mask) =>
							!rows.some(
								(row) =>
									row.dataMaskExternalReferenceCode ===
									mask.externalReferenceCode
							)
					)}
					nextExecutionOrder={
						rows.reduce(
							(maxExecutionOrder, row) =>
								Math.max(maxExecutionOrder, row.executionOrder),
							0
						) + 1
					}
					onAdded={loadRows}
					onClose={() => setShowAddModal(false)}
					profileExternalReferenceCode={profileERC}
				/>
			)}
		</div>
	);
}
