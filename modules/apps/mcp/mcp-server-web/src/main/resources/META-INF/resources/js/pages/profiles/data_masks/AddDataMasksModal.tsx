/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayManagementToolbar from '@clayui/management-toolbar';
import ClayModal from '@clayui/modal';
import {escapeHTML} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import AutoSearch from '../../../components/AutoSearch';
import CountBadge from '../../../components/CountBadge';
import FilterResultsStatus from '../../../components/FilterResultsStatus';
import Highlight from '../../../components/Highlight';
import SelectedItemsBar from '../../../components/SelectedItemsBar';
import useTreeFilter from '../../../hooks/useTreeFilter';
import ProfileDataMaskService from '../../../services/ProfileDataMaskService';
import {DataMask} from '../../../types/DataMask';
import {openErrorToast, openSuccessToast} from '../../../utils/toasts';
import {
	DataMaskTreeItem,
	buildDataMaskTree,
	filterDataMaskTree,
	getSelectedDataMaskExternalReferenceCodes,
} from './dataMaskSelection';

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
	const [saving, setSaving] = useState(false);
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(new Set());

	const {getRowClassName, matchCount, onSearch, query, trimmedQuery} =
		useTreeFilter({
			expandedKeys,
			filter: filterDataMaskTree,
			items: tree,
			onExpandedChange: setExpandedKeys,
		});

	const selectedExternalReferenceCodes =
		getSelectedDataMaskExternalReferenceCodes(
			tree,
			selectedKeys as Set<string | number>
		);

	const addSelected = async () => {
		setSaving(true);

		const results = await Promise.all(
			selectedExternalReferenceCodes.map(
				(dataMaskExternalReferenceCode, index) =>
					ProfileDataMaskService.postProfileDataMask({
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
					.map((errorMessage) => escapeHTML(errorMessage))
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
				{!!tree.length && (
					<div className="sticky-top">
						<ClayManagementToolbar role="none">
							<AutoSearch onSearch={onSearch} query={query} />
						</ClayManagementToolbar>

						<SelectedItemsBar
							count={selectedExternalReferenceCodes.length}
							onClear={() => setSelectedKeys(new Set())}
						/>
					</div>
				)}

				<div className="px-4 py-2">
					{tree.length ? (
						<>
							<FilterResultsStatus
								matchCount={matchCount}
								query={trimmedQuery}
							/>

							<TreeView
								aria-label={Liferay.Language.get('data-masks')}
								className="bg-transparent"
								defaultItems={tree}
								expandedKeys={expandedKeys}
								key={trimmedQuery}
								nestedKey="children"
								onExpandedChange={setExpandedKeys}
								onSelectionChange={setSelectedKeys}
								selectedKeys={selectedKeys}
								selectionMode="multiple-recursive"
								showExpanderOnHover={false}
							>
								{(item: DataMaskTreeItem, selection) => {
									const countId = `${item.id}-count`;

									return (
										<TreeView.Item
											className={getRowClassName(item)}
										>
											<TreeView.ItemStack
												aria-describedby={countId}
												expandOnClick={false}
											>
												<ClayCheckbox
													aria-describedby={countId}
													aria-label={item.name}
													checked
												/>

												<span className="font-weight-normal pl-1 text-3">
													<Highlight
														query={trimmedQuery}
														text={item.name}
													/>
												</span>

												<CountBadge
													count={item.maskCount}
													id={countId}
													selected={selection.has(
														item.id
													)}
												/>
											</TreeView.ItemStack>

											<TreeView.Group
												items={item.children}
											>
												{(child: DataMaskTreeItem) => (
													<TreeView.Item
														className={getRowClassName(
															child
														)}
													>
														<ClayCheckbox
															aria-label={
																child.name
															}
															checked
														/>

														<span className="font-weight-normal pl-1 text-3">
															<Highlight
																query={
																	trimmedQuery
																}
																text={
																	child.name
																}
															/>
														</span>
													</TreeView.Item>
												)}
											</TreeView.Group>
										</TreeView.Item>
									);
								}}
							</TreeView>
						</>
					) : (
						<div className="align-items-center d-flex justify-content-center py-4">
							<p className="text-secondary">
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
							aria-busy={saving}
							disabled={
								!selectedExternalReferenceCodes.length || saving
							}
							loading={saving}
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
