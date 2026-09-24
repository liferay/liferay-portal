/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import SelectedItemsBar from '../components/SelectedItemsBar';
import {RequestResult} from '../services/ApiHelper';
import {getProfileTools} from '../services/getProfileTools';
import {getToolSetTools} from '../services/getToolSetTools';
import {getToolSets} from '../services/getToolSets';
import {postProfileTool} from '../services/postProfileTool';
import {ProfileTool, ToolSet, ToolSummary, ToolTreeItem} from '../types';
import {
	buildToolChildren,
	buildToolWaves,
	getAvailableToolSets,
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

	const toolsCacheRef = useRef<
		Record<string, Promise<RequestResult<ToolSummary[]>>>
	>({});

	const loadToolSet = useCallback((toolSetName: string) => {
		if (!toolsCacheRef.current[toolSetName]) {
			toolsCacheRef.current[toolSetName] = getToolSetTools(
				toolSetName
			).then((result) => {
				if (result.data) {
					return result;
				}

				delete toolsCacheRef.current[toolSetName];

				return {
					data: null,
					error:
						result.error ||
						Liferay.Language.get('an-unexpected-error-occurred'),
				};
			});
		}

		return toolsCacheRef.current[toolSetName];
	}, []);

	const loadToolsByToolSetName = useCallback(
		async (toolSetNames: string[]) => {
			const toolsByToolSetName = new Map<string, ToolSummary[]>();

			await Promise.all(
				toolSetNames.map(async (toolSetName) => {
					const {data: tools} = await loadToolSet(toolSetName);

					if (tools) {
						toolsByToolSetName.set(toolSetName, tools);
					}
				})
			);

			return toolsByToolSetName;
		},
		[loadToolSet]
	);

	useEffect(() => {
		let isMounted = true;

		const loadAvailableToolSets = async () => {
			const [toolSetsResult, profileToolsResult] = await Promise.all([
				getToolSets(),
				getProfileTools(profileERC),
			]);

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

			const loadedProfileTools = profileToolsResult.data.items;

			const listedToolSetNames = new Set(
				toolSetsResult.data.map((toolSet) => toolSet.name)
			);

			const toolsByToolSetName = await loadToolsByToolSetName(
				[
					...new Set(
						loadedProfileTools.map(
							(profileTool) => profileTool.toolSetName
						)
					),
				].filter((toolSetName) => listedToolSetNames.has(toolSetName))
			);

			if (!isMounted) {
				return;
			}

			setProfileTools(loadedProfileTools);
			setToolSets(
				getAvailableToolSets(
					toolSetsResult.data,
					loadedProfileTools,
					toolsByToolSetName
				)
			);

			setLoading(false);
		};

		loadAvailableToolSets();

		return () => {
			isMounted = false;
		};
	}, [loadToolsByToolSetName, onClose, profileERC]);

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

	const selectToolSetChildren = (
		item: ToolTreeItem,
		children: ToolTreeItem[]
	) => {
		setSelectedKeys((previousKeys) => {
			if (!previousKeys.has(item.id)) {
				return previousKeys;
			}

			return new Set([
				...previousKeys,
				...children.map((child) => child.id),
			]);
		});
	};

	const onLoadMore = async (item: ToolTreeItem) => {
		if (!toolSets.some((toolSet) => toolSet.name === item.id)) {
			return;
		}

		const {data: tools, error} = await loadToolSet(item.name);

		if (!tools) {
			openErrorToast(error);

			return;
		}

		const children = buildToolChildren(item.name, tools, profileTools);

		if (!children.length) {
			return;
		}

		selectToolSetChildren(item, children);

		return children;
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

		loadToolSet(item.name).then(({data: tools, error}) => {
			stopSelectingToolSet(item.name);

			if (!tools) {
				openErrorToast(error);

				return;
			}

			selectToolSetChildren(
				item,
				buildToolChildren(item.name, tools, profileTools)
			);
		});
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
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-tools')}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				{loading ? (
					<ClayLoadingIndicator />
				) : (
					<>
						<div className="sticky-top">
							<SelectedItemsBar
								count={selectedTools.length}
								onDeselectAll={() => setSelectedKeys(new Set())}
							/>
						</div>

						<div className="px-4 py-2">
							{initialItems.length ? (
								<TreeView
									className="bg-transparent"
									defaultItems={initialItems}
									nestedKey="children"
									onLoadMore={onLoadMore}
									onSelectionChange={setSelectedKeys}
									selectedKeys={selectedKeys}
									selectionMode="multiple-recursive"
									showExpanderOnHover={false}
								>
									{(item: ToolTreeItem) =>
										item.children ? (
											<TreeView.Item>
												<TreeView.ItemStack
													expandOnClick={false}
												>
													<ClayCheckbox
														aria-label={item.name}
														checked
													/>

													<span className="font-weight-normal pl-1 text-3">
														{item.name}
													</span>
												</TreeView.ItemStack>

												<TreeView.Group
													items={item.children}
												>
													{(child: ToolTreeItem) => (
														<TreeView.Item>
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
													)}
												</TreeView.Group>
											</TreeView.Item>
										) : (
											<TreeView.Item
												expandable
												onClick={(event) => {
													event.preventDefault();

													toggleCollapsedToolSet(
														item
													);
												}}
												onKeyDown={(event) => {
													if (
														event.key === 'Enter' ||
														event.key === ' '
													) {
														event.preventDefault();

														toggleCollapsedToolSet(
															item
														);
													}
												}}
											>
												<span
													id={`${item.id}-checkbox`}
												>
													<ClayCheckbox
														aria-label={item.name}
														checked={selectedKeys.has(
															item.id
														)}
														onChange={() =>
															toggleCollapsedToolSet(
																item
															)
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
														id={`${item.id}-loading`}
														size="sm"
													/>
												)}
											</TreeView.Item>
										)
									}
								</TreeView>
							) : (
								<div className="align-items-center d-flex justify-content-center py-4">
									<p className="text-secondary" role="status">
										{Liferay.Language.get(
											'no-tools-were-found'
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
		</>
	);
}
