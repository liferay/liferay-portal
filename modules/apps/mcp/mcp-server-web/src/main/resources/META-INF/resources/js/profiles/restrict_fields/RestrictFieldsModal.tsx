/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import React, {useEffect, useRef, useState} from 'react';

import SelectedItemsBar from '../../components/SelectedItemsBar';
import {getTool} from '../../services/getTool';
import {patchProfileTool} from '../../services/patchProfileTool';
import {ProfileTool} from '../../types';
import {openErrorToast, openSuccessToast} from '../../utils';
import {FieldTreeItem} from './types';
import {
	buildFieldTree,
	getExpandedKeys,
	getSelectedKeys,
	toRestrictFields,
} from './utils';

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

	const treeRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if (treeVersion) {
			treeRef.current
				?.querySelector<HTMLElement>('[role="treeitem"]')
				?.focus();
		}
	}, [treeVersion]);

	useEffect(() => {
		let isMounted = true;

		getTool(toolSetName, toolName).then(({data, error}) => {
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

	const deselectAll = () => {
		setSelectedKeys(new Set());

		setTreeVersion((previousVersion) => previousVersion + 1);
	};

	const save = async () => {
		setSaving(true);

		const {error} = await patchProfileTool(externalReferenceCode, {
			restrictFields: toRestrictFields(items, selectedKeys),
		});

		setSaving(false);

		if (error) {
			openErrorToast(error);

			return;
		}

		openSuccessToast(Liferay.Language.get('successfully-saved'));

		onSaved();
		onClose();
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Util.sub(
					Liferay.Language.get('x-colon-y'),
					Liferay.Language.get('restrict-fields'),
					toolName
				)}
			</ClayModal.Header>

			<ClayModal.Body className="pt-0 px-0">
				{loading ? (
					<div className="align-items-center d-flex h-100 justify-content-center">
						<ClayLoadingIndicator />
					</div>
				) : (
					<>
						<div className="sticky-top">
							<SelectedItemsBar
								count={selectedKeys.size}
								onDeselectAll={deselectAll}
							/>
						</div>

						<div className="px-4 py-2" ref={treeRef}>
							{items.length ? (
								<TreeView
									className="bg-transparent"
									defaultItems={items}
									expandedKeys={expandedKeys}
									key={treeVersion}
									nestedKey="children"
									onExpandedChange={setExpandedKeys}
									onSelectionChange={setSelectedKeys}
									selectedKeys={selectedKeys}
									selectionMode="multiple-recursive"
									showExpanderOnHover={false}
								>
									{(item: FieldTreeItem) => (
										<TreeView.Item>
											<TreeView.ItemStack
												expandOnClick={false}
											>
												<ClayCheckbox checked />

												{item.name}
											</TreeView.ItemStack>

											<TreeView.Group
												items={item.children}
											>
												{(child: FieldTreeItem) => (
													<TreeView.Item>
														<ClayCheckbox checked />

														{child.name}
													</TreeView.Item>
												)}
											</TreeView.Group>
										</TreeView.Item>
									)}
								</TreeView>
							) : (
								<p className="text-secondary" role="status">
									{Liferay.Language.get(
										'no-fields-were-found'
									)}
								</p>
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
							disabled={loading || saving || !items.length}
							displayType="primary"
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
