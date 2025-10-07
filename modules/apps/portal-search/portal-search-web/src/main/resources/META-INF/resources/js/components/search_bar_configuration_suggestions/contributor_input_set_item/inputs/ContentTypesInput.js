/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import ClayTable from '@clayui/table';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useState} from 'react';

const CONTENT_TYPES = [
	{
		className: 'blog',
		displayName: Liferay.Language.get(
			'model.resource.com.liferay.blogs.model.BlogsEntry'
		),
	},
	{
		className: 'document',
		displayName: Liferay.Language.get(
			'model.resource.com.liferay.portal.kernel.repository.model.FileEntry'
		),
	},
	{
		className: 'form',
		displayName: Liferay.Language.get(
			'model.resource.com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord'
		),
	},
	{
		className: 'web-content',
		displayName: Liferay.Language.get(
			'model.resource.com.liferay.journal.model.JournalArticle'
		),
	},
];

function ContentTypesModal({
	initialSelectedTypes = [],
	observer,
	onBlur,
	onChange,
	onClose,
}) {
	const [selectedTypes, setSelectedTypes] = useState(initialSelectedTypes);

	const contentTypesClassNames = CONTENT_TYPES.map(
		({className}) => className
	);

	const _handleCancel = () => {
		onClose();
		onBlur();
	};

	const _handleDone = () => {
		onChange(
			selectedTypes.length === CONTENT_TYPES.length
				? ''
				: selectedTypes.toString()
		);

		_handleCancel();
	};

	const _handleRowCheck = (type) => () => {
		setSelectedTypes(
			selectedTypes.includes(type)
				? selectedTypes.filter((item) => item !== type)
				: [...selectedTypes, type]
		);
	};

	return (
		<ClayModal className="modal-height-xl" observer={observer} size="lg">
			<ClayModal.Header>
				{Liferay.Language.get('select-types')}
			</ClayModal.Header>

			<ManagementToolbar.Container
				className={!!selectedTypes.length && 'management-bar-primary'}
			>
				<div className="navbar-form navbar-form-autofit navbar-overlay">
					<ManagementToolbar.ItemList>
						<ManagementToolbar.Item>
							<ClayCheckbox
								checked={!!selectedTypes.length}
								indeterminate={
									!!selectedTypes.length &&
									selectedTypes.length !==
										CONTENT_TYPES.length
								}
								onChange={() =>
									setSelectedTypes(
										!selectedTypes.length
											? contentTypesClassNames
											: []
									)
								}
							/>
						</ManagementToolbar.Item>

						<ManagementToolbar.Item>
							{selectedTypes.length ? (
								<>
									<span className="component-text">
										{Liferay.Util.sub(
											Liferay.Language.get(
												'x-of-x-selected'
											),
											selectedTypes.length,
											CONTENT_TYPES.length
										)}
									</span>

									{selectedTypes.length <
										CONTENT_TYPES.length && (
										<ClayButton
											displayType="link"
											onClick={() => {
												setSelectedTypes(
													contentTypesClassNames
												);
											}}
											size="sm"
										>
											{Liferay.Language.get('select-all')}
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
						{CONTENT_TYPES.map(({className, displayName}) => {
							const isSelected =
								selectedTypes.includes(className);

							return (
								<ClayTable.Row
									active={isSelected}
									key={className}
									onClick={_handleRowCheck(className)}
								>
									<ClayTable.Cell>
										<ClayCheckbox
											aria-label={Liferay.Util.sub(
												Liferay.Language.get(
													'select-x'
												),
												[displayName]
											)}
											checked={isSelected}
											onChange={_handleRowCheck(
												className
											)}
										/>
									</ClayTable.Cell>

									<ClayTable.Cell expanded headingTitle>
										{displayName}
									</ClayTable.Cell>
								</ClayTable.Row>
							);
						})}
					</ClayTable.Body>
				</ClayTable>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={_handleCancel}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!selectedTypes.length}
							onClick={_handleDone}
						>
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

export default function ContentTypesInput({index, onBlur, onChange, value}) {
	const {observer, onOpenChange, open} = useModal();

	const _getSelectedTypes = (items) => {
		return items
			? items.split(',')
			: CONTENT_TYPES.map(({className}) => className);
	};

	const _handleClose = () => {
		onOpenChange(false);
	};

	const _handleOpen = () => {
		onOpenChange(true);

		onBlur();
	};

	return (
		<>
			{open && (
				<ContentTypesModal
					initialSelectedTypes={_getSelectedTypes(value)}
					observer={observer}
					onChange={onChange}
					onClose={_handleClose}
				/>
			)}

			<ClayInput.GroupItem>
				<label htmlFor={`contentTypes${index}`}>
					{Liferay.Language.get('content-types')}

					<ClayTooltipProvider>
						<span
							className="c-ml-2"
							data-tooltip-align="top"
							tabIndex={0}
							title={Liferay.Language.get('content-types-help')}
						>
							<ClayIcon symbol="question-circle-full" />
						</span>
					</ClayTooltipProvider>
				</label>

				<ClayInput.Group>
					<ClayButton
						aria-label={Liferay.Language.get('content-types')}
						displayType="secondary"
						id={`contentTypes${index}`}
						onClick={_handleOpen}
						size="sm"
					>
						{!value
							? Liferay.Util.sub(
									Liferay.Language.get('all-x-selected'),
									CONTENT_TYPES.length
								)
							: Liferay.Util.sub(
									Liferay.Language.get('x-of-x-selected'),
									_getSelectedTypes(value).length,
									CONTENT_TYPES.length
								)}
					</ClayButton>
				</ClayInput.Group>
			</ClayInput.GroupItem>
		</>
	);
}
