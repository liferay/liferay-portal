/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {useMemo, useState} from 'react';
import {useOutletContext} from 'react-router-dom';
import useSWR from 'swr';

import {useMarketplaceContext} from '../../../../../../context/MarketplaceContext';
import {
	ProductCategories,
	ProductSpecificationKey,
} from '../../../../../../enums/Product';
import useGetProductByOrderId from '../../../../../../hooks/useGetProductByOrderId';
import HeadlessCommerceDeliveryCatalog from '../../../../../../services/rest/HeadlessCommerceDeliveryCatalog';
import {getProductCategoriesByVocabularyName} from '../../../../../../utils/productUtils';
import DownloadTable from './DownloadTable';

type OutletContext = ReturnType<typeof useGetProductByOrderId>;

const Download = () => {
	const marketplaceContext = useMarketplaceContext();
	const outletContext = useOutletContext<OutletContext['data']>();

	const [search, setSearch] = useState('');

	const channel = marketplaceContext.channel;

	const virtualProducts = useMemo(
		() => outletContext?.placedOrder.placedOrderItems || [],
		[outletContext?.placedOrder.placedOrderItems]
	);

	const latestVersionSpecification =
		outletContext?.product.productSpecifications.find(
			(specification) =>
				specification.specificationKey ===
				ProductSpecificationKey.APP_VERSION
		);

	const {data: skus = [], isLoading} = useSWR(
		latestVersionSpecification
			? null
			: `marketplace-order-${outletContext?.placedOrder.id}`,
		() =>
			Promise.all(
				virtualProducts.map((orderItem: PlacedOrderItems) =>
					HeadlessCommerceDeliveryCatalog.getSkuInfo(
						channel.id,
						orderItem.productId,
						orderItem.skuId,
						new URLSearchParams({
							accountId: '-1',
						})
					)
				)
			)
	);

	const virtualItems = useMemo(() => {
		const versionSKUCustomField = (skus as any)[0]?.customFields.find(
			(customField: CustomField) =>
				customField.name === 'Version' && customField.customValue.data
		);

		const virtualItemsWithVersion = virtualProducts[0].virtualItems?.map(
			(virtualItem: VirtualItem) => ({
				...virtualItem,
				productVersion: latestVersionSpecification
					? latestVersionSpecification.value
					: versionSKUCustomField?.customValue.data,
				version:
					virtualItem.version ||
					'Liferay Portal ' +
						getProductCategoriesByVocabularyName(
							outletContext?.product?.categories || [],
							ProductCategories.MARKETPLACE_LIFERAY_VERSION
						)
							.map((versionName) => versionName)
							.join(', '),
			})
		);

		return virtualItemsWithVersion?.filter(
			(virtualItem: VirtualItem) =>
				virtualItem.version
					?.toLowerCase()
					?.includes(search.toLowerCase()) ||
				virtualItem.productVersion
					?.toLowerCase()
					?.includes(search.toLowerCase())
		);
	}, [
		latestVersionSpecification,
		outletContext?.product?.categories,
		search,
		skus,
		virtualProducts,
	]);

	return (
		<>
			<ClayForm.Group className="align-items-center bg-light d-flex justify-content-center mb-0 my-6 p-3 rounded-lg w-100">
				<ClayInput.Group stacked>
					<ClayInput.GroupItem prepend>
						<ClayInput
							className="bg-white border-0"
							onChange={({target}) => setSearch(target.value)}
							placeholder="Search"
							type="text"
							value={search}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem prepend shrink>
						<ClayInput.GroupText className="bg-white border-0">
							<ClayButtonWithIcon
								aria-label="Search"
								className="border-0"
								displayType="unstyled"
								symbol="search"
							/>
						</ClayInput.GroupText>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			<DownloadTable loading={isLoading} virtualItems={virtualItems} />
		</>
	);
};

export default Download;
