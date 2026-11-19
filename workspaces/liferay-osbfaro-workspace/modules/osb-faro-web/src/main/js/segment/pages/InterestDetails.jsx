import BaseInterestDetails from 'contacts/pages/BaseInterestDetails';
import PropTypes from 'prop-types';
import React from 'react';
import {Routes} from 'shared/util/router';
import {Segment} from 'shared/util/records';
import {SEGMENTS} from 'shared/util/constants';

export default class InterestDetails extends React.Component {
	static propTypes = {
		segment: PropTypes.instanceOf(Segment).isRequired
	};

	render() {
		const {segment, ...otherProps} = this.props;

		return (
			<BaseInterestDetails
				{...otherProps}
				entity={segment}
				interestDetailsRoute={Routes.CONTACTS_SEGMENT_INTEREST_DETAILS}
				type={SEGMENTS}
			/>
		);
	}
}
