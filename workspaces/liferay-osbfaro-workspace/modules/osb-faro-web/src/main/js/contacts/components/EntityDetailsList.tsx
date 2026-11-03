import Card from 'shared/components/Card';
import Checkbox from 'shared/components/Checkbox';
import getCN from 'classnames';
import Nav from 'shared/components/Nav';
import React from 'react';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import {detailsListColumns} from 'shared/util/table-columns';
import {isBlank} from 'shared/util/util';
import {pick, some} from 'lodash';
import {sub} from 'shared/util/lang';
import {withStatefulPagination} from 'shared/hoc';

const DETAIL_QUERY_OPTIONS = ['dataSourceName', 'name', 'sourceName', 'value'];

const SearchableEntityTableStateful = withStatefulPagination(
	SearchableEntityTable
);

interface IEntityDetailsListProps extends React.HTMLAttributes<HTMLElement> {
	demographicsIMap: any;
	groupId: string;
	timeZoneId?: string;
	title?: string;
}

export default class EntityDetailsList extends React.Component<IEntityDetailsListProps> {
	static defaultProps = {
		title: Liferay.Language.get('properties'),
	};

	state = {
		hideBlanks: false,
	};

	_detailsData: any;
	_knownCount: any;

	constructor(props: IEntityDetailsListProps) {
		super(props);

		this.getDetailsData();
		this.getKnownCount();

		this.filterDetails = this.filterDetails.bind(this);
		this.handleToggleBlankRows = this.handleToggleBlankRows.bind(this);
		this.renderNav = this.renderNav.bind(this);
	}

	filterDetails({
		hideBlanks,
		query = '',
	}: {
		hideBlanks: boolean;
		query?: string;
	}) {
		const items = this._detailsData.filter((rowData: {value: string}) => {
			if (hideBlanks && isBlank(rowData.value)) {
				return false;
			}

			return some(pick(rowData, DETAIL_QUERY_OPTIONS), (item) =>
				item
					? item
							.toString()
							.toLowerCase()
							.includes(query.toLowerCase())
					: false
			);
		});

		return Promise.resolve({
			items,
			total: items.length,
		});
	}

	getColumns() {
		const {groupId, timeZoneId} = this.props;

		return [
			detailsListColumns.name,
			detailsListColumns.sourceName,
			detailsListColumns.getDataSourceName(groupId),
			detailsListColumns.getDateModified(timeZoneId),
		];
	}

	getDetailsData() {
		const {demographicsIMap} = this.props;

		this._detailsData = demographicsIMap
			.map((values: any) => {
				const fieldValue = values.get(0);

				return {
					dataSourceId: fieldValue.get('dataSourceId'),
					dataSourceName: fieldValue.get('dataSourceName'),
					dateModified: fieldValue.get('dateModified'),
					name: fieldValue.get('name'),
					sourceName: fieldValue.get('sourceName'),
					value: values
						.map((fieldMapping: any) => fieldMapping.get('value'))
						.join(', '),
				};
			})
			.valueSeq()
			.toArray();
	}

	getKnownCount() {
		const {demographicsIMap} = this.props;

		this._knownCount = demographicsIMap.filter(
			(values: any) => !isBlank(values.getIn([0, 'value']))
		).size;
	}

	handleToggleBlankRows(event: React.ChangeEvent<HTMLInputElement>) {
		this.setState({
			hideBlanks: event.target.checked,
		});
	}

	renderNav() {
		const {hideBlanks} = this.state;

		return (
			<Nav>
				<Nav.Item>
					<Checkbox
						checked={hideBlanks}
						label={Liferay.Language.get('hide-blank-attributes')}
						onChange={this.handleToggleBlankRows}
					/>
				</Nav.Item>
			</Nav>
		);
	}

	render() {
		const {
			props: {className, title},
			state: {hideBlanks},
		} = this;

		return (
			<Card
				className={getCN('entity-details-list-root', className)}
				pageDisplay
			>
				<Card.Header>
					<Card.Title>{title}</Card.Title>

					<div className="secondary-info">
						{this._knownCount === 1
							? sub(
									Liferay.Language.get(
										'1-known-individual-is-available-of-x-total'
									),
									[
										<b key="TOTAL">
											{this._detailsData.length}
										</b>,
									],
									false
								)
							: sub(
									Liferay.Language.get(
										'x-known-individuals-are-available-of-x-total'
									),
									[
										<b key="KNOWN">{this._knownCount}</b>,
										<b key="TOTAL">
											{this._detailsData.length}
										</b>,
									],
									false
								)}
					</div>
				</Card.Header>

				<Card.Body noPadding>
					<SearchableEntityTableStateful
						columns={this.getColumns()}
						dataSourceFn={this.filterDetails}
						dataSourceParams={{hideBlanks}}
						internalSort
						navRenderer={this.renderNav}
						nowrap={false}
						rowIdentifier="name"
						showPagination={false}
					/>
				</Card.Body>
			</Card>
		);
	}
}
