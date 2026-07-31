import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import FormMetricsQuery from 'shared/queries/FormMetricsQuery';
import HTMLBarChart from 'shared/components/HTMLBarChart';
import React from 'react';
import {compose} from 'redux';
import {graphql} from '@apollo/client/react/hoc';
import {HOC_CARD_PROPTYPES} from 'shared/util/proptypes';
import {
	mapPropsToOptions,
	mapResultToProps
} from './mappers/form-abandonment-query';
import {withEmpty, withError} from 'cerebro-shared/hocs/utils';
import {withLoading} from 'shared/hoc';

const FormAbandonmentWithData = compose(
	graphql(FormMetricsQuery, {
		options: mapPropsToOptions,
		props: mapResultToProps
	}),
	withLoading(),
	withError(),
	withEmpty({
		emptyTitle: Liferay.Language.get('empty-message-form-abandoment')
	})
)(HTMLBarChart);

FormAbandonmentWithData.propTypes = HOC_CARD_PROPTYPES;

const defaultProps = {
	className: 'analytics-form-abandonment-card'
};

const FormAbandonmentCard = ({className, label, legacyDropdownRangeKey}) => (
	<BaseCard
		className={className}
		label={label}
		legacyDropdownRangeKey={legacyDropdownRangeKey}
		minHeight={536}
	>
		{({
			accountId,
			filters,
			interval,
			rangeSelectors,
			router,
			segmentId
		}) => (
			<Card.Body>
				<FormAbandonmentWithData
					accountId={accountId}
					filters={filters}
					interval={interval}
					rangeSelectors={rangeSelectors}
					router={router}
					segmentId={segmentId}
				/>
			</Card.Body>
		)}
	</BaseCard>
);

FormAbandonmentCard.defaultProps = defaultProps;

export {FormAbandonmentCard};
export default FormAbandonmentCard;
