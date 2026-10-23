import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import autobind from 'autobind-decorator';
import BasePage from 'settings/components/base-page/BasePage';
import DataTransformation, {
	processFieldMappings
} from 'settings/components/DataTransformation';
import getCN from 'classnames';
import React from 'react';
import Sheet from 'shared/components/Sheet';
import TextTruncate from 'shared/components/TextTruncate';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {compose, withAdminPermission} from 'shared/hoc';
import {connect} from 'react-redux';
import {DataSource} from 'shared/util/records';
import {hasChanges} from 'shared/util/react';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';

class ConfigureCSV extends React.Component {
	static propTypes = {
		addAlert: PropTypes.func.isRequired,
		dataSource: PropTypes.instanceOf(DataSource),
		fileVersionId: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]),
		groupId: PropTypes.string.isRequired,
		history: PropTypes.object.isRequired,
		id: PropTypes.string,
		name: PropTypes.string
	};

	state = {
		name: '',
		submitting: false
	};

	constructor(props) {
		super(props);

		const {dataSource, name} = this.props;

		this.state = {
			...this.state,
			name: dataSource ? dataSource.name : name
		};
	}

	componentDidUpdate(prevProps) {
		if (hasChanges(prevProps, this.props, 'dataSource')) {
			const {name} = this.props.dataSource;

			this.setState({name});
		}
	}

	@autobind
	handleCreateCSVSource(fieldMappings) {
		const {
			props: {addAlert, fileVersionId, groupId, history, id},
			state: {name}
		} = this;

		this.setState({
			submitting: true
		});

		const identifier = fileVersionId ? {fileVersionId} : {id};

		const data = {
			fieldMappingMaps: processFieldMappings(fieldMappings),
			groupId,
			name,
			...identifier
		};

		const request = id
			? API.dataSource.updateCSV
			: API.dataSource.createCSV;

		request(data)
			.then(response => {
				history.push(
					toRoute(Routes.SETTINGS_DATA_SOURCE, {
						groupId,
						id: response.id
					})
				);
			})
			.catch(err => {
				addAlert({
					alertType: Alert.Types.Error,
					message:
						err.message === UNAUTHORIZED_ACCESS
							? Liferay.Language.get('unauthorized-access')
							: Liferay.Language.get('error')
				});

				this.setState({
					submitting: false
				});
			});
	}

	render() {
		const {
			props: {className, fileVersionId, groupId, id},
			state: {name, submitting}
		} = this;

		const breadcrumbItems = id
			? [
					breadcrumbs.getEntityName({
						active: false,
						href: toRoute(Routes.SETTINGS_DATA_SOURCE, {
							groupId,
							id
						}),
						label: name
					})
			  ]
			: [];

		return (
			<BasePage
				breadcrumbItems={[
					breadcrumbs.getDataSources({groupId}),
					...breadcrumbItems,
					{
						active: true,
						label: Liferay.Language.get('configure-csv')
					}
				]}
				className={getCN('csv-root', className)}
				documentTitle={name || Liferay.Language.get('csv-data-source')}
				groupId={groupId}
			>
				<Sheet className='wizard'>
					<DataTransformation
						cancelHref={
							id
								? toRoute(Routes.SETTINGS_DATA_SOURCE, {
										groupId,
										id
								  })
								: ''
						}
						fileVersionId={fileVersionId}
						groupId={groupId}
						id={id}
						key='DATA_TRANSFORMATION'
						name={name}
						navigationWarning
						onSubmit={this.handleCreateCSVSource}
						sourceFieldPlaceholder={Liferay.Language.get(
							'select-field-from-csv-file'
						)}
						sourceTitle={Liferay.Language.get('csv-data-model')}
						submitting={submitting}
						title={sub(
							Liferay.Language.get('map-x-csv-data'),
							[
								<TextTruncate
									inline
									key='NAME'
									maxCharLength={50}
									title={name}
								/>
							],
							false
						)}
					/>
				</Sheet>
			</BasePage>
		);
	}
}

export {ConfigureCSV as ConfigureCSVTesting};

const ConnectedConfigureCSV = connect(null, {addAlert})(
	ConfigureCSV
);
export {ConnectedConfigureCSV as ConfigureCSV};

export default compose(
	withAdminPermission,
	connect(null, {addAlert})
)(ConfigureCSV);
