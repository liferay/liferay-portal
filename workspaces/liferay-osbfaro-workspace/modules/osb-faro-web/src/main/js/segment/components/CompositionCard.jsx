import Card from 'shared/components/Card';
import CompositionChart from 'shared/components/CompositionChart';
import React from 'react';
import {PropTypes} from 'prop-types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';

export default class CompositionCard extends React.Component {
	static propTypes = {
		activeIndividualCount: PropTypes.number,
		individualCount: PropTypes.number,
		knownIndividualCount: PropTypes.number,
		membershipProcessing: PropTypes.bool
	};

	render() {
		const {
			activeIndividualCount,
			individualCount,
			knownIndividualCount,
			membershipProcessing
		} = this.props;

		return (
			<Card
				className='composition-card-root'
				reportContainer={ReportContainer.SegmentCompositionCard}
			>
				<Card.Header>
					<Card.Title>
						{Liferay.Language.get('segment-composition')}
					</Card.Title>
				</Card.Header>

				<Card.Body>
					<CompositionChart
						innerData={{
							label: Liferay.Language.get('known-members'),
							value: knownIndividualCount
						}}
						outerData={{
							label: Liferay.Language.get('active-last-30-days'),
							value: activeIndividualCount
						}}
						processing={membershipProcessing}
						total={individualCount}
					/>
				</Card.Body>
			</Card>
		);
	}
}
