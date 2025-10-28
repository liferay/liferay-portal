/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {FormContent} from '../../src/main/resources/META-INF/resources/ai_creator_modal/FormContent';

describe('FormContent', () => {
	it('has some form inputs to configure OpenAI', () => {
		render(<FormContent portletNamespace="namespace" />);

		expect(screen.getByLabelText('description')).toBeInTheDocument();
		expect(screen.getByLabelText('tone')).toBeInTheDocument();
		expect(screen.getByLabelText('word-count')).toBeInTheDocument();
	});
});
