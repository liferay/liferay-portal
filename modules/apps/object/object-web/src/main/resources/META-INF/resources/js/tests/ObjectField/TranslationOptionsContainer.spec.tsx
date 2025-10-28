/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';

import {TranslationOptionsContainer} from '../../components/ObjectField/Tabs/BasicInfo/TranslationOptionsContainer';

const mockLearnResources = {
	'object-web': {
		'localizing-object-definitions-and-entries': {
			en_US: {
				message: 'Learn more.',
				url: 'https://learn.liferay.com',
			},
		},
	},
};

const defaultProps = {
	learnResources: mockLearnResources,
	published: false,
	setValues: jest.fn(),
	values: {},
};

const alertIsRendered = () => {
	expect(screen.getByText('info:')).toBeInTheDocument();

	expect(
		screen.getByText('this-field-type-does-not-support-translations')
	).toBeInTheDocument();

	expect(screen.getByText('Learn more.')).toBeInTheDocument();
};

const alertNotRendered = () => {
	expect(
		screen.queryByText('this-field-type-does-not-support-translations')
	).toBeNull();
};

const renderComponent = (props = {}) => {
	render(<TranslationOptionsContainer {...defaultProps} {...props} />);
};

describe('When the feature flag LPD-32050 is enabled', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPD-32050': true,
			},
		};
	});

	test.each`
		businessType             | system   | shouldRenderAlert
		${'Aggregation'}         | ${false} | ${'render'}
		${'Attachment'}          | ${false} | ${'not render'}
		${'AutoIncrement'}       | ${false} | ${'render'}
		${'Boolean'}             | ${false} | ${'not render'}
		${'Date'}                | ${false} | ${'not render'}
		${'DateTime'}            | ${false} | ${'not render'}
		${'Decimal'}             | ${false} | ${'not render'}
		${'Encrypted'}           | ${false} | ${'render'}
		${'Formula'}             | ${false} | ${'render'}
		${'Integer'}             | ${false} | ${'not render'}
		${'LongInteger'}         | ${false} | ${'not render'}
		${'LongText'}            | ${false} | ${'not render'}
		${'MultiselectPicklist'} | ${false} | ${'not render'}
		${'RichText'}            | ${false} | ${'not render'}
		${'Picklist'}            | ${false} | ${'not render'}
		${'PrecisionDecimal'}    | ${false} | ${'not render'}
		${'Text'}                | ${false} | ${'not render'}
		${'Aggregation'}         | ${true}  | ${'render'}
		${'Attachment'}          | ${true}  | ${'render'}
		${'AutoIncrement'}       | ${true}  | ${'render'}
		${'Boolean'}             | ${true}  | ${'render'}
		${'Date'}                | ${true}  | ${'render'}
		${'DateTime'}            | ${true}  | ${'render'}
		${'Decimal'}             | ${true}  | ${'render'}
		${'Encrypted'}           | ${true}  | ${'render'}
		${'Formula'}             | ${true}  | ${'render'}
		${'Integer'}             | ${true}  | ${'render'}
		${'LongInteger'}         | ${true}  | ${'render'}
		${'LongText'}            | ${true}  | ${'render'}
		${'MultiselectPicklist'} | ${true}  | ${'render'}
		${'RichText'}            | ${true}  | ${'render'}
		${'Picklist'}            | ${true}  | ${'render'}
		${'PrecisionDecimal'}    | ${true}  | ${'render'}
		${'Text'}                | ${true}  | ${'render'}
	`(
		'the object field $businessType system $system should $shouldRenderAlert the translation alert',
		({businessType, shouldRenderAlert, system}) => {
			renderComponent({values: {businessType, system}});

			if (shouldRenderAlert === 'render') {
				alertIsRendered();
			}
			else {
				alertNotRendered();
			}
		}
	);
});

describe('When the feature flag LPD-32050 is disabled', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPD-32050': false,
			},
		};
	});

	test.each`
		businessType             | system   | shouldRenderAlert
		${'Aggregation'}         | ${false} | ${'render'}
		${'Attachment'}          | ${false} | ${'render'}
		${'AutoIncrement'}       | ${false} | ${'render'}
		${'Boolean'}             | ${false} | ${'render'}
		${'Date'}                | ${false} | ${'render'}
		${'DateTime'}            | ${false} | ${'render'}
		${'Decimal'}             | ${false} | ${'render'}
		${'Encrypted'}           | ${false} | ${'render'}
		${'Formula'}             | ${false} | ${'render'}
		${'Integer'}             | ${false} | ${'render'}
		${'LongInteger'}         | ${false} | ${'render'}
		${'LongText'}            | ${false} | ${'not render'}
		${'MultiselectPicklist'} | ${false} | ${'render'}
		${'RichText'}            | ${false} | ${'not render'}
		${'Picklist'}            | ${false} | ${'render'}
		${'PrecisionDecimal'}    | ${false} | ${'render'}
		${'Text'}                | ${false} | ${'not render'}
		${'Aggregation'}         | ${true}  | ${'render'}
		${'Attachment'}          | ${true}  | ${'render'}
		${'AutoIncrement'}       | ${true}  | ${'render'}
		${'Boolean'}             | ${true}  | ${'render'}
		${'Date'}                | ${true}  | ${'render'}
		${'DateTime'}            | ${true}  | ${'render'}
		${'Decimal'}             | ${true}  | ${'render'}
		${'Encrypted'}           | ${true}  | ${'render'}
		${'Formula'}             | ${true}  | ${'render'}
		${'Integer'}             | ${true}  | ${'render'}
		${'LongInteger'}         | ${true}  | ${'render'}
		${'LongText'}            | ${true}  | ${'render'}
		${'MultiselectPicklist'} | ${true}  | ${'render'}
		${'RichText'}            | ${true}  | ${'render'}
		${'Picklist'}            | ${true}  | ${'render'}
		${'PrecisionDecimal'}    | ${true}  | ${'render'}
		${'Text'}                | ${true}  | ${'render'}
	`(
		'the object field $businessType system $system should $shouldRenderAlert the translation alert',
		({businessType, shouldRenderAlert, system}) => {
			renderComponent({values: {businessType, system}});

			if (shouldRenderAlert === 'render') {
				alertIsRendered();
			}
			else {
				alertNotRendered();
			}
		}
	);
});
