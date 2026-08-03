/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayManagementToolbar, {
	ClayResultsBar,
} from '@clayui/management-toolbar';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useMemo, useState} from 'react';

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

function Highlight({query, text}: {query: string; text: string}) {
	const index = query ? text.toLowerCase().indexOf(query.toLowerCase()) : -1;

	if (index < 0) {
		return <span className="font-weight-normal pl-1 text-3">{text}</span>;
	}

	return (
		<span className="font-weight-normal pl-1 text-3">
			{text.substring(0, index)}

			<mark className="bg-transparent border-0 font-weight-bold p-0 shadow-none">
				{text.substring(index, index + query.length)}
			</mark>

			{text.substring(index + query.length)}
		</span>
	);
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

	const {observer} = useModal({onClose});

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

		openSuccessToast(Liferay.Language.get('masks-were-added-successfully'));

		onAdded();
		onClose();
	};

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-masks')}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				<ClayManagementToolbar>
					<ClayManagementToolbar.Search
						onSubmit={(event) => event.preventDefault()}
					>
						<ClayInput.Group>
							<ClayInput.GroupItem>
								<ClayInput.GroupInsetItem before tag="span">
									<ClayIcon
										className="inline-item inline-item-before"
										focusable="false"
										role="presentation"
										symbol="search"
									/>
								</ClayInput.GroupInsetItem>

								<ClayInput
									aria-label={Liferay.Language.get('search')}
									insetAfter={!!query}
									insetBefore
									onChange={(event) =>
										onSearch(event.target.value)
									}
									placeholder={Liferay.Language.get('search')}
									type="text"
									value={query}
								/>

								{query && (
									<ClayInput.GroupInsetItem after tag="span">
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'clear-search'
											)}
											borderless
											displayType="secondary"
											monospaced={false}
											onClick={() => onSearch('')}
											size="sm"
											symbol="times"
											title={Liferay.Language.get(
												'clear-search'
											)}
										/>
									</ClayInput.GroupInsetItem>
								)}
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayManagementToolbar.Search>
				</ClayManagementToolbar>

				{!!selectedExternalReferenceCodes.length && (
					<ClayResultsBar>
						<ClayResultsBar.Item expand>
							<span
								className="component-text text-truncate-inline"
								role="status"
							>
								<span className="text-truncate">
									{selectedExternalReferenceCodes.length}
									&nbsp;
									{selectedExternalReferenceCodes.length === 1
										? Liferay.Language.get('item-selected')
										: Liferay.Language.get(
												'items-selected'
											)}
								</span>
							</span>
						</ClayResultsBar.Item>

						<ClayResultsBar.Item>
							<ClayButton
								className="component-link tbar-link"
								displayType="unstyled"
								onClick={() => setSelectedKeys(new Set())}
							>
								{Liferay.Language.get('deselect-all')}
							</ClayButton>
						</ClayResultsBar.Item>
					</ClayResultsBar>
				)}

				<div className="cadmin container-fluid container-fluid-max-xl px-4 py-2">
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

										<Highlight
											query={query}
											text={item.name}
										/>
									</TreeView.ItemStack>

									<TreeView.Group items={item.children}>
										{(child: DataMaskTreeItem) => (
											<TreeView.Item>
												<ClayCheckbox
													aria-label={child.name}
													checked
												/>

												<Highlight
													query={query}
													text={child.name}
												/>
											</TreeView.Item>
										)}
									</TreeView.Group>
								</TreeView.Item>
							)}
						</TreeView>
					) : (
						<p className="text-secondary">
							{Liferay.Language.get('no-data-masks-were-found')}
						</p>
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
		</ClayModal>
	);
}
