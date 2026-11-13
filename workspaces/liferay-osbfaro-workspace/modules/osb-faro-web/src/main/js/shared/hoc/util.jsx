import ErrorDisplay from 'shared/components/ErrorDisplay';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {compose} from 'redux';
import {get, omit} from 'lodash';
import {sub} from 'shared/util/lang';
import {toRoute} from 'shared/util/router';
/**
 * HOC for ErrorDisplay.
 * @param {Object} - Options object to pass as props to ErrorDisplay
 * @returns {Function} Returns the ErrorDisplay or Wrapped Component.
 */
export const withError =
	(options = {}) =>
	Component =>
	({error, errorProps = {}, pageDisplay = true, refetch, ...otherProps}) => {
		const otherOptions = omit(options, 'page');

		if (error) {
			return get(options, 'page', pageDisplay) ? (
				<ErrorPage {...errorProps} {...otherOptions} />
			) : (
				<ErrorDisplay
					onReload={refetch}
					spacer
					{...errorProps}
					{...otherOptions}
				/>
			);
		}

		return <Component refetch={refetch} {...otherProps} />;
	};

/**
 * HOC for NoResultsDisplay.
 * @param {Object} - Options object to pass as props to NoResultsDisplay.
 * @returns {Function} Returns the NoResultsDisplay or WrappedComponent.
 */
export const withEmpty =
	(options = {}) =>
	Component =>
	({
		data,
		error,
		loading,
		noResultsRenderer,
		noResultsRendererProps,
		total,
		...props
	}) => {
		if (((data && data.total === 0) || total === 0) && !loading && !error) {
			if (noResultsRenderer) {
				const NoResults = noResultsRenderer;

				return <NoResults />;
			}

			return (
				<NoResultsDisplay {...options} {...noResultsRendererProps} />
			);
		}

		return (
			<Component
				data={data}
				error={error}
				loading={loading}
				total={total}
				{...props}
			/>
		);
	};

/**
 * HOC for Loading display.
 * @param {Object} - Options object to pass as props to Loading component.
 * @returns {Function} Returns the Loading or WrappedComponent.
 */
export const withLoading =
	(options = {}) =>
	Component =>
	({loading, ...otherProps}) => {
		if (loading) return <Loading {...options} />;

		return <Component {...otherProps} />;
	};

export const withNull =
	(key, errorProps = {}) =>
	Component =>
	props => {
		const {entityType = Liferay.Language.get('page'), linkRoute} =
			errorProps;

		if (key && !props[key]) {
			return (
				<ErrorPage
					{...props}
					href={toRoute(linkRoute, props.router.params)}
					linkLabel={sub(Liferay.Language.get('go-to-x'), [
						entityType
					])}
					message={sub(
						Liferay.Language.get(
							'the-x-you-are-looking-for-does-not-exist'
						),
						[entityType.toLowerCase()]
					)}
					subtitle={sub(Liferay.Language.get('x-was-not-found'), [
						entityType
					])}
					title={Liferay.Language.get('404')}
				/>
			);
		}

		return <Component {...props} />;
	};

/**
 * HOC for displaying results.
 */
export const SafeResults = compose(
	withLoading({spacer: true}),
	withError()
)(({children, data}) => children(data));

export const WrapSafeResults = compose(
	withLoading(),
	withError()
)(({children}) => children);
