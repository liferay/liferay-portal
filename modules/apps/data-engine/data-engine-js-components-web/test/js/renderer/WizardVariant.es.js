/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {ConfigProvider} from '../../../src/main/resources/META-INF/resources/js/core/hooks/useConfig.es';
import {
	FormProvider,
	useFormState,
} from '../../../src/main/resources/META-INF/resources/js/core/hooks/useForm.es';
import {PageProvider} from '../../../src/main/resources/META-INF/resources/js/core/hooks/usePage.es';
import {pageReducer} from '../../../src/main/resources/META-INF/resources/js/custom/form/reducers/index.es';
import {Column} from '../../../src/main/resources/META-INF/resources/js/custom/form/renderer/WizardVariant.es';
import mockPages from '../__mock__/mockPages.es';

const defaultProps = {
	config: {
		defaultLanguageId: 'en_US',
	},
	page: {
		pageIndex: 0,
	},
	state: {
		editingLanguageId: 'en_US',
		pages: mockPages,
	},
};

const WithProvider = ({children, config, onAction, page, state}) => (
	<ConfigProvider value={config}>
		<FormProvider
			onAction={onAction}
			reducers={[pageReducer]}
			value={state}
		>
			<PageProvider value={page}>{children}</PageProvider>
		</FormProvider>
	</ConfigProvider>
);

const ColumnWrapper = ({column}) => {
	return (
		<Column column={column}>
			{(fieldProps) => {
				return (
					<div key={fieldProps.index}>{fieldProps.field.label}</div>
				);
			}}
		</Column>
	);
};

const ColumnWithHideFieldProperty = () => {
	const {pages} = useFormState();
	const column = pages[0].rows[1].columns[0];

	return <ColumnWrapper column={column} />;
};

const ColumnWithoutHideFieldProperty = () => {
	const {pages} = useFormState();
	const column = pages[0].rows[0].columns[0];

	return <ColumnWrapper column={column} />;
};

describe('WizardVariant.PageHeader', () => {
	afterEach(cleanup);

	beforeEach(() => {
		jest.useFakeTimers();
	});

	it('renders column without hide classname when field does not have a hideField property', () => {
		const {container} = render(
			<WithProvider {...defaultProps}>
				<ColumnWithoutHideFieldProperty />
			</WithProvider>
		);

		expect(container.firstChild).toHaveAttribute(
			'class',
			'col-ddm col-md-3'
		);
	});

	it('renders column with hide classname when field has a hideField property', () => {
		const {container} = render(
			<WithProvider {...defaultProps}>
				<ColumnWithHideFieldProperty />
			</WithProvider>
		);

		expect(container.firstChild).toHaveAttribute(
			'class',
			'col-ddm hide col-md-4'
		);
	});
});
