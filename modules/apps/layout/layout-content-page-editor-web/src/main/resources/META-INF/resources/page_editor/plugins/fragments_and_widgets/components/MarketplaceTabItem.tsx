/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import {
	MarketplaceView,
	Product,
	useMarketplaceContext,
} from '@liferay/marketplace-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useImperativeHandle} from 'react';

interface MarketplaceTabItemProps {
	item: Product;
	onClickRef: React.RefObject<() => void | null>;
}

export default function MarketplaceTabItem({
	item,
	onClickRef,
}: MarketplaceTabItemProps) {
	const {
		modal: {onOpenChange},
		setProduct,
		setView,
	} = useMarketplaceContext();

	const handleClick = useCallback(() => {
		setProduct(item);
		setView(MarketplaceView.STOREFRONT);
		onOpenChange(true);
	}, [item, setProduct, setView, onOpenChange]);

	useImperativeHandle(onClickRef, () => handleClick, [handleClick]);

	return (
		<ClayCard
			className="card-interactive card-interactive-primary card-type-template mb-2 template-card-horizontal"
			role="button"
			title={sub(Liferay.Language.get('x-details'), item.name)}
		>
			<ClayCard.Body className="p-2">
				<ClayCard.Row>
					<ClayLayout.ContentCol>
						<ClaySticker displayType="unstyled" size="lg">
							<img
								alt=""
								className="card-item-first"
								src={item.urlImage}
							/>
						</ClaySticker>
					</ClayLayout.ContentCol>

					<ClayLayout.ContentCol className="ml-2" expand>
						<ClayLayout.ContentSection>
							<span className="card-title text-dark text-truncate">
								{item.name}
							</span>

							<span className="card-subtitle text-truncate">
								{item.catalogName}
							</span>
						</ClayLayout.ContentSection>
					</ClayLayout.ContentCol>

					<ClayLayout.ContentCol className="btn btn-monospaced btn-unstyled">
						<ClayIcon
							className="text-secondary"
							symbol="angle-right"
						/>
					</ClayLayout.ContentCol>
				</ClayCard.Row>
			</ClayCard.Body>
		</ClayCard>
	);
}
