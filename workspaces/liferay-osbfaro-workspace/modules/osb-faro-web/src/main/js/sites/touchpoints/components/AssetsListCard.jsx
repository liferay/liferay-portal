import BasePage from 'shared/components/base-page';
import React from 'react';
import Table from 'shared/components/table';
import TextTruncate from 'shared/components/TextTruncate';
import {Link} from 'react-router-dom';
import {pickBy} from 'lodash';
import {PropTypes} from 'prop-types';
import {toAssetOverviewRoute} from 'shared/util/router';

const CLASSNAME = 'analytics-assets-list';

const ITEMS_SHAPE = PropTypes.shape({
	assetId: PropTypes.string,
	assetType: PropTypes.string,
	interactions: PropTypes.number,
	title: PropTypes.string,
	type: PropTypes.string
});

/**
 * Assets List Card
 * @class
 */
class AssetsListCard extends React.Component {
	static contextType = BasePage.Context;

	static defaultProps = {
		items: []
	};

	static propTypes = {
		items: PropTypes.arrayOf(ITEMS_SHAPE),
		rangeSelectors: PropTypes.object
	};

	constructor(props) {
		super(props);

		this._elementRef = React.createRef();
	}

	/**
	 * Get Asset URL
	 * @description Get url to navigate in an overview
	 * @param {string} assetId
	 * @param {string} assetType
	 * @param {string} title
	 */
	getUrl(assetId, assetType, title) {
		const {
			context: {
				router: {params, query}
			},
			props: {rangeSelectors}
		} = this;

		return toAssetOverviewRoute(
			assetType,
			{
				...params,
				assetId,
				title
			},
			pickBy({
				...query,
				rangeEnd: rangeSelectors.rangeEnd,
				rangeKey: rangeSelectors.rangeKey,
				rangeStart: rangeSelectors.rangeStart
			})
		);
	}

	/**
	 * Render Title Column
	 * @param {object} param0
	 */
	renderTitleColumn({assetId, assetType, title}) {
		const url = this.getUrl(assetId, assetType, title);

		return (
			<td className='table-cell-expand'>
				<Link
					className='font-weight-semibold text-truncate-inline text-dark'
					to={url}
				>
					<TextTruncate title={title} />
				</Link>
			</td>
		);
	}

	/**
	 * Render Type Column
	 * @param {object} param0
	 */
	renderTypeColumn({type}) {
		return (
			<td className='table-cell-expand'>
				<div className='font-weight-semibold text-secondary text-truncate-inline'>
					<TextTruncate title={type} />
				</div>
			</td>
		);
	}

	/**
	 * Lifecycle Render - ReactJS
	 */
	render() {
		const {items} = this.props;

		const tableColumns = [
			{
				accessor: 'title',
				cellRenderer: ({data}) => this.renderTitleColumn(data),
				className: 'table-cell-expand',
				label: Liferay.Language.get('asset-name'),
				sortable: false,
				title: true
			},
			{
				accessor: 'type',
				cellRenderer: ({data}) => this.renderTypeColumn(data),
				label: Liferay.Language.get('asset-type'),
				sortable: false
			}
		];

		return (
			<div className={CLASSNAME} ref={this._elementRef}>
				<Table
					className='table-hover'
					columns={tableColumns}
					items={items}
					rowIdentifier={['assetId', 'title']}
				/>
			</div>
		);
	}
}

export default AssetsListCard;
