import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import Label from '@clayui/label';
import React from 'react';
import {ConnectorEntityDescriptor, ConnectorStatus} from './types';
import {getEntityDisplay} from './getEntityDisplay';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';

interface IConnectorEntitiesProps {
	connectorStatus?: ConnectorStatus;
	entities: ConnectorEntityDescriptor[];
	syncedCounts: {[entity: string]: number | undefined};
}

const ConnectorEntities: React.FC<IConnectorEntitiesProps> = ({
	connectorStatus,
	entities,
	syncedCounts,
}) => (
	<ClayList className="mb-0">
		{entities.map(({entity}) => {
			const {icon, label} = getEntityDisplay(entity);
			const count = syncedCounts[entity];
			const configured =
				connectorStatus !== ConnectorStatus.Disconnected &&
				typeof count === 'number' &&
				count > 0;

			return (
				<ClayList.Item flex key={entity}>
					<ClayList.ItemField>
						<ClaySticker displayType="unstyled">
							<ClayIcon
								className="text-secondary"
								symbol={icon}
							/>
						</ClaySticker>
					</ClayList.ItemField>

					<ClayList.ItemField expand>
						<ClayList.ItemTitle>{label}</ClayList.ItemTitle>

						{typeof count === 'number' && count >= 0 && (
							<ClayList.ItemText>
								{sub(Liferay.Language.get('x-items-synced'), [
									toLocale(count),
								])}
							</ClayList.ItemText>
						)}
					</ClayList.ItemField>

					<ClayList.ItemField className="justify-content-center">
						<Label
							displayType={configured ? 'success' : 'secondary'}
						>
							{configured
								? Liferay.Language.get('configured')
								: Liferay.Language.get('unconfigured')}
						</Label>
					</ClayList.ItemField>
				</ClayList.Item>
			);
		})}
	</ClayList>
);

export default ConnectorEntities;
