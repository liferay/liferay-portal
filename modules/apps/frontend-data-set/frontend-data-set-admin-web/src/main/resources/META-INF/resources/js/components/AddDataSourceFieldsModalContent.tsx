/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayManagementToolbar, {
	ClayResultsBar,
} from '@clayui/management-toolbar';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {ComponentProps, useEffect, useState} from 'react';

import {IField, IFieldTreeItem} from '../utils/types';
import AutoSearch from './AutoSearch';

import '../../css/components/FieldSelectModalContent.scss';

export function visit(fields: Array<IFieldTreeItem>, callback: Function) {
	fields.forEach((field) => {
		callback(field);

		if (field.children) {
			visit(field.children, callback);
		}
	});
}

const initializeFields = ({
	fields: initialFields,
	selectedFields,
}: {
	fields: IField[];
	selectedFields: Array<IField>;
}): [Set<React.Key>, Array<IFieldTreeItem>] => {
	const selectedKeys = new Set<React.Key>();
	const fields: IFieldTreeItem[] = Array.from(initialFields);

	visit(fields, (field: IFieldTreeItem) => {
		const selectedField = selectedFields.find(
			(selectedField) => selectedField.name === field.name
		);

		if (selectedField) {
			selectedKeys.add(selectedField.name);

			field.savedId = selectedField.id;
		}

		field.initialChildren = field.children;
		field.id = field.name;
	});

	return [selectedKeys, fields];
};

function filterFields({
	fields,
	onFilter,
	onMatch,
	query,
}: {
	fields: Array<IFieldTreeItem>;
	onFilter?: (field: IFieldTreeItem) => void;
	onMatch?: (field: IFieldTreeItem) => void;
	query: string;
}) {
	const filteredItems: Array<IFieldTreeItem> = [];
	const regexp = new RegExp(query, 'i');

	fields.forEach((field) => {
		const match = field.label ? regexp.test(field.label) : false;

		const filteredChildren = field.children?.length
			? filterFields({fields: field.children, onFilter, onMatch, query})
			: [];

		if (match || (field.children?.length && filteredChildren.length)) {
			filteredItems.push({
				...field,
				children: filteredChildren,
				query,
			});

			if (onFilter) {
				onFilter(field);
			}
		}

		if (match && onMatch) {
			onMatch(field);
		}
	});

	return filteredItems;
}

function applyFilter({
	fields,
	query,
}: {fields?: Array<IFieldTreeItem>; query?: string} = {}) {
	if (!query || !fields) {
		return {
			counter: 0,
			filteredItems: fields ?? [],
			filteredKeys: [],
		};
	}

	let counter = 0;
	const filteredKeys: Array<React.Key> = [];
	const filteredItems = filterFields({
		fields,
		onFilter: ({id}: IFieldTreeItem) => {
			if (id) {
				filteredKeys.push(id);
			}
		},
		onMatch: () => counter++,
		query,
	});

	return {
		counter,
		filteredItems,
		filteredKeys,
	};
}

const Highlight = ({query, text}: {query?: string; text?: string}) => {
	if (!query || !text) {
		return (
			<span className="field-label font-weight-normal pl-1 text-3">
				{text ?? ''}
			</span>
		);
	}

	const indexMatch = text.search(RegExp(query, 'i'));

	if (indexMatch > -1) {
		return (
			<span className="field-label font-weight-normal pl-1 text-3">
				{text.substring(0, indexMatch)}

				<mark className="bg-transparent border-0 font-weight-bold p-0 shadow-none">
					{text.substring(indexMatch, indexMatch + query.length)}
				</mark>

				{text.substring(indexMatch + query.length)}
			</span>
		);
	}

	return <>{text}</>;
};

const AddDataSourceFieldsModalContent = ({
	closeModal,
	fieldTreeItems,
	onSaveButtonClick,
	saveButtonDisabled,
	selectedFields,
	selectionMode = 'single',
}: {
	closeModal: Function;
	fieldTreeItems: Array<IFieldTreeItem>;
	onSaveButtonClick: ({
		selectedFields,
	}: {
		selectedFields: Array<IFieldTreeItem>;
	}) => void;
	saveButtonDisabled: boolean;
	selectedFields: Array<IField>;
	selectionMode?: ComponentProps<typeof TreeView>['selectionMode'];
}) => {
	const [initialFields, setInitialFields] =
		useState<Array<IFieldTreeItem> | null>(fieldTreeItems);
	const [selectedKeys, setSelectedKeys] = useState<Set<React.Key>>(
		new Set<React.Key>()
	);
	const [fields, setFields] = useState<Array<IFieldTreeItem> | null>(
		initialFields
	);
	const [query, setQuery] = useState<string>('');
	const [expandedKeys, setExpandedKeys] = useState<Array<React.Key>>([]);

	useEffect(() => {
		if (fields) {
			const [initialSelectedKeys, updatedFields] = initializeFields({
				fields,
				selectedFields,
			});

			setSelectedKeys(initialSelectedKeys);

			setFields(updatedFields);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const onSearch = (query: string) => {
		setQuery(query);

		const {filteredItems, filteredKeys} = applyFilter({
			fields: initialFields ?? [],
			query,
		});

		setFields(filteredItems);
		setExpandedKeys(filteredKeys);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(
					Liferay.Language.get('select-x'),
					Liferay.Language.get('field')
				)}
			</ClayModal.Header>

			<ClayModal.Body className="field-select-modal pt-0 px-0">
				{fields === null ? (
					<ClayLoadingIndicator />
				) : (
					<>
						<ClayManagementToolbar>
							<ClayManagementToolbar.Search
								onSubmit={(event) => event.preventDefault()}
							>
								<AutoSearch onSearch={onSearch} query={query} />
							</ClayManagementToolbar.Search>
						</ClayManagementToolbar>

						{selectedKeys.size > 0 && (
							<ClayResultsBar>
								<ClayResultsBar.Item expand>
									<span className="component-text text-truncate-inline">
										<span className="text-truncate">
											{selectedKeys.size}
											&nbsp;
											{selectedKeys.size === 1
												? Liferay.Language.get(
														'item-selected'
													)
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
										onClick={() => {
											selectedKeys.clear();
											onSearch('');
										}}
									>
										{Liferay.Language.get('deselect-all')}
									</ClayButton>
								</ClayResultsBar.Item>
							</ClayResultsBar>
						)}

						<div
							className={`container-fluid container-fluid-max-xl px-4 py-2 selection-mode-${selectionMode}`}
						>
							<TreeView
								className="bg-light"
								expandedKeys={new Set(expandedKeys)}
								items={fields}
								nestedKey="children"
								onExpandedChange={(keys) => {
									setExpandedKeys(Array.from(keys));
								}}
								onItemsChange={(items) =>
									setInitialFields(
										items as Array<IFieldTreeItem>
									)
								}
								onSelectionChange={setSelectedKeys}
								selectedKeys={selectedKeys}
								selectionMode={selectionMode}
								showExpanderOnHover={false}
							>
								{({
									children,
									disabled,
									initialChildren,
									label,
									query,
								}: IFieldTreeItem) => (
									<TreeView.Item>
										{selectionMode === 'single' ? (
											<TreeView.ItemStack
												disabled={
													!!initialChildren?.length ||
													!!disabled
												}
												expanderDisabled={false}
											>
												<ClayCheckbox checked />

												<Highlight
													query={query}
													text={label}
												/>
											</TreeView.ItemStack>
										) : (
											<TreeView.ItemStack
												disabled={disabled}
											>
												<ClayCheckbox checked>
													<Highlight
														query={query}
														text={label}
													/>
												</ClayCheckbox>
											</TreeView.ItemStack>
										)}

										<TreeView.Group items={children}>
											{({
												disabled: childDisabled,
												label,
											}: IFieldTreeItem) => (
												<TreeView.Item
													disabled={childDisabled}
												>
													<ClayCheckbox checked>
														{selectionMode ===
															'multiple' && (
															<Highlight
																query={query}
																text={label}
															/>
														)}
													</ClayCheckbox>

													{selectionMode ===
														'single' && (
														<Highlight
															query={query}
															text={label}
														/>
													)}
												</TreeView.Item>
											)}
										</TreeView.Group>
									</TreeView.Item>
								)}
							</TreeView>
						</div>
					</>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={saveButtonDisabled}
							onClick={() => {
								const selectedFields: Array<IFieldTreeItem> =
									[];

								visit(
									initialFields || [],
									(field: IFieldTreeItem) => {
										if (selectedKeys.has(field.name)) {
											selectedFields.push({
												...field,
												id: field.savedId,
											});
										}
									}
								);

								onSaveButtonClick({
									selectedFields,
								});
							}}
						>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

export default AddDataSourceFieldsModalContent;
