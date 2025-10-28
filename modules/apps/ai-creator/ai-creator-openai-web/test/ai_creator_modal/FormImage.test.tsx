/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {FormImage} from '../../src/main/resources/META-INF/resources/ai_creator_modal/FormImage';

describe('FormImage', () => {
	it('has some form inputs to configure OpenAI', () => {
		render(<FormImage portletNamespace="namespace" />);

		expect(screen.getByLabelText('description')).toBeInTheDocument();
		expect(screen.getByLabelText('image-size')).toBeInTheDocument();
		expect(
			screen.getByLabelText('number-of-images-to-generate')
		).toBeInTheDocument();
	});
});
