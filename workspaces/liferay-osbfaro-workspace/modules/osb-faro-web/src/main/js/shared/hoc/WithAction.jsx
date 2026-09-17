import ErrorPage from '../pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React from 'react';
import {connect} from 'react-redux';
import {isFunction, noop} from 'lodash';
import {PropTypes} from 'prop-types';
import {RemoteData} from '../util/records';

/**
 * Maps an HTTP status to the ErrorPage props that describe it. Without this
 * every failure renders the 404 page, so a user who lacks permission is told
 * the page does not exist.
 * @param {number} errorStatus - The status the request failed with.
 * @returns {object} - Props for ErrorPage, empty when the status is unknown.
 */
export function getStatusErrorPageProps(errorStatus) {
	if (errorStatus === 403) {
		return {
			message: Liferay.Language.get(
				'you-do-not-have-permission-to-view-this-resource'
			),
			subtitle: Liferay.Language.get('unauthorized-access'),
			title: '403'
		};
	}

	return {};
}

const defaultOptions = {
	errorPageProps: {},
	propName: 'data',
	renderErrorPage: props => <ErrorPage {...props} />
};

/**
 * HOC for handling loading, success, and error states.
 * @param {function} action - The action to call. Should return a Promise.
 * @param {function} mapStateToRemoteData - Should return an instance of RemoteData
 * or null.
 * @param {object} [options] - Optional configuration
 * @param {boolean} options.bypassErrorPage - Determine whether to bypass the error page if an error occurs but data still exists.
 * @param {string} options.propName - The name of the data prop that will be passed to
 * WrappedComponent
 * @param {object|function} options.errorPageProps - The props that will be
 * passed to ErrorPage. If this is a function, then it will be passed an
 * object and is expected to return a props object for ErrorPage.
 * @returns {Function} - The new component
 */
export default (action, mapStateToRemoteData, options = {}) =>
	WrappedComponent => {
		const {bypassErrorPage, errorPageProps, propName, renderErrorPage} = {
			...defaultOptions,
			...options
		};

		return connect(
			(state, ownProps) => {
				const remoteData =
					mapStateToRemoteData(state, ownProps) || new RemoteData();

				return remoteData.toObject();
			},
			{
				action
			}
		)(
			class extends React.Component {
				static propTypes = {
					data: PropTypes.any,
					error: PropTypes.bool.isRequired,
					loading: PropTypes.bool.isRequired,
					router: PropTypes.object
				};

				componentDidMount() {
					const {action, groupId, ...otherProps} = this.props;

					const result = action({groupId, ...otherProps});

					if (result instanceof Promise) {
						result.catch(noop);
					}
				}

				render() {
					const {
						channelId,
						className,
						data,
						error,
						errorStatus,
						groupId,
						loading,
						...otherProps
					} = this.props;

					if (
						(error && !bypassErrorPage) ||
						(error && bypassErrorPage && !data)
					) {
						const props = isFunction(errorPageProps)
							? errorPageProps({channelId, groupId})
							: errorPageProps;

						return renderErrorPage({
							className,
							...getStatusErrorPageProps(errorStatus),
							...props
						});
					} else if (!data && loading) {
						return <Loading className={className} />;
					} else {
						const componentData = {[propName]: data};

						return (
							<WrappedComponent
								{...otherProps}
								{...componentData}
								channelId={channelId}
								className={className}
								groupId={groupId}
							/>
						);
					}
				}
			}
		);
	};
