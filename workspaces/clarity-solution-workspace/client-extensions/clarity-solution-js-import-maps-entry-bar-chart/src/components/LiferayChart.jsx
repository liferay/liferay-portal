/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useRef, useState, useEffect} from 'react';
import BarChart from './BarChart';

const LiferayChart = ({
	title,
	datasetLabel,
	aggregationField,
	aggregationType,
	restContextPath,
	color,
}) => {
	const [labels, setLabels] = useState([]);
	const [values, setValues] = useState([]);

	const pageSize = 2;
	const runIdRef = useRef(0);

	function getValue(obj, path) {
		if (!path.includes('.')) {
			return obj?.[path];
		}

		return path.split('.').reduce((o, k) => o?.[k], obj);
	}

	function countBy(array, path) {
		return array.reduce((acc, item) => {
			const value = getValue(item, path);
			if (!value) return acc;

			acc[value] = (acc[value] || 0) + 1;
			return acc;
		}, {});
	}

	function mergeCounts(a = {}, b = {}) {
		const result = {...a};

		for (const [key, value] of Object.entries(b)) {
			result[key] = (result[key] || 0) + value;
		}

		return result;
	}

	function fetchData(page, currentCountAggregation, runId) {
		window.Liferay.Util.fetch(
			`${restContextPath}?fields=${aggregationField}&page=${page}&pageSize=${pageSize}`
		)
			.then((response) => {
				const contentType = response.headers.get('content-type');
				if (!contentType || !contentType.includes('application/json')) {
					throw new Error(
						`Expected JSON but received: ${contentType}`
					);
				}
				if (!response.ok) {
					throw new Error(`HTTP error ${response.status}`);
				}
				return response.json();
			})
			.then((json) => {
				if (runIdRef.current === runId) {
					if (aggregationType == 'count') {
						const countAggregation = mergeCounts(
							currentCountAggregation,
							countBy(json.items, aggregationField)
						);
						const keys = Object.keys(countAggregation);
						setLabels(keys);
						setValues(keys.map((k) => countAggregation[k]));

						if (page < json.lastPage && runIdRef.current == runId) {
							fetchData(page + 1, countAggregation, runId);
						}
					} // We could manage other aggregation types, like sums or averages...
				}
			})
			.catch((error) => console.error(error));
	}

	useEffect(() => {
		const runId = ++runIdRef.current;
		if (restContextPath) {
			fetchData(1, {}, runId);
		}
	}, [restContextPath, aggregationField, aggregationType]);

	return (
		<>
			<BarChart
				title={title}
				datasetLabel={datasetLabel}
				labels={labels}
				values={values}
				color={color}
			/>
		</>
	);
};

export default LiferayChart;
