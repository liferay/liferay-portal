/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayManagementToolbar from '@clayui/management-toolbar';
import ClayModal from '@clayui/modal';
import React, {useMemo, useState} from 'react';

import AutoSearch from '../components/AutoSearch';
import Highlight from '../components/Highlight';
import SelectedItemsBar from '../components/SelectedItemsBar';
import {postProfileDataMask} from '../services/postProfileDataMask';
import {DataMask, DataMaskTreeItem} from '../types';
import {
	buildDataMaskTree,
	filterDataMaskTree,
	getSelectedDataMaskExternalReferenceCodes,
	openErrorToast,
	openSuccessToast,
} from '../utils';

interface AddDataMasksModalProps {
	dataMasks: DataMask[];
	nextExecutionOrder: number;
	onAdded: () => void;
	onClose: () => void;
	profileExternalReferenceCode: string;
}

export default function AddDataMasksModal({
	dataMasks,
	nextExecutionOrder,
	onAdded,
	onClose,
	profileExternalReferenceCode,
}: AddDataMasksModalProps) {
	const tree = useMemo(() => buildDataMaskTree(dataMasks), [dataMasks]);

	const [expandedKeys, setExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(tree.map((group) => group.id))
	);
	const [query, setQuery] = useState('');
	const [saving, setSaving] = useState(false);
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(new Set());

	const items = useMemo(
		() => filterDataMaskTree(tree, query).items,
		[query, tree]
	);

	const selectedExternalReferenceCodes =
		getSelectedDataMaskExternalReferenceCodes(
			tree,
			selectedKeys as Set<string | number>
		);

	const onSearch = (value: string) => {
		setQuery(value);

		setExpandedKeys(new Set(filterDataMaskTree(tree, value).expandedKeys));
	};

	const addSelected = async () => {
		setSaving(true);

		const results = await Promise.all(
			selectedExternalReferenceCodes.map(
				(dataMaskExternalReferenceCode, index) =>
					postProfileDataMask({
						dataMaskExternalReferenceCode,
						executionOrder: nextExecutionOrder + index,
						mcpServerProfileExternalReferenceCode:
							profileExternalReferenceCode,
					})
			)
		);

		setSaving(false);

		const failed = results.filter((result) => result.error);

		if (failed.length) {
			const errorMessages = [
				...new Set(failed.map((result) => result.error as string)),
			];

			openErrorToast(
				errorMessages
					.map((errorMessage) =>
						Liferay.Util.escapeHTML(errorMessage)
					)
					.join('<br>'),
				{dangerouslySetMessageHTML: true}
			);

			if (failed.length < results.length) {
				onAdded();
				onClose();
			}

			return;
		}

		openSuccessToast(Liferay.Language.get('masks-were-successfully-added'));

		onAdded();
		onClose();
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-masks')}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				<div className="sticky-top">
					<ClayManagementToolbar>
						<ClayManagementToolbar.Search
							onSubmit={(event) => event.preventDefault()}
						>
							<AutoSearch onSearch={onSearch} query={query} />
						</ClayManagementToolbar.Search>
					</ClayManagementToolbar>

					<SelectedItemsBar
						count={selectedExternalReferenceCodes.length}
						onDeselectAll={() => setSelectedKeys(new Set())}
					/>
				</div>

				<div className="px-4 py-2">
					{items.length ? (
						<TreeView
							className="bg-transparent"
							expandedKeys={expandedKeys}
							items={items}
							nestedKey="children"
							onExpandedChange={setExpandedKeys}
							onSelectionChange={setSelectedKeys}
							selectedKeys={selectedKeys}
							selectionMode="multiple-recursive"
							showExpanderOnHover={false}
						>
							{(item: DataMaskTreeItem) => (
								<TreeView.Item>
									<TreeView.ItemStack expandOnClick={false}>
										<ClayCheckbox
											aria-label={item.name}
											checked
										/>

										<span className="font-weight-normal pl-1 text-3">
											<Highlight
												query={query}
												text={item.name}
											/>
										</span>
									</TreeView.ItemStack>

									<TreeView.Group items={item.children}>
										{(child: DataMaskTreeItem) => (
											<TreeView.Item>
												<ClayCheckbox
													aria-label={child.name}
													checked
												/>

												<span className="font-weight-normal pl-1 text-3">
													<Highlight
														query={query}
														text={child.name}
													/>
												</span>
											</TreeView.Item>
										)}
									</TreeView.Group>
								</TreeView.Item>
							)}
						</TreeView>
					) : (
						<div className="align-items-center d-flex justify-content-center py-4">
							<p className="text-secondary" role="status">
								{Liferay.Language.get(
									'no-data-masks-were-found'
								)}
							</p>
						</div>
					)}
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={onClose}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={
								!selectedExternalReferenceCodes.length || saving
							}
							onClick={addSelected}
							type="button"
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
