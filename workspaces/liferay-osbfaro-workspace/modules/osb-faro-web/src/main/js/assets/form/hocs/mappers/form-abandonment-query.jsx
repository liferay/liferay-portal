import React from 'react';
import {getPercentage} from 'shared/util/util';
import {getVariables, safeResultToProps} from 'shared/util/mappers';
import {sub} from 'shared/util/lang';

/**
 * Get Label Value
 * @param {number} value
 * @returns {string} label
 */
const getLabelHeader = value =>
	sub(Liferay.Language.get('x-form-steps'), [value]);

/**
 * Get Label Fields
 * @description if value is equal 1, returns label in the singular
 * @param {number} value
 * @returns {string} label
 */
const getLabelFields = value => {
	if (value === 1) {
		return sub(Liferay.Language.get('x-field'), [value]);
	}

	return sub(Liferay.Language.get('x-fields'), [value]);
};

/**
 * Get Color
 * @description if the index is even, return a color
 * if it is odd return another color
 * @param {number} index
 * @returns {string} color
 */
const getColor = index => (index % 2 === 0 ? '#B1D4FF' : '#95C5FF');

const mapResultToProps = safeResultToProps(({form}) => {
	const {formPageMetrics} = form;

	const header = [
		{
			icon: 'document',
			label: getLabelHeader(formPageMetrics.length)
		},
		{
			icon: 'custom-field',
			label: getLabelFields(
				formPageMetrics
					.map(({formFieldMetrics}) => formFieldMetrics.length)
					.reduce((total, num) => total + num, 0)
			)
		}
	];

	const items = formPageMetrics.map(
		(
			{
				formFieldMetrics,
				pageAbandonmentsMetric,
				pageName,
				pageViewsMetric
			},
			index
		) => ({
			columns: [
				{
					icon: 'document',
					label: () => (
						<>
							{sub(Liferay.Language.get('step-x'), [index + 1])}{' '}
							{pageName && <strong>{` ${pageName}`}</strong>}
						</>
					)
				},
				{
					icon: 'custom-field',
					label: getLabelFields(formFieldMetrics.length)
				}
			],
			expanded: index === 0 ? true : false,
			items: formFieldMetrics.map(
				({fieldAbandonmentsMetric, fieldName}, index) => ({
					columns: [
						{
							icon: 'custom-field',
							label: fieldName
						}
					],
					progress: [
						{
							color: getColor(index),
							value: `${getPercentage(
								fieldAbandonmentsMetric.value,
								pageViewsMetric.value
							)}%`
						}
					]
				})
			),
			progress: [
				{
					color: '#4B9BFF',
					value: `${getPercentage(
						pageAbandonmentsMetric.value,
						pageViewsMetric.value
					)}%`
				}
			],
			showControls: formPageMetrics.length === 1 ? false : true
		})
	);

	return {
		header,
		items
	};
});

/**
 * Map Props to Options
 * @param {object} param0 props
 * @param {object} param1 context
 */
const mapPropsToOptions = ({
	accountId,
	filters,
	interval,
	rangeSelectors,
	router: {params}
}) => getVariables({accountId, filters, interval, params, rangeSelectors});

export {mapPropsToOptions, mapResultToProps};
