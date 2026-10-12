/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {FrontendDataSet, IView} from '@liferay/frontend-data-set-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ProductRelationshipSelectorTitleRenderer from './cell_renderers/ProductRelationshipSelectorTitleRenderer';
import addProductRelationships, {
	getRelatedObjectEntryIds,
} from './services/ProductRelationshipService';

import '../css/ProductRelationshipSelector.scss';

const TITLE_TABLE_CELL_RENDERER = 'titleTableCellRenderer';

function getViews(): IView[] {
	return [
		{
			contentRenderer: 'table',
			label: Liferay.Language.get('table'),
			name: 'table',
			schema: {
				fields: [
					{
						contentRenderer: TITLE_TABLE_CELL_RENDERER,
						fieldName: 'embedded.title',
						label: Liferay.Language.get('title'),
					},
					{
						fieldName: 'embedded.sku',
						label: Liferay.Language.get('sku'),
					},
					{
						fieldName:
							'embedded.systemProperties.objectDefinitionBrief.label',
						label: Liferay.Language.get('type'),
					},
					{
						contentRenderer: 'status',
						fieldName: 'embedded.status',
						label: Liferay.Language.get('status'),
					},
				],
			},
			thumbnail: 'table',
		},
	];
}

type Product = {
	embedded: {externalReferenceCode: string; id: number};
	entryClassName: string;
};

export default function ProductRelationshipSelectorModal({
	className,
	externalReferenceCode,
	filters,
	loadData,
	scopeKey,
	searchAPIURL,
	title,
}: {
	className: string;
	externalReferenceCode: string;
	filters: string;
	loadData?: () => void;
	scopeKey: string;
	searchAPIURL: string;
	title: string;
}) {
	const [apiURL, setAPIURL] = useState('');
	const {observer, onOpenChange, open} = useModal();
	const [saving, setSaving] = useState(false);
	const [selectedItems, setSelectedItems] = useState<Product[]>([]);

	useEffect(() => {
		getRelatedObjectEntryIds({className, externalReferenceCode, scopeKey})
			.catch(() => [])
			.then((relatedObjectEntryIds) => {
				setAPIURL(
					`${window.location.origin}${Liferay.ThemeDisplay.getPathContext()}${searchAPIURL}`.replace(
						'{relatedObjectEntryIds}',
						relatedObjectEntryIds
							.map(
								(relatedObjectEntryId) =>
									`,${relatedObjectEntryId}`
							)
							.join('')
					)
				);

				onOpenChange(true);
			});
	}, [
		className,
		externalReferenceCode,
		onOpenChange,
		scopeKey,
		searchAPIURL,
	]);

	if (!open) {
		return null;
	}

	const save = async () => {
		setSaving(true);

		const response = await addProductRelationships({
			scopeKey,
			sourceLinkReference: {className, externalReferenceCode},
			targetLinkReferences: selectedItems.map((product) => ({
				className: product.entryClassName,
				externalReferenceCode: product.embedded.externalReferenceCode,
			})),
			type: 'variant',
		}).catch(() => null);

		setSaving(false);

		if (!response?.ok) {
			openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});

			return;
		}

		openToast({
			message: Liferay.Language.get(
				'your-request-completed-successfully'
			),
			type: 'success',
		});

		onOpenChange(false);

		loadData?.();
	};

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('create-relationship-with-x'), title)}
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<FrontendDataSet
					apiURL={apiURL}
					customRenderers={{
						tableCell: [
							{
								component:
									ProductRelationshipSelectorTitleRenderer,
								name: TITLE_TABLE_CELL_RENDERER,
								type: 'internal',
							},
						],
					}}
					filters={JSON.parse(filters || '[]')}
					id="productRelationshipSelector"
					onSelectedItemsChange={setSelectedItems}
					selectedItems={selectedItems}
					selectedItemsKey="embedded.id"
					selectionType="multiple"
					style="fluid"
					views={getViews()}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={saving}
							displayType="secondary"
							onClick={() => onOpenChange(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!selectedItems.length || saving}
							onClick={save}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
