/* eslint-disable no-unused-vars */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ErrorAlertExtendedInfo from '../../../../src/main/resources/META-INF/resources/js/components/layout_reports/ErrorAlertExtendedInfo';

const getExtendedInfoComponent = ({error = null} = {}) => {
	return <ErrorAlertExtendedInfo error={error} />;
};

describe('Error Alert Extended Info', () => {
	afterEach(cleanup);

	it('Renders an empty div if error object is empty', () => {
		const {container} = render(getExtendedInfoComponent({error: {}}));
		expect(container).toBeEmpty();
	});
});
