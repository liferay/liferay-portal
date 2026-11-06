import DisplayComponent from './display-components';
import React, {Fragment, useContext} from 'react';
import {
	ConjunctionKey,
	SegmentCategories,
	SegmentTypes,
} from 'shared/util/constants';
import {Criteria} from 'segment/segment-editor/dynamic/utils/types';
import {findPropertyByCriterion} from 'segment/segment-editor/dynamic/utils/utils';
import {ReferencedObjectsContext} from 'segment/segment-editor/dynamic/context/referencedObjects';

interface ICriteriaViewProps extends React.HTMLAttributes<HTMLDivElement> {
	criteria: Criteria;
	forwardedRef?: React.Ref<any>;
	segmentCategory?: SegmentCategories;
	segmentType: SegmentTypes;
	sequential: boolean;
	timeZoneId: string;
}

const CONJUNCTION_MAP: Record<string, string> = {
	[ConjunctionKey.And]: Liferay.Language.get('and'),
	[ConjunctionKey.Or]: Liferay.Language.get('or'),
	[ConjunctionKey.Then]: Liferay.Language.get('then'),
};

const CriteriaView: React.FC<ICriteriaViewProps> = ({
	criteria,
	forwardedRef,
	segmentCategory,
	segmentType,
	sequential,
	timeZoneId,
}) => {
	const {referencedProperties} = useContext(ReferencedObjectsContext);

	const renderCriteriaGroup = (criteria: Criteria, depth: number) => {
		const {conjunctionName, criteriaGroupId, items} = criteria as {
			conjunctionName: string;
			criteriaGroupId: string;
			items: any[];
		};

		const isSequentialTopLevel =
			segmentType === SegmentTypes.RealTime && depth === 0 && sequential;

		const conjunction = isSequentialTopLevel
			? CONJUNCTION_MAP[ConjunctionKey.Then]
			: CONJUNCTION_MAP[conjunctionName];

		return (
			<div className="criteria-group" key={criteriaGroupId}>
				{items.map((criterion: any, index: number) => {
					const content = criterion.items
						? renderCriteriaGroup(criterion, depth + 1)
						: renderCriteriaRow(criterion);

					return (
						<Fragment key={index}>
							{index !== 0 && (
								<div className="conjunction">{conjunction}</div>
							)}

							{isSequentialTopLevel ? (
								<div className="criteria-step">
									<span className="criteria-step-number mr-2">
										{index + 1}
									</span>

									{content}
								</div>
							) : (
								content
							)}
						</Fragment>
					);
				})}
			</div>
		);
	};

	const renderCriteriaRow = (criterion: any) => {
		const property = findPropertyByCriterion(
			criterion,
			referencedProperties
		);

		return (
			<div className="criteria-row">
				{property ? (
					<DisplayComponent
						criterion={criterion}
						property={property}
						segmentCategory={segmentCategory}
						segmentType={segmentType}
						timeZoneId={timeZoneId}
					/>
				) : (
					<b className="undefined-property">
						{Liferay.Language.get('attribute-no-longer-exists')}
					</b>
				)}
			</div>
		);
	};

	return (
		<div className="criteria-view-root pt-2" ref={forwardedRef}>
			{renderCriteriaGroup(criteria, 0)}
		</div>
	);
};

export default React.forwardRef<HTMLDivElement, ICriteriaViewProps>(
	(props, ref) => <CriteriaView forwardedRef={ref} {...props} />
);
