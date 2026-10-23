/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayManagementToolbar from '@clayui/management-toolbar';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import AutoSearch from '../../../components/AutoSearch';
import CountBadge from '../../../components/CountBadge';
import FilterResultsStatus from '../../../components/FilterResultsStatus';
import Highlight from '../../../components/Highlight';
import SelectedItemsBar from '../../../components/SelectedItemsBar';
import useTreeFilter from '../../../hooks/useTreeFilter';
import ProfileToolService from '../../../services/ProfileToolService';
import ToolService from '../../../services/ToolService';
import {ProfileTool} from '../../../types/ProfileTool';
import {openErrorToast, openSuccessToast} from '../../../utils/toasts';
import {
	FieldTreeItem,
	buildFieldTree,
	filterFieldTree,
	getExpandedKeys,
	getSelectedKeys,
	toRestrictFields,
} from './fieldSelection';

interface RestrictFieldsModalProps {
	onClose: () => void;
	onSaved: () => void;
	profileTool: ProfileTool;
}

export default function RestrictFieldsModal({
	onClose,
	onSaved,
	profileTool,
}: RestrictFieldsModalProps) {
	const {externalReferenceCode, restrictFields, toolName, toolSetName} =
		profileTool;

	const [expandedKeys, setExpandedKeys] = useState<Set<React.Key>>(new Set());
	const [items, setItems] = useState<FieldTreeItem[]>([]);
	const [loading, setLoading] = useState(true);
	const [saving, setSaving] = useState(false);
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(new Set());
	const [treeVersion, setTreeVersion] = useState(0);

	const searchInputRef = useRef<HTMLInputElement>(null);
	const treeRef = useRef<HTMLDivElement>(null);

	const {
		getRowClassName,
		isVisible,
		matchCount,
		onSearch,
		query,
		trimmedQuery,
	} = useTreeFilter({
		expandedKeys,
		filter: filterFieldTree,
		items,
		onExpandedChange: setExpandedKeys,
		restoreExpansion: true,
	});

	useEffect(() => {
		if (treeVersion) {
			(
				Array.from(
					treeRef.current?.querySelectorAll<HTMLElement>(
						'[role="treeitem"]'
					) ?? []
				).find((element) => !element.closest('.d-none')) ??
				searchInputRef.current
			)?.focus();
		}
	}, [treeVersion]);

	useEffect(() => {
		let isMounted = true;

		ToolService.getTool(toolSetName, toolName).then(({data, error}) => {
			if (!isMounted) {
				return;
			}

			if (error) {
				openErrorToast(error);

				onClose();

				return;
			}

			const tree = buildFieldTree(data?.outputSchema);

			setExpandedKeys(getExpandedKeys(restrictFields));
			setItems(tree);
			setSelectedKeys(getSelectedKeys(tree, restrictFields));

			setLoading(false);
		});

		return () => {
			isMounted = false;
		};
	}, [onClose, restrictFields, toolName, toolSetName]);

	const clearSelection = () => {
		setSelectedKeys(new Set());

		setTreeVersion((previousVersion) => previousVersion + 1);
	};

	const save = async () => {
		setSaving(true);

		const {error} = await ProfileToolService.patchProfileTool(
			externalReferenceCode,
			{
				restrictFields: toRestrictFields(items, selectedKeys),
			}
		);

		setSaving(false);

		if (error) {
			openErrorToast(error);

			return;
		}

		openSuccessToast(Liferay.Language.get('successfully-saved'));

		onSaved();
		onClose();
	};

	const isExpandable = (item: FieldTreeItem) =>
		!!item.children?.length && item.children.some(isVisible);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(
					Liferay.Language.get('x-colon-y'),
					Liferay.Language.get('restrict-fields'),
					toolName
				)}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				{loading ? (
					<ClayLoadingIndicator />
				) : (
					<>
						{!!items.length && (
							<div className="sticky-top">
								<ClayManagementToolbar role="none">
									<AutoSearch
										label={Liferay.Language.get(
											'search-fields'
										)}
										onSearch={onSearch}
										query={query}
										ref={searchInputRef}
									/>
								</ClayManagementToolbar>

								<SelectedItemsBar
									count={selectedKeys.size}
									onClear={clearSelection}
								/>
							</div>
						)}

						<div className="px-4 py-2" ref={treeRef}>
							{items.length ? (
								<>
									<FilterResultsStatus
										matchCount={matchCount}
										query={trimmedQuery}
									/>

									<TreeView
										aria-label={Liferay.Language.get(
											'fields'
										)}
										className="bg-transparent"
										defaultItems={items}
										expandedKeys={expandedKeys}
										key={`${treeVersion}:${trimmedQuery}`}
										nestedKey="children"
										onExpandedChange={setExpandedKeys}
										onSelectionChange={setSelectedKeys}
										selectedKeys={selectedKeys}
										selectionMode="multiple-recursive"
										showExpanderOnHover={false}
									>
										{(item: FieldTreeItem, selection) => {
											const countId = `${item.id}-count`;

											return (
												<TreeView.Item
													className={getRowClassName(
														item
													)}
												>
													<TreeView.ItemStack
														aria-describedby={
															item.children
																? countId
																: undefined
														}
														expandOnClick={false}
														expanderDisabled={
															!isExpandable(item)
														}
														onKeyDown={(event) => {
															if (
																event.key ===
																	'ArrowRight' &&
																!isExpandable(
																	item
																)
															) {
																event.preventDefault();
															}
														}}
													>
														<ClayCheckbox
															aria-describedby={
																item.children
																	? countId
																	: undefined
															}
															checked
														/>

														<span className="font-weight-normal pl-1 text-3">
															<Highlight
																query={
																	trimmedQuery
																}
																text={item.name}
															/>
														</span>

														{item.children && (
															<CountBadge
																count={
																	item.fieldCount
																}
																id={countId}
																selected={selection.has(
																	item.id
																)}
															/>
														)}
													</TreeView.ItemStack>

													<TreeView.Group
														items={item.children}
													>
														{(
															child: FieldTreeItem
														) => (
															<TreeView.Item
																className={getRowClassName(
																	child
																)}
															>
																<ClayCheckbox
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
											'no-fields-were-found'
										)}
									</p>
								</div>
							)}
						</div>
					</>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							aria-busy={saving}
							disabled={loading || saving || !items.length}
							displayType="primary"
							loading={saving}
							onClick={save}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
