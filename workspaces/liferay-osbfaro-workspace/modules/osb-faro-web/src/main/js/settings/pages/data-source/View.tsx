import ConnectorOverview from 'settings/components/3rd-party-connector/ConnectorOverview';
import CSV from './CSV';
import LiferayOverview from 'settings/components/liferay/LiferayOverview';
import MarketoCampaignOverview from 'settings/components/marketo/MarketoCampaignOverview';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import PropTypes from 'prop-types';
import React, {ComponentType} from 'react';
import SalesforceOverview from 'settings/components/salesforce/SalesforceOverview';
import {compose, withDataSource} from 'shared/hoc';
import {DataSource} from 'shared/util/records';
import {DataSourceTypes} from 'shared/util/constants';
import {getConnectorConfig} from 'settings/components/3rd-party-connector/registry';
import {withRouter} from 'react-router-dom';

const PAGE_MAP: {[type: string]: ComponentType<any>} = {
	[DataSourceTypes.Csv]: CSV as ComponentType<any>,
	[DataSourceTypes.Liferay]: LiferayOverview as ComponentType<any>,
	[DataSourceTypes.MarketoCampaign]:
		MarketoCampaignOverview as ComponentType<any>,
	[DataSourceTypes.Salesforce]: SalesforceOverview as ComponentType<any>,
};

interface IViewProps {
	dataSource: DataSource;
	[key: string]: any;
}

export class View extends React.Component<IViewProps> {
	static propTypes = {
		dataSource: PropTypes.instanceOf(DataSource).isRequired,
	};

	render() {
		const {dataSource, ...otherProps} = this.props;

		const config = getConnectorConfig(dataSource.providerType);

		const restProps = omitDefinedProps(otherProps, View.propTypes);

		if (config) {
			return (
				<ConnectorOverview
					{...restProps}
					config={config}
					dataSource={dataSource}
				/>
			);
		}

		const Page = dataSource.providerType
			? PAGE_MAP[dataSource.providerType]
			: undefined;

		if (Page) {
			return <Page {...restProps} dataSource={dataSource} />;
		}

		return null;
	}
}

export default compose(withRouter, withDataSource)(View) as ComponentType<any>;
