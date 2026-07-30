import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {debounce} from 'lodash';
import Form, {validateRequired} from 'shared/components/form';
import InfoPopover from 'shared/components/InfoPopover';
import Loading from 'shared/components/Loading';
import React from 'react';
import TitleEditor from 'shared/components/TitleEditor';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {ACCOUNT_NAME, createOrderIOMap, NAME} from 'shared/util/pagination';
import {Criteria} from './utils/types';
import {hasChanges} from 'shared/util/react';
import {ACCOUNTS, INDIVIDUALS} from 'shared/util/router';
import {
	accountsListColumns,
	individualsListColumns,
} from 'shared/util/table-columns';
import {Modal} from 'shared/types';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {validateSegmentInputs} from './utils/utils';

interface IToolbarProps {
	channelId: string;
	close: Modal.close;
	criteria: Criteria;
	criteriaString: string;
	groupId: string;
	id: string;
	includeAnonymousUsers: boolean;
	segmentCategory: SegmentCategories;
	open: Modal.open;
	valid: boolean;
	segmentType: SegmentTypes;
}

interface IToolbarState {
	countLoading: boolean;
	criteriaValid: boolean;
	membersCount: number;
}

export class Toolbar extends React.Component<IToolbarProps, IToolbarState> {
	state = {
		countLoading: true,
		criteriaValid: false,
		membersCount: 0,
	};

	constructor(props: IToolbarProps) {
		super(props);
		this.fetchMembers = this.fetchMembers.bind(this);
		this.handlePreviewClick = this.handlePreviewClick.bind(this);
	}

	componentDidMount() {
		this.setState(
			{criteriaValid: validateSegmentInputs(this.props.criteria)},
			this.getMembersCount
		);
	}

	componentDidUpdate(prevProps: IToolbarProps) {
		if (
			hasChanges(
				prevProps,
				this.props,
				'criteria',
				'includeAnonymousUsers'
			)
		) {
			this.setState(
				{criteriaValid: validateSegmentInputs(this.props.criteria)},
				this.getMembersCount
			);
		}
	}

	componentWillUnmount() {

		// @ts-ignore: Property 'cancel' does not exist on type '() => any'.
		this.getMembersCount.cancel();
	}

	getMembersCount = debounce((): void => {
		const {
			props: {criteria, includeAnonymousUsers},
			state: {criteriaValid},
		} = this;

		if (criteria) {
			this.setState({countLoading: true});
		}
		else {
			this.setState({countLoading: false, membersCount: 0});
		}

		if (criteriaValid) {
			this.fetchMembers({delta: 0, includeAnonymousUsers})
				.then(({total}: {total: number}) =>
					this.setState({countLoading: false, membersCount: total})
				)
				.catch((err: any) => {
					if (!err.IS_CANCELLATION_ERROR) {
						this.setState({countLoading: false});
					}
				});
		}
	}, 400);

	fetchMembers(params: Record<string, any>) {
		const {channelId, criteriaString, groupId, segmentCategory} =
			this.props;

		if (segmentCategory === SegmentCategories.Account) {
			const {delta, orderIOMap, page, query} = params;

			return API.accounts
				.searchByFilter({
					channelId,
					filter: criteriaString,
					groupId,
					orderIOMap,
					page,
					pageSize: delta,
					query,
				})
				.then(
					({
						items,
						totalCount,
					}: {
						items: Array<Record<string, any>>;
						totalCount: number;
					}) => ({items, total: totalCount})
				);
		}

		return API.individuals.search({
			channelId,
			filter: criteriaString,
			groupId,
			...params,
		});
	}

	handlePreviewClick() {
		const {channelId, close, groupId, open, segmentCategory} = this.props;

		const isAccountSegment = segmentCategory === SegmentCategories.Account;

		open(modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL, {
			...(isAccountSegment && {initialDelta: 20}),
			columns: [
				isAccountSegment
					? accountsListColumns.getAccountName({channelId, groupId})
					: individualsListColumns.name,
			],
			dataSourceFn: this.fetchMembers,
			entityLabel: isAccountSegment
				? Liferay.Language.get('accounts')
				: Liferay.Language.get('individuals'),
			entityType: isAccountSegment ? ACCOUNTS : INDIVIDUALS,
			initialOrderIOMap: createOrderIOMap(
				isAccountSegment ? ACCOUNT_NAME : NAME
			),
			onClose: close,
			rowIdentifier: 'id',
			size: 'lg',
			title: isAccountSegment
				? Liferay.Language.get('segment-accounts')
				: Liferay.Language.get('known-segment-members'),
		});
	}

	getPreviewCriteriaTooltipProps() {
		const {criteriaValid} = this.state;

		return criteriaValid
			? {}
			: {
					'data-tooltip': true,
					'data-tooltip-align': 'bottom',
					title: Liferay.Language.get(
						'some-of-your-criteria-are-incomplete-or-invalid'
					),
				};
	}

	render() {
		const {
			props: {
				channelId,
				groupId,
				id,
				segmentCategory,
				segmentType,
				valid,
			},
			state: {countLoading, criteriaValid, membersCount},
		} = this;

		const totalMembersCount = countLoading ? (
			<Loading key="LOADING" />
		) : (
			membersCount.toLocaleString()
		);

		const isAccountSegment = segmentCategory === SegmentCategories.Account;
		const isBatch = segmentType === SegmentTypes.Batch;

		const viewLabel = isAccountSegment
			? Liferay.Language.get('view-accounts')
			: Liferay.Language.get('view-members');

		const viewMembersButtonContent = isBatch ? (
			<span {...this.getPreviewCriteriaTooltipProps()}>
				<ClayIcon className="icon-root" symbol="view" />
			</span>
		) : (
			<div {...this.getPreviewCriteriaTooltipProps()}>
				<ClayIcon className="icon-root mr-2" symbol="view" />
				{viewLabel}
			</div>
		);

		return (
			<div className="form-header">
				<div className="page-container">
					<div className="container-fluid form-header-container">
						<div className="form-header-section-left">
							<TitleEditor
								name="name"
								placeholder={Liferay.Language.get(
									'unnamed-segment'
								)}
								validate={validateRequired}
							/>
						</div>

						<div className="form-header-section-right">
							{isBatch && (
								<div className="btn-group">
									<div className="btn-group-item">
										<Form.ToggleSwitch
											className="include-anonymous"
											label={Liferay.Language.get(
												'include-anonymous'
											)}
											name="includeAnonymousUsers"
										/>
									</div>

									<div className="btn-group-item">
										<InfoPopover
											className="include-anon-help-icon"
											content={Liferay.Language.get(
												'criteria-containing-individual-or-account-attributes-excludes-anonymous-individuals'
											)}
										/>
									</div>
								</div>
							)}

							<div className="btn-group">
								{isBatch && (
									<div className="btn-group-item">
										<div className="total-members">
											{sub(
												isAccountSegment
													? Liferay.Language.get(
															'total-accounts-x'
														)
													: Liferay.Language.get(
															'total-members-x'
														),
												[
													<div
														className="total-members-count"
														key="TOTAL_MEMBERS_COUNT"
													>
														{totalMembersCount}
													</div>,
												],
												false
											)}
										</div>
									</div>
								)}

								<div className="btn-group-item">
									<ClayButton
										aria-label={viewLabel}
										borderless
										className="button-root preview-criteria"
										data-testid="preview-criteria-button"
										data-tooltip
										disabled={
											!criteriaValid ||
											(criteriaValid && !membersCount)
										}
										displayType="secondary"
										onClick={this.handlePreviewClick}
										size="sm"
										title={viewLabel}
									>
										{viewMembersButtonContent}
									</ClayButton>
								</div>
							</div>

							<div className="btn-group">
								<div className="btn-group-item save">
									<ClayButton
										className="button-root"
										disabled={!valid}
										displayType="primary"
										size="sm"
										type="submit"
									>
										{Liferay.Language.get('save-segment')}
									</ClayButton>
								</div>

								<div className="btn-group-item cancel">
									<ClayLink
										button
										className="button-root"
										displayType="secondary"
										href={
											id
												? toRoute(
														Routes.CONTACTS_SEGMENT,
														{
															channelId,
															groupId,
															id,
														}
													)
												: toRoute(
														Routes.CONTACTS_LIST_SEGMENT,
														{
															channelId,
															groupId,
															type: SEGMENTS,
														}
													)
										}
										small
									>
										{Liferay.Language.get('cancel')}
									</ClayLink>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default connect(null, {
	close,
	open,
})(Toolbar);
