/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayList from '@clayui/list';
import {useModal} from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';
import {
	ItemSelector,
	ItemSelectorModal,
	openItemSelectorModal,
} from '@liferay/frontend-js-item-selector-web';
import React, {useState} from 'react';

import CMSFilesItemSelectorModal from './CMSFilesItemSelectorModal';
import {
	assetLibraryViews,
	documentViews,
	userViews,
} from './utils/defaultViews';
import {
	EItemSelectorModalViewsConfig,
	getDefaultItemSelectorModalViews,
} from './utils/getDefaultItemSelectorModalViews';

import type {DisplayType} from '@clayui/sticker';

function SampleContainer({
	children,
	label,
}: {
	children: React.ReactNode;
	label: string;
}) {
	return (
		<div className="mt-4">
			<h2>{label}</h2>

			{children}
		</div>
	);
}

type Document = {
	contentUrl: string;
	creator: {
		name: string;
	};
	encodingFormat: string;
	fileName: string;
	id: string;
	title: string;
};

type CMSFile = {
	id: number;
	title: string;
};

type Space = {
	id: number;
	name: string;
};

type User = {
	givenName: string;
	id: number;
	name: string;
	roleBriefs?: {
		name: string;
	}[];
};

const FDS_DEFAULT_PROPS: Partial<IFrontendDataSetProps> = {
	pagination: {
		deltas: [{label: 20}, {label: 40}, {label: 60}],
		initialDelta: 20,
	},
	selectionType: 'single',
};

const assetLibrariesItemSelectorConfig = {
	apiURL: `${location.origin}/o/headless-asset-library/v1.0/asset-libraries`,
	itemTypeLabel: Liferay.Language.get('space'),
	locator: {
		id: 'id',
		label: 'name',
		value: 'id',
	},
	views: assetLibraryViews,
};

const documentsItemSelectorConfig = {
	apiURL: `${location.origin}/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getSiteGroupId()}/documents`,
	itemTypeLabel: Liferay.Language.get('documents'),
	locator: {
		id: 'id',
		label: 'fileName',
		value: 'id',
	},
	views: documentViews,
};

const userAccountsItemSelectorConfig = {
	apiURL: `${location.origin}/o/headless-admin-user/v1.0/user-accounts`,
	itemTypeLabel: Liferay.Language.get('user'),
	locator: {
		id: 'id',
		label: 'givenName',
		value: 'id',
	},
	views: userViews,
};

function getRandomId(): string {
	return Math.random().toString(36).substring(2, 9);
}

export default function ItemSelectorSamples() {
	const [documents, setDocuments] = useState<Document[]>([]);
	const [space, setSpace] = useState<Space>();

	const [cmsFiles, setCMSFiles] = useState<CMSFile[]>([]);
	const [documentsItemSelectorModal, setDocumentsItemSelectorModal] =
		useState<Document[]>([]);
	const [spacesItemSelectorModal, setSpacesItemSelectorModal] = useState<
		Space[]
	>([]);
	const [usersItemSelectorModal, setUsersItemSelectorModal] = useState<
		User[]
	>([]);
	const [user2, setUser2] = useState<User | null>();
	const [space3, setSpace3] = useState<Space | null>();
	const [spacesMultiSelect, setSpacesMultiSelect] = useState<Space[]>([]);
	const [usersMultiSelect, setUsersMultiSelect] = useState<User[]>([]);

	const {
		observer: documentItemSelectorObserver,
		onOpenChange: documentItemSelectorOpenChange,
		open: documentItemSelectorOpen,
	} = useModal();
	const {
		observer: spaceItemSelectorObserver,
		onOpenChange: spaceItemSelectorOpenChange,
		open: spaceItemSelectorOpen,
	} = useModal();
	const {
		observer: userItemSelectorObserver,
		onOpenChange: userItemSelectorOpenChange,
		open: userItemSelectorOpen,
	} = useModal();
	const {
		observer: cmsFilesItemSelectorObserver,
		onOpenChange: cmsFilesItemSelectorOpenChange,
		open: cmsFilesItemSelectorOpen,
	} = useModal();

	return (
		<>
			<SampleContainer label="Single Select (Documents) - Paginated Items">
				<ItemSelector<Document>
					apiURL={`${location.origin}/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getSiteGroupId()}/documents`}
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.title}>
							{item.title}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Single Select (Spaces) - Controlled Component">
				<ClayInput.Group>
					<ClayInput.GroupItem prepend shrink>
						<ClayInput.GroupText>
							{space && (
								<ClaySticker
									displayType={
										`outline-${space.id % 10}` as DisplayType
									}
									size="sm"
								>
									{space.name.slice(0, 1)}
								</ClaySticker>
							)}
						</ClayInput.GroupText>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append>
						<ItemSelector<Space>
							apiURL={`${location.origin}/o/headless-asset-library/v1.0/asset-libraries`}
							as={ClayInput}
							items={space ? [space] : []}
							onItemsChange={(items: Space[]) => {
								if (items.length) {
									setSpace(items[0]);
								}
								else {
									setSpace(undefined);
								}
							}}
						>
							{(item: Space) => (
								<ItemSelector.Item
									key={item.id}
									textValue={item.name}
								>
									<span className="inline-item inline-item-before">
										<ClaySticker
											displayType={
												`outline-${item.id % 10}` as DisplayType
											}
											size="sm"
										>
											{item.name.slice(0, 1)}
										</ClaySticker>
									</span>

									<span className="inline-item inline-item-after">
										{item.name}
									</span>
								</ItemSelector.Item>
							)}
						</ItemSelector>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</SampleContainer>

			<SampleContainer label="Single Select (Users)">
				<ItemSelector
					apiURL={`${location.origin}/o/headless-admin-user/v1.0/user-accounts`}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.name}>
							{item.name}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Multiple Select (Documents) - Paginated Items">
				<ItemSelector<Document>
					apiURL={`${location.origin}/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getSiteGroupId()}/documents`}
					multiSelect
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.title}>
							{item.title}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Single Select (Users) - Open Modal Trigger">
				<ItemSelector
					apiURL={userAccountsItemSelectorConfig.apiURL}
					itemSelectorModalProps={{
						fdsProps: {
							...FDS_DEFAULT_PROPS,
							id: `itemSelectorModal-users-${getRandomId()}`,
							views: getDefaultItemSelectorModalViews({
								viewsConfig:
									EItemSelectorModalViewsConfig.USER_ACCOUNTS,
							}),
						},
						itemTypeLabel:
							userAccountsItemSelectorConfig.itemTypeLabel,
					}}
					items={user2 ? [user2] : []}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					onItemsChange={(items: User[]) => setUser2(items[0])}
					placeholder="Select a User"
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.name}>
							{item.name}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Multiple Select (Users) - Open Modal Trigger">
				<ItemSelector
					apiURL={userAccountsItemSelectorConfig.apiURL}
					itemSelectorModalProps={{
						fdsProps: {
							...FDS_DEFAULT_PROPS,
							id: `itemSelectorModal-documents-${getRandomId()}`,
							views: getDefaultItemSelectorModalViews({
								viewsConfig:
									EItemSelectorModalViewsConfig.USER_ACCOUNTS,
							}),
						},
						itemTypeLabel:
							userAccountsItemSelectorConfig.itemTypeLabel,
					}}
					items={usersMultiSelect}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					multiSelect
					onItemsChange={(items: User[]) => {
						setUsersMultiSelect(items);
					}}
					placeholder="Select a User"
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.name}>
							{item.name}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Single Select (Spaces) - Open Modal Trigger">
				<ItemSelector
					apiURL={`${location.origin}/o/headless-asset-library/v1.0/asset-libraries`}
					itemSelectorModalProps={{
						fdsProps: {
							...FDS_DEFAULT_PROPS,
							id: `itemSelectorModal-spaces-${getRandomId()}`,
							views: getDefaultItemSelectorModalViews({
								viewsConfig:
									EItemSelectorModalViewsConfig.ASSET_LIBRARIES,
							}),
						},
						itemTypeLabel:
							assetLibrariesItemSelectorConfig.itemTypeLabel,
					}}
					items={space3 ? [space3] : []}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					onItemsChange={(items: Space[]) => setSpace3(items[0])}
					placeholder="Select a Space"
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.name}>
							{item.name}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Multiple Select (Spaces) - Open Modal Trigger">
				<ItemSelector
					apiURL={`${location.origin}/o/headless-asset-library/v1.0/asset-libraries`}
					itemSelectorModalProps={{
						fdsProps: {
							...FDS_DEFAULT_PROPS,
							id: `itemSelectorModal-spaces-${getRandomId()}`,
							views: getDefaultItemSelectorModalViews({
								viewsConfig:
									EItemSelectorModalViewsConfig.ASSET_LIBRARIES,
							}),
						},
						itemTypeLabel:
							assetLibrariesItemSelectorConfig.itemTypeLabel,
					}}
					items={spacesMultiSelect}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					multiSelect
					onItemsChange={(items: Space[]) => {
						setSpacesMultiSelect(items);
					}}
					placeholder="Select a Space"
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.name}>
							{item.name}
						</ItemSelector.Item>
					)}
				</ItemSelector>
			</SampleContainer>

			<SampleContainer label="Multiple Select (Documents) - Custom Selected Items List">
				<ItemSelector<Document>
					apiURL={`${location.origin}/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getSiteGroupId()}/documents`}
					displaySelectedItems={false}
					items={documents}
					multiSelect
					onItemsChange={(items: Document[]) => {
						setDocuments(items);
					}}
					placeholder="Select a Document"
				>
					{(item) => (
						<ItemSelector.Item key={item.id} textValue={item.title}>
							{item.title}
						</ItemSelector.Item>
					)}
				</ItemSelector>

				{!!documents.length && (
					<ClayList className="mt-3">
						{documents.map((document) => (
							<ClayList.Item flex key={document.id}>
								{document.encodingFormat.includes('image') && (
									<ClayList.ItemField>
										<ClaySticker className="mr-1" size="xl">
											<ClaySticker.Image
												alt={document.title}
												src={document.contentUrl}
											/>
										</ClaySticker>
									</ClayList.ItemField>
								)}

								<ClayList.ItemField expand>
									<ClayList.ItemTitle>
										{document.title}
									</ClayList.ItemTitle>

									<ClayList.ItemText>
										Creator: {document.creator.name}
									</ClayList.ItemText>
								</ClayList.ItemField>

								<ClayList.ItemField>
									<ClayList.QuickActionMenu>
										<ClayList.QuickActionMenu.Item
											aria-label="Delete"
											onClick={() =>
												setDocuments((documents) =>
													documents.filter(
														(item) =>
															item.id !==
															document.id
													)
												)
											}
											symbol="trash"
											title="Delete"
										/>
									</ClayList.QuickActionMenu>
								</ClayList.ItemField>
							</ClayList.Item>
						))}
					</ClayList>
				)}
			</SampleContainer>

			<SampleContainer label="Item Selector Modal">
				<CMSFilesItemSelectorModal
					items={cmsFiles}
					multiSelect
					observer={cmsFilesItemSelectorObserver}
					onItemsChange={(items: any) => {
						setCMSFiles(items);
					}}
					onOpenChange={cmsFilesItemSelectorOpenChange}
					open={cmsFilesItemSelectorOpen}
				/>

				<ItemSelectorModal<Document>
					apiURL={documentsItemSelectorConfig.apiURL}
					createItemURL={Liferay.ThemeDisplay.getPortalURL()}
					fdsProps={{
						...FDS_DEFAULT_PROPS,
						id: `itemSelectorModal-documents-${getRandomId()}`,
						views: getDefaultItemSelectorModalViews({
							viewsConfig:
								EItemSelectorModalViewsConfig.DOCUMENTS,
						}),
					}}
					itemTypeLabel={documentsItemSelectorConfig.itemTypeLabel}
					items={documentsItemSelectorModal}
					locator={documentsItemSelectorConfig.locator}
					multiSelect
					observer={documentItemSelectorObserver}
					onItemsChange={(items: Document[]) => {
						setDocumentsItemSelectorModal(items);
					}}
					onOpenChange={documentItemSelectorOpenChange}
					open={documentItemSelectorOpen}
				/>

				<ItemSelectorModal<Space>
					apiURL={assetLibrariesItemSelectorConfig.apiURL}
					fdsProps={{
						...FDS_DEFAULT_PROPS,
						id: `itemSelectorModal-assets-${getRandomId()}`,
						views: getDefaultItemSelectorModalViews({
							viewsConfig:
								EItemSelectorModalViewsConfig.ASSET_LIBRARIES,
						}),
					}}
					itemTypeLabel={
						assetLibrariesItemSelectorConfig.itemTypeLabel
					}
					items={spacesItemSelectorModal}
					locator={assetLibrariesItemSelectorConfig.locator}
					observer={spaceItemSelectorObserver}
					onItemsChange={(items: Space[]) => {
						setSpacesItemSelectorModal(items);
					}}
					onOpenChange={spaceItemSelectorOpenChange}
					open={spaceItemSelectorOpen}
				/>

				<ItemSelectorModal<User>
					apiURL={userAccountsItemSelectorConfig.apiURL}
					createItemURL={Liferay.ThemeDisplay.getPortalURL()}
					fdsProps={{
						...FDS_DEFAULT_PROPS,
						id: `itemSelectorModal-users-${getRandomId()}`,
						views: getDefaultItemSelectorModalViews({
							viewsConfig:
								EItemSelectorModalViewsConfig.USER_ACCOUNTS,
						}),
					}}
					itemTypeLabel={userAccountsItemSelectorConfig.itemTypeLabel}
					items={usersItemSelectorModal}
					locator={userAccountsItemSelectorConfig.locator}
					observer={userItemSelectorObserver}
					onItemsChange={(items: User[]) => {
						setUsersItemSelectorModal(items);
					}}
					onOpenChange={userItemSelectorOpenChange}
					open={userItemSelectorOpen}
				/>

				<ClayButton.Group className="mb-3" spaced>
					<ClayButton
						displayType="primary"
						onClick={() => {
							documentItemSelectorOpenChange(true);
						}}
					>
						Select Documents
					</ClayButton>

					<ClayButton
						displayType="primary"
						onClick={() => {
							spaceItemSelectorOpenChange(true);
						}}
					>
						Select Space
					</ClayButton>

					<ClayButton
						displayType="primary"
						onClick={() => {
							userItemSelectorOpenChange(true);
						}}
					>
						Select User
					</ClayButton>

					<ClayButton
						displayType="primary"
						onClick={() => {
							cmsFilesItemSelectorOpenChange(true);
						}}
					>
						Select CMS Files
					</ClayButton>

					<ClayButton
						displayType="primary"
						onClick={() => {
							openItemSelectorModal({
								apiURL: userAccountsItemSelectorConfig.apiURL,
								fdsProps: {
									...FDS_DEFAULT_PROPS,
									id: `itemSelectorModal-users-${getRandomId()}`,
									views: getDefaultItemSelectorModalViews({
										viewsConfig:
											EItemSelectorModalViewsConfig.USER_ACCOUNTS,
									}),
								},
								itemTypeLabel:
									userAccountsItemSelectorConfig.itemTypeLabel,
								items: usersItemSelectorModal,
								locator: userAccountsItemSelectorConfig.locator,
								onItemsChange: (items: User[]) => {
									setUsersItemSelectorModal(items);
								},
							});
						}}
					>
						Open Modal With JS Utility
					</ClayButton>
				</ClayButton.Group>

				{!!cmsFiles.length &&
					cmsFiles.map((cmsFile: any) => (
						<ClayAlert
							displayType="info"
							key={cmsFile.title}
							symbol="document"
							title={cmsFile.title}
						/>
					))}

				{!!documentsItemSelectorModal.length &&
					documentsItemSelectorModal.map((document) => (
						<ClayAlert
							displayType="info"
							key={document.id}
							symbol="document"
							title={document.fileName}
						/>
					))}

				{!!spacesItemSelectorModal.length && (
					<ClayAlert
						displayType="info"
						symbol="nodes"
						title={spacesItemSelectorModal[0].name}
					/>
				)}

				{!!usersItemSelectorModal.length && (
					<ClayAlert
						displayType="info"
						symbol="user"
						title={usersItemSelectorModal[0].name}
					/>
				)}
			</SampleContainer>
		</>
	);
}
