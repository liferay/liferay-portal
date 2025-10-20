import autobind from 'autobind-decorator';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import CriteriaView from './CriteriaView';
import getCN from 'classnames';
import Label from 'shared/components/Label';
import React from 'react';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {translateQueryToCriteria} from 'segment/segment-editor/dynamic/utils/odata';
import {withReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';

const HEADER_MARGIN = 16;

interface ICriteriaCardProps {
	criteriaString: string;
	includeAnonymousUsers: boolean;
	segment: Segment;
	timeZoneId: string;
}

interface ICriteriaCardState {
	expand: boolean;
	truncate: boolean;
}

class CriteriaCard extends React.Component<
	ICriteriaCardProps,
	ICriteriaCardState
> {
	state = {
		expand: false,
		truncate: true
	};

	private _criteriaViewRef = React.createRef<HTMLDivElement>();

	componentDidMount() {
		this.updateCriteriaTruncation();

		window.addEventListener('resize', this.updateCriteriaTruncation);
	}

	componentWillUnmount() {
		window.removeEventListener('resize', this.updateCriteriaTruncation);
	}

	@autobind
	handleClick() {
		this.setState({expand: true});
	}

	@autobind
	updateCriteriaTruncation() {
		const node = this._criteriaViewRef.current;

		if (node) {
			const {bottom} = node.getBoundingClientRect();

			this.setState({
				truncate: bottom > window.innerHeight - HEADER_MARGIN
			});
		}
	}

	render() {
		const {
			props: {criteriaString, includeAnonymousUsers, segment, timeZoneId},
			state: {expand, truncate}
		} = this;

		const hideOverflow = !expand && truncate;

		return (
			<Card
				className='criteria-card-root'
				reportContainer={
					segment.segmentType === SegmentTypes.Batch &&
					ReportContainer.SegmentCriteriaCard
				}
			>
				<Card.Header>
					<Card.Title>
						{Liferay.Language.get('segment-criteria')}
					</Card.Title>

					{includeAnonymousUsers && (
						<Label display='secondary' size='lg' uppercase>
							{Liferay.Language.get('include-anonymous')}
						</Label>
					)}
				</Card.Header>

				<Card.Body className={getCN({truncate: hideOverflow})}>
					<CriteriaView
						criteria={translateQueryToCriteria(criteriaString)}
						ref={this._criteriaViewRef}
						timeZoneId={timeZoneId}
					/>
				</Card.Body>

				{hideOverflow && (
					<div className='fade-out-cover'>
						<div className='view-all-button-container'>
							<ClayButton
								className='button-root'
								displayType='unstyled'
								onClick={this.handleClick}
								size='sm'
							>
								{Liferay.Language.get('view-all-criteria')}
							</ClayButton>
						</div>
					</div>
				)}
			</Card>
		);
	}
}

export default withReferencedObjectsProvider(CriteriaCard);
