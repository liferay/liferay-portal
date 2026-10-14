import React from 'react';
import {IStageTrigger} from 'lifecycle/utils/stageTrigger';
import {Text} from '@clayui/core';

interface ITokenProps {
	children: React.ReactNode;
	strong?: boolean;
}

const Token: React.FC<ITokenProps> = ({children, strong = false}) => (
	<span className="mr-1">
		<Text
			color={strong ? undefined : 'secondary'}
			size={3}
			weight={strong ? 'semi-bold' : undefined}
		>
			{children}
		</Text>
	</span>
);

interface IStageTriggerProps {
	trigger: IStageTrigger;
}

const StageTrigger: React.FC<IStageTriggerProps> = ({
	trigger: {conditions, connector},
}) => (
	<ul className="list-unstyled mb-0">
		{conditions.map(({field, key, operator, value}, index) => (
			<li key={key}>
				{!!index && (
					<div className="my-2">
						<Text color="secondary" size={3} weight="semi-bold">
							{connector.toUpperCase()}
						</Text>
					</div>
				)}

				<Token>{Liferay.Language.get('account')}</Token>

				<Token strong>{field}</Token>

				<Token>{operator}</Token>

				{value != null && <Token strong>{`'${value}'`}</Token>}
			</li>
		))}
	</ul>
);

export default StageTrigger;
