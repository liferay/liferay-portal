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
import ViewsContext, {ICustomView} from '../../views/ViewsContext';
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

const CustomViewsControlsTrigger = React.forwardRef(
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
			className="custom-views-selection dropdown-toggle"
			displayType="unstyled"
			ref={ref}
		>
			<span className="navbar-text-truncate">{triggerLabel}</span>

			{viewUpdated && (
				<span className="inline-item-after reference-mark view-updated-mark">
					<span className="hide-accessible sr-only">
						{sub(
							Liferay.Language.get('custom-view-x-updated'),
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

const CustomViewsControls = () => {
	const {
		dataSetERC,
		id: fdsName,
		namespace,
		portletId,
	} = useContext(FrontendDataSetContext);
	const [
		{
			activeCustomViewId,
			activeView,
			customViews,
			filters,
			paginationDelta,
			sorts,
			viewUpdated,
			visibleFieldNames,
		},
		viewsDispatch,
	] = useContext(ViewsContext);

	const [actionsDropdownActive, setActionsDropdownActive] = useState(false);

	const defaultCustomView = {
		customViewERC: DEFAULT_VIEW_ID,
		customViewLabel: Liferay.Language.get('default-view'),
	};

	const activeCustomView: ICustomView =
		(customViews.length &&
			activeCustomViewId &&
			customViews.find(
				(view: ICustomView) => view.customViewERC === activeCustomViewId
			)) ||
		defaultCustomView;

	const customViewLabelInputRef =
		useRef() as React.MutableRefObject<HTMLInputElement>;

	const SaveCustomViewModalBody = () => (
		<ClayForm.Group>
			<label htmlFor={`${namespace}customViewLabelInput`}>
				{Liferay.Language.get('name')}

				<RequiredMark />
			</label>

			<ClayInput
				autoFocus={true}
				defaultValue={
					activeCustomView?.customViewERC !== DEFAULT_VIEW_ID
						? activeCustomView?.customViewLabel
						: ''
				}
				id={`${namespace}customViewLabelInput`}
				ref={customViewLabelInputRef}
				type="text"
			/>
		</ClayForm.Group>
	);

	const saveCustomView = ({
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
			url = `/o/data-set-admin/user-fds-configs`;
		}
		else {
			method = 'PATCH';
			url = `/o/data-set-admin/user-fds-configs/by-external-reference-code/${activeCustomView.customViewERC}`;
		}

		const customViewId = id ?? getRandomId();

		const viewState = {
			activeView,
			filters,
			paginationDelta,
			sorts,
			visibleFieldNames,
		};

		const body = {
			externalReferenceCode: customViewId,
			fdsName,
			label: label || activeCustomView.customViewLabel,
			portletId,
			viewConfig: JSON.stringify(viewState),
		};

		fetch(url, {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method,
		})
			.then((response) => {
				if (response.ok) {
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
						type: EViewsActionTypes.ADD_OR_UPDATE_CUSTOM_VIEW,
						value: {
							customViewConfig: viewState,
							customViewERC: customViewId,
							customViewLabel: label,
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

	const openSaveCustomViewModal = () => {
		openModal({
			bodyComponent: SaveCustomViewModalBody,
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					label: Liferay.Language.get('save'),
					onClick: ({processClose}) => {
						saveCustomView({
							label: customViewLabelInputRef.current.value,
							processClose,
						});
					},
				},
			],
			title: Liferay.Language.get('save-new-view-as'),
		});
	};

	const renameActiveCustomView = ({
		label,
		processClose,
	}: {
		label: string;
		processClose: Function;
	}) => {
		const url = `/o/data-set-admin/user-fds-configs/by-external-reference-code/${activeCustomView.customViewERC}`;

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
						type: EViewsActionTypes.RENAME_ACTIVE_CUSTOM_VIEW,
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

	const openRenameCustomViewModal = () => {
		openModal({
			bodyComponent: SaveCustomViewModalBody,
			buttons: [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					label: Liferay.Language.get('save'),
					onClick: ({processClose}) => {
						renameActiveCustomView({
							label: customViewLabelInputRef.current?.value,
							processClose,
						});
					},
				},
			],
			title: Liferay.Language.get('save-new-view-as'),
		});
	};

	const deleteCustomView = ({id}: {id: string}) => {
		const url = `/o/data-set-admin/user-fds-configs/by-external-reference-code/${activeCustomView.customViewERC}`;

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
						type: EViewsActionTypes.DELETE_CUSTOM_VIEW,
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

	const openDeleteCustomViewModal = ({id}: {id: string}) => {
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

						deleteCustomView({
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
		if (value === DEFAULT_VIEW_ID) {
			viewsDispatch({
				type: EViewsActionTypes.RESET_TO_DEFAULT_VIEW,
			});
		}
		else {
			viewsDispatch({
				type: EViewsActionTypes.UPDATE_ACTIVE_CUSTOM_VIEW,
				value,
			});
		}
	};

	return (
		<>
			<ManagementToolbar.Item>
				<Picker
					as={CustomViewsControlsTrigger}
					items={[defaultCustomView, ...customViews]}
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
					selectedKey={activeCustomView.customViewERC}
					triggerLabel={
						activeCustomViewId
							? activeCustomView.customViewLabel
							: Liferay.Language.get('default-view')
					}
					viewUpdated={viewUpdated}
				>
					{(view) => (
						<Option key={view.customViewERC}>
							{view.customViewLabel}
						</Option>
					)}
				</Picker>
			</ManagementToolbar.Item>

			<ManagementToolbar.Item>
				<ClayDropDown
					active={actionsDropdownActive}
					className="custom-views-actions"
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
						{activeCustomViewId && (
							<ClayDropDown.Item
								onClick={() => {
									saveCustomView({
										id: activeCustomViewId,
									});

									setActionsDropdownActive(false);
								}}
								symbolLeft="disk"
							>
								{Liferay.Language.get('save-view')}
							</ClayDropDown.Item>
						)}

						<ClayDropDown.Item
							onClick={openSaveCustomViewModal}
							symbolLeft="disk"
						>
							{Liferay.Language.get('save-view-as')}
						</ClayDropDown.Item>

						{activeCustomViewId && (
							<>
								<ClayDropDown.Item
									onClick={openRenameCustomViewModal}
									symbolLeft="pencil"
								>
									{Liferay.Language.get('rename-view')}
								</ClayDropDown.Item>

								<ClayDropDown.Item
									onClick={() =>
										openDeleteCustomViewModal({
											id: activeCustomViewId,
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

export default CustomViewsControls;
