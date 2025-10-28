/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayButtonGroup from '@clayui/button/lib/Group';
import {Body, Cell, Head, Row, Table} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayModal, {Context as ModalContext, useModal} from '@clayui/modal';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayToolbar from '@clayui/toolbar';
import moment from 'moment';
import React, {useContext, useEffect, useState} from 'react';

import {
	deleteFolderTemplateInformation,
	getAvailableTemplatesPage,
} from '../../services/TemplateListService';
import Diagram from '../template-diagram/Diagram';
import CreateTemplate from './CreateTemplate';
import GenerateFolders from './GenerateFolders';

const DELTAS = [{label: 5}, {label: 10}, {label: 20}, {label: 40}];

const MODAL_OPEN = 'OPEN';

const HEADERS = [
	{
		key: 'id',
		label: 'ID',
	},
	{
		expanded: true,
		key: 'templateName',
		label: 'Template Name',
	},
	{
		key: 'dateCreated',
		label: 'Created Date',
	},
	{
		key: 'actions',
		label: '',
	},
];

const TemplateList = () => {
	const [data, setData] = useState([]);
	const [delta, setDelta] = useState(5);
	const [isDeleting, setIsDeleting] = useState(false);
	const [isLoading, setIsLoading] = useState(false);
	const [pageIndex, setPageIndex] = useState(1);
	const [selectedTemplate, setSelectedTemplate] = useState();
	const [totalItems, setTotalItems] = useState(0);

	const [modalState, dispatchModal] = useContext(ModalContext);

	const {observer, onOpenChange, open} = useModal();

	const confirmDeleteItemModal = (template) => {
		const deleteTemplate = async () => {
			setIsDeleting(true);

			await deleteFolderTemplateInformation(template.id);

			setIsDeleting(false);

			reload();
		};

		Liferay.Util.openConfirmModal({
			message:
				"Deleting a template also removes it's entries. This action is permanent and cannot be undone.",
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					deleteTemplate();
				}
			},
		});
	};

	const closeNewItemModal = (closeAndReload) => {
		if (closeAndReload) {
			reload();
		}

		modalState.onClose(true);
	};

	const openDesignerModal = (template) => {
		setSelectedTemplate(template);

		onOpenChange(true);
	};

	const openCreateFolderModal = (template) => {
		dispatchModal({
			payload: {
				body: <GenerateFolders templateId={template.id} />,
				center: true,
				header: 'Create Folder Structure',
				size: 'lg',
			},
			type: MODAL_OPEN,
		});
	};

	const reload = () => {
		setTotalItems(0);

		if (pageIndex === 1) {
			loadPage();
		}
		else {
			setPageIndex(1);
		}
	};

	const loadPage = async () => {
		setIsLoading(true);

		const results = await getAvailableTemplatesPage(pageIndex, delta);

		setData(results.items);

		setTotalItems(results.totalCount);

		setIsLoading(false);
	};

	const openNewItemModal = () => {
		dispatchModal({
			payload: {
				body: (
					<CreateTemplate
						onClose={closeNewItemModal}
						onSuccess={reload}
					/>
				),
				center: true,
				header: 'Create Folder Template',
				size: 'lg',
			},
			type: MODAL_OPEN,
		});
	};

	useEffect(() => {
		const fetchData = async () => {
			const results = await getAvailableTemplatesPage(pageIndex, delta);

			setData(results.items);

			setTotalItems(results.totalCount);
		};

		fetchData();
	}, [delta, pageIndex]);

	return (
		<>
			<ClayToolbar className="mb-3">
				<ClayToolbar.Nav>
					<ClayToolbar.Item className="text-left" expand>
						<ClayToolbar.Section>
							<label className="component-title">
								Folder Templates
							</label>
						</ClayToolbar.Section>
					</ClayToolbar.Item>
					<ClayToolbar.Item></ClayToolbar.Item>
					<ClayToolbar.Item>
						<ClayToolbar.Section>
							{Liferay.ThemeDisplay.isSignedIn() && (
								<ClayButtonGroup spaced={true}>
									<ClayButtonWithIcon
										aria-label="Reload"
										className="lfr-portal-tooltip"
										disabled={isDeleting || isLoading}
										displayType="secondary"
										onClick={reload}
										symbol="reload"
										title="Reload"
									/>

									<ClayButtonWithIcon
										aria-label="Create New"
										className="lfr-portal-tooltip"
										disabled={isDeleting || isLoading}
										displayType="primary"
										onClick={openNewItemModal}
										symbol="plus"
										title="Create New"
									/>
								</ClayButtonGroup>
							)}
						</ClayToolbar.Section>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>

			{totalItems > 0 && (
				<>
					<Table>
						<Head items={HEADERS}>
							{(column) => (
								<Cell
									expanded={column.expanded}
									key={column.key}
									wrap={false}
								>
									{column.label}
								</Cell>
							)}
						</Head>

						<Body>
							{data &&
								data.map((row) => (
									<Row key={row['id']}>
										<Cell wrap={false}>{row['id']}</Cell>
										<Cell expanded={true} wrap={false}>
											{row['templateName']}
										</Cell>
										<Cell wrap={false}>
											{moment(row['dateCreated']).format(
												'MMMM D, YYYY'
											)}
										</Cell>
										<Cell textAlign="end" wrap={false}>
											<ClayButton.Group
												spaced={false}
												style={{minWidth: '150px'}}
											>
												<ClayButtonWithIcon
													aria-label="Create Folder Structure"
													className="lfr-portal-tooltip"
													displayType="unstyled"
													onClick={() => {
														openCreateFolderModal(
															row
														);
													}}
													size="sm"
													symbol="folder"
													title="Create Folder Structure"
												>
													Create Folder
												</ClayButtonWithIcon>
												<ClayButtonWithIcon
													aria-label="Design Template"
													className="lfr-portal-tooltip"
													displayType="unstyled"
													onClick={() => {
														openDesignerModal(row);
													}}
													size="sm"
													symbol="diagram"
													title="Design Template"
												>
													Design Template
												</ClayButtonWithIcon>
												<ClayButtonWithIcon
													aria-label="Delete Template"
													className="lfr-portal-tooltip"
													displayType="unstyled"
													onClick={() => {
														confirmDeleteItemModal(
															row
														);
													}}
													size="sm"
													symbol="trash"
													title="Delete Template"
												>
													Delete
												</ClayButtonWithIcon>
											</ClayButton.Group>
										</Cell>
									</Row>
								))}
						</Body>
					</Table>

					<ClayPaginationBarWithBasicItems
						activeDelta={delta}
						defaultActive={1}
						deltas={DELTAS}
						ellipsisBuffer={3}
						onActiveChange={(page) => {
							setPageIndex(page);
						}}
						onDeltaChange={(delta) => {
							setDelta(delta);
						}}
						totalItems={totalItems}
					/>
				</>
			)}

			{totalItems <= 0 && !isLoading && (
				<ClayEmptyState
					description={null}
					imgProps={{alt: 'Alternative Text', title: 'Hello World!'}}
					imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state.gif`}
					imgSrcReducedMotion={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/search_state_reduced_motion.gif`}
					title="No Templates Found"
				>
					{Liferay.ThemeDisplay.isSignedIn() && (
						<ClayButton
							aria-label="Create New"
							className="lfr-portal-tooltip"
							disabled={isDeleting || isLoading}
							displayType="primary"
							onClick={openNewItemModal}
							title="Create New"
						>
							<span className="inline-item inline-item-before my-auto">
								<ClayIcon symbol="plus" />
							</span>

							<span>Create New Template</span>
						</ClayButton>
					)}
				</ClayEmptyState>
			)}

			{open && selectedTemplate && (
				<ClayModal observer={observer} size="full-screen">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						Design Template
					</ClayModal.Header>
					<ClayModal.Body className="p-0">
						<Diagram templateId={selectedTemplate.id} />
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
};

export default TemplateList;
