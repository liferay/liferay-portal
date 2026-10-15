import ClayLabel from '@clayui/label';
import getCN from 'classnames';
import React from 'react';
import {AccessToken} from '../types';
import {isExpired} from '../pages/AccessTokenList';

const TokenCell: React.FC<
	{data: AccessToken} & React.HTMLAttributes<HTMLElement>
> = ({className, data: {expirationDate, token}}) => {
	const expired = isExpired(expirationDate);

	return (
		<td
			className={getCN(className)}
			data-testid={`row-token-${token.slice(-4)}`}
		>
			<span className="text-secondary mr-1">
				{Liferay.Language.get('token-ending-in')}
			</span>

			<strong className="font-weight-bold">{token.slice(-4)}</strong>

			{expired && (
				<ClayLabel
					className="ml-2"
					displayType="danger"
					inverse
					size="lg"
				>
					{Liferay.Language.get('expired')}
				</ClayLabel>
			)}
		</td>
	);
};

export default TokenCell;
