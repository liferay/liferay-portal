import Card from 'shared/components/Card';
import ClayLayout from '@clayui/layout';
import React from 'react';

interface IGenericBarsCard {
	cardTitle: string;
}

export const GenericBarsCard: React.FC<IGenericBarsCard> = ({cardTitle}) => (
	<ClayLayout.Col key={cardTitle} xl={4}>
		<Card className='mt-4'>
			<Card.Header>
				<Card.Title>
					<h3>{cardTitle}</h3>
				</Card.Title>
			</Card.Header>
			<Card.Body>
				<div className='chart-bars'>
					<div className='small-bar'></div>
					<div className='big-bar'></div>
				</div>
			</Card.Body>
		</Card>
	</ClayLayout.Col>
);
