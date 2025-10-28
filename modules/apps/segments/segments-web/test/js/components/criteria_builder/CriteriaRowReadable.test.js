/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import CriteriaRowReadable from '../../../../src/main/resources/META-INF/resources/js/components/criteria_builder/CriteriaRowReadable';
import {dateToInternationalHuman} from '../../../../src/main/resources/META-INF/resources/js/utils/utils';

import '@testing-library/jest-dom';

import {
	booleanCriterion,
	booleanProperty,
	collectionCriterion,
	collectionProperty,
	dateCriterion,
	dateProperty,
	doubleCriterion,
	doubleProperty,
	entityCriterion,
	entityProperty,
	stringCriterion,
	stringProperty,
} from '../../mockData';

const equalsOperator = {label: 'Equals', name: 'eq'};

describe('CriteriaRowReadable', () => {
	it('renders string criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={stringCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={stringProperty}
			/>
		);

		expect(getByText(stringProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(getByText(stringCriterion.value)).toBeInTheDocument();
	});

	it('renders boolean criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={booleanCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={booleanProperty}
			/>
		);

		expect(getByText(booleanProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(getByText(booleanCriterion.value)).toBeInTheDocument();
	});

	it('renders date criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={dateCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={dateProperty}
			/>
		);

		expect(getByText(dateProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(
			getByText(dateToInternationalHuman(dateCriterion.value))
		).toBeInTheDocument();
	});

	it('renders entity criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={entityCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={entityProperty}
			/>
		);

		expect(getByText(entityProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(getByText(entityCriterion.displayValue)).toBeInTheDocument();
	});

	it('renders collection criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={collectionCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={collectionProperty}
			/>
		);

		expect(getByText(collectionProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(getByText(collectionCriterion.value)).toBeInTheDocument();
	});

	it('renders double criterion', () => {
		const {getByText} = render(
			<CriteriaRowReadable
				criterion={doubleCriterion}
				selectedOperator={equalsOperator}
				selectedProperty={doubleProperty}
			/>
		);

		expect(getByText(doubleProperty.label)).toBeInTheDocument();
		expect(getByText(equalsOperator.label)).toBeInTheDocument();
		expect(getByText(doubleCriterion.value)).toBeInTheDocument();
	});
});
