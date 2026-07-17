/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {useModal} from '@clayui/modal';
import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';
import {ItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {openSelectionModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

type Scope = {
	externalReferenceCode: string;
	label: string;
};

type StyleBook = {
	externalReferenceCode: string;
	name: string;
	scope: Scope | null;
};

const DesignLibraryNameLabel = ({value}: {value: string}) => (
	<ClayLabel
		aria-label={sub(
			Liferay.Language.get('style-book-from-x-design-library'),
			value
		)}
		displayType="success"
		inverse
		withClose={false}
	>
		<ClayLabel.ItemBefore>
			<ClayIcon symbol="books-brush" />
		</ClayLabel.ItemBefore>

		<ClayLabel.ItemExpand>{value}</ClayLabel.ItemExpand>
	</ClayLabel>
);

const STYLE_BOOK_VIEWS: IFrontendDataSetProps['views'] = [
	{
		contentRenderer: 'cards',
		default: true,
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: '',
			symbol: '',
			title: 'name',
		},
		setItemComponentProps: ({
			item,
			props,
		}: {
			item: StyleBook;
			props: object;
		}) => ({
			...props,
			className: 'style-book-selector-card',
			labels: item.scope?.label
				? [
						{
							displayType: 'success',
							inverse: true,
							value: (
								<>
									<ClayLabel.ItemBefore>
										<ClayIcon symbol="books-brush" />
									</ClayLabel.ItemBefore>

									<ClayLabel.ItemExpand>
										{item.scope.label}
									</ClayLabel.ItemExpand>
								</>
							),
							withClose: false,
						},
					]
				: undefined,
			symbol: 'book',
		}),
		thumbnail: 'cards2',
	},
	{
		contentRenderer: 'table',
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					fieldName: 'name',
					label: Liferay.Language.get('name'),
					sortable: false,
				},
				{
					contentRenderer: 'designLibraryNameRenderer',
					fieldName: 'scope',
					label: Liferay.Language.get('design-library'),
					sortable: false,
				},
			],
		},
		thumbnail: 'table',
	},
];

export default function StyleBookConfiguration({
	changeStyleBookURL,
	isReadOnly,
	portletNamespace,
	styleBookEntryDesignLibraryName: initialStyleBookEntryDesignLibraryName,
	styleBookEntryERC: initialStyleBookEntryERC,
	styleBookEntryName: initialStyleBookEntryName,
	styleBookEntryScopeERC: initialStyleBookEntryScopeERC,
	styleBooksApiURL,
}: {
	changeStyleBookURL: string;
	isReadOnly: boolean;
	portletNamespace: string;
	styleBookEntryDesignLibraryName: string | null;
	styleBookEntryERC: string;
	styleBookEntryName: string;
	styleBookEntryScopeERC: string;
	styleBooksApiURL: string;
}) {
	const [styleBookEntry, setStyleBookEntry] = useState({
		designLibraryName: initialStyleBookEntryDesignLibraryName,
		name: initialStyleBookEntryName,
		styleBookEntryERC: initialStyleBookEntryERC,
		styleBookEntryScopeERC: initialStyleBookEntryScopeERC,
	});

	const [selectedItems, setSelectedItems] = useState<StyleBook[]>([]);

	const {observer, onOpenChange, open} = useModal();

	const handleChangeStyleBook = () => {
		if (isReadOnly) {
			return;
		}

		if (Liferay.FeatureFlags['LPD-57283']) {
			if (styleBookEntry.styleBookEntryERC) {
				setSelectedItems([
					{
						externalReferenceCode: styleBookEntry.styleBookEntryERC,
						name: styleBookEntry.name,
						scope: styleBookEntry.styleBookEntryScopeERC
							? {
									externalReferenceCode:
										styleBookEntry.styleBookEntryScopeERC,
									label:
										styleBookEntry.designLibraryName ?? '',
								}
							: null,
					},
				]);
			}

			onOpenChange(true);
		}
		else {
			openSelectionModal({
				iframeBodyCssClass: '',
				onSelect(selectedItem: {value: string}) {
					if (selectedItem) {
						const itemValue = JSON.parse(selectedItem.value);

						setStyleBookEntry({
							designLibraryName: null,
							name: itemValue.name,
							styleBookEntryERC: itemValue.externalReferenceCode,
							styleBookEntryScopeERC: '',
						});
					}
				},
				selectEventName: `${portletNamespace}selectStyleBook`,
				title: Liferay.Language.get('select-style-book'),
				url: changeStyleBookURL,
			});
		}
	};

	const styleBookApiURLWithNestedFields = `${styleBooksApiURL}${styleBooksApiURL.includes('?') ? '&' : '?'}nestedFields=scope.key,scope.label`;

	return (
		<>
			<input
				name={`${portletNamespace}styleBookEntryERC`}
				type="hidden"
				value={styleBookEntry.styleBookEntryERC}
			/>

			<input
				name={`${portletNamespace}styleBookEntryScopeERC`}
				type="hidden"
				value={styleBookEntry.styleBookEntryScopeERC}
			/>

			<label htmlFor={`${portletNamespace}styleBookEntry`}>
				{Liferay.Language.get('style-book')}
			</label>

			<div className="d-flex">
				<ClayForm.Group className="c-mb-0 flex-grow-1">
					<ClayInput
						id={`${portletNamespace}styleBookEntry`}
						onClick={handleChangeStyleBook}
						readOnly
						value={styleBookEntry.name}
					/>
				</ClayForm.Group>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('change-style-book')}
					className="c-ml-2"
					disabled={isReadOnly}
					displayType="secondary"
					onClick={handleChangeStyleBook}
					symbol="change"
				/>
			</div>

			{styleBookEntry.designLibraryName && (
				<div className="mt-2">
					<DesignLibraryNameLabel
						value={styleBookEntry.designLibraryName}
					/>
				</div>
			)}

			{open && Liferay.FeatureFlags['LPD-57283'] && (
				<ItemSelectorModal<StyleBook>
					apiURL={styleBookApiURLWithNestedFields}
					fdsProps={{
						customRenderers: {
							tableCell: [
								{
									component: ({
										value,
									}: {
										value: Scope | null;
									}) =>
										value?.label ? (
											<DesignLibraryNameLabel
												value={value.label}
											/>
										) : null,
									name: 'designLibraryNameRenderer',
									type: 'internal',
								},
							],
						},
						id: `${portletNamespace}styleBookSelector`,
						pagination: {
							deltas: [{label: 20}],
							initialDelta: 20,
						},
						views: STYLE_BOOK_VIEWS,
					}}
					items={selectedItems}
					locator={{
						id: 'externalReferenceCode',
						label: 'name',
						value: 'externalReferenceCode',
					}}
					observer={observer}
					onItemsChange={(items) => {
						if (items[0]) {
							setStyleBookEntry({
								designLibraryName:
									items[0].scope?.label ?? null,
								name: items[0].name,
								styleBookEntryERC:
									items[0].externalReferenceCode,
								styleBookEntryScopeERC:
									items[0].scope?.externalReferenceCode ?? '',
							});
						}

						setSelectedItems([]);
					}}
					onOpenChange={onOpenChange}
					open={open}
					size="lg"
					title={Liferay.Language.get('select-style-book')}
				/>
			)}
		</>
	);
}
