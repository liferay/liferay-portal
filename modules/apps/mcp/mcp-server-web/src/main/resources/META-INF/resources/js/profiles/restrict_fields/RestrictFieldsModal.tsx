/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import React, {useEffect, useMemo, useState} from 'react';

import {getTool} from '../../services/getTool';
import {JSONSchema} from '../../types';
import {openErrorToast} from '../../utils';
import {FieldTreeItem} from './types';
import {buildFieldTree} from './utils';

interface RestrictFieldsModalProps {
	onClose: () => void;
	toolName: string;
	toolSetName: string;
}

export default function RestrictFieldsModal({
	onClose,
	toolName,
	toolSetName,
}: RestrictFieldsModalProps) {
	const [loading, setLoading] = useState(true);
	const [outputSchema, setOutputSchema] = useState<JSONSchema>();
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(new Set());

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

			setOutputSchema(data?.outputSchema);

			setLoading(false);
		});

		return () => {
			isMounted = false;
		};
	}, [onClose, toolName, toolSetName]);

	const items = useMemo(() => buildFieldTree(outputSchema), [outputSchema]);

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

			<ClayModal.Body>
				{loading ? (
					<div className="align-items-center d-flex justify-content-center py-4">
						<ClayLoadingIndicator />
					</div>
				) : items.length ? (
					<TreeView
						className="bg-transparent"
						defaultItems={items}
						nestedKey="children"
						onSelectionChange={setSelectedKeys}
						selectedKeys={selectedKeys}
						selectionMode="multiple-recursive"
						showExpanderOnHover={false}
					>
						{(item: FieldTreeItem) => (
							<TreeView.Item>
								<TreeView.ItemStack expandOnClick={false}>
									<ClayCheckbox checked />

									{item.name}
								</TreeView.ItemStack>

								<TreeView.Group items={item.children}>
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
						{Liferay.Language.get('no-fields-were-found')}
					</p>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading || !items.length}
							displayType="primary"
							onClick={onClose}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
