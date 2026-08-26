/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {NetworkStatus} from '@clayui/data-provider';
import ClayForm, {ClayCheckbox} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import {sub} from 'frontend-js-web';
import React, {useEffect, useId, useState} from 'react';

import SpaceSticker from '../../../common/components/SpaceSticker';
import ErrorFeedback from '../../../common/components/forms/ErrorFeedback';
import RequiredMark from '../../../common/components/forms/RequiredMark';
import {LogoColor} from '../../../common/types/Space';

export type ScopeItem = {
	displayType?: LogoColor;
	label: string;
	scopeKey: string;
	value: any;
};

type ScopeLabels = {
	allItemsValue: string;
	ariaLabel: string;
	checkbox: string;
	field: string;
};

export default function ScopeMultiSelect<T extends ScopeItem>({
	disabled = false,
	labels,
	onChange,
	onError,
	onSelectionChange,
	preselectedItems,
	required = true,
	sourceItems,
}: {
	disabled?: boolean;
	labels: ScopeLabels;
	onChange?: (value: boolean) => void;
	onError: (value: string) => void;
	onSelectionChange: (value: any) => void;
	preselectedItems?: T[];
	required?: boolean;
	sourceItems: T[];
}) {
	const [active, setActive] = useState(false);
	const [allScopesChecked, setAllScopesChecked] = useState(true);
	const [query, setQuery] = useState('');
	const [touched, setTouched] = useState(false);
	const [selectedItems, setSelectedItems] = useState<T[]>([]);

	const errorId = useId();
	const inputId = useId();

	const loadingState = !sourceItems.length
		? NetworkStatus.Polling
		: undefined;

	useEffect(() => {
		setAllScopesChecked(!preselectedItems?.length);
		setSelectedItems(preselectedItems ?? []);
	}, [preselectedItems]);

	useEffect(() => {
		if (!onChange) {
			return;
		}

		const selectionChanged =
			preselectedItems?.length !== selectedItems.length ||
			preselectedItems?.some(
				(preselectedItem) =>
					!selectedItems.some(
						(item) => item.value === preselectedItem.value
					)
			);

		onChange(!allScopesChecked && Boolean(selectionChanged));
	}, [allScopesChecked, onChange, preselectedItems, selectedItems]);

	const errorMessage =
		!required || allScopesChecked || selectedItems.length
			? ''
			: sub(
					Liferay.Language.get('the-x-field-is-required'),
					labels.field
				);

	useEffect(() => {
		onError(errorMessage);
	}, [errorMessage, onError]);

	const getSelectedItems = (items: T[]) =>
		sourceItems.filter((sourceItem) =>
			items.some((item) => sourceItem.value === item.value)
		);

	const showError = touched && !active && Boolean(errorMessage);

	return (
		<div>
			<label htmlFor={inputId}>
				{labels.field}

				{required && <RequiredMark />}
			</label>

			<div className={showError ? 'has-error' : ''}>
				<ClayMultiSelect
					active={active}
					aria-describedby={showError ? errorId : undefined}
					aria-label={labels.ariaLabel}
					disabled={allScopesChecked || disabled}
					id={inputId}
					items={selectedItems}
					key={sourceItems.length ? 'loaded' : 'empty'}
					loadingState={loadingState}
					onActiveChange={setActive}
					onBlur={() => setTouched(true)}
					onChange={setQuery}
					onItemsChange={(items: T[]) => {
						setTouched(true);

						const nextSelectedItems = getSelectedItems(items);

						setSelectedItems(nextSelectedItems);

						onSelectionChange(
							nextSelectedItems.map((item) => item.scopeKey)
						);
					}}
					sourceItems={sourceItems}
					value={allScopesChecked ? labels.allItemsValue : query}
				>
					{(item) => (
						<ClayMultiSelect.Item
							key={item.value}
							textValue={item.label}
						>
							<SpaceSticker
								displayType={item.displayType}
								name={item.label}
								size="sm"
							/>
						</ClayMultiSelect.Item>
					)}
				</ClayMultiSelect>

				{showError && (
					<ClayForm.FeedbackGroup id={errorId} role="alert">
						<ErrorFeedback message={errorMessage} />
					</ClayForm.FeedbackGroup>
				)}
			</div>

			<div className="mt-2">
				<ClayCheckbox
					checked={allScopesChecked}
					disabled={disabled}
					label={labels.checkbox}
					onChange={() => {
						if (allScopesChecked) {
							setTouched(false);
						}

						setAllScopesChecked((checked) => !checked);
						setSelectedItems([]);
						onSelectionChange([]);
						setQuery('');
					}}
				/>
			</div>
		</div>
	);
}
