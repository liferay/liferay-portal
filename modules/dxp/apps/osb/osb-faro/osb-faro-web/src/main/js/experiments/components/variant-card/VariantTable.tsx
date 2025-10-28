import columns from './variant-columns';
import React from 'react';
import Table from 'shared/components/table';
import {createOrderIOMap} from 'shared/util/pagination';
import {
	getBestVariant,
	getMetricUnit,
	mergedVariants
} from 'experiments/util/experiments';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';

export const VariantTable = ({experiment}) => {
	const {onOrderIOMapChange, orderIOMap} = useStatefulPagination(null, {
		initialOrderIOMap: createOrderIOMap('dxpVariantName')
	});

	const {
		dxpVariants,
		goal,
		metrics: {variantMetrics},
		publishedDXPVariantId,
		status,
		type,
		winnerDXPVariantId
	} = experiment;

	const variants = mergedVariants(dxpVariants, variantMetrics);
	const metric = goal?.metric;
	const metricUnit = getMetricUnit(metric);
	const bestVariant = getBestVariant(experiment);

	return (
		<div className='analytics-variant-card-table'>
			<Table
				columns={columns({
					bestVariant,
					metric,
					metricUnit,
					publishedDXPVariantId,
					status,
					type,
					winnerDXPVariantId
				})}
				headingNowrap={false}
				internalSort
				items={variants}
				onOrderIOMapChange={onOrderIOMapChange}
				orderIOMap={orderIOMap}
				rowIdentifier='dxpVariantId'
			/>
		</div>
	);
};
