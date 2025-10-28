/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import SXPElement from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/sxp_element/index';
import getUIConfigurationValues from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/sxp_element/get_ui_configuration_values';
import {INDEX_FIELDS} from '../../mocks/data';
import {QUERY_SXP_ELEMENTS} from '../../mocks/sxpElements';

import '@testing-library/jest-dom';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/CodeMirrorEditor',
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

function renderSXPElement(props) {
	return render(
		<SXPElement
			collapseAll={false}
			indexFields={INDEX_FIELDS}
			onDeleteSXPElement={onDeleteSXPElement}
			sxpElement={QUERY_SXP_ELEMENTS[0]}
			uiConfigurationValues={getUIConfigurationValues(
				QUERY_SXP_ELEMENTS[0]
			)}
			{...props}
		/>
	);
}

describe('SXPElement', () => {
	global.URL.createObjectURL = jest.fn();

	it('renders the element', () => {
		const {container} = renderSXPElement();

		expect(container).not.toBeNull();
	});

	it('displays the title', () => {
		const {getByText} = renderSXPElement();

		getByText(QUERY_SXP_ELEMENTS[0].title_i18n['en_US']);
	});

	it('displays the description', () => {
		const {getByText} = renderSXPElement();

		getByText(QUERY_SXP_ELEMENTS[0].description_i18n['en_US']);
	});

	it('can collapse the query elements', () => {
		const {container, getByLabelText} = renderSXPElement();

		fireEvent.click(getByLabelText('collapse'));

		expect(container.querySelector('.configuration-form-list')).toBeNull();
	});

	it('calls onDeleteElement when clicking on remove from dropdown', () => {
		const {getByLabelText, getByText} = renderSXPElement();

		fireEvent.click(getByLabelText('dropdown'));

		fireEvent.click(getByText('remove'));

		expect(onDeleteSXPElement).toHaveBeenCalled();
	});
});
