import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Constants from 'shared/util/constants';
import Loading from 'shared/components/Loading';
import React from 'react';
import RecommendationPageAssetsQuery from '../queries/RecommendationPageAssetsQuery';
import RuleItem from './RuleItem';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {EXCLUDE, Filter} from '../utils/utils';
import {get} from 'lodash';
import {Modal} from 'shared/types';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {useQuery} from '@apollo/client';

const {
	pagination: {orderDescending},
} = Constants;

interface ITrainingItemProps {
	close: Modal.close;
	name: string;
	open: Modal.open;
	value: string;
}

const TrainingItem: React.FC<ITrainingItemProps> = ({
	close,
	name,
	open,
	value,
}) => (
	<div className="training-item-root d-flex align-items-baseline">
		<ClayButton
			aria-label={Liferay.Language.get('watch')}
			borderless
			className="button-root"
			displayType="secondary"
			onClick={() => {
				open(modalTypes.MATCHING_PAGES_MODAL, {
					itemFilters: [{name, value}],
					onClose: close,
				});
			}}
		>
			<ClayIcon className="icon-root" symbol="view" />
		</ClayButton>

		<RuleItem name={name} value={value} />
	</div>
);

interface ITrainingItemsCardProps {
	close: Modal.close;
	itemFilters: Filter[];
	open: Modal.open;
}

const TrainingItemsCard: React.FC<ITrainingItemsCardProps> = ({
	close,
	itemFilters,
	open,
}) => {
	const {data, loading} = useQuery(RecommendationPageAssetsQuery, {
		variables: {
			propertyFilters: itemFilters.map(({name, value}) => ({
				filter: value,
				negate: name === EXCLUDE,
			})),
			size: 0,
			sort: {
				column: 'title',
				type: orderDescending.toUpperCase(),
			},
			start: 0,
		},
	});

	const renderTotalTrainingUrls = () => {
		if (loading) {
			return <Loading key="LOADING" />;
		}

		return toLocale(get(data, ['pageAssets', 'total'], 0));
	};

	return (
		<Card className="training-items-card-root">
			<Card.Header>
				<Card.Title>
					{Liferay.Language.get('training-items')}
				</Card.Title>
			</Card.Header>

			<Card.Body>
				{itemFilters.map(({name, value}) => (
					<TrainingItem
						close={close}
						key={`${name}-${value}`}
						name={name}
						open={open}
						value={value}
					/>
				))}

				<div className="total-training-urls d-flex align-items-center">
					<ClayButton
						aria-label={Liferay.Language.get('watch')}
						borderless
						className="button-root"
						displayType="secondary"
						onClick={() => {
							open(modalTypes.MATCHING_PAGES_MODAL, {
								itemFilters,
								onClose: close,
							});
						}}
					>
						<ClayIcon className="icon-root" symbol="view" />
					</ClayButton>

					{sub(
						Liferay.Language.get('total-training-urls-x'),
						[renderTotalTrainingUrls()],
						false
					)}
				</div>
			</Card.Body>
		</Card>
	);
};

export default connect(null, {close, open})(TrainingItemsCard);
