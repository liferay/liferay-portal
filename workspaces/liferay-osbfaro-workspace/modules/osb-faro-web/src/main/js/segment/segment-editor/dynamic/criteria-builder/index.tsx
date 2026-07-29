import ClearAllButton from './ClearAllButton';
import CriteriaGroup from './CriteriaGroup';
import React from 'react';
import {Criteria, Criterion, CriterionGroup} from '../utils/types';
import {insertAtIndex, removeAtIndex, replaceAtIndex} from 'shared/util/array';
import {isCriterionGroup} from '../utils/utils';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {wrapInCriteriaGroup} from '../utils/odata';

interface ICriteriaBuilderProps {
	channelId: string;
	criteria: CriterionGroup;
	groupId: string;
	id?: string;
	onChange: (items: Criteria) => void;
	segmentCategory: SegmentCategories;
	segmentType: SegmentTypes;
	sequential: boolean;
}

class CriteriaBuilder extends React.Component<ICriteriaBuilderProps> {
	constructor(props: ICriteriaBuilderProps) {
		super(props);
		this.handleCriteriaChange = this.handleCriteriaChange.bind(this);
		this.handleClearAll = this.handleClearAll.bind(this);
		this.handleCriterionMove = this.handleCriterionMove.bind(this);
	}

	/**
	 * Cleans criteria items by performing the following:
	 * 1. Remove any groups with no items.
	 * 2. Flatten groups that directly contain a single group.
	 * 3. Flatten groups that contain a single criterion.
	 * @param {Array} criteriaItems A list of criterion and criterion groups.
	 * @param {boolean} root True if the criteriaItems are from the root group to clean.
	 */
	cleanCriteriaMapItems(
		criteriaItems: Criteria[],
		root?: boolean
	): Criteria[] {
		const criteria = criteriaItems
			.filter((criteria) =>
				isCriterionGroup(criteria) ? criteria.items.length : true
			)
			.map((item) => {
				let cleanedItem: Criteria = item;

				if (isCriterionGroup(item)) {
					if (item.items.length === 1) {
						const soloItem: Criteria = item.items[0];

						if (isCriterionGroup(soloItem)) {
							cleanedItem = root
								? {
										...item,
										items: this.cleanCriteriaMapItems(
											item.items
										),
									}
								: {
										conjunctionName:
											soloItem.conjunctionName,
										criteriaGroupId:
											soloItem.criteriaGroupId,
										items: this.cleanCriteriaMapItems(
											soloItem.items
										),
									};
						}
						else {
							cleanedItem = root ? item : soloItem;
						}
					}
					else {
						cleanedItem = {
							...item,
							items: this.cleanCriteriaMapItems(item.items),
						};
					}
				}

				return cleanedItem;
			});

		return criteria;
	}

	/**
	 * Cleans and updates the criteria with the newer criteria.
	 * @param {Object} newCriteria The criteria with the most recent changes.
	 */
	handleCriteriaChange(newCriteria: Criteria): void {
		const items = this.cleanCriteriaMapItems([newCriteria], true);

		this.props.onChange(items[items.length - 1]);
	}

	handleClearAll(): void {
		this.props.onChange(wrapInCriteriaGroup([]));
	}

	/**
	 * Moves the criterion to the specified index by removing and adding, and
	 * updates the criteria.
	 */
	handleCriterionMove(
		startGroupId: string,
		startIndex: number,
		destGroupId: string,
		destIndex: number,
		criterion: Criterion | CriterionGroup,
		replace?: boolean
	): void {
		const newCriteria = this.searchAndUpdateCriteria(
			this.props.criteria,
			startGroupId,
			startIndex,
			destGroupId,
			destIndex,
			criterion,
			replace
		);

		this.handleCriteriaChange(newCriteria);
	}

	/**
	 * Searches through the criteria object and adds or replaces and removes
	 * the criterion at their respective specified index. insertAtIndex must
	 * come before removeAtIndex since the startIndex is incremented by 1
	 * when the destination index comes before the start index in the same
	 * group. The startIndex is not incremented if a replace is occurring.
	 * This is used for moving a criterion between groups.
	 */
	searchAndUpdateCriteria(
		criteria: CriterionGroup,
		startGroupId: string,
		startIndex: number,
		destGroupId: string,
		destIndex: number,
		addCriterion: Criteria,
		replace?: boolean
	): CriterionGroup {
		let updatedCriteriaItems = criteria.items;

		if (criteria.criteriaGroupId === destGroupId) {
			updatedCriteriaItems = replace
				? replaceAtIndex(updatedCriteriaItems, destIndex, addCriterion)
				: insertAtIndex(updatedCriteriaItems, destIndex, addCriterion);
		}

		if (criteria.criteriaGroupId === startGroupId) {
			updatedCriteriaItems = removeAtIndex(
				updatedCriteriaItems,
				destGroupId === startGroupId &&
					destIndex < startIndex &&
					!replace
					? startIndex + 1
					: startIndex
			);
		}

		return {
			...criteria,
			items: updatedCriteriaItems.map((item) =>
				isCriterionGroup(item) && !!item.items.length
					? this.searchAndUpdateCriteria(
							item,
							startGroupId,
							startIndex,
							destGroupId,
							destIndex,
							addCriterion,
							replace
						)
					: item
			),
		};
	}

	render() {
		const {
			channelId,
			criteria,
			groupId,
			id,
			segmentCategory,
			segmentType,
			sequential,
		} = this.props;

		const showClearAll = (criteria?.items?.length ?? 0) > 1;

		return (
			<div className="criteria-builder-root">
				<CriteriaGroup
					channelId={channelId}
					criteria={criteria}
					criteriaGroupId={criteria && criteria.criteriaGroupId}
					groupId={groupId}
					id={id}
					onChange={this.handleCriteriaChange}
					onMove={
						this
							.handleCriterionMove as import('../utils/types').OnMove
					}
					root
					segmentCategory={segmentCategory}
					segmentType={segmentType}
					sequential={sequential}
				/>

				{showClearAll && (
					<ClearAllButton onClear={this.handleClearAll} />
				)}
			</div>
		);
	}
}

export default CriteriaBuilder;
