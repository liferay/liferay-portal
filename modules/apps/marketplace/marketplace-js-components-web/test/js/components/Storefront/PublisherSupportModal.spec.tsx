/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import PublisherSupportModal from '../../../../src/main/resources/META-INF/resources/js/components/Storefront/PublisherSupportModal';
import {MarketplaceProduct} from '../../../../src/main/resources/META-INF/resources/js/core/MarketplaceProduct';
import product from '../../__mock__/product';

const handleClick = jest.fn();

describe('PublisherSupportModal', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeEach(() => {
		jest.useFakeTimers();
	});

	it('rendering components with product specifcations', async () => {
		const {queryByAltText, queryByText} = render(
			<PublisherSupportModal
				onClose={() => handleClick()}
				product={product}
			/>
		);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(queryByAltText('App Logo')).toHaveAttribute(
			'src',
			product.urlImage
		);
		expect(queryByText('phone')).toBeInTheDocument();
		expect(queryByText('publisher-support-url')).toBeInTheDocument();
		expect(
			queryByText('publisher-support-contact-info')
		).toBeInTheDocument();
		expect(queryByText('support-email-address')).toBeInTheDocument();
		expect(queryByText(product.catalogName)).toBeInTheDocument();

		const marketplaceProduct = new MarketplaceProduct(product);

		const publisherWebsiteLink = queryByText(
			marketplaceProduct.specificationValues.PUBLISHER_WEBSITE_URL
		);

		expect(publisherWebsiteLink).toBeInTheDocument();
		expect(publisherWebsiteLink?.getAttribute('href')).toContain(
			marketplaceProduct.specificationValues.PUBLISHER_WEBSITE_URL
		);

		const supportEmailAdressLink = queryByText(
			marketplaceProduct.specificationValues.SUPPORT_EMAIL_ADDRESS
		);

		expect(supportEmailAdressLink).toBeInTheDocument();
		expect(supportEmailAdressLink?.getAttribute('href')).toContain(
			marketplaceProduct.specificationValues.SUPPORT_EMAIL_ADDRESS
		);

		const suportPhoneLink = queryByText(
			marketplaceProduct.specificationValues.SUPPORT_PHONE
		);

		expect(suportPhoneLink).toBeInTheDocument();
		expect(suportPhoneLink?.getAttribute('href')).toContain(
			marketplaceProduct.specificationValues.SUPPORT_PHONE
		);
	});

	it('rendering components without product specifications', async () => {
		product.productSpecifications = [];

		const {queryByText} = render(
			<PublisherSupportModal
				onClose={() => handleClick()}
				product={product}
			/>
		);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(queryByText('phone')).toBeFalsy();
		expect(queryByText('publisher-support-url')).toBeFalsy();
		expect(queryByText('support-email-address')).toBeFalsy();
	});
});
