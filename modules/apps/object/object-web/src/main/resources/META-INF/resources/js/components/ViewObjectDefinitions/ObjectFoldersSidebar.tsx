/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text, VerticalNav} from '@clayui/core';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {stringUtils} from '@liferay/object-js-components-web';
import {createResourceURL} from 'frontend-js-web';
import React, {SetStateAction} from 'react';

import {exportObjectEntity} from '../../utils/exportObjectEntity';
import {ModalImportProperties} from './ViewObjectDefinitions';

interface ObjectFoldersSidebarProps {
	baseResourceURL: string;
	importObjectFolderURL: string;
	objectDefinitionsActions: Actions;
	objectFoldersRequestInfo?: ObjectFoldersRequestInfo;
	portletNamespace: string;
	selectedObjectFolder?: Partial<ObjectFolder>;
	setModalImportProperties: (
		value: SetStateAction<ModalImportProperties>
	) => void;
	setSelectedObjectFolder: (
		value: SetStateAction<Partial<ObjectFolder>>
	) => void;
	setShowModal: (value: SetStateAction<ShowObjectDefinitionsModals>) => void;
}

export default function ObjectFoldersSideBar({
	baseResourceURL,
	importObjectFolderURL,
	objectDefinitionsActions,
	objectFoldersRequestInfo,
	selectedObjectFolder,
	setModalImportProperties,
	setSelectedObjectFolder,
	setShowModal,
}: ObjectFoldersSidebarProps) {
	const objectFoldersKebabOptions = [];

	if (selectedObjectFolder) {
		objectFoldersKebabOptions.push({
			label: Liferay.Language.get('export-object-folder'),
			onClick: () => {
				const exportObjectFolderURL = createResourceURL(
					baseResourceURL,
					{
						objectFolderId: selectedObjectFolder.id,
						p_p_resource_id:
							'/object_definitions/export_object_folder',
					}
				).href;

				exportObjectEntity({
					exportObjectEntityURL: exportObjectFolderURL,
					objectEntityId: selectedObjectFolder.id as number,
				});
			},
			symbolLeft: 'export',
			value: 'exportObjectFolder',
		});
	}

	if (
		objectDefinitionsActions?.create &&
		objectFoldersRequestInfo?.actions.create
	) {
		objectFoldersKebabOptions.push({
			label: Liferay.Language.get('import-object-folder'),
			onClick: () => {
				setModalImportProperties({
					JSONInputId: 'objectFolderJSON',
					apiURL: '/o/object-admin/v1.0/object-folders/by-external-reference-code/',
					importURL: importObjectFolderURL,
					modalImportKey: 'objectFolder',
				});

				setShowModal((previousState: ShowObjectDefinitionsModals) => ({
					...previousState,
					importModal: true,
				}));
			},
			symbolLeft: 'import',
			value: 'importObjectFolder',
		});
	}

	return (
		<div className="lfr__object-web-view-object-definitions-object-folder-list-container">
			<div className="lfr__object-web-view-object-definitions-object-folder-list-header">
				<h2 className="lfr__object-web-view-object-definitions-object-folder-list-title mb-0">
					{Liferay.Language.get('object-folders')}
				</h2>

				{selectedObjectFolder && objectFoldersRequestInfo && (
					<div className="d-flex">
						<ClayButton
							aria-label={Liferay.Language.get(
								'add-object-folder'
							)}
							className="component-action"
							displayType="unstyled"
							monospaced
							onClick={() =>
								setShowModal(
									(
										previousState: ShowObjectDefinitionsModals
									) => ({
										...previousState,
										addObjectFolder: true,
									})
								)
							}
						>
							<ClayIcon symbol="plus" />
						</ClayButton>

						<ClayDropDownWithItems
							items={objectFoldersKebabOptions}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'object-folder-actions'
									)}
									className="component-action"
									displayType="unstyled"
									monospaced
									onClick={(event) => {
										event?.stopPropagation();
									}}
									symbol="ellipsis-v"
								/>
							}
						/>
					</div>
				)}
			</div>

			{objectFoldersRequestInfo && selectedObjectFolder && (
				<VerticalNav
					active={selectedObjectFolder.externalReferenceCode}
					aria-label={Liferay.Language.get('object-folders')}
					items={objectFoldersRequestInfo.items}
				>
					{(currentObjectFolder: ObjectFolder) => {
						const label = stringUtils.getLocalizableLabel({
							fallbackLabel: currentObjectFolder.name,
							labels: currentObjectFolder.label,
						});

						return (
							<VerticalNav.Item
								key={currentObjectFolder.externalReferenceCode}
								onClick={() => {
									setSelectedObjectFolder(
										currentObjectFolder
									);

									const currentURL = new URL(
										window.location.href
									);

									currentURL.searchParams.set(
										'objectFolderName',
										currentObjectFolder.name
									);

									window.history.replaceState(
										null,
										'',
										currentURL.href
									);
								}}
								textValue={label}
							>
								<Text truncate>{label}</Text>
							</VerticalNav.Item>
						);
					}}
				</VerticalNav>
			)}
		</div>
	);
}
