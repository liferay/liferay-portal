import Card from 'shared/components/Card';
import CriteriaView from './CriteriaView';
import Label from 'shared/components/Label';
import Panel from '@clayui/panel';
import React, {useContext, useEffect, useMemo} from 'react';
import {extractRemoteCriterionEntries} from 'segment/segment-editor/dynamic/criterion-types/extract';
import {ReferencedObjectsContext} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {SegmentTypes} from 'shared/util/constants';
import {translateQueryToCriteria} from 'segment/segment-editor/dynamic/utils/odata';

interface ICriteriaCardProps {
	channelId?: string;
	criteriaString: string;
	groupId?: string;
	includeAnonymousUsers: boolean;
	segmentType: SegmentTypes;
	sequential: boolean;
	timeZoneId: string;
}

const CriteriaCard: React.FC<ICriteriaCardProps> = ({
	channelId,
	criteriaString,
	groupId,
	includeAnonymousUsers,
	segmentType,
	sequential,
	timeZoneId,
}) => {
	const _criteriaViewRef = React.createRef<HTMLDivElement>();

	const {addProperty} = useContext(ReferencedObjectsContext);

	const criteria = useMemo(
		() => translateQueryToCriteria(criteriaString),
		[criteriaString]
	);

	useEffect(() => {
		if (!channelId || !groupId || !addProperty) {
			return;
		}

		extractRemoteCriterionEntries(criteria).forEach(
			({criterionType, id, name}) => {
				addProperty(criterionType.createProperty({id, name}));
			}
		);
	}, [channelId, groupId, criteria]);

	return (
		<Card reportContainer={ReportContainer.SegmentCriteriaCard}>
			<Panel
				className="card-root"
				collapsable
				defaultExpanded
				displayTitle={
					<Panel.Title className="card-title">
						{Liferay.Language.get('segment-criteria')}
					</Panel.Title>
				}
			>
				<Panel.Body className="criteria-card-root">
					{includeAnonymousUsers && (
						<Label display="info" size="lg" uppercase>
							{Liferay.Language.get(
								'includes-anonymous-individuals'
							)}
						</Label>
					)}

					{segmentType === SegmentTypes.RealTime && sequential && (
						<Label display="info" size="lg" uppercase>
							{Liferay.Language.get('sequential-events')}
						</Label>
					)}

					{criteria && (
						<CriteriaView
							criteria={criteria}
							ref={_criteriaViewRef}
							segmentType={segmentType}
							sequential={sequential}
							timeZoneId={timeZoneId}
						/>
					)}
				</Panel.Body>
			</Panel>
		</Card>
	);
};

export default CriteriaCard;
