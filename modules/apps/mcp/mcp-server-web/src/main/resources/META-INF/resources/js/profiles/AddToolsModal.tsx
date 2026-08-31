/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayResultsBar} from '@clayui/management-toolbar';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {getProfileTools} from '../services/getProfileTools';
import {getToolSetTools} from '../services/getToolSetTools';
import {getToolSets} from '../services/getToolSets';
import {postProfileTool} from '../services/postProfileTool';
import {ProfileTool, ToolSet, ToolSummary, ToolTreeItem} from '../types';
import {
	buildToolChildren,
	buildToolWaves,
	getAssignedToolIds,
	getEligibleToolIds,
	getSelectedTools,
	openErrorToast,
	openSuccessToast,
} from '../utils';

interface AddToolsModalProps {
	onAdded: () => void;
	onClose: () => void;
	profileERC: string;
}

export default function AddToolsModal({
	onAdded,
	onClose,
	profileERC,
}: AddToolsModalProps) {
	const [loading, setLoading] = useState(true);
	const [profileTools, setProfileTools] = useState<ProfileTool[]>([]);
	const [toolSets, setToolSets] = useState<ToolSet[]>([]);

	const [saving, setSaving] = useState(false);
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(new Set());
	const [selectingToolSetNames, setSelectingToolSetNames] = useState<
		Set<string>
	>(new Set());

	const selectedKeysRef = useRef(selectedKeys);

	useEffect(() => {
		selectedKeysRef.current = selectedKeys;
	}, [selectedKeys]);

	const toolsCacheRef = useRef<Record<string, Promise<ToolSummary[] | null>>>(
		{}
	);

	const {observer} = useModal({onClose});

	const assignedToolIds = useMemo(
		() => getAssignedToolIds(profileTools),
		[profileTools]
	);

	const assignedToolSetNames = useMemo(
		() =>
			new Set(profileTools.map((profileTool) => profileTool.toolSetName)),
		[profileTools]
	);

	const loadToolSet = useCallback((toolSetName: string) => {
		if (!toolsCacheRef.current[toolSetName]) {
			toolsCacheRef.current[toolSetName] = getToolSetTools(
				toolSetName
			).then(({data, error}) => {
				if (error || !data) {
					delete toolsCacheRef.current[toolSetName];

					openErrorToast(
						error ||
							Liferay.Language.get('an-unexpected-error-occurred')
					);

					return null;
				}

				return data;
			});
		}

		return toolsCacheRef.current[toolSetName];
	}, []);

	useEffect(() => {
		let isMounted = true;

		Promise.all([getToolSets(), getProfileTools(profileERC)]).then(
			([toolSetsResult, profileToolsResult]) => {
				if (!isMounted) {
					return;
				}

				if (
					toolSetsResult.error ||
					!toolSetsResult.data ||
					profileToolsResult.error ||
					!profileToolsResult.data
				) {
					openErrorToast(
						toolSetsResult.error ||
							profileToolsResult.error ||
							Liferay.Language.get('an-unexpected-error-occurred')
					);

					onClose();

					return;
				}

				setProfileTools(profileToolsResult.data.items);
				setToolSets(toolSetsResult.data);

				setLoading(false);
			}
		);

		return () => {
			isMounted = false;
		};
	}, [onClose, profileERC]);

	const initialItems = useMemo(
		() =>
			toolSets.map((toolSet) => ({
				id: toolSet.name,
				name: toolSet.name,
			})),
		[toolSets]
	);

	const selectedTools = getSelectedTools(
		selectedKeys as Set<string | number>
	);

	const onLoadMore = async (item: ToolTreeItem) => {
		if (!toolSets.some((toolSet) => toolSet.name === item.id)) {
			return;
		}

		const tools = await loadToolSet(item.name);

		if (!tools) {
			return;
		}

		const children = buildToolChildren(item.name, tools, profileTools);

		if (selectedKeysRef.current.has(item.id)) {
			const eligibleToolIds = getEligibleToolIds(children);

			setSelectedKeys(
				(previousKeys) => new Set([...previousKeys, ...eligibleToolIds])
			);
		}

		return children;
	};

	const getAssignedToolSetState = (item: ToolTreeItem) => {
		const eligibleToolIds = getEligibleToolIds(item.children ?? []);

		const selectedCount = eligibleToolIds.filter((eligibleToolId) =>
			selectedKeys.has(eligibleToolId)
		).length;

		const checked =
			!eligibleToolIds.length || selectedCount === eligibleToolIds.length;

		return {
			checked,
			disabled: !eligibleToolIds.length,
			indeterminate: !checked,
		};
	};

	const toggleAssignedToolSet = (item: ToolTreeItem) => {
		const eligibleToolIds = getEligibleToolIds(item.children ?? []);

		setSelectedKeys((previousKeys) => {
			const keys = new Set(previousKeys);

			if (
				eligibleToolIds.length &&
				eligibleToolIds.every((eligibleToolId) =>
					keys.has(eligibleToolId)
				)
			) {
				eligibleToolIds.forEach((eligibleToolId) =>
					keys.delete(eligibleToolId)
				);
			}
			else {
				eligibleToolIds.forEach((eligibleToolId) =>
					keys.add(eligibleToolId)
				);
			}

			return keys;
		});
	};

	const stopSelectingToolSet = (toolSetName: string) => {
		setSelectingToolSetNames(
			(previousNames) =>
				new Set(
					[...previousNames].filter((name) => name !== toolSetName)
				)
		);
	};

	const toggleCollapsedToolSet = (item: ToolTreeItem) => {
		if (selectingToolSetNames.has(item.name)) {
			return;
		}

		if (selectedKeys.has(item.id)) {
			setSelectedKeys(
				(previousKeys) =>
					new Set(
						[...previousKeys].filter(
							(key) =>
								key !== item.id &&
								!String(key).startsWith(`${item.name}/`)
						)
					)
			);
			stopSelectingToolSet(item.name);

			return;
		}

		setSelectedKeys((previousKeys) => new Set(previousKeys).add(item.id));
		setSelectingToolSetNames((previousNames) =>
			new Set(previousNames).add(item.name)
		);

		loadToolSet(item.name).then((tools) => {
			stopSelectingToolSet(item.name);

			if (!tools || !selectedKeysRef.current.has(item.id)) {
				return;
			}

			const eligibleToolIds = getEligibleToolIds(
				buildToolChildren(item.name, tools, profileTools)
			);

			setSelectedKeys(
				(previousKeys) => new Set([...previousKeys, ...eligibleToolIds])
			);
		});
	};

	const onSelectionChange = (keys: Set<React.Key>) => {
		setSelectedKeys(
			new Set(
				[...keys].filter((key) => !assignedToolIds.has(String(key)))
			)
		);
	};

	const addSelected = async () => {
		setSaving(true);

		const results = [];

		for (const wave of buildToolWaves(selectedTools)) {
			results.push(
				...(await Promise.all(
					wave.map((selectedTool) =>
						postProfileTool({
							r_mcpServerProfileToTools_l_mcpServerProfileERC:
								profileERC,
							toolName: selectedTool.toolName,
							toolSetName: selectedTool.toolSetName,
						})
					)
				))
			);
		}

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

		openSuccessToast(
			results.length === 1
				? Liferay.Language.get('the-tool-was-successfully-added')
				: Liferay.Util.sub(
						Liferay.Language.get('x-tools-were-successfully-added'),
						String(results.length)
					)
		);

		onAdded();
		onClose();
	};

	return (
		<ClayModal className="modal-height-full" observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-tools')}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				<div className="sticky-top">
					<ClayResultsBar>
						<ClayResultsBar.Item expand>
							<span
								className="component-text text-truncate-inline"
								role="status"
							>
								<span className="text-truncate">
									{selectedTools.length}
									&nbsp;
									{selectedTools.length === 1
										? Liferay.Language.get('item-selected')
										: Liferay.Language.get(
												'items-selected'
											)}
								</span>
							</span>
						</ClayResultsBar.Item>

						{!!selectedTools.length && (
							<ClayResultsBar.Item>
								<ClayButton
									className="component-link tbar-link"
									displayType="unstyled"
									onClick={() => setSelectedKeys(new Set())}
								>
									{Liferay.Language.get('deselect-all')}
								</ClayButton>
							</ClayResultsBar.Item>
						)}
					</ClayResultsBar>
				</div>

				<div className="cadmin container-fluid container-fluid-max-xl px-4 py-2">
					{loading ? (
						<div className="align-items-center d-flex justify-content-center py-4">
							<ClayLoadingIndicator />
						</div>
					) : null}

					{!loading && !!initialItems.length ? (
						<TreeView
							className="bg-transparent"
							defaultItems={initialItems}
							nestedKey="children"
							onLoadMore={onLoadMore}
							onSelectionChange={onSelectionChange}
							selectedKeys={selectedKeys}
							selectionMode="multiple-recursive"
							showExpanderOnHover={false}
						>
							{(item: ToolTreeItem) =>
								item.children ? (
									<TreeView.Item>
										<TreeView.ItemStack
											disabled={item.children.every(
												(child) => child.assigned
											)}
											expandOnClick={false}
											expanderDisabled={false}
											onClick={(event) =>
												event.preventDefault()
											}
										>
											{item.children.some(
												(child) => child.assigned
											) ? (
												<span>
													<ClayCheckbox
														aria-label={item.name}
														onChange={() =>
															toggleAssignedToolSet(
																item
															)
														}
														onClick={(event) =>
															event.stopPropagation()
														}
														{...getAssignedToolSetState(
															item
														)}
													/>
												</span>
											) : (
												<ClayCheckbox
													aria-label={item.name}
													checked
												/>
											)}

											<span className="font-weight-normal pl-1 text-3">
												{item.name}
											</span>
										</TreeView.ItemStack>

										<TreeView.Group items={item.children}>
											{(child: ToolTreeItem) =>
												child.assigned ? (
													<TreeView.Item disabled>
														<span>
															<ClayCheckbox
																aria-label={
																	child.name
																}
																checked
																disabled
																onChange={() => {}}
															/>
														</span>

														<span className="font-weight-normal pl-1 text-3">
															{child.name}
														</span>
													</TreeView.Item>
												) : (
													<TreeView.Item
														onClick={(event) =>
															event.preventDefault()
														}
													>
														<ClayCheckbox
															aria-label={
																child.name
															}
															checked
														/>

														<span className="font-weight-normal pl-1 text-3">
															{child.name}
														</span>
													</TreeView.Item>
												)
											}
										</TreeView.Group>
									</TreeView.Item>
								) : assignedToolSetNames.has(item.name) ? (
									<TreeView.Item
										expandable
										onClick={(event) =>
											event.preventDefault()
										}
									>
										<span>
											<ClayCheckbox
												aria-label={item.name}
												checked={false}
												disabled
												indeterminate
												onChange={() => {}}
											/>
										</span>

										<span className="font-weight-normal pl-1 text-3">
											{item.name}
										</span>
									</TreeView.Item>
								) : (
									<TreeView.Item
										expandable
										onClick={(event) =>
											event.preventDefault()
										}
									>
										<span>
											<ClayCheckbox
												aria-label={item.name}
												checked={selectedKeys.has(
													item.id
												)}
												onChange={() =>
													toggleCollapsedToolSet(item)
												}
												onClick={(event) =>
													event.stopPropagation()
												}
											/>
										</span>

										<span className="font-weight-normal pl-1 text-3">
											{item.name}
										</span>

										{selectingToolSetNames.has(
											item.name
										) && (
											<ClayLoadingIndicator
												className="mb-0 ml-2 mt-0"
												displayType="secondary"
												size="sm"
											/>
										)}
									</TreeView.Item>
								)
							}
						</TreeView>
					) : null}

					{!loading && !initialItems.length ? (
						<p className="text-secondary">
							{Liferay.Language.get('no-tools-were-found')}
						</p>
					) : null}
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={saving}
							displayType="secondary"
							onClick={onClose}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							aria-busy={saving}
							disabled={!selectedTools.length || saving}
							loading={saving}
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
