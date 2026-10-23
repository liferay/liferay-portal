import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import Constants from 'shared/util/constants';
import Loading from 'shared/components/Loading';
import React from 'react';
import MatchingPagesModal from 'shared/components/modals/MatchingPagesModal';
import RecommendationPageAssetsQuery from '../queries/RecommendationPageAssetsQuery';
import RuleItem from './RuleItem';
import {EXCLUDE, Filter} from '../utils/utils';
import {get} from 'lodash';
import {sub} from 'shared/util/lang';
import {toThousands} from 'shared/util/numbers';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

const {
	pagination: {orderDescending},
} = Constants;

interface ITrainingItemProps {
	name: string;
	value: string;
}

const TrainingItem: React.FC<ITrainingItemProps> = ({name, value}) => {
	const matchingPagesModal = useModal();

	return (
		<div className="training-item-root d-flex align-items-baseline">
			<ClayButton
				aria-label={Liferay.Language.get('watch')}
				borderless
				className="button-root"
				displayType="secondary"
				onClick={() => {
					matchingPagesModal.onOpenChange(true);
				}}
			>
				<ClayIcon className="icon-root" symbol="view" />
			</ClayButton>

			<RuleItem name={name} value={value} />

			{matchingPagesModal.open && (
				<MatchingPagesModal
					itemFilters={[{name, value}]}
					observer={matchingPagesModal.observer}
					onClose={() => matchingPagesModal.onOpenChange(false)}
				/>
			)}
		</div>
	);
};

interface ITrainingItemsCardProps {
	itemFilters: Filter[];
}

const TrainingItemsCard: React.FC<ITrainingItemsCardProps> = ({
	itemFilters,
}) => {
	const totalModal = useModal();

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

		return toThousands(get(data, ['pageAssets', 'total'], 0));
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
						key={`${name}-${value}`}
						name={name}
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
							totalModal.onOpenChange(true);
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

				{totalModal.open && (
					<MatchingPagesModal
						itemFilters={itemFilters}
						observer={totalModal.observer}
						onClose={() => totalModal.onOpenChange(false)}
					/>
				)}
			</Card.Body>
		</Card>
	);
};

export default TrainingItemsCard;
