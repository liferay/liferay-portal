/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import JSONSXPElement from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/JSONSXPElement';
import {QUERY_SXP_ELEMENTS} from '../mocks/sxpElements';

import '@testing-library/jest-dom';

jest.mock(
	'../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/CodeMirrorEditor',
	() =>
		({onChange, value}) => (
			<textarea
				aria-label="text-area"
				onChange={onChange}
				value={value}
			/>
		)
);

const onDeleteSXPElement = jest.fn();
const setFieldTouched = jest.fn();
const setFieldValue = jest.fn();

function renderJSONSXPElement(props) {
	return render(
		<JSONSXPElement
			collapseAll={false}
			id={0}
			onDeleteSXPElement={onDeleteSXPElement}
			setFieldTouched={setFieldTouched}
			setFieldValue={setFieldValue}
			sxpElement={QUERY_SXP_ELEMENTS[0]}
			{...props}
		/>
	);
}

describe('JSONSXPElement', () => {
	it('renders the element', () => {
		const {container} = renderJSONSXPElement();

		expect(container).not.toBeNull();
	});

	it('displays the title', () => {
		const {getByText} = renderJSONSXPElement();

		getByText(QUERY_SXP_ELEMENTS[0].title_i18n['en_US']);
	});

	it('displays the description', () => {
		const {getByText} = renderJSONSXPElement();

		getByText(QUERY_SXP_ELEMENTS[0].description_i18n['en_US']);
	});

	it('can collapse the query elements', () => {
		const {container, getByLabelText} = renderJSONSXPElement();

		fireEvent.click(getByLabelText('collapse'));

		expect(container.querySelector('.configuration-editor')).toBeNull();
	});

	it('calls onDeleteSXPElement when clicking on delete from dropdown', () => {
		const {getByLabelText, getByText} = renderJSONSXPElement();

		fireEvent.click(getByLabelText('dropdown'));

		fireEvent.click(getByText('remove'));

		expect(onDeleteSXPElement).toHaveBeenCalled();
	});
});
