/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {
	ManagementToolbar,
	openModal,
	openToast,
} from 'frontend-js-components-web';
import {cancelDebounce, debounce, fetch, sub} from 'frontend-js-web';
import React, {Ref, useContext, useMemo, useRef, useState} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';
import {DEFAULT_FETCH_HEADERS} from '../../../constants';
import getRandomId from '../../../utils/getRandomId';
import ViewsContext, {ISnapshot, ISnapshots} from '../../../views/ViewsContext';
import {EViewsActionTypes} from '../../../views/viewsReducer';
import shareSnapshotAction from './shareSnapshotAction';

const DEFAULT_VIEW_ID = 'DEFAULT_VIEW';

const SEARCH_DEBOUNCE_DELAY = 300;

const RequiredMark = () => (
	<>
		<span className="inline-item-after reference-mark text-warning">
			<ClayIcon symbol="asterisk" />
		</span>

		<span className="hide-accessible sr-only">
			{Liferay.Language.get('required')}
		</span>
	</>
);

const SnapshotsControlsTrigger = React.forwardRef(
	(
		{
			snapshotUpdated,
			triggerLabel,
			...otherProps
		}: {snapshotUpdated: boolean; triggerLabel: string},
		ref: Ref<HTMLButtonElement>
	) => (
		<ClayButton
			{...otherProps}
			aria-label={Liferay.Language.get('views')}
			className="dropdown-toggle snapshot-selection"
			displayType="unstyled"
			ref={ref}
		>
			<span className="navbar-text-truncate">{triggerLabel}</span>

			{snapshotUpdated && (
				<span className="inline-item-after reference-mark view-updated-mark">
					<span className="hide-accessible sr-only">
						{sub(
							Liferay.Language.get('snapshot-x-updated'),
							triggerLabel
						)}
					</span>

					<ClayIcon symbol="asterisk" />
				</span>
			)}

			<ClayIcon className="ml-2" symbol="caret-bottom" />
		</ClayButton>
	)
);

function SaveSnapshotModalComponent({
	closeModal,
	existingLabels,
	initialLabel,
	namespace,
	onSave,
	title,
}: {
	closeModal: () => void;
	existingLabels: string[];
	initialLabel: string;
	namespace: string;
	onSave: (label: string) => void;
	title: string;
}) {
	const [label, setLabel] = useState(initialLabel);
	const [errorMessage, setErrorMessage] = useState<string | null>(null);

	const trimmedLabel = label.trim();

	const getValidationError = () => {
		if (!trimmedLabel) {
			return Liferay.Language.get('this-field-is-required');
		}

		const duplicated = existingLabels.some(
			(existingLabel) =>
				existingLabel.trim().toLowerCase() ===
				trimmedLabel.toLowerCase()
		);

		if (duplicated) {
			return Liferay.Language.get('a-view-with-this-name-already-exists');
		}

		return null;
	};

	const handleSave = () => {
		const validationError = getValidationError();

		if (validationError) {
			setErrorMessage(validationError);

			return;
		}

		onSave(trimmedLabel);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{title}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group className={errorMessage ? 'has-error' : ''}>
					<label htmlFor={`${namespace}labelInput`}>
						{Liferay.Language.get('name')}

						<RequiredMark />
					</label>

					<ClayInput
						autoFocus={true}
						id={`${namespace}labelInput`}
						onChange={(event) => {
							setLabel(event.target.value);
							setErrorMessage(null);
						}}
						type="text"
						value={label}
					/>

					{errorMessage && (
						<ClayForm.FeedbackGroup>
							<ClayForm.FeedbackItem>
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{errorMessage}
							</ClayForm.FeedbackItem>
						</ClayForm.FeedbackGroup>
					)}
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!trimmedLabel}
							onClick={handleSave}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

const SnapshotsControls = () => {
	const {
		globalFDSState,
		id: fdsName,
		namespace,
		onSnapshotChange,
		portletId,
	} = useContext(FrontendDataSetContext);

	const [
		{
			activeSnapshotERC,
			activeView,
			defaultSnapshot,
			paginationDelta,
			snapshotUpdated,
			snapshots,
			sorts,
			visibleFieldNames,
		},
		viewsDispatch,
	] = useContext(ViewsContext);

	const [actionsDropdownActive, setActionsDropdownActive] = useState(false);
	const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
	const [searchTerm, setSearchTerm] = useState('');
	const [viewsDropdownActive, setViewsDropdownActive] = useState(false);

	const searchInputRef = useRef<HTMLInputElement>(null);

	const debouncedSetSearchTerm = useMemo(
		() => debounce(setDebouncedSearchTerm, SEARCH_DEBOUNCE_DELAY),
		[]
	);

	const clearSearch = () => {
		cancelDebounce(debouncedSetSearchTerm);

		setSearchTerm('');
		setDebouncedSearchTerm('');
	};

	const updateSearch = (value: string) => {
		setSearchTerm(value);
		debouncedSetSearchTerm(value);
	};

	const defaultSnapshotItem = {
		erc: DEFAULT_VIEW_ID,
		label: Liferay.Language.get('default-view'),
	};

	const snapshotsList = snapshots.flatMap((group: ISnapshots) => group.items);

	const activeSnapshot: ISnapshot =
		(snapshotsList.length &&
			activeSnapshotERC &&
			snapshotsList.find(
				(snapshot: ISnapshot) => snapshot.erc === activeSnapshotERC
			)) ||
		defaultSnapshotItem;

	const ownedSnapshots = snapshots.find(
		(group: ISnapshots) => !group.headerVisible
	);
	const visibleHeaderSnapshots = snapshots.filter(
		(group: ISnapshots) => group.headerVisible
	);

	const isActiveSnapshotOwned = !!ownedSnapshots?.items.some(
		(item: ISnapshot) => item.erc === activeSnapshot.erc
	);

	const pickerItems: ISnapshots[] = [
		{
			headerVisible: false,
			items: [defaultSnapshotItem, ...(ownedSnapshots?.items ?? [])],
		},
		...visibleHeaderSnapshots,
	];

	const normalizedSearchTerm = debouncedSearchTerm.trim().toLowerCase();

	const filteredPickerItems = pickerItems
		.map((group: ISnapshots) => ({
			...group,
			items: group.items.filter((item: ISnapshot) =>
				item.label.toLowerCase().includes(normalizedSearchTerm)
			),
		}))
		.filter((group: ISnapshots) => !!group.items.length);

	const hasSearchResults = !!filteredPickerItems.length;

	const activeSnapshotLabel = activeSnapshot.label ?? '';
	const initialLabel =
		activeSnapshot.erc !== DEFAULT_VIEW_ID ? activeSnapshotLabel : '';

	const saveSnapshot = ({
		label,
		processClose,
		snapshotERC,
	}: {
		label?: string;
		processClose?: Function;
		snapshotERC?: string;
	}) => {
		let method;
		let url: string;

		if (!snapshotERC) {
			method = 'POST';
			url = '/o/data-set-admin/snapshots';
		}
		else {
			method = 'PATCH';
			url = `/o/data-set-admin/snapshots/by-external-reference-code/${snapshotERC}`;
		}

		const body = {
			externalReferenceCode: snapshotERC ?? getRandomId(),
			fdsName,
			label: label || activeSnapshot.label,
			portletId,
			viewConfig: JSON.stringify({
				activeView,
				filters: globalFDSState.filters,
				paginationDelta,
				sorts,
				visibleFieldNames,
			}),
		};

		fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		})
			.then((response) => {
				if (!response.ok) {
					return response
						.json()
						.then((jsonResponse) =>
							Promise.reject(new Error(jsonResponse.title))
						);
				}

				const responseJSON = response.json();

				return responseJSON;
			})
			.then((snapshot) => {
				if (processClose) {
					processClose();
				}

				openToast({
					message: Liferay.Language.get(
						'view-was-saved-successfully'
					),
					type: 'success',
				});

				viewsDispatch({
					type: EViewsActionTypes.ADD_OR_UPDATE_SNAPSHOT,
					value: {
						configuration: JSON.parse(snapshot.viewConfig),
						erc: snapshot.externalReferenceCode,
						id: snapshot.id,
						label: snapshot.label,
					},
				});
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const ownedSnapshotItems = ownedSnapshots?.items ?? [];

	const openSaveSnapshotModal = () => {
		openModal({
			contentComponent: ({closeModal}) => (
				<SaveSnapshotModalComponent
					closeModal={closeModal}
					existingLabels={ownedSnapshotItems.map(
						(item: ISnapshot) => item.label
					)}
					initialLabel={initialLabel}
					namespace={namespace ?? ''}
					onSave={(label) =>
						saveSnapshot({label, processClose: closeModal})
					}
					title={Liferay.Language.get('save-new-view-as')}
				/>
			),
		});
	};

	const renameActiveSnapshot = ({
		label,
		processClose,
	}: {
		label: string;
		processClose: Function;
	}) => {
		const url = `/o/data-set-admin/snapshots/by-external-reference-code/${activeSnapshot.erc}`;

		fetch(url, {
			body: JSON.stringify({
				label,
			}),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'PATCH',
		})
			.then((response) => {
				if (response.ok) {
					if (processClose) {
						processClose();
					}

					openToast({
						message: Liferay.Language.get(
							'view-was-renamed-successfully'
						),
						type: 'success',
					});

					viewsDispatch({
						type: EViewsActionTypes.RENAME_ACTIVE_SNAPSHOT,
						value: {
							label,
						},
					});
				}
				else {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				}
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const openRenameSnapshotModal = () => {
		openModal({
			contentComponent: ({closeModal}) => (
				<SaveSnapshotModalComponent
					closeModal={closeModal}
					existingLabels={ownedSnapshotItems
						.filter(
							(item: ISnapshot) => item.erc !== activeSnapshot.erc
						)
						.map((item: ISnapshot) => item.label)}
					initialLabel={initialLabel}
					namespace={namespace ?? ''}
					onSave={(label) =>
						renameActiveSnapshot({label, processClose: closeModal})
					}
					title={Liferay.Language.get('rename-view')}
				/>
			),
		});
	};

	const deleteSnapshot = ({snapshotERC}: {snapshotERC: string}) => {
		const url = `/o/data-set-admin/snapshots/by-external-reference-code/${snapshotERC}`;

		fetch(url, {
			method: 'DELETE',
		})
			.then((response) => {
				if (response.ok) {
					openToast({
						message: Liferay.Language.get(
							'view-was-deleted-successfully'
						),
						type: 'success',
					});

					viewsDispatch({
						type: EViewsActionTypes.DELETE_SNAPSHOT,
						value: {
							snapshotERC,
						},
					});
				}
				else {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				}
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const openDeleteSnapshotModal = ({snapshotERC}: {snapshotERC: string}) => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this'
			),
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					autoFocus: true,
					displayType: 'danger',
					label: Liferay.Language.get('delete'),
					onClick: ({processClose}) => {
						processClose();

						deleteSnapshot({
							snapshotERC,
						});
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-view'),
		});
	};

	return (
		<>
			<ManagementToolbar.Item>
				<ClayDropDown
					active={viewsDropdownActive}
					closeOnClick
					hasRightSymbols
					menuWidth="shrink"
					onActiveChange={(active: boolean) => {
						setViewsDropdownActive(active);

						if (!active) {
							clearSearch();
						}
					}}
					trigger={
						<SnapshotsControlsTrigger
							snapshotUpdated={snapshotUpdated}
							triggerLabel={
								activeSnapshotERC
									? activeSnapshot.label
									: Liferay.Language.get('default-view')
							}
						/>
					}
				>
					<div className="dropdown-section">
						<ClayInput.Group small>
							<ClayInput.GroupItem className="input-group-item-focusable">
								<ClayInput
									aria-label={Liferay.Language.get('search')}
									insetAfter
									onChange={(event) =>
										updateSearch(event.target.value)
									}
									placeholder={Liferay.Language.get('search')}
									ref={searchInputRef}
									type="text"
									value={searchTerm}
								/>

								<ClayInput.GroupInsetItem after tag="span">
									{searchTerm ? (
										<ClayButton
											aria-label={Liferay.Language.get(
												'clear'
											)}
											displayType="unstyled"
											onClick={() => {
												clearSearch();

												searchInputRef.current?.focus();
											}}
											title={Liferay.Language.get(
												'clear'
											)}
											type="button"
										>
											<ClayIcon symbol="times" />
										</ClayButton>
									) : (
										<ClayIcon symbol="search" />
									)}
								</ClayInput.GroupInsetItem>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</div>

					{hasSearchResults ? (
						<ClayDropDown.ItemList>
							{filteredPickerItems.map((group: ISnapshots) => (
								<ClayDropDown.Group
									header={
										group.headerVisible
											? group.label
											: undefined
									}
									key={group.label || 'default'}
								>
									{group.items.map((snapshot: ISnapshot) => (
										<ClayDropDown.Item
											active={
												snapshot.erc ===
												activeSnapshot.erc
											}
											key={snapshot.erc}
											onClick={() =>
												onSnapshotChange({
													defaultSnapshot,
													snapshots,
													value: snapshot.erc,
												})
											}
											symbolRight={
												snapshot.erc ===
												activeSnapshot.erc
													? 'check'
													: undefined
											}
										>
											{snapshot.label}
										</ClayDropDown.Item>
									))}
								</ClayDropDown.Group>
							))}
						</ClayDropDown.ItemList>
					) : (
						<ClayDropDown.Caption>
							{Liferay.Language.get('no-results-found')}
						</ClayDropDown.Caption>
					)}
				</ClayDropDown>
			</ManagementToolbar.Item>

			<ManagementToolbar.Item>
				<ClayDropDown
					active={actionsDropdownActive}
					className="snapshot-actions"
					hasLeftSymbols
					onActiveChange={setActionsDropdownActive}
					trigger={
						<ClayButton
							aria-label={Liferay.Language.get(
								'show-view-actions'
							)}
							displayType="unstyled"
							title={Liferay.Language.get('show-view-actions')}
						>
							<ClayIcon symbol="ellipsis-v" />
						</ClayButton>
					}
				>
					<ClayDropDown.ItemList>
						{activeSnapshotERC && isActiveSnapshotOwned && (
							<ClayDropDown.Item
								onClick={() => {
									saveSnapshot({
										snapshotERC: activeSnapshotERC,
									});

									setActionsDropdownActive(false);
								}}
								symbolLeft="disk"
							>
								{Liferay.Language.get('save-view')}
							</ClayDropDown.Item>
						)}

						<ClayDropDown.Item
							onClick={openSaveSnapshotModal}
							symbolLeft="disk"
						>
							{Liferay.Language.get('save-view-as')}
						</ClayDropDown.Item>

						{activeSnapshotERC && isActiveSnapshotOwned && (
							<>
								<ClayDropDown.Item
									onClick={openRenameSnapshotModal}
									symbolLeft="pencil"
								>
									{Liferay.Language.get('rename-view')}
								</ClayDropDown.Item>

								{activeSnapshot.id ? (
									<ClayDropDown.Item
										onClick={() => {
											shareSnapshotAction({
												itemId: activeSnapshot.id as number,
												title: activeSnapshot.label,
											});

											setActionsDropdownActive(false);
										}}
										symbolLeft="share"
									>
										{Liferay.Language.get('share-view')}
									</ClayDropDown.Item>
								) : null}

								<ClayDropDown.Item
									onClick={() =>
										openDeleteSnapshotModal({
											snapshotERC: activeSnapshotERC,
										})
									}
									symbolLeft="trash"
								>
									{Liferay.Language.get('delete-view')}
								</ClayDropDown.Item>
							</>
						)}
					</ClayDropDown.ItemList>
				</ClayDropDown>
			</ManagementToolbar.Item>
		</>
	);
};

export default SnapshotsControls;
