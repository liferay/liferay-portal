/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MiniCartUtils, ProductOptionsDataRenderer} from 'commerce-frontend-js';
import {openModal} from 'frontend-js-components-web';
import {createPortletURL} from 'frontend-js-web';

import ProductURLDataRenderer from '../data_renderers/ProductURLDataRenderer';

const PlacedOrdersFDSPropsTransformer = (props) => ({
	...props,
	customDataRenderers: {
		productOptionsDataRenderer: (componentProps) =>
			ProductOptionsDataRenderer({
				...componentProps,
				additionalProps: props.additionalProps,
			}),
		productURLDataRenderer: (componentProps) =>
			ProductURLDataRenderer({
				...componentProps,
				additionalProps: props.additionalProps,
			}),
	},
	onActionDropdownItemClick: ({
		action: {
			data: {id: actionId},
		},
		itemData: {id: orderItemId, productURLs},
	}) => {
		if (actionId === 'view') {
			window.location.href = MiniCartUtils.generateProductPageURL(
				props.additionalProps.siteDefaultURL,
				productURLs,
				props.additionalProps.productURLSeparator
			);
		}

		if (actionId === 'shipments') {
			openModal({
				height: '32rem',
				iframeBodyCssClass: '',
				size: 'lg',
				title: Liferay.Language.get('shipments'),
				url: createPortletURL(props.additionalProps.viewShipmentsURL, {
					commerceOrderItemId: orderItemId,
				}),
			});
		}
	},
});

export default PlacedOrdersFDSPropsTransformer;
