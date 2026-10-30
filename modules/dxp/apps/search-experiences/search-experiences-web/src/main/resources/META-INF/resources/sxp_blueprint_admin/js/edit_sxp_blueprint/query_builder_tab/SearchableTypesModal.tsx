/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import ClayTable from '@clayui/table';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useState} from 'react';

import sub from '../../utils/language/sub';
import {ISearchableType} from '../../utils/types';

function SearchableTypesModal({
	initialSelectedTypes,
	observer,
	onChangeTypes,
	onClose,
	onFetchSearchableTypes,
	searchableTypes,
}: {
	initialSelectedTypes: string[];
	observer: any;
	onChangeTypes: (types: string[]) => void;
	onClose: () => void;
	onFetchSearchableTypes: () => void;
	searchableTypes: ISearchableType[];
}) {
	const [modalSelectedTypes, setModalSelectedTypes] =
		useState(initialSelectedTypes);

	const searchableTypesClassNames = searchableTypes.map(
		({className}: {className: string}) => className
	);

	const _handleModalDone = () => {
		onClose();

		onChangeTypes(modalSelectedTypes);
	};

	const _handleRowCheck = (type: string) => () => {
		const isSelected = modalSelectedTypes.includes(type);

		setModalSelectedTypes(
			isSelected
				? modalSelectedTypes.filter((item: string) => item !== type)
				: [...modalSelectedTypes, type]
		);
	};

	return (
		<ClayModal
			className="modal-height-xl sxp-searchable-types-modal-root"
			observer={observer}
			size="lg"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('select-types')}
			</ClayModal.Header>

			{searchableTypes.length ? (
				<>
					<ManagementToolbar.Container
						className={
							modalSelectedTypes.length
								? 'management-bar-primary'
								: ''
						}
					>
						<div className="navbar-form navbar-form-autofit navbar-overlay">
							<ManagementToolbar.ItemList>
								<ManagementToolbar.Item>
									<ClayCheckbox
										checked={!!modalSelectedTypes.length}
										indeterminate={
											!!modalSelectedTypes.length &&
											modalSelectedTypes.length !==
												searchableTypes.length
										}
										onChange={() =>
											setModalSelectedTypes(
												!modalSelectedTypes.length
													? searchableTypesClassNames
													: []
											)
										}
									/>
								</ManagementToolbar.Item>

								<ManagementToolbar.Item>
									{modalSelectedTypes.length ? (
										<>
											<span className="component-text">
												{sub(
													Liferay.Language.get(
														'x-of-x-selected'
													),
													[
														modalSelectedTypes.length,
														searchableTypes.length,
													],
													false
												)}
											</span>

											{modalSelectedTypes.length <
												searchableTypes.length && (
												<ClayButton
													displayType="link"
													onClick={() => {
														setModalSelectedTypes(
															searchableTypesClassNames
														);
													}}
													small
												>
													{Liferay.Language.get(
														'select-all'
													)}
												</ClayButton>
											)}
										</>
									) : (
										<span className="component-text">
											{Liferay.Language.get('select-all')}
										</span>
									)}
								</ManagementToolbar.Item>
							</ManagementToolbar.ItemList>
						</div>
					</ManagementToolbar.Container>

					<ClayModal.Body scrollable>
						<ClayTable>
							<ClayTable.Body>
								{searchableTypes.map(
									({
										className,
										displayName,
									}: {
										className: string;
										displayName: string;
									}) => {
										const isSelected =
											modalSelectedTypes.includes(
												className
											);

										return (
											<ClayTable.Row
												active={isSelected}
												key={className}
												onClick={_handleRowCheck(
													className
												)}
											>
												<ClayTable.Cell>
													<ClayCheckbox
														aria-label={
															sub(
																Liferay.Language.get(
																	'select-x'
																),
																[displayName]
															) as string
														}
														checked={isSelected}
														onChange={_handleRowCheck(
															className
														)}
													/>
												</ClayTable.Cell>

												<ClayTable.Cell
													expanded
													headingTitle
												>
													{displayName}
												</ClayTable.Cell>
											</ClayTable.Row>
										);
									}
								)}
							</ClayTable.Body>
						</ClayTable>
					</ClayModal.Body>
				</>
			) : (
				<ClayModal.Body>
					<ClayEmptyState
						description={Liferay.Language.get(
							'an-error-has-occurred-and-we-were-unable-to-load-the-results'
						)}
						image={
							<ClayIcon
								className="c-empty-state-icon c-empty-state-icon-empty-state"
								illustration
								spritemap="/o/admin-theme/images/clay/empty_states.svg"
								symbol="empty-state"
							/>
						}
						title={Liferay.Language.get('no-items-were-found')}
					>
						<ClayButton
							displayType="secondary"
							onClick={onFetchSearchableTypes}
						>
							{Liferay.Language.get('refresh')}
						</ClayButton>
					</ClayEmptyState>
				</ClayModal.Body>
			)}

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton onClick={_handleModalDone}>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

export default function ({
	children,
	initialSelectedTypes,
	onChangeTypes,
	onFetchSearchableTypes,
	searchableTypes,
}: {
	children: React.ReactNode;
	initialSelectedTypes: string[];
	onChangeTypes: (types: string[]) => void;
	onFetchSearchableTypes: () => void;
	searchableTypes: ISearchableType[];
}) {
	const {observer, onOpenChange, open} = useModal();

	const _handleClose = () => {
		onOpenChange(false);
	};

	return (
		<>
			{open && (
				<SearchableTypesModal
					initialSelectedTypes={initialSelectedTypes}
					observer={observer}
					onChangeTypes={onChangeTypes}
					onClose={_handleClose}
					onFetchSearchableTypes={onFetchSearchableTypes}
					searchableTypes={searchableTypes}
				/>
			)}

			<span onClick={() => onOpenChange(!open)}>{children}</span>
		</>
	);
}
