/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import ApiHelper from '../../../../common/services/ApiHelper';
import MoveCategoryTreeView from './MoveCategoryTreeView';

export const FETCH_URLS = {
	getCategories: (id: any) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${id}/taxonomy-categories`,
	getCategory: (id: any) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-categories/${id}/`,
	getSubCategories: (id: any) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-categories/${id}/taxonomy-categories`,
	getVocabularies: (id: any) =>
		`/o/headless-admin-taxonomy/v1.0/sites/${id}/taxonomy-vocabularies`,
	getVocabulary: (id: any) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${id}/`,
};

function MoveCategoryModalContent({
	closeModal,
	itemData,
	loadData,
	name,
	setFieldValue,
	value,
}: any) {
	const [categoryTree, setCategoryTree] = useState<any[]>([]);
	const [loading, setLoading] = useState(false);

	const _handleFieldValueChange = (newFieldValue: any) => {
		setFieldValue(name, newFieldValue);
	};

	useEffect(() => {
		const buildTree = async () => {

			// Fetches all vocabularies. This information will
			// be the start of the category tree, in which the children of the
			// vocabulary get added on as the tree gets expanded.

			const tree: any[] = [];

			const {data, error} = await ApiHelper.get<any>(
				FETCH_URLS.getVocabularies(itemData.siteId)
			);

			if (data) {
				tree[0] = {
					children: data.items.map(
						({
							assetLibraries,
							id,
							name,
							numberOfTaxonomyCategories,
						}: {
							assetLibraries: any;
							id: any;
							name: string;
							numberOfTaxonomyCategories: number;
						}) => ({
							assetLibraries,
							id: JSON.stringify(id),
							name,
							numberOfTaxonomyCategories,
						})
					),
					descriptiveName: data.descriptiveName,
					id: JSON.stringify(data.id),
					name: data.name,
				};

				setCategoryTree(tree);
			}

			if (error || !data) {
				setCategoryTree([]);
			}

			setLoading(true);
		};

		buildTree();
	}, [itemData.siteId]);

	return (
		<>
			{loading && (
				<MoveCategoryTreeView
					closeModal={closeModal}
					itemData={itemData}
					loadData={loadData}
					onChangeTree={setCategoryTree}
					onChangeValue={_handleFieldValueChange}
					tree={categoryTree}
					value={value}
				/>
			)}
		</>
	);
}

export default MoveCategoryModalContent;
