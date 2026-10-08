/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable react/no-unknown-property */

import {ImageResponse} from 'next/og';

import {liferay} from '../../../liferay/index';
import {getSkuDetails} from '../../../utils/product';
import {getProductDetails} from './data';

export const contentType = 'image/png';

export const runtime = 'nodejs';

export const size = {
	height: 630,
	width: 1200,
};

export default async function OGImage({params}: {params: {id: string}}) {
	const {data: product, error} = await getProductDetails({
		friendlyUrlPath: params.id,
		liferay,
		nestedFields: 'skus',
	});

	if (error || !product) {
		console.error(error);

		return new ImageResponse(
			(
				<div tw="bg-gray-50 flex h-full items-center justify-center text-gray-900 w-full">
					Error generating image
				</div>
			),
			size
		);
	}

	const {availability, price} = getSkuDetails(product);

	return new ImageResponse(
		(
			<div tw="bg-white border-[#80ACFF] border-b-[10px] flex flex-col font-sans h-full p-12 w-full">
				<div tw="flex items-center gap-6">
					<img
						alt={product.name}
						src={product.urlImage}
						tw=" h-24 mr-4 object-cover rounded-xl w-24"
					/>

					<div tw="flex flex-col">
						<span tw="font-bold leading-tight text-[42px] text-gray-900">
							{product.name}
						</span>

						<span tw="mt-2 text-[28px] text-gray-600">
							{product.catalogName}
						</span>
					</div>
				</div>

				<div tw="max-w-[90%] mt-8 text-[28px] text-gray-700">
					{product.description}
				</div>

				<div tw="flex gap-8 mt-auto text-[24px] text-gray-600">
					<div tw="flex mr-2">
						💲 {price?.finalPrice?.replace('$', '')}
					</div>

					<div tw="flex mr-2">
						📦 {availability?.stockQuantity ?? 0} Available
					</div>

					<div tw="flex">⭐ 100 Stars</div>
				</div>
			</div>
		),
		size
	);
}
