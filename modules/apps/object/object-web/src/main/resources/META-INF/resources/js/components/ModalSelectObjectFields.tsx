/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {IClayAlertProps} from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayModal, {useModal} from '@clayui/modal';
import {ManagementToolbarSearch} from '@liferay/object-js-components-web';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import './ModalSelectObjectFields.scss';

export type Alert = {
	content: string;
	otherProps: IClayAlertProps;
};

function ModalSelectObjectFields<T extends ModalItem>() {
	const [
		{
			alerts,
			emptyState,
			getLabel,
			getName,
			header,
			items,
			onAfterClose,
			onSave,
			searchTerm,
			selected,
			showModal,
			title,
		},
		setState,
	] = useState<IState<T>>({
		items: [],
		searchTerm: '',
		selected: [],
		showModal: false,
	});

	const resetModal = () => {
		setState({items: [], searchTerm: '', selected: [], showModal: false});
	};

	const {observer, onClose} = useModal({
		onClose: onAfterClose
			? () => {
					onAfterClose();
					resetModal();

					return;
				}
			: resetModal,
	});

	useEffect(() => {
		const openModal = ({
			alerts = [],
			items = [],
			onAfterClose,
			searchTerm = '',
			selected = [],
			showModal = false,
			...otherProps
		}: Partial<IState<T>>) => {
			setState({
				alerts,
				items,
				onAfterClose,
				searchTerm,
				selected,
				showModal,
				...otherProps,
			});
		};

		Liferay.on('openModalSelectObjectFields', openModal);

		return () =>
			Liferay.detach(
				'openModalSelectObjectFields',
				openModal as () => void
			);
	}, []);

	const filteredItems = useMemo(() => {
		const loweredTerm = searchTerm.toLowerCase();
		const selectedIds = new Set(selected.map(({id}) => id));

		const filtered: T[] = [];
		items.forEach((item) => {
			if (getName?.(item).toLowerCase().includes(loweredTerm)) {
				filtered.push({
					...item,
					checked: selectedIds.has(item.id),
				});
			}
		});

		return filtered;
	}, [getName, searchTerm, selected, items]);

	const toggleFieldCheckbox = (id: unknown, checked: boolean) => {
		let selectedItems: T[];
		if (checked) {
			const item = items.find((item) => item.id === id) as T;
			selectedItems = [...selected, item];
		}
		else {
			selectedItems = selected.filter((item) => item.id !== id);
		}
		setState((state) => ({...state, selected: selectedItems}));
	};

	return showModal ? (
		<ClayModal
			center
			className="lfr-object__object-view-modal-select-object-fields"
			observer={observer}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{header}
			</ClayModal.Header>

			{!!alerts?.length &&
				!!items.length &&
				alerts.map((alert, index) => (
					<ClayAlert
						displayType={alert.otherProps.displayType}
						key={index}
						title={alert.otherProps.title}
						variant={alert.otherProps.variant}
					>
						{alert.content}
					</ClayAlert>
				))}

			<ClayModal.Body>
				{items.length ? (
					<>
						<div className="lfr-object__object-view-modal-select-object-fields-selection-title">
							{title}
						</div>

						<ManagementToolbar.Container>
							<ManagementToolbar.ItemList>
								<ManagementToolbar.Item>
									<ClayCheckbox
										checked={
											items.length === selected.length
										}
										indeterminate={
											!!selected.length &&
											items.length !== selected.length
										}
										name="selectAllObjectFields"
										onChange={() => {
											const disabledItems =
												selected.filter(
													(item) =>
														item.disableCheckbox
												);
											const selectedItems =
												items.length -
													disabledItems.length ===
												selected.length -
													disabledItems.length
													? [...disabledItems]
													: [...items];
											setState((state) => ({
												...state,
												selected: selectedItems,
											}));
										}}
									/>
								</ManagementToolbar.Item>
							</ManagementToolbar.ItemList>

							<ManagementToolbarSearch
								query={searchTerm}
								setQuery={(searchTerm) =>
									setState((state) => ({
										...state,
										searchTerm,
									}))
								}
							/>
						</ManagementToolbar.Container>

						<ClayList className="lfr-object__object-view-modal-select-object-fields-list">
							{filteredItems.map((item, index) => (
								<ClayList.Item flex key={`list-item-${index}`}>
									<ClayCheckbox
										checked={!!item.checked}
										disabled={item.disableCheckbox}
										label={
											getLabel?.(item) ?? getName?.(item)
										}
										onChange={() => {
											toggleFieldCheckbox(
												item.id,
												!item.checked
											);
										}}
									/>

									{item.required && (
										<span className="lfr-object__object-view-modal-select-object-fields-reference-mark">
											<ClayIcon symbol="asterisk" />
										</span>
									)}
								</ClayList.Item>
							))}
						</ClayList>
					</>
				) : (
					<div className="lfr-object__object-view-modal-select-object-fields-empty-state">
						<ClayEmptyState
							description={
								emptyState?.message ??
								Liferay.Language.get(
									'there-are-no-fields-in-this-definition'
								)
							}
							imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
							small
							title={
								emptyState?.title ??
								Liferay.Language.get(
									'there-are-no-fields-created-yet'
								)
							}
						/>
					</div>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					items.length ? (
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								displayType="primary"
								onClick={() => {
									onSave?.(selected);
									resetModal();
								}}
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					) : (
						<ClayButton displayType="primary" onClick={resetModal}>
							{Liferay.Language.get('done')}
						</ClayButton>
					)
				}
			/>
		</ClayModal>
	) : null;
}

export default ModalSelectObjectFields;

interface ModalItem extends ObjectField {
	checked?: boolean;
	disableCheckbox?: boolean;
}

interface IState<T extends ModalItem> {
	alerts?: Alert[];
	disableRequired?: boolean;
	disableRequiredChecked?: boolean;
	emptyState?: {
		message: string;
		title: string;
	};
	getLabel?: (label: T) => string;
	getName?: (name: T) => string;
	header?: string;
	items: T[];
	onAfterClose?: () => void;
	onSave?: (selected: T[]) => void;
	searchTerm: string;
	selected: T[];
	showModal: boolean;
	title?: string;
}
