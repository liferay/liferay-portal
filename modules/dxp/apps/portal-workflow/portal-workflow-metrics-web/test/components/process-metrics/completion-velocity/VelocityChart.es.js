/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {AppContext} from '../../../../src/main/resources/META-INF/resources/js/components/AppContext.es';
import VelocityChart from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/completion-velocity/VelocityChart.es';

import '@testing-library/jest-dom';

describe('The velocity chart should', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {
		delete window.ResizeObserver;
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));

		jest.spyOn(
			HTMLElement.prototype,
			'clientHeight',
			'get'
		).mockReturnValue(100);
		jest.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockReturnValue(
			100
		);
	});

	afterAll(() => {
		cleanup();
		window.ResizeObserver = ResizeObserver;
		jest.restoreAllMocks();
	});

	it('Be rendered in document', () => {
		const velocityData = {
			histograms: [
				{
					key: '2019-12-03T00:00',
					value: 0.0,
				},
				{
					key: '2019-12-04T00:00',
					value: 0.0,
				},
				{
					key: '2019-12-05T00:00',
					value: 0.0,
				},
				{
					key: '2019-12-06T00:00',
					value: 0.0,
				},
				{
					key: '2019-12-07T00:00',
					value: 0.2,
				},
				{
					key: '2019-12-08T00:00',
					value: 0.8,
				},
				{
					key: '2019-12-09T00:00',
					value: 0.0,
				},
			],
			value: 0.0,
		};

		const {container} = render(
			<AppContext.Provider value={{isAmPm: true}}>
				<VelocityChart
					timeRange={{
						dateEnd: '2019-12-09T00:00:00Z',
						dateStart: '2019-12-03T00:00:00Z',
					}}
					velocityData={velocityData}
					velocityUnit={{key: 'Days', name: 'Int / Day'}}
				/>
			</AppContext.Provider>
		);

		const velocityChart = container.querySelector('.velocity-chart');

		expect(velocityChart).toBeInTheDocument();
	});

	describe('Be render tooltip', () => {
		const CLASS_CHART_CONTENT = '.workflow-tooltip-chart-content';

		it('with valid dates', async () => {
			const timeRange = {
				active: true,
				dateEnd: '2021-06-29T12:00:00.000Z',
				dateStart: '2021-03-31T12:00:00.000Z',
				defaultTimeRange: false,
				description: 'Mar 31 - Jun 29',
				id: 90,
				key: '90',
				name: 'Last 90 Days',
			};

			const getContainer = (payload, unit, unitKey) => {
				const {container} = render(
					<VelocityChart.Tooltip
						active
						isAmPm
						payload={payload}
						timeRange={timeRange}
						unit={unit}
						unitKey={unitKey}
					/>
				);

				return container;
			};

			const payload = [
				{
					payload: {
						key: '2021-06-28T16:00:00.000Z',
						value: 0,
					},
					value: 0,
				},
			];

			const text = getContainer(
				payload,
				'Inst / Month',
				'Months'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textDefault = getContainer(
				payload,
				'Inst / Month',
				'test'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textHours = getContainer(
				payload,
				'Inst / Hours',
				'Hours'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textHoursAmPm = getContainer(
				payload,
				'Inst / Hours',
				'Hours'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textMonths = getContainer(
				payload,
				'Inst / Months',
				'Months'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textWeeks = getContainer(
				payload,
				'Inst / Weeks',
				'Weeks'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			const textYears = getContainer(
				payload,
				'Inst / Years',
				'Years'
			).querySelectorAll(CLASS_CHART_CONTENT)[1].innerHTML;

			expect(text).toContain('0 Inst / Month');
			expect(textDefault).toContain('0 Inst / Month');
			expect(textHours).toContain('0 Inst / Hours');
			expect(textHoursAmPm).toContain('0 Inst / Hours');
			expect(textMonths).toContain('0 Inst / Months');
			expect(textWeeks).toContain('0 Inst / Weeks');
			expect(textYears).toContain('0 Inst / Years');
		});

		it('with invalid date', async () => {
			const timeRange = {
				active: true,
				dateEnd: '2021-06-29T12:00:00.000Z',
				dateStart: '2021-03-31T00:00:00.000Z',
				defaultTimeRange: false,
				description: 'Mar 31 - Jun 29',
				id: 90,
				key: '90',
				name: 'Last 90 Days',
			};

			const getContainer = (payload) => {
				const {container} = render(
					<VelocityChart.Tooltip
						active
						isAmPm
						payload={payload}
						timeRange={timeRange}
						unit="Inst / Hours"
						unitKey="Hours"
					/>
				);

				return container;
			};

			const payload = [
				{
					payload: {
						key: '1936-09-25T17:06:52.000Z',
						value: 0,
					},
					value: 0,
				},
			];

			const textHours =
				getContainer(payload).querySelectorAll(CLASS_CHART_CONTENT)[1]
					.innerHTML;

			payload[0].payload.key = null;

			const textDefault =
				getContainer(payload).querySelectorAll(CLASS_CHART_CONTENT)[1]
					.innerHTML;

			expect(textHours).toContain('0 Inst / Hours');
			expect(textDefault).toContain('0 Inst / Hours');
		});
	});
});
