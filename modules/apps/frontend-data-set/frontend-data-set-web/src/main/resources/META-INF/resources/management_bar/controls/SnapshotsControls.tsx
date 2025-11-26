/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {
	ManagementToolbar,
	openModal,
	openToast,
} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {Ref, useContext, useRef, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import {DEFAULT_FETCH_HEADERS} from '../../constants';
import getRandomId from '../../utils/getRandomId';
import ViewsContext, {ISnapshot} from '../../views/ViewsContext';
import {EViewsActionTypes} from '../../views/viewsReducer';

const DEFAULT_VIEW_ID = 'DEFAULT_VIEW';

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
			triggerLabel,
			viewUpdated,
			...otherProps
		}: {triggerLabel: string; viewUpdated: boolean},
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

			{viewUpdated && (
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

const SnapshotsControls = () => {
	const {
		handleSnapshotChange,
		id: fdsName,
		namespace,
		portletId,
	} = useContext(FrontendDataSetContext);
	const [
		{
			activeSnapshotId,
			activeView,
			defaultView,
			filters,
			paginationDelta,
			snapshots,
			sorts,
			viewUpdated,
			visibleFieldNames,
		},
		viewsDispatch,
	] = useContext(ViewsContext);

	const [actionsDropdownActive, setActionsDropdownActive] = useState(false);

	const defaultSnapshot = {
		erc: DEFAULT_VIEW_ID,
		label: Liferay.Language.get('default-view'),
	};

	const activeSnapshot: ISnapshot =
		(snapshots.length &&
			activeSnapshotId &&
			snapshots.find(
				(view: ISnapshot) => view.erc === activeSnapshotId
			)) ||
		defaultSnapshot;

	const labelInputRef = useRef() as React.MutableRefObject<HTMLInputElement>;

	const SaveSnapshotModalBody = () => (
		<ClayForm.Group>
			<label htmlFor={`${namespace}labelInput`}>
				{Liferay.Language.get('name')}

				<RequiredMark />
			</label>

			<ClayInput
				autoFocus={true}
				defaultValue={
					activeSnapshot?.erc !== DEFAULT_VIEW_ID
						? activeSnapshot?.label
						: ''
				}
				id={`${namespace}labelInput`}
				ref={labelInputRef}
				type="text"
			/>
		</ClayForm.Group>
	);

	const saveSnapshot = ({
		id,
		label,
		processClose,
	}: {
		id?: string;
		label?: string;
		processClose?: Function;
	}) => {
		let method;
		let url: string;

		if (!id) {
			method = 'POST';
			url = `/o/data-set-admin/snapshot-fds-configs`;
		}
		else {
			method = 'PATCH';
			url = `/o/data-set-admin/snapshot-fds-configs/by-external-reference-code/${activeSnapshot.erc}`;
		}

		const snapshotId = id ?? getRandomId();

		const viewState = {
			activeView,
			filters,
			paginationDelta,
			sorts,
			visibleFieldNames,
		};

		const body = {
			externalReferenceCode: snapshotId,
			fdsName,
			label: label || activeSnapshot.label,
			portletId,
			viewConfig: JSON.stringify(viewState),
		};

		fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		})
			.then((response) => {
				if (!response.ok) {
					return [];
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

	const openSaveSnapshotModal = () => {
		openModal({
			bodyComponent: SaveSnapshotModalBody,
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					label: Liferay.Language.get('save'),
					onClick: ({processClose}) => {
						saveSnapshot({
							label: labelInputRef.current.value,
							processClose,
						});
					},
				},
			],
			title: Liferay.Language.get('save-new-view-as'),
		});
	};

	const renameActiveSnapshot = ({
		label,
		processClose,
	}: {
		label: string;
		processClose: Function;
	}) => {
		const url = `/o/data-set-admin/snapshot-fds-configs/by-external-reference-code/${activeSnapshot.erc}`;

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
			bodyComponent: SaveSnapshotModalBody,
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					label: Liferay.Language.get('save'),
					onClick: ({processClose}) => {
						renameActiveSnapshot({
							label: labelInputRef.current?.value,
							processClose,
						});
					},
				},
			],
			title: Liferay.Language.get('save-new-view-as'),
		});
	};

	const deleteSnapshot = ({id}: {id: string}) => {
		const url = `/o/data-set-admin/snapshot-fds-configs/by-external-reference-code/${activeSnapshot.erc}`;

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
							id,
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

	const openDeleteSnapshotModal = ({id}: {id: string}) => {
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
							id,
						});
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-view'),
		});
	};

	const handleSelectionChange = (value: React.Key) => {
		handleSnapshotChange({defaultView, snapshots, value});
	};

	return (
		<>
			<ManagementToolbar.Item>
				<Picker
					as={SnapshotsControlsTrigger}
					items={[defaultSnapshot, ...snapshots]}
					messages={{
						itemDescribedby: Liferay.Language.get(
							'you-are-currently-on-a-text-element,-inside-of-a-list-box'
						),
						itemSelected: Liferay.Language.get('x-selected'),
						scrollToBottomAriaLabel:
							Liferay.Language.get('scroll-to-bottom'),
						scrollToTopAriaLabel:
							Liferay.Language.get('scroll-to-top'),
					}}
					onSelectionChange={handleSelectionChange}
					selectedKey={activeSnapshot.erc}
					triggerLabel={
						activeSnapshotId
							? activeSnapshot.label
							: Liferay.Language.get('default-view')
					}
					viewUpdated={viewUpdated}
				>
					{(view) => <Option key={view.erc}>{view.label}</Option>}
				</Picker>
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
						{activeSnapshotId && (
							<ClayDropDown.Item
								onClick={() => {
									saveSnapshot({
										id: activeSnapshotId,
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

						{activeSnapshotId && (
							<>
								<ClayDropDown.Item
									onClick={openRenameSnapshotModal}
									symbolLeft="pencil"
								>
									{Liferay.Language.get('rename-view')}
								</ClayDropDown.Item>

								<ClayDropDown.Item
									onClick={() =>
										openDeleteSnapshotModal({
											id: activeSnapshotId,
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
