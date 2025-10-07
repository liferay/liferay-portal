/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {formatData} from '../../../../js/components/technology/utils';
import {MetricName, MetricType} from '../../../../js/types/global';

describe('Format Technology Data', () => {
	it('format data for scenario 1', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [{value: 0, valueKey: 'Desktop'}],
				},
			],
		};

		expect(formatData(data, MetricType.Views)).toEqual({
			data: [
				{
					label: 'Desktop',
					percentage: 0,
					value: 0,
				},
			],
			total: 0,
		});
	});

	it('format data for scenario 2', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [{value: 10, valueKey: 'Desktop'}],
				},
			],
		};

		expect(formatData(data, MetricType.Views)).toEqual({
			data: [
				{
					label: 'Desktop',
					percentage: 100,
					value: 10,
				},
			],
			total: 10,
		});
	});

	it('format data for scenario 3', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [
						{value: 50, valueKey: 'Desktop'},
						{value: 40, valueKey: 'Smartphone'},
						{value: 10, valueKey: 'TV'},
					],
				},
			],
		};

		expect(formatData(data, MetricType.Views)).toEqual({
			data: [
				{
					label: 'Desktop',
					percentage: 50,
					value: 50,
				},
				{
					label: 'Smartphone',
					percentage: 40,
					value: 40,
				},
				{
					label: 'TV',
					percentage: 10,
					value: 10,
				},
			],
			total: 100,
		});
	});

	it('format data for scenario 4', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [
						{value: 50, valueKey: 'Desktop'},
						{value: 40, valueKey: 'Smartphone'},
						{value: 5, valueKey: 'TV'},
						{value: 5, valueKey: 'Amazon Kindle'},
					],
				},
			],
		};

		expect(formatData(data, MetricType.Views)).toEqual({
			data: [
				{
					label: 'Desktop',
					percentage: 50,
					value: 50,
				},
				{
					label: 'Smartphone',
					percentage: 40,
					value: 40,
				},
				{
					label: 'TV',
					percentage: 5,
					value: 5,
				},
				{
					label: 'Amazon Kindle',
					percentage: 5,
					value: 5,
				},
			],
			total: 100,
		});
	});

	it('format data for scenario 5', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [
						{value: 50, valueKey: 'Desktop'},
						{value: 40, valueKey: 'Smartphone'},
						{value: 5, valueKey: 'TV'},
						{value: 4, valueKey: 'Amazon Kindle'},
						{value: 1, valueKey: 'Toten'},
					],
				},
			],
		};

		expect(formatData(data, MetricType.Views)).toEqual({
			data: [
				{
					label: 'Desktop',
					percentage: 50,
					value: 50,
				},
				{
					label: 'Smartphone',
					percentage: 40,
					value: 40,
				},
				{
					label: 'TV',
					percentage: 5,
					value: 5,
				},
				{
					label: 'others',
					percentage: 5,
					value: 5,
				},
			],
			total: 100,
		});
	});

	it('format data for scenario 6', () => {
		const data = {
			deviceMetrics: [
				{
					metricName: MetricName.Views,
					metrics: [
						{value: 50, valueKey: 'Desktop'},
						{value: 40, valueKey: 'Smartphone'},
					],
				},
				{
					metricName: MetricName.Downloads,
					metrics: [
						{value: 50, valueKey: 'TV'},
						{value: 50, valueKey: 'Smartphone'},
					],
				},
			],
		};

		expect(formatData(data, MetricType.Downloads)).toEqual({
			data: [
				{
					label: 'TV',
					percentage: 50,
					value: 50,
				},
				{
					label: 'Smartphone',
					percentage: 50,
					value: 50,
				},
			],
			total: 100,
		});
	});
});
